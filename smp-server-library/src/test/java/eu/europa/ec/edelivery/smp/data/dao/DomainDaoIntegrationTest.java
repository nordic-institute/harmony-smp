/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
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

import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.testutil.TestConstants;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Purpose of class is to test all resource methods with database.
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
class DomainDaoIntegrationTest extends AbstractBaseDao {

    @Autowired
    DomainDao testInstance;

    @BeforeEach
    public void prepareDatabase() {
        testUtilsDao.clearData();
    }


    @Test
    void persistDomain() {
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

    @Test
    void persistDuplicateDomain() {
        // set
        DBDomain d = new DBDomain();
        d.setDomainCode(TestConstants.TEST_DOMAIN_CODE_1);
        testInstance.persistFlushDetach(d);
        DBDomain d2 = new DBDomain();
        d2.setDomainCode(TestConstants.TEST_DOMAIN_CODE_1);

        // execute
        Exception exception = assertThrows(Exception.class, () -> testInstance.persistFlushDetach(d2));
        assertThat(exception.getMessage(), CoreMatchers.containsString("ConstraintViolationException"));
    }

    @Test
    void getTheOnlyDomainNoDomain() {

        // execute
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> testInstance.getTheOnlyDomain());
        assertEquals(ErrorCode.NO_DOMAIN.getMessage(), exception.getMessage());
    }

    @Test
    void getTheOnlyDomainMultipleDomain() {
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
        assertFalse(res.isPresent());
    }

    @Test
    void getDomainByCodeExists() {
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
    void getDomainByCodeNotExists() {
        // test
        Optional<DBDomain> res = testInstance.getDomainByCode(TestConstants.TEST_DOMAIN_CODE_1);
        assertFalse(res.isPresent());
    }

    @Test
    void removeByDomainCodeExists() {
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
    void removeByDomainCodeNotExists() {
        // set

        // test
        boolean res = testInstance.removeByDomainCode(TestConstants.TEST_DOMAIN_CODE_1);
        assertFalse(res);
    }

    @Test
    void removeByDomainById() {
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
    void testValidateDeleteOKScenario() {
        // set
        DBDomain d = TestDBUtils.createDBDomain();
        testInstance.persistFlushDetach(d);

        // execute
        Long cnt = testInstance.getResourceCountForDomain(d.getId());
        assertEquals(0, cnt.intValue());
    }

    @Test
    void testValidateDeleteHasResources() {
        // set
        testUtilsDao.createSubresources();
        DBDomain d = testUtilsDao.getD1();
        Long cnt = testInstance.getResourceCountForDomain(d.getId());

        assertEquals(1, cnt.intValue());
    }
}
