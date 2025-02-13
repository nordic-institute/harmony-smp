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


import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBGroup;
import eu.europa.ec.edelivery.smp.data.model.doc.DBDocument;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResourceFilter;
import eu.europa.ec.edelivery.smp.data.model.ext.DBResourceDef;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Purpose of class is to test all resource methods with database.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */

class ResourceDaoTest extends AbstractBaseDao {

    @Autowired
    ResourceDao testInstance;

    @BeforeEach
    public void prepareDatabase() {
        // setup initial data!
        testUtilsDao.clearData();
        testUtilsDao.createResourceMemberships();
    }

    @Test
    @Transactional
    void persistNewResourceWithDocument() {
        String testIdValue = "test-resource-id";
        String testIdSchema = "test-resource-scheme";
        DBResource testData = TestDBUtils.createDBResource(testIdValue, testIdSchema);
        testData.setGroup(testUtilsDao.getGroupD1G1());
        testData.setDomainResourceDef(testUtilsDao.getDomainResourceDefD1R1());

        DBDocument document = TestDBUtils.createDBDocument();
        document.addNewDocumentVersion(TestDBUtils.createDBDocumentVersion(testIdValue, testIdSchema));
        testData.setDocument(document);

        testInstance.persistFlushDetach(testData);

        Optional<DBResource> optResult = testInstance.getResource(testIdValue, testIdSchema, testUtilsDao.getResourceDefSmp(), testUtilsDao.getD1());

        assertTrue(optResult.isPresent());
        assertNotNull(optResult.get().getDocument());
        assertNotNull(optResult.get().getDocument().getId());
        assertEquals(1, optResult.get().getDocument().getCurrentVersion());
        assertEquals(1, optResult.get().getDocument().getDocumentVersions().size());
        assertNotNull(optResult.get().getDocument().getDocumentVersions().get(0).getId());
        assertEquals(1, optResult.get().getDocument().getDocumentVersions().get(0).getVersion());
    }

    @Test
    @Transactional
    void persistNewVersionToResourceWithDocument() {
        Optional<DBResource> optResource = testInstance.getResource(TEST_SG_ID_1, TEST_SG_SCHEMA_1,
                testUtilsDao.getResourceDefSmp(), testUtilsDao.getD1());
        assertTrue(optResource.isPresent());
        DBResource resource = testInstance.find(optResource.get().getId());

        int docCount = resource.getDocument().getDocumentVersions().size();
        int docVersion = resource.getDocument().getCurrentVersion();

        resource.getDocument().addNewDocumentVersion(TestDBUtils.createDBDocumentVersion(TEST_SG_ID_1, TEST_SG_SCHEMA_1));

        testInstance.persistFlushDetach(resource);
        testInstance.clearPersistenceContext();

        Optional<DBResource> optResult = testInstance.getResource(TEST_SG_ID_1, TEST_SG_SCHEMA_1, testUtilsDao.getResourceDefSmp(), testUtilsDao.getD1());

        assertTrue(optResult.isPresent());
        assertNotNull(optResult.get().getDocument());
        assertEquals(docVersion , optResult.get().getDocument().getCurrentVersion());
        assertEquals(docCount + 1, optResult.get().getDocument().getDocumentVersions().size());
    }

    @Test
    void getResourceOK() {
        Optional<DBResource> optResource = testInstance.getResource(TEST_SG_ID_1, TEST_SG_SCHEMA_1, testUtilsDao.getResourceDefSmp(), testUtilsDao.getD1());
        assertTrue(optResource.isPresent());
        assertEquals(testUtilsDao.getResourceD1G1RD1().getId(), optResource.get().getId());
    }

    @Test
    void getResourceOKNullSchema() {
        Optional<DBResource> optResource = testInstance.getResource(TEST_SG_ID_2, null, testUtilsDao.getResourceDefSmp(), testUtilsDao.getD2());
        assertTrue(optResource.isPresent());
        assertEquals(testUtilsDao.getResourceD2G1RD1().getId(), optResource.get().getId());
    }

