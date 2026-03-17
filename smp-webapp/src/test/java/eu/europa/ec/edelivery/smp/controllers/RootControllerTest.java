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
import org.mockito.Mockito;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RootControllerTest {

    RootController testInstance = new RootController();

    @Test
    void testRedirectToUI() {
        ModelMap mockModel = Mockito.mock(ModelMap.class);
        ModelAndView result = testInstance.redirectToUI(mockModel);

        assertNotNull(result);
        assertEquals("redirect:/ui/", result.getViewName());
    }

    @Test
    void testForwardToAngularIndex() {
        ModelMap mockModel = Mockito.mock(ModelMap.class);
        ModelAndView result = testInstance.forwardToAngularIndex(mockModel);

        assertNotNull(result);
        assertEquals("forward:/ui/index.html", result.getViewName());
    }

}
