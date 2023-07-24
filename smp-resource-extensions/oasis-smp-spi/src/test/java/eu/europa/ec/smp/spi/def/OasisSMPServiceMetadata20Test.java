package eu.europa.ec.smp.spi.def;

import eu.europa.ec.smp.spi.handler.OasisSMPServiceMetadata20Handler;
import eu.europa.ec.smp.spi.resource.ResourceHandlerSpi;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OasisSMPServiceMetadata20Test {

    OasisSMPServiceMetadata20Handler mockOasisSMPServiceMetadata20Handler = Mockito.mock(OasisSMPServiceMetadata20Handler.class);
    OasisSMPServiceMetadata20 testInstance = new OasisSMPServiceMetadata20(mockOasisSMPServiceMetadata20Handler);

    @Test
    void identifier() {
        String result = testInstance.identifier();

        assertEquals("edelivery-oasis-smp-2.0-servicemetadata", result);
    }

    @Test
    void urlSegment() {
        String result = testInstance.urlSegment();

        assertEquals("services", result);
    }

    @Test
    void name() {
        String result = testInstance.name();

        assertEquals("Oasis SMP 2.0 ServiceMetadata", result);
    }

    @Test
    void description() {
        String result = testInstance.description();

        assertEquals("Oasis SMP 2.0 Service Metadata resource handler", result);
    }

    @Test
    void mimeType() {
        String result = testInstance.mimeType();

        assertEquals("text/xml", result);
    }

    @Test
    void getResourceHandler() {
        ResourceHandlerSpi result = testInstance.getResourceHandler();

        assertEquals(mockOasisSMPServiceMetadata20Handler, result);
    }

    @Test
    void testToString() {
        String result = testInstance.toString();

        MatcherAssert.assertThat(result, CoreMatchers.containsString("edelivery-oasis-smp-2.0-servicemetadata"));
    }
}
