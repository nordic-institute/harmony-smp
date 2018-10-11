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


import eu.europa.ec.edelivery.smp.conversion.ExtensionConverter;
import eu.europa.ec.edelivery.smp.data.model.*;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.testutil.TestConstants;
import org.hamcrest.core.StringStartsWith;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceMetadataReferenceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.conversion.ServiceGroupConverter.unmarshal;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.*;
import static eu.europa.ec.edelivery.smp.testutil.XmlTestUtils.loadDocumentAsString;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;
import static eu.europa.ec.smp.api.Identifiers.asParticipantId;
import static org.junit.Assert.*;

/**
 * Created by gutowpa on 17/01/2018.
 */
public class ServiceGroupServiceSingleDomainIntegrationTest extends AbstractServiceIntegrationTest {

    @Autowired
    protected ServiceGroupService testInstance;

    @Rule
    public ExpectedException expectedExeption = ExpectedException.none();

    @Before
    @Transactional
    public void prepareDatabase() {
        prepareDatabaseForSignleDomainEnv();
    }
    @Test
    public void createAndReadPositiveScenarioForNullDomain() throws IOException {
        // given
        ServiceGroup inServiceGroup = unmarshal(loadDocumentAsString(TestConstants.SERVICE_GROUP_POLAND_XML_PATH));
        Optional<DBServiceGroup> dbsg = serviceGroupDao.findServiceGroup(TEST_SG_ID_PL, TEST_SG_SCHEMA_2);
        assertFalse(dbsg.isPresent()); // test if exists
        DBDomain domain = domainDao.getTheOnlyDomain().get();
        assertNotNull(domain);
        // when
        boolean bCreated = testInstance.saveServiceGroup(inServiceGroup, null, TestConstants.USERNAME_1,
                TestConstants.USERNAME_1);

        Optional<DBServiceGroup> optRes= serviceGroupDao.findServiceGroup(TEST_SG_ID_PL, TEST_SG_SCHEMA_2);

        // then
        assertTrue(bCreated);
        dbAssertion.assertServiceGroupForOnlyDomain(inServiceGroup.getParticipantIdentifier().getValue(),
                inServiceGroup.getParticipantIdentifier().getScheme(),domain.getDomainCode());

    }

   @Test
    public void createAndReadPositiveScenarioForWithDomain() throws IOException {
       // given
       ServiceGroup inServiceGroup = unmarshal(loadDocumentAsString(TestConstants.SERVICE_GROUP_POLAND_XML_PATH));
       Optional<DBServiceGroup> dbsg = serviceGroupDao.findServiceGroup(TEST_SG_ID_PL, TEST_SG_SCHEMA_2);
       assertFalse(dbsg.isPresent()); // test if exists
       DBDomain domain = domainDao.getTheOnlyDomain().get();
       assertNotNull(domain);

       // when
       boolean bCreated = testInstance.saveServiceGroup(inServiceGroup, domain.getDomainCode(), TestConstants.USERNAME_1,
               TestConstants.USERNAME_1);


       Optional<DBServiceGroup> optRes= serviceGroupDao.findServiceGroup(TEST_SG_ID_PL, TEST_SG_SCHEMA_2);

       // then
       assertTrue(bCreated);
       dbAssertion.assertServiceGroupForOnlyDomain(inServiceGroup.getParticipantIdentifier().getValue(),
               inServiceGroup.getParticipantIdentifier().getScheme(),domain.getDomainCode());
    }

