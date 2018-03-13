package org.kgrid.activator.adapter.javascript;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import org.kgrid.activator.adapter.api.Adapter;
import org.kgrid.activator.adapter.api.CompoundKnowledgeObject;
import org.kgrid.activator.adapter.api.Executor;
import org.kgrid.activator.adapter.api.Result;

public class MultipleGatewayTest {

  @Test
  public void activateTest() {
    CompoundKnowledgeObject cko = getCompoundKnowledgeObject();

    Adapter adapter = initialize();

    cko.setEndpoint("question");
    Executor question = adapter.activate(cko);

    cko.setEndpoint("score");
    Executor score = adapter.activate(cko);

    JsonNode answers = getAnswers();

    Result result = score.execute(new ObjectMapper().convertValue(answers, HashMap.class));

    JsonNode r = new ObjectMapper().convertValue(result.getResult(), JsonNode.class);

    assertEquals("Score should be 10", "{\"score\":10.0}", r.toString());

    result = question.execute(null);

    r = new ObjectMapper().convertValue(result.getResult(), JsonNode.class);

    assertNotNull("Questions should be good ones", r.get("questions"));
    ((ObjectNode)(answers.get("answers").get(0).get("scores").get(0))).put("score", 0);
    result = score.execute(new ObjectMapper().convertValue(answers, HashMap.class));

    r = new ObjectMapper().convertValue(result.getResult(), JsonNode.class);
    assertEquals("Score should be 10", "{\"score\":9.0}", r.toString());

  }

  @Test
  public void reinitalizeAdapter() {

    CompoundKnowledgeObject cko = getCompoundKnowledgeObject();
    Adapter adapter = initialize();
    cko.setEndpoint("score");
    Executor score = adapter.activate(cko);

    Result result = score.execute(new ObjectMapper().convertValue(getAnswers(), HashMap.class));
    JsonNode r = new ObjectMapper().convertValue(result.getResult(), JsonNode.class);
    assertEquals("Score should be 10", "{\"score\":10.0}", r.toString());
    adapter.initialize();


    result = score.execute(new ObjectMapper().convertValue(getAnswers(), HashMap.class));
    r = new ObjectMapper().convertValue(result.getResult(), JsonNode.class);
    assertEquals("Score should be 10", "{\"score\":10.0}", r.toString());

  }

  private JsonNode getAnswers() {
    try {
      File answersjson = new File("src/test/resources/answers.json");
      return new ObjectMapper().readTree(answersjson);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private Adapter initialize() {
    JavascriptAdapterGateway gateway = new JavascriptAdapterGateway();

    Adapter adapter = gateway.getAdapter();

    adapter.initialize();

    return adapter;

  }

  CompoundKnowledgeObject getCompoundKnowledgeObject() {
    byte[] code = null;
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