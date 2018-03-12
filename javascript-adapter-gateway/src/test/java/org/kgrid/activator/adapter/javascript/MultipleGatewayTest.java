package org.kgrid.activator.adapter.javascript;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.kgrid.activator.adapter.api.Adapter;
import org.kgrid.activator.adapter.api.CompoundKnowledgeObject;
import org.kgrid.activator.adapter.api.Executor;
import org.kgrid.activator.adapter.api.Result;

public class MultipleGatewayTest {

  @Test
  public void activateTest()  {

    JavascriptAdapterGateway gateway = new JavascriptAdapterGateway();

    CompoundKnowledgeObject cko = gateway
        .getCompoundKnowledgeObject();

    Adapter adapter = gateway.getAdapter();

    adapter.initialize();


    cko.setEndpoint("question");
    Executor question = adapter.activate(cko);

    cko.setEndpoint("score");
    Executor score = adapter.activate(cko);

    Result result = score.execute(gateway.getAnswers());

    JsonNode r = new ObjectMapper().convertValue(result.getResult(), JsonNode.class);

    assertEquals("Score should be 10", "{\"score\":208.0}", r.toString());

    result = question.execute(null);

    r = new ObjectMapper().convertValue(result.getResult(), JsonNode.class);

    assertNotNull("Questions should be good ones", r.get("questions"));






  }

}