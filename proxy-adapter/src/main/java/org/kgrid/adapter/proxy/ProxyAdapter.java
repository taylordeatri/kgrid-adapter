package org.kgrid.adapter.proxy;

import java.net.URI;
import java.nio.file.Path;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.kgrid.adapter.api.Adapter;
import org.kgrid.adapter.api.AdapterSupport;
import org.kgrid.adapter.api.Executor;
import org.kgrid.shelf.repository.CompoundDigitalObjectStore;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class ProxyAdapter implements Adapter, AdapterSupport {

  // Todo: have this pass through adapter types supported by the proxy?
  @Override
  public String getType() {
    return "PROXY";
  }

  CompoundDigitalObjectStore cdoStore;

  @Override
  public void initialize() {

  }

  /*
   * Right now just sends a post request to the url specified in the resource metadata + the endpoint
   * passes through the body of the request and returns the body of the response.
   * Let the RestTemplate handle http errors.
   */
  @Override
  public Executor activate(Path resource, String endpoint) {
    try {
      HttpClient instance = HttpClientBuilder.create().build();
      RestTemplate restTemplate = new RestTemplate(
          new HttpComponentsClientHttpRequestFactory(instance));
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      return new Executor() {
        @Override
        public Object execute(Object input) {
          HttpEntity<Object> entity = new HttpEntity<>(input, headers);
          String serverURL = cdoStore.getMetadata(resource.getParent().getParent().toString()).get("resource").asText();
          URI proxyEndpoint = URI.create(serverURL + "/" + endpoint);
          ResponseEntity<String> response =
              restTemplate.exchange(proxyEndpoint, HttpMethod.POST, entity, String.class);
          return response.getBody();
        }
      };

    } catch (HttpClientErrorException hcex) {
      throw new IllegalArgumentException("Cannot run proxy ko " + hcex, hcex);
    }
  }

  /*
   * Would like this to check the remote server but the way it works now is that each object can point
   * to a different server
   */
  @Override
  public String status() {
    if(cdoStore != null) {
      return "UP" ;
    } else {
      return "DOWN";
    }
  }

  public void setCdoStore(CompoundDigitalObjectStore cdoStore) {
    this.cdoStore = cdoStore;
  }
}
