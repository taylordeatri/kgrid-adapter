package org.kgrid.activator.adapter.javascript;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import jdk.nashorn.internal.runtime.ECMAException;
import org.kgrid.adapter.api.Adapter;
import org.kgrid.adapter.api.Executor;
import org.kgrid.adapter.javascript.JavascriptAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

@RestController
@SpringBootApplication
public class JavascriptAdapterGateway {

  @Autowired
  Adapter adapter;

  Map<String, Executor> executorMap;

  public static void main(String[] args) {
    SpringApplication.run(JavascriptAdapterGateway.class, args);

    System.out.println("Starting JavascriptAdapterGateway");
  }

  @Bean
  public Adapter getAdapter() {

    adapter = new JavascriptAdapter();
    adapter.initialize();
    return adapter;
  }

  @PostMapping("/activate/{naan}-{name}/{version}/{endpoint}")
  public void activate(@PathVariable String naan, @PathVariable String name, @PathVariable String version, @PathVariable String endpoint) {

    Executor executor = adapter.activate(Paths.get(naan + "-" + name, version, "models", "resource"), endpoint);
    if(executorMap == null) {
      executorMap = new HashMap<>();
    }
    executorMap.put(endpoint, executor);
  }

  @PostMapping("/execute/{naan}-{name}/{version}/{endpoint}")
  public Object executeEndpoint(@PathVariable String naan, @PathVariable String name, @PathVariable String version, @PathVariable String endpoint, @RequestBody Map inputs) {

    return executorMap.get(endpoint).execute(inputs).getResult();
  }

  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<String> handleNullExceptions(NullPointerException e, ServletWebRequest request) {
    return new ResponseEntity<>("Cannot locate data at " + request.getRequest().getRequestURI(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ECMAException.class)
  public ResponseEntity<String> handleNullExceptions(ECMAException e, ServletWebRequest request) {
    return new ResponseEntity<>("Error running javascript code: " + e.getMessage(), HttpStatus.FAILED_DEPENDENCY);
  }


}
