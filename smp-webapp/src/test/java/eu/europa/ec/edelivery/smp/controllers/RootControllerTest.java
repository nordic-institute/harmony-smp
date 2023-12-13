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
package eu.europa.ec.edelivery.smp.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RootControllerTest {

    RootController testInstance = new RootController();

    @Test
    void testRedirectOldIndexPath() {
        ModelMap mockModel = Mockito.mock(ModelMap.class);
        ModelAndView result = testInstance.redirectOldIndexPath(mockModel);

        assertNotNull(result);
        assertEquals("redirect:/index.html", result.getViewName());
    }

    @ParameterizedTest
    @CsvSource({
            ", text/html",
            "/index.html, text/html",
            "/favicon.png, image/png",
            "/favicon.ico, image/x-ico"
    })
    void testGetStaticResources(String pathInfo, String contentType) throws IOException {
        //given
        HttpServletRequest mockHttpServletRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse mockHttpServletResponse = Mockito.mock(HttpServletResponse.class);
        Mockito.when(mockHttpServletRequest.getPathInfo()).thenReturn(pathInfo);
        //when
        byte[] result = testInstance.getStaticResources(mockHttpServletRequest, mockHttpServletResponse);
        //then
        assertNotNull(result);
        Mockito.verify(mockHttpServletResponse).setContentType(contentType);

    }

    @Test
    void testRedirectWithUsingRedirectPrefix() {
        ModelMap mockModel = Mockito.mock(ModelMap.class);
        ModelAndView result = testInstance.redirectWithUsingRedirectPrefix(mockModel);

        assertNotNull(result);
        assertEquals("redirect:/ui/index.html", result.getViewName());
    }

}
