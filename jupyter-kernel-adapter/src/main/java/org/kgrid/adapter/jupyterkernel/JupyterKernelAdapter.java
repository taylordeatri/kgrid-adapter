package org.kgrid.adapter.jupyterkernel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.kgrid.adapter.api.ActivationContext;
import org.kgrid.adapter.api.Adapter;
import org.kgrid.adapter.api.AdapterException;
import org.kgrid.adapter.api.AdapterSupport;
import org.kgrid.adapter.api.Executor;
import org.kgrid.shelf.repository.CompoundDigitalObjectStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.thavam.util.concurrent.blockingMap.BlockingHashMap;
import org.thavam.util.concurrent.blockingMap.BlockingMap;

public class JupyterKernelAdapter implements Adapter, AdapterSupport {

  private final Logger log = LoggerFactory.getLogger(this.getClass());
  private CompoundDigitalObjectStore cdoStore;
  private String serverURL;
  private String kernelType = "";
  private Map<String, WebSocketClient> clientEndPoints;
  private BlockingMap<String, String> kernelResults = new BlockingHashMap<>();
  private BlockingMap<String, String> kernelErrors = new BlockingHashMap<>();

  @Override
  public String getType() {
    return kernelType;
  }

  @Override
  public void initialize(ActivationContext context) {

  }

  public void initialize(Properties properties) {
    serverURL = properties.getProperty("kgrid.adapter.kernel.url");
    if (serverURL == null) {
      serverURL = "localhost:8888/api/kernels";
    } else if (serverURL.startsWith("http://")) {
      serverURL = serverURL.substring(7);
    }
    kernelType = properties.getProperty("kgrid.adapter.kernel.type");
    if (kernelType == null) {
      kernelType = "python2";
    }
    clientEndPoints = Collections.synchronizedMap(new HashMap<>());
  }

