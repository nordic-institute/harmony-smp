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
import eu.europa.ec.edelivery.smp.test.SmpTestWebAppConfig;
import eu.europa.ec.edelivery.smp.test.testutils.X509CertificateTestUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.adapter.ForwardedHeaderTransformer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.UUID;

import static eu.europa.ec.edelivery.smp.ServiceGroupBodyUtil.generateServiceMetadata;
import static eu.europa.ec.edelivery.smp.ServiceGroupBodyUtil.getSampleServiceGroupBody;
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
        "classpath:/webapp_integration_test_data.sql"},
        executionPhase = BEFORE_TEST_METHOD)
public class ResourceControllerTest {

    public static final Logger LOG = LoggerFactory.getLogger(ResourceControllerTest.class);

    private static final String IDENTIFIER_SCHEME = "ehealth-participantid-qns";
    private static final String DOCUMENT_SCHEME = "doctype";

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
    public void notFoundIsReturnedWhenServiceGroupDoesNotExist() throws Exception {
        mvc.perform(get(format("/%s::%s", IDENTIFIER_SCHEME, UUID.randomUUID().toString())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void adminCanCreateServiceGroup() throws Exception {
        String participantId = UUID.randomUUID().toString();
        String resourceExample = getSampleServiceGroupBody(IDENTIFIER_SCHEME, participantId);
        String urPath = format("/%s::%s", IDENTIFIER_SCHEME, participantId);

        mvc.perform(put(urPath)
                .with(ADMIN_CREDENTIALS)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .contentType(APPLICATION_XML_VALUE)
                .content(resourceExample))
                .andExpect(status().isCreated());
    }

    @Test
    @Ignore("Setting of the 'identifiersBehaviour.scheme.mandatory' not working")
    public void adminCanCreateServiceGroupNullScheme() throws Exception {
        String participantId = UUID.randomUUID().toString();
        String nullSchemeExample = getSampleServiceGroupBody(null, UUID.randomUUID().toString());

        mvc.perform(put(format("/%s",  participantId))
                .with(ADMIN_CREDENTIALS)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .contentType(APPLICATION_XML_VALUE)
                .content(nullSchemeExample))
                .andExpect(status().isCreated());
    }

    @Test
    public void adminCanUpdateServiceGroup() throws Exception {
        String participantId = UUID.randomUUID().toString();
        String resourceExample = getSampleServiceGroupBody(IDENTIFIER_SCHEME, participantId);
        String urPath = format("/%s::%s", IDENTIFIER_SCHEME, participantId);


        mvc.perform(put(urPath)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)
                .content(resourceExample))
                .andExpect(status().isCreated());

        mvc.perform(put(urPath)
                .with(ADMIN_CREDENTIALS)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .contentType(APPLICATION_XML_VALUE)
                .content(resourceExample))
                .andExpect(status().isOk());
    }

    @Test
    public void existingServiceGroupCanBeRetrievedByEverybody() throws Exception {
        String participantId = UUID.randomUUID().toString();
        String resourceExample = getSampleServiceGroupBody(IDENTIFIER_SCHEME, participantId);
        String urPath = format("/%s::%s", IDENTIFIER_SCHEME, participantId);

        mvc.perform(put(urPath)
                .with(ADMIN_CREDENTIALS)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .contentType(APPLICATION_XML_VALUE)
                .content(resourceExample))
                .andExpect(status().isCreated());

        mvc.perform(get(urPath))
                .andExpect(content().xml(resourceExample));

    }

    @Test
    public void existingServiceMetadataCanBeRetrievedByEverybody() throws Exception {

        String participantId = UUID.randomUUID().toString();
        String documentId = UUID.randomUUID().toString();
        String resourceExample = getSampleServiceGroupBody(IDENTIFIER_SCHEME, participantId);
        String urlPath = format("/%s::%s", IDENTIFIER_SCHEME, participantId);
        String docUrlPath = format("%s/services/%s::%s", urlPath, DOCUMENT_SCHEME, documentId);

        String xmlMD = generateServiceMetadata(participantId, IDENTIFIER_SCHEME, documentId, DOCUMENT_SCHEME, "test");
        // crate service group
        mvc.perform(put(urlPath)
                .with(ADMIN_CREDENTIALS)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .contentType(APPLICATION_XML_VALUE)
                .content(resourceExample))
                .andExpect(status().isCreated());
        // add service metadata
        mvc.perform(put(docUrlPath)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)

                .content(xmlMD))
                .andExpect(status().isCreated());

        mvc.perform(get(urlPath))
                .andExpect(content().xml(generateExpectedServiceGroup("http://localhost/", IDENTIFIER_SCHEME, participantId, DOCUMENT_SCHEME, documentId)));

        mvc.perform(get(docUrlPath))
                .andExpect(status().isOk());
    }

    @Test
    public void getExistingServiceMetadataWithReverseProxyHost() throws Exception {
        //given
        String participantId = UUID.randomUUID().toString();
        String documentId = UUID.randomUUID().toString();
        String urlPath = format("/%s::%s", IDENTIFIER_SCHEME, participantId);
        prepareForGet(participantId,documentId);

        // when then..
        String expectedUrl = "http://ec.test.eu/";
        mvc.perform(get(urlPath)
                .header("X-Forwarded-Host", "ec.test.eu")
                .header("X-Forwarded-Port", "")
                .header("X-Forwarded-Proto", "http"))
                .andExpect(content().xml(generateExpectedServiceGroup(expectedUrl, IDENTIFIER_SCHEME, participantId, DOCUMENT_SCHEME, documentId)));
    }

    @Test
    public void getExistingServiceMetadataWithReverseNoProxyHost() throws Exception {
        //given
        String participantId = UUID.randomUUID().toString();
        String documentId = UUID.randomUUID().toString();
        String urlPath = format("/%s::%s", IDENTIFIER_SCHEME, participantId);
        prepareForGet(participantId,documentId);


        // when then..
        String expectedUrl = "http://localhost/";
        mvc.perform(get(urlPath)
                .header("X-Forwarded-Port", "")
                .header("X-Forwarded-Proto", "http"))
                .andExpect(content().xml(generateExpectedServiceGroup(expectedUrl, IDENTIFIER_SCHEME, participantId, DOCUMENT_SCHEME, documentId)));
    }

    @Test
    @Ignore
    public void getExistingServiceMetadataWithReverseProxyPort() throws Exception {
        //given
        String participantId = UUID.randomUUID().toString();
        String documentId = UUID.randomUUID().toString();
        String urlPath = format("/%s::%s", IDENTIFIER_SCHEME, participantId);
        prepareForGet(participantId,documentId);

        // when then..
        String expectedUrl = "https://ec.test.eu:8443/";
        mvc.perform(get(urlPath)
                .header("X-Forwarded-Host", "ec.test.eu:8443")
                .header("X-Forwarded-Proto", "https"))
                .andExpect(content().xml(generateExpectedServiceGroup(expectedUrl, IDENTIFIER_SCHEME, participantId, DOCUMENT_SCHEME, documentId)));
    }

    @Test
    public void getExistingServiceMetadataWithReverseProxySchema() throws Exception {
        //given
        String participantId = UUID.randomUUID().toString();
        String documentId = UUID.randomUUID().toString();
        String urlPath = format("/%s::%s", IDENTIFIER_SCHEME, participantId);
        prepareForGet(participantId,documentId);

        // when then..
        String expectedUrl = "https://ec.test.eu:8443/";
        mvc.perform(get(urlPath)
                .header("X-Forwarded-Port", "8443")
                .header("X-Forwarded-Host", "ec.test.eu")
                .header("X-Forwarded-Proto", "https"))
                .andExpect(content().xml(generateExpectedServiceGroup(expectedUrl, IDENTIFIER_SCHEME, participantId, DOCUMENT_SCHEME, documentId)));
    }

    @Test
    public void getExistingServiceMetadataWithReverseProxySkipDefaultPortHttps() throws Exception {
        //given
        String participantId = UUID.randomUUID().toString();
        String documentId = UUID.randomUUID().toString();
        String urlPath = format("/%s::%s", IDENTIFIER_SCHEME, participantId);

        LOG.info("Create service metadata: getExistingServiceMetadataWithReverseProxySkipDefaultPortHttps [{}]", urlPath);
        prepareForGet(participantId,documentId);

        // when then..
        String expectedUrl = "https://ec.test.eu/";
        mvc.perform(get(urlPath)
                .header("X-Forwarded-Port", "443")
                .header("X-Forwarded-Host", "ec.test.eu")
                .header("X-Forwarded-Proto", "https"))
                .andExpect(content().xml(generateExpectedServiceGroup(expectedUrl, IDENTIFIER_SCHEME, participantId, DOCUMENT_SCHEME, documentId)));
    }

    @Test
    public void getExistingServiceMetadataWithReverseProxySkipDefaultPortHttp() throws Exception {
        //given
        String participantId = UUID.randomUUID().toString();
        String documentId = UUID.randomUUID().toString();
        String urlPath = format("/%s::%s", IDENTIFIER_SCHEME, participantId);
        prepareForGet(participantId,documentId);

        // when then..
        String expectedUrl = "http://ec.test.eu/";
        mvc.perform(get(urlPath)
                .header("X-Forwarded-Port", "80")
                .header("X-Forwarded-Host", "ec.test.eu")
                .header("X-Forwarded-Proto", "http"))
                .andExpect(content().xml(generateExpectedServiceGroup(expectedUrl, IDENTIFIER_SCHEME, participantId, DOCUMENT_SCHEME, documentId)));
    }

    @Test
    @Ignore
    public void getExistingServiceMetadataWithReverseProxyPortInHost() throws Exception {
        //given
        String participantId = UUID.randomUUID().toString();
        String documentId = UUID.randomUUID().toString();
        String urlPath = format("/%s::%s", IDENTIFIER_SCHEME, participantId);
        prepareForGet(participantId,documentId);

        // when then..
        String expectedUrl = "https://ec.test.eu:8443/";
        mvc.perform(get(urlPath)
                .header("X-Forwarded-Port", "8443")
                .header("X-Forwarded-Host", "ec.test.eu:8443")
                .header("X-Forwarded-Proto", "https"))
                .andExpect(content().xml(generateExpectedServiceGroup(expectedUrl, IDENTIFIER_SCHEME, participantId, DOCUMENT_SCHEME, documentId)));
    }

    public String generateExpectedServiceGroup(String expectedUrl, String resourceScheme, String resourceValue, String subresourceScheme, String subresourceValue) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<ServiceGroup xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\">" +
                "<ParticipantIdentifier scheme=\""+resourceScheme+"\">"+resourceValue+"</ParticipantIdentifier>" +
                "<ServiceMetadataReferenceCollection>" +
                "<ServiceMetadataReference href=\"" + generateEncodedURL(expectedUrl, resourceScheme, resourceValue,subresourceScheme,subresourceValue)+  "\"/>" +
                "</ServiceMetadataReferenceCollection></ServiceGroup>";
    }
    public String generateEncodedURL(String expectedUrl, String resourceScheme, String resourceValue,  String subresourceScheme, String subresourceValue){
        return expectedUrl + URLEncoder.encode(resourceScheme + "::" +  resourceValue) + "/services/"+  URLEncoder.encode(subresourceScheme + "::" +  subresourceValue);
    }

