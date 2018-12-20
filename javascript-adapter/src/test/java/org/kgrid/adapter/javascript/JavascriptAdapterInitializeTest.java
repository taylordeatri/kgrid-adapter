package org.kgrid.adapter.javascript;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;
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

    URI uri = this.getClass().getResource("/shelf").toURI();
    cdoStore = new FilesystemCDOStore("filesystem:" + uri.toString());

    URI uri1 = this.getClass().getResource("/shelf/simple-scripts").toURI();
    simpleScripts = new FilesystemCDOStore("filesystem:" + uri1.toString());

    assertEquals(4, cdoStore.getChildren("").size());
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