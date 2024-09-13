package eu.europa.ec.edelivery.smp.data.ui;

import eu.europa.ec.edelivery.smp.data.enums.DocumentVersionStatusType;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class DocumentVersionRO implements Serializable {
    private static final long serialVersionUID = 9008583888835630039L;

    private int version;
    private DocumentVersionStatusType versionStatus;
    private OffsetDateTime createdOn;
    private OffsetDateTime lastUpdatedOn;


    List<DocumentVersionEventRO> documentVersionEvents = new ArrayList<>();

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public DocumentVersionStatusType getVersionStatus() {
        return versionStatus;
    }

    public void setVersionStatus(DocumentVersionStatusType versionStatus) {
        this.versionStatus = versionStatus;
    }

    public List<DocumentVersionEventRO> getDocumentVersionEvents() {
        if (documentVersionEvents == null) {
            documentVersionEvents = new ArrayList<>();
        }
        return documentVersionEvents;
    }

    public OffsetDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(OffsetDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public OffsetDateTime getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(OffsetDateTime lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }
}
