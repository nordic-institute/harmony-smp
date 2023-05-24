package eu.europa.ec.edelivery.smp.ui.internal;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.europa.ec.edelivery.smp.data.dao.ConfigurationDao;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.data.ui.KeystoreImportResult;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.services.ui.UIKeystoreService;
import eu.europa.ec.edelivery.smp.test.SmpTestWebAppConfig;
import eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils;
import eu.europa.ec.edelivery.smp.test.testutils.X509CertificateTestUtils;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils.getLoggedUserData;
import static eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils.loginWithSystemAdmin;
import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.CONTEXT_PATH_INTERNAL_KEYSTORE;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SmpTestWebAppConfig.class})
@Sql(scripts = {
        "classpath:/cleanup-database.sql",
        "classpath:/webapp_integration_test_data.sql"})
public class KeystoreResourceIntegrationTest {
    private static final String PATH = CONTEXT_PATH_INTERNAL_KEYSTORE;
    Path keystore = Paths.get("src", "test", "resources", "keystores", "smp-keystore.jks");

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private UIKeystoreService uiKeystoreService;
    @Autowired
    private ConfigurationDao configurationDao;

    private MockMvc mvc;

    @Before
    public void setup() throws IOException {
        X509CertificateTestUtils.reloadKeystores();
        mvc = MockMvcUtils.initializeMockMvc(webAppContext);
        configurationDao.reloadPropertiesFromDatabase();
        uiKeystoreService.refreshData();
    }

    @Test
    public void getKeyCertificateList() throws Exception {
        // given when
        int countStart = uiKeystoreService.getKeystoreEntriesList().size();
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        MvcResult result = mvc.perform(get(PATH + "/" + userRO.getUserId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        //then
        ObjectMapper mapper = getObjectMapper();
        List<CertificateRO> listCerts = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<CertificateRO>>(){});

        assertNotNull(listCerts);
        assertEquals(countStart, listCerts.size());
        listCerts.forEach(sgMap -> {
            CertificateRO cert = mapper.convertValue(sgMap, CertificateRO.class);
            assertNotNull(cert.getAlias());
            assertNotNull(cert.getCertificateId());
            assertNotNull(cert.getClientCertHeader());
            assertNull(cert.getEncodedValue()); // submit only metadata
        });
    }

    @Test
    public void uploadKeystoreFailed() throws Exception {
        // given when
        // login
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        MvcResult result = mvc.perform(post(PATH + "/" + userRO.getUserId() + "/upload/JKS/test123")
                        .session(session)
                        .with(csrf())
                        .content("invalid keystore")).
                andExpect(status().isOk()).andReturn();

        //then
        ObjectMapper mapper = getObjectMapper();
        KeystoreImportResult res = mapper.readValue(result.getResponse().getContentAsString(), KeystoreImportResult.class);

        assertNotNull(res);
        assertEquals("java.io.IOException occurred while reading the keystore: Invalid keystore format", res.getErrorMessage());
    }

    @Test

    public void uploadKeystoreInvalidPassword() throws Exception {
        // login
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        // given when
        MvcResult result = mvc.perform(post(PATH + "/" + userRO.getUserId() + "/upload/JKS/NewPassword1234")
                        .session(session)
                        .with(csrf())
                        .content(Files.readAllBytes(keystore)))
                .andExpect(status().isOk()).andReturn();

        //them
        ObjectMapper mapper = getObjectMapper();
        KeystoreImportResult res = mapper.readValue(result.getResponse().getContentAsString(), KeystoreImportResult.class);

        assertNotNull(res);
        assertEquals("java.io.IOException occurred while reading the keystore: Keystore was tampered with, or password was incorrect", res.getErrorMessage());
    }

    @Test
    public void uploadKeystoreOK() throws Exception {

        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        int countStart = uiKeystoreService.getKeystoreEntriesList().size();
        // given when
        MvcResult result = mvc.perform(post(PATH + "/" + userRO.getUserId() + "/upload/JKS/test123")
                        .session(session)
                        .with(csrf())
                        .content(Files.readAllBytes(keystore)))
                .andExpect(status().isOk()).andReturn();

        //then
        ObjectMapper mapper = getObjectMapper();
        KeystoreImportResult res = mapper.readValue(result.getResponse().getContentAsString(), KeystoreImportResult.class);

        assertNotNull(res);
        assertNull(res.getErrorMessage());
        assertEquals(countStart + 1, uiKeystoreService.getKeystoreEntriesList().size());
    }

    @Test
    public void deleteKeystoreEntryOK() throws Exception {
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);


        int countStart = uiKeystoreService.getKeystoreEntriesList().size();
        // given when
        MvcResult result = mvc.perform(delete(PATH + "/" + userRO.getUserId() + "/delete/second_domain_alias")
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        //them
        ObjectMapper mapper = getObjectMapper();
        CertificateRO res = mapper.readValue(result.getResponse().getContentAsString(), CertificateRO.class);

        assertNotNull(res);
        assertNull(res.getActionMessage());
        uiKeystoreService.refreshData();
        assertEquals(countStart - 1, uiKeystoreService.getKeystoreEntriesList().size());
    }

    protected ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

}
