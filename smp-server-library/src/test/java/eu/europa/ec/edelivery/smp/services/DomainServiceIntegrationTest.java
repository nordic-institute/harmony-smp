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
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.testutil.TestConstants;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;
import static org.junit.Assert.*;

/**
 *  Purpose of class is to test ServiceGroupService base methods
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class DomainServiceIntegrationTest extends AbstractServiceIntegrationTest {

    @Rule
    public ExpectedException expectedExeption = ExpectedException.none();

    @Autowired
    protected DomainService testInstance;

    @Before
    @Transactional
    public void prepareDatabase() {
        prepareDatabaseForSignleDomainEnv();
    }


    @Test
    public void getDomainForBlankCodeForSingleDomain(){

        // given
        assertEquals(1, domainDao.getAllDomains().size());

        //Only one domain is in database - get domain should return the one.
        DBDomain dmn = testInstance.getDomain(null);
        assertEquals(TEST_DOMAIN_CODE_1, dmn.getDomainCode());
        dmn = testInstance.getDomain("");
        assertEquals(TEST_DOMAIN_CODE_1, dmn.getDomainCode());
        dmn = testInstance.getDomain(" ");
        assertEquals(TEST_DOMAIN_CODE_1, dmn.getDomainCode());
    }

    @Test
    public void getDomainForBlankCodeForMultipleDomain(){
        // given
        DBDomain testDomain02 =TestDBUtils.createDBDomain(TestConstants.TEST_DOMAIN_CODE_2);
        domainDao.persistFlushDetach(testDomain02);
        assertEquals(2, domainDao.getAllDomains().size());
        expectedExeption.expect(SMPRuntimeException.class);
        expectedExeption.expectMessage(ErrorCode.MISSING_DOMAIN.getMessage());

        // when-then
        //Multiple domains in database - get domain should return the SMPRuntimeException.
        testInstance.getDomain(null);
    }







}
