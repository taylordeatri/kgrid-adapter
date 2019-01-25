package org.kgrid.adapter.jupyterkernel;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.kgrid.adapter.api.Adapter;
import org.kgrid.shelf.repository.CompoundDigitalObjectStore;
import org.kgrid.shelf.repository.FilesystemCDOStore;

@Category(JupyterIntegationTest.class)
public class JupyterKernelAdapterInitializeTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private CompoundDigitalObjectStore cdoStore;

  @Before
  public void setUpCDOStore() throws URISyntaxException {

    cdoStore = new FilesystemCDOStore(
        Paths.get(this.getClass().getResource("/cdo-store").toURI()).toString());

//    assertEquals(3, cdoStore.getChildren(null).size());
  }

//  @Test
//  public void initializeWithCDOStore() {
//
//     Adapter adapter = new JupyterKernelAdapter();
//    adapter.initialize(new Properties());
//    assertEquals("UP", adapter.status());
//
//  }

  @Test
  public void initializeWithOutCDOStore() {

    Adapter adapter = new JupyterKernelAdapter();
    adapter.initialize(null);
    assertEquals("DOWN", adapter.status());

  }

}