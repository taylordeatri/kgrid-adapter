package org.kgrid.activator.adapter.api;

import org.kgrid.shelf.domain.CompoundKnowledgeObject;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public interface Adapter {


  String getType();

  void initialize();

  // resource Path: .../99999-fk4r73t/v.1.1.2/models/resource, endpoint: "score"
  Executor activate(Path resource, String endpoint);

  String status();
}
