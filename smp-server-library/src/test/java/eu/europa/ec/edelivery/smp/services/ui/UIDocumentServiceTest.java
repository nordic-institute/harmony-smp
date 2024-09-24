/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
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
import eu.europa.ec.edelivery.smp.data.ui.DocumentPropertyRO;
import eu.europa.ec.edelivery.smp.data.ui.DocumentRO;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.services.AbstractServiceIntegrationTest;
import eu.europa.ec.edelivery.smp.services.resource.ResourceHandlerService;
import eu.europa.ec.edelivery.smp.utils.StringNamedSubstitutor;
import eu.europa.ec.smp.spi.def.OasisSMPResource10;
import eu.europa.ec.smp.spi.def.OasisSMPSubresource10;
import eu.europa.ec.smp.spi.enums.TransientDocumentPropertyType;
import eu.europa.ec.smp.spi.handler.OasisSMPResource10Handler;
import eu.europa.ec.smp.spi.handler.OasisSMPSubresource10Handler;
import eu.europa.ec.smp.spi.validation.Subresource10Validator;
import org.assertj.core.api.Assertions;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ContextConfiguration(classes = {UIDocumentService.class, ConversionTestConfig.class, ResourceHandlerService.class,
        OasisSMPResource10.class, OasisSMPResource10Handler.class, OasisSMPSubresource10.class, OasisSMPSubresource10Handler.class, Subresource10Validator.class,})
class UIDocumentServiceTest extends AbstractServiceIntegrationTest {

    @Autowired
    protected UIDocumentService testInstance;

    @Autowired
    ResourceHandlerService resourceHandlerService;

    @BeforeEach
    public void prepareDatabase() {
        // setup initial data!
        testUtilsDao.clearData();
        testUtilsDao.createSubresources();
    }

    @Test
    void testGenerateDocumentForResource() {

        DocumentRO result = testInstance.generateDocumentForResource(testUtilsDao.getResourceD1G1RD1().getId());
        assertNotNull(result);
        assertNotNull(result.getPayload());
    }

    @Test
    void testGenerateDocumentForSubResource() {
        DBSubresource subresource = testUtilsDao.getSubresourceD1G1RD1_S1();

        DocumentRO result = testInstance.generateDocumentForSubresource(subresource.getId(),
                subresource.getResource().getId());
        assertNotNull(result);
        assertNotNull(result.getPayload());
    }

    @Test
    void testValidateForResource() {
        DBResource resource = testUtilsDao.getResourceD1G1RD1();
        DocumentRO testDoc = testInstance.generateDocumentForResource(resource.getId());
        assertNotNull(testDoc.getPayload());
        // must not throw exception
        testInstance.validateDocumentForResource(resource.getId(), testDoc);
    }

