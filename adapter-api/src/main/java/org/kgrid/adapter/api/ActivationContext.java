package org.kgrid.adapter.api;

import org.kgrid.adapter.api.Executor;

public interface ActivationContext {

  Executor getExecutor(String key);

  byte[] getBinary(String pathToBinary);

  String getProperty(String key);
}
