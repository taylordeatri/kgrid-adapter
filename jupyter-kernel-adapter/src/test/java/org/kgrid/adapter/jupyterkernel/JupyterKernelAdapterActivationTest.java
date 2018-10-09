package org.kgrid.adapter.jupyterkernel;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.kgrid.adapter.api.Adapter;
import org.kgrid.adapter.api.AdapterSupport;
import org.kgrid.adapter.api.Executor;
import org.kgrid.shelf.repository.CompoundDigitalObjectStore;
import org.kgrid.shelf.repository.FilesystemCDOStore;

@Category(JupyterIntegationTest.class)
public class JupyterKernelAdapterActivationTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private CompoundDigitalObjectStore cdoStore;
  private Adapter adapter;

  @Before
  public void setUpCDOStore() throws URISyntaxException {

    cdoStore = new FilesystemCDOStore(
        Paths.get(this.getClass().getResource("/cdo-store").toURI()).toString());

    adapter = new JupyterKernelAdapter();
    ((AdapterSupport) adapter).setCdoStore(cdoStore);
    Properties properties = new Properties();
    properties.setProperty("adapter.kernel.url", "localhost:8888/api/kernels");
    properties.setProperty("adapter.kernel.type", "python");
    adapter.initialize(properties);
  }


  @Test
  public void happyActivation() {
    Path path = Paths.get("simple-scripts", "hello.py");
    Executor executor = adapter.activate(path, "hello");
    Map input = new LinkedHashMap();
    input.put("name", "world!");
    assertEquals("'hello world!'", executor.execute(input));
    assertEquals("'hello world!'", executor.execute("{\"name\":\"world!\"}"));

    path = Paths.get("simple-scripts", "doubler.py");
    executor = adapter.activate(path, "doubler");
    assertEquals(6, Integer.parseInt(executor.execute(3).toString()) );

  }

}