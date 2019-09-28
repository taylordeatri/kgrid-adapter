/**
 * 
 */
package org.kgrid.adapter.mlflow;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.OptionalInt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kgrid.adapter.api.ActivationContext;
import org.kgrid.adapter.api.Adapter;
import org.kgrid.adapter.api.Executor;
import org.kgrid.adapter.mlflow.util.DockerUtil;

/**
 * This is the main MLFlow adapter. It creates an MLFlowExecutor that acts as the proxy to the docker container.
 * 
 * @see MLFlowExecutor
 * 
 * @author taylorde
 *
 */
public class MLFlowAdapter implements Adapter {
	
	private static final Log log = LogFactory.getLog(MLFlowAdapter.class);

	private ActivationContext context;
	
	int port = 0;

	@Override
	public String getType() {
		return "MLFLOW";
	}

	@Override
	public void initialize(ActivationContext context) {
		this.context = context;
	}
	
	/** 
	 * Activate this KO by creating an MLFlowExecutor.
	 * @see MLFlowExecutor
	 * 
	 * (non-Javadoc)
	 * @see org.kgrid.adapter.api.Adapter#activate(java.nio.file.Path, java.lang.String)
	 **/
	@Override
	public Executor activate(Path resource, String endpoint) {
		byte[] binary;
		try {
			binary = context.getBinary(resource.toString());
			String config = new String(binary, Charset.defaultCharset());
			String[] lines = config.split("\n");
			for ( String line : Arrays.asList(lines)) {
				String[] fields = line.split(":");
				if ("mlflow-docker-image".equalsIgnoreCase(fields[0]) ) {
					String imageName = fields[1];
					OptionalInt optPort = DockerUtil.getPort();	// Get port for executor to proxy to.
					if ( optPort.isPresent() ) {
						this.port = optPort.getAsInt();
						// Start up docker image in a container with the configured port and configured volume
						MLFlowExecutor mlFlowExecutor = new MLFlowExecutor(imageName, port);
					
						return mlFlowExecutor;
					} else {
						log.error(String.format("Failed to get a port allocated for %s",  imageName));
						DockerUtil.clearPort(port);
					}
					break;
				}
			}
		} catch (Exception e) {
			log.error("Error activating MLFlowAdapter", e);
		}
		return null;
	}	

	@Override
	public String status() {
		// verify container is started and port is accessible.
		return port > 0 ? "UP" : "DOWN";
	}
}
