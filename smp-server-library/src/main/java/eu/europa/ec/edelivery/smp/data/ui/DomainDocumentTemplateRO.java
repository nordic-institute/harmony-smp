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
package eu.europa.ec.edelivery.smp.data.ui;

import eu.europa.ec.edelivery.smp.data.enums.DocumentLevelType;

import java.io.Serial;

/**
 * RO for Domain Document Template
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class DomainDocumentTemplateRO extends BaseRO {

    @Serial
    private static final long serialVersionUID = 9008583888835630051L;

    private String templateId;
    private String domainCode;
    private String resourceDefIdentifier;
    private String subresourceDefIdentifier;
    private DocumentLevelType documentLevel;


    public String getTemplateId() {
        return templateId;
    }
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getDomainCode() {
        return domainCode;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }

    public String getResourceDefIdentifier() {
        return resourceDefIdentifier;
    }

    public void setResourceDefIdentifier(String resourceDefIdentifier) {
        this.resourceDefIdentifier = resourceDefIdentifier;
    }

    public String getSubresourceDefIdentifier() {
        return subresourceDefIdentifier;
    }

    public void setSubresourceDefIdentifier(String subresourceDefIdentifier) {
        this.subresourceDefIdentifier = subresourceDefIdentifier;
    }

    public DocumentLevelType getDocumentLevel() {
        return documentLevel;
    }

    public void setDocumentLevel(DocumentLevelType documentLevel) {
        this.documentLevel = documentLevel;
    }
}