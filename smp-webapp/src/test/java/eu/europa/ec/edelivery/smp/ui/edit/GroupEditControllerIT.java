package eu.europa.ec.edelivery.smp.ui.edit;

import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.enums.VisibilityType;
import eu.europa.ec.edelivery.smp.data.ui.*;
import eu.europa.ec.edelivery.smp.services.ui.UIGroupPublicService;
import eu.europa.ec.edelivery.smp.test.testutils.TestROUtils;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * For the test configuration see the webapp_integration_test_data.sql file.
 * The system admin user is admin member of domain '1' and group '1'.
 */
public class GroupEditControllerIT extends AbstractControllerTest {
    private static final String PATH = CONTEXT_PATH_EDIT_GROUP;

    @Autowired
    protected UIGroupPublicService uiGroupPublicService;

    @BeforeEach
    public void setup() throws IOException {
        super.setup();
    }

    @ParameterizedTest
    @CsvSource({
            ", 2",
            "'', 2",
            "group-admin, 1",
            "resource-admin, 1",
            "group-viewer, 0",
            "all-roles, 1"
    })
    public void testGetGroup(String roleType, int values) throws Exception {
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
        List<GroupRO> listGroups = getArrayFromResponse(result, GroupRO.class);
        assertNotNull(listGroups);
        assertEquals(values, listGroups.size());
    }

    @Test
    public void testPutGroup() throws Exception {
        // given
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        List<DomainRO> domainsForUser = geUserDomainsForRole(mvc, session, userRO, null);
        assertEquals(1, domainsForUser.size());
        DomainRO domainRO = domainsForUser.get(0);
        GroupRO groupRO = new GroupRO();
        groupRO.setGroupName(UUID.randomUUID().toString());
        groupRO.setGroupDescription(UUID.randomUUID().toString());
        groupRO.setVisibility(VisibilityType.PRIVATE);

        int initialGroupSize = uiGroupPublicService.getAllGroupsForDomain(1L).size();
        // when
        MvcResult result = mvc.perform(put(PATH + '/' + SUB_CONTEXT_PATH_EDIT_GROUP_CREATE, userRO.getUserId(), domainRO.getDomainId())
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsBytes(groupRO)))
                .andExpect(status().isOk()).andReturn();

        //then
        GroupRO response = getObjectFromResponse(result, GroupRO.class);
        assertNotNull(response);
        assertEquals(VisibilityType.PRIVATE, response.getVisibility());
        assertEquals(groupRO.getGroupName(), response.getGroupName());
        assertEquals(groupRO.getGroupDescription(), response.getGroupDescription());
        assertEquals(initialGroupSize + 1, uiGroupPublicService.getAllGroupsForDomain(1L).size());
    }

    @Test
    public void testDeleteGroup() throws Exception {
        // given
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        List<DomainRO> domainsForUser = geUserDomainsForRole(mvc, session, userRO, null);
        assertEquals(1, domainsForUser.size());
        DomainRO domainRO = domainsForUser.get(0);
        GroupRO groupRO = addGroupToDomain(session, domainRO, userRO);

        int initialGroupSize = uiGroupPublicService.getAllGroupsForDomain(1L).size();

        // when
        MvcResult result = mvc.perform(delete(PATH + '/' + SUB_CONTEXT_PATH_EDIT_GROUP_DELETE, userRO.getUserId(), domainRO.getDomainId(), groupRO.getGroupId())
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();


        GroupRO response = getObjectFromResponse(result, GroupRO.class);
        assertNotNull(response);
        assertEquals(VisibilityType.PRIVATE, response.getVisibility());
        assertEquals(groupRO.getGroupName(), response.getGroupName());
        assertEquals(groupRO.getGroupDescription(), response.getGroupDescription());
        assertEquals(initialGroupSize - 1, uiGroupPublicService.getAllGroupsForDomain(1L).size());
    }

    @Test
    public void testUpdateGroup() throws Exception {
        // given
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        List<DomainRO> domainsForUser = geUserDomainsForRole(mvc, session, userRO, null);
        assertEquals(1, domainsForUser.size());
        DomainRO domainRO = domainsForUser.get(0);
        GroupRO groupRO = addGroupToDomain(session, domainRO, userRO);

        groupRO.setVisibility(VisibilityType.PUBLIC);
        groupRO.setGroupDescription(TestROUtils.anyString());
        groupRO.setGroupName(TestROUtils.anyString());

        // when
        MvcResult result = mvc.perform(post(PATH + '/' + SUB_CONTEXT_PATH_EDIT_GROUP_UPDATE, userRO.getUserId(), domainRO.getDomainId(), groupRO.getGroupId())
                        .session(session)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getObjectMapper().writeValueAsBytes(groupRO)))
                .andExpect(status().isOk()).andReturn();


        GroupRO response = getObjectFromResponse(result, GroupRO.class);
        assertNotNull(response);
        assertEquals(groupRO.getVisibility(), response.getVisibility());
        assertEquals(groupRO.getGroupName(), response.getGroupName());
        assertEquals(groupRO.getGroupDescription(), response.getGroupDescription());
    }


    @Test
    public void testGetGroupMembers() throws Exception {
        // given
        MockHttpSession session = loginWithSystemAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        List<DomainRO> domainsForUser = geUserDomainsForRole(mvc, session, userRO, null);
        assertEquals(1, domainsForUser.size());
        DomainRO domainRO = domainsForUser.get(0);
        GroupRO groupRO = addGroupToDomain(session, domainRO, userRO);

        // when
        MvcResult result = mvc.perform(get(PATH + '/' + SUB_CONTEXT_PATH_EDIT_GROUP_MEMBER, userRO.getUserId(), domainRO.getDomainId(), groupRO.getGroupId())
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
        GroupRO groupRO = addGroupToDomain(session, domainRO, userRO);

        //when
        MemberRO response = addGroupMember(session, domainRO, groupRO, userRO, SG_USER_USERNAME);
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
        GroupRO groupRO = addGroupToDomain(session, domainRO, userRO);
        MemberRO member = addGroupMember(session, domainRO, groupRO, userRO, SG_USER_USERNAME);

        MvcResult deleteGroupMemberResult = mvc.perform(delete(PATH + '/' + SUB_CONTEXT_PATH_EDIT_GROUP_MEMBER_DELETE, userRO.getUserId(), domainRO.getDomainId(), groupRO.getGroupId(), member.getMemberId())
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
        GroupRO groupRO = addGroupToDomain(session, domainRO, userRO);
        MemberRO member = addGroupMember(session, domainRO, groupRO, userRO, SG_USER_USERNAME);
        assertEquals(MembershipRoleType.VIEWER, member.getRoleType());
        member.setRoleType(MembershipRoleType.ADMIN);


        MvcResult deleteGroupMemberResult = mvc.perform(put(PATH + '/' + SUB_CONTEXT_PATH_EDIT_GROUP_MEMBER_PUT, userRO.getUserId(), domainRO.getDomainId(), groupRO.getGroupId(), member.getMemberId())
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
