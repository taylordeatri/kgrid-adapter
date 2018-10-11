package org.kgrid.activator.adapter.jupyterkernel;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Properties;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.kgrid.adapter.api.Adapter;
import org.kgrid.adapter.api.AdapterException;
import org.kgrid.adapter.api.AdapterSupport;
import org.kgrid.adapter.api.Executor;
import org.kgrid.adapter.jupyterkernel.JupyterIntegationTest;
import org.kgrid.adapter.jupyterkernel.JupyterKernelAdapter;
import org.kgrid.shelf.repository.CompoundDigitalObjectStore;
import org.kgrid.shelf.repository.FilesystemCDOStore;

@Category(JupyterIntegationTest.class)
public class JupyterKernelAdapterApplicationTests {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  private CompoundDigitalObjectStore cdoStore;

  @Before
  public void setUpCDOStore() throws URISyntaxException {

    cdoStore = new FilesystemCDOStore(
        Paths.get(this.getClass().getResource("/cdo-store/simple-scripts").toURI()).toString());

  }


  @Test
  public void contextLoads() {
  }

  @Test
  public void typeHandling() {

    JupyterKernelAdapter adapter = new JupyterKernelAdapter();

    EnumSet<Type> types = EnumSet.allOf(Type.class);

    Stream<Type> typeStream = EnumSet.allOf(Type.class).stream();

    System.out.println(types.contains(Type.FOO));
    System.out.println(typeStream.anyMatch(t -> t.name().equalsIgnoreCase("Foo")));

    System.out
        .println(Arrays.stream(Type.values()).anyMatch(t -> t.name().equalsIgnoreCase("FoOo")));

  }

  @Test
  public void testExecutor() {

    Adapter adapter = new JupyterKernelAdapter();


    adapter.initialize(new Properties());
    ((AdapterSupport) adapter).setCdoStore(cdoStore);
    Executor x = adapter.activate(Paths.get("doubler.py"), "doubler");

    Object r = x.execute(3);

    assertEquals("6", r);
  }

  @Test
  public void testEngine() {
    JupyterKernelAdapter adapter = new JupyterKernelAdapter();

    adapter.initialize(new Properties());
    adapter.setCdoStore(cdoStore);

    Executor x = adapter.activate(Paths.get("hello.py"), "hello");

    Object bob = x.execute("'Bob'");
    System.out.println(bob);

    System.out.println(x.execute("'EmmyLou'"));

    // Let's try a second one

    Executor y = adapter.activate(Paths.get("hello.py"), "hello");
    System.out.println(y.execute("'Lorelei'"));

    System.out.println(x.execute("'Ralph'"));

  }


  @Test
  public void testCanActivateKoFromCdoStore() throws Exception {
    JupyterKernelAdapter adapter = new JupyterKernelAdapter();
    String path = (new File(this.getClass().getResource("/cdo-store").toURI())).getPath();
    CompoundDigitalObjectStore cdoStore = new FilesystemCDOStore(path);

    adapter.setCdoStore(cdoStore);

    adapter.initialize(new Properties());
    Executor ex = adapter
        .activate(Paths.get("99999-python", "v0.0.1", "models", "resource", "content.py"), "content");
    Object res = ex.execute("10");
    assertEquals("'10test'", res);

  }

  @Test
  public void testCantActivateKoFromCdoStoreBadEndPoint() throws Exception {

    exception.expect(AdapterException.class);

    JupyterKernelAdapter adapter = new JupyterKernelAdapter();
    String path = (new File(this.getClass().getResource("/cdo-store").toURI())).getPath();
    CompoundDigitalObjectStore cdoStore = new FilesystemCDOStore(path);

    adapter.setCdoStore(cdoStore);

    adapter.initialize(new Properties());
    Executor ex = adapter
        .activate(Paths.get("99999-python", "v0.0.1", "models", "resource"), "xxxxx");
    Object res = ex.execute("10");
  }

  enum Type {FOO, BAR}

}
