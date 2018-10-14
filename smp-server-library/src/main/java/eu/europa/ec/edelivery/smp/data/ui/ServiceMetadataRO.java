package eu.europa.ec.edelivery.smp.data.ui;


import java.io.Serializable;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class ServiceMetadataRO extends BaseRO {


    private static final long serialVersionUID = 67944640449327185L;

    String documentIdentifier;
    String documentIdentifierScheme;
    String smlSubdomain;
    String domainCode;

    public String getDocumentIdentifier() {
        return documentIdentifier;
    }

    public void setDocumentIdentifier(String documentIdentifier) {
        this.documentIdentifier = documentIdentifier;
    }

    public String getDocumentIdentifierScheme() {
        return documentIdentifierScheme;
    }

    public void setDocumentIdentifierScheme(String documentIdentifierScheme) {
        this.documentIdentifierScheme = documentIdentifierScheme;
    }

    public String getSmlSubdomain() {
        return smlSubdomain;
    }

    public void setSmlSubdomain(String smlSubdomain) {
        this.smlSubdomain = smlSubdomain;
    }

    public String getDomainCode() {
        return domainCode;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }
}