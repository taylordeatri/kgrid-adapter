package org.kgrid.activator.adapter.javascript;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.kgrid.activator.adapter.AdapterGateway;
import org.kgrid.activator.adapter.api.Adapter;
import org.kgrid.activator.adapter.api.CompoundKnowledgeObject;
import org.kgrid.activator.adapter.api.Executor;
import org.kgrid.activator.adapter.api.Result;
import org.kgrid.adapter.javascript.JavascriptAdapter;
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

  @GetMapping("/question")
  public Object getFoo() {

    CompoundKnowledgeObject gad7 = getCompoundKnowledgeObject();

    adapter.initialize();

    gad7.setEndpoint("question");

    Executor ex = adapter.activate(gad7);

    Result r = ex.execute(null);

    return r.getResult();
  }

  @GetMapping("/score")
  public Result getScore() throws IOException {

    JsonNode answers = getAnswers();

    CompoundKnowledgeObject gad7 = getCompoundKnowledgeObject();

    adapter.initialize();

    gad7.setEndpoint("score");

    Executor ex = adapter.activate(gad7);

    Result r = ex.execute(answers);

    return r;

  }

  JsonNode getAnswers() {
    try {
      File answersjson = new File("src/main/resources/answers.json");
      System.out.println(answersjson.getAbsolutePath());
      return new ObjectMapper().readTree(answersjson);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }



  CompoundKnowledgeObject getCompoundKnowledgeObject() {
    byte[] code = {'n'};
    try {

      final Path path = FileSystems
          .getDefault().getPath("shelf", "gad7", "v1", "models", "resource", "gad7.js");
      code = Files.readAllBytes(path);
    } catch (IOException e) {
      e.printStackTrace();
    }

    CompoundKnowledgeObject gad7 = new CompoundKnowledgeObject();

    gad7.setCode(new String(code, Charset.forName("UTF-8")));
    gad7.setEndpoint("status");
    gad7.setOutputType("foo");
    gad7.setInputType("bar");
    return gad7;
  }
}
