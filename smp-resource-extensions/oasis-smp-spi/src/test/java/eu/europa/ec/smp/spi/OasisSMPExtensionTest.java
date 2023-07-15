package eu.europa.ec.smp.spi;

import eu.europa.ec.smp.spi.def.OasisSMPServiceGroup10;
import eu.europa.ec.smp.spi.def.OasisSMPServiceGroup20;
import eu.europa.ec.smp.spi.resource.ResourceDefinitionSpi;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OasisSMPExtensionTest {

    OasisSMPServiceGroup10 mockOasisSMPServiceGroup10 = Mockito.mock(OasisSMPServiceGroup10.class);
    OasisSMPServiceGroup20 mockOasisSMPServiceGroup20 = Mockito.mock(OasisSMPServiceGroup20.class);

    OasisSMPExtension testInstance = new OasisSMPExtension(mockOasisSMPServiceGroup10, mockOasisSMPServiceGroup20);
    @Test
    void testIdentifier() {

        String result = testInstance.identifier();

        assertEquals("edelivery-oasis-smp-extension", result);
    }

    @Test
    void testName() {
        String result = testInstance.name();
        assertEquals("Oasis SMP 1.0 and 2.0", result);
    }

    @Test
    void testDescription() {
        String result = testInstance.description();
        assertEquals("The extension implements Oasis SMP 1.0 and Oasis 2.0 document handlers", result);
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
        assertEquals(mockOasisSMPServiceGroup10, result.get(0));
        assertEquals(mockOasisSMPServiceGroup20, result.get(1));
    }

    @Test
    void testPayloadValidators() {
        List<PayloadValidatorSpi> result = testInstance.payloadValidators();
        assertEquals(0, result.size());
    }
}
