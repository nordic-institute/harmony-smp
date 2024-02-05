/*-
 * #START_LICENSE#
 * oasis-smp-spi
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
package eu.europa.ec.smp.spi.def;

import eu.europa.ec.smp.spi.handler.OasisSMPSubresource10Handler;
import eu.europa.ec.smp.spi.resource.ResourceHandlerSpi;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OasisSMPSubresource10Test {

    OasisSMPSubresource10Handler mockOasisSMPSubresource10Handler = Mockito.mock(OasisSMPSubresource10Handler.class);
    OasisSMPSubresource10 testInstance = new OasisSMPSubresource10(mockOasisSMPSubresource10Handler);

    @Test
    void identifier() {
        String result = testInstance.identifier();

        assertEquals("edelivery-oasis-smp-1.0-servicemetadata", result);
    }

    @Test
    void urlSegment() {
        String result = testInstance.urlSegment();

        assertEquals("services", result);
    }

    @Test
    void name() {
        String result = testInstance.name();

        assertEquals("Oasis SMP 1.0 ServiceMetadata", result);
    }

    @Test
    void description() {
        String result = testInstance.description();

        assertEquals("Oasis SMP 1.0 Service Metadata resource handler", result);
    }

    @Test
    void mimeType() {
        String result = testInstance.mimeType();

        assertEquals("text/xml", result);
    }

    @Test
    void getResourceHandler() {
        ResourceHandlerSpi result = testInstance.getResourceHandler();

        assertEquals(mockOasisSMPSubresource10Handler, result);
    }

    @Test
    void testToString() {
        String result = testInstance.toString();

        MatcherAssert.assertThat(result, CoreMatchers.containsString("edelivery-oasis-smp-1.0-servicemetadata"));
    }
}
