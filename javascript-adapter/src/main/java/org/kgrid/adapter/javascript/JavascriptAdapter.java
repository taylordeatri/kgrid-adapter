package org.kgrid.adapter.javascript;

import edu.umich.lhs.activator.repository.CompoundDigitalObjectStore;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.kgrid.activator.adapter.api.Adapter;
import org.kgrid.activator.adapter.api.AdapterSupport;
import org.kgrid.activator.adapter.api.CompoundKnowledgeObject;
import org.kgrid.activator.adapter.api.Executor;
import org.kgrid.activator.adapter.api.Result;


public class JavascriptAdapter implements Adapter, AdapterSupport {

  ScriptEngine engine;
  CompoundDigitalObjectStore cdoStore;

  public ScriptEngine getEngine() {
    return engine;
  }

  @Override
  public void initialize() {

    engine = new ScriptEngineManager().getEngineByName("JavaScript");

  }

  @Override
  public Executor activate(CompoundKnowledgeObject compoundKnowledgeObject) {

    ScriptContext context = new SimpleScriptContext();
    context.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
    try {
      CompiledScript script = ((Compilable) engine).compile(compoundKnowledgeObject.getCode());
      script.eval(context);
    } catch (ScriptException e) {
      e.printStackTrace();
    }

    ScriptObjectMirror mirror = (ScriptObjectMirror) context
        .getBindings(ScriptContext.ENGINE_SCOPE);

    return new Executor() {

      String endpoint = new String(compoundKnowledgeObject.getEndpoint());

      @Override
      public Result execute(Object input) {

        Object output = mirror.callMember(endpoint, input);

        final Map<String, String> errors = new HashMap<>();
        errors.put("spec", compoundKnowledgeObject.toString());

        return new Result(output, errors);
      }

    };
  }

  public Executor activate(Path resourcePath, String endpointName) {

    ScriptContext context = new SimpleScriptContext();
    context.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
    try {
      CompiledScript script = ((Compilable) engine).compile(new String(cdoStore.getBinary(resourcePath.resolve(endpointName + ".js")), Charset.defaultCharset()));
      script.eval(context);
    } catch (ScriptException e) {
      e.printStackTrace();
    }

    ScriptObjectMirror mirror = (ScriptObjectMirror) context
        .getBindings(ScriptContext.ENGINE_SCOPE);

    return new Executor() {

      String endpoint = endpointName;

      @Override
      public Result execute(Object input) {

        Object output = mirror.callMember(endpoint, input);

        final Map<String, String> errors = new HashMap<>();
        errors.put("spec", resourcePath  + "/" + endpoint);

        return new Result(output, errors);
      }

    };
  }

  @Override
  public String status() {
    return "UP";
  }

  @Override
  public List<String> getTypes() {

    return Arrays.asList("JAVASCRIPT", "JS");
  }

  public void setCdoStore(CompoundDigitalObjectStore cdoStore) {
    this.cdoStore = cdoStore;
  }

}
