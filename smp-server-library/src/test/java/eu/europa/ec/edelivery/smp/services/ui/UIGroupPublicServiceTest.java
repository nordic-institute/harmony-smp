/*-
 * #START_LICENSE#
 * smp-server-library
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
package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.dao.AbstractJunit5BaseDao;
import eu.europa.ec.edelivery.smp.data.dao.GroupMemberDao;
import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.enums.VisibilityType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBGroup;
import eu.europa.ec.edelivery.smp.data.model.user.DBGroupMember;
import eu.europa.ec.edelivery.smp.data.ui.GroupRO;
import eu.europa.ec.edelivery.smp.data.ui.MemberRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.testutil.TestROUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UIGroupPublicServiceTest extends AbstractJunit5BaseDao {


    @Autowired
    UIGroupPublicService testInstance;
    @Autowired
    ConversionService conversionService;
    @Autowired
    GroupMemberDao  groupMemberDao;

    @BeforeEach
    public void prepareDatabase() {
        testUtilsDao.clearData();
        testUtilsDao.createResourceDefinitionsForDomains();
        testUtilsDao.createGroupMemberships();
    }

    @Test
    public void testGetAllGroupsForDomainD1() {
        List<GroupRO> groups = testInstance.getAllGroupsForDomain(testUtilsDao.getD1().getId());
        assertEquals(2, groups.size());
    }


    @Test
    public void testGetAllGroupsForDomainD2() {
        List<GroupRO> groups = testInstance.getAllGroupsForDomain(testUtilsDao.getD2().getId());
        assertEquals(1, groups.size());
    }


    @Test
    public void testGetAllGroupsForDomainAndUserAndGroupRoleExpectedOneGroup() {
        List<GroupRO> groups = testInstance.getAllGroupsForDomainAndUserAndGroupRole(testUtilsDao.getD1().getId(),
                testUtilsDao.getUser1().getId(), MembershipRoleType.ADMIN);
        assertEquals(1, groups.size());
    }

    @Test
    public void testGetAllGroupsForDomainAndUserAndGroupRoleExpectedNullGroup() {
        List<GroupRO> groups = testInstance.getAllGroupsForDomainAndUserAndGroupRole(testUtilsDao.getD1().getId(),
                testUtilsDao.getUser1().getId(), MembershipRoleType.VIEWER);
        assertEquals(0, groups.size());
    }

    @Test
    public void testGetAllGroupsForDomainAndUserAndResourceRoleExpectedOneGroup() {
        List<GroupRO> groups = testInstance.getAllGroupsForDomainAndUserAndGroupRole(testUtilsDao.getD1().getId(),
                testUtilsDao.getUser1().getId(), MembershipRoleType.ADMIN);
        assertEquals(1, groups.size());
    }

    @Test
    public void testGetAllGroupsForDomainAndUserAndResourceRoleExpectedNullGroup() {
        List<GroupRO> groups = testInstance.getAllGroupsForDomainAndUserAndGroupRole(testUtilsDao.getD1().getId(),
                testUtilsDao.getUser1().getId(), MembershipRoleType.VIEWER);
        assertEquals(0, groups.size());
    }

    @Test
    public void testCreateGroupForDomain() {
        // given
        GroupRO groupRO = TestROUtils.createGroup(UUID.randomUUID().toString(), VisibilityType.PUBLIC);
        DBDomain domain = testUtilsDao.getD1();
        int iGroupCount = testInstance.getAllGroupsForDomain(domain.getId()).size();
        // when
        GroupRO createdGroup = testInstance.createGroupForDomain(groupRO, domain.getId(), testUtilsDao.getUser1().getId());
        // then
        assertNotNull(createdGroup);
        assertNotNull(createdGroup.getGroupId());
        assertEquals(groupRO.getGroupName(), createdGroup.getGroupName());
        assertEquals(groupRO.getVisibility(), createdGroup.getVisibility());
        assertEquals(iGroupCount + 1, testInstance.getAllGroupsForDomain(domain.getId()).size());
    }

    @Test
    public void testSaveGroupForDomain() {
        // given
        DBDomain domain = testUtilsDao.getD1();
        DBGroup group = testUtilsDao.getGroupD1G1();
        GroupRO groupRO = TestROUtils.createGroup(UUID.randomUUID().toString(), VisibilityType.PRIVATE);
        assertNotEquals(groupRO.getGroupName(), group.getGroupName());
        assertNotEquals(groupRO.getVisibility(), group.getVisibility());
        assertNotEquals(groupRO.getGroupDescription(), group.getGroupDescription());

        // when
        GroupRO createdGroup = testInstance.saveGroupForDomain(domain.getId(), group.getId(), groupRO);
        // then
        assertNotNull(createdGroup);
        assertNotNull(createdGroup.getGroupId());
        assertEquals(groupRO.getGroupName(), createdGroup.getGroupName());
        assertEquals(groupRO.getVisibility(), createdGroup.getVisibility());
        assertEquals(groupRO.getGroupDescription(), createdGroup.getGroupDescription());
    }

    @Test
    public void testDeleteGroupForDomain() {
        // given
        GroupRO groupRO = TestROUtils.createGroup(UUID.randomUUID().toString(), VisibilityType.PUBLIC);
        DBDomain domain = testUtilsDao.getD1();
        int iGroupCount = testInstance.getAllGroupsForDomain(domain.getId()).size();
        GroupRO createdGroup = testInstance.createGroupForDomain(groupRO, domain.getId(), testUtilsDao.getUser1().getId());
        assertEquals(iGroupCount + 1, testInstance.getAllGroupsForDomain(domain.getId()).size());

        // when
        testInstance.deleteGroupFromDomain(domain.getId(), Long.parseLong(createdGroup.getGroupId()));
        // then
        assertEquals(iGroupCount, testInstance.getAllGroupsForDomain(domain.getId()).size());
    }

    @Test
    public void testGetGroupMembers() {
        // given
        // see the data in testUtilsDao.createGroupMemberships()
        // when
        ServiceResult<MemberRO> members = testInstance.getGroupMembers(testUtilsDao.getGroupD1G1().getId(),testUtilsDao.getD1().getId(), -1, -1, null);
        // then
        assertNotNull(members);
        assertEquals(1, members.getCount().intValue());
        assertEquals(testUtilsDao.getUser1().getUsername(), members.getServiceEntities().get(0).getUsername());
    }



    @Test
    public void testAddUpdateMemberToGroupUpdate() {
        // given
        // see the data in testUtilsDao.createGroupMemberships()
        DBGroupMember dbMember = testUtilsDao.getGroupMemberU1D1G1Admin();
        MemberRO member = conversionService.convert(dbMember, MemberRO.class);
        member.setRoleType(MembershipRoleType.VIEWER);
        assertNotEquals(MembershipRoleType.VIEWER, dbMember.getRole());
        // when
        testInstance.addMemberToGroup(testUtilsDao.getGroupD1G1().getId(), testUtilsDao.getD1().getId(), member, dbMember.getId());
        // then
        DBGroupMember result = groupMemberDao.find(dbMember.getId());
        assertNotNull(result);
        assertEquals(MembershipRoleType.VIEWER, result.getRole());
    }

    @Test
    public void testAddUpdateMemberToGroupAdd() {
        // given
        ServiceResult<MemberRO> members = testInstance.getGroupMembers(testUtilsDao.getGroupD1G1().getId(),testUtilsDao.getD1().getId(), -1, -1, null);
        int memberCount = members.getCount().intValue();

        DBGroupMember dbMember = new DBGroupMember();
        dbMember.setRole(MembershipRoleType.VIEWER);
        dbMember.setUser(testUtilsDao.getUser2());
        dbMember.setGroup(testUtilsDao.getGroupD1G1());
        MemberRO member = conversionService.convert(dbMember, MemberRO.class);

        // when
        testInstance.addMemberToGroup(testUtilsDao.getGroupD1G1().getId(), testUtilsDao.getD1().getId(), member, null);
        // then
        ServiceResult<MemberRO> resourceMembers = testInstance.getGroupMembers(testUtilsDao.getGroupD1G1().getId(),testUtilsDao.getD1().getId(), -1, -1, null);
        assertEquals(memberCount + 1, resourceMembers.getCount().intValue());
    }

}
