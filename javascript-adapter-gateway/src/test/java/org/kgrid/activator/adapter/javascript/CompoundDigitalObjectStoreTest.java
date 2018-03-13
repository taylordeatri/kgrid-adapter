package org.kgrid.activator.adapter.javascript;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.umich.lhs.activator.repository.CompoundDigitalObjectStore;
import edu.umich.lhs.activator.repository.FilesystemCDOStore;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
public class CompoundDigitalObjectStoreTest {

  @Test
  public void canLoadDigitalObject() throws Exception {
    Path shelf = Files.createTempDirectory("testShelf");

    CompoundDigitalObjectStore cdoStore = new FilesystemCDOStore(shelf.toAbsolutePath().toString());

    String filename = "99999-fk45m6gq9t.zip";

    URL zipStream = CompoundDigitalObjectStoreTest.class.getResource("/" + filename);
    byte[] zippedKO = Files.readAllBytes(Paths.get(zipStream.toURI()));
    MockMultipartFile koZip = new MockMultipartFile("ko", filename, "application/zip", zippedKO);

    ObjectNode metadata = cdoStore.addCompoundObjectToShelf(koZip);

    assertEquals(Collections.singletonList("99999-fk45m6gq9t"), cdoStore.getChildren(null));
  }
}
