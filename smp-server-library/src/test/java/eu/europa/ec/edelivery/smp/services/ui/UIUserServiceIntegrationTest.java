package eu.europa.ec.edelivery.smp.services.ui;


import eu.europa.ec.edelivery.smp.config.ConversionTestConfig;
import eu.europa.ec.edelivery.smp.data.dao.ServiceGroupDao;
import eu.europa.ec.edelivery.smp.data.model.DBCertificate;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.*;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.AbstractServiceIntegrationTest;
import eu.europa.ec.edelivery.smp.testutil.TestConstants;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import eu.europa.ec.edelivery.smp.testutil.TestROUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;
import static org.junit.Assert.*;


/**
 * Purpose of class is to test ServiceGroupService base methods
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@ContextConfiguration(classes = {UIUserService.class, ConversionTestConfig.class})
public class UIUserServiceIntegrationTest extends AbstractServiceIntegrationTest {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIUserServiceIntegrationTest.class);

    @Rule
    public ExpectedException expectedExeption = ExpectedException.none();

    @Autowired
    protected UIUserService testInstance;


    @Autowired
    protected ServiceGroupDao serviceGroupDao;


    protected void insertDataObjects(int size) {
        for (int i = 0; i < size; i++) {
            DBUser d = TestDBUtils.createDBUserByUsername("user" + i);
            d.setPassword(BCrypt.hashpw(d.getPassword(), BCrypt.gensalt()));
            userDao.persistFlushDetach(d);
        }
    }

    @Test
    public void testGetTableListEmpty() {
        LOG.info("testGetTableListEmpty");
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
        LOG.info("testGetTableList15");
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
        assertNull(res.getServiceEntities().get(0).getPassword()); // Service list must not return passwords
        assertNotNull(res.getServiceEntities().get(0).getRole());
    }

    @Test
    public void testAddUserWithoutCertificate() {
        LOG.info("testAddUserWithoutCertificate");
        // given
        insertDataObjects(15);
        long iCnt = userDao.getDataListCount(null);

        UserRO user = new UserRO();
        user.setPassword(UUID.randomUUID().toString());
        user.setUsername(UUID.randomUUID().toString());
        user.setEmailAddress(UUID.randomUUID().toString());
        user.setRole("ROLE");
        user.setStatus(EntityROStatus.NEW.getStatusNumber());



        //when
        testInstance.updateUserList(Collections.singletonList(user), null);

        // then
        long iCntNew = userDao.getDataListCount(null);
        assertEquals(iCnt + 1, iCntNew);
        Optional<DBUser> oUsr = userDao.findUserByUsername(user.getUsername());
        assertTrue(oUsr.isPresent());
        assertTrue(BCrypt.checkpw(user.getPassword(), oUsr.get().getPassword())); // password must be encrypted
        assertEquals(user.getUsername(), oUsr.get().getUsername());
        assertEquals(user.getRole(), oUsr.get().getRole());
        assertEquals(user.getEmailAddress(), oUsr.get().getEmailAddress());
        assertNull(oUsr.get().getCertificate());
    }

    @Test
    public void testAddUserWithCertificate() {
        LOG.info("testAddUserWithCertificate");
        // given
        insertDataObjects(15);
        long iCnt = userDao.getDataListCount(null);

        Calendar calTo = Calendar.getInstance();
        calTo.add(Calendar.YEAR, 1);
        Date now = Calendar.getInstance().getTime();
        Date future = calTo.getTime();

        UserRO user = new UserRO();
        user.setPassword(UUID.randomUUID().toString());
        user.setUsername(UUID.randomUUID().toString());
        user.setEmailAddress(UUID.randomUUID().toString());
        user.setRole("ROLE");
        CertificateRO cert = new CertificateRO();
        cert.setSubject(UUID.randomUUID().toString());
        cert.setIssuer(UUID.randomUUID().toString());
        cert.setSerialNumber(UUID.randomUUID().toString());
        cert.setCertificateId(UUID.randomUUID().toString());
        cert.setValidFrom(now);
        cert.setValidTo(future);
        user.setCertificate(cert);

        user.setStatus(EntityROStatus.NEW.getStatusNumber());

        //when
        testInstance.updateUserList(Collections.singletonList(user), null);

        // then
        long iCntNew = userDao.getDataListCount(null);
        assertEquals(iCnt + 1, iCntNew);
        Optional<DBUser> oUsr = userDao.findUserByUsername(user.getUsername());
        assertTrue(oUsr.isPresent());
        assertTrue(BCrypt.checkpw(user.getPassword(), oUsr.get().getPassword())); // password must be encrypted
        assertEquals(user.getUsername(), oUsr.get().getUsername());
        assertEquals(user.getRole(), oUsr.get().getRole());
        assertEquals(user.getEmailAddress(), oUsr.get().getEmailAddress());
        assertNotNull(oUsr.get().getCertificate());
        assertEquals(cert.getCertificateId(), cert.getCertificateId());
        assertEquals(cert.getSubject(), cert.getSubject());
        assertEquals(cert.getIssuer(), cert.getIssuer());
        assertEquals(cert.getSerialNumber(), cert.getSerialNumber());
        assertEquals(now, cert.getValidFrom());
        assertEquals(future, cert.getValidTo());
    }

    @Test
    public void testAddUserWithOnlyCertificate() {
        LOG.info("testAddUserWithOnlyCertificate");
        // given
        insertDataObjects(15);
        long iCnt = userDao.getDataListCount(null);

        Calendar calTo = Calendar.getInstance();
        calTo.add(Calendar.YEAR, 1);
        Date now = Calendar.getInstance().getTime();
        Date future = calTo.getTime();

        UserRO user = new UserRO();

        user.setRole("ROLE");
        CertificateRO cert = new CertificateRO();
        cert.setSubject(UUID.randomUUID().toString());
        cert.setIssuer(UUID.randomUUID().toString());
        cert.setSerialNumber(UUID.randomUUID().toString());
        cert.setCertificateId(UUID.randomUUID().toString());
        cert.setValidFrom(now);
        cert.setValidTo(future);
        user.setCertificate(cert);

        user.setStatus(EntityROStatus.NEW.getStatusNumber());

        //when
        testInstance.updateUserList(Collections.singletonList(user), null);

        // then
        long iCntNew = userDao.getDataListCount(null);
        assertEquals(iCnt + 1, iCntNew);
        Optional<DBUser> oUsr = userDao.findUserByIdentifier(user.getCertificate().getCertificateId());
        assertTrue(oUsr.isPresent());
        assertEquals(user.getRole(), oUsr.get().getRole());
        assertEquals(user.getEmailAddress(), oUsr.get().getEmailAddress());
        assertNotNull(oUsr.get().getCertificate());
        assertEquals(cert.getCertificateId(), cert.getCertificateId());
        assertEquals(cert.getSubject(), cert.getSubject());
        assertEquals(cert.getIssuer(), cert.getIssuer());
        assertEquals(cert.getSerialNumber(), cert.getSerialNumber());
        assertEquals(now, cert.getValidFrom());
        assertEquals(future, cert.getValidTo());
    }


    @Test
    public void testUserRemoveCertificate() {
        LOG.info("testUserRemoveCertificate");
        // given
        OffsetDateTime now = OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        OffsetDateTime future = now.plusYears(1);

        DBUser user = new DBUser();
        user.setPassword(UUID.randomUUID().toString());
        user.setUsername(UUID.randomUUID().toString());
        user.setEmailAddress(UUID.randomUUID().toString());
        user.setRole("ROLE");
        DBCertificate cert = new DBCertificate();
        cert.setSubject(UUID.randomUUID().toString());
        cert.setIssuer(UUID.randomUUID().toString());
        cert.setSerialNumber(UUID.randomUUID().toString());
        cert.setCertificateId(UUID.randomUUID().toString());
        cert.setValidFrom(now);
        cert.setValidTo(future);
        user.setCertificate(cert);
        userDao.persistFlushDetach(user);
        ServiceResult<UserRO> urTest = testInstance.getTableList(-1, -1, null, null, null);
        assertEquals(1, urTest.getServiceEntities().size());
        UserRO userRO = urTest.getServiceEntities().get(0);
        assertNotNull(userRO.getCertificate());

        //when
        userRO.setCertificate(null);
        userRO.setStatus(EntityROStatus.UPDATED.getStatusNumber());

        testInstance.updateUserList(Collections.singletonList(userRO), null);

        // then
        ServiceResult<UserRO> res = testInstance.getTableList(-1, -1, null, null, null);
        assertEquals(1, urTest.getServiceEntities().size());
        UserRO userResRO = urTest.getServiceEntities().get(0);
        assertNull(userResRO.getCertificate());

    }

    @Test
    public void testDeleteUser() {
        LOG.info("testDeleteUser");
        // given
        insertDataObjects(15);
        ServiceResult<UserRO> urTest = testInstance.getTableList(-1, -1, null, null, null);
        assertEquals(15, urTest.getServiceEntities().size());

        UserRO user = urTest.getServiceEntities().get(0);
        user.setStatus(EntityROStatus.REMOVE.getStatusNumber());

        //when
        testInstance.updateUserList(Collections.singletonList(user), null);

        // then
        long iCntNew = userDao.getDataListCount(null);
        Optional<DBUser> rmUsr = userDao.findUserByUsername(user.getUsername());

        assertEquals(urTest.getServiceEntities().size() - 1, iCntNew);
        assertFalse(rmUsr.isPresent());
    }

    @Test
    @Transactional
    public void testGenerateAccessTokenForUser() {
        LOG.info("testGenerateAccessTokenForUser");
        String userPassword = UUID.randomUUID().toString();
        DBUser user = new DBUser();
        user.setPassword(BCrypt.hashpw(userPassword, BCrypt.gensalt()));
        user.setUsername(UUID.randomUUID().toString());
        user.setEmailAddress(UUID.randomUUID().toString());
        user.setRole("ROLE");
        LOG.info("persist");
        userDao.persistFlushDetach(user);

        LOG.info("generateAccessTokenForUser");
        AccessTokenRO token = testInstance.generateAccessTokenForUser(user.getId(), user.getId(), userPassword);

        LOG.info("findUserByAuthenticationToken");
        Optional<DBUser> optResult = userDao.findUserByAuthenticationToken(token.getIdentifier());
        LOG.info("asserts");
        assertTrue(optResult.isPresent());
        assertNotNull(token);
        DBUser result = optResult.get();
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(result.getAccessTokenIdentifier(), token.getIdentifier());
        assertTrue(BCrypt.checkpw(token.getValue(), result.getAccessToken()));
        assertNotNull(result.getAccessTokenExpireOn());
        assertNotNull(result.getAccessTokenGeneratedOn());
    }

    @Test
    public void testUpdateUserPasswordNotMatchReqExpression() {
        LOG.info("testUpdateUserPasswordNotMatchReqExpression");
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
        LOG.info("testUpdateUserPasswordUserNotExists");
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
        LOG.info("testUpdateUserPasswordUserNotAuthorized");
        String userPassword = UUID.randomUUID().toString();
        DBUser user = new DBUser();
        user.setPassword(BCrypt.hashpw(userPassword, BCrypt.gensalt()));
        user.setUsername(UUID.randomUUID().toString());
        user.setEmailAddress(UUID.randomUUID().toString());
        user.setRole("ROLE");
        userDao.persistFlushDetach(user);

        long authorizedUserId = user.getId();
        long userToUpdateId = 1L;
        String authorizedPassword = "oldPass";
        String newPassword = "TTTTtttt1111$$$$$";

        BadCredentialsException result = assertThrows(BadCredentialsException.class,
                () -> testInstance.updateUserPassword(authorizedUserId, userToUpdateId, authorizedPassword, newPassword));

        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString("Password change failed; Invalid current password!"));
    }

    @Test
    public void testUpdateUserPasswordOK() {
        LOG.info("testUpdateUserPasswordOK");
        String userPassword = UUID.randomUUID().toString();
        DBUser user = new DBUser();
        user.setPassword(BCrypt.hashpw(userPassword, BCrypt.gensalt()));
        user.setUsername(UUID.randomUUID().toString());
        user.setEmailAddress(UUID.randomUUID().toString());
        user.setRole("ROLE");
        userDao.persistFlushDetach(user);

        long authorizedUserId = user.getId();
        long userToUpdateId = user.getId();
        String authorizedPassword = userPassword;
        String newPassword = "TTTTtttt1111$$$$$";

        testInstance.updateUserPassword(authorizedUserId, userToUpdateId, authorizedPassword, newPassword);
    }

    @Test
    public void testUpdateUserdataOK() {
        LOG.info("testUpdateUserdataOK");
        String userPassword = UUID.randomUUID().toString();
        DBUser user = new DBUser();
        user.setPassword(BCrypt.hashpw(userPassword, BCrypt.gensalt()));
        user.setUsername(UUID.randomUUID().toString());
        user.setEmailAddress(UUID.randomUUID().toString());
        user.setRole("ROLE");
        userDao.persistFlushDetach(user);

        UserRO userRO = new UserRO();
        userRO.setEmailAddress(UUID.randomUUID().toString());
        userRO.setUsername(UUID.randomUUID().toString());
        userRO.setAccessTokenId(UUID.randomUUID().toString());
        userRO.setRole(UUID.randomUUID().toString());

        testInstance.updateUserdata(user.getId(), userRO);

        DBUser changedUser = userDao.findUser(user.getId()).get();
        // fields must not change
        assertEquals(user.getUsername(), changedUser.getUsername());
        assertEquals(user.getAccessToken(), changedUser.getAccessToken());
        assertEquals(user.getRole(), changedUser.getRole());
        // changed
        assertEquals(userRO.getEmailAddress(), changedUser.getEmailAddress());
    }

    @Test
    public void testUpdateUserdataCertificateOK() throws Exception {
        LOG.info("testUpdateUserdataCertificateOK");
        String certSubject = "CN=" + UUID.randomUUID().toString() + ",O=eDelivery,C=EU";
        String userPassword = UUID.randomUUID().toString();
        DBUser user = new DBUser();
        user.setPassword(BCrypt.hashpw(userPassword, BCrypt.gensalt()));
        user.setUsername(UUID.randomUUID().toString());
        user.setEmailAddress(UUID.randomUUID().toString());
        user.setRole("ROLE");
        userDao.persistFlushDetach(user);

        CertificateRO certificateRO = TestROUtils.createCertificateRO(certSubject, BigInteger.TEN);
        UserRO userRO = new UserRO();
        userRO.setCertificate(certificateRO);

        testInstance.updateUserdata(user.getId(), userRO);


        DBUser changedUser = userDao.findUser(user.getId()).get();
        // fields must not change
        assertNotNull(changedUser.getCertificate());
        assertNotNull(changedUser.getCertificate().getPemEncoding());
        assertNotNull(certificateRO.getCertificateId(), changedUser.getCertificate().getCertificateId());
        assertNotNull(certificateRO.getSubject(), changedUser.getCertificate().getSubject());
        assertNotNull(certificateRO.getIssuer(), changedUser.getCertificate().getIssuer());
        assertNotNull(certificateRO.getSerialNumber(), changedUser.getCertificate().getSerialNumber());
    }


    @Test
    public void testUpdateUserdataCertificateWithExistingCertificateOK() throws Exception {
        LOG.info("testUpdateUserdataCertificateWithExistingCertificateOK");
        String certSubject = "CN=" + UUID.randomUUID().toString() + ",O=eDelivery,C=EU";
        DBUser user = TestDBUtils.createDBUserByCertificate(TestConstants.USER_CERT_2);
        userDao.persistFlushDetach(user);

        CertificateRO certificateRO = TestROUtils.createCertificateRO(certSubject, BigInteger.TEN);
        UserRO userRO = new UserRO();
        userRO.setCertificate(certificateRO);

        testInstance.updateUserdata(user.getId(), userRO);


        DBUser changedUser = userDao.findUser(user.getId()).get();
        // fields must not change
        assertNotNull(changedUser.getCertificate());
        assertNotNull(changedUser.getCertificate().getPemEncoding());
        assertNotNull(certificateRO.getCertificateId(), changedUser.getCertificate().getCertificateId());
        assertNotNull(certificateRO.getSubject(), changedUser.getCertificate().getSubject());
        assertNotNull(certificateRO.getIssuer(), changedUser.getCertificate().getIssuer());
        assertNotNull(certificateRO.getSerialNumber(), changedUser.getCertificate().getSerialNumber());
    }

    @Test
    public void testValidateDeleteRequest() throws Exception {
        LOG.info("testValidateDeleteRequest");
        String username1 = "test-user-delete-01";
        String username2 = "test-user-delete-02";

        DBUser user1 = TestDBUtils.createDBUser(username1);
        DBUser user2 = TestDBUtils.createDBUser(username2);
        userDao.persistFlushDetach(user1);
        userDao.persistFlushDetach(user2);

        DBDomain d = new DBDomain();
        d.setDomainCode(TEST_DOMAIN_CODE_1);
        d.setSmlSubdomain(TEST_SML_SUBDOMAIN_CODE_1);
        domainDao.persistFlushDetach(d);

        DBServiceGroup sg = TestDBUtils.createDBServiceGroup(TEST_SG_ID_1, TEST_SG_SCHEMA_1);
        sg.getUsers().add(user2);
        sg.addDomain(d);

        serviceGroupDao.persistFlushDetach(sg);
        DeleteEntityValidation validation = new DeleteEntityValidation();
        validation.getListIds().add(user1.getId()+"");
        validation.getListIds().add(user2.getId()+"");

        DeleteEntityValidation result = testInstance.validateDeleteRequest(validation);

        assertEquals(1, result.getListDeleteNotPermitedIds().size());
        assertEquals(user2.getId()+"",  result.getListDeleteNotPermitedIds().get(0));
        assertEquals(2, result.getListIds().size());
    }
}