    @Test
    public void updateAndReadPositiveScenario() throws IOException, JAXBException, XMLStreamException {
        // given
        ServiceGroup inServiceGroup = unmarshal(loadDocumentAsString(TestConstants.SERVICE_GROUP_TEST2_XML_PATH));
        Optional<DBServiceGroup> dbsg = serviceGroupDao.findServiceGroup(TEST_SG_ID_2, TEST_SG_SCHEMA_2);
        assertTrue(dbsg.isPresent()); // test if exists
        DBDomain domain = domainDao.getTheOnlyDomain().get();
        assertNotNull(domain);

        byte[] extension = dbsg.get().getExtension(); // test if exists
        byte[] newExtension  = ExtensionConverter.marshalExtensions(inServiceGroup.getExtensions());
        assertFalse(Arrays.equals(extension, newExtension)); // extension updated

        // when
        boolean bCreated = testInstance.saveServiceGroup(inServiceGroup, domain.getDomainCode(), TestConstants.USERNAME_1,
                TestConstants.USERNAME_1);


        Optional<DBServiceGroup> optRes= serviceGroupDao.findServiceGroup(TEST_SG_ID_PL, TEST_SG_SCHEMA_2);

        // then
        assertFalse(bCreated);
        dbAssertion.assertServiceGroupExtensionEqual(inServiceGroup.getParticipantIdentifier().getValue(),
                inServiceGroup.getParticipantIdentifier().getScheme(),
                newExtension);
    }

    @Test
    public void serviceGroupNotExistsWhenRetrievingSG() {
        // given
        expectedExeption.expect(SMPRuntimeException.class);
        expectedExeption.expectMessage(SG_NOT_EXISTS.getMessage("service-group", "not-existing"));
        // when-then
        testInstance.getServiceGroup(asParticipantId("not-existing::service-group") );
    }

    @Test
    public void saveAndDeletePositiveScenario() throws IOException {
        // given
        ServiceGroup inServiceGroup = unmarshal(loadDocumentAsString(TestConstants.SERVICE_GROUP_POLAND_XML_PATH));
        boolean bCreated = testInstance.saveServiceGroup(inServiceGroup, null, TestConstants.USERNAME_1,
                TestConstants.USERNAME_1);
        assertTrue(bCreated);
        serviceGroupDao.clearPersistenceContext();

        //when
        testInstance.deleteServiceGroup(inServiceGroup.getParticipantIdentifier());
        serviceGroupDao.clearPersistenceContext();

        //then
        expectedExeption.expect(SMPRuntimeException.class);
        // get by null domain so: (all registered domains)
        expectedExeption.expectMessage(SG_NOT_EXISTS.getMessage( inServiceGroup.getParticipantIdentifier().getValue(),
                inServiceGroup.getParticipantIdentifier().getScheme()));

        ServiceGroup sg = testInstance.getServiceGroup(inServiceGroup.getParticipantIdentifier());

    }

    @Test
    public void defineGroupOwnerWhenOwnerIsNull(){
        String testUser = "user";
        String result = testInstance.defineGroupOwner(null, testUser);
        assertEquals(testUser, result);

        result = testInstance.defineGroupOwner("", testUser);
        assertEquals(testUser, result);
    }

    @Test
    public void defineGroupOwnerWhenOwnerIsNotNull(){
        String testUser = "user";
        String testOwner = "owner";
        String result = testInstance.defineGroupOwner(testOwner, testUser);
        assertEquals(testOwner, result);
    }


    @Test
    public void updateInvalidUserException() throws IOException, JAXBException {

        // given
        ServiceGroup inServiceGroup = unmarshal(loadDocumentAsString(TestConstants.SERVICE_GROUP_TEST2_XML_PATH));
        Optional<DBServiceGroup>  dbsg = dbAssertion.findAndInitServiceGroup(TEST_SG_ID_2, TEST_SG_SCHEMA_2);
        Optional<DBUser> dbUser = userDao.findUserByIdentifier(TestConstants.USER_CERT_2);
        assertTrue(dbsg.isPresent()); // test if exists
        assertTrue(dbUser.isPresent()); // test if exists
        assertFalse(dbsg.get().getUsers().contains(dbUser.get())); // test not owner

        //then
        expectedExeption.expect(SMPRuntimeException.class);
        // get by null domain so: (all registered domains)
        expectedExeption.expectMessage(USER_IS_NOT_OWNER.getMessage(TestConstants.USER_CERT_2,

                dbsg.get().getParticipantIdentifier(), dbsg.get().getParticipantScheme()));

        // when
        testInstance.saveServiceGroup(inServiceGroup,null,
                TestConstants.USER_CERT_2, TestConstants.USER_CERT_2);
    }

