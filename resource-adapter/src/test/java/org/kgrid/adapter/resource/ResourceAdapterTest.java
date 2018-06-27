package org.kgrid.adapter.resource;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Test;
import org.kgrid.adapter.api.Executor;
import org.kgrid.shelf.repository.CompoundDigitalObjectStore;
import org.kgrid.shelf.repository.FilesystemCDOStore;

public class ResourceAdapterTest {

  private CompoundDigitalObjectStore cdoStore;

  @Before
  public void setCdoStore() throws URISyntaxException {
    cdoStore = new FilesystemCDOStore(
        Paths.get(this.getClass().getResource("/cdo-store").toURI()).toString());
  }

  @Test
  public void testSimpleResourceReturn() {
    ResourceAdapter resourceAdapter = new ResourceAdapter();
    resourceAdapter.setCdoStore(cdoStore);
    Path resource = Paths.get("99999-newko/v0.0.1/model/resource");
    Executor executor = resourceAdapter.activate(resource, "test");
    assertEquals("Hello, World", new String((byte[])executor.execute(null), Charset.defaultCharset()));
  }
}