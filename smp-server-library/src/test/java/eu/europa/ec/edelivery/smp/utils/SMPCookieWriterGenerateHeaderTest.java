package eu.europa.ec.edelivery.smp.utils;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsNot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.doReturn;

@RunWith(Parameterized.class)
public class SMPCookieWriterGenerateHeaderTest {

    // parameters
    String description;
    boolean isSecure;
    Integer maxAge;
    String path;
    String sameSite;
    String expectedResultContains;
    String expectedResultNotContains;

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection cookieWriterTestParameters() {
        return Arrays.asList(new Object[][]{
                {"Contains HttpOnly", false, 36000, "/path", "Strict",  "; HttpOnly", null},
                {"Test with secure off", false, 36000, "/path", "Strict",  null, "; secure"},
                {"Test with secure on", true, 36000, "/path", "Strict",  "; secure", null},
                {"MaxAge given", true, 123456, "/path", "Strict",  "; Max-Age=123456; Expires=", null},
                {"MaxAge not given", true, null, "/path", "Strict", null,  "; Max-Age="},
                {"SameSite: off", false, 36000, "/path", null,  null, "; SameSite="},
                {"SameSite: Strict", true, 36000, "/path", "Strict",  "; SameSite=Strict", null},
                {"SameSite: Lax", true, 36000, "/path", "Lax",  "; SameSite=Lax", null},
                {"SameSite: None", true, 36000, "/path", "None",  "; SameSite=None", null},
                {"Path: Null - set request context by default", true, 36000, null, "None",  "; Path=/request-context;", null},
                {"Path: user-defined-path", true, 36000, "/user-defined-path", "None",  "; Path=/user-defined-path", null},
        });
    }

    public SMPCookieWriterGenerateHeaderTest(String description, boolean isSecure, Integer maxAge, String path, String sameSite,String expectedResultContains, String expectedResultNotContains) {
        this.description = description;
        this.isSecure = isSecure;
        this.maxAge = maxAge;
        this.path = path;
        this.sameSite = sameSite;
        this.expectedResultContains = expectedResultContains;
        this.expectedResultNotContains = expectedResultNotContains;
    }

    // test instance
    SMPCookieWriter testInstance = new SMPCookieWriter();

    @Test
    public void generateSetCookieHeader() {
        // given
        String sessionID = UUID.randomUUID().toString();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        doReturn("/request-context").when(request).getContextPath();

        // when
        String result = testInstance.generateSetCookieHeader(MockHttpSession.SESSION_COOKIE_NAME, sessionID, isSecure, maxAge, path, sameSite,  request);

        // then
        assertThat(result, startsWith(MockHttpSession.SESSION_COOKIE_NAME+"="+sessionID));
        if (StringUtils.isNotEmpty(expectedResultContains)) {
            assertThat(result, containsString(expectedResultContains));
        }
        if (StringUtils.isNotEmpty(expectedResultNotContains)) {
            assertThat(result, IsNot.not(containsString(expectedResultNotContains)));
        }
    }
}