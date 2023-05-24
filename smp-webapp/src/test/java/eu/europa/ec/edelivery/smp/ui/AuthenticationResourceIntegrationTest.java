package eu.europa.ec.edelivery.smp.ui;

import eu.europa.ec.edelivery.smp.data.dao.ConfigurationDao;
import eu.europa.ec.edelivery.smp.data.dao.CredentialDao;
import eu.europa.ec.edelivery.smp.data.model.user.DBCredential;
import eu.europa.ec.edelivery.smp.services.ui.UIKeystoreService;
import eu.europa.ec.edelivery.smp.test.SmpTestWebAppConfig;
import eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils;
import eu.europa.ec.edelivery.smp.test.testutils.X509CertificateTestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SmpTestWebAppConfig.class})
@Sql(scripts = {
        "classpath:/cleanup-database.sql",
        "classpath:/webapp_integration_test_data.sql"},
        executionPhase = BEFORE_TEST_METHOD)
public class AuthenticationResourceIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationResourceIntegrationTest.class);

    private static final String PATH = ResourceConstants.CONTEXT_PATH_PUBLIC_SECURITY + "/authentication";

    @Autowired
    private WebApplicationContext webAppContext;
    @Autowired
    private UIKeystoreService uiKeystoreService;

    @Autowired
    private CredentialDao credentialDao;

    @Autowired
    private ConfigurationDao configurationDao;

    private MockMvc mvc;

    @Before
    public void setup() throws IOException {
        X509CertificateTestUtils.reloadKeystores();
        mvc = MockMvcUtils.initializeMockMvc(webAppContext);
        uiKeystoreService.refreshData();

    }

    @Test
    public void authenticateSuccessTest() throws Exception {
        // given when
        HttpSession session = mvc.perform(post(PATH)
                        .header("Content-Type", "application/json")
                        .content("{\"username\":\"smp_admin\",\"password\":\"test123\"}"))
                .andExpect(status().isOk()).andReturn()
                .getRequest()
                .getSession();

        assertNotNull(session);
    }


    @Test
    public void authenticateInvalidPasswordTest() throws Exception {
        // given when then
        mvc.perform(post(PATH)
                        .header("Content-Type", "application/json")
                        .content("{\"username\":\"smp_admin\",\"password\":\"test1235\"}"))
                .andExpect(status().isUnauthorized()).andReturn()
                .getRequest()
                .getSession();
    }

    @Test
    public void authenticateInvalidUsernameTest() throws Exception {

        // given when
        mvc.perform(post(PATH)
                        .header("Content-Type", "application/json")
                        .content("{\"username\":\"smp_admin1\",\"password\":\"test123\"}"))
                .andExpect(status().isUnauthorized()).andReturn()
                .getRequest()
                .getSession();
    }
}
