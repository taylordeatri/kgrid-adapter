package org.kgrid.adapter.proxy;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.kgrid.adapter.api.Executor;
import org.kgrid.shelf.repository.CompoundDigitalObjectStore;
import org.kgrid.shelf.repository.FilesystemCDOStore;
import org.springframework.http.MediaType;

/*
 NOTE THESE TESTS WILL FAIL IF YOU HAVE A SERVER RUNNING AT localhost:8888!!!!!!
 */
public class ProxyAdapterTest {

  private CompoundDigitalObjectStore cdoStore;

  @ClassRule
  public static WireMockClassRule wireMockRule = new WireMockClassRule(8888);

  @Rule
  public WireMockClassRule instanceRule = wireMockRule;

  @Before
  public void setUpCDOStore() throws URISyntaxException {
    cdoStore = new FilesystemCDOStore(
        Paths.get(this.getClass().getResource("/shelf").toURI()).toString());
  }

  @Test
  public void testSimpleObjectExecution() {

    String response = "Hello, Rob\n";
    mockProxyAdapter(response);

    ProxyAdapter proxyAdapter = new ProxyAdapter();
    proxyAdapter.setCdoStore(cdoStore);
    Path url = Paths.get("99999-newko/v0.0.1/model/http:/localhost:8888");
    Executor executor = proxyAdapter.activate(url,
        "helloworld");
    Map<String, Object> inputs = new HashMap<>();
    inputs.put("name", "Rob");
    String result = executor.execute(inputs).toString();

    assertEquals("Hello, Rob\n", result);
  }

  @Test
  public void testNoInputs() {

    String response = "Hello, World\n";
    mockProxyAdapter(response);

    ProxyAdapter proxyAdapter = new ProxyAdapter();
    proxyAdapter.setCdoStore(cdoStore);
    Path url = Paths.get("99999-newko/v0.0.1/model/http:/localhost:8888");
    Executor executor = proxyAdapter.activate(url,
        "helloworld");
    Map<String, Object> inputs = new HashMap<>();
    assertEquals("Hello, World\n", executor.execute(inputs));
  }

  // Mock jupyter notebook server response
  private void mockProxyAdapter(String response) {
    stubFor(post(urlEqualTo("/helloworld"))
        .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON_VALUE))
        .willReturn(aResponse().withStatus(200)
            .withBody(response)));
  }

}