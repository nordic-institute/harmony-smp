package eu.europa.ec.edelivery.smp.data.ui;


import javax.persistence.*;
import java.io.Serializable;

import static eu.europa.ec.edelivery.smp.data.model.CommonColumnsLengths.MAX_IDENTIFIER_VALUE_LENGTH;
import static eu.europa.ec.edelivery.smp.data.model.CommonColumnsLengths.MAX_USERNAME_LENGTH;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */

@Entity
@Table(name = "smp_domain")
public class DomainRO implements Serializable {

    @Id
    @Column(name = "domainId")
    private String domainId;
    @Column(name = "bdmslClientCertHeader")
    private String bdmslClientCertHeader;
    @Column(name = "bdmslClientCertAlias")
    private String bdmslClientCertAlias;
    @Column(name = "bdmslSmpId")
    private String bdmslSmpId;
    @Column(name = "signatureCertAlias")
    private String signatureCertAlias;


    public DomainRO(){

    }


    public DomainRO(String domainId, String bdmslClientCertHeader, String bdmslClientCertAlias, String bdmslSmpId, String signatureCertAlias) {
        this.domainId = domainId;
        this.bdmslClientCertHeader = bdmslClientCertHeader;
        this.bdmslClientCertAlias = bdmslClientCertAlias;
        this.bdmslSmpId = bdmslSmpId;
        this.signatureCertAlias = signatureCertAlias;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public String getBdmslClientCertHeader() {
        return bdmslClientCertHeader;
    }

    public void setBdmslClientCertHeader(String bdmslClientCertHeader) {
        this.bdmslClientCertHeader = bdmslClientCertHeader;
    }

    public String getBdmslClientCertAlias() {
        return bdmslClientCertAlias;
    }

    public void setBdmslClientCertAlias(String bdmslClientCertAlias) {
        this.bdmslClientCertAlias = bdmslClientCertAlias;
    }

    public String getBdmslSmpId() {
        return bdmslSmpId;
    }

    public void setBdmslSmpId(String bdmslSmpId) {
        this.bdmslSmpId = bdmslSmpId;
    }

    public String getSignatureCertAlias() {
        return signatureCertAlias;
    }

    public void setSignatureCertAlias(String signatureCertAlias) {
        this.signatureCertAlias = signatureCertAlias;
    }
}
