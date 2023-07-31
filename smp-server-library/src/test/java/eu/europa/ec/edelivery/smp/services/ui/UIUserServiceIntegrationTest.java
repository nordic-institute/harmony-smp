package eu.europa.ec.edelivery.smp.services.ui;


import eu.europa.ec.edelivery.smp.config.ConversionTestConfig;
import eu.europa.ec.edelivery.smp.data.dao.AbstractJunit5BaseDao;
import eu.europa.ec.edelivery.smp.data.dao.CredentialDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.enums.ApplicationRoleType;
import eu.europa.ec.edelivery.smp.data.enums.CredentialTargetType;
import eu.europa.ec.edelivery.smp.data.enums.CredentialType;
import eu.europa.ec.edelivery.smp.data.model.user.DBCredential;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.*;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import eu.europa.ec.edelivery.smp.exceptions.BadRequestException;
import eu.europa.ec.edelivery.smp.exceptions.ErrorBusinessCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import eu.europa.ec.edelivery.smp.testutil.TestROUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static eu.europa.ec.edelivery.smp.testutil.SMPAssert.assertEqualDates;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


/**
 * Purpose of class is to test ServiceGroupService base methods
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@ContextConfiguration(classes = {UIUserService.class, ConversionTestConfig.class})
public class UIUserServiceIntegrationTest extends AbstractJunit5BaseDao {

    @Autowired
    protected UIUserService testInstance;

    @Autowired
    protected UserDao userDao;

    @Autowired
    protected CredentialDao credentialDao;


    protected void insertDataObjects(int size) {
        for (int i = 0; i < size; i++) {
            DBUser d = TestDBUtils.createDBUserByUsername("user" + i);
            userDao.persistFlushDetach(d);
        }
    }

    @Test
    public void testGetTableListEmpty() {
        // given

        //when
        ServiceResult<UserRO> res = testInstance.getTableList(-1, -1, null, null, null);

        // then
        assertNotNull(res);
        assertEquals(0, res.getCount().intValue());
        assertEquals(0, res.getPage().intValue());
        assertEquals(0, res.getPageSize().intValue());
        assertEquals(0, res.getServiceEntities().size());
        assertNull(res.getFilter());
    }

    @Test
    public void testGetTableList15() {
        // given
        insertDataObjects(15);

        //when
        ServiceResult<UserRO> res = testInstance.getTableList(-1, -1, null, null, null);

        // then
        assertNotNull(res);
        assertEquals(15, res.getCount().intValue());
        assertEquals(0, res.getPage().intValue());
        assertEquals(15, res.getPageSize().intValue());
        assertEquals(15, res.getServiceEntities().size());
        assertNull(res.getFilter());

        // all table properties should not be null
        assertNotNull(res);
        assertNotNull(res.getServiceEntities().get(0).getUserId());
        assertNotNull(res.getServiceEntities().get(0).getUsername());
        assertNotNull(res.getServiceEntities().get(0).getEmailAddress());
        assertNotNull(res.getServiceEntities().get(0).getRole());
    }

    @Test
    public void testAddUser() {
        // given
        insertDataObjects(15);
        long iCnt = userDao.getDataListCount(null);

        UserRO user = new UserRO();
        user.setUsername(UUID.randomUUID().toString());
        user.setEmailAddress(UUID.randomUUID().toString());
        user.setRole(ApplicationRoleType.USER);
        user.setStatus(EntityROStatus.NEW.getStatusNumber());

        //when
        testInstance.adminCreateUserData(user);

        // then
        long iCntNew = userDao.getDataListCount(null);
        assertEquals(iCnt + 1, iCntNew);
        Optional<DBUser> oUsr = userDao.findUserByUsername(user.getUsername());
        assertTrue(oUsr.isPresent());
        assertEquals(user.getUsername(), oUsr.get().getUsername());
        assertEquals(user.getRole(), oUsr.get().getApplicationRole());
        assertEquals(user.getEmailAddress(), oUsr.get().getEmailAddress());
    }

    @Test
    public void testDeleteUser() {
        // given
        insertDataObjects(15);
        ServiceResult<UserRO> urTest = testInstance.getTableList(-1, -1, null, null, null);
        assertEquals(15, urTest.getServiceEntities().size());

        UserRO user = urTest.getServiceEntities().get(0);
        Optional<DBUser> rmUsr = userDao.findUserByUsername(user.getUsername());

        //when
        testInstance.adminDeleteUserData(rmUsr.get().getId());

        // then
        long iCntNew = userDao.getDataListCount(null);
        Optional<DBUser> rmUsr2 = userDao.findUserByUsername(user.getUsername());

        assertEquals(urTest.getServiceEntities().size() - 1, iCntNew);
        assertFalse(rmUsr2.isPresent());
    }

    @Test
    public void testUpdateUserPasswordNotMatchReqExpression() {
        long authorizedUserId = 1L;
        long userToUpdateId = 1L;
        String authorizedPassword = "testPass";
        String newPassword = "newPass";

        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                () -> testInstance.updateUserPassword(authorizedUserId, userToUpdateId, authorizedPassword, newPassword));

        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString("Invalid request [PasswordChange]."));
    }

    @Test
    public void testUpdateUserPasswordUserNotExists() {

        long authorizedUserId = 1L;
        long userToUpdateId = 1L;
        String authorizedPassword = "oldPass";
        String newPassword = "TTTTtttt1111$$$$$";

        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                () -> testInstance.updateUserPassword(authorizedUserId, userToUpdateId, authorizedPassword, newPassword));

        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString("Invalid request [UserId]. Error: Can not find user id!"));
    }

    @Test
    public void testUpdateUserPasswordUserNotAuthorized() {


        DBUser user = TestDBUtils.createDBUserByUsername(UUID.randomUUID().toString());
        DBCredential credential = TestDBUtils.createDBCredentialForUser(user, null, null, null);
        credential.setValue(BCrypt.hashpw("userPassword", BCrypt.gensalt()));
        userDao.persistFlushDetach(user);
        credentialDao.persistFlushDetach(credential);


        long authorizedUserId = user.getId();
        String authorizedPassword = "oldPass";
        String newPassword = "TTTTtttt1111$$$$$";

        BadCredentialsException result = assertThrows(BadCredentialsException.class,
                () -> testInstance.updateUserPassword(authorizedUserId, authorizedUserId, authorizedPassword, newPassword));

        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString("Password change failed; Invalid authorization password!"));
    }

    @Test
    public void testUpdateUserPasswordOK() {
        DBUser user = TestDBUtils.createDBUserByUsername(UUID.randomUUID().toString());
        DBCredential credential = TestDBUtils.createDBCredentialForUser(user, null, null, null);
        credential.setValue(BCrypt.hashpw("userPassword", BCrypt.gensalt()));
        userDao.persistFlushDetach(user);
        credentialDao.persistFlushDetach(credential);

        long authorizedUserId = user.getId();
        long userToUpdateId = user.getId();
        String authorizedPassword = "userPassword";
        String newPassword = "TTTTtttt1111$$$$$";

        testInstance.updateUserPassword(authorizedUserId, userToUpdateId, authorizedPassword, newPassword);
    }

    @Test
    public void testUpdateUserPasswordByAdminUserNotExists() {
        // system admin
        DBUser user = TestDBUtils.createDBUserByUsername(UUID.randomUUID().toString());
        user.setApplicationRole(ApplicationRoleType.SYSTEM_ADMIN);
        DBCredential credential = TestDBUtils.createDBCredentialForUser(user, null, null, null);
        credential.setValue(BCrypt.hashpw("userPassword", BCrypt.gensalt()));
        userDao.persistFlushDetach(user);
        credentialDao.persistFlushDetach(credential);

        long authorizedUserId = user.getId();
        long userToUpdateId =-1000L;
        String authorizedPassword = "userPassword";
        String newPassword = "TTTTtttt1111$$$$$";

        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                () -> testInstance.updateUserPassword(authorizedUserId,userToUpdateId, authorizedPassword, newPassword));

        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString("Invalid request [UserId]. Error: Can not find user id to update"));
    }

    @Test
    public void testAdminUpdateUserdataOK() {
        DBUser user = TestDBUtils.createDBUserByUsername(UUID.randomUUID().toString());
        userDao.persistFlushDetach(user);

        UserRO userRO = new UserRO();
        userRO.setEmailAddress(UUID.randomUUID().toString());
        userRO.setFullName(UUID.randomUUID().toString());
        userRO.setRole(ApplicationRoleType.SYSTEM_ADMIN);

        testInstance.adminUpdateUserData(user.getId(), userRO);

        DBUser changedUser = userDao.findUser(user.getId()).get();
        // fields must not change
        assertEquals(userRO.getRole(), changedUser.getApplicationRole());
        assertEquals(userRO.getEmailAddress(), changedUser.getEmailAddress());
        assertEquals(userRO.getFullName(), changedUser.getFullName());
        // changed
        assertEquals(userRO.getEmailAddress(), changedUser.getEmailAddress());
    }


    @Test
    public void testCreateAccessTokenForUser() {
        DBUser user = TestDBUtils.createDBUserByUsername(UUID.randomUUID().toString());
        userDao.persistFlushDetach(user);
        CredentialRO credentialRO = new CredentialRO();
        credentialRO.setCredentialType(CredentialType.ACCESS_TOKEN);
        credentialRO.setDescription("test description");

        AccessTokenRO accessToken = testInstance.createAccessTokenForUser(user.getId(), credentialRO);

        assertNotNull(accessToken);
        assertNotNull(accessToken.getValue());
        assertNotNull(accessToken.getIdentifier());
        assertNotNull(accessToken.getCredential());
        assertNotNull(accessToken.getCredential().getCredentialId());
        assertNotNull(accessToken.getExpireOn());
        assertNotNull(accessToken.getGeneratedOn());
        assertEquals(credentialRO.getDescription(), accessToken.getCredential().getDescription());
        assertEquals(accessToken.getIdentifier(), accessToken.getCredential().getName());
        assertEqualDates(accessToken.getExpireOn(), accessToken.getCredential().getExpireOn());
        assertEqualDates(accessToken.getGeneratedOn(), accessToken.getCredential().getActiveFrom());
    }

    @Test
    public void testCreateAccessTokenForUserUserNotExists() {
        CredentialRO credentialRO = new CredentialRO();
        credentialRO.setCredentialType(CredentialType.ACCESS_TOKEN);
        credentialRO.setDescription("test description");

        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                () -> testInstance.createAccessTokenForUser(-100L, credentialRO));

        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString("Invalid request [UserId]. Error: Can not find user id!"));
    }

    @Test
    public void testStoreCertificateCredentialForUser() throws Exception {
        DBUser user = TestDBUtils.createDBUserByUsername(UUID.randomUUID().toString());
        userDao.persistFlushDetach(user);
        CertificateRO certificateRO = TestROUtils.createCertificateRO("CN=Test,OU=Test,O=Test,L=Test,ST=Test,C=EU", BigInteger.TEN);

        CredentialRO credentialRO = new CredentialRO();
        credentialRO.setCredentialType(CredentialType.CERTIFICATE);
        credentialRO.setDescription("test description");
        credentialRO.setCertificate(certificateRO);

        CredentialRO result = testInstance.storeCertificateCredentialForUser(user.getId(), credentialRO);

        assertNotNull(result);
        assertNotNull(result.getCertificate());
        assertEquals(certificateRO.getCertificateId(), result.getName());
        assertEqualDates(certificateRO.getValidTo(), result.getExpireOn());
        assertEqualDates(certificateRO.getValidFrom(), result.getActiveFrom());
        assertEquals(credentialRO.getDescription(), result.getDescription());
    }

    @Test
    public void testStoreCertificateCredentialForUserUserNotExists() throws Exception {
        CertificateRO certificateRO = TestROUtils.createCertificateRO("CN=Test,OU=Test,O=Test,L=Test,ST=Test,C=EU", BigInteger.TEN);

        CredentialRO credentialRO = new CredentialRO();
        credentialRO.setCredentialType(CredentialType.CERTIFICATE);
        credentialRO.setDescription("test description");
        credentialRO.setCertificate(certificateRO);

        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                () -> testInstance.storeCertificateCredentialForUser(-100L, credentialRO));

        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString("Invalid request [UserId]. Error: Can not find user id!"));
    }

    @Test
    public void testUpdateUserProfile() {
        DBUser user = TestDBUtils.createDBUserByUsername(UUID.randomUUID().toString());
        userDao.persistFlushDetach(user);
        UserRO userRO = new UserRO();
        userRO.setUsername(UUID.randomUUID().toString());
        // add opposite to current role
        userRO.setRole(user.getApplicationRole() == ApplicationRoleType.USER ? ApplicationRoleType.SYSTEM_ADMIN : ApplicationRoleType.USER);
        userRO.setEmailAddress(UUID.randomUUID().toString());
        userRO.setFullName(UUID.randomUUID().toString());
        userRO.setSmpTheme(UUID.randomUUID().toString());
        userRO.setSmpLocale(UUID.randomUUID().toString());

        testInstance.updateUserProfile(user.getId(), userRO);

        DBUser changedUser = userDao.findUser(user.getId()).get();
        // fields must not change
        assertEquals(user.getUsername(), changedUser.getUsername());
        assertEquals(user.getApplicationRole(), changedUser.getApplicationRole());
        // changed
        assertEquals(userRO.getEmailAddress(), changedUser.getEmailAddress());
        assertEquals(userRO.getSmpTheme(), changedUser.getSmpTheme());
        assertEquals(userRO.getSmpLocale(), changedUser.getSmpLocale());
        assertEquals(userRO.getEmailAddress(), changedUser.getEmailAddress());
        assertEquals(userRO.getFullName(), changedUser.getFullName());
    }

    @Test
    public void testGetUserCredentials() {
        DBUser user = TestDBUtils.createDBUserByUsername(UUID.randomUUID().toString());
        userDao.persistFlushDetach(user);
        CredentialRO credentialRO = new CredentialRO();
        credentialRO.setCredentialType(CredentialType.ACCESS_TOKEN);
        credentialRO.setDescription("test description");

        testInstance.createAccessTokenForUser(user.getId(), credentialRO);

        List<CredentialRO> result = testInstance.getUserCredentials(user.getId(), CredentialType.ACCESS_TOKEN, CredentialTargetType.REST_API);
        List<CredentialRO> result2 = testInstance.getUserCredentials(user.getId(), CredentialType.CERTIFICATE, CredentialTargetType.REST_API);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(0, result2.size());
        assertEquals(credentialRO.getDescription(), result.get(0).getDescription());
    }

    @Test
    public void testGetUserCertificateCredential() throws Exception {
        DBUser user = TestDBUtils.createDBUserByUsername(UUID.randomUUID().toString());
        userDao.persistFlushDetach(user);

        CertificateRO certificateRO = TestROUtils.createCertificateRO("CN=Test,OU=Test,O=Test,L=Test,ST=Test,C=EU", BigInteger.TEN);

        CredentialRO credentialRO = new CredentialRO();
        credentialRO.setCredentialType(CredentialType.CERTIFICATE);
        credentialRO.setDescription("test description");
        credentialRO.setCertificate(certificateRO);
        credentialRO = testInstance.storeCertificateCredentialForUser(user.getId(), credentialRO);
        // the credential id for the test is not encrypted and we can use "Long parsing".
        CredentialRO result = testInstance.getUserCertificateCredential(user.getId(), new Long(credentialRO.getCredentialId()));

        assertNotNull(result);
        assertEquals(credentialRO.getCredentialId(), result.getCredentialId());
        assertEquals(credentialRO.getDescription(), result.getDescription());
        assertEquals(credentialRO.getCertificate().getCertificateId(), result.getCertificate().getCertificateId());
    }

    @Test
    public void testDeleteUserCredentials() {
        DBUser user = TestDBUtils.createDBUserByUsername(UUID.randomUUID().toString());
        userDao.persistFlushDetach(user);
        CredentialRO credentialRO = new CredentialRO();
        credentialRO.setCredentialType(CredentialType.ACCESS_TOKEN);
        credentialRO.setDescription("test description");
        testInstance.createAccessTokenForUser(user.getId(), credentialRO);

        List<CredentialRO> result = testInstance.getUserCredentials(user.getId(), CredentialType.ACCESS_TOKEN, CredentialTargetType.REST_API);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(credentialRO.getDescription(), result.get(0).getDescription());
        // the credential id for the test is not encrypted and we can use "Long parsing".
        testInstance.deleteUserCredentials(user.getId(), new Long(result.get(0).getCredentialId()), CredentialType.ACCESS_TOKEN, CredentialTargetType.REST_API);

        result = testInstance.getUserCredentials(user.getId(), CredentialType.ACCESS_TOKEN, CredentialTargetType.REST_API);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testUpdateUserCredentials() {
        DBUser user = TestDBUtils.createDBUserByUsername(UUID.randomUUID().toString());
        userDao.persistFlushDetach(user);
        CredentialRO credentialRO = new CredentialRO();
        credentialRO.setCredentialType(CredentialType.ACCESS_TOKEN);
        credentialRO.setDescription("test description");
        testInstance.createAccessTokenForUser(user.getId(), credentialRO);

        List<CredentialRO> result = testInstance.getUserCredentials(user.getId(), CredentialType.ACCESS_TOKEN, CredentialTargetType.REST_API);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(credentialRO.getDescription(), result.get(0).getDescription());

        CredentialRO credentialRO2 = new CredentialRO();
        credentialRO2.setCredentialType(CredentialType.ACCESS_TOKEN);
        credentialRO2.setDescription("test description 2");
        // the credential id for the test is not encrypted and we can use "Long parsing".
        testInstance.updateUserCredentials(user.getId(), new Long(result.get(0).getCredentialId()), CredentialType.ACCESS_TOKEN, CredentialTargetType.REST_API, credentialRO2);

        result = testInstance.getUserCredentials(user.getId(), CredentialType.ACCESS_TOKEN, CredentialTargetType.REST_API);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(credentialRO2.getDescription(), result.get(0).getDescription());
    }

    @Test
    public void testSearchUsers() {
        long count = testInstance.searchUsers(-1, -1, null).getCount();
        DBUser user = TestDBUtils.createDBUserByUsername(UUID.randomUUID().toString());
        userDao.persistFlushDetach(user);

        ServiceResult<SearchUserRO> result = testInstance.searchUsers(-1, -1, null);
        assertNotNull(result);
        assertEquals(count + 1, result.getServiceEntities().size());
        assertEquals(user.getUsername(), result.getServiceEntities().get(0).getUsername());
        assertEquals(user.getFullName(), result.getServiceEntities().get(0).getFullName());
    }

    @Test
    public void testSearchUsersFilter() {
        long count = testInstance.searchUsers(-1, -1, null).getCount();
        DBUser user = TestDBUtils.createDBUserByUsername(UUID.randomUUID().toString());
        DBUser user2 = TestDBUtils.createDBUserByUsername("TESTuser_" + UUID.randomUUID());
        DBUser user3 = TestDBUtils.createDBUserByUsername("test_" + UUID.randomUUID());
        userDao.persistFlushDetach(user);
        userDao.persistFlushDetach(user2);
        userDao.persistFlushDetach(user3);

        ServiceResult<SearchUserRO> result = testInstance.searchUsers(-1, -1, "test");
        assertNotNull(result);
        assertEquals(count + 2, result.getServiceEntities().size());
        MatcherAssert.assertThat(result.getServiceEntities().get(0).getUsername(), CoreMatchers.containsStringIgnoringCase("test"));
        MatcherAssert.assertThat(result.getServiceEntities().get(1).getUsername(), CoreMatchers.containsStringIgnoringCase("test"));
    }

    @ParameterizedTest
    @CsvSource({
            ", USERNAME_PASSWORD, UI, 1, USERNAME_PASSWORD, UI,  'Credential does not exist!'",
            "1, USERNAME_PASSWORD, UI, 2, USERNAME_PASSWORD, UI,  'User is not owner of the credential'",
            "1, USERNAME_PASSWORD, UI, 1, ACCESS_TOKEN, UI,  'Credentials are not expected credential type!'",
            "1, USERNAME_PASSWORD, UI, 1, USERNAME_PASSWORD, REST_API,  'Credentials are not expected target type!'"})
    public void testValidateCredentialsFails(Long credentialUserId, CredentialType credentialType, CredentialTargetType credentialTargetType, Long testUserId, CredentialType testCredentialType, CredentialTargetType testCredentialTargetType, String errorMessage){
        DBCredential credential = credentialUserId == null? null:Mockito.mock(DBCredential.class);
        if (credential!= null){
            DBUser user = Mockito.mock(DBUser.class);
            when(user.getId()).thenReturn(credentialUserId);
            when(credential.getUser()).thenReturn(user);
            when(credential.getCredentialType()).thenReturn(credentialType);
            when(credential.getCredentialTarget()).thenReturn(credentialTargetType);
        }

        BadRequestException result = assertThrows(BadRequestException.class, () -> testInstance.validateCredentials(credential, testUserId, testCredentialType, testCredentialTargetType));
        assertEquals(ErrorBusinessCode.UNAUTHORIZED, result.getErrorBusinessCode());
        assertEquals(errorMessage, result.getMessage());
    }
}
