/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
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

import eu.europa.ec.bdmsl.ws.soap.IManageParticipantIdentifierWS;
import eu.europa.ec.bdmsl.ws.soap.IManageServiceMetadataWS;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.data.dao.AbstractJunit5BaseDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.sml.SmlConnector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

/**
 * Purpose of class is to test DomainSMLIntegrationService base methods
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
class DomainSMLIntegrationServiceTest extends AbstractJunit5BaseDao {

    @Autowired
    private SmlConnector smlConnector;
    @Autowired
    private SMLIntegrationService smlIntegrationService;
    @Autowired
    private DomainSMLIntegrationService testInstance;
    // needed for mocking WS services
    @MockBean
    private IManageParticipantIdentifierWS iManageParticipantIdentifierWS;
    @MockBean
    private IManageServiceMetadataWS iManageServiceMetadataWS;

    @BeforeEach
    public void prepareDatabase() throws IOException {
        smlConnector = Mockito.spy(smlConnector);
        Mockito.doNothing().when(smlConnector).configureClient(any(), any(), any());

        ReflectionTestUtils.setField(smlIntegrationService, "smlConnector", smlConnector);
        ReflectionTestUtils.setField(testInstance, "smlIntegrationService", smlIntegrationService);

        resetKeystore();
        setDatabaseProperty(SMPPropertyEnum.SML_PHYSICAL_ADDRESS, "0.0.0.0");
        setDatabaseProperty(SMPPropertyEnum.SML_LOGICAL_ADDRESS, "http://localhost/smp");
        setDatabaseProperty(SMPPropertyEnum.SML_URL, "http://localhost/edelivery-sml");
        setDatabaseProperty(SMPPropertyEnum.SML_ENABLED, "true");
    }

    @Test
    void testRegisterDomainAndParticipantsOK() {
        // given
        testUtilsDao.clearData();
        testUtilsDao.createResources();
        DBDomain testDomain = testUtilsDao.getD1();
        DBResource testResource = testUtilsDao.getResourceD1G1RD1();
        assertFalse(testDomain.isSmlRegistered());
        assertFalse(testResource.isSmlRegistered());

        // when
        testInstance.registerDomainAndParticipants(testDomain.getId());

        // then
        // update resource because testResource is detached
        DBResource dbUpdatedResource = testUtilsDao.find(DBResource.class, testResource.getId());

        assertTrue(dbUpdatedResource.isSmlRegistered());
    }

    @Test
    void testUnRegisterDomainAndParticipantsOK() {
        // given
        testUtilsDao.clearData();
        testUtilsDao.createResources();
        DBDomain testDomain = testUtilsDao.getD1();
        testInstance.registerDomainAndParticipants(testDomain.getId());
        DBResource dbUpdatedResource = testUtilsDao.find(DBResource.class, testUtilsDao.getResourceD1G1RD1().getId());

        assertTrue(dbUpdatedResource.isSmlRegistered());

        // when
        testInstance.unregisterDomainAndParticipantsFromSml(testDomain.getId());

        // then
        dbUpdatedResource = testUtilsDao.find(DBResource.class, dbUpdatedResource.getId());
        assertFalse(dbUpdatedResource.isSmlRegistered());
    }
}
