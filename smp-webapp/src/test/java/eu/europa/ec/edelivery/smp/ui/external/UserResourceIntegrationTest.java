package eu.europa.ec.edelivery.smp.ui.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import eu.europa.ec.edelivery.smp.data.ui.AccessTokenRO;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.data.ui.PasswordChangeRO;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.test.SmpTestWebAppConfig;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import javax.ws.rs.core.MediaType;
import java.util.UUID;

import static eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils.*;
import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.CONTEXT_PATH_PUBLIC_SECURITY_USER;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SmpTestWebAppConfig.class})
@Sql(scripts = {
        "classpath:/cleanup-database.sql",
        "classpath:/webapp_integration_test_data.sql"},
        executionPhase = BEFORE_TEST_METHOD)
public class UserResourceIntegrationTest {

    private static final String PATH_PUBLIC = ResourceConstants.CONTEXT_PATH_PUBLIC_USER;

    @Autowired
    private WebApplicationContext webAppContext;

    private MockMvc mvc;

    ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    @Before
    public void setup() {

        mvc = initializeMockMvc(webAppContext);
    }

    @Test
    public void testUpdateCurrentUserOK() throws Exception {
        // login
        MockHttpSession session = loginWithSMPAdmin(mvc);
        // when update data
        UserRO userRO = getLoggedUserData(mvc, session);
        userRO.setActive(!userRO.isActive());
        userRO.setEmailAddress("test@mail.com");
        if (userRO.getCertificate() == null) {
            userRO.setCertificate(new CertificateRO());
        }
        userRO.getCertificate().setCertificateId(UUID.randomUUID().toString());
        mvc.perform(put(PATH_PUBLIC + "/" + userRO.getUserId())
                .with(csrf())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userRO))
        ).andExpect(status().isOk()).andReturn();
    }

    @Test
    public void testUpdateCurrentUserNotAuthenticatedUser() throws Exception {

        // given when - log as SMP admin
        // then change values and list uses for changed value
        MockHttpSession session = loginWithSMPAdmin(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        assertNotNull(userRO);
        // when
        userRO.setActive(!userRO.isActive());
        userRO.setEmailAddress("test@mail.com");
        if (userRO.getCertificate() == null) {
            userRO.setCertificate(new CertificateRO());
        }
        userRO.getCertificate().setCertificateId(UUID.randomUUID().toString());

        mvc.perform(put(PATH_PUBLIC + "/" + userRO.getUserId())
                .with(getHttpBasicSystemAdminCredentials()) // authenticate with system admin
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userRO))
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void generateAccessTokenForUser() throws Exception {
        MockHttpSession session = loginWithServiceGroupUser2(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        assertNotNull(userRO);

        MvcResult result = mvc.perform(post(PATH_PUBLIC + "/" + userRO.getUserId() + "/generate-access-token")
                .with(csrf())
                .session(session)
                .contentType(MediaType.TEXT_PLAIN)
                .content(SG_USER2_PASSWD)
        ).andExpect(status().isOk()).andReturn();

        MvcResult resultUser = mvc.perform(get(CONTEXT_PATH_PUBLIC_SECURITY_USER)
                .with(csrf())
                .session(session)
        ).andExpect(status().isOk()).andReturn();

        UserRO updateUserData = mapper.readValue(resultUser.getResponse().getContentAsString(), UserRO.class);
        AccessTokenRO resAccessToken = mapper.readValue(result.getResponse().getContentAsString(), AccessTokenRO.class);
        assertNotNull(resAccessToken);
        assertNotEquals(userRO.getAccessTokenId(), resAccessToken.getIdentifier());
        assertNotEquals(userRO.getAccessTokenExpireOn(), resAccessToken.getExpireOn());
        assertEquals(updateUserData.getAccessTokenId(), resAccessToken.getIdentifier());
        assertEquals(updateUserData.getAccessTokenExpireOn(), resAccessToken.getExpireOn());
    }

    @Test
    public void changePassword() throws Exception {
        String newPassword = "TESTtest1234!@#$";

        MockHttpSession session = loginWithServiceGroupUser2(mvc);
        UserRO userRO = getLoggedUserData(mvc, session);
        assertNotNull(userRO);
        PasswordChangeRO newPass = new PasswordChangeRO();
        newPass.setUsername(SG_USER2_USERNAME);
        newPass.setCurrentPassword(SG_USER2_PASSWD);
        newPass.setNewPassword(newPassword);
        assertNotEquals(newPassword, SG_USER2_PASSWD);

        mvc.perform(put(PATH_PUBLIC + "/" + userRO.getUserId() + "/change-password")
                .with(csrf())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newPass))
        ).andExpect(status().isOk()).andReturn();

        // test to login with new password
        MockHttpSession sessionNew = loginWithCredentials(mvc, SG_USER2_USERNAME, newPassword);
        assertNotNull(sessionNew);
    }

}
