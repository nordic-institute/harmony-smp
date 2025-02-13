
package rest.models;

import java.util.List;

/**
 * Data object for Document used in Subresource.
 */
public class DocumentModel {
    private List<Long> allVersions;
    private Long currentResourceVersion;
    private Object documentId;
    private String mimeType;
    private String name;
    private String payload;
    private Long payloadVersion;

    public List<Long> getAllVersions() {
        return allVersions;
    }

    public void setAllVersions(List<Long> allVersions) {
        this.allVersions = allVersions;
    }

    public Long getCurrentResourceVersion() {
        return currentResourceVersion;
    }

    public void setCurrentResourceVersion(Long currentResourceVersion) {
        this.currentResourceVersion = currentResourceVersion;
    }

    public Object getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Object documentId) {
        this.documentId = documentId;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Long getPayloadVersion() {
        return payloadVersion;
    }

    public void setPayloadVersion(Long payloadVersion) {
        this.payloadVersion = payloadVersion;
    }

}
