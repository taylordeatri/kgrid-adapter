package org.kgrid.adapter.javascript;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.kgrid.adapter.api.Adapter;
import org.kgrid.adapter.api.AdapterException;
import org.kgrid.adapter.api.AdapterSupport;
import org.kgrid.adapter.api.Executor;
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
  public Executor activate(Path resourcePath, String functionName) {

    ScriptContext context = new SimpleScriptContext();
    context.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
    String scriptPath = resourcePath.toString();
    byte[] binary = cdoStore.getBinary(scriptPath);

    if(binary==null){
      throw new AdapterException("Can't find endpoint " + functionName + " in path " + scriptPath,null);
    }

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

      String endpoint = functionName;

      @Override
      public synchronized Object execute(Object input) {

        try {
          CompiledScript script = ((Compilable) engine)
              .compile(new String(binary, Charset.defaultCharset()));
          script.eval(context);
        } catch (ScriptException e) {
          throw new AdapterException("unable to compile script " + scriptPath + " : " +e.getMessage(), e);
        }

        Object output = mirror.callMember(endpoint, input);

        final Map<String, String> errors = new HashMap<>();

        return output;
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
