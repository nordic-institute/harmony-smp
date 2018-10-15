package eu.europa.ec.edelivery.smp.data.ui;


import java.io.Serializable;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */

public class DomainRO implements Serializable {


    private static final long serialVersionUID = -9008583888835630560L;

    Long id;

    String domainCode;
    String smlSubdomain;
    String smlSmpId;
    String smlParticipantIdentifierRegExp;
    String smlClientCertHeader;
    String smlClientKeyAlias;
    String signatureKeyAlias;
 ;


    public DomainRO() {

    }

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

    public String getSmlParticipantIdentifierRegExp() {
        return smlParticipantIdentifierRegExp;
    }

    public void setSmlParticipantIdentifierRegExp(String smlParticipantIdentifierRegExp) {
        this.smlParticipantIdentifierRegExp = smlParticipantIdentifierRegExp;
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
}
