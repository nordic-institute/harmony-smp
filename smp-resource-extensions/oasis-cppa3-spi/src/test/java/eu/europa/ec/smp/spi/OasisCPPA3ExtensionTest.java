package eu.europa.ec.smp.spi;

import eu.europa.ec.smp.spi.def.OasisCppaCppDocument;
import eu.europa.ec.smp.spi.resource.ResourceDefinitionSpi;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OasisCPPA3ExtensionTest {
    OasisCppaCppDocument mockOasisCppaCppDocument = Mockito.mock(OasisCppaCppDocument.class);

    OasisCPPA3Extension testInstance = new OasisCPPA3Extension(mockOasisCppaCppDocument);
    @Test
    void testIdentifier() {

        String result = testInstance.identifier();

        assertEquals("edelivery-oasis-cppa3-extension", result);
    }

    @Test
    void testName() {
        String result = testInstance.name();
        assertEquals("Oasis CPPA 3.0", result);
    }

    @Test
    void testDescription() {
        String result = testInstance.description();
        assertEquals("The extension implements Oasis CPPA-CPP document handlers", result);
    }

    @Test
    void testVersion() {
        String  result = testInstance.version();
        assertEquals("1.0", result);
    }

    @Test
    void testResourceTypes() {
        List<ResourceDefinitionSpi> result = testInstance.resourceTypes();
        assertEquals(1, result.size());
        assertEquals(mockOasisCppaCppDocument, result.get(0));
    }

    @Test
    void testPayloadValidators() {
        List<PayloadValidatorSpi> result = testInstance.payloadValidators();
        assertEquals(0, result.size());
    }
}
