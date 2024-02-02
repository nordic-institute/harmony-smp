/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.config.ConversionTestConfig;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
import eu.europa.ec.edelivery.smp.data.ui.DocumentRo;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.services.AbstractServiceIntegrationTest;
import eu.europa.ec.edelivery.smp.services.resource.ResourceHandlerService;
import eu.europa.ec.smp.spi.def.OasisSMPResource10;
import eu.europa.ec.smp.spi.def.OasisSMPSubresource10;
import eu.europa.ec.smp.spi.handler.OasisSMPResource10Handler;
import eu.europa.ec.smp.spi.handler.OasisSMPSubresource10Handler;
import eu.europa.ec.smp.spi.validation.Subresource10Validator;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

@ContextConfiguration(classes = {UIDocumentService.class, ConversionTestConfig.class, ResourceHandlerService.class,
        OasisSMPResource10.class, OasisSMPResource10Handler.class, OasisSMPSubresource10.class, OasisSMPSubresource10Handler.class, Subresource10Validator.class,})
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
