/**
 * 
 */
package org.kgrid.adapter.docker;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kgrid.adapter.api.ActivationContext;
import org.kgrid.adapter.docker.util.DockerUtil;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse.ContainerState;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerPort;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Ports;

/**
 * This class manages the lookup of docker containers, starts them if needed, and stops /removes them if needed.
 * 
 * @see DockerUtil
 * 
 * @author taylorde
 *
 */
public class DockerRunner {
	
	private static final Log log = LogFactory.getLog(DockerRunner.class);
	
	ActivationContext context; 

	public DockerRunner(ActivationContext context) {
		this.context = context;
	}

	public Optional<RunningDockerContainer> startDockerContainer(String imageName, int port, String imageArchivePath) throws IOException {
		
		try(DockerClient dockerClient = DockerUtil.createDockerClient()) {
			
			log.info(String.format("STARTING DOCKER CONTAINER: %s on port %d", imageName, port) );
			
			Optional<Container> container = getContainer(dockerClient, imageName);


			if ( !containerIsRunning(dockerClient, container) ) {
				String containerId = createContainer(dockerClient, imageName, port, imageArchivePath);
				RunningDockerContainer runningContainer = new RunningDockerContainer(containerId, port);
				log.info(String.format("STARTED CONTAINER: for %s on port %d with id = %s", imageName, port, containerId) );
				return Optional.of(runningContainer); 
			} else {
				if ( container.isPresent() ) {
					log.info(String.format("FOUND RUNNING DOCKER CONTAINER: for %s on port %d with id = %s", imageName, port, container.get().getId()) );
					ContainerPort[] containerPorts = container.get().getPorts();
					
					ContainerPort firstPort = containerPorts[0];
					Integer publicPort = firstPort.getPublicPort();
					if ( publicPort > 0 ) {
						RunningDockerContainer runningContainer = new RunningDockerContainer(container.get().getId(), publicPort);
						return Optional.of(runningContainer);
					} else {
						return Optional.empty();
					}
				} else {
					return Optional.empty();
				}
			}
		}
	}

	String createContainer(DockerClient dockerClient, String imageName, int port, String imageArchivePath) {
		ExposedPort internalPort = ExposedPort.tcp(8080);
		Ports portBindings = new Ports();
		portBindings.bind(internalPort, Ports.Binding.bindPort(port));
		
		// Verify docker image exists and if it doesn't do we have a valid path to an archive to load?
		List<Image> images = dockerClient.listImagesCmd().withImageNameFilter(imageName).exec();
		if ( images.size() <= 0 ) {	// Image not found so verify archive path
			if ( StringUtils.isNotBlank(imageArchivePath)) {
				dockerClient.loadImageCmd(context.getInputStream(imageArchivePath)).exec();
				List<Image> images2 = dockerClient.listImagesCmd().withImageNameFilter(imageName).exec();
				if ( images.size() <= 0 ) {
					log.error(String.format("ERROR - Failed to load image %s to local docker repo.", imageArchivePath));
				}
			}
		}
		
		log.info(String.format("CREATING DOCKER CONTAINER: for %s on port %d", imageName, port ) );

		CreateContainerResponse containerResponse = dockerClient.createContainerCmd(imageName)
			.withExposedPorts(internalPort)
			.withHostConfig(HostConfig.newHostConfig().withPortBindings(portBindings))
			.withName(getContainerNameFromPort(port))
			.exec();
		
		String containerId = containerResponse.getId();
		
		String[] containerCreateWarnings = containerResponse.getWarnings();
		if ( containerCreateWarnings != null && containerCreateWarnings.length > 0 ) {
			log.error(
					"Container creation warnings:\n" +
							Arrays.asList(containerCreateWarnings)
								.stream()
									.collect(Collectors.joining(",\n")
							)
				);
		}
		
		dockerClient.startContainerCmd(containerId).exec();
		
		return containerId;
	}
	
	public void stopDockerContainer(int port, String imageName) throws IOException {
//		String containerName = getContainerNameFromPort(port);
		
		try(DockerClient dockerClient = DockerUtil.createDockerClient()) {
			if (containerIsRunning(dockerClient, imageName) ) {
				stopAndRemoveContainer(dockerClient, imageName, port);
			} else {
				log.error("Container was supposed to stop but is already stopped.");
			}
		}
	}

	boolean containerIsRunning(DockerClient dockerClient, Optional<Container> container) {
		if ( container.isPresent() ) {
			log.info(String.format("IS DOCKER CONTAINER RUNNING: with name = %s", container.get().getNames()[0]) );
			InspectContainerResponse containerResponse = dockerClient.inspectContainerCmd(container.get().getId()).exec();
			ContainerState containerState = containerResponse.getState();
			if ( ! containerState.getRunning() ) {
				log.info( String.format("DOCKER CONTAINER IS NOT RUNNING - REMOVING IT: id = %s", container.get().getId()) );
				dockerClient.removeContainerCmd(container.get().getId()).exec();
				return false;
			} else {
				log.info(String.format("DOCKER CONTAINER IS RUNNING: with name = %s", container.get().getNames()[0]) );
			}
			return containerState.getRunning();
		}
		log.info(String.format("DOCKER IS NOT RUNNING CONTAINER") );
		return false;
	}
	

	public boolean containerIsRunning(int port) throws IOException {
		String containerName = getContainerNameFromPort(port);

		log.info(String.format("IS DOCKER CONTAINER RUNNING: with name = %s", containerName) );

		try(DockerClient dockerClient = DockerUtil.createDockerClient()) {
			return containerIsRunning(dockerClient, containerName);
		}
	}
	
	Optional<Container> getContainer(DockerClient dockerClient, String imageName) {
		List<Container> existingContainers = dockerClient.listContainersCmd().withAncestorFilter(Arrays.asList(new String[] { imageName }) ).exec();
		if ( existingContainers != null && existingContainers.size() >= 1) {
			Container container = existingContainers.get(0);
			return Optional.of(container);
		} else {
			return Optional.empty();
		}
	}


	boolean containerIsRunning(DockerClient dockerClient, String imageName) {
		Optional<Container> container = getContainer(dockerClient, imageName);
		return containerIsRunning(dockerClient, container);
	}

	void stopAndRemoveContainer(DockerClient dockerClient, String imageName, int port) {
		Optional<Container> container = getContainer(dockerClient, imageName);
		if ( container.isPresent()) {
			dockerClient.stopContainerCmd(container.get().getId());
			dockerClient.removeContainerCmd(container.get().getId());
		}
		DockerUtil.clearPort(port);
	}
	
	String getContainerNameFromPort(int port) {
		return String.format("kgrid_docker_%d", port);
	}
	
	public static class RunningDockerContainer {
		String containerId;
		int runningPort;
		
		public RunningDockerContainer(String containerId, int runningPort) {
			this.containerId=containerId;
			this.runningPort = runningPort;
		}
		
		public String getContainerId() {
			return containerId;
		}
		
		public int getRunningPort() {
			return runningPort;
		}
	}
}
