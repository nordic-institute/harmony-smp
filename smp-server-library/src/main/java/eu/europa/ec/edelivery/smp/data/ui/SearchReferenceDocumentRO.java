package eu.europa.ec.edelivery.smp.data.ui;

import eu.europa.ec.edelivery.smp.data.enums.DocumentReferenceType;

/**
 *
 *
 * @since 5.0
 * @author Joze RIHTARSIC
 */
public class SearchReferenceDocumentRO extends BaseRO {

    private static final long serialVersionUID = 9008583888835630041L;

    private String documentId;
    private String resourceId;
    private String subresourceId;

    private DocumentReferenceType referenceType;
    private String referenceUrl;
    private String documentName;
    private String resourceValue;
    private String resourceScheme;
    private String subresourceValue;
    private String subresourceScheme;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getSubresourceId() {
        return subresourceId;
    }

    public void setSubresourceId(String subresourceId) {
        this.subresourceId = subresourceId;
    }


    public String getReferenceUrl() {
        return referenceUrl;
    }

    public void setReferenceUrl(String referenceUrl) {
        this.referenceUrl = referenceUrl;
    }

    public DocumentReferenceType getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(DocumentReferenceType referenceType) {
        this.referenceType = referenceType;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getResourceValue() {
        return resourceValue;
    }

    public void setResourceValue(String resourceValue) {
        this.resourceValue = resourceValue;
    }

    public String getResourceScheme() {
        return resourceScheme;
    }

    public void setResourceScheme(String resourceScheme) {
        this.resourceScheme = resourceScheme;
    }

    public String getSubresourceValue() {
        return subresourceValue;
    }

    public void setSubresourceValue(String subresourceValue) {
        this.subresourceValue = subresourceValue;
    }

    public String getSubresourceScheme() {
        return subresourceScheme;
    }

    public void setSubresourceScheme(String subresourceScheme) {
        this.subresourceScheme = subresourceScheme;
    }
}
