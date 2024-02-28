/*-
 * #START_LICENSE#
 * DomiSMP
 * %%
 * Copyright 2017 European Commission | CEF eDelivery
 * %%
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

package eu.europa.ec.edelivery.smp.server.security;


import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.data.dao.ConfigurationDao;
import eu.europa.ec.edelivery.smp.test.SmpTestWebAppConfig;
import eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils;
import eu.europa.ec.edelivery.smp.test.testutils.X509CertificateTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by gutowpa on 20/02/2017.
 */
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SmpTestWebAppConfig.class})
@DirtiesContext
@Sql(scripts = {
        "classpath:/cleanup-database.sql",
        "classpath:/webapp_integration_test_data.sql"},
        executionPhase = BEFORE_TEST_METHOD)
public class SecurityConfigurationClientCertTest {
    public static final Logger LOG = LoggerFactory.getLogger(SecurityConfigurationClientCertTest.class);

    //Jul++9+23:59:00+2019+GMT"
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM  dd HH:mm:ss yyyy 'GMT'");
    private static final String CLIENT_CERT_FORMAT = "sno=%s" +
            "&subject=%s" +
            "&validfrom=%s" +
            "&validto=%s" +
            "&issuer=%s";

    public static final String RETURN_LOGGED_USER_PATH = "/getLoggedUsername";

    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {
                        "Simple case",
                        "CN=common name,O=org,C=BE:000000000000bb66",
                        "C=BE,O=org,CN=common name",
                        "bb66",
                },
                {
                        "Upper serial number",
                        "CN=common name,O=org,C=BE:000000000000bb66",
                        "C=BE,O=org,CN=common name",
                        "BB66",
                },
                {
                        "Serial number with colon",
                        "CN=common name,O=org,C=BE:000000000000bb66",
                        "C=BE,O=org,CN=common name",
                        "BB:66",
                },
                {
                        "Email, serial number, street, towm formatted asl BC before  ",
                        "CN=common name,O=org,C=BE:000000000000bb66",
                        "CN=common name/emailAddress\\=CEF-EDELIVERY-SUPPORT@ec.europa.eu/serialNumber\\=1,O=org,ST=My town/postalCode\\=2151, L=GreatTown/street\\=My Street. 20, C=BE",
                        "BB66",
                },
                {
                        "Email, serial number, street, town formatted asl BC before = is not escaped  ",
                        "CN=common name,O=org,C=BE:000000000000bb66",
                        "CN=common name/emailAddress=CEF-EDELIVERY-SUPPORT@ec.europa.eu/serialNumber=1,O=org,ST=My town/postalCode=2151, L=GreatTown/street=My Street. 20, C=BE",
                        "BB66",
                },
                {
                        "Email, serial number, street, town formatted - new RP",
                        "CN=common name,O=org,C=BE:000000000000bb66",
                        "C=BE,ST=My town, postalCode=2151, L=GreatTown, street=My Street. 20, O=org,CN=common name, emailAddress=CEF-EDELIVERY-SUPPORT@ec.europa.eu,common name, serialNumber=1",
                        "BB66",
                },
                {
                        "Test with colon",
                        "CN=GRP:test_proxy_01,O=European Commission,C=BE:0000000000001234",
                        "CN=GRP:test_proxy_01,OU=CEF_eDelivery.europa.eu,OU=SML,OU=testabc,O=European Commission,C=BE",
                        "1234",
                },
                {
                        "Test with Utf8 chars - in url encoded",
                        "CN=GRP:TEST_\\\\+\\\\,& \\\\=eau!,O=European Commission,C=BE:0000000000001234",
                        "C%3DBE%2C+O%3DEuropean+Commission%2C+OU%3DCEF_eDelivery.europa.eu%2C+OU%3Dtestabc%2C+OU%3DSMP%2C+CN%3DGRP%3ATEST_%2B%2C%26+%3D%5CxC3%5CxA9%5CxC3%5CxA1%5CxC5%5CxB1%21%2FemailAddress%3DCEF-EDELIVERY-SUPPORT%40ec.europa.eu",
                        "1234",
                },
                {
                        "Issue test one",
                        "CN=ncp.fi.ehealth.testa.eu,O=Kansanelakelaitos,C=FI:f71ee8b11cb3b787",
                        "C=FI, O=Kansanelakelaitos, OU=CEF_eDelivery.testa.eu, OU=eHealth, OU=Kanta, CN=ncp.fi.ehealth.testa.eu, emailAddress=tekninentuki@kanta.fi",
                        "f71ee8b11cb3b787",
                },
                {
                        "Issue test two",
                        "CN=Internal Business CA 2,O=T-Systems International GmbH,C=DE:f71ee8b11cb3b787",
                        "C=DE, O=T-Systems International GmbH, OU=T-Systems Trust Center, ST=Nordrhein Westfalen, postalCode=57250, L=Netphen, street=Untere Industriestr. 20, CN=Internal Business CA 2",
                        "f71ee8b11cb3b787",
                },
        });
    }

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ConfigurationDao configurationDao;

    MockMvc mvc;

    @BeforeEach
    public void setup() throws IOException {
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED, "true", "");
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.CLIENT_CERT_HEADER_ENABLED_DEPRECATED, "true", "");
        configurationDao.reloadPropertiesFromDatabase();

        X509CertificateTestUtils.reloadKeystores();
        mvc = MockMvcUtils.initializeMockMvc(context);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void validClientCertHeaderAuthorizedForPutTest(
            String testName,
            String expectedCertificateId,
            String certificateDn,
            String serialNumber
    ) throws Exception {
        LOG.info("Test: [{}]", testName);
        String clientCert = buildClientCert(serialNumber, certificateDn);
        System.out.println("Client-Cert: " + clientCert);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Client-Cert", clientCert);
        mvc.perform(MockMvcRequestBuilders.put(RETURN_LOGGED_USER_PATH)
                        .headers(headers).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(expectedCertificateId)))
                .andReturn().getResponse().getContentAsString();
    }

    public static String buildClientCert(String serial, String subject) {
        OffsetDateTime from = OffsetDateTime.now().minusYears(1);
        OffsetDateTime to = OffsetDateTime.now().plusYears(1);
        return buildClientCert(serial, subject, "C=x,O=y,CN=z", from, to);
    }

    public static String buildClientCert(String serial, String subject, String issuer, OffsetDateTime from, OffsetDateTime to) {

        return String.format(CLIENT_CERT_FORMAT, serial, subject, formatToGMTString(from), formatToGMTString(to), issuer);
    }

    public static String formatToGMTString(OffsetDateTime time) {
        return DATE_FORMATTER.format(time);
    }

}
