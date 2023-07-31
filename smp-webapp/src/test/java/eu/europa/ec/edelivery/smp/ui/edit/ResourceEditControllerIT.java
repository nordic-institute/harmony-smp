package eu.europa.ec.edelivery.smp.ui.edit;

import eu.europa.ec.edelivery.smp.data.enums.VisibilityType;
import eu.europa.ec.edelivery.smp.data.ui.*;
import eu.europa.ec.edelivery.smp.services.ui.UIResourceSearchService;
import eu.europa.ec.edelivery.smp.services.ui.filters.ResourceFilter;
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
import java.util.UUID;

import static eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils.*;
import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * For the test configuration see the webapp_integration_test_data.sql file.
 * The system admin user is admin member of domain '1' and group '1'.
 */
public class ResourceEditControllerIT extends AbstractControllerTest {
    private static final String PATH = CONTEXT_PATH_EDIT_RESOURCE;

    @Autowired
    protected UIResourceSearchService uiResourceSearchService;

    @BeforeEach
    public void setup() throws IOException {
        super.setup();
    }

    // test must match the webapp_integration_test_data.sql file!
    @ParameterizedTest
    @CsvSource({
            ",'', 2",
            ",'group-admin', 2",
            ",'resource-admin', 1",
            "'','', 2",
            "ehealth-actorid-qns,'', 2", // filter by group match
            "'No match at all','', 0",
            "australia,'', 1", // filter by value match
    })
    public void testGetResourcesForGroup(String filter, String roleType, int expectedResults) throws Exception {
        // given when
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        List<DomainRO> domainsForUser = geUserDomainsForRole(mvc, session, userRO, null);
        assertEquals(1, domainsForUser.size());
        DomainRO domainRO = domainsForUser.get(0);
        List<GroupRO> groupsForUser = geUserGroups(mvc, session, userRO, domainRO, null);
        assertFalse(groupsForUser.isEmpty()); // set the webapp_integration_test_data.sql file
        GroupRO groupRO = groupsForUser.get(0);
        // when
        MvcResult result = mvc.perform(get(PATH, userRO.getUserId(), domainRO.getDomainId(), groupRO.getGroupId())
                        .session(session)
                        .param(PARAM_PAGINATION_FILTER, filter)
                        .param(PARAM_NAME_TYPE, roleType)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        //then
        ServiceResult serviceResult = getObjectFromResponse(result, ServiceResult.class);
        assertNotNull(serviceResult);
        assertEquals(expectedResults, serviceResult.getServiceEntities().size());

    }

    @Test
    public void testPutResource() throws Exception {
        // given
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        List<DomainRO> domainsForUser = geUserDomainsForRole(mvc, session, userRO, null);
        assertEquals(1, domainsForUser.size());
        DomainRO domainRO = domainsForUser.get(0);
        List<GroupRO> groupsForUser = geUserGroups(mvc, session, userRO, domainRO, null);
        assertFalse(groupsForUser.isEmpty()); // set the webapp_integration_test_data.sql file
        GroupRO groupRO = groupsForUser.get(0);


        ResourceRO resource = new ResourceRO();
        resource.setResourceTypeIdentifier("edelivery-oasis-smp-1.0-servicegroup");
        resource.setIdentifierValue(UUID.randomUUID().toString());
        resource.setIdentifierScheme("test-test-test");
        resource.setVisibility(VisibilityType.PUBLIC);

        int initialSize = getResourceCount();
        // when
        MvcResult result = mvc.perform(put(PATH + '/' + SUB_CONTEXT_PATH_EDIT_RESOURCE_CREATE, userRO.getUserId(), domainRO.getDomainId(), groupRO.getGroupId())
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsBytes(resource)))
                .andExpect(status().isOk()).andReturn();

        //then
        ResourceRO response = getObjectFromResponse(result, ResourceRO.class);
        assertNotNull(response);
        assertEquals(VisibilityType.PUBLIC, response.getVisibility());
        assertEquals(resource.getIdentifierValue(), response.getIdentifierValue());
        assertEquals(resource.getIdentifierScheme(), response.getIdentifierScheme());

        assertEquals(initialSize + 1, getResourceCount());
    }

