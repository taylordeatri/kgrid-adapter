package org.kgrid.activator.adapter.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Test;

public class AdapterSupportTest {

  @Test
  public void testAdapterSupportsExposesSetShelfTest() {

    Adapter adapter1 = new TestAdapter();
    Adapter adapter2 = new TestAdapterSupport();

    if (adapter1 instanceof AdapterSupport) {
      ((AdapterSupport) adapter1).setShelf(new Shelf());
    }
    if (adapter2 instanceof AdapterSupport) {
      ((AdapterSupport) adapter2).setShelf(new Shelf());
    }

    adapter1.initialize();
    adapter2.initialize();

    assertEquals(adapter1.status(), "does not support shelf");

    assertTrue(
        "Status should contain: " + Shelf.class.getCanonicalName(),
        adapter2.status().contains(Shelf.class.getCanonicalName())
    );

  }

  class TestAdapter implements Adapter {

    @Override
    public List<String> getTypes() {
      return null;
    }

    @Override
    public void initialize() {

    }

    @Override
    public Executor activate(CompoundKnowledgeObject compoundKnowledgeObject) {
      return null;
    }

    @Override
    public String status() {
      return "does not support shelf";
    }
  }

  class TestAdapterSupport implements Adapter, AdapterSupport {


    private Shelf shelf;

    @Override
    public List<String> getTypes() {
      return null;
    }

    @Override
    public void initialize() {

    }

    @Override
    public Executor activate(CompoundKnowledgeObject compoundKnowledgeObject) {
      return null;
    }

    @Override
    public String status() {
      return "supports shelf: " + shelf.toString();
    }

    @Override
    public void setShelf(Shelf shelf) {

      this.shelf = shelf;

    }
  }

}