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
package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.user.DBResourceMember;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class ResourceMemberDaoTest extends AbstractBaseDao {

    @Autowired
    UserDao userDao;
    @Autowired
    ResourceDao resourceDao;
    @Autowired
    ResourceMemberDao testInstance;

    @BeforeEach
    public void prepareDatabase() {
        // setup initial data!
        testUtilsDao.clearData();
        testUtilsDao.createUsers();
        testUtilsDao.createResources();
    }

    @Test
    public void testIsUserDomainsMember() {
        DBUser user = testUtilsDao.getUser1();
        DBResource resource = testUtilsDao.getResourceD2G1RD1();

        DBResourceMember resourceMember = new DBResourceMember();

        resourceMember.setResource(resource);
        resourceMember.setUser(user);
        testInstance.persistFlushDetach(resourceMember);
        // then
        boolean result = testInstance.isUserResourceMember(user, resource);

        assertTrue(result);
    }

    @Test
    public void testIsUserDomainsMemberFalse() {
        // user is not member to resource D2G1RD1 by default
        DBUser user = testUtilsDao.getUser1();
        DBResource resource = testUtilsDao.getResourceD2G1RD1();
        // then
        boolean result = testInstance.isUserResourceMember(user, resource);

        assertFalse(result);
    }

    @Test
    public void testIsUserDomainsMemberWithRole() {
        DBUser user = testUtilsDao.getUser1();
        DBResource resource = testUtilsDao.getResourceD2G1RD1();
        DBResourceMember resourceMember = new DBResourceMember();

        resourceMember.setResource(resource);
        resourceMember.setUser(user);
        resourceMember.setRole(MembershipRoleType.ADMIN);
        testInstance.persistFlushDetach(resourceMember);
        // then
        boolean result = testInstance.isUserResourceMemberWithRole(user.getId(), resource.getId(), MembershipRoleType.ADMIN);
        assertTrue(result);
        result = testInstance.isUserResourceMemberWithRole(user.getId(), resource.getId(), MembershipRoleType.VIEWER);
        assertFalse(result);
    }


    @Test
    public void isUserAnyDomainResourceMember() {
        DBUser user = testUtilsDao.getUser1();
        DBResource resource = testUtilsDao.getResourceD2G1RD1();
        DBResourceMember resourceMember = new DBResourceMember();
        resourceMember.setResource(resource);
        resourceMember.setUser(user);
        resourceMember.setRole(MembershipRoleType.ADMIN);
        testInstance.persistFlushDetach(resourceMember);
        // when
        boolean result = testInstance.isUserAnyDomainResourceMember(user, testUtilsDao.getD2());
        // then
        assertTrue(result);
        // when
        result = testInstance.isUserAnyDomainResourceMember(user, testUtilsDao.getD1());
        // then
        assertFalse(result);

    }

    @Test
    public void isUserAnyDomainResourceMemberWithRole() {
        DBUser user = testUtilsDao.getUser1();
        DBResource resource = testUtilsDao.getResourceD2G1RD1();
        DBResourceMember resourceMember = new DBResourceMember();
        resourceMember.setResource(resource);
        resourceMember.setUser(user);
        resourceMember.setRole(MembershipRoleType.VIEWER);
        testInstance.persistFlushDetach(resourceMember);
        // when
        boolean result = testInstance.isUserAnyDomainResourceMemberWithRole(user, testUtilsDao.getD2(), MembershipRoleType.VIEWER);
        // then
        assertTrue(result);
        // when
        result = testInstance.isUserAnyDomainResourceMemberWithRole(user, testUtilsDao.getD2(), MembershipRoleType.ADMIN);
        // then
        assertFalse(result);

        result = testInstance.isUserAnyDomainResourceMemberWithRole(user, testUtilsDao.getD1(), MembershipRoleType.VIEWER);
        // then
        assertFalse(result);
    }

}
