/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
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

import eu.europa.ec.edelivery.smp.data.model.ext.DBExtension;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
class ExtensionDaoTest extends AbstractBaseDao {

    @Autowired
    ExtensionDao testInstance;


    @Test
    void persistTest() {
        // set
        String testName = "TestClassName";
        DBExtension testData = TestDBUtils.createDBExtension(testName);
        // execute
        testInstance.persistFlushDetach(testData);

        // test
        List<DBExtension> res = testInstance.getAllExtensions();
        assertEquals(1, res.size());
        assertEquals(testData, res.get(0)); // test equal method
    }

    @Test
    void persistDuplicate() {
        // set
        String testName = "TestClassName";
        DBExtension testData = TestDBUtils.createDBExtension(testName);
        testInstance.persistFlushDetach(testData);
        DBExtension testData2 = TestDBUtils.createDBExtension(testName);
        // execute
        PersistenceException result = assertThrows(PersistenceException.class, () -> testInstance.persistFlushDetach(testData2));
        assertEquals("org.hibernate.exception.ConstraintViolationException: could not execute statement", result.getMessage());
    }

    @Test
    void getDomainByIdentifier() {
        String testName = "TestClassNameIdentifier";
        DBExtension testData = TestDBUtils.createDBExtension(testName);
        testInstance.persistFlushDetach(testData);
        // test
        Optional<DBExtension> res = testInstance.getExtensionByIdentifier(testName);
        assertTrue(res.isPresent());
        assertEquals(testName, res.get().getIdentifier());
    }

}
