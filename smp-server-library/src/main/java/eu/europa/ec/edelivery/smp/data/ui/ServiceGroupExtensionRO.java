package eu.europa.ec.edelivery.smp.data.ui;

public class ServiceGroupExtensionRO extends BaseRO {
    private static final long serialVersionUID = -7555221767041516157L;
    Long serviceGroupId;
    String extension;
    String errorMessage;

    public Long getServiceGroupId() {
        return serviceGroupId;
    }

    public void setServiceGroupId(Long serviceGroupId) {
        this.serviceGroupId = serviceGroupId;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
