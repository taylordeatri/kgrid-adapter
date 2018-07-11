# Kgrid-Adapter
[![CircleCI](https://circleci.com/gh/kgrid/kgrid-adapter/tree/master.svg?style=shield)](https://circleci.com/gh/kgrid/kgrid-adapter/tree/master)

## Table of Contents

1. [Build and Test Activator](#build-adpaters)
1. [Javascript Adapter](#javascript-adapter])
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



## Additional Information

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