    @Test
    public void updateUnknownUserException() throws IOException, JAXBException {

        // given
        ServiceGroup inServiceGroup = unmarshal(loadDocumentAsString(TestConstants.SERVICE_GROUP_TEST2_XML_PATH));
        Optional<DBServiceGroup>  dbsg = dbAssertion.findAndInitServiceGroup(TEST_SG_ID_2, TEST_SG_SCHEMA_2);
        Optional<DBUser> dbUser = userDao.findUserByIdentifier(TestConstants.USER_CERT_3);
        assertTrue(dbsg.isPresent()); // test if note exists
        assertFalse(dbUser.isPresent()); // test if exists

        //then
        expectedExeption.expect(SMPRuntimeException.class);
        // get by null domain so: (all registered domains)
        expectedExeption.expectMessage(USER_NOT_EXISTS.getMessage());

        // when
        testInstance.saveServiceGroup(inServiceGroup, null,
                TestConstants.USER_CERT_3, TestConstants.USER_CERT_3);
    }

    @Test
    public void updateInvalidUserEncodingException() throws IOException {
        String  username = "test::20%atest";
        //given
        ServiceGroup inServiceGroup = unmarshal(loadDocumentAsString(TestConstants.SERVICE_GROUP_TEST2_XML_PATH));
        expectedExeption.expect(SMPRuntimeException.class);
        expectedExeption.expectMessage(StringStartsWith.startsWith("Unsupported or invalid encoding"));

        //when
        testInstance.saveServiceGroup(inServiceGroup, null, username, username);

    }

  @Test
    public void savingUnderNotExistingDomainIsNotAllowed() throws Throwable {
        //given
        String domain="NOTEXISTINGDOMAIN";
        ServiceGroup inServiceGroup = unmarshal(loadDocumentAsString(TestConstants.SERVICE_GROUP_POLAND_XML_PATH));
        expectedExeption.expect(SMPRuntimeException.class);
        expectedExeption.expectMessage(DOMAIN_NOT_EXISTS.getMessage(domain));

        //execute
        testInstance.saveServiceGroup(inServiceGroup, domain, USERNAME_1, USERNAME_1);
    }

    @Test
    public void onlyASCIICharactersAllowedInDomainId() throws Throwable {
        //given
        String domain="notAllowedChars:-_;#$";
        ServiceGroup inServiceGroup = unmarshal(loadDocumentAsString(TestConstants.SERVICE_GROUP_POLAND_XML_PATH));
        expectedExeption.expect(SMPRuntimeException.class);
        expectedExeption.expectMessage(INVALID_DOMAIN_CODE.getMessage(domain,
                DomainService.DOMAIN_ID_PATTERN.pattern()));

        //execute
        testInstance.saveServiceGroup(inServiceGroup, domain, USERNAME_1, USERNAME_1);
    }

    @Test
    public void urlsAreHandledByWebLayer() throws Throwable {

        //when
        ParticipantIdentifierType pt = new ParticipantIdentifierType();
        pt.setValue(TEST_SG_ID_2);
        pt.setScheme(TEST_SG_SCHEMA_2);
        // execute
        ServiceGroup serviceGroup = testInstance.getServiceGroup(pt);
        assertNotNull(serviceGroup);
        //then
        List<ServiceMetadataReferenceType> serviceMetadataReferences = serviceGroup.getServiceMetadataReferenceCollection().getServiceMetadataReferences();
        //URLs are handled in by the REST webservices layer
        assertEquals(0, serviceMetadataReferences.size());
    }

}