  @Override
  public Executor activate(Path resourcePath, String functionName) {

    // Get a kernel from jupyter
    HttpClient instance = HttpClientBuilder.create().build();
    RestTemplate restTemplate = new RestTemplate(
        new HttpComponentsClientHttpRequestFactory(instance));
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Object> entity = new HttpEntity<>("{\"name\":\"" + kernelType + "\"}", headers);
    URI proxyEndpoint = URI.create("http://" + serverURL);
    try {
      ResponseEntity<Map> response = restTemplate
          .exchange(proxyEndpoint, HttpMethod.POST, entity, Map.class);
      String kernelID = response.getBody().get("id").toString();
      kernelType = response.getBody().get("name").toString();

      URI executionEndpoint = URI.create("ws://" + serverURL + "/" + kernelID + "/channels");
      log.info("Connecting to websocket URI " + executionEndpoint);
      clientEndPoints.put(resourcePath + functionName, new WebSocketClient(executionEndpoint));

      /*
       * Should receive a big string of json from the jupyter kernel that we need to parse for the result
       * Jupyter message response format:
       * parent_header: {username:"", version: "", msg_type: "", msg_id: *the UUID we sent to the kernel*, session: "", date: ""}
       * msg_type: status or execute_input or stream or execute_reply or execute_result or error
       * msg_id: internal ID - do not use
       * content: (depends on the msg_type)
       *     for status {"execution_state": ""}
       *     for execute_input {"execution_count": , "code": ""}
       *     for stream {text: *string printed from the code*, name: *channel text is in*}
       *     for execute_result {"execution_count": , "data": {"mimetype": *result returned from function*}, "metadata": {}}
       *     for execute_reply {"status": "ok", "execution_count": 0, "user_expressions": {}, "payload": []}
       *       or {"status": "error", "ename": "", "evalue": "" "traceback": "" "execution_count": #, "user_expressions": {},
       *           "engine_info": {"engine_id": , "method": "execute", "engine_uuid": ""}, "payload": []}
       *     for error {"ename": "", "evalue": "", "traceback": []}
       * header: {"username": "", "version": "", "msg_type": "", "msg_id": "", "session": "", "date": ""}
       * channel: ""
       * buffers: []
       * metadata: {}
       */
      clientEndPoints.get(resourcePath + functionName)
          .addMessageHandler(new WebSocketClient.MessageHandler() {
            @Override
            public void handleMessage(String message) throws IOException, InterruptedException {
              JsonNode jsonData = new ObjectMapper().readTree(message);
              String msgType = jsonData.get("msg_type").asText();
              if ("execute_result".equals(msgType)) {
                String id = jsonData.get("parent_header").get("msg_id")
                    .asText(); // Leaving this in case we need to match the returned UUID we sent in
                String result = jsonData.get("content").get("data").get("text/plain")
                    .asText(); // Right now handles plaintext results, in the future can handle other mimetypes?
                kernelResults.put(id, result);
                kernelErrors.put(id, "");
              } else if ("error".equals(msgType)) {
                String id = jsonData.get("parent_header").get("msg_id").asText();
                String errorMessage = jsonData.get("content").get("ename").asText() + ": " +
                    jsonData.get("content").get("evalue").asText();
                kernelResults.put(id, null);
                kernelErrors.put(id, errorMessage);
                log.error("Failed to execute message with ID " + id + " " + jsonData);
                throw new AdapterException(errorMessage);
              }
              // If want to analyze other status messages from the kernel:
  //            else {
  //              log.info("Got response from websocket:\n" + message);
  //            }
            }
          });

      String scriptPath = resourcePath.toString();
      byte[] binary = cdoStore.getBinary(scriptPath);

      if (binary == null) {
        throw new AdapterException("Can't find endpoint " + functionName + " in path " + scriptPath);
      }

      // Send the function stored in the KO payload to the kernel
      String code = new String(binary, Charset.defaultCharset());
      String activateSessionID = UUID.randomUUID().toString();
      String codeID = UUID.randomUUID().toString();
      JsonNode functionCode = createKernelMessage(codeID, activateSessionID, code);
      clientEndPoints.get(resourcePath + functionName).sendMessage(functionCode.toString());

      return new Executor() {

        @Override
        public synchronized Object execute(Object input) {
          // Need to convert the LinkedHashMap we get from the rest client input to a python dict,
          // may need to make other converters for other kernel types and split this out
          StringBuilder functCallBuilder = new StringBuilder().append(functionName).append("(");
          try {
            Map inputMap = (Map) input;
            if (inputMap.size() > 0) {
              functCallBuilder.append("{");
              (inputMap).forEach((Object key, Object value) -> {
                functCallBuilder.append("\"").append(key).append("\": \"").append(value).append("\",");
              });
              functCallBuilder.deleteCharAt(functCallBuilder.length() - 1).append("}");
            }
            functCallBuilder.append(")");
          } catch (ClassCastException e) {
            functCallBuilder.append(input).append(")");
          }

          // Send the function call to the kernel to be executed
          String executeSessionID = UUID.randomUUID().toString();
          String functionID = UUID.randomUUID().toString();
          JsonNode functionRun = createKernelMessage(functionID, executeSessionID,
              functCallBuilder.toString());
          clientEndPoints.get(resourcePath + functionName).sendMessage(functionRun.toString());
          try {
            // Can change this to make it timeout faster or allow more complex programs
            String result = kernelResults.take(functionID, 10, TimeUnit.SECONDS);
            if (result == null) {
              throw new AdapterException(
                  "Error in jupyter kernel execution: " +
                  kernelErrors.take(functionID, 10, TimeUnit.SECONDS));
            }
            return result;
          } catch (InterruptedException e) {
            throw new AdapterException(e.getMessage(), e);
          }
        }
      };
    } catch (ResourceAccessException e) {
      throw new AdapterException(e.getMessage());
    }
  }

  @Override
  public String status() {
    if (clientEndPoints == null || clientEndPoints.size() < 1 || cdoStore == null) {
      return "DOWN";
    }
    return "UP";
  }

  @Override
  public void setContext(ActivationContext context) {

  }

  @Override
  public ActivationContext getContext() {
    return null;
  }

  /*
   * Fields for kernel websocket input message defined by jupyter kernel spec
   *  https://jupyter-client.readthedocs.io/en/latest/messaging.html
   */
  private JsonNode createKernelMessage(String msgID, String sessionID, String code) {
    ObjectNode header = new ObjectMapper().createObjectNode();
    header.put("username", "");
    header.put("version", "5.1");
    header.put("session", sessionID);
    header.put("msg_id", msgID);
    header.put("msg_type", "execute_request");

    ObjectNode content = new ObjectMapper().createObjectNode();
    content.put("code", code);
    content.put("silent", false);
    content.put("store_history", false);
    content.putObject("user_expressions");
    content.put("allow_stdin", false);

    ObjectNode json = new ObjectMapper().createObjectNode();
    json.set("header", header);
    json.set("content", content);
    json.putObject("parent_header");
    json.put("channel", "shell");
    json.putObject("metadata");
    json.putObject("buffers");
    return json;
  }
}
