package eu.europa.ec.edelivery.smp.data.ui;

public class ServiceGroupValidationRO extends BaseRO {
    private static final long serialVersionUID = 9008583888835630017L;

    public static final int ERROR_CODE_OK =0;
    public static final int ERROR_CODE_SERVICE_GROUP_EXISTS =1;
    public static final int ERROR_CODE_INVALID_EXTENSION =2;

    Long serviceGroupId;
    String extension;
    String errorMessage;
    String participantScheme;
    String participantIdentifier;
    int statusAction = 0;

    int errorCode = ERROR_CODE_OK;

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

    public int getStatusAction() {
        return statusAction;
    }

    public void setStatusAction(int statusAction) {
        this.statusAction = statusAction;
    }

    public String getParticipantScheme() {
        return participantScheme;
    }

    public void setParticipantScheme(String participantScheme) {
        this.participantScheme = participantScheme;
    }

    public String getParticipantIdentifier() {
        return participantIdentifier;
    }

    public void setParticipantIdentifier(String participantIdentifier) {
        this.participantIdentifier = participantIdentifier;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
