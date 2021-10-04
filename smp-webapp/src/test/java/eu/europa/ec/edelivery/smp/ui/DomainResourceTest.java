package eu.europa.ec.edelivery.smp.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.edelivery.smp.config.*;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.ui.DeleteEntityValidation;
import eu.europa.ec.edelivery.smp.data.ui.DomainRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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
@TestPropertySource(properties = {
        "smp.artifact.name=TestApplicationSmpName",
        "smp.artifact.version=TestApplicationVersion",
        "smp.artifact.build.time=2018-11-27 00:00:00",
        "bdmsl.integration.enabled=false"})
public class DomainResourceTest {
    private static final String PATH = "/ui/rest/domain";

    @Autowired
    private WebApplicationContext webAppContext;


    @Autowired
    DomainDao domainDao;

    private MockMvc mvc;
    private static final RequestPostProcessor SYSTEM_CREDENTIALS = httpBasic("sys_admin", "test123");

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
    public void geDomainList() throws Exception {

        // given when
        MvcResult result = mvc.perform(get(PATH)
                .with(SYSTEM_CREDENTIALS)
                .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        //them
        ObjectMapper mapper = new ObjectMapper();
        ServiceResult res = mapper.readValue(result.getResponse().getContentAsString(), ServiceResult.class);


        assertNotNull(res);
        assertEquals(2, res.getServiceEntities().size());
        res.getServiceEntities().forEach(sgMap -> {
            DomainRO sgro = mapper.convertValue(sgMap, DomainRO.class);
            assertNotNull(sgro.getId());
            assertNotNull(sgro.getDomainCode());
            assertNotNull(sgro.getSmlSmpId());
        });
    }

    @Test
    public void updateDomainListOkDelete() throws Exception {
// given when
        assertEquals("CEF-SMP-002", domainDao.getDomainByCode("domainTwo").get().getSmlSmpId());

        MvcResult result = mvc.perform(put(PATH )
                .with(SYSTEM_CREDENTIALS)
                .with(csrf())
                .header("Content-Type", " application/json")
                .content("[{\"status\":3,\"index\":9,\"id\":2,\"domainCode\":\"domainTwo\",\"smlSubdomain\":\"newdomain\",\"smlSmpId\":\"CEF-SMP-010\",\"smlParticipantIdentifierRegExp\":null,\"smlClientCertHeader\":null,\"smlClientKeyAlias\":null,\"signatureKeyAlias\":\"sig-key\",\"smlBlueCoatAuth\":true,\"smlRegistered\":false,\"deleted\":true}]")) // delete domain with id 2
                .andExpect(status().isOk()).andReturn();

        // check if exists
        assertFalse(domainDao.getDomainByCode("domainTwo").isPresent());
 }


    @Test
    public void updateDomainListNotExists() throws Exception {
// given when
        MvcResult result = mvc.perform(put(PATH )
                .with(SYSTEM_CREDENTIALS)
                .with(csrf())
                .header("Content-Type", " application/json")
                .content("[{\"status\":3,\"index\":9,\"id\":10,\"domainCode\":\"domainTwoNotExist\",\"smlSubdomain\":\"newdomain\",\"smlSmpId\":\"CEF-SMP-010\",\"smlParticipantIdentifierRegExp\":null,\"smlClientCertHeader\":null,\"smlClientKeyAlias\":null,\"signatureKeyAlias\":\"sig-key\",\"smlBlueCoatAuth\":true,\"smlRegistered\":false,\"deleted\":true}]")) // delete domain with id 2
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    public void validateDeleteDomainOK() throws Exception {
        // given when
        MvcResult result = mvc.perform(post(PATH + "/validateDelete")
                .with(SYSTEM_CREDENTIALS)
                .with(csrf())
                .header("Content-Type", " application/json")
                .content("[2]")) // delete domain with id 2
                .andExpect(status().isOk()).andReturn();

        //them
        ObjectMapper mapper = new ObjectMapper();
        DeleteEntityValidation res = mapper.readValue(result.getResponse().getContentAsString(), DeleteEntityValidation.class);

        assertNotNull(res);
        assertTrue(res.getListDeleteNotPermitedIds().isEmpty());
        assertEquals(1, res.getListIds().size());
        assertEquals(true, res.isValidOperation());
        assertNull(res.getStringMessage());
    }

    @Test
    public void updateDomainListOkUpdate() throws Exception {
// given when
        assertEquals("CEF-SMP-002", domainDao.getDomainByCode("domainTwo").get().getSmlSmpId());

        MvcResult result = mvc.perform(put(PATH )
                .with(SYSTEM_CREDENTIALS)
                .with(csrf())
                .header("Content-Type", " application/json")
                .content("[{\"status\":1,\"index\":9,\"id\":2,\"domainCode\":\"domainTwo\",\"smlSubdomain\":\"newdomain\",\"smlSmpId\":\"CEF-SMP-010\",\"smlParticipantIdentifierRegExp\":null,\"smlClientCertHeader\":null,\"smlClientKeyAlias\":null,\"signatureKeyAlias\":\"sig-key\",\"smlBlueCoatAuth\":true,\"smlRegistered\":false,\"deleted\":true}]")) // delete domain with id 2
                .andExpect(status().isOk()).andReturn();

        // check if exists
        assertEquals("CEF-SMP-010", domainDao.getDomainByCode("domainTwo").get().getSmlSmpId());

    }
    @Test
    public void validateDeleteDomainFalse() throws Exception {
        // given when
        MvcResult result = mvc.perform(post(PATH + "/validateDelete")
                .with(SYSTEM_CREDENTIALS)
                .with(csrf())
                .header("Content-Type", " application/json")
                .content("[1]")) // delete domain with id 2
                .andExpect(status().isOk()).andReturn();

        //them
        ObjectMapper mapper = new ObjectMapper();
        DeleteEntityValidation res = mapper.readValue(result.getResponse().getContentAsString(), DeleteEntityValidation.class);

        assertNotNull(res);
        assertEquals(1, res.getListDeleteNotPermitedIds().size());
        assertEquals(1, res.getListIds().size());
        assertEquals(false, res.isValidOperation());
        assertEquals("Could not delete domains used by Service groups! Domain: domain (domain ) uses by:2 SG.",res.getStringMessage());
    }

    @Test
    public void registerDomainAndParticipantsNotEnabled() throws Exception {
        // given when
        // 3- user id
        // domainTwo -  domain code
        mvc.perform(post(PATH + "/3/smlregister/domainTwo")
                .with(SYSTEM_CREDENTIALS)
                .with(csrf())
                .header("Content-Type", " application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string(stringContainsInOrder("Configuration error: SML integration is not enabled!!")));
    }

    @Test
    public void unregisterDomainAndParticipants() throws Exception {
        // given when
        // 3- user id
        // domainTwo -  domain code
        mvc.perform(post(PATH + "/3/smlunregister/domainTwo")
                .with(SYSTEM_CREDENTIALS)
                .with(csrf())
                .header("Content-Type", " application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string(stringContainsInOrder("Configuration error: SML integration is not enabled!!")));
    }
}