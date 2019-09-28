/**
 * 
 */
package org.kgrid.adapter.mlflow;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.kgrid.adapter.api.Executor;
import org.kgrid.adapter.mlflow.MLFlowDockerRunner.RunningMLFlowContainer;
import org.kgrid.adapter.mlflow.util.DockerUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is executor that proxies to the running docker container. It creates the container if not already running.
 * 
 * @see MLFlowDockerRunner
 * 
 * @author taylorde
 *
 */
public class MLFlowExecutor implements Executor {

	private static final Log log = LogFactory.getLog(MLFlowExecutor.class);

	MLFlowDockerRunner dockerRunner = new MLFlowDockerRunner();
	
	String dockerImageName;
	int port;
	
	String containerId;
	
	public MLFlowExecutor(String dockerImageName, int port) {
		this.dockerImageName = dockerImageName.trim();
		this.port = port;
		try {
			Optional<RunningMLFlowContainer> runningContainer = dockerRunner.startMLFlowDockerContainer(this.dockerImageName, port);
			if ( runningContainer.isPresent()) {
				this.containerId = runningContainer.get().getContainerId();
				if ( runningContainer.get().getRunningPort() != this.port ) {
					log.debug(String.format("Running port is different the allocated - clear allocated port and set port to running port", runningContainer.get().getRunningPort(), this.port));
					
					DockerUtil.clearPort(this.port);
					this.port = runningContainer.get().getRunningPort();
				}
			} else {
				log.error(String.format("\n\nERROR - Failed to start docker service", dockerImageName));
			}
		} catch (Exception e) {
			log.error(
					String.format("Error starting docker image:%s on port %d",
							dockerImageName, 
							port),
					e
					);
		}
	}
	
	
	@Override
	public Object execute(Object input) {
		
		// Take input and send to docker container at port

		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
			HttpPost postReq = new HttpPost(String.format("http://localhost:%d/invocations", this.port));
			
			postReq.addHeader("Content-Type", "application/json");
			
			StringEntity stringEntity = new StringEntity(
					new ObjectMapper().writeValueAsString(input), 
					ContentType.create("application/json")
					);
			
			postReq.setEntity(stringEntity);
			
			try (CloseableHttpResponse response = httpclient.execute(postReq)) {
				return IOUtils.toString(response.getEntity().getContent(), "UTF-8");
			} catch (ClientProtocolException e) {
				log.error(e);
			} catch (IOException e) {
				log.error(e);
			}
			
			String result = "";
			return result;
		} catch (IOException e1) {
			log.error(e1);
		}
		return "";
	}
}
