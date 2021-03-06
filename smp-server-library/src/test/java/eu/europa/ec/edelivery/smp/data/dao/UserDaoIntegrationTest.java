package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.model.DBUserDeleteValidation;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.testutil.TestConstants;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.INVALID_USER_NO_IDENTIFIERS;
import static org.junit.Assert.*;


/**
 * Purpose of class is to test all resource methods with database.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class UserDaoIntegrationTest extends AbstractBaseDao {

    @Autowired
    UserDao testInstance;

    @Autowired
    ServiceGroupDao serviceGroupDao;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void persistUserWithoutIdentifier() {
        // set
        DBUser u = new DBUser();
        expectedEx.expectMessage(INVALID_USER_NO_IDENTIFIERS.getMessage());
        expectedEx.expect(SMPRuntimeException.class);
        // execute
        testInstance.persistFlushDetach(u);

        fail();
    }

    @Test
    public void persistUserWithUsername() {
        // set
        DBUser u = TestDBUtils.createDBUserByUsername(TestConstants.USERNAME_1);

        // execute
        testInstance.persistFlushDetach(u);

        //test
        Optional<DBUser> ou = testInstance.findUserByUsername(TestConstants.USERNAME_1);
        assertNotSame(u , ou.get());
        assertEquals(u, ou.get());
        assertEquals(u.getEmailAddress(), ou.get().getEmailAddress());
        assertEquals(u.getPassword(), ou.get().getPassword());
        assertEquals(u.getRole(), ou.get().getRole());
        assertEquals(u.getUsername(), ou.get().getUsername());
    }


    @Test
    public void persistUserWithUsernameAndEmptyCertificateID() {
        // if certificate id is null then do not store certificate object to database
        // because of unique constraint  and null value in mysql is also subject to the constraint!
        DBUser u = TestDBUtils.createDBUser(TestConstants.USERNAME_1, null);
        assertNotNull(u.getCertificate());
        assertNull(u.getCertificate().getCertificateId());

        // execute
        testInstance.persistFlushDetach(u);

        //test
        Optional<DBUser> ou = testInstance.findUserByUsername(TestConstants.USERNAME_1);
        assertNotSame(u , ou.get());
        assertEquals(u, ou.get());
        assertEquals(u.getUsername(), ou.get().getUsername());
        assertNull(u.getCertificate());
    }

    @Test
    public void persistUserWithCertificate() {
        // set
        DBUser u = TestDBUtils.createDBUserByCertificate(TestConstants.USER_CERT_1);

        // execute
        testInstance.persistFlushDetach(u);

        //test
        Optional<DBUser> ou = testInstance.findUserByCertificateId(TestConstants.USER_CERT_1);
        assertNotSame(u , ou.get());
        assertEquals(u, ou.get());
        assertEquals(u.getEmailAddress(), ou.get().getEmailAddress());
        assertEquals(u.getCertificate().getCertificateId(), ou.get().getCertificate().getCertificateId());
        assertEquals(u.getCertificate().getValidFrom().truncatedTo(ChronoUnit.MINUTES), ou.get().getCertificate().getValidFrom().truncatedTo(ChronoUnit.MINUTES));
        assertEquals(u.getCertificate().getValidTo().truncatedTo(ChronoUnit.MINUTES), ou.get().getCertificate().getValidTo().truncatedTo(ChronoUnit.MINUTES));
    }

    @Test
    public void findCertUserByIdentifier() {
        // set
        DBUser u = TestDBUtils.createDBUserByCertificate(TestConstants.USER_CERT_1);

        // execute
        testInstance.persistFlushDetach(u);

        //test
        Optional<DBUser> ou = testInstance.findUserByIdentifier(TestConstants.USER_CERT_1);
        assertNotSame(u , ou.get());
        assertEquals(u, ou.get());
        assertEquals(u.getEmailAddress(), ou.get().getEmailAddress());
        assertEquals(u.getCertificate().getCertificateId(), ou.get().getCertificate().getCertificateId());
        // some database timestamp objects does not store miliseconds
        assertEquals(u.getCertificate().getValidFrom().truncatedTo(ChronoUnit.MINUTES), ou.get().getCertificate().getValidFrom().truncatedTo(ChronoUnit.MINUTES));
        assertEquals(u.getCertificate().getValidTo().truncatedTo(ChronoUnit.MINUTES), ou.get().getCertificate().getValidTo().truncatedTo(ChronoUnit.MINUTES));
    }

    @Test
    public void findUsernameUserByIdentifier() {
        // set
        DBUser u = TestDBUtils.createDBUserByUsername(TestConstants.USERNAME_1);

        // execute
        testInstance.persistFlushDetach(u);

        //test
        Optional<DBUser> ou = testInstance.findUserByIdentifier(TestConstants.USERNAME_1);
        assertNotSame(u , ou.get());
        assertEquals(u, ou.get());
        assertEquals(u.getEmailAddress(), ou.get().getEmailAddress());
    }

    @Test
    public void deleteUserWithCertificate() {
        // givem
        DBUser u = TestDBUtils.createDBUserByCertificate(UUID.randomUUID().toString());
        testInstance.persistFlushDetach(u);
        assertNotNull(u.getId());

        // when then
        testInstance.removeById(u.getId());
        //test
        Optional<DBUser> ou = testInstance.findUserByIdentifier(u.getCertificate().getCertificateId());
        assertFalse(ou.isPresent());

    }

    @Test
    public void findBlankUsernameUser() {
        // set
        DBUser u = TestDBUtils.createDBUserByUsername(TestConstants.USERNAME_1);

        // execute
        testInstance.persistFlushDetach(u);

        //test
        Optional<DBUser> ou = testInstance.findUserByIdentifier(null);
        assertFalse(ou.isPresent());

        ou = testInstance.findUserByIdentifier("");
        assertFalse(ou.isPresent());

        ou = testInstance.findUserByIdentifier(" ");
        assertFalse(ou.isPresent());
    }

    @Test
    public void findNotExistsUsernameUser() {
        // set
        DBUser u = TestDBUtils.createDBUserByUsername(TestConstants.USERNAME_1);

        // execute
        testInstance.persistFlushDetach(u);

        //test
        Optional<DBUser> ou = testInstance.findUserByIdentifier(TestConstants.USERNAME_2);
        assertFalse(ou.isPresent());
    }

    @Test
    public void findCaseInsensitiveUsernameUser() {
        // set
        DBUser u = TestDBUtils.createDBUserByUsername(TestConstants.USERNAME_1.toLowerCase());

        // execute
        testInstance.persistFlushDetach(u);

        //test
        Optional<DBUser> ou = testInstance.findUserByUsername(TestConstants.USERNAME_1.toUpperCase());
        assertTrue(ou.isPresent());
        assertEquals(u, ou.get());
        assertEquals(u.getEmailAddress(), ou.get().getEmailAddress());

    }

    @Test
    public void testValidateUsersForDeleteOKScenario() {
        // set
        DBUser u = TestDBUtils.createDBUserByUsername(TestConstants.USERNAME_1.toLowerCase());
        testInstance.persistFlushDetach(u);

        // execute
        List<DBUserDeleteValidation> lst = testInstance.validateUsersForDelete(Collections.singletonList(u.getId()));
        assertTrue(lst.isEmpty());
    }

    @Test
    public void testValidateUsersForDeleteUserIsOwner() {
        // set
        DBUser u = TestDBUtils.createDBUserByUsername(TestConstants.USERNAME_1.toLowerCase());
        DBServiceGroup sg = TestDBUtils.createDBServiceGroup();
        testInstance.persistFlushDetach(u);
        sg.addUser(u);

        serviceGroupDao.persistFlushDetach(sg);


        // execute
        List<DBUserDeleteValidation> lst = testInstance.validateUsersForDelete(Collections.singletonList(u.getId()));
        assertEquals(1, lst.size());
        assertEquals(u.getUsername(), lst.get(0).getUsername());
        assertEquals(1, lst.get(0).getCount().intValue());
    }
}