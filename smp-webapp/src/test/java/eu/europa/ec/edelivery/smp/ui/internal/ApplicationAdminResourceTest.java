package eu.europa.ec.edelivery.smp.ui.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.edelivery.smp.config.PropertiesTestConfig;
import eu.europa.ec.edelivery.smp.config.SmpAppConfig;
import eu.europa.ec.edelivery.smp.config.SmpWebAppConfig;
import eu.europa.ec.edelivery.smp.config.SpringSecurityConfig;
import eu.europa.ec.edelivery.smp.data.ui.SmpConfigRO;
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
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
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
public class ApplicationAdminResourceTest {
    private static final String PATH = ResourceConstants.CONTEXT_PATH_INTERNAL_APPLICATION;
    private static final RequestPostProcessor SMP_ADMIN_CREDENTIALS = httpBasic("smp_admin", "test123");
    private static final RequestPostProcessor SG_ADMIN_CREDENTIALS = httpBasic("sg_admin", "test123");
    private static final RequestPostProcessor SYSTEM_CREDENTIALS = httpBasic("sys_admin", "test123");

    @Autowired
    private WebApplicationContext webAppContext;
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
    public void testGetApplicationConfigNotAuthorized() throws Exception {
        // when
        mvc.perform(get(PATH + "/config")
                .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();
    }

    @Test
    public void testGetApplicationConfigAuthorized() throws Exception {
        //  SMP admin
        String val = mvc.perform(get(PATH + "/config")
                .with(SMP_ADMIN_CREDENTIALS)
                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertNotNull(val);
        //  service group
        val = mvc.perform(get(PATH + "/config").with(SG_ADMIN_CREDENTIALS)
                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertNotNull(val);
        // system admin
        val = mvc.perform(get(PATH + "/config")
                .with(SYSTEM_CREDENTIALS)
                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertNotNull(val);
    }

    @Test
    public void testGetApplicationConfigSMPAdmin() throws Exception {
        // when
        String value = mvc.perform(get(PATH + "/config")
                .with(SMP_ADMIN_CREDENTIALS)
                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        ObjectMapper mapper = new ObjectMapper();
        SmpConfigRO res = mapper.readValue(value, SmpConfigRO.class);

        assertNotNull(res);
        assertEquals("Participant scheme must start with:urn:oasis:names:tc:ebcore:partyid-type:(iso6523:|unregistered:) OR must be up to 25 characters long with form [domain]-[identifierArea]-[identifierType] (ex.: 'busdox-actorid-upis') and may only contain the following characters: [a-z0-9].", res.getParticipantSchemaRegExpMessage());
        assertEquals("^((?!^.{26})([a-z0-9]+-[a-z0-9]+-[a-z0-9]+)|urn:oasis:names:tc:ebcore:partyid-type:(iso6523|unregistered)(:.+)?$)", res.getParticipantSchemaRegExp());
        assertFalse(res.isSmlIntegrationOn());
        assertFalse(res.isSmlParticipantMultiDomainOn());
    }
}
