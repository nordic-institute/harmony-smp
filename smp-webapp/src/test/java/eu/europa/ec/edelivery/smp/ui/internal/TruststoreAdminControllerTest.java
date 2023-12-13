/*-
 * #%L
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
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
 * #L%
 */
package eu.europa.ec.edelivery.smp.ui.internal;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import eu.europa.ec.edelivery.smp.services.ui.UITruststoreService;
import eu.europa.ec.edelivery.smp.test.testutils.X509CertificateTestUtils;
import eu.europa.ec.edelivery.smp.ui.AbstractControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.UUID;

import static eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils.*;
import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.CONTEXT_PATH_INTERNAL_TRUSTSTORE;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class TruststoreAdminControllerTest extends AbstractControllerTest {
    private static final String PATH = CONTEXT_PATH_INTERNAL_TRUSTSTORE;

    @Autowired
    private UITruststoreService uiTruststoreService;

    @BeforeEach
    public void setup() throws IOException {
        super.setup();
        uiTruststoreService.refreshData();
    }

    @Test
    public void testGetSystemTruststoreCertificates() throws Exception {
        // given when
        int countStart = uiTruststoreService.getCertificateROEntriesList().size();
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
    public void testUploadCertificateFailed() throws Exception {
        // given when
        // login
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        MvcResult result = mvc.perform(post(PATH + "/" + userRO.getUserId() + "/upload-certificate")
                        .session(session)
                        .with(csrf())
                        .content("Not Certificate")).
                andExpect(status().isOk()).andReturn();

        //then
        CertificateRO res = getObjectFromResponse(result, CertificateRO.class);

        assertNotNull(res);
        assertTrue(res.isError());

        assertEquals("Error occurred while parsing certificate. Is certificate valid!", res.getActionMessage());
    }

    @Test
    public void testUploadCertificateOK() throws Exception {

        X509Certificate cert = X509CertificateTestUtils.createX509CertificateForTest("123456", "cn=test,o=test,c=eu");
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        int countStart = uiTruststoreService.getCertificateROEntriesList().size();
        // given when
        MvcResult result = mvc.perform(post(PATH + "/" + userRO.getUserId() + "/upload-certificate")
                        .session(session)
                        .with(csrf())
                        .content(cert.getEncoded()))
                .andExpect(status().isOk()).andReturn();

        //then
        CertificateRO res = getObjectFromResponse(result, CertificateRO.class);

        assertNotNull(res);
        assertEquals(countStart + 1, uiTruststoreService.getCertificateROEntriesList().size());
    }

    @Test
    public void testDeleteCertificateFailed() throws Exception {

        String alias = UUID.randomUUID().toString();

        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        int countStart = uiTruststoreService.getCertificateROEntriesList().size();
        // given when
        MvcResult result = mvc.perform(delete(PATH + "/" + userRO.getUserId() + "/delete/" + alias)
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        //then
        CertificateRO res = getObjectFromResponse(result, CertificateRO.class);

        assertNotNull(res);
        assertTrue(res.isError());

        assertEquals("Certificate not removed because alias [" + alias + "] does not exist in truststore!", res.getActionMessage());
        assertEquals(countStart, uiTruststoreService.getCertificateROEntriesList().size());
    }

    @Test
    public void testDeleteCertificateOK() throws Exception {

        X509Certificate cert = X509CertificateTestUtils.createX509CertificateForTest("123456", "cn=test,o=test,c=eu");
        String alias = UUID.randomUUID().toString();
        uiTruststoreService.addCertificate(alias, cert);


        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        int countStart = uiTruststoreService.getCertificateROEntriesList().size();
        // given when
        MvcResult result = mvc.perform(delete(PATH + "/" + userRO.getUserId() + "/delete/" + alias)
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        //then
        CertificateRO res = getObjectFromResponse(result, CertificateRO.class);

        assertNotNull(res);
        assertEquals(EntityROStatus.REMOVE.getStatusNumber(), res.getStatus());
        assertEquals(countStart - 1, uiTruststoreService.getCertificateROEntriesList().size());
    }
}
