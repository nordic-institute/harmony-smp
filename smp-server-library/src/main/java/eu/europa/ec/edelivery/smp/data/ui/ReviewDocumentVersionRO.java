package eu.europa.ec.edelivery.smp.data.ui;

import eu.europa.ec.edelivery.smp.data.enums.DocumentVersionStatusType;

import java.time.OffsetDateTime;
/**
 * Class represents RO for the document version for the review
 *
 * @since 5.1
 * @author Joze RIHARSIC
 */
public class ReviewDocumentVersionRO {

    private String documentId;
    private String documentVersionId;
    private String resourceId;
    private int version;
    private DocumentVersionStatusType status = DocumentVersionStatusType.DRAFT;
    private String resourceIdentifier;
    private String resourceScheme;
    private OffsetDateTime lastUpdatedOn;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentVersionId() {
        return documentVersionId;
    }

    public void setDocumentVersionId(String documentVersionId) {
        this.documentVersionId = documentVersionId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public DocumentVersionStatusType getStatus() {
        return status;
    }

    public void setStatus(DocumentVersionStatusType status) {
        this.status = status;
    }

    public String getResourceIdentifier() {
        return resourceIdentifier;
    }

    public void setResourceIdentifier(String resourceIdentifier) {
        this.resourceIdentifier = resourceIdentifier;
    }

    public String getResourceScheme() {
        return resourceScheme;
    }

    public void setResourceScheme(String resourceScheme) {
        this.resourceScheme = resourceScheme;
    }

    public OffsetDateTime getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(OffsetDateTime lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }
}
