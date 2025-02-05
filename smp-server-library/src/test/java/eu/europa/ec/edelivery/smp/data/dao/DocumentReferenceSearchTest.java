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

import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBSearchReferenceDocumentMapping;
import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DocumentReferenceSearchTest extends AbstractBaseDao {

    @Autowired
    DocumentDao testInstance;

    @BeforeEach
    public void prepareDatabase() {
        // setup initial data!
        testUtilsDao.clearData();
        testUtilsDao.createResourcesForSearch();
    }

    @Test
    @Transactional
    void testGetSearchReferenceDocumentResourcesNoPublic() {
        // given
        DBResource dbResource = testUtilsDao.getResourceSearchPubPubPub();
        assertNotNull(dbResource);

        List<DBSearchReferenceDocumentMapping> result = testInstance.getSearchReferenceDocumentResources(dbResource, null, null, -1, -1);
        long countResult = testInstance.getSearchReferenceDocumentResourcesCount(dbResource, null, null);
        assertEquals(0, result.size());
        assertEquals(0, countResult);
    }

    @Test
    @Transactional
    void testGetSearchReferenceDocumentSubresourcesNoPublic() {
        // given public resource does not have any suitable public subresources for search
        DBSubresource dbSubresource = testUtilsDao.getSubresourceSearchPubPubPub();
        assertNotNull(dbSubresource);

        List<DBSearchReferenceDocumentMapping> result = testInstance.getSearchReferenceDocumentSubresource(
                dbSubresource, null, null, null, null, -1, -1);
        long countResult = testInstance.getSearchReferenceDocumentSubresourceCount(dbSubresource, null, null, null, null);
        assertEquals(0, result.size());
        assertEquals(0, countResult);
    }

    @ParameterizedTest
    @CsvSource({"All resources, , , 3",
            "Filter by identifier, 'pubPubPub',, 1",
            "Filter partial by identifier, 'pub',, 3",
            "Filter by scheme, ,'5-5-5', 1",
            "Filter partial by scheme, ,'5-5', 1"
    })
    void testGetSearchReferenceDocumentResources(String desc, String identifier, String scheme, int expectedCount) {
        // given
        System.out.println("desc = " + desc);
        DBResource dbResource = testUtilsDao.getResourceSearchPrivPrivPriv();
        assertNotNull(dbResource);

        List<DBSearchReferenceDocumentMapping> result = testInstance.getSearchReferenceDocumentResources(dbResource, identifier, scheme, -1, -1);
        long countResult = testInstance.getSearchReferenceDocumentResourcesCount(dbResource, identifier, scheme);

        // the same group and one from co-group which is public
        result.forEach(System.out::println);
        assertEquals(expectedCount, result.size());
        assertEquals(expectedCount, (int)countResult);
    }

    @ParameterizedTest
    @CsvSource({"All resources,,,,,3",
            "Filter by sidentifier,,,'pubPubPub',, 1",
            "Filter partial by sidentifier,,, 'pub',, 3",
            "Filter by sscheme,,,,'5-5-5', 1",
            "Filter partial by sscheme,,,,'5-5', 1",
            "Filter by ridentifier,'pubPubPub',,,,1",
            "Filter partial by ridentifier, 'pub',,,,3",
            "Filter by rscheme,,'5-5-5',,,1",
            "Filter partial by rscheme,,'5-5',,,1",
            "Filter partial by both identifier, 'pub',,sub,,3",
            "Filter partial by both identifier, 'pub',,subres-priv,,2"
    })
    void testGetSearchReferenceDocumentSubResources(String desc,String ridentifier, String rscheme,  String sidentifier, String sscheme, int expectedCount) {
        // given
        System.out.println("desc = " + desc);
        DBSubresource dbSubresource = testUtilsDao.getSubresourceSearchPrivPrivPriv();
        assertNotNull(dbSubresource);

        List<DBSearchReferenceDocumentMapping> result = testInstance.getSearchReferenceDocumentSubresource(dbSubresource, ridentifier, rscheme, sidentifier, sscheme, -1, -1);
        long countResult = testInstance.getSearchReferenceDocumentSubresourceCount(dbSubresource, ridentifier, rscheme, sidentifier, sscheme);

        // the same group and one from co-group which is public
        result.forEach(System.out::println);
        assertEquals(expectedCount, result.size());
        assertEquals(expectedCount, (int)countResult);
    }
}
