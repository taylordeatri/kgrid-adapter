package org.kgrid.adapter.javascript;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kgrid.activator.adapter.api.Adapter;
import org.kgrid.activator.adapter.api.AdapterSupport;
import org.kgrid.shelf.repository.CompoundDigitalObjectStore;
import org.kgrid.shelf.repository.FilesystemCDOStore;

public class JavascriptAdapterInitializeTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private CompoundDigitalObjectStore cdoStore;
  private FilesystemCDOStore simpleScripts;

  @Before
  public void setUpCDOStore() throws URISyntaxException {

    cdoStore = new FilesystemCDOStore(
        Paths.get(this.getClass().getResource("/cdo-store").toURI()).toString());

    simpleScripts = new FilesystemCDOStore(
        Paths.get(this.getClass().getResource("/cdo-store/simple-scripts").toURI()).toString());

    assertEquals(2, cdoStore.getChildren(null).size());
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