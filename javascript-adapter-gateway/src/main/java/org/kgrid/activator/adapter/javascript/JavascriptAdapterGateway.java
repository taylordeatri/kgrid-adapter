package org.kgrid.activator.adapter.javascript;

import com.fasterxml.jackson.databind.ObjectMapper;
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

  public static final String ANSWERS = "{\n"
      + "    \"answers\": [\n"
      + "        {\n"
      + "            \"id\": \"1\",\n"
      + "            \"scores\": [\n"
      + "                {\n"
      + "                    \"id\": \"1a\",\n"
      + "                    \"score\": 1\n"
      + "                },\n"
      + "                {\n"
      + "                    \"id\": \"1b\",\n"
      + "                  \"score\": 2\n"
      + "                },\n"
      + "                {\n"
      + "                    \"id\": \"1c\",\n"
      + "                    \"score\": 1\n"
      + "                },\n"
      + "                {\n"
      + "                    \"id\": \"1d\",\n"
      + "                  \"score\": 3\n"
      + "                },\n"
      + "                {\n"
      + "                  \"id\": \"1e\",\n"
      + "                  \"score\": 0\n"
      + "                },\n"
      + "                {\n"
      + "                  \"id\": \"1f\",\n"
      + "                  \"score\": 1\n"
      + "                },\n"
      + "                {\n"
      + "                  \"id\": \"1g\",\n"
      + "                  \"score\": 200\n"
      + "                }\n"
      + "            ]\n"
      + "        }\n"
      + "    ]\n"
      + "}\n";
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

    Map<String, Object> answers = getAnswers();

    CompoundKnowledgeObject gad7 = getCompoundKnowledgeObject();

    adapter.initialize();

    gad7.setEndpoint("score");

    Executor ex = adapter.activate(gad7);

    Result r = ex.execute(answers);

    return r;

  }

  HashMap getAnswers() {
    try {
      return new ObjectMapper().readValue(ANSWERS, HashMap.class);
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
//      System.out.println(path.toAbsolutePath());
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
