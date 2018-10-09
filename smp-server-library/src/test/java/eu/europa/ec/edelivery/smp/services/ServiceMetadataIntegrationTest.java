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

import eu.europa.ec.edelivery.smp.config.H2JPATestConfiguration;
import eu.europa.ec.edelivery.smp.config.PropertiesSingleDomainTestConfig;
import eu.europa.ec.edelivery.smp.conversion.CaseSensitivityNormalizer;
import eu.europa.ec.edelivery.smp.conversion.ServiceGroupConverter;
import eu.europa.ec.edelivery.smp.conversion.ServiceMetadataConverter;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.dao.ServiceGroupDao;
import eu.europa.ec.edelivery.smp.data.dao.ServiceMetadataDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.data.model.DBServiceMetadata;
import eu.europa.ec.edelivery.smp.config.SmpServicesTestConfig;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.sml.SmlConnector;
import eu.europa.ec.edelivery.smp.testutil.DBAssertion;
import eu.europa.ec.edelivery.smp.testutil.TestConstants;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.busdox.transport.identifiers._1.DocumentIdentifierType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.conversion.ServiceMetadataConverter.unmarshal;
import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;
import static eu.europa.ec.edelivery.smp.testutil.XmlTestUtils.loadDocumentAsString;
import static eu.europa.ec.edelivery.smp.testutil.XmlTestUtils.marshall;
import static eu.europa.ec.smp.api.Identifiers.asDocumentId;
import static eu.europa.ec.smp.api.Identifiers.asParticipantId;
import static org.junit.Assert.*;

