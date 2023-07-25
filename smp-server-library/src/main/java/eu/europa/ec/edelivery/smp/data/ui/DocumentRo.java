package eu.europa.ec.edelivery.smp.data.ui;

import java.util.ArrayList;
import java.util.List;

public class DocumentRo {

    String documentId;
    String mimeType;
    Integer currentResourceVersion;
    List<Integer> allVersions;
    String name;
    Integer payloadVersion;
    String payload;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Integer getCurrentResourceVersion() {
        return currentResourceVersion;
    }

    public void setCurrentResourceVersion(Integer currentResourceVersion) {
        this.currentResourceVersion = currentResourceVersion;
    }

    public List<Integer> getAllVersions() {
        if (allVersions == null) {
            allVersions = new ArrayList<>();
        }
        return allVersions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Integer getPayloadVersion() {
        return payloadVersion;
    }

    public void setPayloadVersion(Integer payloadVersion) {
        this.payloadVersion = payloadVersion;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
