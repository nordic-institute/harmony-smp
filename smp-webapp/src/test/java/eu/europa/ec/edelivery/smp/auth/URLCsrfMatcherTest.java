/*-
 * #%L
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
 * #L%
 */
package eu.europa.ec.edelivery.smp.auth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class URLCsrfMatcherTest {

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection cookieWriterTestParameters() {
        return asList(new Object[][]{
                {"/test/", false, Collections.singletonList("/.*"), null},
                {"/ui/resource", true, Collections.singletonList("/!(ui/).*"), null},
                {"/test/resource", false, Collections.singletonList("^/(?!ui/).*"), null},
                {"/ui/resource", true, Collections.singletonList("^/(?!ui/).*"), null},

        });
    }

    @Parameterized.Parameter(0)
    public String patInfo;

    @Parameterized.Parameter(1)
    public boolean notMatchResult;

    @Parameterized.Parameter(2)
    public List<String> regExp;

    @Parameterized.Parameter(3)
    public List<HttpMethod> httpMethods;


    @Test
    public void matches() {
        URLCsrfIgnoreMatcher testInstance = new URLCsrfIgnoreMatcher(regExp, httpMethods);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn(patInfo).when(request).getRequestURI();
        Mockito.doReturn("").when(request).getServletPath();

        boolean result = testInstance.matches(request);
        assertEquals(notMatchResult, result);


    }
}
