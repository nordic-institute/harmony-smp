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
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;
import static org.junit.Assert.assertFalse;

/**
 * Purpose of class is to test ServiceGroupService base methods
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
@TestPropertySource(properties = {
        "bdmsl.integration.enabled=false"})
@ContextConfiguration(classes = {SMLIntegrationService.class})
public class SMLIntegrationServiceNoSMLIntegrationTest extends AbstractServiceIntegrationTest {

    @Rule
    public ExpectedException expectedExeption = ExpectedException.none();

    @Autowired
    protected SMLIntegrationService testInstance;

    @Before
    @Transactional
    public void prepareDatabase() {
        prepareDatabaseForSignleDomainEnv();
    }

    @Test
    public void registerOnlyDomainToSml() {

        expectedExeption.expect(SMPRuntimeException.class);
        expectedExeption.expectMessage("Configuration error: SML integration is not enabled!");
        // given
        DBDomain testDomain01 = domainDao.getDomainByCode(TEST_DOMAIN_CODE_1).get();
        assertFalse(testDomain01.isSmlRegistered());

        // when
        testInstance.registerDomain(testDomain01);
    }

    @Test
    public void unregisterOnlyDomainToSml() {

        expectedExeption.expect(SMPRuntimeException.class);
        expectedExeption.expectMessage("Configuration error: SML integration is not enabled!");
        // given
        DBDomain testDomain01 = domainDao.getDomainByCode(TEST_DOMAIN_CODE_1).get();
        testDomain01.setSmlRegistered(true);

        // when
        testInstance.unRegisterDomain(testDomain01);
    }



    @Test
    public void registerOnlyParticipantDomainToSml() {

        expectedExeption.expect(SMPRuntimeException.class);
        expectedExeption.expectMessage("Configuration error: SML integration is not enabled!");
        // when
        testInstance.registerParticipant(TEST_SG_ID_1, TEST_SG_SCHEMA_1, TEST_DOMAIN_CODE_1);
    }

}
