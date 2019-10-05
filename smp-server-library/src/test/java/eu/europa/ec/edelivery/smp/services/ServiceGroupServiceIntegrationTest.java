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

import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
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

import java.util.Optional;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.*;
import static org.junit.Assert.assertTrue;

/**
 *  Purpose of class is to test ServiceGroupService base methods
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class ServiceGroupServiceIntegrationTest extends AbstractServiceIntegrationTest {

    @Rule
    public ExpectedException expectedExeption = ExpectedException.none();

    @Autowired
    ServiceGroupService testInstance;

    @Before
    public void initDatabase(){
        prepareDatabaseForSingleDomainEnv();
    }

    @Test
    public void validateOwnershipUserNotExists(){
        Optional<DBServiceGroup>  dbsg = serviceGroupDao.findServiceGroup( TEST_SG_ID_2, TEST_SG_SCHEMA_2);
        assertTrue(dbsg.isPresent()); // test if exists

        expectedExeption.expect(SMPRuntimeException.class);
        expectedExeption.expectMessage(ErrorCode.USER_NOT_EXISTS.getMessage());
        //test
        testInstance.validateOwnership("UserNotExist", dbsg.get());
    }

    @Test
    @Transactional
    public void validateMethodOwnershipUserNotOnwner(){
        Optional<DBServiceGroup>  dbsg = serviceGroupDao.findServiceGroup(TEST_SG_ID_2, TEST_SG_SCHEMA_2);
        assertTrue(dbsg.isPresent()); // test if exists

        DBUser u3= TestDBUtils.createDBUserByCertificate(TestConstants.USER_CERT_3);
        userDao.persistFlushDetach(u3);

        expectedExeption.expect(SMPRuntimeException.class);
        expectedExeption.expectMessage(ErrorCode.USER_IS_NOT_OWNER.getMessage(USER_CERT_3,
                TEST_SG_ID_2, TEST_SG_SCHEMA_2));
        //test
        testInstance.validateOwnership(USER_CERT_3, dbsg.get());

    }
}
