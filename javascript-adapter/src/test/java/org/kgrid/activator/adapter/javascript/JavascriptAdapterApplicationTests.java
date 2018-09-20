package org.kgrid.activator.adapter.javascript;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;
import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kgrid.adapter.api.Adapter;
import org.kgrid.adapter.api.AdapterException;
import org.kgrid.adapter.api.AdapterSupport;
import org.kgrid.adapter.api.Executor;
import org.kgrid.adapter.javascript.JavascriptAdapter;
import org.kgrid.shelf.repository.CompoundDigitalObjectStore;
import org.kgrid.shelf.repository.FilesystemCDOStore;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class JavascriptAdapterApplicationTests {

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

    JavascriptAdapter adapter = new JavascriptAdapter();

    EnumSet<Type> types = EnumSet.allOf(Type.class);

    Stream<Type> typeStream = EnumSet.allOf(Type.class).stream();

    System.out.println(types.contains(Type.FOO));
    System.out.println(typeStream.anyMatch(t -> t.name().equalsIgnoreCase("Foo")));

    System.out
        .println(Arrays.stream(Type.values()).anyMatch(t -> t.name().equalsIgnoreCase("FoOo")));

  }

  ;

  @Test
  public void testExecutor() {

    Adapter adapter = new JavascriptAdapter();

    adapter.initialize();
    ((AdapterSupport) adapter).setCdoStore(cdoStore);
    Executor x = adapter.activate(Paths.get("doubler.js"), "doubler");

//		exception.expect(Throwable.class);
    Object r = x.execute(3);

    assertEquals(6.0, r);
  }

  @Test
  public void testEngine() {
    JavascriptAdapter adapter = new JavascriptAdapter();

    adapter.initialize();
    adapter.setCdoStore(cdoStore);

    Executor x = adapter.activate(Paths.get("hello.js"), "hello");

    Object bob = x.execute("Bob");
    System.out.println(bob);

    System.out.println(x.execute("EmmyLou"));

    // Let's try a second one

    Executor y = adapter.activate(Paths.get("hello.js"), "hello");
    System.out.println(y.execute("Lorelei"));

    System.out.println(x.execute("Ralph"));


  }

  @Test
  public void testCompiledScripts() throws ScriptException, NoSuchMethodException {

    ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

    ScriptContext context = new SimpleScriptContext();

    Bindings bindings = engine.createBindings();

    context.setBindings(bindings, ScriptContext.ENGINE_SCOPE);

    engine.eval("function bye(name) { return 'Bye, ' + name + '!'; }", context);

    engine.setContext(context);
    Invocable invoker = ((Invocable) engine);

    String output = (String) invoker.invokeFunction("bye", "Bob");

    System.out.println(output);
  }

  @Test
  public void testCanActivateKoFromCdoStore() throws Exception {
    JavascriptAdapter adapter = new JavascriptAdapter();
    String path = (new File(this.getClass().getResource("/cdo-store").toURI())).getPath();
    CompoundDigitalObjectStore cdoStore = new FilesystemCDOStore(path);

    adapter.setCdoStore(cdoStore);

    adapter.initialize();
    Executor ex = adapter
        .activate(Paths.get("99999-fk45m6gq9t", "v0.0.1", "models", "resource", "content.js"), "content");
    Object res = ex.execute("10");
    assertEquals("10test", res);

  }
  @Test
  public void testCantActivateKoFromCdoStoreBadEndPoint() throws Exception {

    exception.expect(AdapterException.class);

    JavascriptAdapter adapter = new JavascriptAdapter();
    String path = (new File(this.getClass().getResource("/cdo-store").toURI())).getPath();
    CompoundDigitalObjectStore cdoStore = new FilesystemCDOStore(path);

    adapter.setCdoStore(cdoStore);

    adapter.initialize();
    Executor ex = adapter
        .activate(Paths.get("99999-fk45m6gq9t", "v0.0.1", "models", "resource"), "xxxxx");
    Object res = ex.execute("10");


  }

  @Test
  public void testCanExecuteKoMultipleTimesAndGetDifferentResults() throws Exception {
    JavascriptAdapter adapter = new JavascriptAdapter();
    String path = (new File(this.getClass().getResource("/cdo-store").toURI())).getPath();
    CompoundDigitalObjectStore cdoStore = new FilesystemCDOStore(path);

    adapter.setCdoStore(cdoStore);

    adapter.initialize();
    drugEx = adapter
        .activate(Paths.get("99999-fk4058s74p", "v0.0.1", "model", "resource", "recommendation.js"), "dosingrecommendation");
    Map<String, Map> inputs = new HashMap<>();
    Map<String, String> hlab = new HashMap<>();
    hlab.put("diplotype", "*58:01/*1");
    inputs.put("HLA-B", hlab);

    Map<String, Map> inputs2 = new HashMap<>();
    Map<String, String> hlab2 = new HashMap<>();
    hlab2.put("diplotype", "*1/*1");
    inputs2.put("HLA-B", hlab2);

    ExecutorService executorService = Executors.newSingleThreadExecutor();
    ExecutorService executorService2 = Executors.newSingleThreadExecutor();
    Callable<Map> callable = () -> (Map)drugEx.execute(inputs);
    Callable<Map> callable2 = () -> (Map)drugEx.execute(inputs2);

    Future<Map> future = executorService.submit(callable);
    Future<Map> future2 = executorService2.submit(callable2);
    executorService.shutdown();
    executorService2.shutdown();

    Object res = future.get();
    assertEquals("Allopurinol is contraindicated", ((Map)((Map)res).get("recommendation")).get("content"));
    assertEquals("*58:01/*1", ((Map)((Map)((Map)res).get("genes")).get("HLA-B")).get("diplotype"));

    Object res2 = future2.get();
    assertEquals("Use allopurinol per standard dosing guidelines", ((Map)((Map)res2).get("recommendation")).get("content"));
    assertEquals("*1/*1", ((Map)((Map)((Map)res2).get("genes")).get("HLA-B")).get("diplotype"));

  }

  Executor drugEx;


  enum Type {FOO, BAR}

}
