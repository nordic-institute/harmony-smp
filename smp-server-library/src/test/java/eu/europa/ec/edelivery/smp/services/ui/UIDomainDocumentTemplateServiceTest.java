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

import eu.europa.ec.edelivery.smp.data.enums.DocumentLevelType;
import eu.europa.ec.edelivery.smp.data.enums.DocumentVersionStatusType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBDomainDocumentTemplate;
import eu.europa.ec.edelivery.smp.data.model.DBDomainResourceDef;
import eu.europa.ec.edelivery.smp.data.ui.DocumentRO;
import eu.europa.ec.edelivery.smp.data.ui.DomainDocumentTemplateRO;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.services.AbstractServiceTest;
import eu.europa.ec.edelivery.smp.testutil.TestROUtils;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for UIDomainDocumentTemplateService
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */

@ContextConfiguration(classes = {UIDomainDocumentTemplateService.class})
class UIDomainDocumentTemplateServiceTest extends AbstractServiceTest {

    @Autowired
    private UIDocumentService documentService;
    @Autowired
    private UIDomainDocumentTemplateService testInstance;

    @BeforeEach
    public void prepareDatabase() {
        testUtilsDao.clearData();
        testUtilsDao.createDomainTemplates();;
    }

    @Test
    void testGetAllDomainsDocumentTemplatesForDomain() {
        // the domain def for the document: see the createDomainTemplates
        DBDomainResourceDef d1r1 = testUtilsDao.getDomainResourceDefD1R1();

        List<DomainDocumentTemplateRO> templates = testInstance.getAllDomainsDocumentTemplatesForDomain(testUtilsDao.getD1().getId());
        assertNotNull(templates);
        assertEquals(2, templates.size());
        DomainDocumentTemplateRO template = templates.stream().filter(
                t -> t.getResourceDefIdentifier().equals(d1r1.getResourceDef().getIdentifier()) && t.getSubresourceDefIdentifier() == null
        ).findFirst().orElse(null);

        assertNotNull(template);
        assertEquals(d1r1.getDomain().getDomainCode(), template.getDomainCode());
        assertEquals(d1r1.getResourceDef().getIdentifier(), template.getResourceDefIdentifier());
        assertNull(template.getSubresourceDefIdentifier());
        assertEquals(DocumentLevelType.RESOURCE, template.getDocumentLevel());
    }

    @Test
    void testCreateTemplateForDomainAndTemplateData() {
        // given
        DBDomainResourceDef d2r1 = testUtilsDao.getDomainResourceDefD2R1();
        Long domainId = d2r1.getDomain().getId();
        String resourceDefIdentifier = d2r1.getResourceDef().getIdentifier();
        DomainDocumentTemplateRO templateRO = new DomainDocumentTemplateRO();
        templateRO.setDomainCode(d2r1.getDomain().getDomainCode());
        templateRO.setResourceDefIdentifier(resourceDefIdentifier);
        templateRO.setDocumentLevel(DocumentLevelType.RESOURCE);


        DomainDocumentTemplateRO result = testInstance.createTemplateForDomainAndTemplateData(domainId,templateRO);
        assertNotNull(result);
        assertEquals(d2r1.getDomain().getDomainCode(), result.getDomainCode());
        assertEquals(d2r1.getResourceDef().getIdentifier(), result.getResourceDefIdentifier());
        assertNull(result.getSubresourceDefIdentifier());
        assertEquals(DocumentLevelType.RESOURCE, result.getDocumentLevel());
    }


