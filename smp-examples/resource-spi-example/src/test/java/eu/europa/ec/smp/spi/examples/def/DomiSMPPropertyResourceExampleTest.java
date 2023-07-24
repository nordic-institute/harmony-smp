package eu.europa.ec.smp.spi.examples.def;

import eu.europa.ec.smp.spi.examples.handler.DomiSMPPropertyHandlerExample;
import eu.europa.ec.smp.spi.resource.ResourceHandlerSpi;
import eu.europa.ec.smp.spi.resource.SubresourceDefinitionSpi;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DomiSMPPropertyResourceExampleTest {

    DomiSMPPropertyHandlerExample mockDomiSMPPropertyHandlerExample = Mockito.mock(DomiSMPPropertyHandlerExample.class);
    DomiSMPPropertyResourceExample testInstance = new DomiSMPPropertyResourceExample(mockDomiSMPPropertyHandlerExample);

    @Test
    void identifier() {
        String result = testInstance.identifier();

        assertEquals("domismp-resource-example-properties", result);
    }

    @Test
    void defaultUrlSegment() {
        String result = testInstance.defaultUrlSegment();

        assertEquals("prop", result);
    }

    @Test
    void name() {
        String result = testInstance.name();

        assertEquals("DomiSMP property example", result);
    }

    @Test
    void description() {
        String result = testInstance.description();

        assertEquals("DomiSMP property example", result);
    }

    @Test
    void mimeType() {
        String result = testInstance.mimeType();

        assertEquals("text/x-properties", result);
    }

    @Test
    void getSubresourceSpiList() {
        List<SubresourceDefinitionSpi> result = testInstance.getSubresourceSpiList();

        assertTrue(result.isEmpty());
    }

    @Test
    void getResourceHandler() {
        ResourceHandlerSpi result = testInstance.getResourceHandler();

        assertEquals(mockDomiSMPPropertyHandlerExample, result);
    }

    @Test
    void testToString() {
        String result = testInstance.toString();

        MatcherAssert.assertThat(result, CoreMatchers.containsString("domismp-resource-example-properties"));
    }
}
