package eu.europa.ec.edelivery.smp.ui.internal;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.data.ui.KeystoreImportResult;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.services.ui.UIKeystoreService;
import eu.europa.ec.edelivery.smp.ui.AbstractControllerTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils.*;
import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.CONTEXT_PATH_INTERNAL_KEYSTORE;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class KeystoreResourceIntegrationTest extends AbstractControllerTest {
    private static final String PATH = CONTEXT_PATH_INTERNAL_KEYSTORE;
    Path keystore = Paths.get("src", "test", "resources", "keystores", "smp-keystore.jks");

    @Autowired
    private UIKeystoreService uiKeystoreService;

    @Before
    public void setup() throws IOException {
        super.setup();
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
                        .content("Not keystore")).
                andExpect(status().isOk()).andReturn();

        //then
        KeystoreImportResult res = getObjectFromResponse(result, KeystoreImportResult.class);

        assertNotNull(res);
        assertEquals("java.io.IOException occurred while reading the keystore: Invalid keystore format", res.getErrorMessage());
    }

    @Test
    public void uploadKeystoreInvalidPassword() throws Exception {
        // login
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = (UserRO)session.getAttribute(MOCK_LOGGED_USER);
        // given when
        MvcResult result = mvc.perform(post(PATH + "/" + userRO.getUserId() + "/upload/JKS/NewPassword1234")
                        .session(session)
                        .with(csrf())
                        .content(Files.readAllBytes(keystore)))
                .andExpect(status().isOk()).andReturn();

        //then
        KeystoreImportResult res = getObjectFromResponse(result, KeystoreImportResult.class);
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
        KeystoreImportResult res = getObjectFromResponse(result, KeystoreImportResult.class);

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

        //then
        CertificateRO res = getObjectFromResponse(result, CertificateRO.class);

        assertNotNull(res);
        assertNull(res.getActionMessage());
        uiKeystoreService.refreshData();
        assertEquals(countStart - 1, uiKeystoreService.getKeystoreEntriesList().size());
    }


}
