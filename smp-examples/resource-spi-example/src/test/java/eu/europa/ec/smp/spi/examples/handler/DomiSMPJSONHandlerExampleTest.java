package eu.europa.ec.smp.spi.examples.handler;

import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;
import eu.europa.ec.smp.spi.exceptions.ResourceException;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;


class DomiSMPJSONHandlerExampleTest extends AbstractHandlerTest {

    @Override
    AbstractHandler getTestInstance() {
        return new DomiSMPJSONHandlerExample(mockSmpDataApi, mockSmpIdentifierServiceApi, mockSignatureApi);
    }

    @Test
    void testGenerateResource() throws ResourceException {

        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("test-identifier", "test-test-test");

        generateResourceAction(resourceIdentifier);
    }

    @Test
    void validateResourceOK() throws ResourceException {
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("test-identifier", "test-test-test");
        // validate
        validateResourceAction("/examples/json_ok.json", resourceIdentifier);
    }


    @Test
    void validateResourceInvalidIdentifier() {
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("urn:poland:ncpb:utestt", "test-test-test");
        // validate
        ResourceException result = assertThrows(ResourceException.class,
                () -> validateResourceAction("/examples/json_ok.json", resourceIdentifier));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.containsString("Property: [identifier] does not match value for the resource"));
    }

    @Test
    void validateResourceInvalidScheme() {

        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("test-identifier", "test1-test-test");
        // validate
        ResourceException result = assertThrows(ResourceException.class,
                () -> validateResourceAction("/examples/json_ok.json", resourceIdentifier));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.containsString("Property: [identifier] does not match value for the resource"));
    }

    @Test
    void readResourceOK() throws ResourceException {
        String resourceName = "/examples/json_ok.json";
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("test-identifier", "test-test-test");

        readResourceAction(resourceName, resourceIdentifier);
    }

    @Test
    void storeResourceOK() throws ResourceException {
        String resourceName = "/examples/json_ok.json";
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("test-identifier", "test-test-test");
        storeResourceAction(resourceName, resourceIdentifier);
    }
}
