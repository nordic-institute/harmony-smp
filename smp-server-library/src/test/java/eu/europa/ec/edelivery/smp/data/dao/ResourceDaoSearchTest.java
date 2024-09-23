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


import eu.europa.ec.edelivery.smp.data.enums.VisibilityType;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResourceFilter;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Purpose of class is to test all resource methods with database.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */

class ResourceDaoSearchTest extends AbstractBaseDao {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceDaoSearchTest.class);
    @Autowired
    ResourceDao testInstance;

    @BeforeEach
    public void prepareDatabase() {
        // setup initial data!
        testUtilsDao.clearData();
        testUtilsDao.createResourcesForSearch();
    }

    @Test
    @Transactional
    void getAllPublicResources() {
        List<DBResource> allResources = testInstance.getResourcesForFilter(-1, -1, DBResourceFilter.createBuilder().build());
        assertEquals(8, allResources.size());

        // only one group is public -
        List<ResourceDao.DBResourceWrapper> result = testInstance.getPublicResourcesSearch(-1, -1, null, null, null, null, null);
        assertEquals(1, result.size());
        assertResources(result, "1-1-1::pubPubPub");

        // user1 (admin) and user2 (viewer) are members of all resources
        result = testInstance.getPublicResourcesSearch(-1, -1, testUtilsDao.getUser2(), null, null, null, null);
        assertEquals(8, result.size());

        result = testInstance.getPublicResourcesSearch(-1, -1, testUtilsDao.getUser1(), null, "pubPub", null, null);
        assertEquals(2, result.size());
        result.forEach(resource -> assertThat(resource.getDbResource().getIdentifierValue(), CoreMatchers.containsString("pubPub")));

        result = testInstance.getPublicResourcesSearch(-1, -1, testUtilsDao.getUser1(), "1-1", null, null, null);
        assertEquals(1, result.size());
        result.forEach(resource -> assertThat(resource.getDbResource().getIdentifierScheme(), CoreMatchers.containsString("1-1")));

        result = testInstance.getPublicResourcesSearch(-1, -1, testUtilsDao.getUser1(), "1-1", "priv", null, null);
        assertEquals(0, result.size());

        result = testInstance.getPublicResourcesSearch(-1, -1, testUtilsDao.getUser2(), null, null, null, null);
        assertEquals(8, result.size());


        // user3 is direct member of private domain - can see only public resource on public groups
        result = testInstance.getPublicResourcesSearch(-1, -1, testUtilsDao.getUser3(), null, null, null, null);
        assertResources(result, "1-1-1::pubPubPub", "5-5-5::privPubPub");

        // user4 is direct member of private group in private domain
        result = testInstance.getPublicResourcesSearch(-1, -1, testUtilsDao.getUser4(), null, null, null, null);
        assertResources(result, "1-1-1::pubPubPub", "5-5-5::privPubPub", "7-7-7::privPrivPub");

        // user5 is direct member of private resource in  private group in private domain
        result = testInstance.getPublicResourcesSearch(-1, -1, testUtilsDao.getUser5(), null, null, null, null);
        assertResources(result, "1-1-1::pubPubPub", "5-5-5::privPubPub", "7-7-7::privPrivPub", "8-8-8::privPrivPriv");
    }

    public void assertResources(List<ResourceDao.DBResourceWrapper> result, String... resourceIdentifiers) {
        List<String> resultIdentifiers = result.stream().map(val -> val.getDbResource().getIdentifierScheme() + "::" + val.getDbResource().getIdentifierValue()).collect(Collectors.toList());
        System.out.println(resultIdentifiers);
        assertArrayEquals(resourceIdentifiers, resultIdentifiers.stream().toArray());
    }

    @Test
    void getAllPublicResourcesCount() {
        List<DBResource> allResources = testInstance.getResourcesForFilter(-1, -1, DBResourceFilter.createBuilder().build());
        assertEquals(8, allResources.size());

        // only one group is public -
        Long result = testInstance.getPublicResourcesSearchCount(null, null, null, null, null);
        assertEquals(1, result.intValue());

        // user1 (admin) and user2 (viewer) are members of all resources
        result = testInstance.getPublicResourcesSearchCount(testUtilsDao.getUser1(), null, null, null, null);
        assertEquals(8, result.intValue());

        result = testInstance.getPublicResourcesSearchCount(testUtilsDao.getUser1(), null, "pubPub", null, null);
        assertEquals(2, result.intValue());

        result = testInstance.getPublicResourcesSearchCount(testUtilsDao.getUser1(), "1-1", null, null, null);
        assertEquals(1, result.intValue());

        result = testInstance.getPublicResourcesSearchCount(testUtilsDao.getUser1(), "1-1", "priv", null, null);
        assertEquals(0, result.intValue());

        result = testInstance.getPublicResourcesSearchCount(testUtilsDao.getUser2(), null, null, null, null);
        assertEquals(8, result.intValue());

        // user3 is direct member of private domain - can see only public resource on public groups
        result = testInstance.getPublicResourcesSearchCount(testUtilsDao.getUser3(), null, null, null, null);
        assertEquals(2, result.intValue());

        // user4 is direct member of private group in private domain
        result = testInstance.getPublicResourcesSearchCount(testUtilsDao.getUser4(), null, null, null, null);
        assertEquals(3, result.intValue());

        // user5 is direct member of private resource in  private group in private domain
        result = testInstance.getPublicResourcesSearchCount(testUtilsDao.getUser5(), null, null, null, null);
        assertEquals(4, result.intValue());

    }

    @ParameterizedTest
    @CsvSource({ "'Get all public  OK',0088:1234556-1234,test-test-test,,,2",
            "'Search by identifier OK',0088:1234556-1234,test-test-test,0088:1234556-123,, 1",
            "'Search by identifier partial OK',0088:1234556-1234,test-test-test,88:12,, 1",
            "'Search by scheme partial OK',0088:1234556,test-test-test,,-test-, 1",
            "'Search by chars OK',0088:#$%!@#$-1234,test-test-test,#$%!@#,, 1",
            "'Search by '/' OK',0088/1234,test-test-test,88/12,, 1",
            "'Search by '\\' OK',0088\\1234,test-test-test,88\\12,, 1",
    })
    void testGetSearchSlashCharacter(String testDec, String identifierValue, String identifierScheme, String searchValue, String searcScheme, int cnt) {
        LOG.info(testDec);
        // given
        testUtilsDao.createResource(identifierValue,identifierScheme, VisibilityType.PUBLIC, testUtilsDao.getDomainResourceDefD1R1(), testUtilsDao.getGroupD1G1()  );
        // then
        List<ResourceDao.DBResourceWrapper> result = testInstance.getPublicResourcesSearch(-1, -1, null, searcScheme, searchValue, null, null);
        assertEquals(cnt, result.size());
        result.stream().forEach(val -> {
            assertEquals(VisibilityType.PUBLIC, val.getDbResource().getVisibility());
        });
    }

}
