/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.utils;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


public class SMPCookieWriterTest {
    SMPCookieWriter testInstance = spy(new SMPCookieWriter());

    @Test
    public void generateSetCookieHeaderForName() {
        // given
        String generatedHeader = "JSESSION=this-is-test-example; HttpOnly; Max-Age=36000; Expires=Thu, 16 Sep 2021 19:41:30 +0200; Path=/path; SameSite=Strict";
        String sessionValue = "SessionValue";
        boolean isSecure = true;
        Integer maxAge = null;
        String path = null;
        String sameSite = "Lax";
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        doReturn("/request-context").when(request).getContextPath();
        doReturn(generatedHeader).when(testInstance).generateSetCookieHeader(SMPCookieWriter.SESSION_COOKIE_NAME, sessionValue, isSecure, maxAge, path, sameSite, request);

        // when
        testInstance.writeCookieToResponse(SMPCookieWriter.SESSION_COOKIE_NAME, sessionValue, isSecure, maxAge, path, sameSite, request, response);
        // then
        verify(response).setHeader(eq(HttpHeaders.SET_COOKIE), eq(generatedHeader));
    }
}
