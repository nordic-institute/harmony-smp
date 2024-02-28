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

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.doReturn;

public class SMPCookieWriterGenerateHeaderTest {


    public static Collection cookieWriterTestParameters() {
        return Arrays.asList(new Object[][]{
                {"Contains HttpOnly", false, 36000, "/path", "Strict", "; HttpOnly", null},
                {"Test with secure off", false, 36000, "/path", "Strict", null, "; secure"},
                {"Test with secure on", true, 36000, "/path", "Strict", "; secure", null},
                {"MaxAge given", true, 123456, "/path", "Strict", "; Max-Age=123456; Expires=", null},
                {"MaxAge not given", true, null, "/path", "Strict", null, "; Max-Age="},
                {"SameSite: off", false, 36000, "/path", null, null, "; SameSite="},
                {"SameSite: Strict", true, 36000, "/path", "Strict", "; SameSite=Strict", null},
                {"SameSite: Lax", true, 36000, "/path", "Lax", "; SameSite=Lax", null},
                {"SameSite: None", true, 36000, "/path", "None", "; SameSite=None", null},
                {"Path: Null - set request context by default", true, 36000, null, "None", "; Path=/request-context;", null},
                {"Path: user-defined-path", true, 36000, "/user-defined-path", "None", "; Path=/user-defined-path", null},
        });
    }


    // test instance
    SMPCookieWriter testInstance = new SMPCookieWriter();

    @ParameterizedTest
    @MethodSource("cookieWriterTestParameters")
    public void generateSetCookieHeader(String description, boolean isSecure,
                                        Integer maxAge, String path,
                                        String sameSite, String expectedResultContains,
                                        String expectedResultNotContains) {
        // given
        String sessionID = UUID.randomUUID().toString();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        doReturn("/request-context").when(request).getContextPath();

        // when
        String result = testInstance.generateSetCookieHeader(MockHttpSession.SESSION_COOKIE_NAME, sessionID, isSecure, maxAge, path, sameSite, request);

        // then
        assertThat(result, startsWith(MockHttpSession.SESSION_COOKIE_NAME + "=" + sessionID));
        if (StringUtils.isNotEmpty(expectedResultContains)) {
            assertThat(result, containsString(expectedResultContains));
        }
        if (StringUtils.isNotEmpty(expectedResultNotContains)) {
            assertThat(result, IsNot.not(containsString(expectedResultNotContains)));
        }
    }
}
