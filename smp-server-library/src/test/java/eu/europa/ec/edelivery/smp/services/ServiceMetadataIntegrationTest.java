/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.conversion.IdentifierService;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.identifiers.Identifier;
import eu.europa.ec.edelivery.smp.services.spi.SmpXmlSignatureService;
import eu.europa.ec.edelivery.smp.services.ui.UIKeystoreService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;


import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;
import static eu.europa.ec.edelivery.smp.testutil.XmlTestUtils.loadDocumentAsByteArray;
import static eu.europa.ec.edelivery.smp.testutil.XmlTestUtils.marshallToByteArray;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by gutowpa on 15/11/2017.
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
public class ServiceMetadataIntegrationTest extends AbstractServiceIntegrationTest {

    static Identifier PT_ID = null;
    static Identifier DOC_ID = null;

    static {
        PT_ID = new Identifier();
        PT_ID.setValue(TEST_SG_ID_PL2);
        PT_ID.setScheme(TEST_SG_SCHEMA_PL2);
        DOC_ID = new Identifier();
        DOC_ID.setValue(TEST_DOC_ID_PL2);
        DOC_ID.setScheme(TEST_DOC_SCHEMA_PL2);
    }

    Path resourceDirectory = Paths.get("src", "test", "resources", "keystores");

    ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);

    @Autowired
    IdentifierService identifierService;
    @Autowired
    ServiceMetadataService testInstance;
    @Autowired
    UIKeystoreService uiKeystoreService;
    @Autowired
    SmpXmlSignatureService signer;

    @Rule
    public ExpectedException expectedExeption = ExpectedException.none();

    @Before
    @Transactional
    public void prepareDatabase() {
        configurationService = Mockito.spy(configurationService);
        ReflectionTestUtils.setField(uiKeystoreService, "configurationService", configurationService);
        ReflectionTestUtils.setField(signer, "uiKeystoreService", uiKeystoreService);
        ReflectionTestUtils.setField(testInstance, "signer", signer);
        ReflectionTestUtils.setField(testInstance, "identifierService", identifierService);

        identifierService.configureParticipantIdentifierFormatter(null, null, Pattern.compile(".*"));
        // set keystore properties
        File keystoreFile = new File(resourceDirectory.toFile(), "smp-keystore.jks");
        Mockito.doReturn(keystoreFile).when(configurationService).getKeystoreFile();
        Mockito.doReturn(resourceDirectory.toFile()).when(configurationService).getSecurityFolder();
        Mockito.doReturn("test123").when(configurationService).getKeystoreCredentialToken();
        uiKeystoreService.refreshData();

        prepareDatabaseForSingleDomainEnv();
        DBResource sg = new DBResource();
        sg.setIdentifierValue(TEST_SG_ID_PL2.toLowerCase());
        sg.setIdentifierScheme(TEST_SG_SCHEMA_PL2.toLowerCase());
        DBDomain domain = domainDao.getDomainByCode(TEST_DOMAIN_CODE_1).get();
       // sg.addDomain(domain);
        serviceGroupDao.persistFlushDetach(sg);
    }

    @Test
    public void saveAndReadPositiveScenario() throws IOException, TransformerException, JAXBException {
        //given
        byte[] inServiceMetadataXml = loadDocumentAsByteArray(SERVICE_METADATA_XML_PATH);
        //     byte[] expectedSignedServiceMetadataXml = loadDocumentAsByteArray(SIGNED_SERVICE_METADATA_XML_PATH);
        List<Identifier> docIdsBefore = testInstance.findServiceMetadataIdentifiers(PT_ID);
        assertEquals(0, docIdsBefore.size());

        //when
        testInstance.saveServiceMetadata(null, PT_ID, DOC_ID, inServiceMetadataXml);
        List<Identifier> docIdsAfter = testInstance.findServiceMetadataIdentifiers(PT_ID);
        Document outServiceMetadataDoc = testInstance.getServiceMetadataDocument(PT_ID, DOC_ID);

        //then
        assertEquals(1, docIdsAfter.size());
        assertEquals(DOC_ID.getValue().toLowerCase(), docIdsAfter.get(0).getValue()); // normalized
        assertEquals(DOC_ID.getScheme().toLowerCase(), docIdsAfter.get(0).getScheme()); // normalized
        assertEquals("SignedServiceMetadata", outServiceMetadataDoc.getDocumentElement().getTagName());
        // has signature
        assertEquals(1, outServiceMetadataDoc.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature").getLength());
        // has service metadata
        NodeList serviceMetadata = outServiceMetadataDoc.getElementsByTagNameNS("http://docs.oasis-open.org/bdxr/ns/SMP/2016/05", "ServiceMetadata");
        assertEquals(1, serviceMetadata.getLength());
        //        assertArrayEquals(expectedSignedServiceMetadataXml, ServiceMetadataConverter.toByteArray(outServiceMetadataDoc));
    }
/*
    @Test
    public void serviceMetadataNotExistsWhenReading() {

        expectedExeption.expect(SMPRuntimeException.class);
        expectedExeption.expectMessage(ErrorCode.METADATA_NOT_EXISTS.getMessage(SERVICE_GROUP_ID.getValue().toLowerCase(),
                SERVICE_GROUP_ID.getScheme().toLowerCase(), DOC_ID.getValue().toLowerCase(), DOC_ID.getScheme().toLowerCase()));

        testInstance.getServiceMetadataDocument(SERVICE_GROUP_ID, DOC_ID);
    }


    @Test
    public void serviceMetadataNotExistsWhenDeleting() {
        // given
        expectedExeption.expect(SMPRuntimeException.class);
        expectedExeption.expectMessage(ErrorCode.METADATA_NOT_EXISTS.getMessage(SERVICE_GROUP_ID.getValue().toLowerCase(),
                SERVICE_GROUP_ID.getScheme().toLowerCase(), DOC_ID.getValue().toLowerCase(), DOC_ID.getScheme().toLowerCase()));
        // when - then
        testInstance.deleteServiceMetadata(null, SERVICE_GROUP_ID, DOC_ID);
    }

    @Test
    public void saveAndDeletePositiveScenario() throws IOException {
        //given
        byte[] inServiceMetadataXml = loadDocumentAsByteArray(SERVICE_METADATA_XML_PATH);
        testInstance.saveServiceMetadata(null, PT_ID, DOC_ID, inServiceMetadataXml);
        List<Identifier> docIdsBefore = testInstance.findServiceMetadataIdentifiers(PT_ID);
        assertEquals(1, docIdsBefore.size());
        Optional<DBSubresource> DBSubresource = serviceMetadataDao.findServiceMetadata(
                PT_ID.getValue().toLowerCase(), PT_ID.getScheme().toLowerCase(),
                DOC_ID.getValue().toLowerCase(), DOC_ID.getScheme().toLowerCase());
        assertTrue(DBSubresource.isPresent());

        //when
        testInstance.deleteServiceMetadata(null, PT_ID, DOC_ID);

        //then
        List<Identifier> docIdsAfter = testInstance.findServiceMetadataIdentifiers(SERVICE_GROUP_ID);
        assertEquals(0, docIdsAfter.size());

        expectedExeption.expect(SMPRuntimeException.class);
        expectedExeption.expectMessage(ErrorCode.METADATA_NOT_EXISTS.getMessage(SERVICE_GROUP_ID.getValue().toLowerCase(),
                SERVICE_GROUP_ID.getScheme().toLowerCase(), DOC_ID.getValue().toLowerCase(), DOC_ID.getScheme().toLowerCase()));

        testInstance.getServiceMetadataDocument(SERVICE_GROUP_ID, DOC_ID);
    }

    @Test
    public void updatePositiveScenario() throws IOException, JAXBException, TransformerException {
        //given
        byte[] oldServiceMetadataXml = loadDocumentAsByteArray(SERVICE_METADATA_XML_PATH);
        testInstance.saveServiceMetadata(null, PT_ID, DOC_ID, oldServiceMetadataXml);

        ServiceMetadata newServiceMetadata = unmarshal(loadDocumentAsByteArray(SERVICE_METADATA_XML_PATH));
        EndpointType endpoint = newServiceMetadata.getServiceInformation().getProcessList().getProcesses().get(0).getServiceEndpointList().getEndpoints().get(0);
        endpoint.setServiceDescription("New Description");
        byte[] newServiceMetadataXml = marshallToByteArray(newServiceMetadata);
        testInstance.saveServiceMetadata(null, PT_ID, DOC_ID, newServiceMetadataXml);

        //when
        Document resultServiceMetadataDoc = testInstance.getServiceMetadataDocument(PT_ID, DOC_ID);
        //then
        String newDescription = resultServiceMetadataDoc.getElementsByTagName("ServiceDescription").item(0).getTextContent();
        assertEquals("New Description", newDescription);
    }

    @Test
    public void findServiceMetadataIdsPositiveScenario() throws IOException, JAXBException, TransformerException {
        //given
        byte[] serviceMetadataXml1 = loadDocumentAsByteArray(SERVICE_METADATA_XML_PATH);
        testInstance.saveServiceMetadata(null, PT_ID, DOC_ID, serviceMetadataXml1);

        String secondDocIdValue = "second-doc-id";
        Identifier secondDocId = new Identifier(secondDocIdValue, DOC_ID.getScheme());
        ServiceMetadata serviceMetadata2 = unmarshal(loadDocumentAsByteArray(SERVICE_METADATA_XML_PATH));
        serviceMetadata2.getServiceInformation().getDocumentIdentifier().setValue(secondDocIdValue);
        byte[] serviceMetadataXml2 = marshallToByteArray(serviceMetadata2);
        testInstance.saveServiceMetadata(null, PT_ID, secondDocId, serviceMetadataXml2);

        //when
        List<Identifier> docIds = testInstance.findServiceMetadataIdentifiers(PT_ID);

        //then
        assertEquals(2, docIds.size());
        Identifier docId1 = docIds.get(0);
        assertEquals(DOC_ID.getScheme().toLowerCase(), docId1.getScheme());
        assertEquals(DOC_ID.getValue().toLowerCase(), docId1.getValue());
        Identifier docId2 = docIds.get(1);
        assertEquals(DOC_ID.getScheme().toLowerCase(), docId2.getScheme());
        assertEquals(secondDocIdValue, docId2.getValue());
    }
*/

}
