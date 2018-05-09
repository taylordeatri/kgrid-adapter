package org.kgrid.activator.adapter.javascript;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.Collections;
import org.junit.Test;
import org.kgrid.shelf.repository.CompoundDigitalObjectStore;
import org.kgrid.shelf.repository.FilesystemCDOStore;

public class CompoundDigitalObjectStoreTest {

  @Test
  public void canLoadDigitalObject() throws Exception {
//    Path shelf = Files.createTempDirectory("testShelf");
//
//    CompoundDigitalObjectStore cdoStore = new FilesystemCDOStore(shelf.toAbsolutePath().toString());
//
//    String filename = "99999-fk45m6gq9t.zip";
//
//    URL zipStream = CompoundDigitalObjectStoreTest.class.getResource("/" + filename);
//    byte[] zippedKO = Files.readAllBytes(Paths.get(zipStream.toURI()));
//    MockMultipartFile koZip = new MockMultipartFile("ko", filename, "application/zip", zippedKO);
//
//    ObjectNode metadata = cdoStore.addCompoundObjectToShelf(koZip);


    // create a cdo store and set it as the adapter's cdo store
    CompoundDigitalObjectStore cdoStore = new FilesystemCDOStore(
        Paths.get(   this.getClass().getResource("/cdo-store").toURI()).toString() );


    assertTrue("Should find the 99999 fk45m6gq9t ko but found " + cdoStore.getChildren(null),
          cdoStore.getChildren(null).toString().contains( new StringBuffer("99999-fk45m6gq9t")));
  }
}
