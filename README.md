# Kgrid-Adapter
[![CircleCI](https://circleci.com/gh/kgrid/kgrid-adapter/tree/master.svg?style=shield)](https://circleci.com/gh/kgrid/kgrid-adapter/tree/master)

Adapters provide a common interface to load & execute code in the Knowledge Grid

## Table of Contents

1. [Build and Test Activator](#build-adpaters)
1. [Javascript Adapter](#javascript-adapter)
1. [Additional Information](#additional-information)

## Build Adpaters
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites
For building and running the application you need:

- [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven 3](https://maven.apache.org)

### Clone
To get started you can simply clone this repository using git:
```
git clone https://github.com/kgrid/kgrid-adapter.git
cd kgrid-adapter
```

```
mvn clean package
```
## Running the tests

#### Automated tests 
Unit and Integration tests can be executed via
```
mvn clean test
```

## Javascript Adapter

## Proxy Adapter

To use the proxy adapter create an object that has the url of the execution environment as the resource field and the endpoint as the functionName in the model metadata as shown:
```json
{
  "functionName":"helloworld",
  "adapterType":"PROXY",
  "resource":"http://localhost:8888",
  "adapters":[
    {"name":"Proxy","version":"0.0.2","filename":"proxy-adapter-0.0.2-SNAPSHOT.jar","download_url":"https://github.com/kgrid/ko-templates/releases/download/0.2/", "target":"adapters/"}
  ]
}
```
This metadata will cause the proxy adapter to send the post request with the supplied body on to `http://localhost:8888/helloworld` and pass the result back out.

#### Setting up a Jupyter notebook as a proxy execution environment

1. Install Jupyter [http://jupyter.org/install](http://jupyter.org/install) 
2. Start the server by running `jupyter notebook`
3. Create a notebook with annotations describing HTTP methods that can be used to execute the cells in the notebook. (See the [Jupyter documentation](http://jupyter-kernel-gateway.readthedocs.io/en/stable/http-mode.html) for more info)
4. Run the notebook in HTTP mode using the command 
`jupyter kernelgateway --KernelGatewayApp.api='kernel_gateway.notebook_http' --KernelGatewayApp.seed_uri='<PATH TO NOTEBOOK .ipynb FILE>' `

See the test object in `src/test/resources/shelf/99999-newko/` for an example of a proxy object that connects to a jupyter notebook running the helloworld.ipynb notebook.

**Jupyter Integration Testing**
There are a set of Jupyter Integration Tests that require a notebook environment. This currently is not 
part of the continiues integration process.  yo ucan run the integration tests using a particular profile

```bash
mvn clean verify -P jupyter_it
```


## Additional Information

### Adding a new Adapter
Creating new Adapters requires you implement the Adapter API interface. The Adapter's utilize Java Services and require that you package your adapter with a service identifer the META-INF.

```
META-INF
   services
        org.kgrid.adapter.javascript.JavascriptAdapter
```

### Limitations

Adapters running in activators as part of the knowledge grid may impose limitations on the kind of code in the KOs, and the format and type of inputs and outputs for the KOs. For example, the Javascript adapter requires valid JSON inputs ({}, [] at the top level) and the returned object (from the endpoint function in the KO) will be serialized back to JSON with arrays converted to objects with array indexes as keys (a limitation of the current nashorn javascript engine).

Other adapters may require CSV, return particular media types, or not allow use of some or all library code. Some adapters may not implement the activation portion of the KO lifecycle, expecting instead that the KO will already be deployed in a suitable environment and can be accessed (via the adapter) based on lookup by ark ID.

### Proposed adapters

- There is a mapping between KO types and adapter classes...how is that mapping maintained?

#### Resource Adapter

This adapter gives the ability to fetch individual binaries from a packaged knowledge object deplyed in an activator.
- May also allow metadat about the individual binary (or node/cpontainer) to be returned
- May require specification in the service description if not enabled by default.
- Stick with standard mime types or mabe application/octet-stream

#### Proxy adapter (generic; request only, no activation)

This adapter simply passes the request body unchanged to a nother endpoint and returns the response entity as is
- Assume the existence of an activated 
- Should include headers returned
- Uses paths from the service description
- HTTP only

#### Proxy adapter (type specific, with activation)

This adapter knows how to deploy a knowledge object in a remote environment and to pass requests along
- Activation is type specific (code deployment in local environment, container-based deployment, lamba deplotyment, etc.)
- 

#### Jupyter With Python Kernel Adapter

This adapter requires a jupyter instance with either a python 2 or 3 kernel running.
By default jupyter should be running on localhost:8888/ but this can be set in the properties when running an activator by setting `kgrid.adapter.kernel.url`.
If you are working with a kernel other than python you can switch to it by setting `kgrid.adapter.kernel.type`.

Instructions on how to get a jupyter kernel started can be found [here](https://jupyter-kernel-gateway.readthedocs.io/en/stable/getting-started.html).

Write your python code in a function that takes a dict of string inputs and returns a value e.g.
```python
import math
def exp(inputs):
  number = float(inputs.get("num"))
  return math.exp(number)
```

The returned value will be the result the kgrid-activator shows as the result when accessed using the REST api.
