package org.kgrid.adapter.javascript;

import  static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.kgrid.adapter.api.Adapter;
import org.kgrid.adapter.api.AdapterException;
import org.kgrid.adapter.api.AdapterSupport;
import org.kgrid.adapter.api.Executor;
import org.kgrid.shelf.repository.CompoundDigitalObjectStore;
import org.kgrid.shelf.repository.FilesystemCDOStore;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JavascriptAdapterActivationTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private CompoundDigitalObjectStore cdoStore;
  private Adapter adapter;

  @Before
  public void setUpCDOStore() throws URISyntaxException {

    URI uri = this.getClass().getResource("/shelf").toURI();
//    Path path = Paths.get(this.getClass().getResource("/shelf").toURI().toString());
    cdoStore = new FilesystemCDOStore( "filesystem:" + uri.toString());

    adapter = new JavascriptAdapter();
    ( (AdapterSupport) adapter).setCdoStore(cdoStore);
    adapter.initialize(null);
  }

  @Test
  public void badScriptThrowsException() throws AdapterException {

    thrown.expect(AdapterException.class);
    String message = "unable to compile script " + Paths.get("simple-scripts", "badscript").toString() + ".js";
    thrown.expectMessage(message);

    adapter.activate(Paths.get("simple-scripts", "badscript.js"),"badscript");

  }

  @Test
  public void happyActivation(){
    Path path = Paths.get("simple-scripts", "doubler.js");
    Executor executor = adapter.activate(path, "doubler");
    assertEquals(6.0, executor.execute(3) );

    executor = adapter.activate(Paths.get("simple-scripts", "objectResult.js"), "objectResult");
    Map result = (Map) executor.execute(null);

    // treat a javascript object as a map
    assertEquals("1", result.get("something"));
    assertEquals(2, result.get("somethingelse"));
  }

  @Test
  public void activationWithMissingArtifactThrowsAdapterException() {
    thrown.expect(AdapterException.class);
    thrown.expectMessage("Binary resource not found");
    adapter.activate(Paths.get("a-b/c"), "missing.js");

  }
}