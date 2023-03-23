package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Ignore
public class UserDaoAlertsTest extends AbstractBaseDao {
/*
    DBUser okUser = TestDBUtils.createDBUserByUsername("okUser-" + UUID.randomUUID().toString());
    DBUser beforePasswordExpireNoAlertSend = TestDBUtils.createDBUserByUsername("beforePasswordExpireNoAlertSend-" + UUID.randomUUID().toString());
    DBUser beforePasswordExpireRecentAlertSend = TestDBUtils.createDBUserByUsername("beforePasswordExpireRecentAlertSend-" + UUID.randomUUID().toString());
    DBUser beforePasswordExpireAlertSend = TestDBUtils.createDBUserByUsername("beforePasswordExpireAlertSend-" + UUID.randomUUID().toString());
    // set expired test cases
    DBUser passwordExpiredNoAlertSend = TestDBUtils.createDBUserByUsername("passwordExpiredNoAlertSend-" + UUID.randomUUID().toString());
    DBUser passwordExpiredRecentAlertSend = TestDBUtils.createDBUserByUsername("passwordExpiredRecentAlertSend-" + UUID.randomUUID().toString());
    DBUser passwordExpiredAlertSend = TestDBUtils.createDBUserByUsername("passwordExpiredAlertSend-" + UUID.randomUUID().toString());
    // ------------
    // access token users  setup
    DBUser beforeATExpireNoAlertSend = TestDBUtils.createDBUserByUsername("beforeATExpireNoAlertSend-" + UUID.randomUUID().toString());
    DBUser beforeATExpireRecentAlertSend = TestDBUtils.createDBUserByUsername("beforeATExpireRecentAlertSend-" + UUID.randomUUID().toString());
    DBUser beforeATExpireAlertSend = TestDBUtils.createDBUserByUsername("beforeATExpireAlertSend-" + UUID.randomUUID().toString());
    // set expired test cases
    DBUser aTExpiredNoAlertSend = TestDBUtils.createDBUserByUsername("ATExpiredNoAlertSend-" + UUID.randomUUID().toString());
    DBUser aTExpiredRecentAlertSend = TestDBUtils.createDBUserByUsername("ATExpiredRecentAlertSend-" + UUID.randomUUID().toString());
    DBUser aTExpiredAlertSend = TestDBUtils.createDBUserByUsername("ATExpiredAlertSend-" + UUID.randomUUID().toString());

    // ------------
    // access token users  setup
    DBUser beforeCertExpireNoAlertSend = TestDBUtils.createDBUserByCertificate("beforecertxpireNoAlertSend-" + UUID.randomUUID().toString());
    DBUser beforeCertExpireRecentAlertSend = TestDBUtils.createDBUserByCertificate("beforeATExpireRecentAlertSend-" + UUID.randomUUID().toString());
    DBUser beforeCertExpireAlertSend = TestDBUtils.createDBUserByCertificate("beforeATExpireAlertSend-" + UUID.randomUUID().toString());
    // set expired test cases
    DBUser certExpiredNoAlertSend = TestDBUtils.createDBUserByCertificate("ATExpiredNoAlertSend-" + UUID.randomUUID().toString());
    DBUser certExpiredRecentAlertSend = TestDBUtils.createDBUserByCertificate("ATExpiredRecentAlertSend-" + UUID.randomUUID().toString());
    DBUser certExpiredAlertSend = TestDBUtils.createDBUserByCertificate("ATExpiredAlertSend-" + UUID.randomUUID().toString());

    @Autowired
    UserDao testInstance;

    @Before
    public void setupData() {
        // reference OK User
        okUser.setPasswordExpireOn(OffsetDateTime.now().plusDays(90));
        // set before expired username-password testcases
        okUser.setPasswordExpireOn(OffsetDateTime.now().plusDays(90));
        beforePasswordExpireNoAlertSend.setPasswordExpireOn(OffsetDateTime.now().plusDays(20));
        beforePasswordExpireRecentAlertSend.setPasswordExpireOn(OffsetDateTime.now().plusDays(20));
        beforePasswordExpireRecentAlertSend.setPasswordExpireAlertOn(OffsetDateTime.now().minusDays(2));
        beforePasswordExpireAlertSend.setPasswordExpireOn(OffsetDateTime.now().plusDays(20));
        beforePasswordExpireAlertSend.setPasswordExpireAlertOn(OffsetDateTime.now().minusDays(10));
        // set expired username-password testcases
        passwordExpiredNoAlertSend.setPasswordExpireOn(OffsetDateTime.now().minusDays(20));
        passwordExpiredRecentAlertSend.setPasswordExpireOn(OffsetDateTime.now().minusDays(20));
        passwordExpiredRecentAlertSend.setPasswordExpireAlertOn(OffsetDateTime.now().minusDays(2));
        passwordExpiredAlertSend.setPasswordExpireOn(OffsetDateTime.now().minusDays(10));
        //-----------------------------------------
        // set before expired access-token testcases
        beforeATExpireNoAlertSend.setAccessTokenExpireOn(OffsetDateTime.now().plusDays(20));
        beforeATExpireRecentAlertSend.setAccessTokenExpireOn(OffsetDateTime.now().plusDays(20));
        beforeATExpireRecentAlertSend.setAccessTokenExpireAlertOn(OffsetDateTime.now().minusDays(2));
        beforeATExpireAlertSend.setAccessTokenExpireOn(OffsetDateTime.now().plusDays(20));
        beforeATExpireAlertSend.setAccessTokenExpireAlertOn(OffsetDateTime.now().minusDays(10));
        // set expired access-token testcases
        aTExpiredNoAlertSend.setAccessTokenExpireOn(OffsetDateTime.now().minusDays(20));
        aTExpiredRecentAlertSend.setAccessTokenExpireOn(OffsetDateTime.now().minusDays(20));
        aTExpiredRecentAlertSend.setAccessTokenExpireAlertOn(OffsetDateTime.now().minusDays(2));
        aTExpiredAlertSend.setAccessTokenExpireOn(OffsetDateTime.now().minusDays(10));
        //-----------------------------------------
        // set before expired certificates testcases
        beforeCertExpireNoAlertSend.getCertificate().setValidTo(OffsetDateTime.now().plusDays(20));
        beforeCertExpireRecentAlertSend.getCertificate().setValidTo(OffsetDateTime.now().plusDays(20));
        beforeCertExpireRecentAlertSend.getCertificate().setCertificateLastExpireAlertOn(OffsetDateTime.now().minusDays(2));
        beforeCertExpireAlertSend.getCertificate().setValidTo(OffsetDateTime.now().plusDays(20));
        beforeCertExpireAlertSend.getCertificate().setCertificateLastExpireAlertOn(OffsetDateTime.now().minusDays(10));
        // set expired certificates testcases
        certExpiredNoAlertSend.getCertificate().setValidTo(OffsetDateTime.now().minusDays(20));
        certExpiredRecentAlertSend.getCertificate().setValidTo(OffsetDateTime.now().minusDays(20));
        certExpiredRecentAlertSend.getCertificate().setCertificateLastExpireAlertOn(OffsetDateTime.now().minusDays(2));
        certExpiredAlertSend.getCertificate().setValidTo(OffsetDateTime.now().minusDays(10));
        // persists
        testInstance.persistFlushDetach(okUser);
        testInstance.persistFlushDetach(beforePasswordExpireNoAlertSend);
        testInstance.persistFlushDetach(beforePasswordExpireRecentAlertSend);
        testInstance.persistFlushDetach(beforePasswordExpireAlertSend);

        testInstance.persistFlushDetach(passwordExpiredNoAlertSend);
        testInstance.persistFlushDetach(passwordExpiredRecentAlertSend);
        testInstance.persistFlushDetach(passwordExpiredAlertSend);

        testInstance.persistFlushDetach(beforeATExpireNoAlertSend);
        testInstance.persistFlushDetach(beforeATExpireRecentAlertSend);
        testInstance.persistFlushDetach(beforeATExpireAlertSend);

        testInstance.persistFlushDetach(aTExpiredNoAlertSend);
        testInstance.persistFlushDetach(aTExpiredRecentAlertSend);
        testInstance.persistFlushDetach(aTExpiredAlertSend);
        // ---
        testInstance.persistFlushDetach(beforeCertExpireNoAlertSend);
        testInstance.persistFlushDetach(beforeCertExpireRecentAlertSend);
        testInstance.persistFlushDetach(beforeCertExpireAlertSend);

        testInstance.persistFlushDetach(certExpiredNoAlertSend);
        testInstance.persistFlushDetach(certExpiredRecentAlertSend);
        testInstance.persistFlushDetach(certExpiredAlertSend);
    }

    @Test
    public void getPasswordImminentExpireUsers() {
        List<DBUser> dbUserList = testInstance.getBeforePasswordExpireUsersForAlerts(30, 5, 200);
        assertEquals(2, dbUserList.size());
        List<String> usernames = dbUserList.stream().map(DBUser::getUsername).collect(Collectors.toList());
        System.out.println(usernames);
        assertTrue(usernames.contains(beforePasswordExpireNoAlertSend.getUsername()));
        assertTrue(usernames.contains(beforePasswordExpireAlertSend.getUsername()));
    }

    @Test
    public void getPasswordExpireUsers() {
        List<DBUser> dbUserList = testInstance.getPasswordExpiredUsersForAlerts(30, 5, 200);
        assertEquals(2, dbUserList.size());
        List<String> usernames = dbUserList.stream().map(DBUser::getUsername).collect(Collectors.toList());
        System.out.println(usernames);
        assertTrue(usernames.contains(passwordExpiredNoAlertSend.getUsername()));
        assertTrue(usernames.contains(passwordExpiredAlertSend.getUsername()));
    }

    @Test
    public void getAccessTokenImminentExpireUsers() {
        List<DBUser> dbUserList = testInstance.getBeforeAccessTokenExpireUsersForAlerts(30, 5, 200);
        List<String> usernames = dbUserList.stream().map(DBUser::getUsername).collect(Collectors.toList());
        System.out.println(usernames);
        assertEquals(2, dbUserList.size());
        assertTrue(usernames.contains(beforeATExpireNoAlertSend.getUsername()));
        assertTrue(usernames.contains(beforeATExpireAlertSend.getUsername()));
    }

    @Test
    public void getAccessTokenExpireUsers() {
        List<DBUser> dbUserList = testInstance.getAccessTokenExpiredUsersForAlerts(30, 5, 200);
        List<String> usernames = dbUserList.stream().map(DBUser::getUsername).collect(Collectors.toList());
        System.out.println(usernames);
        assertEquals(2, dbUserList.size());
        assertTrue(usernames.contains(aTExpiredNoAlertSend.getUsername()));
        assertTrue(usernames.contains(aTExpiredAlertSend.getUsername()));
    }

    @Test
    public void getCertificateImminentExpireUsers() {
        List<DBUser> dbUserList = testInstance.getBeforeCertificateExpireUsersForAlerts(30, 5, 200);
        List<String> usernames = dbUserList.stream().map(DBUser::getUsername).collect(Collectors.toList());
        System.out.println(usernames);
        assertEquals(2, dbUserList.size());
        assertTrue(usernames.contains(beforeCertExpireNoAlertSend.getUsername()));
        assertTrue(usernames.contains(beforeCertExpireAlertSend.getUsername()));
    }

    @Test
    public void getCertificateExpireUsers() {
        List<DBUser> dbUserList = testInstance.getCertificateExpiredUsersForAlerts(30, 5, 200);
        List<String> usernames = dbUserList.stream().map(DBUser::getUsername).collect(Collectors.toList());
        System.out.println(usernames);
        assertEquals(2, dbUserList.size());
        assertTrue(usernames.contains(certExpiredNoAlertSend.getUsername()));
        assertTrue(usernames.contains(certExpiredAlertSend.getUsername()));
    }

 */
}
