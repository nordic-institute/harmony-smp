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

package eu.europa.ec.edelivery.smp.controllers;

import eu.europa.ec.edelivery.smp.data.dao.ConfigurationDao;
import eu.europa.ec.edelivery.smp.data.ui.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.test.SmpTestWebAppConfig;
import eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils;
import eu.europa.ec.edelivery.smp.test.testutils.X509CertificateTestUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.adapter.ForwardedHeaderTransformer;

import java.io.IOException;

import static eu.europa.ec.edelivery.smp.ServiceGroupBodyUtil.*;
import static java.lang.String.format;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by gutowpa on 02/08/2017.
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SmpTestWebAppConfig.class})
@Sql(scripts = {
        "classpath:/cleanup-database.sql",
        "classpath:/webapp_integration_test_data.sql",
        },
        statements = {
                "update SMP_CONFIGURATION set VALUE='false', LAST_UPDATED_ON=CURRENT_TIMESTAMP() where PROPERTY='identifiersBehaviour.scheme.mandatory';",
                "update SMP_CONFIGURATION set VALUE='true', LAST_UPDATED_ON=CURRENT_TIMESTAMP() where PROPERTY='smp.automation.authentication.external.tls.clientCert.enabled';"
        },
        executionPhase = BEFORE_TEST_METHOD)
public class ServiceGroupControllerTest {

    private static final String PARTICIPANT_SCHEME = "ehealth-participantid-qns";
    private static final String PARTICIPANT_ID = "urn:poland:ncpb";

    private static final String DOCUMENT_SCHEME = "doctype";
    private static final String DOCUMENT_ID = "invoice";

    private static final String URL_PATH = format("/%s::%s", PARTICIPANT_SCHEME, PARTICIPANT_ID);
    private static final String URL_PATH_NULL_SCHEME = format("/%s", PARTICIPANT_ID);
    private static final String URL_DOC_PATH = format("%s/services/%s::%s", URL_PATH, DOCUMENT_SCHEME, DOCUMENT_ID);

    private static final String SERVICE_GROUP_INPUT = getSampleServiceGroupBodyWithScheme(PARTICIPANT_SCHEME);
    private static final String SERVICE_GROUP_INPUT_NULL_SCHEME = getSampleServiceGroupBodyWithScheme(null);
    private static final String HTTP_HEADER_KEY_DOMAIN = "Domain";
    private static final String HTTP_HEADER_KEY_SERVICE_GROUP_OWNER = "ServiceGroup-Owner";
    private static final String HTTP_DOMAIN_VALUE = "domain";


    private static final String OTHER_OWNER_NAME_URL_ENCODED = "CN=utf-8_%C5%BC_SMP,O=EC,C=BE:0000000000000666";

    private static final RequestPostProcessor ADMIN_CREDENTIALS = httpBasic("pat_smp_admin", "123456");

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    ForwardedHeaderTransformer forwardedHeaderTransformer;

    @Autowired
    ConfigurationDao configurationDao;

    private MockMvc mvc;

    @Before
    public void setup() throws IOException {
        forwardedHeaderTransformer.setRemoveOnly(false);
        X509CertificateTestUtils.reloadKeystores();
        mvc = MockMvcUtils.initializeMockMvc(webAppContext);
        configurationDao.reloadPropertiesFromDatabase();

    }

    @Test
    public void notFoundIsReturnedWhenServiceGroupDoesNotExist() throws Exception {
        mvc.perform(get(URL_PATH))
                .andExpect(status().isNotFound());
    }

    @Test
    public void adminCanCreateServiceGroup() throws Exception {
        mvc.perform(put(URL_PATH)
                .with(ADMIN_CREDENTIALS)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .contentType(APPLICATION_XML_VALUE)
                .content(SERVICE_GROUP_INPUT))
                .andExpect(status().isCreated());
    }

    @Test
    @Ignore("Setting of the 'identifiersBehaviour.scheme.mandatory' not working")
    public void adminCanCreateServiceGroupNullScheme() throws Exception {

        mvc.perform(put(URL_PATH_NULL_SCHEME)
                .with(ADMIN_CREDENTIALS)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .contentType(APPLICATION_XML_VALUE)
                .content(SERVICE_GROUP_INPUT_NULL_SCHEME))
                .andExpect(status().isCreated());
    }

    @Test
    public void adminCanUpdateServiceGroup() throws Exception {
        mvc.perform(put(URL_PATH)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)
                .content(SERVICE_GROUP_INPUT))
                .andExpect(status().isCreated());