    @Test
    void testCreateTemplateForDomainAndTemplateDataFailAlreadyExists() {
        // template document on domain d1 for rdef1 already exists see the testUtilsDao.createDomainTemplates();;
        DBDomainResourceDef d1r1 = testUtilsDao.getDomainResourceDefD1R1();
        Long domainId = d1r1.getDomain().getId();
        String resourceDefIdentifier = d1r1.getResourceDef().getIdentifier();
        DomainDocumentTemplateRO templateRO = new DomainDocumentTemplateRO();
        templateRO.setDomainCode(d1r1.getDomain().getDomainCode());
        templateRO.setResourceDefIdentifier(resourceDefIdentifier);
        templateRO.setDocumentLevel(DocumentLevelType.RESOURCE);

        SMPRuntimeException result =  assertThrows(SMPRuntimeException.class, ()-> testInstance.createTemplateForDomainAndTemplateData(domainId,templateRO));
        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString("already exists"));
    }


    @Test
    void testDeleteTemplateForDomain() {
        // Given
        DBDomainResourceDef d2r1 = testUtilsDao.getDomainResourceDefD2R1();
        Long domainId = d2r1.getDomain().getId();
        String resourceDefIdentifier = d2r1.getResourceDef().getIdentifier();
        DomainDocumentTemplateRO templateRO = new DomainDocumentTemplateRO();
        templateRO.setDomainCode(d2r1.getDomain().getDomainCode());
        templateRO.setResourceDefIdentifier(resourceDefIdentifier);
        templateRO.setDocumentLevel(DocumentLevelType.RESOURCE);

        // Create a template to delete
        DomainDocumentTemplateRO created = testInstance.createTemplateForDomainAndTemplateData(domainId, templateRO);

        // When
        Long tmplId = SessionSecurityUtils.decryptEntityId(created.getTemplateId());
        DomainDocumentTemplateRO deleted = testInstance.deleteTemplateForDomain(domainId, tmplId);

        // Then
        assertNotNull(deleted);
        assertEquals(created.getDomainCode(), deleted.getDomainCode());
        assertEquals(created.getResourceDefIdentifier(), deleted.getResourceDefIdentifier());
    }

    @Test
    void testUpdateTemplateForDomain() {
        // Given
        DBDomainResourceDef d2r1 = testUtilsDao.getDomainResourceDefD2R1();
        Long domainId = d2r1.getDomain().getId();
        String resourceDefIdentifier = d2r1.getResourceDef().getIdentifier();
        DomainDocumentTemplateRO templateRO = new DomainDocumentTemplateRO();
        templateRO.setDomainCode(d2r1.getDomain().getDomainCode());
        templateRO.setResourceDefIdentifier(resourceDefIdentifier);
        templateRO.setDocumentLevel(DocumentLevelType.RESOURCE);

        // Create a template to delete
        DomainDocumentTemplateRO created = testInstance.createTemplateForDomainAndTemplateData(domainId, templateRO);
        Long tmplId = SessionSecurityUtils.decryptEntityId(created.getTemplateId());
        DocumentRO documentRO =  documentService.getDocumentForTemplate( tmplId, -1);
        assertEquals(1, documentRO.getDocumentVersions().size());
        DocumentRO update = TestROUtils.createDocument(DocumentVersionStatusType.DRAFT, EntityROStatus.NEW, "<test>updated</test>");
        // When
        DocumentRO result = testInstance.updateTemplateForDomain(domainId, tmplId, update);
        // Then
        assertNotNull(result);
        assertEquals(documentRO.getDocumentVersions().size()+1, result.getDocumentVersions().size());
    }

    @Test
    void testGetTemplateForDomainDocument() {
        // Given
        DBDomainDocumentTemplate d1T1 = testUtilsDao.getDomainDocumentTemplateD1T1();
        int currentVersion = d1T1.getDocument().getCurrentVersion();
        DBDomain d1 = testUtilsDao.getD1();
        String content = "<test>"+ UUID.randomUUID()+"</test>";
        DocumentRO documentRO = TestROUtils.createDocument(DocumentVersionStatusType.DRAFT, EntityROStatus.NEW, content);
        testInstance.updateTemplateForDomain(d1.getId(), d1T1.getId(), documentRO);

        // When get payload for last version
        DocumentRO result = testInstance.getDocumentTemplate(d1.getId(), d1T1.getId(), currentVersion+1);
        // Then
        assertNotNull(result);
        assertEquals(content, documentRO.getPayload());
    }
}