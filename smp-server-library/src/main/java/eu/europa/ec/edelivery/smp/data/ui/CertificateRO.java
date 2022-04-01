package eu.europa.ec.edelivery.smp.data.ui;

import com.fasterxml.jackson.annotation.JsonFormat;
import eu.europa.ec.edelivery.smp.utils.SMPConstants;

import java.util.Date;

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
    private String blueCoatHeader;
    private boolean isInvalid;
    private String invalidReason;


    @JsonFormat(pattern = SMPConstants.JSON_DATETIME_ISO)
    private Date validFrom;
    @JsonFormat(pattern = SMPConstants.JSON_DATETIME_ISO)
    private Date validTo;

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

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public String getEncodedValue() {
        return encodedValue;
    }

    public void setEncodedValue(String encodedValue) {
        this.encodedValue = encodedValue;
    }

    public String getBlueCoatHeader() {
        return blueCoatHeader;
    }

    public void setBlueCoatHeader(String blueCoatHeader) {
        this.blueCoatHeader = blueCoatHeader;
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
