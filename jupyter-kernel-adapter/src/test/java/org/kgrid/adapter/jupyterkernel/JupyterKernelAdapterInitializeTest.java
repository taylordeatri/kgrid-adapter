package org.kgrid.adapter.jupyterkernel;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Properties;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.kgrid.adapter.api.Adapter;
import org.kgrid.adapter.api.AdapterSupport;
import org.kgrid.shelf.repository.CompoundDigitalObjectStore;
import org.kgrid.shelf.repository.FilesystemCDOStore;

@Category(JupyterIntegationTest.class)
public class JupyterKernelAdapterInitializeTest {

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

    assertEquals(3, cdoStore.getChildren(null).size());
  }

  @Test
  public void initializeWithCDOStore() {

     Adapter adapter = new JupyterKernelAdapter();
    ( (AdapterSupport) adapter).setCdoStore(cdoStore);
    adapter.initialize(new Properties());
    assertEquals("UP", adapter.status());

  }

  @Test
  public void initializeWithOutCDOStore() {

    Adapter adapter = new JupyterKernelAdapter();
    adapter.initialize(new Properties());
    assertEquals("DOWN", adapter.status());

  }

}