package org.kgrid.adapter.resource;

import java.nio.charset.Charset;
import java.nio.file.Path;
import org.kgrid.adapter.api.Adapter;
import org.kgrid.adapter.api.AdapterSupport;
import org.kgrid.adapter.api.Executor;
import org.kgrid.shelf.repository.CompoundDigitalObjectStore;

public class ResourceAdapter implements Adapter, AdapterSupport {

  private CompoundDigitalObjectStore cdoStore;

  @Override
  public String getType() {
    return "RESOURCE";
  }

  @Override
  public void initialize() {

  }

  @Override
  public Executor activate(Path resource, String endpointName)  {
    String resourceName = cdoStore.getMetadata(resource.getParent()).get("resource").asText();
    Path resourcePath = resource.getParent().resolve(resourceName);
    byte[] file = cdoStore.getBinary(resourcePath);
    return new Executor() {
      @Override
      public Object execute(Object o) {
        return file;
      }
    };
  }



  @Override
  public String status() {
    if(cdoStore != null) {
     return "UP" ;
    } else {
      return "DOWN";
    }
  }

  @Override
  public void setCdoStore(CompoundDigitalObjectStore compoundDigitalObjectStore) {
    this.cdoStore = compoundDigitalObjectStore;
  }
}
