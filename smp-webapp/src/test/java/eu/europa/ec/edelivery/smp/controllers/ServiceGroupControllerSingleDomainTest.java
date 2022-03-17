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

import eu.europa.ec.edelivery.smp.AbstractTest;
import eu.europa.ec.edelivery.smp.config.PropertiesTestConfig;
import eu.europa.ec.edelivery.smp.config.SmpAppConfig;
import eu.europa.ec.edelivery.smp.config.SmpWebAppConfig;
import eu.europa.ec.edelivery.smp.config.SpringSecurityConfig;
import eu.europa.ec.edelivery.smp.services.ui.UIKeystoreService;
import eu.europa.ec.edelivery.smp.testutils.X509CertificateTestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import java.io.IOException;

import static eu.europa.ec.edelivery.smp.ServiceGroupBodyUtil.*;
import static java.lang.String.format;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by gutowpa on 02/08/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        PropertiesTestConfig.class,
        SmpAppConfig.class,
        SmpWebAppConfig.class,
        SpringSecurityConfig.class,
        UIKeystoreService.class
})
@WebAppConfiguration
@Sql(scripts = {"classpath:/cleanup-database.sql",
        "classpath:/webapp_integration_test_data_one_domain.sql"}
)
public class ServiceGroupControllerSingleDomainTest {

    private static final String PARTICIPANT_SCHEME = "ehealth-participantid-qns";
    private static final String PARTICIPANT_ID = "urn:poland:ncpb";

    private static final String DOCUMENT_SCHEME = "doctype";
    private static final String DOCUMENT_ID = "invoice";

    private static final String URL_PATH = format("/%s::%s", PARTICIPANT_SCHEME, PARTICIPANT_ID);
    private static final String URL_DOC_PATH = format("%s/services/%s::%s", URL_PATH, DOCUMENT_SCHEME, DOCUMENT_ID);

    private static final String SERVICE_GROUP_INPUT_BODY = getSampleServiceGroupBodyWithScheme(PARTICIPANT_SCHEME);
    private static final String HTTP_HEADER_KEY_DOMAIN = "Domain";
    private static final String HTTP_HEADER_KEY_SERVICE_GROUP_OWNER = "ServiceGroup-Owner";

    private static final String OTHER_OWNER_NAME = "CN=EHEALTH_SMP_TEST_BRAZIL,O=European Commission,C=BE:48b681ee8e0dcc08";

    private static final RequestPostProcessor ADMIN_CREDENTIALS = httpBasic("pat_smp_admin", "test123");

    @Autowired
    private WebApplicationContext webAppContext;

    private MockMvc mvc;

    @Before
    public void setup() throws IOException {
        X509CertificateTestUtils.reloadKeystores();

        mvc = MockMvcBuilders.webAppContextSetup(webAppContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        initServletContext();
    }

    private void initServletContext() {
        MockServletContext sc = new MockServletContext("");
        ServletContextListener listener = new ContextLoaderListener(webAppContext);
        ServletContextEvent event = new ServletContextEvent(sc);
        listener.contextInitialized(event);
    }

    @Test
    public void adminCanCreateServiceGroupNoDomain() throws Exception {
        mvc.perform(put(URL_PATH)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)
                .content(SERVICE_GROUP_INPUT_BODY))
                .andExpect(status().isCreated());
    }

    @Test
    public void adminCanUpdateServiceGroupNoDomain() throws Exception {
        mvc.perform(put(URL_PATH)

                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)
                .content(SERVICE_GROUP_INPUT_BODY))
                .andExpect(status().isCreated());

        mvc.perform(put(URL_PATH)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)
                .content(SERVICE_GROUP_INPUT_BODY))
                .andExpect(status().isOk());
    }


    @Test
    public void existingServiceMetadataCanBeRetrievedByEverybodyNoDomain() throws Exception {

        String xmlSG = getSampleServiceGroupBody(PARTICIPANT_SCHEME, PARTICIPANT_ID);
        String xmlMD = generateServiceMetadata(PARTICIPANT_ID, PARTICIPANT_SCHEME, DOCUMENT_ID, DOCUMENT_SCHEME, "test");
        // crate service group
        mvc.perform(put(URL_PATH)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)
                .content(xmlSG))
                .andExpect(status().isCreated());
        // add service metadata
        mvc.perform(put(URL_DOC_PATH)
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
    public void anonymousUserCannotCreateServiceGroup() throws Exception {
        mvc.perform(put(URL_PATH)
                .contentType(APPLICATION_XML_VALUE)
                .content(SERVICE_GROUP_INPUT_BODY))
                .andExpect(status().isUnauthorized());

        mvc.perform(get(URL_PATH))
                .andExpect(status().isNotFound());
    }

    @Test
    public void malformedInputReturnsBadRequestNoDomain() throws Exception {
        mvc.perform(put(URL_PATH)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)
                .content("malformed input XML"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void invalidParticipantSchemeReturnsBadRequestNoDomain() throws Exception {

        String scheme = "length-exceeeeeeds-25chars";
        String urlPath = format("/%s::%s", scheme, PARTICIPANT_ID);

        mvc.perform(put(urlPath)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)
                .content(getSampleServiceGroupBodyWithScheme(scheme)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void creatingServiceGroupUnderBadFormatedDomainReturnsBadRequestNoDomain() throws Exception {
        mvc.perform(put(URL_PATH)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)
                .header(HTTP_HEADER_KEY_DOMAIN, "not-existing-domain")
                .content(SERVICE_GROUP_INPUT_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(stringContainsInOrder("FORMAT_ERROR")));
    }

    @Test
    public void creatingServiceGroupUnderNotExistingDomainReturnsBadRequestNoDomain() throws Exception {
        mvc.perform(put(URL_PATH)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)
                .header(HTTP_HEADER_KEY_DOMAIN, "notExistingDomain")
                .content(SERVICE_GROUP_INPUT_BODY))
                .andExpect(status().isNotFound())
                .andExpect(content().string(stringContainsInOrder("NOT_FOUND")));
    }

    @Test
    public void adminCanAssignNewServiceGroupToOtherOwnerNoDomain() throws Exception {
        mvc.perform(put(URL_PATH)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)
                .header(HTTP_HEADER_KEY_SERVICE_GROUP_OWNER, OTHER_OWNER_NAME)
                .content(SERVICE_GROUP_INPUT_BODY))
                .andExpect(status().isCreated());
    }
}
