/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */

package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.config.SmlIntegrationConfiguration;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.conversion.IdentifierService;
import eu.europa.ec.edelivery.smp.data.dao.AbstractJunit5BaseDao;
import eu.europa.ec.edelivery.smp.data.dao.DomainDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.sml.SmlConnector;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.regex.Pattern;

import static eu.europa.ec.edelivery.smp.testutil.TestConstants.TEST_DOMAIN_CODE_1;
import static eu.europa.ec.edelivery.smp.testutil.TestConstants.TEST_DOMAIN_CODE_2;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

/**
 * Purpose of class is to test ServiceGroupService base methods
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */

public class DomainServiceTest extends AbstractJunit5BaseDao {

    @Autowired
    IdentifierService identifierService;
    @Autowired
    SmlIntegrationConfiguration integrationMock;
    @Autowired
    SmlConnector smlConnector;
    @Autowired
    private SMLIntegrationService smlIntegrationService;


    @Autowired
    protected DomainDao domainDao;

    @Autowired
    protected DomainService testInstance;


    @BeforeEach
    public void prepareDatabase() throws IOException {
        smlConnector = Mockito.spy(smlConnector);
        Mockito.doNothing().when(smlConnector).configureClient(any(), any(), any());

        ReflectionTestUtils.setField(smlIntegrationService, "smlConnector", smlConnector);
        ReflectionTestUtils.setField(testInstance, "smlIntegrationService", smlIntegrationService);

        ReflectionTestUtils.setField(smlIntegrationService, "identifierService", identifierService);
        identifierService.configureParticipantIdentifierFormatter(null, false, Pattern.compile(".*"));

        resetKeystore();
        setDatabaseProperty(SMPPropertyEnum.SML_PHYSICAL_ADDRESS, "0.0.0.0");
        setDatabaseProperty(SMPPropertyEnum.SML_LOGICAL_ADDRESS, "http://localhost/smp");
        setDatabaseProperty(SMPPropertyEnum.SML_URL, "http://localhost/edelivery-sml");
        setDatabaseProperty(SMPPropertyEnum.SML_ENABLED, "true");

        integrationMock.reset();


    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    public void getDomainForBlankCodeForSingleDomain(String searchCode) {

        // given
        DBDomain testDomain01 = testUtilsDao.createDomain(TEST_DOMAIN_CODE_1);
        assertEquals(1, domainDao.getAllDomains().size());

        //Only one domain is in database - get domain should return the one.
        DBDomain dmn = testInstance.getDomain(searchCode);
        assertEquals(testDomain01.getDomainCode(), dmn.getDomainCode());
    }

    @Test
    public void getDomainForBlankCodeForMultipleDomain() {
        // given
        DBDomain testDomain01 = testUtilsDao.createDomain(TEST_DOMAIN_CODE_1);
        DBDomain testDomain02 = TestDBUtils.createDBDomain(TEST_DOMAIN_CODE_2);
        domainDao.persistFlushDetach(testDomain02);
        assertEquals(2, domainDao.getAllDomains().size());

        // when-then
        //Multiple domains in database - get domain should return the SMPRuntimeException.
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                () -> testInstance.getDomain(null));

        assertEquals(ErrorCode.MISSING_DOMAIN, result.getErrorCode());
    }

    @Test
    public void getDomainForBlankCodeForMultipleDomainNotExists() {
        // given
        DBDomain testDomain01 = testUtilsDao.createDomain(TEST_DOMAIN_CODE_1);
        DBDomain testDomain02 = TestDBUtils.createDBDomain(TEST_DOMAIN_CODE_2);
        domainDao.persistFlushDetach(testDomain02);
        assertEquals(2, domainDao.getAllDomains().size());
        String searchDomain = "DomainCodeNotExists";

        // when-then
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                () -> testInstance.getDomain(searchDomain));

        assertEquals(ErrorCode.DOMAIN_NOT_EXISTS, result.getErrorCode());
    }

    @Test
    public void getDomainForInvalidCode() {
        // given
        DBDomain testDomain01 = testUtilsDao.createDomain(TEST_DOMAIN_CODE_1);
        DBDomain testDomain02 = TestDBUtils.createDBDomain(TEST_DOMAIN_CODE_2);
        domainDao.persistFlushDetach(testDomain02);
        assertEquals(2, domainDao.getAllDomains().size());
        String searchDomain = "s2###Q23@#";

        // when-then
        //Multiple domains in database - get domain should return the SMPRuntimeException.
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                () -> testInstance.getDomain(searchDomain));
        assertEquals(ErrorCode.INVALID_DOMAIN_CODE, result.getErrorCode());
        MatcherAssert.assertThat(result.getMessage(),
                CoreMatchers.containsString("Provided Domain Code '" + searchDomain + "' does not match required pattern"));
    }

    @Test
    public void testRegisterDomainAndParticipantsOK() {
        // given
        testUtilsDao.clearData();
        testUtilsDao.createResources();
        DBDomain testDomain = testUtilsDao.getD1();
        DBResource testResource = testUtilsDao.getResourceD1G1RD1();
        assertFalse(testDomain.isSmlRegistered());
        assertFalse(testResource.isSmlRegistered());

        // when
        testInstance.registerDomainAndParticipants(testDomain);

        // then
        // update resource because testResource is detached
        DBResource dbUpdatedResource = testUtilsDao.find(DBResource.class, testResource.getId());

        assertTrue(testDomain.isSmlRegistered());
        assertTrue(dbUpdatedResource.isSmlRegistered());
    }

    @Test
    public void testUnRegisterDomainAndParticipantsOK() {
        // given
        testUtilsDao.clearData();
        testUtilsDao.createResources();
        DBDomain testDomain = testUtilsDao.getD1();
        testInstance.registerDomainAndParticipants(testDomain);
        DBResource dbUpdatedResource = testUtilsDao.find(DBResource.class, testUtilsDao.getResourceD1G1RD1().getId());

        assertTrue(testDomain.isSmlRegistered());
        assertTrue(dbUpdatedResource.isSmlRegistered());

        // when
        testInstance.unregisterDomainAndParticipantsFromSml(testDomain);

        // then
        dbUpdatedResource = testUtilsDao.find(DBResource.class, dbUpdatedResource.getId());
        assertFalse(testDomain.isSmlRegistered());
        assertFalse(dbUpdatedResource.isSmlRegistered());
    }

}
