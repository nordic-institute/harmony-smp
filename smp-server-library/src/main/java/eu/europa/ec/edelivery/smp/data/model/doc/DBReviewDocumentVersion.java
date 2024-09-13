package eu.europa.ec.edelivery.smp.data.model.doc;

import eu.europa.ec.edelivery.smp.data.enums.DocumentVersionStatusType;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * Class represents the document version for the review. It is used with query
 * to get all review tasks for the user
 *
 * @since 5.1
 * @author Joze RIHARSIC
 */
public class DBReviewDocumentVersion implements Serializable {


    private Long documentId;
    private Long documentVersionId;
    private Long resourceId;
    private int version;
    private DocumentVersionStatusType status = DocumentVersionStatusType.DRAFT;
    private String resourceIdentifier;
    private String resourceScheme;
    private OffsetDateTime lastUpdatedOn;

    public DBReviewDocumentVersion() {
    }

    public DBReviewDocumentVersion(
            Long id,
            Long documentId,
            Long resourceId,
            int version,
            String status,
            String resourceIdentifier,
            String resourceScheme,
            OffsetDateTime lastUpdatedOn) {
        this.documentId = documentId;
        this.documentVersionId = id;
        this.resourceId = resourceId;
        this.version = version;
        this.status = StringUtils.isNotBlank(status) ? DocumentVersionStatusType.valueOf(status) : null;
        this.resourceIdentifier = resourceIdentifier;
        this.resourceScheme = resourceScheme;
        this.lastUpdatedOn = lastUpdatedOn;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public Long getDocumentVersionId() {
        return documentVersionId;
    }

    public void setDocumentVersionId(Long documentVersionId) {
        this.documentVersionId = documentVersionId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public OffsetDateTime getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(OffsetDateTime lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
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

}
