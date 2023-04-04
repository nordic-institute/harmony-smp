package eu.europa.ec.edelivery.smp.ui.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import eu.europa.ec.edelivery.smp.data.ui.*;
import eu.europa.ec.edelivery.smp.test.SmpTestWebAppConfig;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils.*;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SmpTestWebAppConfig.class})
@Sql(scripts = {
        "classpath:/cleanup-database.sql",
        "classpath:/webapp_integration_test_data.sql"},
        executionPhase = BEFORE_TEST_METHOD)
public class UserAdminResourceIntegrationTest {

    private static final String PATH_INTERNAL = ResourceConstants.CONTEXT_PATH_INTERNAL_USER;

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
    public void getUsers() throws Exception {
        MockHttpSession session = loginWithSystemAdmin(mvc);
        MvcResult result = mvc.perform(get(PATH_INTERNAL)
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();
        ServiceResult res = mapper.readValue(result.getResponse().getContentAsString(), ServiceResult.class);
        // then
        assertNotNull(res);
        assertEquals(7, res.getServiceEntities().size());
        res.getServiceEntities().forEach(sgMap -> {
            UserRO sgro = mapper.convertValue(sgMap, UserRO.class);
            assertNotNull(sgro.getUserId());
            assertNotNull(sgro.getUsername());
        });
    }

    @Test
    public void testUpdateUserList() throws Exception {
        // given when
        MockHttpSession session = loginWithSystemAdmin(mvc);

        SecurityMockMvcRequestPostProcessors.CsrfRequestPostProcessor csrf = csrf();
        MvcResult result = mvc.perform(get(PATH_INTERNAL)
                        .session(session)
                        .with(csrf))
                .andExpect(status().isOk()).andReturn();
        ServiceResult res = mapper.readValue(result.getResponse().getContentAsString(), ServiceResult.class);
        assertNotNull(res);
        assertFalse(res.getServiceEntities().isEmpty());
        UserRO userRO = mapper.convertValue(res.getServiceEntities().get(0), UserRO.class);
        // then
        userRO.setActive(!userRO.isActive());
        userRO.setEmailAddress("test@mail.com");

        if (userRO.getCertificate() == null) {
            userRO.setCertificate(new CertificateRO());
        }
        userRO.getCertificate().setCertificateId(UUID.randomUUID().toString());

        mvc.perform(put(PATH_INTERNAL)
                .session(session)
                .with(csrf)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Collections.singletonList(userRO)))
        ).andExpect(status().isOk());
    }

    @Test
    public void testUpdateUserListWrongAuthentication() throws Exception {
        // given when
        MockHttpSession session = loginWithSystemAdmin(mvc);
        MvcResult result = mvc.perform(get(PATH_INTERNAL)
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();
        ServiceResult res = mapper.readValue(result.getResponse().getContentAsString(), ServiceResult.class);
        assertNotNull(res);
        assertFalse(res.getServiceEntities().isEmpty());
        UserRO userRO = mapper.convertValue(res.getServiceEntities().get(0), UserRO.class);
        // then
        userRO.setActive(!userRO.isActive());
        userRO.setEmailAddress("test@mail.com");
        if (userRO.getCertificate() == null) {
            userRO.setCertificate(new CertificateRO());
        }
        userRO.getCertificate().setCertificateId(UUID.randomUUID().toString());
        // anonymous
        mvc.perform(put(PATH_INTERNAL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Collections.singletonList(userRO)))
        ).andExpect(status().isUnauthorized());

        MockHttpSession sessionSGAdmin = loginWithUserGroupAdmin(mvc);
        mvc.perform(put(PATH_INTERNAL)
                .session(sessionSGAdmin)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Collections.singletonList(userRO)))
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @Ignore
    public void testValidateDeleteUserOK() throws Exception {

        // login
        MockHttpSession session = loginWithSystemAdmin(mvc);
        // get list
        MvcResult result = mvc.perform(get(PATH_INTERNAL)
                        .with(csrf())
                        .session(session))
                .andExpect(status().isOk()).andReturn();
        ServiceResult res = mapper.readValue(result.getResponse().getContentAsString(), ServiceResult.class);
        assertNotNull(res);
        assertFalse(res.getServiceEntities().isEmpty());
        UserRO userRO = mapper.convertValue(res.getServiceEntities().get(0), UserRO.class);

        MvcResult resultDelete = mvc.perform(post(PATH_INTERNAL + "/validate-delete")
                        .with(csrf())
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"" + userRO.getUserId() + "\"]"))
                .andExpect(status().isOk()).andReturn();

        DeleteEntityValidation dev = mapper.readValue(resultDelete.getResponse().getContentAsString(), DeleteEntityValidation.class);

        assertFalse(dev.getListIds().isEmpty());
        assertTrue(dev.getListDeleteNotPermitedIds().isEmpty());
        assertEquals(userRO.getUserId(), dev.getListIds().get(0));
    }

    @Test
    public void testValidateDeleteLoggedUserNotOK() throws Exception {

        // login
        MockHttpSession session = loginWithSystemAdmin(mvc);
        // get list
        mvc.perform(get(PATH_INTERNAL)
                        .with(csrf())
                        .session(session))
                .andExpect(status().isOk()).andReturn();
        UserRO userRO = getLoggedUserData(mvc, session);

        // note system credential has id 3!
        MvcResult resultDelete = mvc.perform(post(PATH_INTERNAL + "/validate-delete")
                        .with(csrf())
                        .session(session)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("[\"" + userRO.getUserId() + "\"]"))
                .andExpect(status().isOk())
                .andReturn();

        DeleteEntityValidation res = mapper.readValue(resultDelete.getResponse().getContentAsString(), DeleteEntityValidation.class);

        assertTrue(res.getListIds().isEmpty());
        assertEquals("Could not delete logged user!", res.getStringMessage());
    }


    @Test
    @Ignore
    public void generateAccessTokenForUser() throws Exception {
        MockHttpSession sessionAdmin = loginWithSystemAdmin(mvc);
        UserRO userROAdmin = getLoggedUserData(mvc, sessionAdmin);

        MvcResult resultUsers = mvc.perform(get(PATH_INTERNAL)
                        .session(sessionAdmin)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();
        ServiceResult res = mapper.readValue(resultUsers.getResponse().getContentAsString(), ServiceResult.class);
        Map userROToUpdate = (Map) res.getServiceEntities().stream()
                .filter(userMap ->
                        StringUtils.equals(SG_USER2_USERNAME, (String) ((Map) userMap).get("username"))).findFirst().get();

        MvcResult result = mvc.perform(post(PATH_INTERNAL + "/" + userROAdmin.getUserId() + "/generate-access-token-for/" + userROToUpdate.get("userId"))
                .with(csrf())
                .session(sessionAdmin)
                .content(SYS_ADMIN_PASSWD)
        ).andExpect(status().isOk()).andReturn();


        AccessTokenRO resAccessToken = mapper.readValue(result.getResponse().getContentAsString(), AccessTokenRO.class);
        assertNotNull(resAccessToken);
        assertNotNull(resAccessToken.getIdentifier());
        assertNotNull(resAccessToken.getValue());

    }

    @Test
    @Ignore
    public void changePasswordForUser() throws Exception {
        MockHttpSession sessionAdmin = loginWithSystemAdmin(mvc);
        UserRO userROAdmin = getLoggedUserData(mvc, sessionAdmin);

        MvcResult resultUsers = mvc.perform(get(PATH_INTERNAL)
                        .session(sessionAdmin)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();
        ServiceResult res = mapper.readValue(resultUsers.getResponse().getContentAsString(), ServiceResult.class);
        Map userROToUpdate = (Map) res.getServiceEntities().stream()
                .filter(userMap ->
                        StringUtils.equals(SG_USER2_USERNAME, (String) ((Map) userMap).get("username"))).findFirst().get();
        String newPassword = "TESTtest1234!@#$";


        PasswordChangeRO newPass = new PasswordChangeRO();
        newPass.setUsername(SG_USER2_USERNAME);
        newPass.setCurrentPassword(SYS_ADMIN_PASSWD);
        newPass.setNewPassword(newPassword);
        assertNotEquals(newPassword, SG_USER2_PASSWD);

        mvc.perform(put(PATH_INTERNAL + "/" + userROAdmin.getUserId() + "//change-password-for/" + userROToUpdate.get("userId"))
                .with(csrf())
                .session(sessionAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newPass))
        ).andExpect(status().isOk()).andReturn();

        // test to login with new password
        MockHttpSession sessionNew = loginWithCredentials(mvc, SG_USER2_USERNAME, newPassword);
        assertNotNull(sessionNew);
    }

}
