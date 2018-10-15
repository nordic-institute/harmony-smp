package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.config.H2JPATestConfiguration;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.testutil.TestConstants;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.INVALID_USER_NO_IDENTIFIERS;
import static org.junit.Assert.*;


/**
 *  Purpose of class is to test all resource methods with database.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class UserDaoIntegrationTest extends AbstractBaseDao{

    @Autowired
    UserDao testInstance;

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
        assertTrue(u!=ou.get());
        assertEquals(u, ou.get());
        assertEquals(u.getEmail(), ou.get().getEmail());
        assertEquals(u.getPassword(), ou.get().getPassword());
        assertEquals(u.getRole(), ou.get().getRole());
        assertEquals(u.getUsername(), ou.get().getUsername());
    }

    @Test
    public void persistUserWithCertificate() {
        // set
        DBUser u = TestDBUtils.createDBUserByCertificate(TestConstants.USER_CERT_1);

        // execute
        testInstance.persistFlushDetach(u);

        //test
        Optional<DBUser> ou = testInstance.findUserByCertificateId(TestConstants.USER_CERT_1);
        assertTrue(u!=ou.get());
        assertEquals(u, ou.get());
        assertEquals(u.getEmail(), ou.get().getEmail());
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
        assertTrue(u!=ou.get());
        assertEquals(u, ou.get());
        assertEquals(u.getEmail(), ou.get().getEmail());
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
        assertTrue(u!=ou.get());
        assertEquals(u, ou.get());
        assertEquals(u.getEmail(), ou.get().getEmail());
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
        assertEquals(u.getEmail(), ou.get().getEmail());

    }
}