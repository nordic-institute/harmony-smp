package eu.europa.ec.edelivery.smp.utils;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.core.IsNot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


public class SMPCookieWriterTest {
    SMPCookieWriter testInstance = spy(new SMPCookieWriter());

    @Test
    public void generateSetCookieHeaderForName() {
        // given
        String generatedHeader = "JSESSION=this-is-test-example; HttpOnly; Max-Age=36000; Expires=Thu, 16 Sep 2021 19:41:30 +0200; Path=/path; SameSite=Strict";
        String sessionValue="SessionValue";
        boolean isSecure=true;
        Integer maxAge =null;
        String path =null;
        String sameSite="Lax";
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        doReturn("/request-context").when(request).getContextPath();
        doReturn(generatedHeader).when(testInstance).generateSetCookieHeader(SMPCookieWriter.SESSION_COOKIE_NAME,sessionValue, isSecure, maxAge, path, sameSite, request);

        // when
        testInstance.writeCookieToResponse(SMPCookieWriter.SESSION_COOKIE_NAME, sessionValue, isSecure, maxAge, path, sameSite, request, response);
        // then
        verify(response).setHeader( eq(HttpHeaders.SET_COOKIE),  eq(generatedHeader));
    }
}