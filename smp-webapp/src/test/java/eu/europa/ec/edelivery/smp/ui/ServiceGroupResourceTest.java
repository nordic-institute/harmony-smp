package eu.europa.ec.edelivery.smp.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.edelivery.smp.config.PropertiesTestConfig;
import eu.europa.ec.edelivery.smp.config.SmpAppConfig;
import eu.europa.ec.edelivery.smp.config.SmpWebAppConfig;
import eu.europa.ec.edelivery.smp.config.SpringSecurityConfig;
import eu.europa.ec.edelivery.smp.data.ui.ServiceGroupRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        PropertiesTestConfig.class,
        SmpAppConfig.class,
        SmpWebAppConfig.class,
        SpringSecurityConfig.class})
@WebAppConfiguration
@Sql("classpath:/cleanup-database.sql")
@Sql("classpath:/webapp_integration_test_data.sql")
@SqlConfig(encoding = "UTF-8")
public class ServiceGroupResourceTest {

    private static final String PATH="/ui/rest/servicegroup";

    private static final String PARTICIPANT_IDENTIFIER= "urn:australia:ncpb";
    private static final String PARTICIPANT_SCHEME= "ehealth-actorid-qns";

    @Autowired
    private WebApplicationContext webAppContext;

    private MockMvc mvc;
    private static final RequestPostProcessor ADMIN_CREDENTIALS = httpBasic("test_admin", "test123");
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
    public void getServiceGroupList() throws Exception {
        // given when
        MvcResult result = mvc.perform(get(PATH)
                .with(ADMIN_CREDENTIALS)
        ).andExpect(status().isOk()).andReturn();

        //them
        ObjectMapper mapper = new ObjectMapper();
        ServiceResult res = mapper.readValue(result.getResponse().getContentAsString(), ServiceResult.class);


        assertNotNull(res);
        assertEquals(2, res.getServiceEntities().size());
        res.getServiceEntities().forEach(sgMap-> {
            ServiceGroupRO  sgro = mapper.convertValue(sgMap, ServiceGroupRO.class);
            assertNotNull(sgro.getId());
            assertNotNull(sgro.getParticipantScheme());
            assertNotNull(sgro.getParticipantIdentifier());
            assertEquals(1, sgro.getUsers().size());
            assertEquals("test_user_hashed_pass", sgro.getUsers().get(0).getUsername());
        });
    }

    @Test
    public void getServiceGroupById() throws Exception{

        // given when
        MvcResult result = mvc.perform(get(PATH+"/100000")
                .with(ADMIN_CREDENTIALS)).
                andExpect(status().isOk()).andReturn();

        //them
        ObjectMapper mapper = new ObjectMapper();
        ServiceGroupRO res = mapper.readValue(result.getResponse().getContentAsString(), ServiceGroupRO.class);

        assertNotNull(res);
        assertEquals(100000, res.getId().intValue());
        assertEquals(PARTICIPANT_IDENTIFIER, res.getParticipantIdentifier());
        assertEquals(PARTICIPANT_SCHEME, res.getParticipantScheme());
        assertEquals(1, res.getUsers().size());
        assertEquals("test_user_hashed_pass", res.getUsers().get(0).getUsername());

        assertEquals(1, res.getServiceGroupDomains().size());
        assertEquals(1, res.getServiceMetadata().size());
        assertEquals("doc_7", res.getServiceMetadata().get(0).getDocumentIdentifier());
        assertEquals(res.getServiceGroupDomains().get(0).getId(), res.getServiceMetadata().get(0).getServiceGroupDomainId());
    }


}