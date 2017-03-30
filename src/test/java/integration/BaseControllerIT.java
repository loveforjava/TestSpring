package integration;

import integration.config.ApplicationConfigTest;
import integration.config.HibernateConfigTest;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationConfigTest.class, HibernateConfigTest.class})
@WebAppConfiguration
public abstract class BaseControllerIT {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;

    @Before
    public final void initialize() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
    }
}
