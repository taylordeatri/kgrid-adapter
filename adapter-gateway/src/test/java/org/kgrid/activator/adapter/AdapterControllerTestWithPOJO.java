package org.kgrid.activator.adapter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kgrid.activator.adapter.api.Adapter;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@WebMvcTest(AdapterController.class)
@ContextConfiguration(classes = {AdapterController.class})
public class AdapterControllerTestWithPOJO {

//	@Autowired
//	private MockMvc mockMvc;

  @MockBean // if using mock adapter
      Adapter adapter;

  @Test
  public void contextLoads() {
  }

}