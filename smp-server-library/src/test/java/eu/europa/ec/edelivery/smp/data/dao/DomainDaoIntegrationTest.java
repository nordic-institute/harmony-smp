package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.*;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.testutil.TestConstants;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 *  Purpose of class is to test all resource methods with database.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class DomainDaoIntegrationTest extends AbstractBaseDao {

    @Autowired
    DomainDao testInstance;

    @Autowired
    ResourceDao serviceGroupDao;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void persistDomain() {
        // set
        DBDomain d = new DBDomain();
        d.setDomainCode(TestConstants.TEST_DOMAIN_CODE_1);
        d.setSmlSubdomain(TestConstants.TEST_SML_SUBDOMAIN_CODE_1);
        // execute
        testInstance.persistFlushDetach(d);

        // test
        Optional<DBDomain> res = testInstance.getTheOnlyDomain();
        assertTrue(res.isPresent());
        assertEquals(d, res.get()); // test equal method
    }

    @Test(expected = Exception.class)
    public void persistDuplicateDomain() {
        // set
        DBDomain d = new DBDomain();
        d.setDomainCode(TestConstants.TEST_DOMAIN_CODE_1);
        testInstance.persistFlushDetach(d);
        DBDomain d2 = new DBDomain();
        d2.setDomainCode(TestConstants.TEST_DOMAIN_CODE_1);

        // execute
        testInstance.persistFlushDetach(d2);
    }

    @Test
    public void getTheOnlyDomainNoDomain() {
        // set
        expectedEx.expect(IllegalStateException.class);
        expectedEx.expectMessage(ErrorCode.NO_DOMAIN.getMessage());
        // execute
        testInstance.getTheOnlyDomain();
    }

    @Test
    public void getTheOnlyDomainMultipleDomain() {
        // set
        DBDomain d = new DBDomain();
        d.setDomainCode(TestConstants.TEST_DOMAIN_CODE_1);
        d.setSmlSubdomain(TestConstants.TEST_SML_SUBDOMAIN_CODE_1);
        testInstance.persistFlushDetach(d);
        DBDomain d2 = new DBDomain();
        d2.setDomainCode(TestConstants.TEST_DOMAIN_CODE_2);
        d2.setSmlSubdomain(TestConstants.TEST_SML_SUBDOMAIN_CODE_2);
        testInstance.persistFlushDetach(d2);

        // test
        Optional<DBDomain> res = testInstance.getTheOnlyDomain();
        assertTrue(!res.isPresent());
    }

    @Test
    public void getDomainByCodeExists() {
        // set
        DBDomain d = new DBDomain();
        d.setDomainCode(TestConstants.TEST_DOMAIN_CODE_1);
        d.setSmlSubdomain(TestConstants.TEST_SML_SUBDOMAIN_CODE_2);
        testInstance.persistFlushDetach(d);

        // test
        Optional<DBDomain> res = testInstance.getDomainByCode(TestConstants.TEST_DOMAIN_CODE_1);
        assertTrue(res.isPresent());
        assertEquals(TestConstants.TEST_DOMAIN_CODE_1, res.get().getDomainCode());
    }

    @Test
    public void getDomainByCodeNotExists() {
        // test
        Optional<DBDomain> res = testInstance.getDomainByCode(TestConstants.TEST_DOMAIN_CODE_1);
        assertFalse(res.isPresent());
    }

    @Test
    public void removeByDomainCodeExists() {
        // set

        DBDomain d = new DBDomain();
        d.setDomainCode(TestConstants.TEST_DOMAIN_CODE_1);
        d.setSmlSubdomain(TestConstants.TEST_SML_SUBDOMAIN_CODE_2);
        testInstance.persistFlushDetach(d);
        Optional<DBDomain> optDmn = testInstance.getDomainByCode(TestConstants.TEST_DOMAIN_CODE_1);
        assertTrue(optDmn.isPresent());

        // test
        boolean res = testInstance.removeByDomainCode(TestConstants.TEST_DOMAIN_CODE_1);
        assertTrue(res);
        optDmn = testInstance.getDomainByCode(TestConstants.TEST_DOMAIN_CODE_1);
        assertFalse(optDmn.isPresent());
    }

    @Test
    public void removeByDomainCodeNotExists() {
        // set

        // test
        boolean res = testInstance.removeByDomainCode(TestConstants.TEST_DOMAIN_CODE_1);
        assertFalse(res);
    }

    @Test
    public void removeByDomainById() {
        // set
        DBDomain d = new DBDomain();
        d.setDomainCode(TestConstants.TEST_DOMAIN_CODE_1);
        d.setSmlSubdomain(TestConstants.TEST_SML_SUBDOMAIN_CODE_2);
        testInstance.persistFlushDetach(d);
        testInstance.clearPersistenceContext();
        Optional<DBDomain> optDmn = testInstance.getDomainByCode(TestConstants.TEST_DOMAIN_CODE_1);
        assertTrue(optDmn.isPresent());

        // test
        boolean res = testInstance.removeById(d.getId());
        assertTrue(res);
        optDmn = testInstance.getDomainByCode(TestConstants.TEST_DOMAIN_CODE_1);
        assertFalse(optDmn.isPresent());
    }

    @Test
    public void testValidateUsersForDeleteOKScenario() {
        // set
        DBDomain d = TestDBUtils.createDBDomain();
        testInstance.persistFlushDetach(d);

        // execute
        List<DBDomainDeleteValidation> lst = testInstance.validateDomainsForDelete(Collections.singletonList(d.getId()));
        assertTrue(lst.isEmpty());
    }

    @Test
    @Ignore
    public void testValidateUsersForDeleteUserIsOwner() {
        // set
        DBDomain d = TestDBUtils.createDBDomain();
        testInstance.persistFlushDetach(d);

        DBResource sg = TestDBUtils.createDBResource();
       // sg.addDomain(d);

        serviceGroupDao.persistFlushDetach(sg);

        // execute
        List<DBDomainDeleteValidation> lst = testInstance.validateDomainsForDelete(Collections.singletonList(d.getId()));
        assertEquals(1, lst.size());
        assertEquals(d.getDomainCode(), lst.get(0).getDomainCode());
        assertEquals(1, lst.get(0).getCount().intValue());
    }
}
