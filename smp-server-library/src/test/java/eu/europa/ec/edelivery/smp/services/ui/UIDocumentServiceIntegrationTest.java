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
import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyTypeEnum;
import eu.europa.ec.edelivery.smp.data.model.DBDomainDocumentTemplate;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.data.ui.DocumentPropertyRO;
import eu.europa.ec.edelivery.smp.data.ui.DocumentRO;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.services.AbstractServiceIntegrationTest;
import eu.europa.ec.edelivery.smp.services.SMPExceptionLanguageService;
import eu.europa.ec.edelivery.smp.testutil.TestROUtils;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigInteger;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = {UIDocumentService.class, ConversionTestConfig.class,
        OasisSMPResource10.class, OasisSMPResource10Handler.class, OasisSMPSubresource10.class, OasisSMPSubresource10Handler.class, Subresource10Validator.class,})
class UIDocumentServiceIntegrationTest extends AbstractServiceIntegrationTest {

    @Autowired
    protected UIDocumentService testInstance;

    @Autowired
    private SMPExceptionLanguageService smpExceptionLanguageService;

    @BeforeEach
    public void prepareDatabase() {
        // setup initial data!
        testUtilsDao.clearData();
        testUtilsDao.createSubresources();
        testUtilsDao.createDomainTemplates();
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

        MatcherAssert.assertThat(smpExceptionLanguageService.getMessageTranslation(result.getMessageCode()),
                CoreMatchers.containsString("Invalid request [ResourceValidation]"));
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

        MatcherAssert.assertThat(result.getMessage(),
                CoreMatchers.containsString("Invalid request [SubresourceValidation]"));
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
        int docVersionCount = resource.getDocument().getDocumentVersions().size();
        DocumentRO testDoc = testInstance.generateDocumentForResource(resource.getId());
        assertNotNull(testDoc.getPayload());
        //when
        DocumentRO result = testInstance.saveDocumentForResource(resource.getId(), testDoc);
        // then
        assertNotNull(result);
        assertEquals(docVersionCount + 1, result.getDocumentVersions().size());
    }

    @ParameterizedTest
    @CsvSource({
            "STRING, new.property.for.test.string,newValue",
            "DATETIME, new.property.for.test.datetime,2024-06-20T12:34:56",
            "DATETIME, new.property.for.test.datetime,2024-06-20T12:34:56Z",
            "DATETIME, new.property.for.test.datetime,2024-06-20T12:34:56+01:00",
            "DATETIME, new.property.for.test.datetime,2024-06-20T12:34:56.123+01:00",
            "DATETIME, new.property.for.test.datetime,2024-06-20",
            "DATETIME, new.property.for.test.datetime,2024-06-20Z",
            "DATETIME, new.property.for.test.datetime,2024-06-20+01:30",
            "INTEGER, new.property.for.test.integer,1234",
            "INTEGER, new.property.for.test.integer,-1234",
            "BOOLEAN, new.property.for.test.boolean,true",
            "REGEXP, new.property.for.test.regexp,^[a-zA-Z0-9]+$",
            "EMAIL, new.property.for.test.email, test@mail.com",
            "FILENAME, new.property.for.test.filename, test-file.txt",
            "URL, new.property.for.test.url, https://test.url.com",
            "CERTIFICATE, new.property.for.test.certificate, -----BEGIN CERTIFICATE-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7vVf+8KX9z5b1xX1+5x7u6Hjv+2Zp4mJvXGZQIDAQAB-----END CERTIFICATE-----",
            "LIST_STRING, new.property.for.test.list.string, 'value1|value2|value3'",
            "MAP_STRING, new.property.for.test.map.string,'key1:value1|key2:value2|key3:value3'",
            "CRON_EXPRESSION, new.property.for.test.cron.expression,0 0/5 * * * ?"
    })
    void testSaveDocumentForResourceWithPropertiesOK(SMPPropertyTypeEnum propertyType, String propertyName, String propertyValue) {

        DBResource resource = testUtilsDao.getResourceD1G1RD1();
        DocumentRO testDoc = testInstance.getDocumentForResource(resource.getId(), -1);
        int propertyCount = testDoc.getProperties().size();
        testDoc.getProperties().add(TestROUtils.createDocumentProperty(
                propertyName, propertyValue, propertyType)
        );
        //when
        DocumentRO result = testInstance.saveDocumentForResource(resource.getId(), testDoc);

        // then
        assertNotNull(result);
        assertEquals(propertyCount + 1, result.getProperties().size());
        DocumentPropertyRO added = result.getProperties().stream()
                .filter(p -> p.getProperty().equals(propertyName)).findFirst().orElse(null);
        assertNotNull(added);
        assertEquals(propertyName, added.getProperty());
        assertEquals(propertyValue, added.getValue());
        assertEquals(propertyType, added.getType());

    }

