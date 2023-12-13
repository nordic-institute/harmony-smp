/*-
 * #%L
 * resource-spi-example
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
package eu.europa.ec.smp.spi.examples;

import eu.europa.ec.smp.spi.PayloadValidatorSpi;
import eu.europa.ec.smp.spi.examples.def.DomiSMPJsonResourceExample;
import eu.europa.ec.smp.spi.examples.def.DomiSMPPropertyResourceExample;
import eu.europa.ec.smp.spi.resource.ResourceDefinitionSpi;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class DomiSMPResourceExampleExtensionTest {
    DomiSMPPropertyResourceExample mockDomiSMPPropertyResourceExample = Mockito.mock(DomiSMPPropertyResourceExample.class);
    DomiSMPJsonResourceExample mocDomiSMPJsonResourceExample = Mockito.mock(DomiSMPJsonResourceExample.class);

    DomiSMPResourceExampleExtension testInstance = new DomiSMPResourceExampleExtension(mockDomiSMPPropertyResourceExample, mocDomiSMPJsonResourceExample );

    @Test
    void testIdentifier() {
        String result = testInstance.identifier();

        assertEquals("domismp-resource-example-extension", result);
    }

    @Test
    void testName() {
        String result = testInstance.name();
        assertEquals("Resource example extension", result);
    }

    @Test
    void testDescription() {
        String result = testInstance.description();
        assertEquals("The extension implements json and property examples", result);
    }

    @Test
    void testVersion() {
        String  result = testInstance.version();
        assertEquals("1.0", result);
    }

    @Test
    void testResourceTypes() {
        List<ResourceDefinitionSpi> result = testInstance.resourceTypes();
        assertEquals(2, result.size());
        assertEquals(mocDomiSMPJsonResourceExample, result.get(0));
        assertEquals(mockDomiSMPPropertyResourceExample, result.get(1));
    }

    @Test
    void testPayloadValidators() {
        List<PayloadValidatorSpi> result = testInstance.payloadValidators();
        assertEquals(0, result.size());
    }
}
