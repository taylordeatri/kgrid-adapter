package org.kgrid.activator.adapter.api;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Result<T> {

  private final T result;
  private final Map<String, String> errors;

  public Result(T result) {
    this(result, new HashMap<String, String>());
  }

  public Result(Map errors) {
    this(null, new HashMap<String, String>());
  }

  public Result(T result, Map<String, String> errors) {
    this.result = result;
    this.errors = errors;
  }

  public T getResult() {
    return result;
  }

  public Map<String, String> getErrors() {
    return Collections.unmodifiableMap(errors);
  }

  void putErrors(Map<String, String> errors) {

    this.errors.putAll(errors);
  }

  void putError(String key, String value) {
    this.errors.put(key, value);
  }

  @Override
  public String toString() {
    return "Result{" +
        "result=" + result +
        ", errors=" + errors +
        '}';
  }


}
