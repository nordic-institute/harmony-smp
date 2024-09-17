package eu.europa.ec.edelivery.smp.data.ui;

/**
 *
 *
 * @since 5.0
 * @author Joze RIHTARSIC
 */
public class SearchReferenceDocumentRO extends BaseRO {

    private static final long serialVersionUID = 9008583888835630041L;

    private String referenceDocumentId;
    private String referenceDocumentName;

    private String referenceResourceValue;
    private String referenceResourceScheme;
    private String referenceSubresourceValue;
    private String referenceSubresourceScheme;

    public String getReferenceDocumentId() {
        return referenceDocumentId;
    }

    public void setReferenceDocumentId(String referenceDocumentId) {
        this.referenceDocumentId = referenceDocumentId;
    }

    public String getReferenceDocumentName() {
        return referenceDocumentName;
    }

    public void setReferenceDocumentName(String referenceDocumentName) {
        this.referenceDocumentName = referenceDocumentName;
    }

    public String getReferenceResourceValue() {
        return referenceResourceValue;
    }

    public void setReferenceResourceValue(String referenceResourceValue) {
        this.referenceResourceValue = referenceResourceValue;
    }

    public String getReferenceResourceScheme() {
        return referenceResourceScheme;
    }

    public void setReferenceResourceScheme(String referenceResourceScheme) {
        this.referenceResourceScheme = referenceResourceScheme;
    }

    public String getReferenceSubresourceValue() {
        return referenceSubresourceValue;
    }

    public void setReferenceSubresourceValue(String referenceSubresourceValue) {
        this.referenceSubresourceValue = referenceSubresourceValue;
    }

    public String getReferenceSubresourceScheme() {
        return referenceSubresourceScheme;
    }

    public void setReferenceSubresourceScheme(String referenceSubresourceScheme) {
        this.referenceSubresourceScheme = referenceSubresourceScheme;
    }
}
