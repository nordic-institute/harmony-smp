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
package eu.europa.ec.edelivery.smp.ui.edit;

import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.ui.DomainRO;
import eu.europa.ec.edelivery.smp.data.ui.MemberRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.services.ui.UIGroupPublicService;
import eu.europa.ec.edelivery.smp.ui.AbstractControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.util.List;

import static eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils.*;
import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class DomainEditControllerIT extends AbstractControllerTest {
    private static final String PATH = CONTEXT_PATH_EDIT_DOMAIN;

    @Autowired
    protected UIGroupPublicService uiGroupPublicService;

    @BeforeEach
    public void setup() throws IOException {
        super.setup();
    }

    @ParameterizedTest
    @CsvSource({
            ", 1",
            "'', 1",
            "domain-admin, 1",
            "group-admin, 1",
            "resource-admin, 1",
    })
    public void testGetDomains(String roleType, int values) throws Exception {
        // given when
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        List<DomainRO> domainsForUser = geUserDomainsForRole(mvc, session, userRO, null);
        assertEquals(1, domainsForUser.size());
        DomainRO domainRO = domainsForUser.get(0);
        // when
        MvcResult result = mvc.perform(get(PATH, userRO.getUserId(), domainRO.getDomainId())
                        .session(session)
                        .param(PARAM_NAME_TYPE, roleType)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        //then
        List<DomainRO> listDomains = getArrayFromResponse(result, DomainRO.class);
        assertNotNull(listDomains);
        assertEquals(values, listDomains.size());
    }


    @Test
    public void testGetDomainMembers() throws Exception {
        // given
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        List<DomainRO> domainsForUser = geUserDomainsForRole(mvc, session, userRO, null);
        assertEquals(1, domainsForUser.size());
        DomainRO domainRO = domainsForUser.get(0);

        // when
        MvcResult result = mvc.perform(get(PATH + '/' + SUB_CONTEXT_PATH_EDIT_DOMAIN_MEMBER, userRO.getUserId(), domainRO.getDomainId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        //then
        ServiceResult serviceResult = getObjectFromResponse(result, ServiceResult.class);
        assertNotNull(serviceResult);
        assertEquals(1, serviceResult.getServiceEntities().size());
        MemberRO memberRO = getObjectMapper().convertValue(serviceResult.getServiceEntities().get(0), MemberRO.class);

        // the admin user who created group is automatically added as member
        assertEquals(userRO.getUsername(), memberRO.getUsername());
    }

    @Test
    public void testAddDomainMember() throws Exception {
        // given
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        List<DomainRO> domainsForUser = geUserDomainsForRole(mvc, session, userRO, null);
        assertEquals(1, domainsForUser.size());
        DomainRO domainRO = domainsForUser.get(0);

        //when
        MemberRO response = addDomainMember(session, domainRO, userRO, SG_USER_USERNAME);
        // then
        assertNotNull(response);
        assertEquals(SG_USER_USERNAME, response.getUsername());
    }

    @Test
    public void testDeleteDomainMember() throws Exception {
        // given
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        List<DomainRO> domainsForUser = geUserDomainsForRole(mvc, session, userRO, null);
        assertEquals(1, domainsForUser.size());
        DomainRO domainRO = domainsForUser.get(0);

        MemberRO member = addDomainMember(session, domainRO, userRO, SG_USER_USERNAME);

        MvcResult deleteGroupMemberResult = mvc.perform(delete(PATH + '/' + SUB_CONTEXT_PATH_EDIT_DOMAIN_MEMBER_DELETE, userRO.getUserId(), domainRO.getDomainId(), member.getMemberId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        //then
        MemberRO response = getObjectFromResponse(deleteGroupMemberResult, MemberRO.class);
        assertNotNull(response);
        assertEquals(SG_USER_USERNAME, response.getUsername());
    }

    @Test
    public void testUpdateDomainMember() throws Exception {
        // given
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        List<DomainRO> domainsForUser = geUserDomainsForRole(mvc, session, userRO, null);
        assertEquals(1, domainsForUser.size());
        DomainRO domainRO = domainsForUser.get(0);
        MemberRO member = addDomainMember(session, domainRO, userRO, SG_USER_USERNAME);
        assertEquals(MembershipRoleType.VIEWER, member.getRoleType());
        member.setRoleType(MembershipRoleType.ADMIN);


        MvcResult deleteGroupMemberResult = mvc.perform(put(PATH + '/' + SUB_CONTEXT_PATH_EDIT_DOMAIN_MEMBER_PUT, userRO.getUserId(), domainRO.getDomainId(), member.getMemberId())
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsBytes(member)))
                .andExpect(status().isOk()).andReturn();

        //then
        MemberRO response = getObjectFromResponse(deleteGroupMemberResult, MemberRO.class);
        assertNotNull(response);
        assertEquals(SG_USER_USERNAME, response.getUsername());
        assertEquals(member.getRoleType(), response.getRoleType());
    }
}
