package org.kgrid.adapter.resource;

import java.nio.file.Path;
import java.util.HashMap;
import org.kgrid.adapter.api.ActivationContext;
import org.kgrid.adapter.api.Adapter;
import org.kgrid.adapter.api.AdapterException;
import org.kgrid.adapter.api.Executor;

public class ResourceAdapter implements Adapter{

  private ActivationContext context;

  @Override
  public String getType() {
    return "RESOURCE";
  }

  @Override
  public void initialize(ActivationContext activationContext) {
      context = activationContext;
  }

  @Override
  public Executor activate(Path path, String entry) {
    byte[] resource;
    HashMap<String, Object> fileWithMetadata = new HashMap<>();
    HashMap<String, Object> metadata = new HashMap<>();
    try {
      resource = this.context.getBinary(path.toString());

    } catch (Exception e) {
      throw new AdapterException("Unable to read resource file at location " + path, e);
    }

    metadata.put("size (bytes)", resource.length);
    fileWithMetadata.put("value", resource);
    fileWithMetadata.put("mimetype", "application/octet-stream");
    fileWithMetadata.put("metadata", metadata);


    return (Object inputs) -> fileWithMetadata;
  }

  @Override
  public String status() {
    return "UP";
  }
}
