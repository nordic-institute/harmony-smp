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
package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Joze Rihtarsic
 * @since 5.2
 */
class UserDaoTest extends AbstractBaseDao {

    @Autowired
    UserDao userDao;

    @BeforeEach
    public void prepareDatabase() {
        // setup initial data!
        testUtilsDao.clearData();
        // method creates user 1 which is registered  as admin on resource D1G1RD1
        // and as viewer on resource D2G1RD1 with review permissions
        testUtilsDao.createResourceMemberships();
    }

    @Test
    void testGetResourceAdminUUsersOk() {
        List<DBUser> result = userDao.getResourceAdminUsers(testUtilsDao.getResourceD1G1RD1());

        assertEquals(1, result.size());
        assertEquals(testUtilsDao.getUser1().getId(), result.get(0).getId());
    }

    @Test
    void testGetResourceAdminUUsersNone() {
        List<DBUser> result = userDao.getResourceAdminUsers(testUtilsDao.getResourceD2G1RD1());

        assertEquals(0, result.size());
    }

    @Test
    void testGetResourceReviewUsersWithUserRoleOk() {
        List<DBUser> result = userDao.getResourceReviewUsers(testUtilsDao.getResourceD2G1RD1());

        assertEquals(1, result.size());
        assertEquals(testUtilsDao.getUser1().getId(), result.get(0).getId());
    }

    @Test
    void testGetResourceReviewUsersWithAdminRoleAndNoReviewPermission() {
        List<DBUser> result = userDao.getResourceReviewUsers(testUtilsDao.getResourceD1G1RD1());

        assertEquals(0, result.size());
    }

    @Test
    void testGetResourceAdminOrReviewUsersAsuUserWithPermissionOk() {
        List<DBUser> result = userDao.getResourceAdminAndReviewUsers(testUtilsDao.getResourceD2G1RD1());

        assertEquals(1, result.size());
        assertEquals(testUtilsDao.getUser1().getId(), result.get(0).getId());
    }
    @Test
    void testGetResourceAdinOrReviewUsersAsuAdminOk() {
        List<DBUser> result = userDao.getResourceAdminAndReviewUsers(testUtilsDao.getResourceD1G1RD1());

        assertEquals(1, result.size());
        assertEquals(testUtilsDao.getUser1().getId(), result.get(0).getId());
    }
}