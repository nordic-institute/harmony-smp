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
package eu.europa.ec.edelivery.smp.ui.edit;

import eu.europa.ec.edelivery.smp.data.ui.*;
import eu.europa.ec.edelivery.smp.data.ui.exceptions.ErrorResponseRO;
import eu.europa.ec.edelivery.smp.exceptions.ErrorBusinessCode;
import eu.europa.ec.edelivery.smp.services.ui.UIDocumentService;
import eu.europa.ec.edelivery.smp.test.testutils.TestROUtils;
import eu.europa.ec.edelivery.smp.ui.AbstractControllerTest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.util.List;

import static eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils.*;
import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DocumentEditControllerIT extends AbstractControllerTest {
    private static final String PATH = CONTEXT_PATH_EDIT_DOCUMENT;

    @Autowired
    protected UIDocumentService documentService;

    @BeforeEach
    public void setup() throws IOException {
        super.setup();
    }

    @Test
    void testGetDocumentForNewResource() throws Exception {
        // given when
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        List<DomainRO> domainsForUser = geUserDomainsForRole(mvc, session, userRO, null);
        assertEquals(1, domainsForUser.size());
        DomainRO domainRO = domainsForUser.get(0);
        List<GroupRO> groupsForUser = geUserGroups(mvc, session, userRO, domainRO, null);
        assertFalse(groupsForUser.isEmpty()); // set the webapp_integration_test_data.sql file
        GroupRO groupRO = groupsForUser.get(0);
        // add new resource
        ResourceRO resourceRO = addResourceToGroup(session, domainRO, groupRO, userRO);

        // when
        MvcResult result = mvc.perform(get(PATH + '/' + SUB_CONTEXT_PATH_EDIT_DOCUMENT_GET,
                        userRO.getUserId(), resourceRO.getResourceId())
                        .session(session)
                        .with(csrf())
                )
                .andExpect(status().isOk()).andReturn();
        // then
        DocumentRo documentRo = getObjectFromResponse(result, DocumentRo.class);
        assertNotNull(documentRo);
        assertTrue(documentRo.getAllVersions().isEmpty()); // was just created without document
    }

    @Test
    void testGetDocumentForResource() throws Exception {
        // given when
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        List<DomainRO> domainsForUser = geUserDomainsForRole(mvc, session, userRO, null);
        assertEquals(1, domainsForUser.size());
        DomainRO domainRO = domainsForUser.get(0);
        List<GroupRO> groupsForUser = geUserGroups(mvc, session, userRO, domainRO, null);
        assertFalse(groupsForUser.isEmpty()); // set the webapp_integration_test_data.sql file
        GroupRO groupRO = groupsForUser.get(0);

        List<ResourceRO> resources = getEditResourcesForGroup(session, userRO, domainRO, groupRO, RESOURCE_001_IDENTIFIER_VALUE, null);
        assertEquals(1, resources.size());
        ResourceRO resourceRO = resources.get(0);

        // when
        MvcResult result = mvc.perform(get(PATH + '/' + SUB_CONTEXT_PATH_EDIT_DOCUMENT_GET,
                        userRO.getUserId(), resourceRO.getResourceId())
                        .session(session)
                        .with(csrf())
                )
                .andExpect(status().isOk()).andReturn();
        // then
        DocumentRo documentRo = getObjectFromResponse(result, DocumentRo.class);
        assertNotNull(documentRo);
        assertFalse(documentRo.getAllVersions().isEmpty()); // was just created without document
        assertNotNull(documentRo.getPayload());
    }

    @Test
    void testValidateDocumentOk() throws Exception {
        // given when
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        List<DomainRO> domainsForUser = geUserDomainsForRole(mvc, session, userRO, null);
        assertEquals(1, domainsForUser.size());
        DomainRO domainRO = domainsForUser.get(0);
        List<GroupRO> groupsForUser = geUserGroups(mvc, session, userRO, domainRO, null);
        assertFalse(groupsForUser.isEmpty()); // set the webapp_integration_test_data.sql file
        GroupRO groupRO = groupsForUser.get(0);

        List<ResourceRO> resources = getEditResourcesForGroup(session, userRO, domainRO, groupRO, RESOURCE_001_IDENTIFIER_VALUE, null);
        assertEquals(1, resources.size());
        ResourceRO resourceRO = resources.get(0);

        // document to validate
        DocumentRo documentRo = new DocumentRo();
        documentRo.setPayload(TestROUtils.createSMP10ServiceGroupPayload(resourceRO.getIdentifierValue(), resourceRO.getIdentifierScheme()));

        // when
        mvc.perform(post(PATH + '/' + SUB_CONTEXT_PATH_EDIT_DOCUMENT_VALIDATE,
                        userRO.getUserId(), resourceRO.getResourceId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsBytes(documentRo))
                        .session(session)
                        .with(csrf())
                )
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    void testValidateDocumentInvalid() throws Exception {
        // given when
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        List<DomainRO> domainsForUser = geUserDomainsForRole(mvc, session, userRO, null);
        assertEquals(1, domainsForUser.size());
        DomainRO domainRO = domainsForUser.get(0);
        List<GroupRO> groupsForUser = geUserGroups(mvc, session, userRO, domainRO, null);
        assertFalse(groupsForUser.isEmpty()); // set the webapp_integration_test_data.sql file
        GroupRO groupRO = groupsForUser.get(0);

        List<ResourceRO> resources = getEditResourcesForGroup(session, userRO, domainRO, groupRO, RESOURCE_001_IDENTIFIER_VALUE, null);
        assertEquals(1, resources.size());
        ResourceRO resourceRO = resources.get(0);

        // document to validate
        DocumentRo documentRo = new DocumentRo();
        documentRo.setPayload("invalid payload");

        // when
        MvcResult result = mvc.perform(post(PATH + '/' + SUB_CONTEXT_PATH_EDIT_DOCUMENT_VALIDATE,
                        userRO.getUserId(), resourceRO.getResourceId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsBytes(documentRo))
                        .session(session)
                        .with(csrf())
                )
                .andExpect(status().is4xxClientError()).andReturn();

        ErrorResponseRO errorRO = getObjectFromResponse(result, ErrorResponseRO.class);
        assertNotNull(errorRO);
        assertEquals(ErrorBusinessCode.TECHNICAL.name(), errorRO.getBusinessCode());
        MatcherAssert.assertThat(errorRO.getErrorDescription(), Matchers.containsString("Invalid request [ResourceValidation]"));
    }

    @Test
    void testGenerateDocument() throws Exception {
        // given when
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        List<DomainRO> domainsForUser = geUserDomainsForRole(mvc, session, userRO, null);
        assertEquals(1, domainsForUser.size());
        DomainRO domainRO = domainsForUser.get(0);
        List<GroupRO> groupsForUser = geUserGroups(mvc, session, userRO, domainRO, null);
        assertFalse(groupsForUser.isEmpty()); // set the webapp_integration_test_data.sql file
        GroupRO groupRO = groupsForUser.get(0);

        List<ResourceRO> resources = getEditResourcesForGroup(session, userRO, domainRO, groupRO, RESOURCE_001_IDENTIFIER_VALUE, null);
        assertEquals(1, resources.size());
        ResourceRO resourceRO = resources.get(0);

        // when
        MvcResult response = mvc.perform(post(PATH + '/' + SUB_CONTEXT_PATH_EDIT_DOCUMENT_GENERATE,
                        userRO.getUserId(), resourceRO.getResourceId())
                        .session(session)
                        .with(csrf())
                )
                .andExpect(status().isOk()).andReturn();

        DocumentRo result = getObjectFromResponse(response, DocumentRo.class);
        assertNotNull(result);
        assertNotNull(result.getPayload());
    }
}
