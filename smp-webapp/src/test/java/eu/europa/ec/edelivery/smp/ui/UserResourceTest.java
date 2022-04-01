package eu.europa.ec.edelivery.smp.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.edelivery.smp.config.PropertiesTestConfig;
import eu.europa.ec.edelivery.smp.config.SmpAppConfig;
import eu.europa.ec.edelivery.smp.config.SmpWebAppConfig;
import eu.europa.ec.edelivery.smp.config.SpringSecurityConfig;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.data.ui.DeleteEntityValidation;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.testutils.X509CertificateTestUtils;
import org.apache.commons.io.IOUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MediaType;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        PropertiesTestConfig.class,
        SmpAppConfig.class,
        SmpWebAppConfig.class,
        SpringSecurityConfig.class})
@WebAppConfiguration
@Sql("classpath:/cleanup-database.sql")
@Sql("classpath:/webapp_integration_test_data.sql")
@SqlConfig(encoding = "UTF-8")
public class UserResourceTest {

    private static final String PATH_INTERNAL = ResourceConstants.CONTEXT_PATH_INTERNAL_USER;
    private static final String PATH_PUBLIC = ResourceConstants.CONTEXT_PATH_PUBLIC_USER;
    private static final String PATH_AUTHENTICATION = ResourceConstants.CONTEXT_PATH_PUBLIC_SECURITY+"/authentication";


    @Autowired
    private WebApplicationContext webAppContext;

    private MockMvc mvc;
    private static final RequestPostProcessor ADMIN_CREDENTIALS = httpBasic("smp_admin", "test123");
    private static final RequestPostProcessor SYSTEM_CREDENTIALS = httpBasic("sys_admin", "test123");
    private static final RequestPostProcessor SG_ADMIN_CREDENTIALS = httpBasic("sg_admin", "test123");

    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(webAppContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        initServletContext();
    }

    private void initServletContext() {
        MockServletContext sc = new MockServletContext("");
        ServletContextListener listener = new ContextLoaderListener(webAppContext);
        ServletContextEvent event = new ServletContextEvent(sc);
    }

    @Test
    public void getUserList() throws Exception {
        // given when
        MvcResult result = mvc.perform(get(PATH_INTERNAL)
                .with(ADMIN_CREDENTIALS)
                .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        //them
        ObjectMapper mapper = new ObjectMapper();
        ServiceResult res = mapper.readValue(result.getResponse().getContentAsString(), ServiceResult.class);


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
    @Ignore
    public void testUpdateCurrentUserOK() throws Exception {

        // given when - log as SMP admin
        MvcResult result = mvc.perform(post(PATH_AUTHENTICATION)
                .header("Content-Type", "application/json")
                .content("{\"username\":\"smp_admin\",\"password\":\"test123\"}"))
                .andExpect(status().isOk()).andReturn();
        ObjectMapper mapper = new ObjectMapper();
        UserRO userRO = mapper.readValue(result.getResponse().getContentAsString(), UserRO.class);
        assertNotNull(userRO);

        MockHttpSession session = (MockHttpSession)result.getRequest().getSession();

        // when
        userRO.setActive(!userRO.isActive());
        userRO.setEmailAddress("test@mail.com");
        userRO.setPassword(UUID.randomUUID().toString());
        if (userRO.getCertificate() == null) {
            userRO.setCertificate(new CertificateRO());
        }
        userRO.getCertificate().setCertificateId(UUID.randomUUID().toString());

        mvc.perform(put(PATH_PUBLIC + "/" + userRO.getUserId())
                .with(ADMIN_CREDENTIALS)
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
        MvcResult result = mvc.perform(post(PATH_AUTHENTICATION)
                .header("Content-Type", "application/json")
                .content("{\"username\":\"smp_admin\",\"password\":\"test123\"}"))
                .andExpect(status().isOk()).andReturn();
        ObjectMapper mapper = new ObjectMapper();
        UserRO userRO = mapper.readValue(result.getResponse().getContentAsString(), UserRO.class);
        assertNotNull(userRO);

        // when
        userRO.setActive(!userRO.isActive());
        userRO.setEmailAddress("test@mail.com");
        userRO.setPassword(UUID.randomUUID().toString());
        if (userRO.getCertificate() == null) {
            userRO.setCertificate(new CertificateRO());
        }
        userRO.getCertificate().setCertificateId(UUID.randomUUID().toString());

        mvc.perform(put(PATH_PUBLIC + "/" + userRO.getUserId())
                .with(SYSTEM_CREDENTIALS)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userRO))
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdateUserList() throws Exception {
        // given when
        MvcResult result = mvc.perform(get(PATH_INTERNAL)
                .with(SYSTEM_CREDENTIALS)
                .with(csrf()))
                .andExpect(status().isOk()).andReturn();
        ObjectMapper mapper = new ObjectMapper();
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

        mvc.perform(put(PATH_INTERNAL)
                .with(SYSTEM_CREDENTIALS)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Arrays.asList(userRO)))
        ).andExpect(status().isOk());
    }

    @Test
    public void testUpdateUserListWrongAuthentication() throws Exception {
        // given when
        MvcResult result = mvc.perform(get(PATH_INTERNAL)
                .with(SYSTEM_CREDENTIALS)
                .with(csrf()))
                .andExpect(status().isOk()).andReturn();
        ObjectMapper mapper = new ObjectMapper();
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
        mvc.perform(put(PATH_INTERNAL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Arrays.asList(userRO)))
        ).andExpect(status().isUnauthorized());

        mvc.perform(put(PATH_INTERNAL)
                .with(ADMIN_CREDENTIALS)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Arrays.asList(userRO)))
        ).andExpect(status().isUnauthorized());

        mvc.perform(put(PATH_INTERNAL)
                .with(SG_ADMIN_CREDENTIALS)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Arrays.asList(userRO)))
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void testValidateDeleteUserOK() throws Exception {
        MvcResult result = mvc.perform(post(PATH_INTERNAL + "/validate-delete")
                .with(SYSTEM_CREDENTIALS)
                .with(csrf())
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("[5]"))
                .andExpect(status().isOk()).andReturn();

        ObjectMapper mapper = new ObjectMapper();
        DeleteEntityValidation res = mapper.readValue(result.getResponse().getContentAsString(), DeleteEntityValidation.class);

        assertFalse(res.getListIds().isEmpty());
        assertTrue(res.getListDeleteNotPermitedIds().isEmpty());
        assertEquals(5, res.getListIds().get(0).intValue());
    }

    @Test
    public void testValidateDeleteUserNotOK() throws Exception {
        // note system credential has id 3!
        MvcResult result = mvc.perform(post(PATH_INTERNAL + "/validate-delete")
                .with(SYSTEM_CREDENTIALS)
                .with(csrf())
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("[3]"))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        DeleteEntityValidation res = mapper.readValue(result.getResponse().getContentAsString(), DeleteEntityValidation.class);

        assertTrue(res.getListIds().isEmpty());
        assertEquals("Could not delete logged user!", res.getStringMessage());

    }

}