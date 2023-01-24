package eu.europa.ec.edelivery.smp.data.ui.exceptions;


import java.util.Objects;


/**
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class ErrorResponseRO {
    protected String businessCode;
    protected String errorDescription;
    protected String errorUniqueId;

    /**
     * Default no-arg constructor
     */
    public ErrorResponseRO() {

    }

    /**
     * Fully-initialising value constructor
     */
    public ErrorResponseRO(final String businessCode, final String errorDescription, final String errorUniqueId) {
        this.businessCode = businessCode;
        this.errorDescription = errorDescription;
        this.errorUniqueId = errorUniqueId;
    }


    public String getBusinessCode() {
        return businessCode;
    }


    public void setBusinessCode(String value) {
        this.businessCode = value;
    }


    public String getErrorDescription() {
        return errorDescription;
    }


    public void setErrorDescription(String value) {
        this.errorDescription = value;
    }


    public String getErrorUniqueId() {
        return errorUniqueId;
    }


    public void setErrorUniqueId(String value) {
        this.errorUniqueId = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorResponseRO that = (ErrorResponseRO) o;
        return businessCode.equals(that.businessCode) &&
                Objects.equals(errorDescription, that.errorDescription) &&
                errorUniqueId.equals(that.errorUniqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(businessCode, errorDescription, errorUniqueId);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ErrorResponseRO{");
        sb.append("'businessCode'='").append(businessCode).append('\'');
        sb.append(", 'errorDescription'='").append(errorDescription).append('\'');
        sb.append(", 'errorUniqueId'='").append(errorUniqueId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}