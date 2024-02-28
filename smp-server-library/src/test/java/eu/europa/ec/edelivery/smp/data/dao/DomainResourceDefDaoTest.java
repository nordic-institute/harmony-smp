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

import eu.europa.ec.edelivery.smp.data.model.DBDomainResourceDef;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class DomainResourceDefDaoTest extends AbstractBaseDao {
    @Autowired
    DomainResourceDefDao testInstance;

    @BeforeEach
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

        Optional<DBDomainResourceDef> result = testInstance.getResourceDefConfigurationForDomainCodeAndResourceDefCtx(TEST_DOMAIN_CODE_2, TEST_RESOURCE_DEF_SMP10_URL);

        assertTrue(result.isPresent());
        assertEquals(testUtilsDao.getDomainResourceDefD2R1().getId(), result.get().getId());
    }

    @Test
    public void getResourceDefConfigurationForDomainAndResourceDefNotExist() {

        Optional<DBDomainResourceDef> result = testInstance.getResourceDefConfigurationForDomainCodeAndResourceDefCtx(TEST_DOMAIN_CODE_2, TEST_RESOURCE_DEF_CPP);

        assertFalse(result.isPresent());

    }
}
