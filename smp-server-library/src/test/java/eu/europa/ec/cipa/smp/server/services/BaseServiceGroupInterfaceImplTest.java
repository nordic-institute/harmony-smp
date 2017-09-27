/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.cipa.smp.server.services;

import com.helger.commons.codec.URLCodec;
import com.helger.commons.scopes.mgr.ScopeManager;
import eu.europa.ec.cipa.smp.server.conversion.ServiceGroupConverter;
import eu.europa.ec.cipa.smp.server.data.dbms.DBMSDataManager;
import eu.europa.ec.cipa.smp.server.data.dbms.model.DBUser;
import eu.europa.ec.cipa.smp.server.util.XmlTestUtils;
import eu.europa.ec.edelivery.smp.config.SmpServicesTestConfig;
import eu.europa.ec.smp.api.Identifiers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceMetadataReferenceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import static eu.europa.ec.smp.api.Identifiers.asDocumentId;
import static eu.europa.ec.smp.api.Identifiers.asParticipantId;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by gutowpa on 27/03/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SmpServicesTestConfig.class})
@Transactional
@Rollback(true)
@Sql("classpath:/service_integration_test_data.sql")
public class BaseServiceGroupInterfaceImplTest {

    private static String SERVICE_GROUP_ID = "eHealth-participantId-qns::urn:Poland:ncpb";
    private static String DOC_ID = "eHealth-resId-qns::DocId.007";

    @Autowired
    private BaseServiceGroupInterfaceImpl serviceGroupService;

    @Autowired
    private DBMSDataManager dataManager;

    @PersistenceContext
    private EntityManager entityManager;

    @Before
    public void setUp() throws IOException, SAXException, ParserConfigurationException {
        String sgXml = XmlTestUtils.loadDocumentAsString("/input/ServiceGroupPoland.xml");
        System.err.print(sgXml);
        ServiceGroup serviceGroup = ServiceGroupConverter.unmarshal(sgXml);
        dataManager.saveServiceGroup(serviceGroup, "test_admin");
        String smXml = XmlTestUtils.loadDocumentAsString("/input/ServiceMetadataPoland.xml");
        dataManager.saveService(asParticipantId(SERVICE_GROUP_ID), asDocumentId(DOC_ID), smXml);



        ServiceGroup sg = serviceGroupService.getServiceGroup(SERVICE_GROUP_ID);
        List<ServiceMetadataReferenceType> serviceMetadataReferences = sg.getServiceMetadataReferenceCollection().getServiceMetadataReferences();
        System.out.print(serviceMetadataReferences.size());
    }

    @Test
    public void testUrlsHandledByWebLayer() throws Throwable {
        //when
        ServiceGroup serviceGroup = serviceGroupService.getServiceGroup(SERVICE_GROUP_ID);

        //then
        List<ServiceMetadataReferenceType> serviceMetadataReferences = serviceGroup.getServiceMetadataReferenceCollection().getServiceMetadataReferences();
        //URLs are handled in by the REST webservices layer
        Assert.assertEquals(0, serviceMetadataReferences.size());
    }
}
