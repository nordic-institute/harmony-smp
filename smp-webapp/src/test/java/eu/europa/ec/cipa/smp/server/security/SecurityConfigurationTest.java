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

import eu.europa.ec.edelivery.exception.BlueCoatParseException;
import eu.europa.ec.edelivery.smp.config.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by gutowpa on 20/02/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        PropertiesTestConfig.class,
        SmpAppConfig.class,
        SmpWebAppConfig.class,
        DatabaseConfig.class,
        SpringSecurityConfig.class,
        SpringSecurityTestConfig.class,
})
@WebAppConfiguration
@Sql("classpath:/cleanup-database.sql")
@Sql("classpath:/webapp_integration_test_data.sql")
public class SecurityConfigurationTest {

    public static final String RETURN_LOGGED_USER_PATH = "/getLoggedUsername";

    public static final String TEST_USERNAME_DB_CLEAR_PASS = "test_user_clear_pass";
    public static final String TEST_USERNAME_DB_HASHED_PASS = "test_user_hashed_pass";
    public static final String PASSWORD = "test123";
    public static final String BLUE_COAT_VALID_HEADER = "sno=bb66&subject=C=BE,O=org,CN=common name&validfrom=Dec 6 17:41:42 2016 GMT&validto=Jul 9 23:59:00 2050 GMT&issuer=C=x,O=y,CN=z";
    public static final String BLUE_COAT_VALID_HEADER_UPPER_SN = "sno=BB66&subject=C=BE,O=org,CN=common name&validfrom=Dec 6 17:41:42 2016 GMT&validto=Jul 9 23:59:00 2050 GMT&issuer=C=x,O=y,CN=z";
    public static final String TEST_USERNAME_BLUE_COAT = "CN=common name,O=org,C=BE:000000000000bb66";
    public static final String BLUE_COAT_VALID_HEADER_DB_UPPER_SN = "sno=BB66&subject=CN=common name UPPER database SN,O=org,C=BE&validfrom=Dec 6 17:41:42 2016 GMT&validto=Jul 9 23:59:00 2050 GMT&issuer=C=x,O=y,CN=z";
    public static final String TEST_USERNAME_BLUE_COAT__DB_UPPER_SN = "CN=common name UPPER database SN,O=org,C=BE:000000000000bb66";

    @Autowired
    private WebApplicationContext context;

    /*
    @PersistenceContext
    private EntityManager em;
    */

    MockMvc mvc;

    @Before
    public void setup() {

        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }


    @Test
    public void getMethodAccessiblePubliclyTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(RETURN_LOGGED_USER_PATH))
                .andExpect(status().isOk())
                .andExpect(content().string("anonymousUser"));
    }

    @Test
    public void notAuthenticatedUserCannotCallPutTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.put(RETURN_LOGGED_USER_PATH))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void notAuthenticatedUserCannotCallDeleteTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete(RETURN_LOGGED_USER_PATH))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void userStoredWithHashedPassIsAuthorizedForPutTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.put(RETURN_LOGGED_USER_PATH)
                .with(httpBasic(TEST_USERNAME_DB_HASHED_PASS, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().string(TEST_USERNAME_DB_HASHED_PASS));
    }

    @Test
    public void userStoredWithUpperCaseUsernameIsAuthorizedForPutTest() throws Exception {
        String upperCaseUsername = TEST_USERNAME_DB_HASHED_PASS.toUpperCase();
        // test that is not the same
        Assert.assertNotEquals(upperCaseUsername, TEST_USERNAME_DB_HASHED_PASS);

        mvc.perform(MockMvcRequestBuilders.put(RETURN_LOGGED_USER_PATH)
                .with(httpBasic(upperCaseUsername, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().string(upperCaseUsername));
    }




    @Test
    public void userStoredWithClearPassIsNotAuthorizedForPutTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.put(RETURN_LOGGED_USER_PATH)
                .with(httpBasic(TEST_USERNAME_DB_CLEAR_PASS, PASSWORD)))
                .andExpect(status().isUnauthorized());
    }


    @Test(expected = BlueCoatParseException.class)
    public void malformedBlueCoatHeaderNotAuthorizedTest() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Client-Cert", "malformed header value");
        mvc.perform(MockMvcRequestBuilders.put(RETURN_LOGGED_USER_PATH)
                .headers(headers))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void validBlueCoatHeaderAuthorizedForPutTest() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Client-Cert", BLUE_COAT_VALID_HEADER);
        mvc.perform(MockMvcRequestBuilders.put(RETURN_LOGGED_USER_PATH)
                .headers(headers))
                .andExpect(status().isOk())
                .andExpect(content().string(TEST_USERNAME_BLUE_COAT))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void validBlueCoatHeaderAuthorizedBeforeValidBasicAuthTest() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Client-Cert", BLUE_COAT_VALID_HEADER);
        mvc.perform(MockMvcRequestBuilders.put(RETURN_LOGGED_USER_PATH)
                .headers(headers)
                .with(httpBasic(TEST_USERNAME_DB_HASHED_PASS, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().string(TEST_USERNAME_BLUE_COAT));
    }

    @Test
    public void validBlueCoatHeaderAuthorizedBeforeValidBasicAuthTestUpper() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Client-Cert", BLUE_COAT_VALID_HEADER_UPPER_SN);
        mvc.perform(MockMvcRequestBuilders.put(RETURN_LOGGED_USER_PATH)
                .headers(headers)
                .with(httpBasic(TEST_USERNAME_DB_HASHED_PASS, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().string(TEST_USERNAME_BLUE_COAT));
    }


    @Test
    public void validBlueCoatHeaderAuthorizedBeforeValidBasicAuthTestDBUpperSN() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Client-Cert", BLUE_COAT_VALID_HEADER_DB_UPPER_SN);
        mvc.perform(MockMvcRequestBuilders.put(RETURN_LOGGED_USER_PATH)
                .headers(headers)
                .with(httpBasic(TEST_USERNAME_DB_HASHED_PASS, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().string(TEST_USERNAME_BLUE_COAT__DB_UPPER_SN));
    }

    @Test
    public void validBlueCoatHeaderAuthorizedBeforeValidBasicAuthTestUpperDBUpperSN() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Client-Cert", BLUE_COAT_VALID_HEADER_DB_UPPER_SN);
        mvc.perform(MockMvcRequestBuilders.put(RETURN_LOGGED_USER_PATH)
                .headers(headers)
                .with(httpBasic(TEST_USERNAME_DB_HASHED_PASS, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().string(TEST_USERNAME_BLUE_COAT__DB_UPPER_SN));
    }


}