    @Test
    public void testDeleteResource() throws Exception {
        // given
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        List<DomainRO> domainsForUser = geUserDomainsForRole(mvc, session, userRO, null);
        assertEquals(1, domainsForUser.size());
        DomainRO domainRO = domainsForUser.get(0);
        List<GroupRO> groupsForUser = geUserGroups(mvc, session, userRO, domainRO, null);
        GroupRO groupRO = groupsForUser.get(0);
        ResourceRO addedResource = addResourceToGroup(session, domainRO, groupRO, userRO);
        int initialSize = getResourceCount();

        // when
        MvcResult result = mvc.perform(delete(PATH + '/' + SUB_CONTEXT_PATH_EDIT_RESOURCE_DELETE, userRO.getUserId(), domainRO.getDomainId(), groupRO.getGroupId(), addedResource.getResourceId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();
        // then
        ResourceRO response = getObjectFromResponse(result, ResourceRO.class);
        assertNotNull(response);
        assertEquals(addedResource.getIdentifierValue(), response.getIdentifierValue());
        assertEquals(addedResource.getIdentifierScheme(), response.getIdentifierScheme());
        assertEquals(initialSize - 1, getResourceCount());
    }

    @Test
    public void testUpdateResource() throws Exception {
        // given
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        List<DomainRO> domainsForUser = geUserDomainsForRole(mvc, session, userRO, null);
        assertEquals(1, domainsForUser.size());
        DomainRO domainRO = domainsForUser.get(0);
        List<GroupRO> groupsForUser = geUserGroups(mvc, session, userRO, domainRO, null);
        GroupRO groupRO = groupsForUser.get(0);
        ResourceRO addedResource = addResourceToGroup(session, domainRO, groupRO, userRO);
        addedResource.setVisibility(VisibilityType.PRIVATE);

        // when
        MvcResult result = mvc.perform(post(PATH + '/' + SUB_CONTEXT_PATH_EDIT_RESOURCE_UPDATE, userRO.getUserId(), domainRO.getDomainId(),
                        groupRO.getGroupId(), addedResource.getResourceId())
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsBytes(addedResource)))
                .andExpect(status().isOk()).andReturn();
        // then
        ResourceRO response = getObjectFromResponse(result, ResourceRO.class);
        assertNotNull(response);
        assertEquals(addedResource.getVisibility(), response.getVisibility());
        assertEquals(addedResource.getIdentifierValue(), response.getIdentifierValue());
        assertEquals(addedResource.getIdentifierScheme(), response.getIdentifierScheme());
    }

    @Test
    public void testGetGroupMembers() throws Exception {
        // given
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        List<DomainRO> domainsForUser = geUserDomainsForRole(mvc, session, userRO, null);
        assertEquals(1, domainsForUser.size());
        DomainRO domainRO = domainsForUser.get(0);
        List<GroupRO> groupsForUser = geUserGroups(mvc, session, userRO, domainRO, null);
        GroupRO groupRO = groupsForUser.get(0);
        ResourceRO addedResource = addResourceToGroup(session, domainRO, groupRO, userRO);

        // when
        MvcResult result = mvc.perform(get(PATH + '/' + SUB_CONTEXT_PATH_EDIT_RESOURCE_MEMBER, userRO.getUserId(), domainRO.getDomainId(), groupRO.getGroupId(),
                        addedResource.getResourceId())
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
    public void testAddGroupMember() throws Exception {
        // given
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        List<DomainRO> domainsForUser = geUserDomainsForRole(mvc, session, userRO, null);
        assertEquals(1, domainsForUser.size());
        DomainRO domainRO = domainsForUser.get(0);
        List<GroupRO> groupsForUser = geUserGroups(mvc, session, userRO, domainRO, null);
        GroupRO groupRO = groupsForUser.get(0);
        ResourceRO addedResource = addResourceToGroup(session, domainRO, groupRO, userRO);

        //when
        MemberRO response = addResourceMember(session, domainRO, groupRO, addedResource, userRO, SG_USER_USERNAME);
        // then
        assertNotNull(response);
        assertEquals(SG_USER_USERNAME, response.getUsername());
    }

    @Test
    public void testDeleteGroupMember() throws Exception {
        // given
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        List<DomainRO> domainsForUser = geUserDomainsForRole(mvc, session, userRO, null);
        assertEquals(1, domainsForUser.size());
        DomainRO domainRO = domainsForUser.get(0);
        List<GroupRO> groupsForUser = geUserGroups(mvc, session, userRO, domainRO, null);
        GroupRO groupRO = groupsForUser.get(0);
        ResourceRO addedResource = addResourceToGroup(session, domainRO, groupRO, userRO);
        MemberRO member = addResourceMember(session, domainRO, groupRO, addedResource, userRO, SG_USER_USERNAME);
        //when
        MvcResult deleteGroupMemberResult = mvc.perform(delete(PATH + '/' + SUB_CONTEXT_PATH_EDIT_RESOURCE_MEMBER_DELETE,
                        userRO.getUserId(), domainRO.getDomainId(), groupRO.getGroupId(), addedResource.getResourceId(), member.getMemberId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        //then
        MemberRO response = getObjectFromResponse(deleteGroupMemberResult, MemberRO.class);
        assertNotNull(response);
        assertEquals(SG_USER_USERNAME, response.getUsername());
    }

    @Test
    public void testUpdateGroupMember() throws Exception {
        // given
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        List<DomainRO> domainsForUser = geUserDomainsForRole(mvc, session, userRO, null);
        assertEquals(1, domainsForUser.size());
        DomainRO domainRO = domainsForUser.get(0);
        List<GroupRO> groupsForUser = geUserGroups(mvc, session, userRO, domainRO, null);
        GroupRO groupRO = groupsForUser.get(0);
        ResourceRO addedResource = addResourceToGroup(session, domainRO, groupRO, userRO);
        MemberRO member = addResourceMember(session, domainRO, groupRO, addedResource, userRO, SG_USER_USERNAME);


        MvcResult updateGroupMemberResult = mvc.perform(put(PATH + '/' + SUB_CONTEXT_PATH_EDIT_RESOURCE_MEMBER_PUT,
                        userRO.getUserId(), domainRO.getDomainId(), groupRO.getGroupId(), addedResource.getResourceId(), member.getMemberId())
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsBytes(member)))
                .andExpect(status().isOk()).andReturn();

        //then
        MemberRO response = getObjectFromResponse(updateGroupMemberResult, MemberRO.class);
        assertNotNull(response);
        assertEquals(SG_USER_USERNAME, response.getUsername());
        assertEquals(member.getRoleType(), response.getRoleType());
    }


    public int getResourceCount() {
        return uiResourceSearchService.getTableList(-1, -1, null, null, new ResourceFilter()).getCount().intValue();
    }
}
