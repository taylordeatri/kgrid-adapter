package org.kgrid.activator.adapter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kgrid.activator.adapter.api.Adapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@RunWith(SpringRunner.class)
@WebMvcTest(AdapterController.class)
@ContextConfiguration(classes = {AdapterController.class})
public class AdapterControllerTest {

  @MockBean // if using mock adapter
      Adapter adapter;
  @Autowired
  private MockMvc mockMvc;

  @Test
  public void contextLoads() {
  }

  //    @Test
  public void testStatusWithPOJO() throws Exception {

    AdapterController ac2 = new AdapterController(adapter);

    when(adapter.supports("whatever")).thenReturn(false);

    assertEquals("UP", ac2.status());
  }

  //    @Test
  public void testStatusWithMVC() throws Exception {
    AdapterController ac2 = new AdapterController(adapter);

    assertEquals("UP", ac2.status());
//        given(adapter.supports(anyString())).willReturn(true); // if using mock adapter

    ResultActions response = mockMvc.perform(get("/status"))
        .andExpect(status().isOk())
        .andExpect(content().string("UP"));
  }

  enum Types {FOO, BAR;}

}