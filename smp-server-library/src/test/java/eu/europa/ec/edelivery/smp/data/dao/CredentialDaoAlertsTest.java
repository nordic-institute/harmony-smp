package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.user.DBCredential;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CredentialDaoAlertsTest extends AbstractBaseDao {

    DBUser okUser = TestDBUtils.createDBUserByUsername("okUser-" + UUID.randomUUID());
    DBUser beforePasswordExpireNoAlertSend = TestDBUtils.createDBUserByUsername("beforePasswordExpireNoAlertSend-" + UUID.randomUUID());
    DBUser beforePasswordExpireRecentAlertSend = TestDBUtils.createDBUserByUsername("beforePasswordExpireRecentAlertSend-" + UUID.randomUUID());
    DBUser beforePasswordExpireAlertSend = TestDBUtils.createDBUserByUsername("beforePasswordExpireAlertSend-" + UUID.randomUUID());
    // set expired test cases
    DBUser passwordExpiredNoAlertSend = TestDBUtils.createDBUserByUsername("passwordExpiredNoAlertSend-" + UUID.randomUUID());
    DBUser passwordExpiredRecentAlertSend = TestDBUtils.createDBUserByUsername("passwordExpiredRecentAlertSend-" + UUID.randomUUID());
    DBUser passwordExpiredAlertSend = TestDBUtils.createDBUserByUsername("passwordExpiredAlertSend-" + UUID.randomUUID());
    // ------------
    // access token users  setup
    DBUser beforeATExpireNoAlertSend = TestDBUtils.createDBUserByUsername("beforeATExpireNoAlertSend-" + UUID.randomUUID());
    DBUser beforeATExpireRecentAlertSend = TestDBUtils.createDBUserByUsername("beforeATExpireRecentAlertSend-" + UUID.randomUUID());
    DBUser beforeATExpireAlertSend = TestDBUtils.createDBUserByUsername("beforeATExpireAlertSend-" + UUID.randomUUID());
    // set expired test cases
    DBUser aTExpiredNoAlertSend = TestDBUtils.createDBUserByUsername("ATExpiredNoAlertSend-" + UUID.randomUUID());
    DBUser aTExpiredRecentAlertSend = TestDBUtils.createDBUserByUsername("ATExpiredRecentAlertSend-" + UUID.randomUUID());
    DBUser aTExpiredAlertSend = TestDBUtils.createDBUserByUsername("ATExpiredAlertSend-" + UUID.randomUUID());

    // ------------
    // access token users  setup
    DBUser beforeCertExpireNoAlertSend = TestDBUtils.createDBUserByCertificate("beforecertxpireNoAlertSend-" + UUID.randomUUID());
    DBUser beforeCertExpireRecentAlertSend = TestDBUtils.createDBUserByCertificate("beforeATExpireRecentAlertSend-" + UUID.randomUUID());
    DBUser beforeCertExpireAlertSend = TestDBUtils.createDBUserByCertificate("beforeATExpireAlertSend-" + UUID.randomUUID());
    // set expired test cases
    DBUser certExpiredNoAlertSend = TestDBUtils.createDBUserByCertificate("ATExpiredNoAlertSend-" + UUID.randomUUID());
    DBUser certExpiredRecentAlertSend = TestDBUtils.createDBUserByCertificate("ATExpiredRecentAlertSend-" + UUID.randomUUID());
    DBUser certExpiredAlertSend = TestDBUtils.createDBUserByCertificate("ATExpiredAlertSend-" + UUID.randomUUID());

    @Autowired
    CredentialDao testInstance;

    @Autowired
    UserDao userDao;

    @Before
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

        // set user password credentials to database
        // reference OK User
        DBCredential credOkUser = TestDBUtils.createDBCredentialForUser(okUser, null, OffsetDateTime.now().plusDays(90) , null);
        // test before password expires -  alerts  are send 30 days before they are expired and mail is send every 5 days
        // set users where
        // -- user credBeforePasswordExpireNoAlertSend  - password will expire in 20 days - alert must be sent.
        // -- user credBeforePasswordExpireRecentAlertSend - password will expire in 20 days - but alert was sent 2 days ago
        // -- user credBeforePasswordExpireAlertSend -  password will expire in 20 days and alert was sent 10 days ago -  alert must be sent again
        DBCredential credBeforePasswordExpireNoAlertSend = TestDBUtils.createDBCredentialForUser(beforePasswordExpireNoAlertSend, null,
                OffsetDateTime.now().plusDays(20), null);
        DBCredential credBeforePasswordExpireRecentAlertSend = TestDBUtils.createDBCredentialForUser(beforePasswordExpireRecentAlertSend, null,
                OffsetDateTime.now().plusDays(20), OffsetDateTime.now().minusDays(2));
        DBCredential credBeforePasswordExpireAlertSend = TestDBUtils.createDBCredentialForUser(beforePasswordExpireAlertSend, null,
                OffsetDateTime.now().plusDays(20),OffsetDateTime.now().minusDays(10));
        // -- user 1  - password expired 20 days aga alert must be sent.
        // -- user 2 - password  expired 20 ago  - but alert was sent 2 days ago - no need to send it yet.
        // -- user 3 -  password  expired 20 ago and alert was sent 10 days ago -  alert must be sent again
        DBCredential credPasswordExpiredNoAlertSend = TestDBUtils.createDBCredentialForUser(passwordExpiredNoAlertSend, null,
                OffsetDateTime.now().minusDays(20), null);
        DBCredential credPasswordExpiredRecentAlertSend = TestDBUtils.createDBCredentialForUser(passwordExpiredRecentAlertSend, null,
                OffsetDateTime.now().minusDays(20), OffsetDateTime.now().minusDays(2));
        DBCredential credPasswordExpiredAlertSend = TestDBUtils.createDBCredentialForUser(passwordExpiredAlertSend, null,
                OffsetDateTime.now().minusDays(20), OffsetDateTime.now().minusDays(10));

      //-----------------------------------------
        // set before expired access-token testcases
        DBCredential credBeforeATExpireNoAlertSend = TestDBUtils.createDBCredentialForUserAccessToken(beforeATExpireNoAlertSend, null,
                OffsetDateTime.now().plusDays(20), null);
        DBCredential credBeforeATExpireRecentAlertSend = TestDBUtils.createDBCredentialForUserAccessToken(beforeATExpireRecentAlertSend, null,
                OffsetDateTime.now().plusDays(20), OffsetDateTime.now().minusDays(2));
        DBCredential credBeforeATExpireAlertSend = TestDBUtils.createDBCredentialForUserAccessToken(beforeATExpireAlertSend, null,
                OffsetDateTime.now().plusDays(20),OffsetDateTime.now().minusDays(10));
        DBCredential credATExpiredNoAlertSend = TestDBUtils.createDBCredentialForUserAccessToken(aTExpiredNoAlertSend, null,
                OffsetDateTime.now().minusDays(20), null);
        DBCredential credATExpiredRecentAlertSend = TestDBUtils.createDBCredentialForUserAccessToken(aTExpiredRecentAlertSend, null,
                OffsetDateTime.now().minusDays(20), OffsetDateTime.now().minusDays(2));
        DBCredential credATExpiredAlertSend = TestDBUtils.createDBCredentialForUserAccessToken(aTExpiredAlertSend, null,
                OffsetDateTime.now().minusDays(20), OffsetDateTime.now().minusDays(10));

        //-----------------------------------------
        // set before expired certificates testcases
        DBCredential credBeforeCertExpireNoAlertSend = TestDBUtils.createDBCredentialForUserCertificate(beforeCertExpireNoAlertSend, null,
                OffsetDateTime.now().plusDays(20), null);
        DBCredential credBeforeCertExpireRecentAlertSend = TestDBUtils.createDBCredentialForUserCertificate(beforeCertExpireRecentAlertSend, null,
                OffsetDateTime.now().plusDays(20), OffsetDateTime.now().minusDays(2));
        DBCredential credBeforeCertExpireAlertSend = TestDBUtils.createDBCredentialForUserCertificate(beforeCertExpireAlertSend, null,
                OffsetDateTime.now().plusDays(20),OffsetDateTime.now().minusDays(10));
        // set expired certificates testcases
        DBCredential credCertExpiredNoAlertSend = TestDBUtils.createDBCredentialForUserCertificate(certExpiredNoAlertSend, null,
                OffsetDateTime.now().minusDays(20), null);
        DBCredential credCertExpiredRecentAlertSend = TestDBUtils.createDBCredentialForUserCertificate(certExpiredRecentAlertSend, null,
                OffsetDateTime.now().minusDays(20), OffsetDateTime.now().minusDays(2));
        DBCredential credCertExpiredAlertSend = TestDBUtils.createDBCredentialForUserCertificate(certExpiredAlertSend, null,
                OffsetDateTime.now().minusDays(20), OffsetDateTime.now().minusDays(10));

        // persists
        testInstance.persistFlushDetach(credOkUser);
        testInstance.persistFlushDetach(credBeforePasswordExpireNoAlertSend);
        testInstance.persistFlushDetach(credBeforePasswordExpireRecentAlertSend);
        testInstance.persistFlushDetach(credBeforePasswordExpireAlertSend);
        testInstance.persistFlushDetach(credPasswordExpiredNoAlertSend);
        testInstance.persistFlushDetach(credPasswordExpiredRecentAlertSend);
        testInstance.persistFlushDetach(credPasswordExpiredAlertSend);
        // access token examples
        testInstance.persistFlushDetach(credBeforeATExpireNoAlertSend);
        testInstance.persistFlushDetach(credBeforeATExpireRecentAlertSend);
        testInstance.persistFlushDetach(credBeforeATExpireAlertSend);
        testInstance.persistFlushDetach(credATExpiredNoAlertSend);
        testInstance.persistFlushDetach(credATExpiredRecentAlertSend);
        testInstance.persistFlushDetach(credATExpiredAlertSend);
        // certificate examples
        testInstance.persistFlushDetach(credBeforeCertExpireNoAlertSend);
        testInstance.persistFlushDetach(credBeforeCertExpireRecentAlertSend);
        testInstance.persistFlushDetach(credBeforeCertExpireAlertSend);
        testInstance.persistFlushDetach(credCertExpiredNoAlertSend);
        testInstance.persistFlushDetach(credCertExpiredRecentAlertSend);
        testInstance.persistFlushDetach(credCertExpiredAlertSend);
    }

    @Test
    public void getPasswordImminentExpireUsers() {
        List<DBCredential> dbUserList = testInstance.getBeforePasswordExpireUsersForAlerts(30, 5, 200);
        List<String> usernames = dbUserList.stream().map(DBCredential::getUser).map(DBUser::getUsername).collect(Collectors.toList());
        assertTrue(usernames.contains(beforePasswordExpireNoAlertSend.getUsername()));
        assertTrue(usernames.contains(beforePasswordExpireAlertSend.getUsername()));
    }

    @Test
    public void getPasswordExpireUsers() {
        List<DBCredential> dbUserList = testInstance.getPasswordExpiredUsersForAlerts(30, 5, 200);
        assertEquals(2, dbUserList.size());
        List<String> usernames = dbUserList.stream().map(DBCredential::getUser).map(DBUser::getUsername).collect(Collectors.toList());
        assertTrue(usernames.contains(passwordExpiredNoAlertSend.getUsername()));
        assertTrue(usernames.contains(passwordExpiredAlertSend.getUsername()));
    }

    @Test
    public void getAccessTokenImminentExpireUsers() {
        List<DBCredential> dbUserList = testInstance.getBeforeAccessTokenExpireUsersForAlerts(30, 5, 200);
        List<String> usernames = dbUserList.stream().map(DBCredential::getUser).map(DBUser::getUsername).collect(Collectors.toList());
        System.out.println(usernames);
        assertEquals(2, dbUserList.size());
        assertTrue(usernames.contains(beforeATExpireNoAlertSend.getUsername()));
        assertTrue(usernames.contains(beforeATExpireAlertSend.getUsername()));
    }

    @Test
    public void getAccessTokenExpireUsers() {
        List<DBCredential> dbUserList = testInstance.getAccessTokenExpiredUsersForAlerts(30, 5, 200);
        List<String> usernames = dbUserList.stream().map(DBCredential::getUser).map(DBUser::getUsername).collect(Collectors.toList());
        System.out.println(usernames);
        assertEquals(2, dbUserList.size());
        assertTrue(usernames.contains(aTExpiredNoAlertSend.getUsername()));
        assertTrue(usernames.contains(aTExpiredAlertSend.getUsername()));
    }

    @Test
    public void getCertificateImminentExpireUsers() {
        List<DBCredential> dbUserList = testInstance.getBeforeCertificateExpireUsersForAlerts(30, 5, 200);
        List<String> usernames = dbUserList.stream().map(DBCredential::getUser).map(DBUser::getUsername).collect(Collectors.toList());
        System.out.println(usernames);
        assertEquals(2, dbUserList.size());
        assertTrue(usernames.contains(beforeCertExpireNoAlertSend.getUsername()));
        assertTrue(usernames.contains(beforeCertExpireAlertSend.getUsername()));
    }

    @Test
    public void getCertificateExpireUsers() {
        List<DBCredential> dbUserList = testInstance.getCertificateExpiredUsersForAlerts(30, 5, 200);
        List<String> usernames = dbUserList.stream().map(DBCredential::getUser).map(DBUser::getUsername).collect(Collectors.toList());
        System.out.println(usernames);
        assertEquals(2, dbUserList.size());
        assertTrue(usernames.contains(certExpiredNoAlertSend.getUsername()));
        assertTrue(usernames.contains(certExpiredAlertSend.getUsername()));
    }
}
