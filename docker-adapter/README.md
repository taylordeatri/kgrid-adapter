# Docker Adapter

## Purpose
This docker adapter supports docker based deployments of knowlege objects. It supports multiple deployments including dynamic loading of saved images, running an image as a container and auto-mapping to a range of ports and finding an already running container.


## Usage
When using this adapter you specify the the adapter type as **DOCKER** and must have a docker-config.yaml file with these values:

``` yaml

docker-image: <docker_image_name_here>
url: <qualified url to invoke service including place holder for _PORT_>
docker-image-archive: <SAVED docker image archive that can be loaded into the local docker repository>
```

Of these only the **docker-image** property is required.

### example docker-config.yaml values:

``` yaml

docker-image: some-docker-image-name
url: http://localhost:_PORT_/invocations
docker-image-archive: some-docker-image-archive.tgz

```

### Environment / Property Variables
This docker adapter uses the [Docker Java Client| https://github.com/docker-java/docker-java]. As such there are certain properties expected to be set to configure the connection parameters to the local Docker engine:

``` sh
export DOCKER_HOST=tcp://192.168.99.100:2376
export DOCKER_CONFIG=c:/users/<your user>/.docker/machine/.docker
export DOCKER_CERTS=c:/users/<your user>/.docker/machine/certs
export KGRID_DOCKER_PORT_BASE=5100
export KGRID_DOCKER_PORT_COUNT=10
```

## Special Considerations



## Future Enhancements


