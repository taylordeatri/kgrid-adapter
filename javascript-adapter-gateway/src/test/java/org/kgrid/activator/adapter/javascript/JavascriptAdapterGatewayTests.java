package org.kgrid.activator.adapter.javascript;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class JavascriptAdapterGatewayTests {

//  @Test
  public void contextLoads() {
  }


  @Test
  public void testSingleton() {

    CDOStore store = CDOStore.INSTANCE;

    assertEquals("UP", store.getStatus());

    store.setStatus("DOWN");

    assertEquals("DOWN", CDOStore.INSTANCE.getStatus());

    assertEquals("muffin DOWN", CDOStore.INSTANCE.butter());

    assertEquals("toast UPish", CDOStore.OTHER_INSTANCE.butter());

    assertEquals(store, CDOStore.INSTANCE);

    assertNotEquals(store, CDOStore.OTHER_INSTANCE);

  }

  public interface Hackerly {

    default String butter() {
        return "muffin " + getStatus();
      }

    String getStatus();
  }

  public enum CDOStore implements Hackerly {
    INSTANCE,
    OTHER_INSTANCE {

      @Override
      public String butter() {
        return "toast " + getStatus();
      }

      @Override
      public String getStatus() {
        return super.getStatus() + "ish";
      }

    };

    // Each instance has its own status
     private String status = "UP";

    public String getStatus() {
      return status;
    }

    public void setStatus(String status) {
      this.status = status;
    }

  }
}
