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

import eu.europa.ec.edelivery.smp.data.model.doc.DBDocumentReferenceData;
import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
import eu.europa.ec.edelivery.smp.identifiers.Identifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.data.enums.DocumentVersionStatusType.PUBLISHED;
import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

class SubresourceDaoTest extends AbstractBaseDao {
    @Autowired
    SubresourceDao testInstance;

    @BeforeEach
    public void prepareDatabase() {
        // setup initial data!
        testUtilsDao.clearData();
        testUtilsDao.createSubresources();
    }

    @Test
    void getSubResource() {
        Identifier suberesId = new Identifier(TEST_DOC_ID_1, TEST_DOC_SCHEMA_1);
        Optional<DBSubresource> subresource = testInstance.getSubResource(suberesId,
                testUtilsDao.getResourceD1G1RD1(), TEST_SUBRESOURCE_DEF_SMP10_URL, false);

        assertTrue(subresource.isPresent());
    }

    @Test
    void getSubResourceWrongResource() {
        Identifier suberesId = new Identifier(TEST_DOC_ID_1, TEST_DOC_SCHEMA_1);
        Optional<DBSubresource> subresource = testInstance.getSubResource(suberesId,
                testUtilsDao.getResourceD2G1RD1(), TEST_SUBRESOURCE_DEF_SMP10_URL, false);

        assertFalse(subresource.isPresent());
    }

    @Test
    void getSubResourcesForResource() {
        Identifier identifier = new Identifier(TEST_SG_ID_1, TEST_SG_SCHEMA_1);

        List<DBSubresource> subresourceList = testInstance.getSubResourcesForResource(identifier, TEST_SUBRESOURCE_DEF_SMP10_ID);

        assertEquals(1, subresourceList.size());
    }

    @Test
    public void testGetDocumentReferenceDataNotAReference() {
        DBDocumentReferenceData dcRef = testInstance.getDocumentReferenceData(testUtilsDao.getSubresourceD1G1RD1_S1());

        assertNotNull(dcRef);
        assertFalse(dcRef.isSharingEnabled());
        assertEquals(0, dcRef.getReferencedByCount());
    }

    @Test
    public void testGetDocumentReferenceDataReference() {
        // given
        DBSubresource subresourceTarget = testUtilsDao.createSubresource(testUtilsDao.getResourceD1G1RD1(),
                "target-reference", "doc-reference-scheme",
                PUBLISHED, testUtilsDao.getSubresourceDefSmpMetadata(), true);

        testUtilsDao.createSubresource(testUtilsDao.getResourceD1G1RD1(),
                "using-reference-01", "doc-reference-scheme",
                PUBLISHED, testUtilsDao.getSubresourceDefSmpMetadata(), subresourceTarget.getDocument());

        testUtilsDao.createSubresource(testUtilsDao.getResourceD1G1RD1(),
                "using-reference-02", "doc-reference-scheme",
                PUBLISHED, testUtilsDao.getSubresourceDefSmpMetadata(), subresourceTarget.getDocument());

        // when ( - the target resource is a reference and has one document which is using it)
        DBDocumentReferenceData dcRef = testInstance.getDocumentReferenceData(subresourceTarget);

        assertNotNull(dcRef);
        assertTrue(dcRef.isSharingEnabled());
        assertEquals(2, dcRef.getReferencedByCount());
    }

    @Test
    public void testGetDocumentReferenceDataUsingReference() {
        // given
        DBSubresource subresourceTarget = testUtilsDao.createSubresource(testUtilsDao.getResourceD1G1RD1(),
                "target-reference", "doc-reference-scheme",
                PUBLISHED, testUtilsDao.getSubresourceDefSmpMetadata(), true);

        DBSubresource subresourceUsingTarget = testUtilsDao.createSubresource(testUtilsDao.getResourceD1G1RD1(),
                "using-reference-01", "doc-reference-scheme",
                PUBLISHED, testUtilsDao.getSubresourceDefSmpMetadata(), subresourceTarget.getDocument());

        // when ( - the target resource is a reference and has one document which is using it)
        DBDocumentReferenceData dcRef = testInstance.getDocumentReferenceData(subresourceUsingTarget);

        assertNotNull(dcRef);
        assertFalse(dcRef.isSharingEnabled());
        assertEquals(0, dcRef.getReferencedByCount());
        assertEquals(subresourceTarget.getDocument().getId(), dcRef.getReferencedDocumentId());
        assertEquals("http://referencedocument", dcRef.getReferenceUrlPath());
    }
}
