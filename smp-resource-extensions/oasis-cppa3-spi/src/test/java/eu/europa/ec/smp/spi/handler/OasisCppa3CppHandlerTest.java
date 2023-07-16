package eu.europa.ec.smp.spi.handler;

import eu.europa.ec.smp.spi.api.SmpDataServiceApi;
import eu.europa.ec.smp.spi.api.SmpIdentifierServiceApi;
import eu.europa.ec.smp.spi.api.SmpXmlSignatureApi;
import eu.europa.ec.smp.spi.api.model.RequestData;
import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;
import eu.europa.ec.smp.spi.api.model.ResponseData;
import eu.europa.ec.smp.spi.exceptions.CPPARuntimeException;
import eu.europa.ec.smp.spi.exceptions.ResourceException;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.xml.sax.SAXParseException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;

import static org.junit.Assert.*;

public class OasisCppa3CppHandlerTest extends AbstractHandlerTest{


    @Override
    AbstractHandler getTestInstance() {
        return  new OasisCppa3CppHandler(mockSmpDataApi, mockSmpIdentifierServiceApi, mockSignatureApi);
    }

    @Test
    void testGenerateResource() throws ResourceException {

        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("test-identifier", "test-test-test");

        generateResourceAction(resourceIdentifier);
    }

    @Test
    void validateResourceOK() throws ResourceException {
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("cppa", "aa-aa-aa");
        // validate
        validateResourceAction("/examples/signed-cpp.xml", resourceIdentifier);
    }

    @Test
    void validateResourceDisallowedDocType() {
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("cppa", "aa-aa-aa");
        // validate
        CPPARuntimeException result = assertThrows(CPPARuntimeException.class,
                () -> validateResourceAction("/examples/signed-cpp-With-DOCTYPE.xml", resourceIdentifier));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.containsString("DOCTYPE is disallowed"));
    }

    @Test
    void validateResourceInvalidIdentifier() {
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("urn:poland:ncpb:utestt", "ehealth-actorid-qns");
        // validate
        ResourceException result = assertThrows(ResourceException.class,
                () -> validateResourceAction("/examples/signed-cpp.xml", resourceIdentifier));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.containsString("Non of participant identifiers match to URL parameter "));
    }

    @Test
    void validateResourceInvalidScheme() {

        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("cppa", "aa-aa-aa");
        // validate
        ResourceException result = assertThrows(ResourceException.class,
                () -> validateResourceAction("/examples/signed-cpp-invalid.xml", resourceIdentifier));
        MatcherAssert.assertThat(result.getMessage(), org.hamcrest.Matchers.containsString("SAXParseException"));
    }

    @Test
    void readResourceOK() throws ResourceException {
        String resourceName = "/examples/signed-cpp.xml";
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("cppa", "aa-aa-aa");

        readResourceAction(resourceName, resourceIdentifier);
    }

    @Test
    void storeResourceOK() throws ResourceException {
        String resourceName = "/examples/signed-cpp.xml";
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("cppa", "aa-aa-aa");

        storeResourceAction(resourceName, resourceIdentifier);
    }
}
