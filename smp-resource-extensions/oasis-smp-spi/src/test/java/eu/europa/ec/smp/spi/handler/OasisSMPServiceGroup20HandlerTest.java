package eu.europa.ec.smp.spi.handler;

import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;
import eu.europa.ec.smp.spi.exceptions.ResourceException;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;

class OasisSMPServiceGroup20HandlerTest extends AbstractHandlerTest {
    @Override
    public AbstractOasisSMPHandler getTestInstance() {
        return new OasisSMPServiceGroup20Handler(mockSmpDataApi, mockSmpIdentifierServiceApi, mockSignatureApi);
    }

    @Test
    void testGenerateResource() throws ResourceException {

        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("test-identifier", "test-test-test");

        generateResourceAction(resourceIdentifier);
    }

    @Test
    void validateResourceOK() throws ResourceException {
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier( "9925:0367302178", "iso6523-actorid-upis");
        // validate
        validateResourceAction("/examples/oasis-smp-2.0/service_group_unsigned_valid_iso6523.xml", resourceIdentifier);
    }

    @Test
    void validateResourceDisallowedDocType() {
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier( "9925:0367302178", "iso6523-actorid-upis");
        // validate
        ResourceException result = assertThrows(ResourceException.class,
                () -> validateResourceAction("/examples/oasis-smp-2.0/service_group_unsigned_invalid_iso6523_DTD.xml", resourceIdentifier));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.containsString("accessExternalDTD"));
    }

    @Test
    void validateResourceInvalidIdentifier() {
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier( "9925:0367302178:invalid", "iso6523-actorid-upis");
        // validate
        ResourceException result = assertThrows(ResourceException.class,
                () -> validateResourceAction("/examples/oasis-smp-2.0/service_group_unsigned_valid_iso6523.xml", resourceIdentifier));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.containsString("Participant identifiers don't match"));
    }

    @Test
    void validateResourceInvalidScheme() {
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier( "9925:0367302178", "iso6523-actorid-upis");
        // validate
        ResourceException result = assertThrows(ResourceException.class,
                () -> validateResourceAction("/examples/oasis-smp-2.0/service_group_unsigned_invalid_iso6523.xml", resourceIdentifier));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.containsString("SAXParseException"));
    }

    @Test
    void readResourceOK() throws ResourceException {
        String resourceName = "/examples/oasis-smp-2.0/service_group_unsigned_valid_iso6523.xml";
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("iso6523-actorid-upis", "9925:0367302178");

        readResourceAction(resourceName, resourceIdentifier);
    }

    @Test
    void storeResourceOK() throws ResourceException {
        String resourceName = "/examples/oasis-smp-2.0/service_group_unsigned_valid_iso6523-no-references.xml";
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("9925:0367302178", "iso6523-actorid-upis");

        storeResourceAction(resourceName, resourceIdentifier);
    }


}
