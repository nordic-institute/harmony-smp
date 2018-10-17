package eu.europa.ec.edelivery.smp.data.ui;





import eu.europa.ec.edelivery.smp.data.model.CommonColumnsLengths;

import javax.persistence.Column;
import javax.persistence.Id;
import java.math.BigInteger;
import java.time.LocalDateTime;


/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class CertificateRO extends BaseRO {



    private static final long serialVersionUID = -4971552086560325302L;

    private String certificateId;
    private String subject;
    private String issuer;
    private String serialNumber;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;

    public CertificateRO(){

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

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }
}
