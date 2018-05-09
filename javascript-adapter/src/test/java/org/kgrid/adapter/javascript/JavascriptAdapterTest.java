package org.kgrid.adapter.javascript;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Test;
import org.kgrid.activator.adapter.api.Adapter;
import org.kgrid.activator.adapter.api.AdapterSupport;
import org.kgrid.shelf.repository.CompoundDigitalObjectStore;
import org.kgrid.shelf.repository.FilesystemCDOStore;

public class JavascriptAdapterTest {

  private CompoundDigitalObjectStore cdoStore;

  @Before
  public void setUpCDOStore() throws URISyntaxException {

    cdoStore = new FilesystemCDOStore(
        Paths.get(this.getClass().getResource("/cdo-store").toURI()).toString());

    assertEquals(1, cdoStore.getChildren(null).size());
  }

  @Test
  public void initializeWithCDOStore() {

     Adapter adapter = new JavascriptAdapter();
    ( (AdapterSupport) adapter).setCdoStore(cdoStore);
    adapter.initialize();
    assertEquals("UP", adapter.status());

  }

  @Test
  public void initializeWithOutCDOStore() {

    Adapter adapter = new JavascriptAdapter();
    adapter.initialize();
    assertEquals("DOWN", adapter.status());

  }
}