package org.kgrid.activator.adapter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.kgrid.activator.adapter.api.Adapter;
import org.kgrid.activator.adapter.api.CompoundKnowledgeObject;
import org.kgrid.activator.adapter.api.Executor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AdapterGateway {

  public static void main(String[] args) {

    SpringApplication.run(AdapterGateway.class, args);

    System.out.println("Starting AdapterGateway");

  }

  // Supply dummy adapter for Controller

  @Bean
  public Adapter getAdapter() {

    return new Adapter() {

      private List<String> types = Arrays.asList("Foo", "bar", "BAZ");

      public boolean supports(final String typeName) {

        Objects.requireNonNull(typeName, "Adapter supports() method requires 'type' parameter.");

        return getTypes().stream().anyMatch(t -> t.equals(typeName));

      }

      @Override
      public List<String> getTypes() {
        types.replaceAll(String::toUpperCase);
        return types;
      }

      @Override
      public void initialize() {

      }

      @Override
      public Executor activate(CompoundKnowledgeObject compoundKnowledgeObject) {
        return null;
      }

      @Override
      public String status() {
        return "UP";
      }
    };
  }

  enum Types {FOO, BAR;}
}
