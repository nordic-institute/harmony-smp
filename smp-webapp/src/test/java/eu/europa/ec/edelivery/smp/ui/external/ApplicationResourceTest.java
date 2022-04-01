package eu.europa.ec.edelivery.smp.ui.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.edelivery.smp.config.PropertiesTestConfig;
import eu.europa.ec.edelivery.smp.config.SmpAppConfig;
import eu.europa.ec.edelivery.smp.config.SmpWebAppConfig;
import eu.europa.ec.edelivery.smp.config.SpringSecurityConfig;
import eu.europa.ec.edelivery.smp.data.ui.SmpInfoRO;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        PropertiesTestConfig.class,
        SmpAppConfig.class,
        SmpWebAppConfig.class,
        SpringSecurityConfig.class})
@WebAppConfiguration
@SqlConfig(encoding = "UTF-8")
@Sql(scripts = {"classpath:cleanup-database.sql",
        "classpath:webapp_integration_test_data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@TestPropertySource(properties = {
        "smp.artifact.name=TestApplicationSmpName",
        "smp.artifact.version=TestApplicationVersion",
        "smp.artifact.build.time=2018-11-27 00:00:00",
})
public class ApplicationResourceTest {
    private static final String PATH = ResourceConstants.CONTEXT_PATH_PUBLIC_APPLICATION;

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private ApplicationResource applicationResource;

    private MockMvc mvc;

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
    public void testGetName() throws Exception {
        String value = mvc.perform(get(PATH + "/name"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("TestApplicationSmpName", value);
    }

    @Test
    public void getDisplayName() throws Exception {
        String value = applicationResource.getDisplayVersion();
        assertEquals("TestApplicationSmpName Version [TestApplicationVersion] Build-Time [2018-11-27 00:00:00|Central European Time]", value);
    }

    @Test
    public void getApplicationInfoTest() throws Exception {
        String value = mvc.perform(get(PATH + "/info"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        SmpInfoRO info = mapper.readValue(value, SmpInfoRO.class);

        assertEquals("TestApplicationSmpName Version [TestApplicationVersion] Build-Time [2018-11-27 00:00:00|Central European Time]", info.getVersion());
        assertEquals(false, info.isSmlIntegrationOn());
        assertEquals("/", info.getContextPath());
    }
}
