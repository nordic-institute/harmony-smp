package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.model.doc.DBDocument;
import eu.europa.ec.edelivery.smp.data.model.doc.DBDocumentVersion;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class DocumentDaoTest extends AbstractBaseDao {

    @Autowired
    DocumentDao testInstance;

    @Before
    public void prepareDatabase() {
        testUtilsDao.clearData();
        testUtilsDao.createSubresources();
    }

    @Test
    public void testPersistDocument() {

        DBDocument document = testUtilsDao.createAndPersistDocument(2);

        assertNotNull(document.getId());
        assertEquals(2, document.getDocumentVersions().size());
        assertEquals(1, document.getCurrentVersion());
    }


    @Test
    public void getDocumentForResource() {
        Optional<DBDocument> result = testInstance.getDocumentForResource(testUtilsDao.getResourceD1G1RD1());

        assertTrue(result.isPresent());
        assertEquals(testUtilsDao.getDocumentD1G1RD1(), result.get());
        // the default setup  createResources  sets two versions (0 and 1 ) with current version 1
        assertEquals(1, result.get().getCurrentVersion());
    }


    @Test
    public void getDocumentVersionsForResource() {
        List<DBDocumentVersion> result = testInstance.getDocumentVersionsForResource(testUtilsDao.getResourceD1G1RD1());

        assertEquals(2, result.size());
    }

    @Test
    public void getCurrentDocumentVersionForResource() {

        Optional<DBDocumentVersion> result = testInstance.getCurrentDocumentVersionForResource(testUtilsDao.getResourceD1G1RD1());

        assertTrue(result.isPresent());
        // the default setup  createResources  sets two versions (0 and 1 ) with current version 1
        assertEquals(1, result.get().getVersion());
        assertEquals(testUtilsDao.getDocumentD1G1RD1().getDocumentVersions().get(1), result.get());
    }


    @Test
    public void getDocumentVersionsForSubresource() {
        List<DBDocumentVersion> result = testInstance.getDocumentVersionsForSubresource(testUtilsDao.getSubresourceD1G1RD1_S1());

        assertEquals(2, result.size());
    }

    @Test
    public void getCurrentDocumentVersionForSubresource() {

        Optional<DBDocumentVersion> result = testInstance.getCurrentDocumentVersionForSubresource(testUtilsDao.getSubresourceD1G1RD1_S1());

        assertTrue(result.isPresent());
        // the default setup  createResources  sets two versions (0 and 1 ) with current version 1
        assertEquals(1, result.get().getVersion());
        assertEquals(testUtilsDao.getDocumentD1G1RD1_S1().getDocumentVersions().get(1), result.get());
    }
}