    @Test
    public void anonymousUserCannotCreateServiceGroup() throws Exception {

        String participantId = UUID.randomUUID().toString();
        String resourceExample = getSampleServiceGroupBody(IDENTIFIER_SCHEME, participantId);
        String urlPath = format("/%s::%s", IDENTIFIER_SCHEME, participantId);

        mvc.perform(put(urlPath)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .contentType(APPLICATION_XML_VALUE)
                .content(resourceExample))
                .andExpect(status().isUnauthorized());

        mvc.perform(get(urlPath))
                .andExpect(status().isNotFound());
    }

    @Test
    public void adminCanDeleteServiceGroup() throws Exception {
        String participantId = UUID.randomUUID().toString();
        String resourceExample = getSampleServiceGroupBody(IDENTIFIER_SCHEME, participantId);
        String urlPath = format("/%s::%s", IDENTIFIER_SCHEME, participantId);

        mvc.perform(put(urlPath)
                .with(ADMIN_CREDENTIALS)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .contentType(APPLICATION_XML_VALUE)
                .content(resourceExample))
                .andExpect(status().isCreated());

        mvc.perform(delete(urlPath)
                .with(ADMIN_CREDENTIALS))
                .andExpect(status().isOk());
        mvc.perform(get(urlPath))
                .andExpect(status().isNotFound());
    }

