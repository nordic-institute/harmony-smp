package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.config.ConversionTestConfig;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
import eu.europa.ec.edelivery.smp.data.ui.DocumentRo;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.services.AbstractServiceIntegrationTest;
import eu.europa.ec.edelivery.smp.services.resource.ResourceHandlerService;
import eu.europa.ec.edelivery.smp.services.resource.ResourceResolverService;
import eu.europa.ec.edelivery.smp.testutil.TestDBUtils;
import eu.europa.ec.smp.spi.api.SmpDataServiceApi;
import eu.europa.ec.smp.spi.api.SmpIdentifierServiceApi;
import eu.europa.ec.smp.spi.def.OasisSMPServiceGroup10;
import eu.europa.ec.smp.spi.def.OasisSMPServiceMetadata10;
import eu.europa.ec.smp.spi.handler.OasisSMPServiceGroup10Handler;
import eu.europa.ec.smp.spi.handler.OasisSMPServiceMetadata10Handler;
import eu.europa.ec.smp.spi.validation.ServiceMetadata10Validator;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.*;

@ContextConfiguration(classes = {UIDocumentService.class, ConversionTestConfig.class, ResourceHandlerService.class,
        OasisSMPServiceGroup10.class, OasisSMPServiceGroup10Handler.class, OasisSMPServiceMetadata10.class, OasisSMPServiceMetadata10Handler.class, ServiceMetadata10Validator.class,})
public class UIDocumentServiceTest extends AbstractServiceIntegrationTest {

    @Autowired
    protected UIDocumentService testInstance;

    @Before
    public void prepareDatabase() {
        // setup initial data!
        testUtilsDao.clearData();
        testUtilsDao.createSubresources();
    }

    @Test
    public void testGenerateDocumentForResource(){

        DocumentRo result = testInstance.generateDocumentForResource(testUtilsDao.getResourceD1G1RD1().getId(), null);
        assertNotNull(result);
        assertNotNull(result.getPayload());
    }

    @Test
    public void testGenerateDocumentForSubResource(){
        DBSubresource subresource = testUtilsDao.getSubresourceD1G1RD1_S1();

        DocumentRo result = testInstance.generateDocumentForSubresource(subresource.getId(),
                subresource.getResource().getId(),
                null);
        assertNotNull(result);
        assertNotNull(result.getPayload());
    }

    @Test
    public void testValidateForResource() {
        DBResource resource = testUtilsDao.getResourceD1G1RD1();
        DocumentRo testDoc = testInstance.generateDocumentForResource(resource.getId(), null);
        assertNotNull(testDoc.getPayload());
        // must not throw exception
        testInstance.validateDocumentForResource(resource.getId(), testDoc);
    }

    @Test
    public void testValidateForResourceError() {
        DBResource resource = testUtilsDao.getResourceD1G1RD1();
        DocumentRo testDoc = new DocumentRo();
        testDoc.setPayload("test");

        SMPRuntimeException result = assertThrows(SMPRuntimeException.class, () -> {
            testInstance.validateDocumentForResource(resource.getId(), testDoc);
        });

        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString("Invalid request [ResourceValidation]"));
    }


    @Test
    public void testValidateForSubresource() {
        DBSubresource subresource = testUtilsDao.getSubresourceD1G1RD1_S1();
        DocumentRo testDoc = testInstance.generateDocumentForSubresource(subresource.getId(),
                subresource.getResource().getId(),
                null);

        assertNotNull(testDoc.getPayload());
        // must not throw exception
        testInstance.validateDocumentForSubresource(subresource.getId(), subresource.getResource().getId(), testDoc);
    }

    @Test
    public void testValidateForSubresourceError() {
        DBSubresource subresource = testUtilsDao.getSubresourceD1G1RD1_S1();
        DocumentRo testDoc = new DocumentRo();
        testDoc.setPayload("test");

        SMPRuntimeException result = assertThrows(SMPRuntimeException.class, () -> {
            testInstance.validateDocumentForSubresource(subresource.getId(), subresource.getResource().getId(), testDoc);
        });

        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString("Invalid request [ResourceValidation]"));
    }

    @Test
    public void testGetDocumentForResource(){
        DBResource resource = testUtilsDao.getResourceD1G1RD1();
        DocumentRo testDoc = testInstance.getDocumentForResource(resource.getId(), 1);
        assertNotNull(testDoc.getPayload());
    }

    @Test
    public void testGetDocumentForSubResource(){
        DBSubresource subresource = testUtilsDao.getSubresourceD1G1RD1_S1();
        DocumentRo testDoc = testInstance.getDocumentForSubResource(subresource.getId(), subresource.getResource().getId(), 1);
        assertNotNull(testDoc.getPayload());
    }

    @Test
    public void testSaveDocumentForResource(){
        DBResource resource = testUtilsDao.getResourceD1G1RD1();
        DocumentRo testDoc = testInstance.generateDocumentForResource(resource.getId(), null);
        assertNotNull(testDoc.getPayload());
        //when
        DocumentRo result = testInstance.saveDocumentForResource(resource.getId(), testDoc);
        // then
        assertNotNull(result);
    }

    @Test
    public void testSaveSubresourceDocumentForResource(){
        DBSubresource subresource = testUtilsDao.getSubresourceD1G1RD1_S1();
        DocumentRo testDoc = testInstance.generateDocumentForSubresource(subresource.getId(),
                subresource.getResource().getId(),
                null);
        assertNotNull(testDoc.getPayload());
        //when
        DocumentRo result = testInstance.saveSubresourceDocumentForResource(subresource.getId(), subresource.getResource().getId(), testDoc);
        // then
        assertNotNull(result);
    }
}
