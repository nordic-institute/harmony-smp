package eu.europa.ec.edelivery.smp.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.data.ui.DeleteEntityValidation;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.test.SmpTestWebAppConfig;
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
import java.util.Arrays;
import java.util.UUID;

import static eu.europa.ec.edelivery.smp.test.testutils.MockMvcUtils.*;
import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.CONTEXT_PATH_INTERNAL_USER;
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
public class UserResourceTest {

    private static final String PATH_PUBLIC = ResourceConstants.CONTEXT_PATH_PUBLIC_USER;

    @Autowired
    private WebApplicationContext webAppContext;

    private MockMvc mvc;

    ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() {
        mvc = initializeMockMvc(webAppContext);
    }

    @Test
    public void getUserList() throws Exception {

        MockHttpSession session = loginWithSystemAdmin(mvc);
        MvcResult result = mvc.perform(get(CONTEXT_PATH_INTERNAL_USER)
                .session(session)
                .with(csrf()))
                .andExpect(status().isOk()).andReturn();
        ServiceResult res = mapper.readValue(result.getResponse().getContentAsString(), ServiceResult.class);
        // then
        assertNotNull(res);
        assertEquals(10, res.getServiceEntities().size());
        res.getServiceEntities().forEach(sgMap -> {
            UserRO sgro = mapper.convertValue(sgMap, UserRO.class);
            assertNotNull(sgro.getUserId());
            assertNotNull(sgro.getUsername());
            assertNotNull(sgro.getRole());
        });
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
    public void testUpdateUserList() throws Exception {
        // given when
        MockHttpSession session = loginWithSystemAdmin(mvc);
        MvcResult result = mvc.perform(get(CONTEXT_PATH_INTERNAL_USER)
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
        userRO.setPassword(UUID.randomUUID().toString());
        if (userRO.getCertificate() == null) {
            userRO.setCertificate(new CertificateRO());
        }
        userRO.getCertificate().setCertificateId(UUID.randomUUID().toString());

        mvc.perform(put(CONTEXT_PATH_INTERNAL_USER)
                .session(session)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Arrays.asList(userRO)))
        ).andExpect(status().isOk());
    }

    @Test
    public void testUpdateUserListWrongAuthentication() throws Exception {
        // given when
        MockHttpSession session = loginWithSystemAdmin(mvc);
        MvcResult result = mvc.perform(get(CONTEXT_PATH_INTERNAL_USER)
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
        userRO.setPassword(UUID.randomUUID().toString());
        if (userRO.getCertificate() == null) {
            userRO.setCertificate(new CertificateRO());
        }
        userRO.getCertificate().setCertificateId(UUID.randomUUID().toString());
        // anonymous
        mvc.perform(put(CONTEXT_PATH_INTERNAL_USER)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Arrays.asList(userRO)))
        ).andExpect(status().isUnauthorized());

        MockHttpSession sessionSMPAdmin = loginWithSMPAdmin(mvc);
        mvc.perform(put(CONTEXT_PATH_INTERNAL_USER)
                .session(sessionSMPAdmin)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Arrays.asList(userRO)))
        ).andExpect(status().isUnauthorized());

        MockHttpSession sessionSGAdmin = loginWithServiceGroupUser(mvc);
        mvc.perform(put(CONTEXT_PATH_INTERNAL_USER)
                .session(sessionSGAdmin)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Arrays.asList(userRO)))
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void testValidateDeleteUserOK() throws Exception {

        // login
        MockHttpSession session = loginWithSystemAdmin(mvc);
        // get list
        MvcResult result = mvc.perform(get(CONTEXT_PATH_INTERNAL_USER)
                .with(csrf())
                .session(session))
                .andExpect(status().isOk()).andReturn();
        ServiceResult res = mapper.readValue(result.getResponse().getContentAsString(), ServiceResult.class);
        assertNotNull(res);
        assertFalse(res.getServiceEntities().isEmpty());
        UserRO userRO = mapper.convertValue(res.getServiceEntities().get(0), UserRO.class);

        MvcResult resultDelete = mvc.perform(post(CONTEXT_PATH_INTERNAL_USER + "/validate-delete")
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
        MvcResult result = mvc.perform(get(CONTEXT_PATH_INTERNAL_USER)
                .with(csrf())
                .session(session))
                .andExpect(status().isOk()).andReturn();
        UserRO userRO = getLoggedUserData(mvc, session);

        // note system credential has id 3!
        MvcResult resultDelete = mvc.perform(post(CONTEXT_PATH_INTERNAL_USER + "/validate-delete")
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

}