    @Test
    void getResourceNotExists() {
        Optional<DBResource> optResource = testInstance.getResource(TEST_SG_ID_1, "WrongSchema", testUtilsDao.getResourceDefSmp(), testUtilsDao.getD1());
        assertFalse(optResource.isPresent());
    }

    @Test
    void getResourceWrongDomain() {
        Optional<DBResource> optResource = testInstance.getResource(TEST_SG_ID_1, TEST_SG_SCHEMA_1, testUtilsDao.getResourceDefSmp(), testUtilsDao.getD2());
        assertFalse(optResource.isPresent());
    }

    @Test
    void getResourceWrongResourceDef() {
        Optional<DBResource> optResource = testInstance.getResource(TEST_SG_ID_1, TEST_SG_SCHEMA_1, testUtilsDao.getResourceDefCpp(), testUtilsDao.getD1());
        assertFalse(optResource.isPresent());
    }

    @Test
    void deleteResourceSimpleOK() {
        Optional<DBResource> optResource = testInstance.getResource(TEST_SG_ID_1, TEST_SG_SCHEMA_1, testUtilsDao.getResourceDefSmp(), testUtilsDao.getD1());
        assertTrue(optResource.isPresent());
        // then
        testInstance.remove(optResource.get());
        Optional<DBResource> optResult = testInstance.getResource(TEST_SG_ID_1, TEST_SG_SCHEMA_1, testUtilsDao.getResourceDefSmp(), testUtilsDao.getD1());
        assertFalse(optResult.isPresent());
    }

    @Test
    void deleteResourceJoinTableOK() {
        testUtilsDao.createSubresources();
        Optional<DBResource> optResource = testInstance.getResource(TEST_SG_ID_1, TEST_SG_SCHEMA_1, testUtilsDao.getResourceDefSmp(), testUtilsDao.getD1());
        assertTrue(optResource.isPresent());
        // then
        testInstance.remove(optResource.get());
        Optional<DBResource> optResult = testInstance.getResource(TEST_SG_ID_1, TEST_SG_SCHEMA_1, testUtilsDao.getResourceDefSmp(), testUtilsDao.getD1());
        assertFalse(optResult.isPresent());
    }


    @Test
    void getAllPublicResources() {
        List<DBResource> result = testInstance.getResourcesForFilter(-1, -1, creatResourceFilter(null, null, null));
        //System.out.println(result.get(0));
        assertEquals(2, result.size());

        result = testInstance.getResourcesForFilter(-1, -1, DBResourceFilter.createBuilder().identifierFilter("test").build());
        assertEquals(2, result.size());

        result = testInstance.getResourcesForFilter(-1, -1, DBResourceFilter.createBuilder().identifierFilter("actorid").build());
        assertEquals(1, result.size());

        result = testInstance.getResourcesForFilter(0, 1, creatResourceFilter(null, null, null));
        assertEquals(1, result.size());

        result = testInstance.getResourcesForFilter(-1, -1, creatResourceFilter(testUtilsDao.getGroupD1G1(), null, null));
        assertEquals(1, result.size());

        result = testInstance.getResourcesForFilter(-1, -1, creatResourceFilter(null, testUtilsDao.getD1(), null));
        assertEquals(1, result.size());

        result = testInstance.getResourcesForFilter(-1, -1, creatResourceFilter(null, null, testUtilsDao.getResourceDefSmp()));
        assertEquals(2, result.size());

        result = testInstance.getResourcesForFilter(-1, -1, creatResourceFilter(testUtilsDao.getGroupD1G1(), testUtilsDao.getD1(), testUtilsDao.getResourceDefSmp()));
        assertEquals(1, result.size());

        result = testInstance.getResourcesForFilter(-1, -1, creatResourceFilter(testUtilsDao.getGroupD1G1(),
                testUtilsDao.getD1(),
                testUtilsDao.getResourceDefSmp(), testUtilsDao.getUser1(), MembershipRoleType.ADMIN));
        assertEquals(1, result.size());

        result = testInstance.getResourcesForFilter(-1, -1, creatResourceFilter(testUtilsDao.getGroupD1G1(),
                testUtilsDao.getD1(),
                testUtilsDao.getResourceDefSmp(),
                testUtilsDao.getUser1(), MembershipRoleType.ADMIN));
        assertEquals(1, result.size());


        result = testInstance.getResourcesForFilter(-1, -1, creatResourceFilter(testUtilsDao.getGroupD1G1(),
                testUtilsDao.getD1(),
                testUtilsDao.getResourceDefSmp(),
                testUtilsDao.getUser2(), MembershipRoleType.ADMIN));
        assertEquals(0, result.size());
    }


