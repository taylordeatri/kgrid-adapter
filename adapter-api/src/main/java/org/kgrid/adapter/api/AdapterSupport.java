package org.kgrid.adapter.api;

import java.util.Map;
import org.kgrid.shelf.repository.CompoundDigitalObjectStore;

public interface AdapterSupport {

  void setCdoStore(CompoundDigitalObjectStore cdoStore);

  default void setEndpoints(Map<String, Object> endpoints) {

  }
}
