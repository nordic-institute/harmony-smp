/*
 * Copyright 2018 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.data.model.DBOwnership;
import eu.europa.ec.edelivery.smp.data.model.DBOwnershipId;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.exceptions.InvalidOwnerException;
import eu.europa.ec.edelivery.smp.exceptions.NotFoundException;
import eu.europa.ec.edelivery.smp.exceptions.UnknownUserException;
import eu.europa.ec.edelivery.smp.exceptions.WrongInputFieldException;
import org.hamcrest.core.StringStartsWith;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ExtensionType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceMetadataReferenceType;
import org.springframework.test.context.jdbc.Sql;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

import static eu.europa.ec.edelivery.smp.conversion.ServiceGroupConverter.toDbModel;
import static eu.europa.ec.edelivery.smp.conversion.ServiceGroupConverter.unmarshal;
import static eu.europa.ec.edelivery.smp.testutil.XmlTestUtils.loadDocumentAsString;
import static eu.europa.ec.edelivery.smp.testutil.XmlTestUtils.marshall;
import static eu.europa.ec.smp.api.Identifiers.asParticipantId;

import static org.junit.Assert.*;
import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;

/**
 * Created by gutowpa on 17/01/2018.
 */

@Sql("classpath:/service_integration_test_data.sql")
public class ServiceGroupServiceSingleDomainIntegrationTest extends AbstractServiceGroupServiceIntegrationTest {


    @Rule
    public ExpectedException expectedExeption = ExpectedException.none();

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
        serviceGroupService.saveServiceGroup(newServiceGroup, null, ADMIN_USERNAME, ADMIN_USERNAME);
        ServiceGroup resultServiceGroup = serviceGroupService.getServiceGroup(SERVICE_GROUP_ID);

        //then
        assertNotEquals(marshall(oldServiceGroup), marshall(resultServiceGroup));
        assertEquals(marshall(newServiceGroup), marshall(resultServiceGroup));
    }

    @Test
    public void updateUnknownUserException() throws IOException, JAXBException {

        String invalidServiceUser = "WrongOwner";
        //given
        ServiceGroup oldServiceGroup = saveServiceGroup();

        expectedExeption.expect(UnknownUserException.class);
        expectedExeption.expectMessage("Unknown user '"+invalidServiceUser+"'");

        ServiceGroup newServiceGroup = unmarshal(loadDocumentAsString(SERVICE_GROUP_XML_PATH));
        ExtensionType newExtension = new ExtensionType();
        newExtension.setExtensionID("new extension ID the second");
        newServiceGroup.getExtensions().add(newExtension);

        //when
        serviceGroupService.saveServiceGroup(newServiceGroup, null, invalidServiceUser, ADMIN_USERNAME);
    }

    @Test
    public void updateInvalidUserException() throws IOException, JAXBException {

        //given
        ServiceGroup oldServiceGroup = saveServiceGroup();
        expectedExeption.expect(InvalidOwnerException.class);
        expectedExeption.expectMessage("User: "+CERT_USER+" is not owner of service group: participant-scheme-qns::urn:poland:ncpb");

        ServiceGroup newServiceGroup = unmarshal(loadDocumentAsString(SERVICE_GROUP_XML_PATH));

        //when
        serviceGroupService.saveServiceGroup(newServiceGroup, null, CERT_USER, ADMIN_USERNAME);

    }

    @Test
    public void updateEncodedInvalidUserException() throws IOException, JAXBException {

        //given
        ServiceGroup oldServiceGroup = saveServiceGroup();
        expectedExeption.expect(InvalidOwnerException.class);
        expectedExeption.expectMessage("User: "+CERT_USER+" is not owner of service group: participant-scheme-qns::urn:poland:ncpb");

        ServiceGroup newServiceGroup = unmarshal(loadDocumentAsString(SERVICE_GROUP_XML_PATH));

        //when
        serviceGroupService.saveServiceGroup(newServiceGroup, null, CERT_USER_ENCODED, ADMIN_USERNAME);

    }

    @Test
    public void updateInvalidUserEncodingException() throws IOException, JAXBException {
        String  username = "test::20%atest";
        //given
        ServiceGroup oldServiceGroup = saveServiceGroup();
        expectedExeption.expect(InvalidOwnerException.class);
        expectedExeption.expectMessage(StringStartsWith.startsWith("Unsupported or invalid encoding"));

        ServiceGroup newServiceGroup = unmarshal(loadDocumentAsString(SERVICE_GROUP_XML_PATH));
        ExtensionType newExtension = new ExtensionType();
        newExtension.setExtensionID("new extension ID the second");
        newServiceGroup.getExtensions().add(newExtension);

        //when
        serviceGroupService.saveServiceGroup(newServiceGroup, null, username, ADMIN_USERNAME);

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

    @Test(expected = WrongInputFieldException.class)
    public void savingUnderNotExistingDomainIsNotAllowed() throws Throwable {
        //given
        saveServiceGroup();
        ServiceGroup newServiceGroup = unmarshal(loadDocumentAsString(SERVICE_GROUP_XML_PATH));

        //when-then
        serviceGroupService.saveServiceGroup(newServiceGroup,"NOTEXISTINGDOMAIN", ADMIN_USERNAME, ADMIN_USERNAME);
    }

    @Test(expected = WrongInputFieldException.class)
    public void onlyASCIICharactersAllowedInDomainId() throws Throwable {
        //given
        ServiceGroup newServiceGroup = unmarshal(loadDocumentAsString(SERVICE_GROUP_XML_PATH));

        //when-then
        serviceGroupService.saveServiceGroup(newServiceGroup,"notAllowedChars:-_;#$", ADMIN_USERNAME, ADMIN_USERNAME);
    }

    @Test
    public void savingUnderTheOnlyDomainSpecifiedExpliciteIsAllowed() throws Throwable {
        //given
        ServiceGroup newServiceGroup = unmarshal(loadDocumentAsString(SERVICE_GROUP_XML_PATH));

        //when
        serviceGroupService.saveServiceGroup(newServiceGroup,"domain1", ADMIN_USERNAME, ADMIN_USERNAME);

        //then
        assertNotNull(serviceGroupService.getServiceGroup(SERVICE_GROUP_ID));
    }

}
