/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.ui.internal;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.data.ui.KeystoreImportResult;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.services.ui.UIKeystoreService;
import eu.europa.ec.edelivery.smp.ui.AbstractControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils.*;
import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.CONTEXT_PATH_INTERNAL_KEYSTORE;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class KeystoreAdminControllerIT extends AbstractControllerTest {
    private static final String PATH = CONTEXT_PATH_INTERNAL_KEYSTORE;
    Path keystore = Paths.get("src", "test", "resources", "keystores", "smp-keystore.jks");

    @Autowired
    private UIKeystoreService uiKeystoreService;

    @BeforeEach
    public void setup() throws IOException {
        super.setup();
        uiKeystoreService.refreshData();
    }

    @Test
    void getKeyCertificateList() throws Exception {
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
        List<CertificateRO> listCerts = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<CertificateRO>>() {
        });

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
    void uploadKeystoreFailed() throws Exception {
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
    void uploadKeystoreInvalidPassword() throws Exception {
        // login
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = (UserRO) session.getAttribute(MOCK_LOGGED_USER);
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
    void uploadKeystoreOK_removeDuplicateCertificates() throws Exception {
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        assertEquals(uiKeystoreService.getKeystoreEntriesList().stream()
                .map(CertificateRO::getAlias)
                .collect(Collectors.toSet()), new HashSet<>(Arrays.asList("second_domain_alias", "single_domain_key")));

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
        assertTrue(res.getAddedCertificates().isEmpty());
        assertEquals(res.getIgnoredAliases(), new HashSet(Arrays.asList("single_domain_key")));
        assertEquals(uiKeystoreService.getKeystoreEntriesList().stream()
                .map(CertificateRO::getAlias)
                .collect(Collectors.toSet()), new HashSet<>(Arrays.asList("second_domain_alias", "single_domain_key")));
    }

    @Test
    void deleteCertificateOK() throws Exception {
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        String alias = "second_domain_alias";

        int countStart = uiKeystoreService.getKeystoreEntriesList().size();
        // given when
        MvcResult result = mvc.perform(delete(PATH + "/" + userRO.getUserId() + "/delete/" + alias)
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

    @Test
    void deleteCertificateNotExists() throws Exception {
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        String alias = "alias-not-exists";

        // given when
        MvcResult result = mvc.perform(delete(PATH + "/" + userRO.getUserId() + "/delete/"+ alias)
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        CertificateRO res = getObjectFromResponse(result, CertificateRO.class);
        assertEquals("Certificate Key not removed because alias ["+alias+"] does not exist in keystore!", res.getActionMessage());
    }
}