    @ParameterizedTest
    @CsvSource({
            "DATETIME, new.property.for.test.datetime,Not-A-Date",
            "INTEGER, new.property.for.test.integer,Not-A-Integer",
            "BOOLEAN, new.property.for.test.boolean,Not-A-Boolean",
            "REGEXP, new.property.for.test.regexp,Invalid-A-RegExp-[{(",
            "EMAIL, new.property.for.test.email, Not-A-email",
            "FILENAME, new.property.for.test.filename, inva/li\\d-file.txt ",
            "URL, new.property.for.test.url, htp:/invalid-url",
            "CRON_EXPRESSION, new.property.for.test.cron.expression,0-0K/5 * * * ?"
    })
    void testSaveDocumentForResourceWithPropertiesInvalid(SMPPropertyTypeEnum propertyType, String propertyName, String propertyValue) {

        DBResource resource = testUtilsDao.getResourceD1G1RD1();
        DocumentRO testDoc = testInstance.getDocumentForResource(resource.getId(), -1);
        testDoc.getProperties().add(TestROUtils.createDocumentProperty(
                propertyName, propertyValue, propertyType)
        );
        //when
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class,
                () -> testInstance.saveDocumentForResource(resource.getId(), testDoc));

        // then
        assertNotNull(result);
        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString("Configuration error: invalid "));
    }


    @Test
    void testSaveDocumentForResourceWithCertificateProperty() throws Exception {
        String propertyName = "new.property.for.test.certificate";
        DBResource resource = testUtilsDao.getResourceD1G1RD1();
        DocumentRO testDoc = testInstance.getDocumentForResource(resource.getId(), -1);
        int propertyCount = testDoc.getProperties().size();
        CertificateRO cert = TestROUtils.createCertificateRO("CN=TestProperty,OU=Test,O=Test,L=Test,ST=Test,C=EU", BigInteger.TEN);

        DocumentPropertyRO certProperty = TestROUtils.createDocumentProperty(
                propertyName, cert.getCertificateId(), SMPPropertyTypeEnum.CERTIFICATE);
        certProperty.setCertificate(cert);
        testDoc.getProperties().add(certProperty);

        //when
        DocumentRO result = testInstance.saveDocumentForResource(resource.getId(), testDoc);

        // then
        assertNotNull(result);
        assertEquals(propertyCount + 1, result.getProperties().size());
        DocumentPropertyRO addedProperty = result.getProperties().stream()
                .filter(p -> p.getProperty().equals(propertyName)).findFirst().orElse(null);
        assertNotNull(addedProperty);
        assertEquals(propertyName, addedProperty.getProperty());
        // certificate data returned only on request
        assertNotNull(addedProperty.getCertificate());
        assertEquals(cert.getCertificateId(), addedProperty.getValue());

    }

    @Test
    void testDeleteDocumentVersionForResource() {
        DBResource resource = testUtilsDao.getResourceD1G1RD1();
        int docVersionCount = resource.getDocument().getDocumentVersions().size();
        DocumentRO testDoc = testInstance.generateDocumentForResource(resource.getId());
        assertNotNull(testDoc.getPayload());
        DocumentRO documentPayload = testInstance.saveDocumentForResource(resource.getId(), testDoc);
        assertEquals(docVersionCount + 1, documentPayload.getDocumentVersions().size());

        //when
        DocumentRO result = testInstance.deleteDocumentVersionForResource(resource.getId(), resource.getDocument().getId(), documentPayload.getPayloadVersion());
        // then
        assertNotNull(result);
        assertEquals(docVersionCount, result.getDocumentVersions().size());
        DocumentRO dbdoc = testInstance.getDocumentForResource(resource.getId(), documentPayload.getPayloadVersion());
        assertNotEquals(documentPayload.getPayloadVersion(), dbdoc.getPayloadVersion());

    }

    @Test
    void testSaveDocumentForTemplate() {
        DBDomainDocumentTemplate template = testUtilsDao.getDomainDocumentTemplateD1T1();
        int docVersionCount = template.getDocument().getDocumentVersions().size();

        DocumentRO testDoc = testInstance.generateTemplateDocument(template.getDomainResourceDef(), null);
        assertNotNull(testDoc.getPayload());

        //when
        DocumentRO result = testInstance.saveDocumentForTemplate(template.getId(), testDoc);
        // then
        assertNotNull(result);
        assertEquals(docVersionCount + 1, result.getDocumentVersions().size());
    }

    @Test
    void testSaveDocumentForSubresourceTemplate() {
        DBDomainDocumentTemplate template = testUtilsDao.getDomainDocumentTemplateD1T1Sub();
        int docVersionCount = template.getDocument().getDocumentVersions().size();

        DocumentRO testDoc = testInstance.generateTemplateDocument(template.getDomainResourceDef(), template.getSubresourceDef());
        assertNotNull(testDoc.getPayload());

        //when
        DocumentRO result = testInstance.saveDocumentForTemplate(template.getId(), testDoc);
        // then
        assertNotNull(result);
        assertEquals(docVersionCount + 1, result.getDocumentVersions().size());
    }

    @Test
    void testSaveDocumentForSubresource() {
        DBSubresource subresource = testUtilsDao.getSubresourceD1G1RD1_S1();
        int docVersionCount = subresource.getDocument().getDocumentVersions().size();
        DocumentRO testDoc = testInstance.generateDocumentForSubresource(subresource.getId(),
                subresource.getResource().getId());
        assertNotNull(testDoc.getPayload());

        //when
        DocumentRO result = testInstance.saveSubresourceDocumentForResource(subresource.getId(), subresource.getResource().getId(), testDoc);
        // then
        assertNotNull(result);
        assertEquals(docVersionCount + 1, result.getDocumentVersions().size());
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