        mvc.perform(put(URL_PATH)
                .with(ADMIN_CREDENTIALS)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .contentType(APPLICATION_XML_VALUE)
                .content(SERVICE_GROUP_INPUT))
                .andExpect(status().isOk());
    }

    @Test
    public void existingServiceGroupCanBeRetrievedByEverybody() throws Exception {
        mvc.perform(put(URL_PATH)
                .with(ADMIN_CREDENTIALS)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .contentType(APPLICATION_XML_VALUE)
                .content(SERVICE_GROUP_INPUT))
                .andExpect(status().isCreated());

        mvc.perform(get(URL_PATH))
                .andExpect(content().xml(SERVICE_GROUP_INPUT));

    }

    @Test
    public void existingServiceMetadataCanBeRetrievedByEverybody() throws Exception {

        String xmlSG = getSampleServiceGroupBody(PARTICIPANT_SCHEME, PARTICIPANT_ID);
        String xmlMD = generateServiceMetadata(PARTICIPANT_ID, PARTICIPANT_SCHEME, DOCUMENT_ID, DOCUMENT_SCHEME, "test");
        // crate service group
        mvc.perform(put(URL_PATH)
                .with(ADMIN_CREDENTIALS)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .contentType(APPLICATION_XML_VALUE)
                .content(xmlSG))
                .andExpect(status().isCreated());
        // add service metadata
        mvc.perform(put(URL_DOC_PATH)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)

                .content(xmlMD))
                .andExpect(status().isCreated());

        MvcResult mr = mvc.perform(get(URL_PATH).header("X-Forwarded-Host", "ec.test.eu")
                .header("X-Forwarded-Port", "443")
                .header("X-Forwarded-Proto", "https")).andReturn();
        System.out.println(mr.getResponse().getContentAsString());
        mvc.perform(get(URL_PATH))
                .andExpect(content().xml("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><ServiceGroup xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"><ParticipantIdentifier scheme=\"ehealth-participantid-qns\">urn:poland:ncpb</ParticipantIdentifier><ServiceMetadataReferenceCollection><ServiceMetadataReference href=\"http://localhost/ehealth-participantid-qns%3A%3Aurn%3Apoland%3Ancpb/services/doctype%3A%3Ainvoice\"/></ServiceMetadataReferenceCollection></ServiceGroup>"));

    }

    @Test
    public void getExistingServiceMetadatWithReverseProxyHost() throws Exception {
        //given
        prepareForGet();

        // when then..
        String expectedUrl = "http://ec.test.eu/";
        mvc.perform(get(URL_PATH)
                .header("X-Forwarded-Host", "ec.test.eu")
                .header("X-Forwarded-Port", "")
                .header("X-Forwarded-Proto", "http"))
                .andExpect(content().xml("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                        "<ServiceGroup xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\">" +
                        "<ParticipantIdentifier scheme=\"ehealth-participantid-qns\">urn:poland:ncpb</ParticipantIdentifier>" +
                        "<ServiceMetadataReferenceCollection>" +
                        "<ServiceMetadataReference href=\"" + expectedUrl + "ehealth-participantid-qns%3A%3Aurn%3Apoland%3Ancpb/services/doctype%3A%3Ainvoice\"/>" +
                        "</ServiceMetadataReferenceCollection></ServiceGroup>"));
    }

    @Test
    public void getExistingServiceMetadataWithReverseNoProxyHost() throws Exception {
        //given
        prepareForGet();

        // when then..
        String expectedUrl = "http://localhost/";
        mvc.perform(get(URL_PATH)
                .header("X-Forwarded-Port", "")
                .header("X-Forwarded-Proto", "http"))
                .andExpect(content().xml("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                        "<ServiceGroup xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\">" +
                        "<ParticipantIdentifier scheme=\"ehealth-participantid-qns\">urn:poland:ncpb</ParticipantIdentifier>" +
                        "<ServiceMetadataReferenceCollection>" +
                        "<ServiceMetadataReference href=\"" + expectedUrl + "ehealth-participantid-qns%3A%3Aurn%3Apoland%3Ancpb/services/doctype%3A%3Ainvoice\"/>" +
                        "</ServiceMetadataReferenceCollection></ServiceGroup>"));
    }

    @Test
    public void getExistingServiceMetadatWithReverseProxyPort() throws Exception {
        //given
        prepareForGet();

        // when then..
        String expectedUrl = "http://ec.test.eu:8443/";
        mvc.perform(get(URL_PATH)
                .header("X-Forwarded-Host", "ec.test.eu:8443")
                .header("X-Forwarded-Proto", "http"))
                .andExpect(content().xml("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                        "<ServiceGroup xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\">" +
                        "<ParticipantIdentifier scheme=\"ehealth-participantid-qns\">urn:poland:ncpb</ParticipantIdentifier>" +
                        "<ServiceMetadataReferenceCollection>" +
                        "<ServiceMetadataReference href=\"" + expectedUrl + "ehealth-participantid-qns%3A%3Aurn%3Apoland%3Ancpb/services/doctype%3A%3Ainvoice\"/>" +
                        "</ServiceMetadataReferenceCollection></ServiceGroup>"));
    }

    @Test
    public void getExistingServiceMetadatWithReverseProxySchema() throws Exception {
        //given
        prepareForGet();

        // when then..
        String expectedUrl = "https://ec.test.eu:8443/";
        mvc.perform(get(URL_PATH)
                .header("X-Forwarded-Port", "8443")
                .header("X-Forwarded-Host", "ec.test.eu")
                .header("X-Forwarded-Proto", "https"))
                .andExpect(content().xml("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                        "<ServiceGroup xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\">" +
                        "<ParticipantIdentifier scheme=\"ehealth-participantid-qns\">urn:poland:ncpb</ParticipantIdentifier>" +
                        "<ServiceMetadataReferenceCollection>" +
                        "<ServiceMetadataReference href=\"" + expectedUrl + "ehealth-participantid-qns%3A%3Aurn%3Apoland%3Ancpb/services/doctype%3A%3Ainvoice\"/>" +
                        "</ServiceMetadataReferenceCollection></ServiceGroup>"));
    }

    @Test
    public void getExistingServiceMetadatWithReverseProxySkipDefaultPortHttps() throws Exception {
        //given
        prepareForGet();

        // when then..
        String expectedUrl = "https://ec.test.eu/";
        mvc.perform(get(URL_PATH)
                .header("X-Forwarded-Port", "443")
                .header("X-Forwarded-Host", "ec.test.eu")
                .header("X-Forwarded-Proto", "https"))
                .andExpect(content().xml("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                        "<ServiceGroup xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\">" +
                        "<ParticipantIdentifier scheme=\"ehealth-participantid-qns\">urn:poland:ncpb</ParticipantIdentifier>" +
                        "<ServiceMetadataReferenceCollection>" +
                        "<ServiceMetadataReference href=\"" + expectedUrl + "ehealth-participantid-qns%3A%3Aurn%3Apoland%3Ancpb/services/doctype%3A%3Ainvoice\"/>" +
                        "</ServiceMetadataReferenceCollection></ServiceGroup>"));
    }

    @Test
    public void getExistingServiceMetadatWithReverseProxySkipDefaultPortHttp() throws Exception {
        //given
        prepareForGet();

        // when then..
        String expectedUrl = "http://ec.test.eu/";
        mvc.perform(get(URL_PATH)
                .header("X-Forwarded-Port", "80")
                .header("X-Forwarded-Host", "ec.test.eu")
                .header("X-Forwarded-Proto", "http"))
                .andExpect(content().xml("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                        "<ServiceGroup xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\">" +
                        "<ParticipantIdentifier scheme=\"ehealth-participantid-qns\">urn:poland:ncpb</ParticipantIdentifier>" +
                        "<ServiceMetadataReferenceCollection>" +
                        "<ServiceMetadataReference href=\"" + expectedUrl + "ehealth-participantid-qns%3A%3Aurn%3Apoland%3Ancpb/services/doctype%3A%3Ainvoice\"/>" +
                        "</ServiceMetadataReferenceCollection></ServiceGroup>"));
    }

    @Test
    public void getExistingServiceMetadatWithReverseProxyPortInHost() throws Exception {
        //given
        prepareForGet();

        // when then..
        String expectedUrl = "https://ec.test.eu:8443/";
        mvc.perform(get(URL_PATH)
                .header("X-Forwarded-Port", "8443")
                .header("X-Forwarded-Host", "ec.test.eu:8443")
                .header("X-Forwarded-Proto", "https"))
                .andExpect(content().xml("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                        "<ServiceGroup xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\">" +
                        "<ParticipantIdentifier scheme=\"ehealth-participantid-qns\">urn:poland:ncpb</ParticipantIdentifier>" +
                        "<ServiceMetadataReferenceCollection>" +
                        "<ServiceMetadataReference href=\"" + expectedUrl + "ehealth-participantid-qns%3A%3Aurn%3Apoland%3Ancpb/services/doctype%3A%3Ainvoice\"/>" +
                        "</ServiceMetadataReferenceCollection></ServiceGroup>"));
    }

    @Test
    public void anonymousUserCannotCreateServiceGroup() throws Exception {
        mvc.perform(put(URL_PATH)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .contentType(APPLICATION_XML_VALUE)
                .content(SERVICE_GROUP_INPUT))
                .andExpect(status().isUnauthorized());

        mvc.perform(get(URL_PATH))
                .andExpect(status().isNotFound());
    }

    @Test
    public void adminCanDeleteServiceGroup() throws Exception {
        mvc.perform(put(URL_PATH)
                .with(ADMIN_CREDENTIALS)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .contentType(APPLICATION_XML_VALUE)
                .content(SERVICE_GROUP_INPUT))
                .andExpect(status().isCreated());

        mvc.perform(delete(URL_PATH)
                .with(ADMIN_CREDENTIALS))
                .andExpect(status().isOk());
        mvc.perform(get(URL_PATH))
                .andExpect(status().isNotFound());
    }

    @Test
    public void malformedInputReturnsBadRequest() throws Exception {
        mvc.perform(put(URL_PATH)
                .with(ADMIN_CREDENTIALS)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .contentType(APPLICATION_XML_VALUE)
                .content("malformed input XML"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void invalidParticipantSchemeReturnsBadRequest() throws Exception {

        String scheme = "length-exceeeeeeds-25chars";
        String urlPath = format("/%s::%s", scheme, PARTICIPANT_ID);

        mvc.perform(put(urlPath)
                .with(ADMIN_CREDENTIALS)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .contentType(APPLICATION_XML_VALUE)
                .content(getSampleServiceGroupBodyWithScheme(scheme)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void creatingServiceGroupUnderBadFormatedDomainReturnsBadRequest() throws Exception {
        mvc.perform(put(URL_PATH)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)
                .header(HTTP_HEADER_KEY_DOMAIN, "not-existing-domain")
                .content(SERVICE_GROUP_INPUT))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(stringContainsInOrder("FORMAT_ERROR")));
    }

    @Test
    public void creatingServiceGroupUnderNotExistingDomainReturnsBadRequest() throws Exception {
        mvc.perform(put(URL_PATH)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)
                .header(HTTP_HEADER_KEY_DOMAIN, "notExistingDomain")
                .content(SERVICE_GROUP_INPUT))
                .andExpect(status().isNotFound())
                .andExpect(content().string(stringContainsInOrder("NOT_FOUND")));
    }

    @Test
    public void adminCanAssignNewServiceGroupToOtherOwner() throws Exception {
        mvc.perform(put(URL_PATH)
                .with(ADMIN_CREDENTIALS)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .contentType(APPLICATION_XML_VALUE)
                .header(HTTP_HEADER_KEY_SERVICE_GROUP_OWNER, OTHER_OWNER_NAME_URL_ENCODED)
                .content(SERVICE_GROUP_INPUT))
                .andExpect(status().isCreated());
    }

    @Test
    public void adminCannotAssignNewServiceGroupToNotExistingOwner() throws Exception {
        mvc.perform(put(URL_PATH)
                .with(ADMIN_CREDENTIALS)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .contentType(APPLICATION_XML_VALUE)
                .header(HTTP_HEADER_KEY_SERVICE_GROUP_OWNER, "not-existing-user")
                .content(SERVICE_GROUP_INPUT))
                .andExpect(status().isBadRequest());
    }

    public void prepareForGet() throws Exception {
        String xmlSG = getSampleServiceGroupBody(PARTICIPANT_SCHEME, PARTICIPANT_ID);
        String xmlMD = generateServiceMetadata(PARTICIPANT_ID, PARTICIPANT_SCHEME, DOCUMENT_ID, DOCUMENT_SCHEME, "test");
        // crate service group
        mvc.perform(put(URL_PATH)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)
                .content(xmlSG))
                .andExpect(status().isCreated());
        // add service metadata
        mvc.perform(put(URL_DOC_PATH)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)

                .content(xmlMD))
                .andExpect(status().isCreated());

    }

}
