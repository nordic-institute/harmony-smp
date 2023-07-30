package eu.europa.ec.edelivery.smp.ui.internal;

import eu.europa.ec.edelivery.smp.data.ui.*;
import eu.europa.ec.edelivery.smp.ui.AbstractControllerTest;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils.*;
import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UserAdminControllerIT extends AbstractControllerTest {

    private static final String PATH_INTERNAL = ResourceConstants.CONTEXT_PATH_INTERNAL_USER;

    @BeforeEach
    public void setup() throws IOException {
        super.setup();
    }

    @Test
    public void getUsers() throws Exception {
        MockHttpSession session = loginWithSystemAdmin(mvc);
        MvcResult result = mvc.perform(get(PATH_INTERNAL)
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();
        ServiceResult res = getObjectMapper().readValue(result.getResponse().getContentAsString(), ServiceResult.class);
        // then
        assertNotNull(res);
        assertEquals(7, res.getServiceEntities().size());
        res.getServiceEntities().forEach(sgMap -> {
            UserRO sgro = getObjectMapper().convertValue(sgMap, UserRO.class);
            assertNotNull(sgro.getUserId());
            assertNotNull(sgro.getUsername());
        });
    }

    @Test
    public void testValidateDeleteUserOK() throws Exception {

        // login
        MockHttpSession session = loginWithSystemAdmin(mvc);
        // get list
        MvcResult result = mvc.perform(get(PATH_INTERNAL)
                        .with(csrf())
                        .session(session))
                .andExpect(status().isOk()).andReturn();
        ServiceResult res = getObjectMapper().readValue(result.getResponse().getContentAsString(), ServiceResult.class);
        assertNotNull(res);
        assertFalse(res.getServiceEntities().isEmpty());
        UserRO userRO = getObjectMapper().convertValue(res.getServiceEntities().get(0), UserRO.class);

        MvcResult resultDelete = mvc.perform(post(PATH_INTERNAL + "/validate-delete")
                        .with(csrf())
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"" + userRO.getUserId() + "\"]"))
                .andExpect(status().isOk()).andReturn();

        DeleteEntityValidation dev = getObjectMapper().readValue(resultDelete.getResponse().getContentAsString(), DeleteEntityValidation.class);

        assertFalse(dev.getListIds().isEmpty());
        assertFalse(dev.getListDeleteNotPermitedIds().isEmpty());
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

        DeleteEntityValidation res = getObjectMapper().readValue(resultDelete.getResponse().getContentAsString(), DeleteEntityValidation.class);

        assertTrue(res.getListIds().isEmpty());
        assertEquals("Could not delete logged user!", res.getStringMessage());
    }

    @Test
    public void changePasswordForUser() throws Exception {
        MockHttpSession sessionAdmin = loginWithSystemAdmin(mvc);
        UserRO userROAdmin = getLoggedUserData(mvc, sessionAdmin);

        MvcResult resultUsers = mvc.perform(get(PATH_INTERNAL)
                        .session(sessionAdmin)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();
        ServiceResult res = getObjectMapper().readValue(resultUsers.getResponse().getContentAsString(), ServiceResult.class);
        Map userROToUpdate = (Map) res.getServiceEntities().stream()
                .filter(userMap ->
                        StringUtils.equals(SG_USER2_USERNAME, (String) ((Map) userMap).get("username"))).findFirst().get();
        String newPassword = "TESTtest1234!@#$";


        PasswordChangeRO newPass = new PasswordChangeRO();
        newPass.setUsername(SG_USER2_USERNAME);
        newPass.setCurrentPassword(SYS_ADMIN_PASSWD);
        newPass.setNewPassword(newPassword);
        assertNotEquals(newPassword, SG_USER2_PASSWD);

        mvc.perform(put(PATH_INTERNAL + "/" + userROAdmin.getUserId() + "/change-password-for/" + userROToUpdate.get("userId"))
                .with(csrf())
                .session(sessionAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(newPass))
        ).andExpect(status().isOk()).andReturn();

        // test to login with new password
        MockHttpSession sessionNew = loginWithCredentials(mvc, SG_USER2_USERNAME, newPassword);
        assertNotNull(sessionNew);
    }

    @Test
    public void testGetUserData() throws Exception {
        MockHttpSession sessionAdmin = loginWithSystemAdmin(mvc);
        UserRO userROAdmin = getLoggedUserData(mvc, sessionAdmin);

        MvcResult resultUsers = mvc.perform(get(PATH_INTERNAL)
                        .session(sessionAdmin)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();
        ServiceResult res = getObjectMapper().readValue(resultUsers.getResponse().getContentAsString(), ServiceResult.class);
        Map userROToUpdate = (Map) res.getServiceEntities().stream()
                .filter(userMap ->
                        StringUtils.equals(SG_USER2_USERNAME, (String) ((Map) userMap).get("username"))).findFirst().get();

        MvcResult result =  mvc.perform(get(PATH_INTERNAL + "/" + userROAdmin.getUserId() + "/" + userROToUpdate.get("userId")+"/retrieve")
                .with(csrf())
                .session(sessionAdmin)
        ).andExpect(status().isOk()).andReturn();
        UserRO resultUser = getObjectMapper().readValue(result.getResponse().getContentAsString(), UserRO.class);

        assertNotNull(resultUser);
        assertNotNull(resultUser.getUserId());
        assertEquals(SG_USER2_USERNAME, resultUser.getUsername());
    }

    @Test
    public void testUpdateUserData() throws Exception {
        MockHttpSession sessionAdmin = loginWithSystemAdmin(mvc);
        UserRO userROAdmin = getLoggedUserData(mvc, sessionAdmin);

        MvcResult resultUsers = mvc.perform(get(PATH_INTERNAL)
                        .session(sessionAdmin)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();
        ServiceResult res = getObjectMapper().readValue(resultUsers.getResponse().getContentAsString(), ServiceResult.class);
        UserRO userROToUpdate = (UserRO) res.getServiceEntities().stream()
                .filter(userMap ->
                        StringUtils.equals(SG_USER2_USERNAME, (String) ((Map) userMap).get("username")))
                .findFirst()
                .map(o -> getObjectMapper().convertValue(o, UserRO.class)).get();

        userROToUpdate.setFullName(UUID.randomUUID().toString());
        // when
        MvcResult result =  mvc.perform(post(PATH_INTERNAL + "/" + userROAdmin.getUserId() + "/" + userROToUpdate.getUserId()+"/update")
                .with(csrf())
                .session(sessionAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(userROToUpdate))
        ).andExpect(status().isOk()).andReturn();
        UserRO resultUser = getObjectMapper().readValue(result.getResponse().getContentAsString(), UserRO.class);
        //then
        assertNotNull(resultUser);
        assertNotNull(resultUser.getUserId());
        assertEquals(SG_USER2_USERNAME, resultUser.getUsername());
        assertEquals(userROToUpdate.getFullName(), resultUser.getFullName());
    }

    @Test
    public void testUDeleteUserData() throws Exception {
        MockHttpSession sessionAdmin = loginWithSystemAdmin(mvc);
        UserRO userROAdmin = getLoggedUserData(mvc, sessionAdmin);

        MvcResult resultUsers = mvc.perform(get(PATH_INTERNAL)
                        .session(sessionAdmin)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();
        ServiceResult res = getObjectMapper().readValue(resultUsers.getResponse().getContentAsString(), ServiceResult.class);
        UserRO userROToUpdate = (UserRO) res.getServiceEntities().stream()
                .filter(userMap ->
                        StringUtils.equals(SG_USER2_USERNAME, (String) ((Map) userMap).get("username")))
                .findFirst()
                .map(o -> getObjectMapper().convertValue(o, UserRO.class)).get();


        // when
        MvcResult result =  mvc.perform(delete(PATH_INTERNAL + "/" + userROAdmin.getUserId() + "/" + userROToUpdate.getUserId()+"/delete")
                .with(csrf())
                .session(sessionAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getObjectMapper().writeValueAsString(userROToUpdate))
        ).andExpect(status().isOk()).andReturn();
        UserRO resultUser = getObjectMapper().readValue(result.getResponse().getContentAsString(), UserRO.class);
        //then
        assertNotNull(resultUser);
        assertNotNull(resultUser.getUserId());
        assertEquals(SG_USER2_USERNAME, resultUser.getUsername());

    }
}
