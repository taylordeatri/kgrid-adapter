package org.kgrid.adapter.javascript;

import static org.junit.Assert.assertEquals;
import static org.kgrid.adapter.javascript.utils.RepoUtils.getBinaryTestFile;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kgrid.adapter.api.ActivationContext;
import org.kgrid.adapter.api.Executor;
import org.kgrid.adapter.javascript.utils.MyMath;
import org.kgrid.shelf.repository.CompoundDigitalObjectStore;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JavascriptToJavaIntegrationTests {

  @Mock
  CompoundDigitalObjectStore cdoStore;

  private JavascriptAdapter adapter;
  private Map<String, Object> endpoints = new HashMap<>();

  @Before
  public void setUp() throws Exception {
    adapter = new JavascriptAdapter();
//    ((AdapterSupport) adapter).setCdoStore(cdoStore);
    adapter.setContext(new ActivationContext() {
      @Override
      public Executor getExecutor(String key) {
        return ((Endpoint) endpoints.get(key)).getExecutor();
      }

      @Override
      public byte[] getBinary(String pathToBinary) {
        return cdoStore.getBinary(pathToBinary);
      }

      @Override
      public String getProperty(String key) {
        return null;
      }
    });
    adapter.initialize(null);
  }

  @Test
  public void activateHappyPath() throws IOException {
    given(cdoStore.getBinary(eq("a-b/e")))
        .willReturn(getBinaryTestFile("a-b/e", "echo.js"));

    Executor executor = adapter.activate(Paths.get("a-b/e"), "echo");

    Object result = executor.execute(new HashMap<String, String>() {{
      put("name", "Bob");
    }});
    assertEquals("{name=Bob}", result.toString());
  }

  @Test
  public void canCallStandardJavaObject() throws IOException {
    given(cdoStore.getBinary(eq("a-b/c")))
        .willReturn(getBinaryTestFile("a-b/c", "math.js"));

    Executor executor = adapter.activate(Paths.get("a-b/c"), "math");

    int[] a = {1, 2};
    Map inputs = new HashMap<String, Object>() {{
      put("value1", 2);
      put("value2", a);
    }};

    Map result = (Map) executor.execute(inputs);

    assertEquals(MyMath.doubler(2), result.get("result1"));

    MyMath myMath = new MyMath();
    assertEquals(myMath.add(1, 2), result.get("result2"));
  }

  @Test
  public void canPassInstanceToScript() throws IOException {
    given(cdoStore.getBinary(eq("a-b/d")))
        .willReturn(getBinaryTestFile("a-b/d", "welcome.js"));

    given(cdoStore.getBinary(eq("a-b/c")))
        .willReturn(getBinaryTestFile("a-b/c", "math.js"));

//    Map<String, Object> endpoints = new HashMap<>();
//    adapter.setEndpoints(endpoints);

    final Executor welcome = adapter.activate(Paths.get("a-b/d"), "welcome");
    final Executor add = adapter.activate(Paths.get("a-b/c"), "math2");

    endpoints.put("a-b/d/welcome", new Endpoint(welcome));

    Map inputs = new HashMap<String, Object>() {{
      put("value1", 2);
      put("value2", new int[]{1, 2});
    }};

    Map result = (Map) add.execute(inputs);

    assertEquals(
        "Welcome to Knowledge Grid, Ted! Answer is: 3",
        result.get("execResult")
    );
  }

  // Dummy endpoint; loaded into the endpoints map and invoked by Javascript KO
  // as 'endpoints["a-b/c/welcome"].getExecutor().execute(inputs)'
  public class Endpoint {

    final Executor executor;

    public Endpoint(Executor executor) {
      this.executor = executor;
    }

    public Executor getExecutor() {
      return executor;
    }
  }
}