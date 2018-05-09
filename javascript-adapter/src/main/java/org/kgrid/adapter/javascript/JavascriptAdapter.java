package org.kgrid.adapter.javascript;

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
import org.kgrid.activator.adapter.api.AdapterException;
import org.kgrid.activator.adapter.api.AdapterSupport;
import org.kgrid.activator.adapter.api.Executor;
import org.kgrid.activator.adapter.api.Result;
import org.kgrid.shelf.repository.CompoundDigitalObjectStore;


public class JavascriptAdapter implements Adapter, AdapterSupport {

  ScriptEngine engine;
  CompoundDigitalObjectStore cdoStore;

  @Override
  public String getType() {
    return "javascript".toUpperCase();
  }

  @Override
  public void initialize() {

    engine = new ScriptEngineManager().getEngineByName("JavaScript");
  }

  @Override
  public Executor activate(Path resourcePath, String endpointName) {

    ScriptContext context = new SimpleScriptContext();
    context.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
    Path scriptPath = resourcePath.resolve(endpointName + ".js");
    byte[] binary = cdoStore.getBinary(scriptPath);
    try {
      CompiledScript script = ((Compilable) engine)
          .compile(new String(binary, Charset.defaultCharset()));
      script.eval(context);
    } catch (ScriptException e) {
      throw new AdapterException("unable to compile script " + scriptPath + " : " +e.getMessage(), e);
    }

    ScriptObjectMirror mirror = (ScriptObjectMirror) context
        .getBindings(ScriptContext.ENGINE_SCOPE);

    return new Executor() {

      String endpoint = endpointName;

      @Override
      public Result execute(Object input) {

        Object output = mirror.callMember(endpoint, input);

        final Map<String, String> errors = new HashMap<>();

        return new Result(output, errors);
      }

    };
  }

  @Override
  public String status() {
    if (engine == null) {
      return "DOWN";
    }
    if (cdoStore == null) {
      return "DOWN";
    }
    return "UP";
  }


  public void setCdoStore(CompoundDigitalObjectStore cdoStore) {
    this.cdoStore = cdoStore;
  }

}
