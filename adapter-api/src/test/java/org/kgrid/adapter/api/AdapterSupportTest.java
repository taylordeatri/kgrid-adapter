package org.kgrid.adapter.api;

import org.junit.Test;
import org.kgrid.shelf.repository.CompoundDigitalObjectStore;
import org.kgrid.shelf.repository.FilesystemCDOStore;

import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

public class AdapterSupportTest {

  @Test
  public void testAdapterSupportsExposesSetShelfTest() {

    Adapter adapter1 = new TestAdapter();
    Adapter adapter2 = new TestAdapterSupport();

    if (adapter1 instanceof AdapterSupport) {
      ((AdapterSupport) adapter1).setCdoStore(new FilesystemCDOStore(""));
    }
    if (adapter2 instanceof AdapterSupport) {
      ((AdapterSupport) adapter2).setCdoStore(new FilesystemCDOStore(""));
    }

    adapter1.initialize();
    adapter2.initialize();

    assertEquals(adapter1.status(), "does not support simple-scripts");


  }

  class TestAdapter implements Adapter {

    @Override
    public String getType() {
      return null;
    }

    @Override
    public void initialize() {

    }

    @Override
    public Executor activate(Path resource, String endpoint) {
      return null;
    }

    @Override
    public String status() {
      return "does not support simple-scripts";
    }
  }

  class TestAdapterSupport implements Adapter, AdapterSupport {


    private CompoundDigitalObjectStore shelf;

    @Override
    public String getType() {
      return null;
    }

    @Override
    public void initialize() {

    }

    @Override
    public Executor activate(Path resource, String endpoint) {
      return null;
    }

    @Override
    public String status() {
      return "supports simple-scripts: " + shelf.toString();
    }

    @Override
    public void setCdoStore(CompoundDigitalObjectStore shelf) {

      this.shelf = shelf;

    }
  }

}