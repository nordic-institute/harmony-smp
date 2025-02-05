/*-
 * #START_LICENSE#
 * smp-spi
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
package eu.europa.ec.smp.spi.enums;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.trim;

/**
 * Enum for transient document properties
 *
 * @author Joze Rihtarsic
 * @since 5.1
 */
public enum TransientDocumentPropertyType {
    RESOURCE_IDENTIFIER_VALUE("resource.identifier.value", "Resource Identifier Value"),
    RESOURCE_IDENTIFIER_SCHEME("resource.identifier.scheme", "Resource Identifier Scheme"),
    SUBRESOURCE_IDENTIFIER_VALUE("subresource.identifier.value", "Subresource Identifier Value"),
    SUBRESOURCE_IDENTIFIER_SCHEME("subresource.identifier.scheme", "Subresource Identifier Scheme"),
    DOCUMENT_NAME("document.name", "Document Name"),
    DOCUMENT_MIMETYPE("document.mimetype", "Document Mimetype"),
    DOCUMENT_VERSION("document.version", "Document Version"),
    ;

    String propertyName;
    String propertyDescription;

    TransientDocumentPropertyType(String propertyName, String propertyDescription) {
        this.propertyName = propertyName;
        this.propertyDescription = propertyDescription;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getPropertyPlaceholder() {
        return "${" + propertyName + "}";
    }

    public String getPropertyDescription() {
        return propertyDescription;
    }

    public static TransientDocumentPropertyType fromPropertyName(String propertyName) {
        for (TransientDocumentPropertyType transientDocumentPropertyType : values()) {
            if (equalsIgnoreCase(transientDocumentPropertyType.propertyName, trim(propertyName))) {
                return transientDocumentPropertyType;
            }
        }
        return null;
    }
}
