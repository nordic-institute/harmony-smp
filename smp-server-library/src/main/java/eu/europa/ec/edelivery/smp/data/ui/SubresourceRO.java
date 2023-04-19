package eu.europa.ec.edelivery.smp.data.ui;

public class SubresourceRO extends BaseRO {
    private static final long serialVersionUID = 9008583888835630029L;
    String subresourceId;
    String identifierValue;
    String identifierScheme;
    String subresourceTypeIdentifier;


    public String getSubresourceId() {
        return subresourceId;
    }

    public void setSubresourceId(String subresourceId) {
        this.subresourceId = subresourceId;
    }

    public String getIdentifierValue() {
        return identifierValue;
    }

    public void setIdentifierValue(String identifierValue) {
        this.identifierValue = identifierValue;
    }

    public String getIdentifierScheme() {
        return identifierScheme;
    }

    public void setIdentifierScheme(String identifierScheme) {
        this.identifierScheme = identifierScheme;
    }

    public String getSubresourceTypeIdentifier() {
        return subresourceTypeIdentifier;
    }

    public void setSubresourceTypeIdentifier(String subresourceTypeIdentifier) {
        this.subresourceTypeIdentifier = subresourceTypeIdentifier;
    }
}
