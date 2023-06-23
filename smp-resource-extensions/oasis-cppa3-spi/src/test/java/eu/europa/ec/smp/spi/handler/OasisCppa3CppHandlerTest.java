package eu.europa.ec.smp.spi.handler;

import eu.europa.ec.smp.spi.api.SmpDataServiceApi;
import eu.europa.ec.smp.spi.api.SmpIdentifierServiceApi;
import eu.europa.ec.smp.spi.api.SmpXmlSignatureApi;
import eu.europa.ec.smp.spi.api.model.RequestData;
import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;
import eu.europa.ec.smp.spi.api.model.ResponseData;
import eu.europa.ec.smp.spi.exceptions.ResourceException;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.xml.sax.SAXParseException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;

import static org.junit.Assert.*;

public class OasisCppa3CppHandlerTest {


    SmpDataServiceApi smpDataApi = Mockito.mock(SmpDataServiceApi.class);
    SmpIdentifierServiceApi smpIdentifierServiceApi = Mockito.mock(SmpIdentifierServiceApi.class);
    SmpXmlSignatureApi smpXmlSignatureApi = Mockito.mock(SmpXmlSignatureApi.class);
    OasisCppa3CppHandler testInstance = new OasisCppa3CppHandler(smpDataApi, smpIdentifierServiceApi, smpXmlSignatureApi);


    RequestData requestData =  Mockito.mock(RequestData.class);
    ResponseData responseData =  Mockito.mock(ResponseData.class);


    @Test
    public void generateAndValidateResource() throws ResourceException {
        ResourceIdentifier resourceIdentifier = new ResourceIdentifier("test-identifier", "test-test-test");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Mockito.doReturn(resourceIdentifier).when(requestData).getResourceIdentifier();
        Mockito.doReturn(baos).when(responseData).getOutputStream();

        testInstance.generateResource(requestData, responseData, Collections.emptyList());

        Mockito.doReturn(resourceIdentifier).when(requestData).getResourceIdentifier();
        Mockito.doReturn(baos).when(responseData).getOutputStream();
        assertTrue(baos.size()>0);
        System.out.println(baos.toString());
        // validate
        ByteArrayInputStream bios = new ByteArrayInputStream(baos.toByteArray());
        Mockito.doReturn(bios).when(requestData).getResourceInputStream();
        Mockito.doReturn(resourceIdentifier).when(smpIdentifierServiceApi).normalizeResourceIdentifier(Mockito.anyString(),Mockito.anyString());

        testInstance.validateResource(requestData);
    }

    @Test
    public void validateOasisCPPASchema() throws ResourceException {
        OasisCppa3CppHandler.validateOasisCPPASchema(OasisCppa3CppHandlerTest.class.getResourceAsStream("/examples/signed-cpp.xml"));
    }
    @Test
    public void validateOasisCPPASchemaInvalid()  {

        ResourceException exception  = assertThrows(ResourceException.class, ()-> OasisCppa3CppHandler.validateOasisCPPASchema(OasisCppa3CppHandlerTest.class.getResourceAsStream("/examples/signed-cpp-invalid.xml")));
        assertEquals(SAXParseException.class, exception.getCause().getClass());
    }
}
