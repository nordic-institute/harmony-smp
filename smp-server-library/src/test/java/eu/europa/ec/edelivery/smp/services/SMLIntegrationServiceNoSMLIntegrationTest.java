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

import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;

/**
 * Purpose of class is to test ServiceGroupService base methods
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@ContextConfiguration(classes = {SMLIntegrationService.class})
public class SMLIntegrationServiceNoSMLIntegrationTest extends AbstractServiceIntegrationTest {

    @Autowired
    protected SMLIntegrationService testInstance;

    @Before
    @Transactional
    public void prepareDatabase() {
        testUtilsDao.clearData();;
        testUtilsDao.createResources();
    }

    @Test
    public void registerOnlyDomainToSml() {

        // given
        DBDomain testDomain01 = testUtilsDao.getD1();
        testDomain01.setSmlRegistered(false);

        // when
        SMPRuntimeException result = Assert.assertThrows(SMPRuntimeException.class, () -> testInstance.registerDomain(testDomain01));
        Assert.assertEquals("Configuration error: [SML integration is not enabled!]!", result.getMessage());
    }

    @Test
    public void unregisterOnlyDomainToSml() {

        // given
        DBDomain testDomain01 = testUtilsDao.getD1();
        testDomain01.setSmlRegistered(true);

        // when
        SMPRuntimeException result = Assert.assertThrows(SMPRuntimeException.class, () -> testInstance.unRegisterDomain(testDomain01));
        Assert.assertEquals("Configuration error: [SML integration is not enabled!]!", result.getMessage());
    }

    @Test
    public void registerParticipant() {

        DBDomain testDomain01 = testUtilsDao.getD1();
        DBResource resource =   testUtilsDao.getResourceD1G1RD1();
        // nothing is expected to be thrown
        testInstance.registerParticipant(resource, testDomain01);
    }

    @Test
    public void unregisterParticipant() {

        DBDomain testDomain01 = testUtilsDao.getD1();
        DBResource resource =   testUtilsDao.getResourceD1G1RD1();
        // nothing is expected to be thrown
        testInstance.unregisterParticipant(resource, testDomain01);
    }


}
