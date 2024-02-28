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

import eu.europa.ec.edelivery.smp.data.model.ext.DBResourceDef;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.jupiter.api.BeforeEach;
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
public class ResourceDefDaoTest extends AbstractBaseDao {

    @Autowired
    ResourceDefDao testInstance;

    @BeforeEach
    public void init() {
        testUtilsDao.clearData();
        testUtilsDao.createExtension();
        testInstance.clearPersistenceContext();
    }

    @Test
    public void persistTest() {
        // set
        String testName = "TestClassName";
        DBResourceDef testData = TestDBUtils.createDBResourceDef(testName);
        testData.setExtension(testUtilsDao.getExtension());
        // execute
        testInstance.persistFlushDetach(testData);
        // test
        List<DBResourceDef> res = testInstance.getAllResourceDef();
        assertEquals(1, res.size());
        assertEquals(testData.getId(), res.get(0).getId()); // test equal method
    }


    @Test
    public void persistDuplicateUrlError() {
        // set
        String testURL = "TestClassName";
        DBResourceDef testData1 = TestDBUtils.createDBResourceDef("resourceDef1");
        DBResourceDef testData2 = TestDBUtils.createDBResourceDef("resourceDef2");
        testData1.setExtension(testUtilsDao.getExtension());
        testData2.setExtension(testUtilsDao.getExtension());

        testData1.setUrlSegment(testURL);
        testData2.setUrlSegment(testURL);
        testInstance.persistFlushDetach(testData1);

        // execute
        PersistenceException result = assertThrows(PersistenceException.class, () -> testInstance.persistFlushDetach(testData2));
        assertEquals("org.hibernate.exception.ConstraintViolationException: could not execute statement", result.getMessage());
    }

    @Test
    public void getExtensionByImplementationName() {
        String testName = "TestClassName";
        DBResourceDef testData = TestDBUtils.createDBResourceDef(testName);
        testData.setExtension(testUtilsDao.getExtension());
        testInstance.persistFlushDetach(testData);

        // test
        Optional<DBResourceDef> res = testInstance.getResourceDefByIdentifierAndExtension(testName, testUtilsDao.getExtension());
        assertTrue(res.isPresent());
        assertEquals(testName, res.get().getIdentifier());
    }

    @Test
    public void getResourceDefByURLSegment() {
        String testUrlSegment = "testUrlSegment";
        DBResourceDef testData = TestDBUtils.createDBResourceDef("Code");
        testData.setUrlSegment(testUrlSegment);
        testData.setExtension(testUtilsDao.getExtension());
        testInstance.persistFlushDetach(testData);
        // test
        Optional<DBResourceDef> res = testInstance.getResourceDefByURLSegment(testUrlSegment);
        assertTrue(res.isPresent());
        assertEquals(testUrlSegment, res.get().getUrlSegment());
    }
}
