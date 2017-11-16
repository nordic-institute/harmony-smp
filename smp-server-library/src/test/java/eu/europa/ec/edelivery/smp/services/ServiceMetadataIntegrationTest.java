/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.cipa.smp.server.conversion.ServiceGroupConverter;
import eu.europa.ec.cipa.smp.server.conversion.ServiceMetadataConverter;
import eu.europa.ec.edelivery.smp.exceptions.NotFoundException;
import eu.europa.ec.edelivery.smp.config.SmpServicesTestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;

import static eu.europa.ec.cipa.smp.server.conversion.ServiceMetadataConverter.unmarshal;
import static eu.europa.ec.cipa.smp.server.util.XmlTestUtils.loadDocumentAsString;
import static eu.europa.ec.cipa.smp.server.util.XmlTestUtils.marshall;
import static eu.europa.ec.smp.api.Identifiers.asDocumentId;
import static eu.europa.ec.smp.api.Identifiers.asParticipantId;
import static org.junit.Assert.*;

/**
 * Created by gutowpa on 15/11/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SmpServicesTestConfig.class})
@Transactional
@Rollback(true)
@Sql("classpath:/service_integration_test_data.sql")
public class ServiceMetadataIntegrationTest {

    private static final String SERVICE_GROUP_XML_PATH = "/eu/europa/ec/edelivery/smp/services/ServiceGroupPoland.xml";
    private static final String SERVICE_METADATA_XML_PATH = "/eu/europa/ec/edelivery/smp/services/ServiceMetadataPoland.xml";
    private static final String SIGNED_SERVICE_METADATA_XML_PATH = "/eu/europa/ec/edelivery/smp/services/SignedServiceMetadataPoland.xml";
    private static final ParticipantIdentifierType SERVICE_GROUP_ID = asParticipantId("participant-scheme-qns::urn:poland:ncpb");
    private static final DocumentIdentifier DOC_ID = asDocumentId("ehealth-resid-qns::docid.007");
    public static final String ADMIN_USERNAME = "test_admin";

    @Autowired
    ServiceMetadataService serviceMetadataService;

    @Autowired
    ServiceGroupService serviceGroupService;

    @Before
    public void before() throws IOException {
        ServiceGroup inServiceGroup = ServiceGroupConverter.unmarshal(loadDocumentAsString(SERVICE_GROUP_XML_PATH));
        serviceGroupService.saveServiceGroup(inServiceGroup, ADMIN_USERNAME);
    }

    @Test
    public void saveAndReadPositiveScenario() throws IOException, TransformerException, JAXBException {
        //given
        String inServiceMetadataXml = loadDocumentAsString(SERVICE_METADATA_XML_PATH);
        String expectedSignedServiceMetadataXml = loadDocumentAsString(SIGNED_SERVICE_METADATA_XML_PATH);
        List<DocumentIdentifier> docIdsBefore = serviceMetadataService.findServiceMetadataIdentifiers(SERVICE_GROUP_ID);
        assertEquals(0, docIdsBefore.size());

        //when
        serviceMetadataService.saveServiceMetadata(SERVICE_GROUP_ID, DOC_ID, inServiceMetadataXml);
        Document outServiceMetadataDoc = serviceMetadataService.getServiceMetadataDocument(SERVICE_GROUP_ID, DOC_ID);

        //then
        assertEquals(expectedSignedServiceMetadataXml, ServiceMetadataConverter.toString(outServiceMetadataDoc));
        List<DocumentIdentifier> docIdsAfter = serviceMetadataService.findServiceMetadataIdentifiers(SERVICE_GROUP_ID);
        assertEquals(1, docIdsAfter.size());
        assertTrue(DOC_ID.equals(docIdsAfter.get(0)));
    }

    @Test(expected = NotFoundException.class)
    public void notFoundExceptionThrownWhenReadingNotExisting() {
        serviceMetadataService.getServiceMetadataDocument(SERVICE_GROUP_ID, DOC_ID);
    }

    @Test
    public void saveAndDeletePositiveScenario() throws IOException {
        //given
        String inServiceMetadataXml = loadDocumentAsString(SERVICE_METADATA_XML_PATH);
        serviceMetadataService.saveServiceMetadata(SERVICE_GROUP_ID, DOC_ID, inServiceMetadataXml);
        List<DocumentIdentifier> docIdsBefore = serviceMetadataService.findServiceMetadataIdentifiers(SERVICE_GROUP_ID);
        assertEquals(1, docIdsBefore.size());

        //when
        serviceMetadataService.deleteServiceMetadata(SERVICE_GROUP_ID, DOC_ID);

        //then
        List<DocumentIdentifier> docIdsAfter = serviceMetadataService.findServiceMetadataIdentifiers(SERVICE_GROUP_ID);
        assertEquals(0, docIdsAfter.size());
        try {
            serviceMetadataService.getServiceMetadataDocument(SERVICE_GROUP_ID, DOC_ID);
        } catch (NotFoundException e) {
            return;
        }
        fail("ServiceMetadata has not been deleted");
    }

    @Test
    public void updatePositiveScenario() throws IOException, JAXBException, TransformerException {
        //given
        String oldServiceMetadataXml = loadDocumentAsString(SERVICE_METADATA_XML_PATH);
        serviceMetadataService.saveServiceMetadata(SERVICE_GROUP_ID, DOC_ID, oldServiceMetadataXml);

        ServiceMetadata newServiceMetadata = unmarshal(loadDocumentAsString(SERVICE_METADATA_XML_PATH));
        EndpointType endpoint = newServiceMetadata.getServiceInformation().getProcessList().getProcesses().get(0).getServiceEndpointList().getEndpoints().get(0);
        endpoint.setServiceDescription("New Description");
        String newServiceMetadataXml = marshall(newServiceMetadata);
        serviceMetadataService.saveServiceMetadata(SERVICE_GROUP_ID, DOC_ID, newServiceMetadataXml);

        //when
        Document resultServiceMetadataDoc = serviceMetadataService.getServiceMetadataDocument(SERVICE_GROUP_ID, DOC_ID);

        //then
        String newDescription = resultServiceMetadataDoc.getElementsByTagName("ServiceDescription").item(0).getTextContent();
        assertEquals("New Description", newDescription);
    }
}
