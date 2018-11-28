package org.kgrid.adapter.javascript;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kgrid.adapter.api.Adapter;
import org.kgrid.adapter.api.AdapterSupport;
import org.kgrid.shelf.repository.CompoundDigitalObjectStore;
import org.kgrid.shelf.repository.FilesystemCDOStore;

public class JavascriptAdapterInitializeTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private CompoundDigitalObjectStore cdoStore;
  private FilesystemCDOStore simpleScripts;

  @Before
  public void setUpCDOStore() throws URISyntaxException {

    Path path = Paths.get(this.getClass().getResource("/cdo-store").toURI().toString());
    cdoStore = new FilesystemCDOStore("filesystem:" + path.toString());

    Path path1 =
        Paths.get(this.getClass().getResource("/cdo-store/simple-scripts").toURI().toString());
    simpleScripts = new FilesystemCDOStore("filesystem:" + path1.toString());

    assertEquals(3, cdoStore.getChildren(null).size());
  }

  @Test
  public void initializeWithCDOStore() {

    Adapter adapter = new JavascriptAdapter();
    ((AdapterSupport) adapter).setCdoStore(cdoStore);
    adapter.initialize(null);
    assertEquals("UP", adapter.status());

  }

  @Test
  public void initializeWithOutCDOStore() {

    Adapter adapter = new JavascriptAdapter();
    adapter.initialize(null);
    assertEquals("DOWN", adapter.status());

  }

}