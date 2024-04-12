/*-
 * #START_LICENSE#
 * smp-server-library
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
package eu.europa.ec.edelivery.smp.servlet;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResourceResponseTest {

    HttpServletResponse mockHttpServletResponse = Mockito.mock(HttpServletResponse.class);
    ResourceResponse testInstance = new ResourceResponse(mockHttpServletResponse);

    @Test
    void testGetHttpStatus() {
        int httpStatus = 200;
        Mockito.when(mockHttpServletResponse.getStatus()).thenReturn(httpStatus);
        int result = testInstance.getHttpStatus();

        assertEquals(httpStatus, result);
    }

    @Test
    void testSetHttpStatus() {
        int httpStatus = 200;
        testInstance.setHttpStatus(httpStatus);

        Mockito.verify(mockHttpServletResponse).setStatus(httpStatus);
    }

    @Test
    void testGetMimeType() {
        String mimeType = "mockMimeType";
        Mockito.when(mockHttpServletResponse.getContentType()).thenReturn(mimeType);
        String result = testInstance.getMimeType();

        assertEquals(mimeType, result);
    }

    @Test
    void testSetContentType() {
        String mimeType = "mockMimeType";
        testInstance.setContentType(mimeType);

        Mockito.verify(mockHttpServletResponse).setContentType(mimeType);
    }

    @Test
    void testGetHttpHeader() {
        String name = "mockName";
        String value = "mockValue";
        Mockito.when(mockHttpServletResponse.getHeader(name)).thenReturn(value);
        String result = testInstance.getHttpHeader(name);

        assertEquals(value, result);
    }

    @Test
    void testSetHttpHeader() {
        String name = "mockName";
        String value = "mockValue";
        testInstance.setHttpHeader(name, value);

        Mockito.verify(mockHttpServletResponse).setHeader(name, value);
    }

    @Test
    void testGetOutputStream() throws IOException {
        testInstance.getOutputStream();

        Mockito.verify(mockHttpServletResponse).getOutputStream();
    }
}
