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
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.testutil.TestConstants;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.conversion.ServiceGroupConverter.unmarshal;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.USER_IS_NOT_OWNER;
import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;
import static eu.europa.ec.edelivery.smp.testutil.XmlTestUtils.loadDocumentAsString;
import static org.junit.Assert.*;

/**
 * Created by gutowpa on 18/01/2018.
 */
public class ServiceGroupServiceMultipleDomainsIntegrationTest extends AbstractServiceIntegrationTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    protected ServiceGroupService testInstance;


    @Before
    public void prepareDatabase() {

        super.prepareDatabaseForMultipeDomainEnv();
        setDatabaseProperty(SMPPropertyEnum.SML_ENABLED,"false");
    }

    @Test
    public void getServiceGroupForAllDomainTest() {
        // given
        ParticipantIdentifierType serviceGroupId = new ParticipantIdentifierType();
        serviceGroupId.setValue(TEST_SG_ID_2);
        serviceGroupId.setScheme(TEST_SG_SCHEMA_2);

        // when
        ServiceGroup sg = testInstance.getServiceGroup(serviceGroupId);

        // then
        assertNotNull(sg);
        assertEquals(TEST_SG_ID_2, sg.getParticipantIdentifier().getValue());
        assertEquals(TEST_SG_SCHEMA_2, sg.getParticipantIdentifier().getScheme());
        assertEquals(1, sg.getExtensions().size());
    }


    @Test
    public void createAndReadPositiveScenarioForMultipleDomain() throws IOException {
        // given
        ServiceGroup inServiceGroup = unmarshal(loadDocumentAsString(TestConstants.SERVICE_GROUP_POLAND_XML_PATH));
        Optional<DBServiceGroup> dbsg = serviceGroupDao.findServiceGroup(TEST_SG_ID_PL, TEST_SG_SCHEMA_2);
        assertFalse(dbsg.isPresent()); // test if exists - it must not :)

        // when
        boolean bCreated = testInstance.saveServiceGroup(inServiceGroup, TEST_DOMAIN_CODE_2, TestConstants.USERNAME_TOKEN_1,
                TestConstants.USERNAME_TOKEN_1);
        Optional<DBServiceGroup> optRes = dbAssertion.findAndInitServiceGroup(TEST_SG_ID_PL, TEST_SG_SCHEMA_2);

        // then
        assertTrue(bCreated);
        assertTrue(optRes.isPresent());
        DBServiceGroup dbServiceGroup = optRes.get();
        assertEquals(1, dbServiceGroup.getServiceGroupDomains().size());
        assertEquals(TEST_DOMAIN_CODE_2, dbServiceGroup.getServiceGroupDomains().get(0).getDomain().getDomainCode());
        assertEquals(inServiceGroup.getParticipantIdentifier().getValue(), dbServiceGroup.getParticipantIdentifier());
        assertEquals(inServiceGroup.getParticipantIdentifier().getScheme(), dbServiceGroup.getParticipantScheme());

    }

    @Test
    public void updateAndReadPositiveScenarioForMultipleDomain() throws IOException, JAXBException, XMLStreamException {
        // given
        ServiceGroup inServiceGroup = unmarshal(loadDocumentAsString(TestConstants.SERVICE_GROUP_TEST2_XML_PATH));
        Optional<DBServiceGroup> dbsg = serviceGroupDao.findServiceGroup(TEST_SG_ID_2, TEST_SG_SCHEMA_2);
        assertTrue(dbsg.isPresent()); // test if exists
        byte[] extension = dbsg.get().getExtension(); // test if exists
        byte[] newExtension = ExtensionConverter.marshalExtensions(inServiceGroup.getExtensions());
        assertNotEquals(extension, newExtension); // extension updated

        // when
        boolean bCreated = testInstance.saveServiceGroup(inServiceGroup, TEST_DOMAIN_CODE_1, ""+ TestConstants.USERNAME_TOKEN_1,
                TestConstants.USERNAME_TOKEN_1);
        serviceGroupDao.clearPersistenceContext();

        Optional<DBServiceGroup> optRes = dbAssertion.findAndInitServiceGroup(TEST_SG_ID_2, TEST_SG_SCHEMA_2);

        // then
        assertFalse(bCreated);
        assertTrue(optRes.isPresent());
        DBServiceGroup dbServiceGroup = optRes.get();
        assertEquals(1, dbServiceGroup.getServiceGroupDomains().size());
        assertEquals(TEST_DOMAIN_CODE_1, dbServiceGroup.getServiceGroupDomains().get(0).getDomain().getDomainCode());
        assertEquals(inServiceGroup.getParticipantIdentifier().getValue(), dbServiceGroup.getParticipantIdentifier());
        assertEquals(inServiceGroup.getParticipantIdentifier().getScheme(), dbServiceGroup.getParticipantScheme());
        assertTrue(Arrays.equals(newExtension, dbServiceGroup.getExtension())); // extension updated
    }

    @Test
    public void userIsNotOwnerOfServiceGroup() throws Throwable {
        //given
        ServiceGroup newServiceGroup = unmarshal(loadDocumentAsString(TestConstants.SERVICE_GROUP_TEST2_XML_PATH));
        DBUser u3 = TestDBUtils.createDBUserByCertificate(TestConstants.USER_CERT_3);
        userDao.persistFlushDetach(u3);

        expectedException.expect(SMPRuntimeException.class);
        expectedException.expectMessage(USER_IS_NOT_OWNER.getMessage(USER_CERT_3, TEST_SG_ID_2, TEST_SG_SCHEMA_2));

        //when-then
        testInstance.saveServiceGroup(newServiceGroup, TEST_DOMAIN_CODE_2, TestConstants.USER_CERT_3, TestConstants.USER_CERT_3);
    }

}
