package eu.europa.ec.edelivery.smp.data.ui;


/**
 * @author Joze Rihtarsic
 * @since 4.1
 */

public class DomainRO extends BaseRO {


    private static final long serialVersionUID = -9008583888835630560L;

    Long id;
    String domainCode;
    String smlSubdomain;
    String smlSmpId;
    String smlClientCertHeader;
    String smlClientKeyAlias;
    String signatureKeyAlias;
    boolean smlClientCertAuth;
    boolean smlRegistered;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDomainCode() {
        return domainCode;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }

    public String getSmlSubdomain() {
        return smlSubdomain;
    }

    public void setSmlSubdomain(String smlSubdomain) {
        this.smlSubdomain = smlSubdomain;
    }

    public String getSmlSmpId() {
        return smlSmpId;
    }

    public void setSmlSmpId(String smlSmpId) {
        this.smlSmpId = smlSmpId;
    }

    public String getSmlClientCertHeader() {
        return smlClientCertHeader;
    }

    public void setSmlClientCertHeader(String smlClientCertHeader) {
        this.smlClientCertHeader = smlClientCertHeader;
    }

    public String getSmlClientKeyAlias() {
        return smlClientKeyAlias;
    }

    public void setSmlClientKeyAlias(String smlClientKeyAlias) {
        this.smlClientKeyAlias = smlClientKeyAlias;
    }

    public String getSignatureKeyAlias() {
        return signatureKeyAlias;
    }

    public void setSignatureKeyAlias(String signatureKeyAlias) {
        this.signatureKeyAlias = signatureKeyAlias;
    }

    public boolean isSmlClientCertAuth() {
        return smlClientCertAuth;
    }

    public void setSmlClientCertAuth(boolean smlClientCertAuth) {
        this.smlClientCertAuth = smlClientCertAuth;
    }

    public boolean isSmlRegistered() {
        return smlRegistered;
    }

    public void setSmlRegistered(boolean smlRegistered) {
        this.smlRegistered = smlRegistered;
    }
}
