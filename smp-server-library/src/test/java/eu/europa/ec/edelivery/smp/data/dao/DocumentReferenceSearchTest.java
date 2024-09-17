package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DocumentReferenceSearchTest extends AbstractBaseDao {

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

        List<DBResource> result = testInstance.getSearchReferenceDocumentResources(dbResource, null, null, -1, -1);
        long countResult = testInstance.getSearchReferenceDocumentResourcesCount(dbResource, null, null);
        assertEquals(0, result.size());
        assertEquals(0, countResult);
    }

    @ParameterizedTest
    @CsvSource({"All resources, , , 2",
            "Filter by identifier, pubPubPub,, 1",
            "Filter by scheme, ,5-5-5, 1"
    })
    void testGetSearchReferenceDocumentResources(String desc, String identifier, String scheme, int expectedCount) {
        // given
        DBResource dbResource = testUtilsDao.getResourceSearchPrivPrivPriv();
        assertNotNull(dbResource);

        List<DBResource> result = testInstance.getSearchReferenceDocumentResources(dbResource, identifier, scheme, -1, -1);
        long countResult = testInstance.getSearchReferenceDocumentResourcesCount(dbResource, identifier, scheme);

        // the same group and one from co-group which is public
        assertEquals(expectedCount, result.size());
        assertEquals(expectedCount, (int)countResult);
    }
}
