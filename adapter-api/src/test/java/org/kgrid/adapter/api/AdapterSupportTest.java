package org.kgrid.adapter.api;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.util.Properties;
import org.junit.Test;

public class AdapterSupportTest {

  @Test
  public void testAdapterWithSupportsExposesSetContext() {

    Adapter adapter1 = new TestAdapter();
    Adapter adapter2 = new TestAdapterWithSupport();

    if (adapter1 instanceof AdapterSupport) {
      ((AdapterSupport) adapter1).setContext(new TestActivationContext());
    }
    if (adapter2 instanceof AdapterSupport) {
      ((AdapterSupport) adapter2).setContext(new TestActivationContext());
    }

    adapter1.initialize(null);
    adapter2.initialize(null);

    assertEquals("does not support simple-scripts", adapter1.status());

    assertEquals("supports simple script calls",adapter2.status());


  }

  private static class TestActivationContext implements ActivationContext {

    @Override
    public Executor getExecutor(String key) {
      return null;
    }

    @Override
    public byte[] getBinary(String pathToBinary) {
      return new byte[0];
    }

    @Override
    public String getProperty(String key) {
      return null;
    }
  }

  class TestAdapter implements Adapter {

    @Override
    public String getType() {
      return null;
    }

    @Override
    public void initialize(Properties properties) {

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

  class TestAdapterWithSupport extends TestAdapter implements AdapterSupport {

  private ActivationContext context;

    @Override
    public ActivationContext getContext() {
      return context;
    }

    @Override
    public void setContext(ActivationContext context) {
      this.context = context;
    }

    @Override
    public String status(){
      return "supports simple script calls";
    }
  }

}