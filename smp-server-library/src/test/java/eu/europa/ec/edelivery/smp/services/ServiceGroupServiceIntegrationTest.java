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

import eu.europa.ec.edelivery.smp.data.dao.OwnershipDao;
import eu.europa.ec.edelivery.smp.data.dao.ServiceGroupDao;
import eu.europa.ec.edelivery.smp.data.model.DBOwnership;
import eu.europa.ec.edelivery.smp.data.model.DBOwnershipId;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.exceptions.NotFoundException;
import eu.europa.ec.edelivery.smp.config.SmpServicesTestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ExtensionType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceMetadataReferenceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

import static eu.europa.ec.edelivery.smp.conversion.ServiceGroupConverter.toDbModel;
import static eu.europa.ec.edelivery.smp.conversion.ServiceGroupConverter.unmarshal;
import static eu.europa.ec.cipa.smp.server.util.XmlTestUtils.loadDocumentAsString;
import static eu.europa.ec.cipa.smp.server.util.XmlTestUtils.marshall;
import static eu.europa.ec.smp.api.Identifiers.asParticipantId;
import static org.junit.Assert.*;

/**
 * Created by gutowpa on 27/03/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SmpServicesTestConfig.class})
@Transactional
@Rollback(true)
@Sql("classpath:/service_integration_test_data.sql")
public class ServiceGroupServiceIntegrationTest {

    private static final String SERVICE_GROUP_XML_PATH = "/eu/europa/ec/edelivery/smp/services/ServiceGroupPoland.xml";
    private static final ParticipantIdentifierType SERVICE_GROUP_ID = asParticipantId("participant-scheme-qns::urn:poland:ncpb");
    public static final String ADMIN_USERNAME = "test_admin";

    @PersistenceContext
    EntityManager em;

    @Autowired
    ServiceGroupDao serviceGroupDao;

    @Autowired
    OwnershipDao ownershipDao;

    @Autowired
    private ServiceGroupService serviceGroupService;

    @Test
    public void makeSureServiceGroupDoesNotExistAlready(){
        DBServiceGroup dbServiceGroup = serviceGroupDao.find(toDbModel(SERVICE_GROUP_ID));
        if(dbServiceGroup != null){
            throw new IllegalStateException("Underlying DB already contains test data that should not be there. Remove them manually.");
        }
    }

    @Test
    public void saveAndReadPositiveScenario() throws IOException, JAXBException {
        //when
        ServiceGroup inServiceGroup = saveServiceGroup();
        ServiceGroup outServiceGroup = serviceGroupService.getServiceGroup(SERVICE_GROUP_ID);

        //then
        assertFalse(inServiceGroup == outServiceGroup);
        assertEquals(marshall(inServiceGroup), marshall(outServiceGroup));

        em.flush();
        DBOwnership outOwnership = ownershipDao.find(new DBOwnershipId(ADMIN_USERNAME, SERVICE_GROUP_ID.getScheme(), SERVICE_GROUP_ID.getValue()));
        assertEquals(ADMIN_USERNAME, outOwnership.getUser().getUsername());
    }

    @Test(expected = NotFoundException.class)
    public void notFoundExceptionThrownWhenReadingNotExisting() {
        serviceGroupService.getServiceGroup(asParticipantId("not-existing::service-group"));
    }

    @Test
    public void saveAndDeletePositiveScenario() throws IOException {
        //given
        saveServiceGroup();
        em.flush();

        //when
        serviceGroupService.deleteServiceGroup(SERVICE_GROUP_ID);
        em.flush();

        //then
        try {
            serviceGroupService.getServiceGroup(SERVICE_GROUP_ID);
        } catch (NotFoundException e) {
            return;
        }
        fail("ServiceGroup has not been deleted");
    }

    @Test
    public void updatePositiveScenario() throws IOException, JAXBException {
        //given
        ServiceGroup oldServiceGroup = saveServiceGroup();

        ServiceGroup newServiceGroup = unmarshal(loadDocumentAsString(SERVICE_GROUP_XML_PATH));
        ExtensionType newExtension = new ExtensionType();
        newExtension.setExtensionID("new extension ID");
        newServiceGroup.getExtensions().add(newExtension);

        //when
        serviceGroupService.saveServiceGroup(newServiceGroup, ADMIN_USERNAME);
        ServiceGroup resultServiceGroup = serviceGroupService.getServiceGroup(SERVICE_GROUP_ID);

        //then
        assertNotEquals(marshall(oldServiceGroup), marshall(resultServiceGroup));
        assertEquals(marshall(newServiceGroup), marshall(resultServiceGroup));
    }

    @Test
    public void urlsAreHandledByWebLayer() throws Throwable {
        //given
        saveServiceGroup();

        //when
        ServiceGroup serviceGroup = serviceGroupService.getServiceGroup(SERVICE_GROUP_ID);

        //then
        List<ServiceMetadataReferenceType> serviceMetadataReferences = serviceGroup.getServiceMetadataReferenceCollection().getServiceMetadataReferences();
        //URLs are handled in by the REST webservices layer
        assertEquals(0, serviceMetadataReferences.size());
    }

    private ServiceGroup saveServiceGroup() throws IOException {
        ServiceGroup inServiceGroup = unmarshal(loadDocumentAsString(SERVICE_GROUP_XML_PATH));
        serviceGroupService.saveServiceGroup(inServiceGroup, ADMIN_USERNAME);
        return inServiceGroup;
    }
}
