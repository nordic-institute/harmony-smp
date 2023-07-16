package eu.europa.ec.smp.spi.def;

import eu.europa.ec.smp.spi.handler.OasisSMPServiceMetadata10Handler;
import eu.europa.ec.smp.spi.resource.ResourceHandlerSpi;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class OasisSMPServiceMetadata10Test {

    OasisSMPServiceMetadata10Handler mockOasisSMPServiceMetadata10Handler = Mockito.mock(OasisSMPServiceMetadata10Handler.class);
    OasisSMPServiceMetadata10 testInstance = new OasisSMPServiceMetadata10(mockOasisSMPServiceMetadata10Handler);

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

        assertEquals(mockOasisSMPServiceMetadata10Handler, result);
    }

    @Test
    void testToString() {
        String result = testInstance.toString();

        MatcherAssert.assertThat(result, CoreMatchers.containsString("edelivery-oasis-smp-1.0-servicemetadata"));
    }
}
