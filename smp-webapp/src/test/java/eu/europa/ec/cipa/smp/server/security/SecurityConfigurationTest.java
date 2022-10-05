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

package eu.europa.ec.cipa.smp.server.security;

import eu.europa.ec.cipa.smp.server.data.dbms.model.DBUser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by gutowpa on 20/02/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring-context.xml", "file:src/main/webapp/WEB-INF/spring-security.xml", "classpath:/spring-security-test-context.xml"})
@WebAppConfiguration
@Transactional
@Rollback(false)
public class SecurityConfigurationTest {

    public static final String RETURN_LOGGED_USER_PATH = "/getLoggedUsername";

    public static final String TEST_USERNAME_CLEAR_PASS = "test_user_clear_pass";
    public static final String TEST_USERNAME_HASHED_PASS = "test_user_hashed_pass";
    public static final String PASSWORD = "gutek123";

    public static final String BLUE_COAT_VALID_HEADER = "sno=66&subject=C=BE,O=org,CN=comon name&validfrom=Dec 6 17:41:42 2016 GMT&validto=Jul 9 23:59:00 2050 GMT&issuer=C=x,O=y,CN=z";
    public static final String TEST_USERNAME_BLUE_COAT = "CN=comon name,O=org,C=BE:0000000000000066";

    //both passwords represent the same value - clear and hashed
    private DBUser userHashedPass = createUser(TEST_USERNAME_HASHED_PASS, "$2a$06$k.Q/6anG4Eq/nNTZ0C1UIuAKxpr6ra5oaMkMSrlESIyA5jKEsUdyS");
    private DBUser userClearPass = createUser(TEST_USERNAME_CLEAR_PASS, PASSWORD);
    private DBUser userBlueCoat = createUser(TEST_USERNAME_BLUE_COAT, null);

    @Autowired
    private WebApplicationContext context;

    @PersistenceContext
    private EntityManager em;

    MockMvc mvc;

    @Before
    public void setup() {
        em.persist(userHashedPass);
        em.persist(userClearPass);
        em.persist(userBlueCoat);
        em.flush();

        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @After
    public void tearDown() {
        em.remove(userHashedPass);
        em.remove(userClearPass);
        em.remove(userBlueCoat);
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
                .with(httpBasic(TEST_USERNAME_HASHED_PASS, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().string(TEST_USERNAME_HASHED_PASS));
    }

    @Test
    public void userStoredWithClearPassIsNotAuthorizedForPutTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.put(RETURN_LOGGED_USER_PATH)
                .with(httpBasic(TEST_USERNAME_CLEAR_PASS, PASSWORD)))
                .andExpect(status().isUnauthorized());
    }

    @Test
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
                .with(httpBasic(TEST_USERNAME_HASHED_PASS, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().string(TEST_USERNAME_BLUE_COAT));
    }

    private DBUser createUser(String username, String pass) {
        DBUser user = new DBUser();
        user.setUsername(username);
        user.setPassword(pass);
        return user;
    }

}
