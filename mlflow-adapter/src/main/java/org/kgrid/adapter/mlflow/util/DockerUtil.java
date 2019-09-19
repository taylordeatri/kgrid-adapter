/**
 * 
 */
package org.kgrid.adapter.mlflow.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;

/**
 * This is a utility class that manages ports allocated to docker containers and provides the method to get a docker client connection.
 * 
 * To override the PORT_BASE set the KGRID_MLFLOW_PORT_BASE property to override default of 5100 base port number.
 * Total available ports are currently only PORT_BASE + 5;
 * 
 * @see DefaujltDockerClientConfig
 * @see DockerClient
 * 
 * @author taylorde
 *
 */


public class DockerUtil {
	
	static DockerClientConfig localConfig;
	
	static Map<Integer, Boolean> allocatedPorts = new HashMap<>();
	
	static int PORT_BASE = 5100;
	
	static int PORT_COUNT = 5;

	/**
	 * Gets unallocated port to map to containers port.
	 * 
	 * @return OptionalInt - allocated port
	 */
	public static OptionalInt getPort() {
		overridePortBase();
		
		overridePortCount();
		
		if ( allocatedPorts.size() <= 0 ) {
			// Support PORT_COUNT ports.
			IntStream.range(PORT_BASE, PORT_BASE + PORT_COUNT).forEach(port -> allocatedPorts.put(port,  Boolean.TRUE));
		}
		OptionalInt freePort = IntStream
			.range(PORT_BASE, PORT_BASE+PORT_COUNT)
				.filter(port -> allocatedPorts.get(port))
				.findFirst();
		if ( freePort.isPresent() ) {
			allocatedPorts.put(freePort.getAsInt(), Boolean.FALSE);
			return OptionalInt.of(freePort.getAsInt());
		}
		return freePort;
	}

	static void overridePortCount() {
		String newPortCountStr = System.getProperty("KGRID_MLFLOW_PORT_COUNT");
		if (StringUtils.isNotBlank(newPortCountStr) && StringUtils.isNumeric(newPortCountStr)) {
			PORT_COUNT = Integer.parseInt(newPortCountStr);
		}
	}

	static void overridePortBase() {
		String newPortBaseStr = System.getProperty("KGRID_MLFLOW_PORT_BASE");
		if (StringUtils.isNotBlank(newPortBaseStr) && StringUtils.isNumeric(newPortBaseStr)) {
			PORT_BASE = Integer.parseInt(newPortBaseStr);
		}
	}
	
	/**
	 * Get a client connection configuration to local Docker server.
	 * 
	 * @return DockerClientConfig
	 */
	public static DockerClientConfig getLocalDockerConfig() {
		if ( localConfig == null ) {
			String userHome = System.getProperty("user.home");
			String dockerHost = System.getProperty("DOCKER_HOST");

			localConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
				    .withDockerHost("tcp://" + dockerHost + ":" + "2376")
				    .withDockerTlsVerify(true)
				    .withDockerCertPath(userHome + "/.docker/machine/certs")
				    .withDockerConfig(userHome + "/.docker/machine/.docker")
				    .withApiVersion("1.30") // optional
				    .withRegistryUrl("https://index.docker.io/v1/")
				    .build();
		}
		
		return localConfig;
	}
	
	/**
	 * Unallocate port so it can be re-used.
	 * 
	 * @param port
	 */
	public static void clearPort(int port) {
		allocatedPorts.put(port, Boolean.TRUE);
	}
	
	/**
	 * Create a client connection to local Docker server.
	 * 
	 * @see DockerClientBuilder
	 * 
	 * @return DockerClient
	 */
	public static DockerClient createDockerClient() {
		DockerClient dockerClient = DockerClientBuilder.getInstance(DockerUtil.getLocalDockerConfig()).build();
		return dockerClient;
	}
}