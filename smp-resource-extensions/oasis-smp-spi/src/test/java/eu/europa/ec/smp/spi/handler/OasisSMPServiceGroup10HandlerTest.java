package eu.europa.ec.smp.spi.handler;

import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;
import eu.europa.ec.smp.spi.exceptions.ResourceException;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;

class OasisSMPServiceGroup10HandlerTest extends AbstractHandlerTest {
    @Override
    public AbstractOasisSMPHandler getTestInstance() {
        return new OasisSMPServiceGroup10Handler(mockSmpDataApi, mockSmpIdentifierServiceApi);
    }

    @Test
    void testGenerateResource() throws ResourceException {

        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("test-identifier", "test-test-test");

        generateResourceAction(resourceIdentifier);
    }

    @Test
    void validateResourceOK() throws ResourceException {
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("urn:eu:ncpb:utest", "ehealth-actorid-qns");
        // validate
        validateResourceAction("/examples/oasis-smp-1.0/ServiceGroupOK.xml", resourceIdentifier);
    }

    @Test
    void validateResourceDisallowedDocType() {
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("urn:eu:ncpb:utest", "ehealth-actorid-qns");
        // validate
        ResourceException result = assertThrows(ResourceException.class,
                () -> validateResourceAction("/examples/oasis-smp-1.0/ServiceGroupWithDOCTYPE.xml", resourceIdentifier));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.containsString("DOCTYPE is disallowed"));
    }

    @Test
    void validateResourceInvalidIdentifier() {
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("urn:poland:ncpb:InvalidIdentifier", "ehealth-actorid-qns");
        // validate
        ResourceException result = assertThrows(ResourceException.class,
                () -> validateResourceAction("/examples/oasis-smp-1.0/ServiceGroupOK.xml", resourceIdentifier));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.containsString("Participant identifiers don't match"));
    }

    @Test
    void validateResourceInvalidScheme() {
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("urn:poland:ncpb:utestt", "ehealth-actorid-qns");
        // validate
        ResourceException result = assertThrows(ResourceException.class,
                () -> validateResourceAction("/examples/oasis-smp-1.0/ServiceGroupInvalidScheme.xml", resourceIdentifier));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.containsString("SAXParseException"));
    }

    @Test
    void readResourceOK() throws ResourceException {
        String resourceName = "/examples/oasis-smp-1.0/ServiceGroupOK.xml";
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("urn:eu:ncpb:utest", "ehealth-actorid-qns");

        readResourceAction(resourceName, resourceIdentifier);
    }


    @Test
    void storeResourceOK() throws ResourceException {
        String resourceName = "/examples/oasis-smp-1.0/ServiceGroupOK.xml";
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("urn:eu:ncpb:utest", "ehealth-actorid-qns");

        storeResourceAction(resourceName, resourceIdentifier);
    }


}