/**
 * Created by gutowpa on 15/11/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class ServiceMetadataIntegrationTest extends AbstractServiceIntegrationTest {

    static ParticipantIdentifierType PT_ID =null;
    static DocumentIdentifier DOC_ID  = null;
    static {
        PT_ID  = new ParticipantIdentifierType();
        PT_ID.setValue(TEST_SG_ID_PL2);
        PT_ID.setScheme(TEST_SG_SCHEMA_PL2);
        DOC_ID  = new DocumentIdentifier();
        DOC_ID.setValue(TEST_DOC_ID_PL2);
        DOC_ID.setScheme(TEST_DOC_SCHEMA_PL2);
    }

    @Autowired
    ServiceMetadataService testInstance;


    @Rule
    public ExpectedException expectedExeption = ExpectedException.none();

    @Before
    @Transactional
    public void prepareDatabase() {
        prepareDatabaseForSignleDomainEnv();
        DBServiceGroup sg = new DBServiceGroup();
        sg.setParticipantIdentifier(TEST_SG_ID_PL2.toLowerCase());
        sg.setParticipantScheme(TEST_SG_SCHEMA_PL2.toLowerCase());
        DBDomain domain = domainDao.getDomainByCode(TEST_DOMAIN_CODE_1).get();
        sg.addDomain(domain);
        serviceGroupDao.persistFlushDetach(sg);
    }

    @Test
    public void saveAndReadPositiveScenario() throws IOException, TransformerException, JAXBException {


        //given
        String inServiceMetadataXml = loadDocumentAsString(SERVICE_METADATA_XML_PATH);
        String expectedSignedServiceMetadataXml = loadDocumentAsString(SIGNED_SERVICE_METADATA_XML_PATH);
        List<DocumentIdentifier> docIdsBefore = testInstance.findServiceMetadataIdentifiers(PT_ID);
        assertEquals(0, docIdsBefore.size());

        //when
        testInstance.saveServiceMetadata(null, PT_ID, DOC_ID, inServiceMetadataXml);
        List<DocumentIdentifier> docIdsAfter = testInstance.findServiceMetadataIdentifiers(PT_ID);
        Document outServiceMetadataDoc = testInstance.getServiceMetadataDocument(PT_ID, DOC_ID);

        //then
        assertEquals(1, docIdsAfter.size());
        assertEquals(DOC_ID.getValue().toLowerCase(), docIdsAfter.get(0).getValue()); // normalized
        assertEquals(DOC_ID.getScheme().toLowerCase(), docIdsAfter.get(0).getScheme()); // normalized
        assertEquals(expectedSignedServiceMetadataXml, ServiceMetadataConverter.toString(outServiceMetadataDoc));
    }

    @Test
    public void serviceMetadataNotExistsWhenReading() {

        expectedExeption.expect(SMPRuntimeException.class);
        expectedExeption.expectMessage(ErrorCode.METADATA_NOT_EXISTS.getMessage(SERVICE_GROUP_ID.getValue().toLowerCase(),
                SERVICE_GROUP_ID.getScheme().toLowerCase(),DOC_ID.getValue().toLowerCase(), DOC_ID.getScheme().toLowerCase()));

        testInstance.getServiceMetadataDocument(SERVICE_GROUP_ID, DOC_ID);
    }


    @Test
    public void serviceMetadataNotExistsWhenDeleting() {
        // given
        expectedExeption.expect(SMPRuntimeException.class);
        expectedExeption.expectMessage(ErrorCode.METADATA_NOT_EXISTS.getMessage(SERVICE_GROUP_ID.getValue().toLowerCase(),
                SERVICE_GROUP_ID.getScheme().toLowerCase(),DOC_ID.getValue().toLowerCase(), DOC_ID.getScheme().toLowerCase()));
        // when - then
        testInstance.deleteServiceMetadata(null, SERVICE_GROUP_ID, DOC_ID);
    }

    @Test
    public void saveAndDeletePositiveScenario() throws IOException {
        //given
        String inServiceMetadataXml = loadDocumentAsString(SERVICE_METADATA_XML_PATH);
        testInstance.saveServiceMetadata(null, PT_ID, DOC_ID, inServiceMetadataXml);
        List<DocumentIdentifier> docIdsBefore = testInstance.findServiceMetadataIdentifiers(PT_ID);
        assertEquals(1, docIdsBefore.size());
        Optional<DBServiceMetadata> dbServiceMetadata = serviceMetadataDao.findServiceMetadata(
                PT_ID.getValue().toLowerCase(), PT_ID.getScheme().toLowerCase(),
                DOC_ID.getValue().toLowerCase(), DOC_ID.getScheme().toLowerCase());;
        assertTrue(dbServiceMetadata.isPresent());

        //when
        testInstance.deleteServiceMetadata(null, PT_ID, DOC_ID);

        //then
        List<DocumentIdentifier> docIdsAfter = testInstance.findServiceMetadataIdentifiers(SERVICE_GROUP_ID);
        assertEquals(0, docIdsAfter.size());

        expectedExeption.expect(SMPRuntimeException.class);
        expectedExeption.expectMessage(ErrorCode.METADATA_NOT_EXISTS.getMessage(SERVICE_GROUP_ID.getValue().toLowerCase(),
                SERVICE_GROUP_ID.getScheme().toLowerCase(),DOC_ID.getValue().toLowerCase(), DOC_ID.getScheme().toLowerCase()));

        testInstance.getServiceMetadataDocument(SERVICE_GROUP_ID, DOC_ID);
    }

    @Test
    public void updatePositiveScenario() throws IOException, JAXBException, TransformerException {
        //given
        String oldServiceMetadataXml = loadDocumentAsString(SERVICE_METADATA_XML_PATH);
        testInstance.saveServiceMetadata(null, PT_ID, DOC_ID, oldServiceMetadataXml);

        ServiceMetadata newServiceMetadata = unmarshal(loadDocumentAsString(SERVICE_METADATA_XML_PATH));
        EndpointType endpoint = newServiceMetadata.getServiceInformation().getProcessList().getProcesses().get(0).getServiceEndpointList().getEndpoints().get(0);
        endpoint.setServiceDescription("New Description");
        String newServiceMetadataXml = marshall(newServiceMetadata);
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
        String serviceMetadataXml1 = loadDocumentAsString(SERVICE_METADATA_XML_PATH);
        testInstance.saveServiceMetadata(null, PT_ID, DOC_ID, serviceMetadataXml1);

        String secondDocIdValue = "second-doc-id";
        DocumentIdentifier secondDocId = new DocumentIdentifier(secondDocIdValue, DOC_ID.getScheme());
        ServiceMetadata serviceMetadata2 = unmarshal(loadDocumentAsString(SERVICE_METADATA_XML_PATH));
        serviceMetadata2.getServiceInformation().getDocumentIdentifier().setValue(secondDocIdValue);
        String serviceMetadataXml2 = marshall(serviceMetadata2);
        testInstance.saveServiceMetadata(null, PT_ID, secondDocId, serviceMetadataXml2);

        //when
        List<DocumentIdentifier> docIds = testInstance.findServiceMetadataIdentifiers(PT_ID);

        //then
        assertEquals(2, docIds.size());
        DocumentIdentifier docId1 = docIds.get(0);
        assertEquals(DOC_ID.getScheme().toLowerCase(), docId1.getScheme());
        assertEquals(DOC_ID.getValue().toLowerCase(), docId1.getValue());
        DocumentIdentifier docId2 = docIds.get(1);
        assertEquals(DOC_ID.getScheme().toLowerCase(), docId2.getScheme());
        assertEquals(secondDocIdValue, docId2.getValue());
    }


}
