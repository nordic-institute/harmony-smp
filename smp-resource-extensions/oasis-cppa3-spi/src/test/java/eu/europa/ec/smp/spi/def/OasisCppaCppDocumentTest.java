package eu.europa.ec.smp.spi.def;

import eu.europa.ec.smp.spi.handler.OasisCppa3CppHandler;
import eu.europa.ec.smp.spi.resource.ResourceHandlerSpi;
import eu.europa.ec.smp.spi.resource.SubresourceDefinitionSpi;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OasisCppaCppDocumentTest {

    OasisCppa3CppHandler mockOasisCppa3CppHandler = Mockito.mock(OasisCppa3CppHandler.class);
    OasisCppaCppDocument testInstance = new OasisCppaCppDocument(mockOasisCppa3CppHandler);

    @Test
    void identifier() {
        String result = testInstance.identifier();

        assertEquals("edelivery-oasis-cppa-3.0-cpp", result);
    }

    @Test
    void defaultUrlSegment() {
        String result = testInstance.defaultUrlSegment();

        assertEquals("cpp", result);
    }

    @Test
    void name() {
        String result = testInstance.name();

        assertEquals("Oasis CPPA3 CPP document", result);
    }

    @Test
    void description() {
        String result = testInstance.description();

        assertEquals("Oasis CPPA-CPP document", result);
    }

    @Test
    void mimeType() {
        String result = testInstance.mimeType();

        assertEquals("text/xml", result);
    }

    @Test
    void getSubresourceSpiList() {
        List<SubresourceDefinitionSpi> result = testInstance.getSubresourceSpiList();

        assertTrue(result.isEmpty());
    }

    @Test
    void getResourceHandler() {
        ResourceHandlerSpi result = testInstance.getResourceHandler();

        assertEquals(mockOasisCppa3CppHandler, result);
    }

    @Test
    void testToString() {
        String result = testInstance.toString();

        MatcherAssert.assertThat(result, CoreMatchers.containsString("edelivery-oasis-cppa-3.0-cpp"));
    }
}