    @Test
    void testValidateForResourceError() {
        DBResource resource = testUtilsDao.getResourceD1G1RD1();
        DocumentRO testDoc = new DocumentRO();
        testDoc.setPayload("test");

        SMPRuntimeException result = assertThrows(SMPRuntimeException.class, () ->
                testInstance.validateDocumentForResource(resource.getId(), testDoc));

        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString("Invalid request [ResourceValidation]"));
    }


    @Test
    void testValidateForSubresource() {
        DBSubresource subresource = testUtilsDao.getSubresourceD1G1RD1_S1();
        DocumentRO testDoc = testInstance.generateDocumentForSubresource(subresource.getId(),
                subresource.getResource().getId());

        assertNotNull(testDoc.getPayload());
        // must not throw exception
        testInstance.validateDocumentForSubresource(subresource.getId(), subresource.getResource().getId(), testDoc);
    }

    @Test
    void testValidateForSubresourceError() {
        DBSubresource subresource = testUtilsDao.getSubresourceD1G1RD1_S1();
        DocumentRO testDoc = new DocumentRO();
        testDoc.setPayload("test");

        SMPRuntimeException result = assertThrows(SMPRuntimeException.class, () ->
                testInstance.validateDocumentForSubresource(subresource.getId(), subresource.getResource().getId(), testDoc));

        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString("Invalid request [ResourceValidation]"));
    }

    @Test
    void testGetDocumentForResource() {
        DBResource resource = testUtilsDao.getResourceD1G1RD1();
        DocumentRO testDoc = testInstance.getDocumentForResource(resource.getId(), 1);
        assertNotNull(testDoc.getPayload());
    }

    @Test
    void testGetDocumentForSubResource() {
        DBSubresource subresource = testUtilsDao.getSubresourceD1G1RD1_S1();
        DocumentRO testDoc = testInstance.getDocumentForSubResource(subresource.getId(), subresource.getResource().getId(), 1);
        assertNotNull(testDoc.getPayload());
    }

    @Test
    void testSaveDocumentForResource() {
        DBResource resource = testUtilsDao.getResourceD1G1RD1();
        DocumentRO testDoc = testInstance.generateDocumentForResource(resource.getId());
        assertNotNull(testDoc.getPayload());
        //when
        DocumentRO result = testInstance.saveDocumentForResource(resource.getId(), testDoc);
        // then
        assertNotNull(result);
    }

    @Test
    void testSaveDocumentForSubresource() {
        DBSubresource subresource = testUtilsDao.getSubresourceD1G1RD1_S1();
        DocumentRO testDoc = testInstance.generateDocumentForSubresource(subresource.getId(),
                subresource.getResource().getId());
        assertNotNull(testDoc.getPayload());

        //when
        DocumentRO result = testInstance.saveSubresourceDocumentForResource(subresource.getId(), subresource.getResource().getId(), testDoc);
        // then
        assertNotNull(result);
    }

    @Test
    void testTransientResolutionForSubresourceDocument() {
        DBSubresource subresource = testUtilsDao.getSubresourceD1G1RD1_S1();
        DocumentRO testDoc = testInstance.generateDocumentForSubresource(subresource.getId(),
                subresource.getResource().getId());
        assertNotNull(testDoc.getPayload());
        // extension used by this test is SMP example extension which generates document with placeholders
        Assertions.assertThat(testDoc.getPayload()).contains(TransientDocumentPropertyType.RESOURCE_IDENTIFIER_VALUE.getPropertyPlaceholder());
        Assertions.assertThat(testDoc.getPayload()).contains(TransientDocumentPropertyType.RESOURCE_IDENTIFIER_SCHEME.getPropertyPlaceholder());
        Assertions.assertThat(testDoc.getPayload()).contains(TransientDocumentPropertyType.SUBRESOURCE_IDENTIFIER_VALUE.getPropertyPlaceholder());
        Assertions.assertThat(testDoc.getPayload()).contains(TransientDocumentPropertyType.SUBRESOURCE_IDENTIFIER_SCHEME.getPropertyPlaceholder());

        //when
        DocumentRO result = testInstance.saveSubresourceDocumentForResource(subresource.getId(), subresource.getResource().getId(), testDoc);

        Map<String, String> mapProperties = result.getProperties().stream().collect(Collectors.toMap(DocumentPropertyRO::getProperty, DocumentPropertyRO::getValue));
        String resolved = StringNamedSubstitutor.resolve(result.getPayload(), mapProperties);
        // then
        Assertions.assertThat(resolved).doesNotContain(TransientDocumentPropertyType.RESOURCE_IDENTIFIER_SCHEME.getPropertyPlaceholder());
        Assertions.assertThat(resolved).doesNotContain(TransientDocumentPropertyType.RESOURCE_IDENTIFIER_VALUE.getPropertyPlaceholder());
        Assertions.assertThat(resolved).doesNotContain(TransientDocumentPropertyType.SUBRESOURCE_IDENTIFIER_VALUE.getPropertyPlaceholder());
        Assertions.assertThat(resolved).doesNotContain(TransientDocumentPropertyType.SUBRESOURCE_IDENTIFIER_SCHEME.getPropertyPlaceholder());

        Assertions.assertThat(resolved).contains(subresource.getIdentifierValue());
        Assertions.assertThat(resolved).contains(subresource.getIdentifierScheme());
        Assertions.assertThat(resolved).contains(subresource.getResource().getIdentifierValue());
        Assertions.assertThat(resolved).contains(subresource.getResource().getIdentifierScheme());
    }
}