    /**
     * test filter. - TODO when moving to JUNIT5 parametrize this method!
     */
    @Test
    void getAllResourcesCount() {

        Long result = testInstance.getResourcesForFilterCount(creatResourceFilter(null, null, null));
        assertEquals(2, result.intValue());
        result = testInstance.getResourcesForFilterCount(creatResourceFilter(testUtilsDao.getGroupD1G1(), null, null));
        assertEquals(1, result.intValue());

        result = testInstance.getResourcesForFilterCount(DBResourceFilter.createBuilder().identifierFilter("test").build());
        assertEquals(2, result.intValue());

        result = testInstance.getResourcesForFilterCount(DBResourceFilter.createBuilder().identifierFilter("actorid").build());
        assertEquals(1, result.intValue());

        result = testInstance.getResourcesForFilterCount(creatResourceFilter(null, testUtilsDao.getD1(), null));
        assertEquals(1, result.intValue());

        result = testInstance.getResourcesForFilterCount(creatResourceFilter(null, null, testUtilsDao.getResourceDefSmp()));
        assertEquals(2, result.intValue());

        result = testInstance.getResourcesForFilterCount(creatResourceFilter(testUtilsDao.getGroupD1G1(), testUtilsDao.getD1(), testUtilsDao.getResourceDefSmp()));
        assertEquals(1, result.intValue());

        result = testInstance.getResourcesForFilterCount(creatResourceFilter(testUtilsDao.getGroupD1G1(), testUtilsDao.getD1(), testUtilsDao.getResourceDefSmp()));
        assertEquals(1, result.intValue());

        result = testInstance.getResourcesForFilterCount(creatResourceFilter(testUtilsDao.getGroupD1G1(),
                testUtilsDao.getD1(),
                testUtilsDao.getResourceDefSmp(), testUtilsDao.getUser1(), MembershipRoleType.ADMIN));
        assertEquals(1, result.intValue());

        result = testInstance.getResourcesForFilterCount(creatResourceFilter(testUtilsDao.getGroupD1G1(),
                testUtilsDao.getD1(),
                testUtilsDao.getResourceDefSmp(),
                testUtilsDao.getUser1(), MembershipRoleType.ADMIN));
        assertEquals(1, result.intValue());


        result = testInstance.getResourcesForFilterCount(creatResourceFilter(testUtilsDao.getGroupD1G1(),
                testUtilsDao.getD1(),
                testUtilsDao.getResourceDefSmp(),
                testUtilsDao.getUser2(), MembershipRoleType.ADMIN));
        assertEquals(0, result.intValue());
    }

    protected static DBResourceFilter creatResourceFilter(DBGroup group, DBDomain domain, DBResourceDef resourceDef) {
        return creatResourceFilter(group, domain, resourceDef, null, null);
    }

    protected static DBResourceFilter creatResourceFilter(DBGroup group, DBDomain domain, DBResourceDef resourceDef, DBUser user, MembershipRoleType membershipRoleType) {
        return DBResourceFilter.createBuilder()
                .resourceDef(resourceDef)
                .domain(domain)
                .group(group)
                .user(user)
                .membershipRoleType(membershipRoleType)
                .build();
    }
}
