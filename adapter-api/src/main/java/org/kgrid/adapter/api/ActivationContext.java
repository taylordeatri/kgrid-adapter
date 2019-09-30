package org.kgrid.adapter.api;

import java.io.InputStream;

public interface ActivationContext {

  Executor getExecutor(String key);

  byte[] getBinary(String pathToBinary);
  
  InputStream getInputStream(String pathToResource);

  String getProperty(String key);
}
