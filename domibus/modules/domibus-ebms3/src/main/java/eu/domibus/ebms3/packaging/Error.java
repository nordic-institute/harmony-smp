package eu.domibus.ebms3.packaging;

import eu.domibus.common.soap.Element;
import eu.domibus.ebms3.module.Constants;

/**
 * @author Hamid Ben Malek
 */
public class Error extends Element {
    private static final long serialVersionUID = -5795434364197382100L;

    public Error() {
        super(Constants.ERROR, Constants.NS, Constants.PREFIX);
    }

    public Error(final String errorCode, final String severity) {
        this();
        if (errorCode != null && !errorCode.trim().equals("")) {
            addAttribute("errorCode", errorCode);
        }
        if (severity != null && !severity.trim().equals("")) {
            addAttribute("severity", severity);
        }
    }

    public Error(final String errorCode, final String severity, final String refToMessageInError) {
        this(errorCode, severity);
        if (refToMessageInError != null && !refToMessageInError.trim().equals("")) {
            addAttribute("refToMessageInError", refToMessageInError);
        }
    }

    public Error(final String origin, final String category, final String errorCode, final String severity,
                 final String refToMessageInError) {
        this(errorCode, severity, refToMessageInError);
        setOrigin(origin);
        setCategory(category);
    }

    public Error(final String origin, final String category, final String errorCode, final String severity,
                 final String refToMessageInError, final String shortDescription) {
        this(origin, category, errorCode, severity, refToMessageInError);
        addAttribute("shortDescription", shortDescription);
    }

    public String getOrigin() {
        return getAttributeValue("origin");
    }

    public void setOrigin(final String origin) {
        if (origin != null && !origin.trim().equals("")) {
            addAttribute("origin", origin);
        }
    }

    public String getCategory() {
        return getAttributeValue("category");
    }

    public void setCategory(final String category) {
        if (category != null && !category.trim().equals("")) {
            addAttribute("category", category);
        }
    }

    public String getErrorCode() {
        return getAttributeValue("errorCode");
    }

    public void setErrorCode(final String errorCode) {
        if (errorCode != null && !errorCode.trim().equals("")) {
            addAttribute("errorCode", errorCode);
        }
    }

    public String getSeverity() {
        return getAttributeValue("severity");
    }

    public void setSeverity(final String severity) {
        if (severity != null && !severity.trim().equals("")) {
            addAttribute("severity", severity);
        }
    }

    public String getRefToMessageInError() {
        return getAttributeValue("refToMessageInError");
    }

    public void setRefToMessageInError(final String refToMessageInError) {
        if (refToMessageInError != null && !refToMessageInError.trim().equals("")) {
            addAttribute("refToMessageInError", refToMessageInError);
        }
    }

    public static Error getEmptyPartitionError(final String refToMessageInError) {
        return new Error("ebMS", "Communication", "EBMS:0006", "warning", refToMessageInError,
                         "EmptyMessagePartitionChannel");
    }

    public static Error getValueNotRecognizedError(final String refToMessageInError) {
        return new Error("ebMS", "Content", "EBMS:0001", "failure", refToMessageInError);
    }

    public static Error getFeatureNotSupportedError(final String refToMessageInError) {
        return new Error("ebMS", "Content", "EBMS:0002", "warning", refToMessageInError);
    }

    public static Error getValueInconsistentError(final String refToMessageInError) {
        return new Error("ebMS", "Content", "EBMS:0003", "failure", refToMessageInError);
    }

    public static Error getOtherError(final String refToMessageInError) {
        return new Error("ebMS", "Content", "EBMS:0004", "failure", refToMessageInError);
    }

    public static Error getConnectionFailureError(final String refToMessageInError) {
        return new Error("ebMS", "Communication", "EBMS:0005", "failure", refToMessageInError);
    }

    public static Error getMimeInconsistencyError(final String refToMessageInError) {
        return new Error("ebMS", "Unpackaging", "EBMS:0007", "failure", refToMessageInError);
    }

    public static Error getInvalidHeaderError(final String refToMessageInError) {
        return new Error("ebMS", "Unpackaging", "EBMS:0009", "failure", refToMessageInError);
    }

    public static Error getProcessingModeMismatchError(final String refToMessageInError) {
        return new Error("ebMS", "Processing", "EBMS:0010", "failure", refToMessageInError, "ProcessingModeMismatch");
    }

    public static Error getFailedAuthenticationError(final String refToMessageInError) {
        return new Error("security", "Processing", "EBMS:0101", "failure", refToMessageInError);
    }

    public static Error getDeliveryFailureError(final String refToMessageInError) {
        return new Error("reliability", "Communication", "EBMS:0202", "failure", refToMessageInError);
    }

    public static Error getDysfunctionalReliabilityError(final String refToMessageInError) {
        return new Error("reliability", "Processing", "EBMS:0201", "failure", refToMessageInError);
    }
}