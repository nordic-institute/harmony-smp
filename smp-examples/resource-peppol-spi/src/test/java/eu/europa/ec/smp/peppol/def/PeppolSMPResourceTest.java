/*-
 * #START_LICENSE#
 * oasis-smp-spi
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
package eu.europa.ec.smp.peppol.def;

import eu.europa.ec.smp.spi.peppol.def.PeppolSMPResource;
import eu.europa.ec.smp.spi.peppol.def.PeppolSMPSubresource;
import eu.europa.ec.smp.spi.peppol.handler.PeppolSMPResourceHandler;
import eu.europa.ec.smp.spi.resource.ResourceHandlerSpi;
import eu.europa.ec.smp.spi.resource.SubresourceDefinitionSpi;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PeppolSMPResourceTest {

    PeppolSMPResourceHandler mockOasisSMPResource10Handler = Mockito.mock(PeppolSMPResourceHandler.class);
    PeppolSMPSubresource mockOasisSMPSubresource10 = Mockito.mock(PeppolSMPSubresource.class);

    PeppolSMPResource testInstance = new PeppolSMPResource(mockOasisSMPResource10Handler, mockOasisSMPSubresource10);


    @Test
    void identifier() {
        String result = testInstance.identifier();

        assertEquals("peppol-smp-servicegroup", result);
    }

    @Test
    void defaultUrlSegment() {
        String result = testInstance.defaultUrlSegment();

        assertEquals("peppol", result);
    }

    @Test
    void name() {
        String result = testInstance.name();

        assertEquals("Peppol SMP ServiceGroup", result);
    }

    @Test
    void description() {
        String result = testInstance.description();

        assertEquals("Peppol SMP Service group resource handler", result);
    }

    @Test
    void mimeType() {
        String result = testInstance.mimeType();

        assertEquals("text/xml", result);
    }

    @Test
    void getSubresourceSpiList() {
        List<SubresourceDefinitionSpi> result = testInstance.getSubresourceSpiList();

        assertEquals(1, result.size());
        assertEquals(mockOasisSMPSubresource10, result.get(0));
    }

    @Test
    void getResourceHandler() {
        ResourceHandlerSpi result = testInstance.getResourceHandler();

        assertEquals(mockOasisSMPResource10Handler, result);
    }

    @Test
    void testToString() {
        String result = testInstance.toString();

        MatcherAssert.assertThat(result, CoreMatchers.containsString("peppol-smp-servicegroup"));
    }
}
