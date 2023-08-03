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

package eu.europa.ec.cipa.smp.server.security;

import eu.europa.ec.edelivery.exception.ClientCertParseException;
import eu.europa.ec.edelivery.smp.data.dao.ConfigurationDao;
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;
import eu.europa.ec.edelivery.smp.test.SmpTestWebAppConfig;
import eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils;
import eu.europa.ec.edelivery.smp.test.testutils.X509CertificateTestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by gutowpa on 20/02/2017.
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SmpTestWebAppConfig.class})
@Sql(scripts = {
        "classpath:/cleanup-database.sql",
        "classpath:/webapp_integration_test_data.sql"},
        executionPhase = BEFORE_TEST_METHOD)
@DirtiesContext
public class SecurityConfigurationTest {

    public static final String RETURN_LOGGED_USER_PATH = "/getLoggedUsername";
    public static final String TEST_USERNAME_DB_CLEAR_PASS = "test_pat_clear_pass";
    public static final String TEST_USERNAME_DB_HASHED_PASS = "test_pat_hashed_pass";
    public static final String PASSWORD = "123456";
    public static final String CLIENT_CERT_VALID_HEADER = "sno=bb66&subject=C=BE,O=org,CN=common name&validfrom=Dec 6 17:41:42 2016 GMT&validto=Jul 9 23:59:00 2050 GMT&issuer=C=x,O=y,CN=z";
    public static final String CLIENT_CERT_VALID_HEADER_UPPER_SN = "sno=BB66&subject=C=BE,O=org,CN=common name&validfrom=Dec 6 17:41:42 2016 GMT&validto=Jul 9 23:59:00 2050 GMT&issuer=C=x,O=y,CN=z";
    public static final String TEST_USERNAME_CLIENT_CERT = "CN=common name,O=org,C=BE:000000000000bb66";
    public static final String CLIENT_CERT_VALID_HEADER_DB_UPPER_SN = "sno=BB66&subject=CN=common name UPPER database SN,O=org,C=BE&validfrom=Dec 6 17:41:42 2016 GMT&validto=Jul 9 23:59:00 2050 GMT&issuer=C=x,O=y,CN=z";
    public static final String TEST_USERNAME_CLIENT_CERT_DB_UPPER_SN = "CN=common name UPPER database SN,O=org,C=BE:000000000000bb66";
    public static final String CLIENT_CERT_NOT_AUTHORIZED_HEADER = "sno=bb61&subject=C=BE,O=org,CN=common name not exists&validfrom=Dec 6 17:41:42 2016 GMT&validto=Jul 9 23:59:00 2050 GMT&issuer=C=x,O=y,CN=z";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ConfigurationDao configurationDao;

    MockMvc mvc;

    @Before
    public void setup() throws IOException {
        X509CertificateTestUtils.reloadKeystores();
        configurationDao.setPropertyToDatabase(SMPPropertyEnum.EXTERNAL_TLS_AUTHENTICATION_CLIENT_CERT_HEADER_ENABLED,"true", null);
        mvc = MockMvcUtils.initializeMockMvc(context);
        configurationDao.reloadPropertiesFromDatabase();
    }

    @Test
    public void getMethodAccessiblePubliclyTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(RETURN_LOGGED_USER_PATH)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("anonymousUser")));
    }

    @Test
    public void notAuthenticatedUserCannotCallPutTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.put(RETURN_LOGGED_USER_PATH)
                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void notAuthenticatedUserCannotCallDeleteTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete(RETURN_LOGGED_USER_PATH)
                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void userStoredWithHashedPassIsAuthorizedForPutTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.put(RETURN_LOGGED_USER_PATH)
                .with(httpBasic(TEST_USERNAME_DB_HASHED_PASS, PASSWORD))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(TEST_USERNAME_DB_HASHED_PASS)));
    }

    @Test
    public void userStoredWithUpperCaseUsernameIsAuthorizedForPutTestIdCaseSensitive() throws Exception {
        String upperCaseUsername = TEST_USERNAME_DB_HASHED_PASS.toUpperCase();
        // test that is not the same
        Assert.assertNotEquals(upperCaseUsername, TEST_USERNAME_DB_HASHED_PASS);

        mvc.perform(MockMvcRequestBuilders.put(RETURN_LOGGED_USER_PATH)
                .with(httpBasic(upperCaseUsername, PASSWORD))
                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void userStoredWithClearPassIsNotAuthorizedForPutTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.put(RETURN_LOGGED_USER_PATH)
                .with(httpBasic(TEST_USERNAME_DB_CLEAR_PASS, PASSWORD)).with(csrf()))
                .andExpect(status().isUnauthorized());
    }


    @Test(expected = ClientCertParseException.class)
    public void malformedClientCertHeaderNotAuthorizedTest() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Client-Cert", "malformed header value");
        mvc.perform(MockMvcRequestBuilders.put(RETURN_LOGGED_USER_PATH)
                .headers(headers).with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void validClientCertHeaderAuthorizedForPutTest() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Client-Cert", CLIENT_CERT_VALID_HEADER);
        String result = mvc.perform(MockMvcRequestBuilders.put(RETURN_LOGGED_USER_PATH)
                .headers(headers)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(TEST_USERNAME_CLIENT_CERT)))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void ClientCertHeaderNotAuthorizedForPutTest() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Client-Cert", CLIENT_CERT_NOT_AUTHORIZED_HEADER);

        mvc.perform(MockMvcRequestBuilders.put(RETURN_LOGGED_USER_PATH)
                .headers(headers).with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Ignore
    public void validClientCertHeaderAuthorizedBeforeValidBasicAuthTest() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Client-Cert", CLIENT_CERT_VALID_HEADER);
        mvc.perform(MockMvcRequestBuilders.put(RETURN_LOGGED_USER_PATH)
                .headers(headers)
                .with(httpBasic(TEST_USERNAME_DB_HASHED_PASS, PASSWORD))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(TEST_USERNAME_CLIENT_CERT)));
    }

    @Test
    @Ignore
    public void validClientCertHeaderAuthorizedBeforeValidBasicAuthTestUpper() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Client-Cert", CLIENT_CERT_VALID_HEADER_UPPER_SN);
        mvc.perform(MockMvcRequestBuilders.put(RETURN_LOGGED_USER_PATH)
                .headers(headers)
                .with(httpBasic(TEST_USERNAME_DB_HASHED_PASS, PASSWORD))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(TEST_USERNAME_CLIENT_CERT)));
    }


    @Test
    @Ignore
    public void validClientCertHeaderAuthorizedBeforeValidBasicAuthTestDBUpperSN() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Client-Cert", CLIENT_CERT_VALID_HEADER_DB_UPPER_SN);
        mvc.perform(MockMvcRequestBuilders.put(RETURN_LOGGED_USER_PATH)
                .headers(headers)
                .with(httpBasic(TEST_USERNAME_DB_HASHED_PASS, PASSWORD))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(TEST_USERNAME_CLIENT_CERT_DB_UPPER_SN))).toString();
    }

    @Test
    @Ignore
    public void validClientCertHeaderAuthorizedBeforeValidBasicAuthTestUpperDBUpperSN() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Client-Cert", CLIENT_CERT_VALID_HEADER_DB_UPPER_SN);
        mvc.perform(MockMvcRequestBuilders.put(RETURN_LOGGED_USER_PATH)
                .headers(headers)
                .with(httpBasic(TEST_USERNAME_DB_HASHED_PASS, PASSWORD))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(TEST_USERNAME_CLIENT_CERT_DB_UPPER_SN)));
    }


}
