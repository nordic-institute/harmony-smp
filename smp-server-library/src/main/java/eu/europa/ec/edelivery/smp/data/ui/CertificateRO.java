package eu.europa.ec.edelivery.smp.data.ui;

import java.time.OffsetDateTime;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class CertificateRO extends BaseRO {

    private static final long serialVersionUID = -4971552086560325302L;

    private String certificateId;
    private String alias;
    private String subject;
    private String issuer;
    private String serialNumber;
    private String crlUrl;
    private String encodedValue;
    private String clientCertHeader;
    private boolean isInvalid;
    private String invalidReason;
    private OffsetDateTime validFrom;
    private OffsetDateTime validTo;

    public CertificateRO() {
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public OffsetDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(OffsetDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public OffsetDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(OffsetDateTime validTo) {
        this.validTo = validTo;
    }

    public String getEncodedValue() {
        return encodedValue;
    }

    public void setEncodedValue(String encodedValue) {
        this.encodedValue = encodedValue;
    }

    public String getClientCertHeader() {
        return clientCertHeader;
    }

    public void setClientCertHeader(String clientCertHeader) {
        this.clientCertHeader = clientCertHeader;
    }

    public String getCrlUrl() {
        return crlUrl;
    }

    public void setCrlUrl(String crlUrl) {
        this.crlUrl = crlUrl;
    }

    public boolean isInvalid() {
        return isInvalid;
    }

    public void setInvalid(boolean invalid) {
        isInvalid = invalid;
    }

    public String getInvalidReason() {
        return invalidReason;
    }

    public void setInvalidReason(String invalidReason) {
        this.invalidReason = invalidReason;
    }
}
