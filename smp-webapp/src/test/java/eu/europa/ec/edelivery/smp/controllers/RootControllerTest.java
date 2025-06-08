/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
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
package eu.europa.ec.edelivery.smp.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RootControllerTest {

    RootController testInstance = new RootController();

    @Test
    void testRedirectOldIndexPath() {

        ModelMap mockModel = Mockito.mock(ModelMap.class);
        ModelAndView result = testInstance.redirectOldIndexPath(mockModel);

        assertNotNull(result);
        assertEquals("redirect:/", result.getViewName());
    }

    @ParameterizedTest
    @CsvSource({
            ", text/html",
            "/index.html, text/html",
            "/favicon.ico, image/x-ico",
            "images/favicon.ico, image/x-ico",
            "styles/domismp.css, text/css",
            "images/DomiSMP_logo.svg, image/svg+xml",
            "images/EC+Logo2.png, image/png",
            "images/oasis-smp-1.png, image/png",
            "images/oasis-smp-2.png, image/png"
    })
    void testGetStaticResources(String pathInfo, String contentType)  {
        //given
        HttpServletRequest mockHttpServletRequest = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse mockHttpServletResponse = Mockito.mock(HttpServletResponse.class);
        Mockito.when(mockHttpServletRequest.getRequestURI()).thenReturn(pathInfo);
        //when
        ResponseEntity<InputStreamResource> result = testInstance.getStaticResources(mockHttpServletRequest);
        //then
        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
        assertEquals(MediaType.parseMediaType(contentType), result.getHeaders().getContentType());
    }

    @Test
    void testRedirectWithUsingRedirectPrefix() {
        ModelMap mockModel = Mockito.mock(ModelMap.class);
        ModelAndView result = testInstance.redirectWithUsingRedirectPrefix(mockModel);

        assertNotNull(result);
        assertEquals("redirect:/ui/", result.getViewName());
    }

}
