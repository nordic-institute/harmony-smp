package eu.europa.ec.edelivery.smp.test.testutils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.CONTEXT_PATH_PUBLIC_SECURITY;
import static eu.europa.ec.edelivery.smp.ui.ResourceConstants.CONTEXT_PATH_PUBLIC_SECURITY_AUTHENTICATION;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Collection on MVC Utility tools accessible via static methods.
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class MockMvcUtils {
    public static Logger LOG  = LoggerFactory.getLogger(MockMvcUtils.class);
    static ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    public static final String SYS_ADMIN_USERNAME = "sys_admin";
    public static final String SYS_ADMIN_PASSWD = "test123";
    public static final String SMP_ADMIN_USERNAME = "smp_admin";
    public static final String SMP_ADMIN_PASSWD = "test123";
    public static final String SG_USER_USERNAME = "sg_admin";
    public static final String SG_USER_PASSWD = "test123";

    public static final String SG_USER2_USERNAME = "test_user_hashed_pass";
    public static final String SG_USER2_PASSWD = "test123";

    public static final String MOCK_LOGGED_USER = "mock_logged_user";

    public static RequestPostProcessor getHttpBasicSystemAdminCredentials() {
        return httpBasic(SYS_ADMIN_USERNAME, SYS_ADMIN_PASSWD);
    }

    public static RequestPostProcessor getHttpBasicSMPAdminCredentials() {
        return httpBasic(SMP_ADMIN_USERNAME, SMP_ADMIN_PASSWD);
    }

    public static RequestPostProcessor getHttpBasicServiceGroupUserCredentials() {
        return httpBasic(SG_USER_USERNAME, SG_USER_PASSWD);
    }

    public static RequestPostProcessor getHttpBasicServiceGroupUser2Credentials() {
        return httpBasic(SG_USER2_USERNAME, SG_USER2_PASSWD);
    }

    /**
     * Login with system the username and data
     *
     * @param mvc
     * @return
     * @throws Exception
     */
    public static MockHttpSession loginWithSystemAdmin(MockMvc mvc) throws Exception {
        return loginWithCredentials(mvc, SYS_ADMIN_USERNAME, SYS_ADMIN_PASSWD);
    }

    /**
     * Login with SMP admin the username and data
     *
     * @param mvc
     * @return
     * @throws Exception
     */
    public static MockHttpSession loginWithUserGroupAdmin(MockMvc mvc) throws Exception {
        return loginWithCredentials(mvc, SG_USER_USERNAME, SG_USER_PASSWD);
    }

    public static MockHttpSession loginWithUser2(MockMvc mvc) throws Exception {
        return loginWithCredentials(mvc, SG_USER2_USERNAME, SG_USER2_PASSWD);
    }

    /**
     * Login with the username and data
     *
     * @param mvc
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public static MockHttpSession loginWithCredentials(MockMvc mvc, String username, String password) throws Exception {
        MvcResult result = mvc.perform(post(CONTEXT_PATH_PUBLIC_SECURITY_AUTHENTICATION)
                        .header(HttpHeaders.CONTENT_TYPE, org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk()).andReturn();
        // assert successful login
        byte[] asByteArray = result.getResponse().getContentAsByteArray();
        LOG.info("User logged with data: []", new String(asByteArray));

        UserRO userRO = mapper.readValue(asByteArray, UserRO.class);
        assertNotNull(userRO);
        MockHttpSession session = (MockHttpSession)result.getRequest().getSession();
        session.setAttribute(MOCK_LOGGED_USER, userRO);
        return session;
    }

    /**
     * Return currently logged in data for the session
     *
     * @param mvc
     * @param session
     * @return
     * @throws Exception
     */
    public static UserRO getLoggedUserData(MockMvc mvc, MockHttpSession session) throws Exception {
        MvcResult result = mvc.perform(get(CONTEXT_PATH_PUBLIC_SECURITY + "/user")
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();
        byte[] asByteArray = result.getResponse().getContentAsByteArray();
        LOG.info("User session validated with logged data: []", new String(asByteArray));
        return mapper.readValue(asByteArray, UserRO.class);
    }

    public static <T> T getObjectFromResponse(MvcResult result, Class<T> clazz)
            throws IOException {
        return mapper.readValue(result.getResponse().getContentAsByteArray(), clazz);
    }

    public static MockMvc initializeMockMvc(WebApplicationContext webAppContext) {
        MockMvc mvc = MockMvcBuilders.webAppContextSetup(webAppContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        MockServletContext sc = new MockServletContext("");
        ServletContextListener listener = new ContextLoaderListener(webAppContext);
        ServletContextEvent event = new ServletContextEvent(sc);
        return mvc;
    }

    public static <T> List<T> parseResponseArray(MvcResult result, Class<T> clazz) throws UnsupportedEncodingException, JsonProcessingException {
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
        return mapper.readValue(result.getResponse().getContentAsString(), collectionType);
    }

    public static <T> T parseResponse(MvcResult result, Class<T> clazz) throws IOException {
        return mapper.readValue(result.getResponse().getContentAsByteArray(), clazz);
    }

    public static String serializeObject(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }
}
