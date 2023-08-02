package eu.europa.ec.edelivery.smp.ui.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.data.ui.SmpConfigRO;
import eu.europa.ec.edelivery.smp.data.ui.SmpInfoRO;
import eu.europa.ec.edelivery.smp.test.SmpTestWebAppConfig;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import org.hamcrest.MatcherAssert;
import org.junit.*;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import static eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils.loginWithSystemAdmin;
import static eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils.loginWithUserGroupAdmin;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SmpTestWebAppConfig.class})
@Sql(scripts = {
        "classpath:/cleanup-database.sql",
        "classpath:/webapp_integration_test_data.sql"},
        executionPhase = BEFORE_TEST_METHOD)
@TestPropertySource(properties = {
        "smp.artifact.name=TestApplicationSmpName",
        "smp.artifact.version=TestApplicationVersion",
        "smp.artifact.build.time=2018-11-27 00:00:00",
})
@DirtiesContext
public class ApplicationResourceIntegrationTest {
    private static final String PATH = ResourceConstants.CONTEXT_PATH_PUBLIC_APPLICATION;

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private ApplicationController applicationResource;

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

        assertEquals("\"TestApplicationSmpName\"", value);
    }

    @Test
    public void getDisplayName() throws Exception {
        String value = applicationResource.getDisplayVersion();
        MatcherAssert.assertThat(value, startsWith("TestApplicationSmpName Version [TestApplicationVersion] Build-Time [2018-11-27 00:00:00"));
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

        MatcherAssert.assertThat(info.getVersion(), startsWith("TestApplicationSmpName Version [TestApplicationVersion] Build-Time [2018-11-27 00:00:00"));
        assertEquals("/", info.getContextPath());
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

        //  User
        MockHttpSession sessionUser = loginWithUserGroupAdmin(mvc);
        String val = mvc.perform(get(PATH + "/config")
                .session(sessionUser)
                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertNotNull(val);
        // system admin
        MockHttpSession sessionSystem = loginWithSystemAdmin(mvc);
        val = mvc.perform(get(PATH + "/config")
                .session(sessionSystem)
                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertNotNull(val);
    }

    @Test
    public void testGetApplicationConfigUser() throws Exception {
        // when
        MockHttpSession session = loginWithUserGroupAdmin(mvc);
        String value = mvc.perform(get(PATH + "/config")
                .session(session)
                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        ObjectMapper mapper = new ObjectMapper();
        SmpConfigRO res = mapper.readValue(value, SmpConfigRO.class);

        assertNotNull(res);
        assertEquals(SMPPropertyEnum.PARTC_SCH_REGEXP_MSG.getDefValue(), res.getParticipantSchemaRegExpMessage());
        assertEquals(SMPPropertyEnum.PARTC_SCH_VALIDATION_REGEXP.getDefValue(), res.getParticipantSchemaRegExp());
        assertEquals(SMPPropertyEnum.PARTC_EBCOREPARTYID_CONCATENATE.getDefValue(), res.isConcatEBCorePartyId() + "");
        assertFalse(res.isSmlIntegrationOn());
    }
}
