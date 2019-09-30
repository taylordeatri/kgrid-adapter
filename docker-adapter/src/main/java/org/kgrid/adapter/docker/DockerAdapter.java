/**
 * 
 */
package org.kgrid.adapter.docker;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.OptionalInt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kgrid.adapter.api.ActivationContext;
import org.kgrid.adapter.api.Adapter;
import org.kgrid.adapter.api.Executor;
import org.kgrid.adapter.docker.util.DockerUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

/**
 * This is the main Docker adapter. It creates an DockerExecutor that acts as the proxy to the docker container.
 * 
 * @see DockerExecutor
 * 
 * @author taylorde
 *
 */
public class DockerAdapter implements Adapter {
	
	private static final Log log = LogFactory.getLog(DockerAdapter.class);

	private ActivationContext context;
	
	int port = 0;

	@Override
	public String getType() {
		return "DOCKER";
	}

	@Override
	public void initialize(ActivationContext context) {
		this.context = context;
	}
	
	/** 
	 * Activate this KO by creating an DockerExecutor.
	 * @see DockerExecutor
	 * 
	 * (non-Javadoc)
	 * @see org.kgrid.adapter.api.Adapter#activate(java.nio.file.Path, java.lang.String)
	 **/
	@Override
	public Executor activate(Path resource, String endpoint) {
		byte[] binary;
		try {
//			binary = context.getBinary(resource.toString());
//			String config = new String(binary, Charset.defaultCharset());
//			String[] lines = config.split("\n");
//			
//			String imageName = "";
//			String imageArchivePath = "";
//			String url = "http://localhost:{port}/";
//			
//			for ( String line : Arrays.asList(lines)) {
//				String[] fields = line.split(":");
//				if ("docker-image".equalsIgnoreCase(fields[0]) ) {
//					imageName = fields[1];
//				} else
//				if ("docker-image-archive".equalsIgnoreCase(fields[0]) ) {
//					imageArchivePath = fields[1];
//				} else
//				if ("url".equalsIgnoreCase(fields[0]) ) {
//					url = fields[1];
//				} else {
//					log.error(String.format("ERROR - Unrecognized field name %s", fields[0]));
//				}
//			}

			YAMLMapper yamlMapper = new YAMLMapper();
			JsonNode dockerConfigNode = yamlMapper.readTree(context.getBinary(resource.toString()));
			
			String imageName = dockerConfigNode.get("docker-image").asText("");
			String imageArchivePath = dockerConfigNode.get("docker-image-archive").asText("");
			String url = dockerConfigNode.get("url").asText("http://localhost:_PORT_/");
			
			OptionalInt optPort = DockerUtil.getPort();	// Get port for executor to proxy to.
			if ( optPort.isPresent() ) {
				this.port = optPort.getAsInt();
				// Start up docker image in a container with the configured port and configured volume
				DockerExecutor dockerExecutor = new DockerExecutor(context, imageName, port, imageArchivePath, url);
				return dockerExecutor;
			} else {
				log.error(String.format("Failed to get a port allocated for %s",  imageName));
				DockerUtil.clearPort(port);
			}
			
		} catch (Exception e) {
			log.error("Error activating DockerAdapter", e);
		}
		return null;
	}	

	@Override
	public String status() {
		// verify container is started and port is accessible.
		return port > 0 ? "UP" : "DOWN";
	}
}
