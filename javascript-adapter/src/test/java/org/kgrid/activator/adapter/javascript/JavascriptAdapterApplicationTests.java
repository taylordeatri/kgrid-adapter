package org.kgrid.activator.adapter.javascript;

import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;
import java.io.File;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Stream;
import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kgrid.activator.adapter.api.Adapter;
import org.kgrid.activator.adapter.api.Executor;
import org.kgrid.activator.adapter.api.Result;
import org.kgrid.adapter.javascript.JavascriptAdapter;
import org.kgrid.shelf.domain.CompoundKnowledgeObject;
import org.kgrid.shelf.repository.CompoundDigitalObjectStore;
import org.kgrid.shelf.repository.FilesystemCDOStore;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class JavascriptAdapterApplicationTests {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

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
  public void typeHandlingTwo() {

    Adapter adapter = new JavascriptAdapter();

    System.out.println(adapter.supports("java.script"));

  }

  @Test
  public void testExecutor() {

    Adapter adapter = new JavascriptAdapter();

    adapter.initialize();
    Executor x = adapter.activate(Paths.get(""), "doubler");

    System.out.println(x.execute("2"));

//		exception.expect(Throwable.class);
    Result r = x.execute(3);

    assertEquals(6.0, r.getResult());
  }

  @Test
  public void testEngine() {
    JavascriptAdapter adapter = new JavascriptAdapter();

    adapter.initialize();

    try {
      adapter.getEngine().eval("function foo(a) {return a * 2;}");
    } catch (ScriptException e) {
      e.printStackTrace();
    }

    Executor x = adapter.activate(Paths.get(""), "hello");

    Result bob = x.execute("Bob");
    System.out.println(bob);

    System.out.println(x.execute("EmmyLou"));

    // Let's try a second one

    Executor y = adapter.activate(Paths.get(""), "hello");
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
    Executor ex = adapter.activate(Paths.get("99999-fk45m6gq9t", "v0.0.1", "models", "resource"), "content");
    Result res = ex.execute("10");
    assertEquals("10", res.getResult());

  }

  enum Type {FOO, BAR}

}
