package eu.europa.ec.edelivery.smp.services.ui;


import eu.europa.ec.edelivery.smp.config.ConversionTestConfig;
import eu.europa.ec.edelivery.smp.data.dao.ResourceDao;
import eu.europa.ec.edelivery.smp.data.enums.ApplicationRoleType;
import eu.europa.ec.edelivery.smp.data.model.user.DBCredential;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.services.AbstractServiceIntegrationTest;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;


/**
 * Purpose of class is to test ServiceGroupService base methods
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@ContextConfiguration(classes = {UIUserService.class, ConversionTestConfig.class})
public class UIUserServiceIntegrationTest extends AbstractServiceIntegrationTest {
    @Rule
    public ExpectedException expectedExeption = ExpectedException.none();
    @Autowired
    protected UIUserService testInstance;
    @Autowired
    protected ResourceDao serviceGroupDao;

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

}
