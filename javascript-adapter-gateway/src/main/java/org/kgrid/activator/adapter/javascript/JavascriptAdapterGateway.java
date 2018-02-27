package org.kgrid.activator.adapter.javascript;

import org.kgrid.activator.adapter.AdapterGateway;
import org.kgrid.activator.adapter.api.Adapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class JavascriptAdapterGateway extends AdapterGateway {

  @Autowired
  Adapter adapter;

  public static void main(String[] args) {
    SpringApplication.run(JavascriptAdapterGateway.class, args);

    System.out.println("Starting JavascriptAdapterGateway");

  }

  @Override
  @Bean
  public Adapter getAdapter() {

    return new JavascriptAdapter();
  }

  @GetMapping("/foo")
  public String getFoo() {

    System.out.println(adapter.getTypes());


    return "foo";
  }

}
