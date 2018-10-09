package org.kgrid.adapter.api;

public class AdapterException extends RuntimeException {

  public AdapterException(String message, Throwable cause) {
    super(message, cause);
  }

  public AdapterException(String message) {
    super(message);
  }
}
