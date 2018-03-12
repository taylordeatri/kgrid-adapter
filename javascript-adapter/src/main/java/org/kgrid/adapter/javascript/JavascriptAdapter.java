package org.kgrid.adapter.javascript;

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
import org.kgrid.activator.adapter.api.Shelf;

public class JavascriptAdapter implements Adapter, AdapterSupport {

  ScriptEngine engine;
  Shelf shelf;

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

  @Override
  public String status() {
    return "UP";
  }

  @Override
  public List<String> getTypes() {

    return Arrays.asList("JAVASCRIPT", "JS");
  }

  @Override
  public void setShelf(Shelf shelf) {

  }
}
