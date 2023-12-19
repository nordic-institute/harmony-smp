/*-
 * #START_LICENSE#
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
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.europa.ec.edelivery.smp.data.dao.ConfigurationDao;
import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.enums.VisibilityType;
import eu.europa.ec.edelivery.smp.data.ui.*;
import eu.europa.ec.edelivery.smp.test.SmpTestWebAppConfig;
import eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils;
import eu.europa.ec.edelivery.smp.test.testutils.TestROUtils;
import eu.europa.ec.edelivery.smp.test.testutils.X509CertificateTestUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils.getObjectFromResponse;
import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.*;
import static java.lang.String.format;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SmpTestWebAppConfig.class})
@DirtiesContext
@Sql(scripts = {
        "classpath:/cleanup-database.sql",
        "classpath:/webapp_integration_test_data.sql"},
        executionPhase = BEFORE_TEST_METHOD)
abstract public class AbstractControllerTest {

    // the webapp_integration_test_data data
    public static final String IDENTIFIER_SCHEME = "ehealth-participantid-qns";
    public static final String DOCUMENT_SCHEME = "doctype";


    public static final String PARTICIPANT_ID = "urn:poland:ncpb";
    public static final String DOCUMENT_ID = "invoice";

    public static final RequestPostProcessor ADMIN_CREDENTIALS = httpBasic("pat_smp_admin", "123456");

    // Oasis SMP 1.0 URL paths
    public static final String URL_PATH = format("/%s::%s", IDENTIFIER_SCHEME, PARTICIPANT_ID);
    public static final String URL_DOC_PATH = format("%s/services/%s::%s", URL_PATH, DOCUMENT_SCHEME, DOCUMENT_ID);

    protected ObjectMapper mapper = null;
    protected MockMvc mvc;
    @Autowired
    private WebApplicationContext webAppContext;
    @Autowired
    private ConfigurationDao configurationDao;

    public void setup() throws IOException {
        X509CertificateTestUtils.reloadKeystores();
        mvc = MockMvcUtils.initializeMockMvc(webAppContext);
        configurationDao.reloadPropertiesFromDatabase();
    }

    /**
     * Helper method for getting the ObjectMapper.
     *
     * @return ObjectMapper
     */
    public ObjectMapper getObjectMapper() {
        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
        }
        return mapper;
    }

    /**
     * Test helper method for Adding a new member with viewer role to a domain.
     *
     * @param session           MockHttpSession of the user that is adding the new member
     * @param domainRO          Domain to which the new member is added
     * @param domainAdminUser   User that is adding the new member
     * @param newMemberUsername Username of the new member
     * @return MemberRO of the new member
     * @throws Exception
     */
    public MemberRO addDomainMember(MockHttpSession session, DomainRO domainRO, UserRO domainAdminUser, String newMemberUsername) throws Exception {
        String pathTemplate = CONTEXT_PATH_EDIT_DOMAIN + '/' + SUB_CONTEXT_PATH_EDIT_DOMAIN_MEMBER_PUT;
        return addMember(session, newMemberUsername, MembershipRoleType.VIEWER, pathTemplate,
                domainAdminUser.getUserId(), domainRO.getDomainId());
    }

    /**
     * Test helper method for Adding a new member with viewer role to a group.
     *
     * @param session           MockHttpSession of the user that is adding the new member
     * @param domainRO          Domain to which the new member is added
     * @param groupRO           Group to which the new member is added
     * @param domainAdminUser   User that is adding the new member
     * @param newMemberUsername Username of the new member
     * @return MemberRO of the new member
     * @throws Exception
     **/
    public MemberRO addGroupMember(MockHttpSession session, DomainRO domainRO, GroupRO groupRO, UserRO domainAdminUser, String newMemberUsername) throws Exception {
        String pathTemplate = CONTEXT_PATH_EDIT_GROUP + '/' + SUB_CONTEXT_PATH_EDIT_GROUP_MEMBER_PUT;
        return addMember(session, newMemberUsername, MembershipRoleType.VIEWER, pathTemplate,
                domainAdminUser.getUserId(), domainRO.getDomainId(), groupRO.getGroupId());
    }

    /**
     * Test helper method for Adding a new member with viewer role to a resourse.
     *
     * @param session           MockHttpSession of the user that is adding the new member
     * @param domainRO          Domain to which the new member is added
     * @param groupRO           Group to which the new member is added
     * @param resourceRO        Resource to which the new member is added
     * @param domainAdminUser   User that is adding the new member
     * @param newMemberUsername Username of the new member
     * @return MemberRO of the new member
     * @throws Exception
     **/
    public MemberRO addResourceMember(MockHttpSession session, DomainRO domainRO, GroupRO groupRO, ResourceRO resourceRO, UserRO domainAdminUser, String newMemberUsername) throws Exception {
        String pathTemplate = CONTEXT_PATH_EDIT_RESOURCE + '/' + SUB_CONTEXT_PATH_EDIT_RESOURCE_MEMBER_PUT;
        return addMember(session, newMemberUsername, MembershipRoleType.VIEWER, pathTemplate,
                domainAdminUser.getUserId(), domainRO.getDomainId(), groupRO.getGroupId(), resourceRO.getResourceId());
    }

    /**
     * Generic Test helper method for Adding a new member with given role to a resource/group/domain.
     *
     * @param session           MockHttpSession of the user that is adding the new member
     * @param newMemberUsername Username of the new member
     * @param roleType          Role of the new member
     * @param urlTemplate       URL template of the resource/group/domain to which the new member is added
     * @param encPathIds        Encoded path ids of the adminUserID, domain id, group id resource id, ...
     * @return MemberRO of the new member
     * @throws Exception
     */
    public MemberRO addMember(MockHttpSession session, String newMemberUsername, MembershipRoleType roleType, String urlTemplate, String... encPathIds) throws Exception {

        MemberRO memberToAdd = new MemberRO();
        memberToAdd.setRoleType(roleType);
        memberToAdd.setUsername(newMemberUsername);

        // when
        MvcResult result = mvc.perform(put(urlTemplate, encPathIds)
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsBytes(memberToAdd)))
                .andExpect(status().isOk()).andReturn();

        //then
        return getObjectFromResponse(result, MemberRO.class);
    }

    /**
     * Test helper method for creating and adding new resource to group.
     *
     * @param session         MockHttpSession of the user that is adding the new resource
     * @param domainRO        Domain to which the new member is added
     * @param groupRO         Group to which the new member is added
     * @param domainAdminUser User that is adding the new member
     * @return ResourceRO return created resource
     * @throws Exception
     **/
    public ResourceRO addResourceToGroup(MockHttpSession session, DomainRO domainRO, GroupRO groupRO, UserRO domainAdminUser) throws Exception {
        // create resource
        ResourceRO resource = new ResourceRO();
        resource.setResourceTypeIdentifier("edelivery-oasis-smp-1.0-servicegroup");
        resource.setIdentifierValue(UUID.randomUUID().toString());
        resource.setIdentifierScheme("test-test-test");
        resource.setVisibility(VisibilityType.PUBLIC);
        // add it to the group
        MvcResult result = mvc.perform(put(CONTEXT_PATH_EDIT_RESOURCE + '/' + SUB_CONTEXT_PATH_EDIT_RESOURCE_CREATE,
                        domainAdminUser.getUserId(), domainRO.getDomainId(), groupRO.getGroupId())
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsBytes(resource)))
                .andExpect(status().isOk()).andReturn();


        return getObjectFromResponse(result, ResourceRO.class);
    }

    /**
     * Test helper method for Adding a new group to a domain.
     *
     * @param session
     * @param domainRO
     * @param domainAdminUser
     * @return
     * @throws Exception
     */
    public GroupRO addGroupToDomain(MockHttpSession session, DomainRO domainRO, UserRO domainAdminUser) throws Exception {
        GroupRO groupRO = TestROUtils.createGroup();
        MvcResult addGroupResult = mvc.perform(put(CONTEXT_PATH_EDIT_GROUP + '/' + SUB_CONTEXT_PATH_EDIT_GROUP_CREATE,
                        domainAdminUser.getUserId(), domainRO.getDomainId())
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsBytes(groupRO)))
                .andExpect(status().isOk()).andReturn();
        return getObjectFromResponse(addGroupResult, GroupRO.class);
    }

    public List<ResourceRO> getEditResourcesForGroup(MockHttpSession session, UserRO userRO, DomainRO domainRO, GroupRO groupRO, String filter, String roleType)
            throws Exception {

        MvcResult result = mvc.perform(get(CONTEXT_PATH_EDIT_RESOURCE, userRO.getUserId(), domainRO.getDomainId(), groupRO.getGroupId())
                        .session(session)
                        .param(PARAM_PAGINATION_FILTER, filter)
                        .param(PARAM_NAME_TYPE, roleType)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        //then
        ServiceResult serviceResult = getObjectFromResponse(result, ServiceResult.class);
        if (serviceResult == null || serviceResult.getServiceEntities().isEmpty()) {
            return Collections.emptyList();
        }
        return (List<ResourceRO>) serviceResult.getServiceEntities().stream().map(o -> getObjectMapper().convertValue(o, ResourceRO.class))
                .collect(Collectors.toList());
    }
}
