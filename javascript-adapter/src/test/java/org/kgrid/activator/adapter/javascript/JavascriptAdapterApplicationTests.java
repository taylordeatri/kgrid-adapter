package org.kgrid.activator.adapter.javascript;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.umich.lhs.activator.repository.CompoundDigitalObjectStore;
import edu.umich.lhs.activator.repository.FilesystemCDOStore;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.kgrid.activator.adapter.api.CompoundKnowledgeObject;
import org.kgrid.activator.adapter.api.Executor;
import org.kgrid.activator.adapter.api.Result;
import org.kgrid.adapter.javascript.JavascriptAdapter;
import org.springframework.mock.web.MockMultipartFile;

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

    final CompoundKnowledgeObject compoundKnowledgeObject = new CompoundKnowledgeObject();

    compoundKnowledgeObject.setOutputType("java.lang.Double");
    compoundKnowledgeObject.setCode("function foo(a) {return a * 2;}");
    compoundKnowledgeObject.setEndpoint("foo");
    adapter.initialize();
    Executor x = adapter.activate(compoundKnowledgeObject);

    System.out.println(x.execute("2"));

    compoundKnowledgeObject.setOutputType("java.lang.String");

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

    // Set up a 'CompoundKnowledgeObject' for a hello world object
    final CompoundKnowledgeObject compoundKnowledgeObject = new CompoundKnowledgeObject();

    compoundKnowledgeObject.setCode(
        "function sayHello(name) {\n" +
            "  return 'Hello, ' + name + '!' + foo(2);\n" +
            "};"
    );
    compoundKnowledgeObject.setEndpoint("sayHello");
    compoundKnowledgeObject.setOutputType("java.lang.String");
    compoundKnowledgeObject.setInputType("java.lang.String");

    Executor x = adapter.activate(compoundKnowledgeObject);

    System.out.println(x.execute("Bob"));

    // compoundKnowledgeObject is copied local to Executor so changing compoundKnowledgeObject won't matter
//		compoundKnowledgeObject.setOutputType("java.lang.Double");

    System.out.println(x.execute("EmmyLou"));

    // Let's try a second one
    CompoundKnowledgeObject compoundKnowledgeObject2 = new CompoundKnowledgeObject(
        compoundKnowledgeObject);

    compoundKnowledgeObject2.setCode("var bye = function(name) { return 'Bye, ' + name + '!'; }");
    compoundKnowledgeObject2.setEndpoint("bye");
    compoundKnowledgeObject2.setOutputType("java.lang.String");
    compoundKnowledgeObject2.setInputType("java.lang.String");

    Executor y = adapter.activate(compoundKnowledgeObject2);
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
  public void testUsingCDOStore() throws Exception {
    JavascriptAdapter adapter = new JavascriptAdapter();

    Path shelf = Files.createTempDirectory("testShelf");

    CompoundDigitalObjectStore cdoStore = new FilesystemCDOStore(shelf.toAbsolutePath().toString());

    adapter.setCdoStore(cdoStore);

    String filename = "99999-fk45m6gq9t.zip";

    URL zipStream = JavascriptAdapterApplicationTests.class.getResource("/" + filename);
    byte[] zippedKO = Files.readAllBytes(Paths.get(zipStream.toURI()));
    MockMultipartFile koZip = new MockMultipartFile("ko", filename, "application/zip", zippedKO);

    ObjectNode metadata = cdoStore.addCompoundObjectToShelf(koZip);


    adapter.initialize();
    Executor ex = adapter.activate(Paths.get("99999-fk45m6gq9t", "v0.0.1", "models", "resource"), "content");
    Result res = ex.execute("10");
    assertEquals("10", res.getResult());

  }

  enum Type {FOO, BAR}

}
