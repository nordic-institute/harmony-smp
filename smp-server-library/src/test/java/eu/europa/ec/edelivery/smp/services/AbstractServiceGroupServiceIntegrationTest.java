/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.config.PropertiesSingleDomainTestConfig;
import eu.europa.ec.edelivery.smp.config.SmpServicesTestConfig;
import eu.europa.ec.edelivery.smp.data.dao.OwnershipDao;
import eu.europa.ec.edelivery.smp.data.dao.ServiceGroupDao;
import org.junit.runner.RunWith;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;

import static eu.europa.ec.edelivery.smp.conversion.ServiceGroupConverter.unmarshal;
import static eu.europa.ec.edelivery.smp.testutil.XmlTestUtils.loadDocumentAsString;
import static eu.europa.ec.smp.api.Identifiers.asParticipantId;

/**
 * Created by gutowpa on 27/03/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SmpServicesTestConfig.class, PropertiesSingleDomainTestConfig.class})
@Transactional
@Rollback(true)
abstract class AbstractServiceGroupServiceIntegrationTest {

    protected static final String SERVICE_GROUP_XML_PATH = "/eu/europa/ec/edelivery/smp/services/ServiceGroupPoland.xml";
    protected static final ParticipantIdentifierType SERVICE_GROUP_ID = asParticipantId("participant-scheme-qns::urn:poland:ncpb");
    public static final String ADMIN_USERNAME = "test_admin";

    @PersistenceContext
    protected EntityManager em;

    @Autowired
    protected ServiceGroupDao serviceGroupDao;

    @Autowired
    protected OwnershipDao ownershipDao;

    @Autowired
    protected ServiceGroupService serviceGroupService;

    protected ServiceGroup saveServiceGroup() throws IOException {
        ServiceGroup inServiceGroup = unmarshal(loadDocumentAsString(SERVICE_GROUP_XML_PATH));
        serviceGroupService.saveServiceGroup(inServiceGroup, null, ADMIN_USERNAME);
        return inServiceGroup;
    }
}
