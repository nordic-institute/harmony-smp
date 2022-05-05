package eu.europa.ec.edelivery.smp.ui.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.ui.DeleteEntityValidation;
import eu.europa.ec.edelivery.smp.test.SmpTestWebAppConfig;
import eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import static eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils.loginWithSystemAdmin;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SmpTestWebAppConfig.class})
@Sql(scripts = {
        "classpath:/cleanup-database.sql",
        "classpath:/webapp_integration_test_data.sql"},
        executionPhase = BEFORE_TEST_METHOD)
public class DomainAdminResourceIntegrationTest {
    private static final String PATH = ResourceConstants.CONTEXT_PATH_INTERNAL_DOMAIN;

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    DomainDao domainDao;

    private MockMvc mvc;

    @Before
    public void setup() {
        mvc = MockMvcUtils.initializeMockMvc(webAppContext);
    }

    @Test
    public void updateDomainListOkDelete() throws Exception {
// given when
        assertEquals("CEF-SMP-002", domainDao.getDomainByCode("domainTwo").get().getSmlSmpId());
        MockHttpSession session = loginWithSystemAdmin(mvc);
        MvcResult result = mvc.perform(put(PATH)
                .session(session)
                .with(csrf())
                .header("Content-Type", " application/json")
                .content("[{\"status\":3,\"index\":9,\"id\":2,\"domainCode\":\"domainTwo\",\"smlSubdomain\":\"newdomain\",\"smlSmpId\":\"CEF-SMP-010\",\"smlParticipantIdentifierRegExp\":null,\"smlClientCertHeader\":null,\"smlClientKeyAlias\":null,\"signatureKeyAlias\":\"sig-key\",\"smlClientCertAuth\":true,\"smlRegistered\":false}]")) // delete domain with id 2
                .andExpect(status().isOk()).andReturn();

        // check if exists
        assertFalse(domainDao.getDomainByCode("domainTwo").isPresent());
    }


    @Test
    public void updateDomainListNotExists() throws Exception {
// given when
        MockHttpSession session = loginWithSystemAdmin(mvc);
        MvcResult result = mvc.perform(put(PATH)
                .session(session)
                .with(csrf())
                .header("Content-Type", " application/json")
                .content("[{\"status\":3,\"index\":9,\"id\":10,\"domainCode\":\"domainTwoNotExist\",\"smlSubdomain\":\"newdomain\",\"smlSmpId\":\"CEF-SMP-010\",\"smlParticipantIdentifierRegExp\":null,\"smlClientCertHeader\":null,\"smlClientKeyAlias\":null,\"signatureKeyAlias\":\"sig-key\",\"smlClientCertAuth\":true,\"smlRegistered\":false}]")) // delete domain with id 2
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    public void validateDeleteDomainOK() throws Exception {
        // given when
        MockHttpSession session = loginWithSystemAdmin(mvc);
        MvcResult result = mvc.perform(put(PATH + "/validate-delete")
                .session(session)
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
        MockHttpSession session = loginWithSystemAdmin(mvc);
        MvcResult result = mvc.perform(put(PATH)
                .session(session)
                .with(csrf())
                .header("Content-Type", " application/json")
                .content("[{\"status\":1,\"index\":9,\"id\":2,\"domainCode\":\"domainTwo\",\"smlSubdomain\":\"newdomain\",\"smlSmpId\":\"CEF-SMP-010\",\"smlParticipantIdentifierRegExp\":null,\"smlClientCertHeader\":null,\"smlClientKeyAlias\":null,\"signatureKeyAlias\":\"sig-key\",\"smlClientCertAuth\":true,\"smlRegistered\":false}]")) // delete domain with id 2
                .andExpect(status().isOk()).andReturn();

        // check if exists
        assertEquals("CEF-SMP-010", domainDao.getDomainByCode("domainTwo").get().getSmlSmpId());
    }

    @Test
    public void validateDeleteDomainFalse() throws Exception {
        // given when
        MockHttpSession session = loginWithSystemAdmin(mvc);
        MvcResult result = mvc.perform(put(PATH + "/validate-delete")
                .session(session)
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
        assertEquals("Could not delete domains used by Service groups! Domain: domain (domain ) uses by:2 SG.", res.getStringMessage());
    }

    @Test
    public void registerDomainAndParticipantsNotEnabled() throws Exception {
        // given when
        // 3- user id
        // domainTwo -  domain code
        MockHttpSession session = loginWithSystemAdmin(mvc);
        mvc.perform(put(PATH + "/3/sml-register/domainTwo")
                .session(session)
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
        MockHttpSession session = loginWithSystemAdmin(mvc);
        mvc.perform(put(PATH + "/3/sml-unregister/domainTwo")
                .session(session)
                .with(csrf())
                .header("Content-Type", " application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string(stringContainsInOrder("Configuration error: SML integration is not enabled!!")));
    }
}