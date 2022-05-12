package eu.europa.ec.edelivery.smp.data.ui;

public class PropertyValidationRO {
    public static final int ERROR_CODE_OK =0;
    public static final int ERROR_CODE_SERVICE_GROUP_EXISTS =1;
    public static final int ERROR_CODE_INVALID_EXTENSION =2;

    String property;
    String value;

    boolean propertyValid;
    String errorMessage;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isPropertyValid() {
        return propertyValid;
    }

    public void setPropertyValid(boolean propertyValid) {
        this.propertyValid = propertyValid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
