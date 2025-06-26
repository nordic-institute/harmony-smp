/*-
 * #START_LICENSE#
 * smp-server-library
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

import eu.europa.ec.edelivery.smp.data.enums.CredentialType;
import eu.europa.ec.edelivery.smp.data.model.DBPeriodicalAlert;
import eu.europa.ec.edelivery.smp.data.model.user.DBCredential;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


class CredentialDaoAlertsTest extends AbstractBaseDao {

    DBUser okUser = TestDBUtils.createDBUserByUsername("okUser-" + UUID.randomUUID());
    DBUser beforePasswordExpireNoAlertSend = TestDBUtils.createDBUserByUsername("befPassExpNoAlertSend-" + UUID.randomUUID());
    DBUser beforePasswordExpireRecentAlertSend = TestDBUtils.createDBUserByUsername("befPassExpRecentAlertSend-" + UUID.randomUUID());
    DBUser beforePasswordExpireAlertSend = TestDBUtils.createDBUserByUsername("befPassExpAlertSend-" + UUID.randomUUID());
    // set expired test cases
    DBUser passwordExpiredNoAlertSend = TestDBUtils.createDBUserByUsername("passwordExpiredNoAlertSend-" + UUID.randomUUID());
    DBUser passwordExpiredRecentAlertSend = TestDBUtils.createDBUserByUsername("passwordExpRecentAlertSend-" + UUID.randomUUID());
    DBUser passwordExpiredAlertSend = TestDBUtils.createDBUserByUsername("passwordExpiredAlertSend-" + UUID.randomUUID());
    // ------------
    // access token users  setup
    DBUser beforeATExpireNoAlertSend = TestDBUtils.createDBUserByUsername("befATExpireNoAlertSend-" + UUID.randomUUID());
    DBUser beforeATExpireRecentAlertSend = TestDBUtils.createDBUserByUsername("befATExpireRecentAlertSend-" + UUID.randomUUID());
    DBUser beforeATExpireAlertSend = TestDBUtils.createDBUserByUsername("befATExpireAlertSend-" + UUID.randomUUID());
    // set expired test cases
    DBUser aTExpiredNoAlertSend = TestDBUtils.createDBUserByUsername("ATExpiredNoAlertSend-" + UUID.randomUUID());
    DBUser aTExpiredRecentAlertSend = TestDBUtils.createDBUserByUsername("ATExpiredRecentAlertSend-" + UUID.randomUUID());
    DBUser aTExpiredAlertSend = TestDBUtils.createDBUserByUsername("ATExpiredAlertSend-" + UUID.randomUUID());

    // ------------
    // access token users  setup
    DBUser beforeCertExpireNoAlertSend = TestDBUtils.createDBUserByCertificate("befCertExpNoAlertSend-" + UUID.randomUUID());
    DBUser beforeCertExpireRecentAlertSend = TestDBUtils.createDBUserByCertificate("befATExpRecAlertSend-" + UUID.randomUUID());
    DBUser beforeCertExpireAlertSend = TestDBUtils.createDBUserByCertificate("befATExpireAlertSend-" + UUID.randomUUID());
    // set expired test cases
    DBUser certExpiredNoAlertSend = TestDBUtils.createDBUserByCertificate("ATExpiredNoAlertSend-" + UUID.randomUUID());
    DBUser certExpiredRecentAlertSend = TestDBUtils.createDBUserByCertificate("ATExpRecAlertSend-" + UUID.randomUUID());
    DBUser certExpiredAlertSend = TestDBUtils.createDBUserByCertificate("ATExpiredAlertSend-" + UUID.randomUUID());

    @Autowired
    CredentialDao testInstance;

    @Autowired
    PeriodicalAlertDao periodicalAlertDao;

    @Autowired
    UserDao userDao;

    @BeforeEach
    public void setupData() {
        // persist users to database
        userDao.persistFlushDetach(okUser);
        userDao.persistFlushDetach(beforePasswordExpireNoAlertSend);
        userDao.persistFlushDetach(beforePasswordExpireRecentAlertSend);
        userDao.persistFlushDetach(beforePasswordExpireAlertSend);
        userDao.persistFlushDetach(passwordExpiredNoAlertSend);
        userDao.persistFlushDetach(passwordExpiredRecentAlertSend);
        userDao.persistFlushDetach(passwordExpiredAlertSend);
        userDao.persistFlushDetach(beforeATExpireNoAlertSend);
        userDao.persistFlushDetach(beforeATExpireRecentAlertSend);
        userDao.persistFlushDetach(beforeATExpireAlertSend);
        userDao.persistFlushDetach(aTExpiredNoAlertSend);
        userDao.persistFlushDetach(aTExpiredRecentAlertSend);
        userDao.persistFlushDetach(aTExpiredAlertSend);
        userDao.persistFlushDetach(beforeCertExpireNoAlertSend);
        userDao.persistFlushDetach(beforeCertExpireRecentAlertSend);
        userDao.persistFlushDetach(beforeCertExpireAlertSend);
        userDao.persistFlushDetach(certExpiredNoAlertSend);
        userDao.persistFlushDetach(certExpiredRecentAlertSend);
        userDao.persistFlushDetach(certExpiredAlertSend);
        // configure user credentials for various issues

        OffsetDateTime twoDaysAgo = OffsetDateTime.now().minusDays(2);
        OffsetDateTime tenDaysAgo = OffsetDateTime.now().minusDays(10);

        // set user password credentials to database
        // reference OK User
        DBCredential credOkUser = TestDBUtils.createDBCredentialForUser(okUser, null, OffsetDateTime.now().plusDays(90));
        // test before password expires -  alerts  are send 30 days before they are expired and mail is send every 5 days
        // set users where
        // -- user credBeforePasswordExpireNoAlertSend  - password will expire in 20 days - alert must be sent.
        // -- user credBeforePasswordExpireRecentAlertSend - password will expire in 20 days - but alert was sent 2 days ago
        // -- user credBeforePasswordExpireAlertSend -  password will expire in 20 days and alert was sent 10 days ago -  alert must be sent again
        DBCredential credBeforePasswordExpireNoAlertSend = TestDBUtils.createDBCredentialForUser(beforePasswordExpireNoAlertSend, null,
                OffsetDateTime.now().plusDays(20));
        DBCredential credBeforePasswordExpireRecentAlertSend = TestDBUtils.createDBCredentialForUser(beforePasswordExpireRecentAlertSend, null,
                OffsetDateTime.now().plusDays(20));
        DBCredential credBeforePasswordExpireAlertSend = TestDBUtils.createDBCredentialForUser(beforePasswordExpireAlertSend, null,
                OffsetDateTime.now().plusDays(20));

        // -- user 1  - password expired 20 days ago alert must be sent.
        // -- user 2 - password  expired 20 ago - but alert was sent 2 days ago - no need to send it yet.
        // -- user 3 -  password  expired 20 ago and alert was sent 10 days ago -  alert must be sent again
        DBCredential credPasswordExpiredNoAlertSend = TestDBUtils.createDBCredentialForUser(passwordExpiredNoAlertSend, null,
                OffsetDateTime.now().minusDays(20));
        DBCredential credPasswordExpiredRecentAlertSend = TestDBUtils.createDBCredentialForUser(passwordExpiredRecentAlertSend, null,
                OffsetDateTime.now().minusDays(20));
        DBCredential credPasswordExpiredAlertSend = TestDBUtils.createDBCredentialForUser(passwordExpiredAlertSend, null,
                OffsetDateTime.now().minusDays(20));

      //-----------------------------------------
        // set before expired access-token testcases
        DBCredential credBeforeATExpireNoAlertSend = TestDBUtils.createDBCredentialForUserAccessToken(beforeATExpireNoAlertSend, null,
                OffsetDateTime.now().plusDays(20));
        DBCredential credBeforeATExpireRecentAlertSend = TestDBUtils.createDBCredentialForUserAccessToken(beforeATExpireRecentAlertSend, null,
                OffsetDateTime.now().plusDays(20));
        DBCredential credBeforeATExpireAlertSend = TestDBUtils.createDBCredentialForUserAccessToken(beforeATExpireAlertSend, null,
                OffsetDateTime.now().plusDays(20));
        DBCredential credATExpiredNoAlertSend = TestDBUtils.createDBCredentialForUserAccessToken(aTExpiredNoAlertSend, null,
                OffsetDateTime.now().minusDays(20));
        DBCredential credATExpiredRecentAlertSend = TestDBUtils.createDBCredentialForUserAccessToken(aTExpiredRecentAlertSend, null,
                OffsetDateTime.now().minusDays(20));
        DBCredential credATExpiredAlertSend = TestDBUtils.createDBCredentialForUserAccessToken(aTExpiredAlertSend, null,
                OffsetDateTime.now().minusDays(20));

        //-----------------------------------------
        // set before expired certificates testcases
        DBCredential credBeforeCertExpireNoAlertSend = TestDBUtils.createDBCredentialForUserCertificate(beforeCertExpireNoAlertSend, null,
                OffsetDateTime.now().plusDays(20));
        DBCredential credBeforeCertExpireRecentAlertSend = TestDBUtils.createDBCredentialForUserCertificate(beforeCertExpireRecentAlertSend, null,
                OffsetDateTime.now().plusDays(20));
        DBCredential credBeforeCertExpireAlertSend = TestDBUtils.createDBCredentialForUserCertificate(beforeCertExpireAlertSend, null,
                OffsetDateTime.now().plusDays(20));
        // set expired certificates testcases
        DBCredential credCertExpiredNoAlertSend = TestDBUtils.createDBCredentialForUserCertificate(certExpiredNoAlertSend, null,
                OffsetDateTime.now().minusDays(20));
        DBCredential credCertExpiredRecentAlertSend = TestDBUtils.createDBCredentialForUserCertificate(certExpiredRecentAlertSend, null,
                OffsetDateTime.now().minusDays(20));
        DBCredential credCertExpiredAlertSend = TestDBUtils.createDBCredentialForUserCertificate(certExpiredAlertSend, null,
                OffsetDateTime.now().minusDays(20));

        // persists
        testInstance.persistFlushDetach(credOkUser);
        testInstance.persistFlushDetach(credBeforePasswordExpireNoAlertSend);
        testInstance.persistFlushDetach(credBeforePasswordExpireRecentAlertSend);
        testInstance.persistFlushDetach(credBeforePasswordExpireAlertSend);
        testInstance.persistFlushDetach(credPasswordExpiredNoAlertSend);
        testInstance.persistFlushDetach(credPasswordExpiredRecentAlertSend);
        testInstance.persistFlushDetach(credPasswordExpiredAlertSend);

        DBPeriodicalAlert credBeforePasswordExpireRecentAlertSendAlert = TestDBUtils.createPeriodicalAlert(CredentialType.USERNAME_PASSWORD, credBeforePasswordExpireRecentAlertSend.getId().toString(), twoDaysAgo);
        DBPeriodicalAlert credBeforePasswordExpireAlertSendAlert = TestDBUtils.createPeriodicalAlert(CredentialType.USERNAME_PASSWORD, credBeforePasswordExpireAlertSend.getId().toString() ,tenDaysAgo);
        DBPeriodicalAlert credPasswordExpiredRecentAlertSendAlert = TestDBUtils.createPeriodicalAlert(CredentialType.USERNAME_PASSWORD, credPasswordExpiredRecentAlertSend.getId().toString(), twoDaysAgo);
        DBPeriodicalAlert credPasswordExpiredAlertSendAlert = TestDBUtils.createPeriodicalAlert(CredentialType.USERNAME_PASSWORD, credPasswordExpiredAlertSend.getId().toString() ,tenDaysAgo);
        periodicalAlertDao.persistFlushDetach(credBeforePasswordExpireRecentAlertSendAlert);
        periodicalAlertDao.persistFlushDetach(credBeforePasswordExpireAlertSendAlert);
        periodicalAlertDao.persistFlushDetach(credPasswordExpiredRecentAlertSendAlert);
        periodicalAlertDao.persistFlushDetach(credPasswordExpiredAlertSendAlert);


        // access token examples
        testInstance.persistFlushDetach(credBeforeATExpireNoAlertSend);
        testInstance.persistFlushDetach(credBeforeATExpireRecentAlertSend);
        testInstance.persistFlushDetach(credBeforeATExpireAlertSend);
        testInstance.persistFlushDetach(credATExpiredNoAlertSend);
        testInstance.persistFlushDetach(credATExpiredRecentAlertSend);
        testInstance.persistFlushDetach(credATExpiredAlertSend);

        DBPeriodicalAlert credBeforeATExpireRecentAlertSendAlert = TestDBUtils.createPeriodicalAlert(CredentialType.ACCESS_TOKEN, credBeforeATExpireRecentAlertSend.getId().toString(), twoDaysAgo);
        DBPeriodicalAlert credBeforeATExpireAlertSendAlert = TestDBUtils.createPeriodicalAlert(CredentialType.ACCESS_TOKEN, credBeforeATExpireAlertSend.getId().toString(), tenDaysAgo);
        DBPeriodicalAlert credATExpiredRecentAlertSendAlert = TestDBUtils.createPeriodicalAlert(CredentialType.ACCESS_TOKEN, credATExpiredRecentAlertSend.getId().toString(), twoDaysAgo);
        DBPeriodicalAlert credATExpiredAlertSendAlert = TestDBUtils.createPeriodicalAlert(CredentialType.ACCESS_TOKEN, credATExpiredAlertSend.getId().toString(), tenDaysAgo);
        periodicalAlertDao.persistFlushDetach(credBeforeATExpireRecentAlertSendAlert);
        periodicalAlertDao.persistFlushDetach(credBeforeATExpireAlertSendAlert);
        periodicalAlertDao.persistFlushDetach(credATExpiredRecentAlertSendAlert);
        periodicalAlertDao.persistFlushDetach(credATExpiredAlertSendAlert);

        // certificate examples
        testInstance.persistFlushDetach(credBeforeCertExpireNoAlertSend);
        testInstance.persistFlushDetach(credBeforeCertExpireRecentAlertSend);
        testInstance.persistFlushDetach(credBeforeCertExpireAlertSend);
        testInstance.persistFlushDetach(credCertExpiredNoAlertSend);
        testInstance.persistFlushDetach(credCertExpiredRecentAlertSend);
        testInstance.persistFlushDetach(credCertExpiredAlertSend);

        DBPeriodicalAlert credBeforeCertExpireRecentAlertSendAlert = TestDBUtils.createPeriodicalAlert(CredentialType.CERTIFICATE, credBeforeCertExpireRecentAlertSend.getId().toString(), twoDaysAgo);
        DBPeriodicalAlert credBeforeCertExpireAlertSendAlert = TestDBUtils.createPeriodicalAlert(CredentialType.CERTIFICATE, credBeforeCertExpireAlertSend.getId().toString(), tenDaysAgo);
        DBPeriodicalAlert credCertExpiredRecentAlertSendAlert = TestDBUtils.createPeriodicalAlert(CredentialType.CERTIFICATE, credCertExpiredRecentAlertSend.getId().toString(), twoDaysAgo);
        DBPeriodicalAlert credCertExpiredAlertSendAlert = TestDBUtils.createPeriodicalAlert(CredentialType.CERTIFICATE, credCertExpiredAlertSend.getId().toString(), tenDaysAgo);
        periodicalAlertDao.persistFlushDetach(credBeforeCertExpireRecentAlertSendAlert);
        periodicalAlertDao.persistFlushDetach(credBeforeCertExpireAlertSendAlert);
        periodicalAlertDao.persistFlushDetach(credCertExpiredRecentAlertSendAlert);
        periodicalAlertDao.persistFlushDetach(credCertExpiredAlertSendAlert);
    }

    @Test
    void getPasswordImminentExpireUsers() {
        List<DBCredential> dbUserList = testInstance.getBeforePasswordExpireUsersForAlerts(30, 5, 200);
        List<String> usernames = dbUserList.stream().map(DBCredential::getUser).map(DBUser::getUsername).toList();
        assertTrue(usernames.contains(beforePasswordExpireNoAlertSend.getUsername()));
        assertTrue(usernames.contains(beforePasswordExpireAlertSend.getUsername()));
    }

    @Test
    void getPasswordExpireUsers() {
        List<DBCredential> dbUserList = testInstance.getPasswordExpiredUsersForAlerts(30, 5, 200);
        assertEquals(2, dbUserList.size());
        List<String> usernames = dbUserList.stream().map(DBCredential::getUser).map(DBUser::getUsername).toList();
        assertTrue(usernames.contains(passwordExpiredNoAlertSend.getUsername()));
        assertTrue(usernames.contains(passwordExpiredAlertSend.getUsername()));
    }

    @Test
    void getAccessTokenImminentExpireUsers() {
        List<DBCredential> dbUserList = testInstance.getBeforeAccessTokenExpireUsersForAlerts(30, 5, 200);
        List<String> usernames = dbUserList.stream().map(DBCredential::getUser).map(DBUser::getUsername).toList();
        System.out.println(usernames);
        assertEquals(2, dbUserList.size());
        assertTrue(usernames.contains(beforeATExpireNoAlertSend.getUsername()));
        assertTrue(usernames.contains(beforeATExpireAlertSend.getUsername()));
    }

    @Test
    void getAccessTokenExpireUsers() {
        List<DBCredential> dbUserList = testInstance.getAccessTokenExpiredUsersForAlerts(30, 5, 200);
        List<String> usernames = dbUserList.stream().map(DBCredential::getUser).map(DBUser::getUsername).toList();
        System.out.println(usernames);
        assertEquals(2, dbUserList.size());
        assertTrue(usernames.contains(aTExpiredNoAlertSend.getUsername()));
        assertTrue(usernames.contains(aTExpiredAlertSend.getUsername()));
    }

    @Test
    void getCertificateImminentExpireUsers() {
        List<DBCredential> dbUserList = testInstance.getBeforeCertificateExpireUsersForAlerts(30, 5, 200);
        List<String> usernames = dbUserList.stream().map(DBCredential::getUser).map(DBUser::getUsername).toList();
        System.out.println(usernames);
        assertEquals(2, dbUserList.size());
        assertTrue(usernames.contains(beforeCertExpireNoAlertSend.getUsername()));
        assertTrue(usernames.contains(beforeCertExpireAlertSend.getUsername()));
    }

    @Test
    void getCertificateExpireUsers() {
        List<DBCredential> dbUserList = testInstance.getCertificateExpiredUsersForAlerts(30, 5, 200);
        List<String> usernames = dbUserList.stream().map(DBCredential::getUser).map(DBUser::getUsername).toList();
        System.out.println(usernames);
        assertEquals(2, dbUserList.size());
        assertTrue(usernames.contains(certExpiredNoAlertSend.getUsername()));
        assertTrue(usernames.contains(certExpiredAlertSend.getUsername()));
    }
}
