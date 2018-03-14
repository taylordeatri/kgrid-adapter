package org.kgrid.activator.adapter.api;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public interface Adapter {

  default boolean supports(final String typeName) {

    Objects.requireNonNull(typeName, "Adapter supports() method requires 'type' parameter.");

    return getTypes().stream().anyMatch(t -> t.equalsIgnoreCase(typeName));

  }

  List<String> getTypes();

  void initialize();

  Executor activate(CompoundKnowledgeObject compoundKnowledgeObject);

  Executor activate(Path resource, String endpoint);

  String status();
}
