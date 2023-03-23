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

import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.testutil.TestConstants;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Pattern;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;
import static org.junit.Assert.*;

/**
 *  Purpose of class is to test ServiceGroupService base methods
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@Ignore
public class ServiceGroupServiceIntegrationTest extends AbstractServiceIntegrationTest {
    public static Pattern DEFAULT_URN_PATTERN = Pattern.compile("^(?i)((urn:)|(mailto:)).*$");

    @Autowired
    ServiceGroupService testInstance;

    @Before
    public void initDatabase(){
        prepareDatabaseForSingleDomainEnv();
    }

    @Test
    public void validateOwnershipUserNotExists(){
        Optional<DBResource>  dbsg = serviceGroupDao.findServiceGroup( TEST_SG_ID_2, TEST_SG_SCHEMA_2);
        assertTrue(dbsg.isPresent()); // test if exists
        //test
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,  () -> testInstance.validateOwnership("UserNotExist", dbsg.get()));
        assertEquals(ErrorCode.USER_NOT_EXISTS.getMessage(), result.getMessage());
    }

    @Test
    @Transactional
    public void validateMethodOwnershipUserNotOnwner(){
        Optional<DBResource>  dbsg = serviceGroupDao.findServiceGroup(TEST_SG_ID_2, TEST_SG_SCHEMA_2);
        assertTrue(dbsg.isPresent()); // test if exists

        DBUser u3= TestDBUtils.createDBUserByCertificate(TestConstants.USER_CERT_3);
        userDao.persistFlushDetach(u3);
        //test
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,  () ->  testInstance.validateOwnership(USER_CERT_3, dbsg.get()) );
        assertEquals(ErrorCode.USER_IS_NOT_OWNER.getMessage(USER_CERT_3,
                TEST_SG_ID_2, TEST_SG_SCHEMA_2), result.getMessage());

    }

    @Test
    public void toServiceGroupTest() {
        // set
        DBResource sg = TestDBUtils.createDBResource();

        //when
        ServiceGroup serviceGroup = testInstance.toServiceGroup(sg, DEFAULT_URN_PATTERN);
        assertNotNull(serviceGroup);
        assertEquals(sg.getIdentifierValue(), serviceGroup.getParticipantIdentifier().getValue());
        assertEquals(sg.getIdentifierScheme(), serviceGroup.getParticipantIdentifier().getScheme());
        assertEquals(1, serviceGroup.getExtensions().size());
    }

    @Test
    public void toServiceGroupTestEBCorePartyIDNotContact() {
        // set

        DBResource sg = TestDBUtils.createDBResource("0088:123456789","urn:oasis:names:tc:ebcore:partyid-type:iso6523");

        //when
        ServiceGroup serviceGroup = testInstance.toServiceGroup(sg, null);
        assertNotNull(serviceGroup);
        assertEquals(sg.getIdentifierValue(), serviceGroup.getParticipantIdentifier().getValue());
        assertEquals(sg.getIdentifierScheme(), serviceGroup.getParticipantIdentifier().getScheme());
        assertEquals(1, serviceGroup.getExtensions().size());
    }

    @Test
    public void toServiceGroupTestEBCorePartyIDContact() {
        // set
        DBResource sg = TestDBUtils.createDBResource("0088:123456789","urn:oasis:names:tc:ebcore:partyid-type:iso6523");
        //when
        ServiceGroup serviceGroup = testInstance.toServiceGroup(sg, DEFAULT_URN_PATTERN);
        assertNotNull(serviceGroup);
        assertEquals(sg.getIdentifierScheme() +":" + sg.getIdentifierValue(), serviceGroup.getParticipantIdentifier().getValue());
        assertNull(serviceGroup.getParticipantIdentifier().getScheme());
        assertEquals(1, serviceGroup.getExtensions().size());
    }

    @Test
    public void toServiceGroupTestMultiExtensions() {
        // set
        /*
        DBResource sg = TestDBUtils.createDBServiceGroup();
        sg.setExtension(ExtensionConverter.concatByteArrays(TestDBUtils.generateExtension(), TestDBUtils.generateExtension()));

        //when-then
        ServiceGroup serviceGroup = testInstance.toServiceGroup(sg, null);
        assertNotNull(serviceGroup);
        assertEquals(sg.getIdentifierValue(), serviceGroup.getParticipantIdentifier().getValue());
        assertEquals(sg.getIdentifierScheme(), serviceGroup.getParticipantIdentifier().getScheme());
        assertEquals(2, serviceGroup.getExtensions().size());

         */
    }

    @Test
    public void toServiceGroupTestIsEmpty() {
        // set
        //when
        ServiceGroup serviceGroup = testInstance.toServiceGroup(null, null);
        assertNull(serviceGroup);
    }

    @Test
    public void testInvalidExtension() {
        //given
        DBResource sg = TestDBUtils.createDBResource();
        sg.setExtension("<This > is invalid extensions".getBytes());

        //when-then
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,  () -> testInstance.toServiceGroup(sg, null));
        MatcherAssert.assertThat( result.getMessage(), Matchers.startsWith("Invalid extension for service group"));
    }
}
