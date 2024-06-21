package eu.europa.ec.smp.spi.enums;

import org.apache.commons.lang3.StringUtils;

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
