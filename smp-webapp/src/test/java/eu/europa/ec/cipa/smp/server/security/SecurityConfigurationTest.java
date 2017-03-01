package eu.europa.ec.cipa.smp.server.security;

import eu.europa.ec.cipa.smp.server.data.dbms.model.DBUser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by gutowpa on 20/02/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring-context.xml", "file:src/main/webapp/WEB-INF/spring-security.xml"})
@WebAppConfiguration
@Transactional
@Rollback(false)
public class SecurityConfigurationTest {

    public static final String TEST_USERNAME_CLEAR_PASS = "test_user_clear_pass";
    public static final String TEST_USERNAME_HASHED_PASS = "test_user_hashed_pass";
    public static final String PASSWORD = "gutek123";

    //both passwords represent the same value - clear and hashed
    private DBUser userHashedPass = createUser(TEST_USERNAME_HASHED_PASS, "$2a$06$k.Q/6anG4Eq/nNTZ0C1UIuAKxpr6ra5oaMkMSrlESIyA5jKEsUdyS");
    private DBUser userClearPass = createUser(TEST_USERNAME_CLEAR_PASS, PASSWORD);

    @Autowired
    private WebApplicationContext context;

    @PersistenceContext
    private EntityManager em;

    MockMvc mvc;

    @Before
    public void setup() {
        em.persist(userHashedPass);
        em.persist(userClearPass);
        em.flush();

        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @After
    public void tearDown() {
        em.remove(userHashedPass);
        em.remove(userClearPass);
    }

    @Test
    public void userStoredWithClearPassIsNotAuthorizedTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/")
                .with(httpBasic(TEST_USERNAME_CLEAR_PASS, PASSWORD)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void userStoredWithHashedPassIsAuthorizedTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/")
                .with(httpBasic(TEST_USERNAME_HASHED_PASS, PASSWORD)))
                .andExpect(status().isNotFound()); // no Controller registered, so 404 is expected
    }

    private DBUser createUser(String username, String pass) {
        DBUser user = new DBUser();
        user.setUsername(username);
        user.setPassword(pass);
        return user;
    }

}
