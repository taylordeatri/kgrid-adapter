package org.kgrid.activator.adapter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

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

  @Test
  public void testStatusWithPOJO() throws Exception {

    AdapterController ac2 = new AdapterController(adapter);

    when(adapter.supports("whatever")).thenReturn(true);

    assertEquals("UP", ac2.status());

    when(adapter.supports("whatever")).thenReturn(false);

    assertEquals("DOWN", ac2.status());
  }

  @Test
  public void testStatusWithMVC() throws Exception {
    AdapterController ac2 = new AdapterController(adapter);

    when(adapter.supports("whatever")).thenReturn(true);

    assertEquals(true, adapter.supports("whatever"));

    assertEquals("UP", ac2.status());

    when(adapter.supports("whatever")).thenReturn(false);

    assertEquals(false, adapter.supports("whatever"));

    assertEquals("DOWN", ac2.status());
//        given(adapter.supports(anyString())).willReturn(true); // if using mock adapter

//		ResultActions response = mockMvc.perform(get("/status"))
//				.andExpect(status().isOk())
//				.andExpect(content().string("UP"))
//				;
  }


  enum Types {FOO, BAR;}

//    @TestConfiguration
//    public static class AdapterTestContextConfiguration {
//
//
//        @Bean
//        Adapter adapter() {
//            return new Adapter() {
//
//                @Override
//                public boolean supports(String typeName) {
//                    return true;
//                }
//
//                @Override
//                public EnumSet<Types> getTypes() {
//                    return EnumSet.allOf(Types.class);
//                }
//
//                @Override
//                public void initialize() {
//
//                }
//
//                @Override
//                public Executor activate(CompoundKnowledgeObject spec) {
//                    return null;
//                }
//            };
//        }
//    }

}