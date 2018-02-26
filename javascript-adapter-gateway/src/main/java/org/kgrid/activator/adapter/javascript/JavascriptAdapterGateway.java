package org.kgrid.activator.adapter.javascript;

import org.kgrid.activator.adapter.AdapterGateway;
import org.kgrid.activator.adapter.api.Adapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JavascriptAdapterGateway extends AdapterGateway {


  public static void main(String[] args) {
    SpringApplication.run(JavascriptAdapterGateway.class, args);

    System.out.println("Starting JavascriptAdapterGateway");

  }

  @Override
  @Bean
  public Adapter getAdapter() {

    return new JavascriptAdapter();
  }

}
