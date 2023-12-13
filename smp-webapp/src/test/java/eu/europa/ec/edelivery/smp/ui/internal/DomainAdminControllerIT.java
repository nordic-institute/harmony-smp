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

import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.ui.DomainRO;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import eu.europa.ec.edelivery.smp.data.ui.exceptions.ErrorResponseRO;
import eu.europa.ec.edelivery.smp.exceptions.ErrorBusinessCode;
import eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils;
import eu.europa.ec.edelivery.smp.test.testutils.TestROUtils;
import eu.europa.ec.edelivery.smp.ui.AbstractControllerTest;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class DomainAdminControllerIT extends AbstractControllerTest {
    private static final String PATH = ResourceConstants.CONTEXT_PATH_INTERNAL_DOMAIN;

    @Autowired
    DomainDao domainDao;

    @BeforeEach
    public void setup() throws IOException {
        super.setup();
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
    public void testCreateBasicDomainData() throws Exception {
        DomainRO testDomain = TestROUtils.createDomain();

        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = MockMvcUtils.getLoggedUserData(mvc, session);

        MvcResult result = mvc.perform(put(PATH + "/" + userRO.getUserId() + "/create")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entitiToString(testDomain)))
                .andExpect(status().isOk()).andReturn();

        DomainRO resultObject = parseResponse(result, DomainRO.class);
        assertNotNull(resultObject);
        assertNotNull(resultObject.getDomainId());
        assertEquals(testDomain.getDomainCode(), resultObject.getDomainCode());
    }

    @Test
    public void testCreateDomainWithEmptyCode() throws Exception {
        DomainRO testDomain = TestROUtils.createDomain("");

        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = MockMvcUtils.getLoggedUserData(mvc, session);

        MvcResult result = mvc.perform(put(PATH + "/" + userRO.getUserId() + "/create")
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entitiToString(testDomain)))
                .andExpect(status().is4xxClientError()).andReturn();

        ErrorResponseRO errorRO = getObjectFromResponse(result, ErrorResponseRO.class);
        assertNotNull(errorRO);
        assertEquals(ErrorBusinessCode.INVALID_INPUT_DATA.name(), errorRO.getBusinessCode());
        MatcherAssert.assertThat(errorRO.getErrorDescription(), Matchers.containsString("Invalid domain data! Domain code must not be empty!"));
    }

    @Test
    public void testUpdateResourceDefDomainList() throws Exception {
        String domainCode = "domainTwo";
        String documentType = "edelivery-oasis-cppa";
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = (UserRO) session.getAttribute(MOCK_LOGGED_USER);

        DomainRO domainToUpdate = getDomain(domainCode, userRO, session);
        assertTrue(domainToUpdate.getResourceDefinitions().isEmpty());

        MvcResult result = mvc.perform(post(PATH + "/" + userRO.getUserId() + "/" + domainToUpdate.getDomainId() + "/update-resource-types")
                        .session(session)
                        .with(csrf())
                        .header("Content-Type", " application/json")
                        .content(entitiToString(Collections.singletonList(documentType))))
                .andExpect(status().isOk()).andReturn();
        DomainRO resultObject = parseResponse(result, DomainRO.class);
        //
        assertNotNull(resultObject);
        assertEquals(1, resultObject.getResourceDefinitions().size());
        assertEquals(documentType, resultObject.getResourceDefinitions().get(0));
    }

    @Test
    public void testDeleteDomainOK() throws Exception {
        // given - delete domain two :)
        String domainCode = "domainTwo";
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = MockMvcUtils.getLoggedUserData(mvc, session);
        DomainRO domainToDelete = getDomain(domainCode, userRO, session);
        assertNotNull(domainToDelete);

        MvcResult result = mvc.perform(delete(PATH + "/" + userRO.getUserId() + "/" + domainToDelete.getDomainId() + "/delete")
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

        MvcResult result = mvc.perform(post(PATH + "/" + userRO.getUserId() + "/" + domainToUpdate.getDomainId() +  "/update")
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
        UserRO userRO = (UserRO) session.getAttribute(MOCK_LOGGED_USER);

        DomainRO domainToUpdate = getDomain(domainCode, userRO, session);
        domainToUpdate.setSmlSubdomain("NewCode");
        domainToUpdate.setSmlClientKeyAlias("New alias");

        MvcResult result = mvc.perform(post(PATH + "/" + userRO.getUserId() + "/" + domainToUpdate.getDomainId() + "/update-sml-integration-data")
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
    public void updateDomainDataAddNewResourceDef() throws Exception {
        // set the webapp_integration_test_data.sql for resourceDefID
        String resourceDefID = "edelivery-oasis-cppa";
        String domainCode = "domainTwo";
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = MockMvcUtils.getLoggedUserData(mvc, session);
        DomainRO domainToUpdate = getDomain(domainCode, userRO, session);
        domainToUpdate.getResourceDefinitions().add(resourceDefID);

        MvcResult result = mvc.perform(post(PATH + "/" + userRO.getUserId() + "/" + domainToUpdate.getDomainId() + "/update-resource-types")
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

    private String entitiToString(Object object) throws Exception {
        return serializeObject(object);


    }

}
