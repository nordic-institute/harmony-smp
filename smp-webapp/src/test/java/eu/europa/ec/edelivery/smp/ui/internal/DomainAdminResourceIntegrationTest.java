package eu.europa.ec.edelivery.smp.ui.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.ui.DeleteEntityValidation;
import eu.europa.ec.edelivery.smp.data.ui.DomainRO;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import eu.europa.ec.edelivery.smp.test.SmpTestWebAppConfig;
import eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
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

import java.util.List;

import static eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils.*;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    public void testGetAllDomains() throws Exception {
        List<DBDomain> domain = domainDao.getAllDomains();
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = MockMvcUtils.getLoggedUserData(mvc, session);

        MvcResult result = mvc.perform(get(PATH + "/" + userRO.getUserId())
                        .session(session)
                        .with(csrf())
                        .header("Content-Type", " application/json"))
                .andExpect(status().isOk()).andReturn();

        List<DomainRO> response = parseResponseArray(result, DomainRO.class);
        assertEquals(domain.size(), response.size());
    }

    @Test
    public void testDeleteDomainOK() throws Exception {
        // given - delete domain two :)
        String domainCode = "domainTwo";
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = MockMvcUtils.getLoggedUserData(mvc, session);
        DomainRO domainToDelete = getDomain(domainCode, userRO, session);
        assertNotNull(domainToDelete);

        MvcResult result = mvc.perform(delete(PATH + "/" + userRO.getUserId() + "/" + domainToDelete.getDomainId() + "" + "/delete")
                        .session(session)
                        .with(csrf())
                        .header("Content-Type", " application/json")) // delete domain with id 2
                .andExpect(status().isOk()).andReturn();
        DomainRO resultObject = parseResponse(result, DomainRO.class);
        //
        assertNotNull(resultObject);
        assertEquals(domainCode, resultObject.getDomainCode());
        assertEquals(EntityROStatus.REMOVE.getStatusNumber(), resultObject.getStatus());
    }

    @Test
    public void updateDomainData() throws Exception {
        String domainCode = "domainTwo";
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = MockMvcUtils.getLoggedUserData(mvc, session);
        DomainRO domainToUpdate = getDomain(domainCode, userRO, session);
        domainToUpdate.setDomainCode("NewCode");
        domainToUpdate.setSignatureKeyAlias("New alias");

        MvcResult result = mvc.perform(post(PATH + "/" + userRO.getUserId() + "/" + domainToUpdate.getDomainId() + "" + "/update")
                        .session(session)
                        .with(csrf())
                        .header("Content-Type", " application/json")
                        .content(entitiToString(domainToUpdate)))
                .andExpect(status().isOk()).andReturn();
        DomainRO resultObject = parseResponse(result, DomainRO.class);
        //
        assertNotNull(resultObject);
        assertEquals(domainToUpdate.getDomainCode(), resultObject.getDomainCode());
        assertEquals(EntityROStatus.UPDATED.getStatusNumber(), resultObject.getStatus());
    }
    @Test
    public void updateDomainSmlIntegrationData() throws Exception {
        String domainCode = "domainTwo";
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = MockMvcUtils.getLoggedUserData(mvc, session);
        DomainRO domainToUpdate = getDomain(domainCode, userRO, session);
        domainToUpdate.setSmlSubdomain("NewCode");
        domainToUpdate.setSmlClientKeyAlias("New alias");

        MvcResult result = mvc.perform(post(PATH + "/" + userRO.getUserId() + "/" + domainToUpdate.getDomainId() + "" + "/update-sml-integration-data")
                        .session(session)
                        .with(csrf())
                        .header("Content-Type", " application/json")
                        .content(entitiToString(domainToUpdate)))
                .andExpect(status().isOk()).andReturn();
        DomainRO resultObject = parseResponse(result, DomainRO.class);
        //
        assertNotNull(resultObject);
        assertEquals(domainToUpdate.getDomainCode(), resultObject.getDomainCode());
        assertEquals(EntityROStatus.UPDATED.getStatusNumber(), resultObject.getStatus());
    }

    @Test
    @Ignore
    public void updateDomainDataAddNewResourceDef() throws Exception {
        // set the webapp_integration_test_data.sql for resourceDefID
        String resourceDefID = "edelivery-oasis-cppa";
        String domainCode = "domainTwo";
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = MockMvcUtils.getLoggedUserData(mvc, session);
        DomainRO domainToUpdate = getDomain(domainCode, userRO, session);
        domainToUpdate.getResourceDefinitions().add(resourceDefID);

        MvcResult result = mvc.perform(post(PATH + "/" + userRO.getUserId() + "/" + domainToUpdate.getDomainId() + "" + "/update-resource-types")
                        .session(session)
                        .with(csrf())
                        .header("Content-Type", " application/json")
                        .content(entitiToString(domainToUpdate.getResourceDefinitions())))
                .andExpect(status().isOk()).andReturn();
        DomainRO resultObject = parseResponse(result, DomainRO.class);
        //
        assertNotNull(resultObject);
        assertEquals(domainToUpdate.getDomainCode(), resultObject.getDomainCode());
        assertEquals(EntityROStatus.UPDATED.getStatusNumber(), resultObject.getStatus());
    }

/*
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
        assertEquals("Could not delete domains used by Service groups! Domain: domain (domain ) uses by:1 SG.", res.getStringMessage());
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
                .andExpect(content().string(stringContainsInOrder("Configuration error: [SML integration is not enabled!]!")));
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
*/

    private List<DomainRO> getAllDomains(UserRO userRO, MockHttpSession session) throws Exception {
        MvcResult result = mvc.perform(get(PATH + "/" + userRO.getUserId())
                        .session(session)
                        .with(csrf())
                        .header("Content-Type", " application/json"))
                .andExpect(status().isOk()).andReturn();
        return parseResponseArray(result, DomainRO.class);
    }

    private DomainRO getDomain(String domainCode, UserRO userRO, MockHttpSession session) throws Exception {
        List<DomainRO> allDomains = getAllDomains(userRO, session);

        return allDomains.stream()
                .filter(domainRO -> StringUtils.equals(domainCode, domainRO.getDomainCode()))
                .findFirst().orElse(null);

    }

    private String entitiToString(Object object ) throws Exception {
            return serializeObject(object);


    }

}
