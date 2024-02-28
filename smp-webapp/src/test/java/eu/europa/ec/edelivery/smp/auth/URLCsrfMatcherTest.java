/*-
 * #START_LICENSE#
 * smp-webapp
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
package eu.europa.ec.edelivery.smp.auth;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class URLCsrfMatcherTest {

    public static Collection cookieWriterTestParameters() {
        return asList(new Object[][]{
                {"/test/", false, Collections.singletonList("/.*"), null},
                {"/ui/resource", true, Collections.singletonList("/!(ui/).*"), null},
                {"/test/resource", false, Collections.singletonList("^/(?!ui/).*"), null},
                {"/ui/resource", true, Collections.singletonList("^/(?!ui/).*"), null},

        });
    }

    @ParameterizedTest
    @MethodSource("cookieWriterTestParameters")
    public void matches(String patInfo,
                        boolean notMatchResult,
                        List<String> regExp,
                        List<HttpMethod> httpMethods) {
        URLCsrfIgnoreMatcher testInstance = new URLCsrfIgnoreMatcher(regExp, httpMethods);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn(patInfo).when(request).getRequestURI();
        Mockito.doReturn("").when(request).getServletPath();

        boolean result = testInstance.matches(request);
        assertEquals(notMatchResult, result);


    }
}
