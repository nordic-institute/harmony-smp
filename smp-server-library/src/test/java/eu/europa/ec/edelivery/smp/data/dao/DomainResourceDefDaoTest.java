package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.DBDomainResourceDef;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class DomainResourceDefDaoTest extends AbstractBaseDao {
    @Autowired
    DomainResourceDefDao testInstance;

    @Before
    public void prepareDatabase() {
        // setup initial data!
        testUtilsDao.clearData();
        testUtilsDao.createResourceDefinitionsForDomains();
        testInstance.clearPersistenceContext();
    }

    @Test
    public void getResourceDefConfigurationForDomain() {
        // when
        List<DBDomainResourceDef> result = testInstance.getResourceDefConfigurationsForDomain(testUtilsDao.getD1());

        assertEquals(result.size(), 2);
        // definitions are sorted by id!
        assertEquals(testUtilsDao.getDomainResourceDefD1R1().getId(), result.get(0).getId());
        assertEquals(testUtilsDao.getDomainResourceDefD1R2().getId(), result.get(1).getId());
    }

    @Test
    public void getResourceDefConfigurationForDomainAndResourceDef() {

        Optional<DBDomainResourceDef> result = testInstance.getResourceDefConfigurationForDomainAndResourceDef(TEST_DOMAIN_CODE_2, TEST_RESOURCE_DEF_SMP10);

        assertTrue(result.isPresent());
        assertEquals(testUtilsDao.getDomainResourceDefD2R1().getId(), result.get().getId());
    }

    @Test
    public void getResourceDefConfigurationForDomainAndResourceDefNotExist() {

        Optional<DBDomainResourceDef> result = testInstance.getResourceDefConfigurationForDomainAndResourceDef(TEST_DOMAIN_CODE_2, TEST_RESOURCE_DEF_CPP);

        assertFalse(result.isPresent());

    }
}
