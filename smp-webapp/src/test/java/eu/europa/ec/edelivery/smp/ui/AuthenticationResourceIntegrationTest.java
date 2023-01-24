package eu.europa.ec.edelivery.smp.ui;

import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.test.SmpTestWebAppConfig;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SmpTestWebAppConfig.class})
@Sql(scripts = {
        "classpath:/cleanup-database.sql",
        "classpath:/webapp_integration_test_data.sql"},
        executionPhase = BEFORE_TEST_METHOD)
public class AuthenticationResourceIntegrationTest {

    private static final String PATH = ResourceConstants.CONTEXT_PATH_PUBLIC_SECURITY + "/authentication";

    @Autowired
    private WebApplicationContext webAppContext;

    private MockMvc mvc;
    private static final RequestPostProcessor ADMIN_CREDENTIALS = httpBasic("smp_admin", "test123");

    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(webAppContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        initServletContext();
    }

    private void initServletContext() {
        MockServletContext sc = new MockServletContext("");
        ServletContextListener listener = new ContextLoaderListener(webAppContext);
        ServletContextEvent event = new ServletContextEvent(sc);
    }


    @Test
    public void authenticateSuccessTest() throws Exception {

        // given when
        HttpSession session = mvc.perform(post(PATH)
                .header("Content-Type", "application/json")
                .content("{\"username\":\"smp_admin\",\"password\":\"test123\"}"))
                .andExpect(status().isOk()).andReturn()
                .getRequest()
                .getSession();

        assertNotNull(session);
    }


    @Test
    public void authenticateInvalidPasswordTest() throws Exception {
        // given when then
        mvc.perform(post(PATH)
                .header("Content-Type", "application/json")
                .content("{\"username\":\"smp_admin\",\"password\":\"test1235\"}"))
                .andExpect(status().isUnauthorized()).andReturn()
                .getRequest()
                .getSession();
    }

    @Test
    public void authenticateInvalidUsernameTest() throws Exception {

        // given when
        mvc.perform(post(PATH)
                .header("Content-Type", "application/json")
                .content("{\"username\":\"smp_admin1\",\"password\":\"test123\"}"))
                .andExpect(status().isUnauthorized()).andReturn()
                .getRequest()
                .getSession();
    }
}