    @Test
    public void malformedInputReturnsBadRequest() throws Exception {

        String participantId = UUID.randomUUID().toString();
        String resourceExample = getSampleServiceGroupBody(IDENTIFIER_SCHEME, participantId);
        String urlPath = format("/%s::%s", IDENTIFIER_SCHEME, participantId);

        mvc.perform(put(urlPath)
                .with(ADMIN_CREDENTIALS)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .contentType(APPLICATION_XML_VALUE)
                .content("malformed input XML"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void invalidParticipantSchemeReturnsBadRequest() throws Exception {

        String participantId = UUID.randomUUID().toString();
        String scheme = "length-exceeeeeeds-25chars";
        String resourceExample = getSampleServiceGroupBody(scheme, participantId);
        String urlPath = format("/%s::%s", scheme, participantId);


        mvc.perform(put(urlPath)
                .with(ADMIN_CREDENTIALS)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .contentType(APPLICATION_XML_VALUE)
                .content(resourceExample))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void creatingServiceGroupUnderBadFormatedDomainReturnsBadRequest() throws Exception {
        String participantId = UUID.randomUUID().toString();
        String resourceExample = getSampleServiceGroupBody(IDENTIFIER_SCHEME, participantId);
        String urlPath = format("/%s::%s", IDENTIFIER_SCHEME, participantId);


        mvc.perform(put(urlPath)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)
                .header(HTTP_HEADER_KEY_DOMAIN, "not-existing-domain")
                .content(resourceExample))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(stringContainsInOrder("FORMAT_ERROR")));
    }

    @Test
    public void creatingServiceGroupUnderNotExistingDomainReturnsBadRequest() throws Exception {

        String participantId = UUID.randomUUID().toString();
        String resourceExample = getSampleServiceGroupBody(IDENTIFIER_SCHEME, participantId);
        String urlPath = format("/%s::%s", IDENTIFIER_SCHEME, participantId);

        mvc.perform(put(urlPath)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)
                .header(HTTP_HEADER_KEY_DOMAIN, "notExistingDomain")
                .content(resourceExample))
                .andExpect(status().isNotFound())
                .andExpect(content().string(stringContainsInOrder("NOT_FOUND")));
    }

    @Test
    public void adminCanAssignNewServiceGroupToOtherOwner() throws Exception {
        String participantId = UUID.randomUUID().toString();
        String resourceExample = getSampleServiceGroupBody(IDENTIFIER_SCHEME, participantId);
        String urlPath = format("/%s::%s", IDENTIFIER_SCHEME, participantId);


        mvc.perform(put(urlPath)
                .with(ADMIN_CREDENTIALS)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .contentType(APPLICATION_XML_VALUE)
                .header(HTTP_HEADER_KEY_SERVICE_GROUP_OWNER, OTHER_OWNER_NAME_URL_ENCODED)
                .content(resourceExample))
                .andExpect(status().isCreated());
    }

    @Test
    public void adminCannotAssignNewServiceGroupToNotExistingOwner() throws Exception {
        String participantId = UUID.randomUUID().toString();
        String resourceExample = getSampleServiceGroupBody(IDENTIFIER_SCHEME, participantId);
        String urlPath = format("/%s::%s", IDENTIFIER_SCHEME, participantId);

        mvc.perform(put(urlPath)
                .with(ADMIN_CREDENTIALS)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .contentType(APPLICATION_XML_VALUE)
                .header(HTTP_HEADER_KEY_SERVICE_GROUP_OWNER, "not-existing-user")
                .content(resourceExample))
                .andExpect(status().isBadRequest());
    }

    public void prepareForGet(String participantId, String documentId) throws Exception {

        String urlPath = format("/%s::%s", IDENTIFIER_SCHEME, participantId);
        String docUrlPath = format("%s/services/%s::%s", urlPath, DOCUMENT_SCHEME, documentId);
        String xmlSG = getSampleServiceGroupBody(IDENTIFIER_SCHEME, participantId);
        String xmlMD = generateServiceMetadata(participantId, IDENTIFIER_SCHEME, documentId, DOCUMENT_SCHEME, "test");
        // crate service group

        LOG.info("create service service group: [{}]", docUrlPath);
        mvc.perform(put(urlPath)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)
                .content(xmlSG))
                .andExpect(status().isCreated());
        // add service metadata
        LOG.info("create service metadata: [{}]", docUrlPath);
        ResultActions actions = mvc.perform(put(docUrlPath)
                .header(HTTP_HEADER_KEY_DOMAIN, HTTP_DOMAIN_VALUE)
                .with(ADMIN_CREDENTIALS)
                .contentType(APPLICATION_XML_VALUE)
                .content(xmlMD))
                .andExpect(status().isCreated());

    }

}
