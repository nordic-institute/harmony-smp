package eu.europa.ec.smp.spi.examples.def;

import eu.europa.ec.smp.spi.examples.handler.DomiSMPJSONHandlerExample;
import eu.europa.ec.smp.spi.resource.ResourceHandlerSpi;
import eu.europa.ec.smp.spi.resource.SubresourceDefinitionSpi;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DomiSMPJsonResourceExampleTest {

    DomiSMPJSONHandlerExample mockDomiSMPJSONHandlerExample = Mockito.mock(DomiSMPJSONHandlerExample.class);
    DomiSMPJsonResourceExample testInstance = new DomiSMPJsonResourceExample(mockDomiSMPJSONHandlerExample);

    @Test
    void identifier() {
        String result = testInstance.identifier();

        assertEquals("domismp-resource-example-json", result);
    }

    @Test
    void defaultUrlSegment() {
        String result = testInstance.defaultUrlSegment();

        assertEquals("json", result);
    }

    @Test
    void name() {
        String result = testInstance.name();

        assertEquals("DomiSMP JSON example", result);
    }

    @Test
    void description() {
        String result = testInstance.description();

        assertEquals("DomiSMP JSON example", result);
    }

    @Test
    void mimeType() {
        String result = testInstance.mimeType();

        assertEquals("application/json", result);
    }

    @Test
    void getSubresourceSpiList() {
        List<SubresourceDefinitionSpi> result = testInstance.getSubresourceSpiList();

        assertTrue(result.isEmpty());
    }

    @Test
    void getResourceHandler() {
        ResourceHandlerSpi result = testInstance.getResourceHandler();

        assertEquals(mockDomiSMPJSONHandlerExample, result);
    }

    @Test
    void testToString() {
        String result = testInstance.toString();

        MatcherAssert.assertThat(result, CoreMatchers.containsString("domismp-resource-example-json"));
    }
}
