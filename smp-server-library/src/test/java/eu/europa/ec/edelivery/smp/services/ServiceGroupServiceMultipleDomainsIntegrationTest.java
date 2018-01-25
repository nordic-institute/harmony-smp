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
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.exceptions.WrongInputFieldException;
import org.junit.Test;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;
import org.springframework.test.context.jdbc.Sql;

import java.io.IOException;

import static eu.europa.ec.edelivery.smp.conversion.ServiceGroupConverter.toDbModel;
import static eu.europa.ec.edelivery.smp.conversion.ServiceGroupConverter.unmarshal;
import static eu.europa.ec.edelivery.smp.testutil.XmlTestUtils.loadDocumentAsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * Created by gutowpa on 18/01/2018.
 */
@Sql({"classpath:/service_integration_test_data.sql",
        "classpath:/service_integration_multiple_domains_test_data.sql"})
public class ServiceGroupServiceMultipleDomainsIntegrationTest extends AbstractServiceGroupServiceIntegrationTest {

    private static final String SECOND_DOMAIN_ID = "second_domain";
    private static final String SECOND_DOMAIN_CERT_HEADER = "client-cert-header-value";
    private static final String SECOND_DOMAIN_SIGNING_ALIAS = "signature-alias";
    private static final String SECOND_DOMAIN_SMP_ID = "SECOND-SMP-ID";

    @Test(expected = WrongInputFieldException.class)
    public void explictlySpecifiedDomainIsRequiredWhenSavingInMultipleDomainConfiguration() throws IOException {
        saveServiceGroup();
    }

    @Test
    public void saveAndReadPositiveScenarioForMultipleDomain() throws IOException {
        // given
        ServiceGroup inServiceGroup = unmarshal(loadDocumentAsString(SERVICE_GROUP_XML_PATH));
        serviceGroupService.saveServiceGroup(inServiceGroup, SECOND_DOMAIN_ID, ADMIN_USERNAME);

        // when
        DBServiceGroup dbServiceGroup = serviceGroupDao.find(toDbModel(SERVICE_GROUP_ID));

        // then
        DBDomain dbDomain = dbServiceGroup.getDomain();
        assertEquals(SECOND_DOMAIN_ID, dbDomain.getId());
        assertEquals(SECOND_DOMAIN_CERT_HEADER, dbDomain.getBdmslClientCertHeader());
        assertEquals(SECOND_DOMAIN_SIGNING_ALIAS, dbDomain.getSignatureCertAlias());
        assertEquals(SECOND_DOMAIN_SMP_ID, dbDomain.getBdmslSmpId());
        assertTrue(isEmpty(dbDomain.getBdmslClientCertAlias()));
    }

    @Test(expected = WrongInputFieldException.class)
    public void changingDomainOfExistingServiceGroupIsNotAllowed() throws Throwable {
        //given
        saveServiceGroup();
        ServiceGroup newServiceGroup = unmarshal(loadDocumentAsString(SERVICE_GROUP_XML_PATH));

        //when-then
        serviceGroupService.saveServiceGroup(newServiceGroup, SECOND_DOMAIN_ID, ADMIN_USERNAME);
    }


}
