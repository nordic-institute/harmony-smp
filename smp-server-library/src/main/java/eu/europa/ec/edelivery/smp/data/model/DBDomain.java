/*
 * Copyright 2018 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.data.model;

import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.io.Serializable;

import static eu.europa.ec.edelivery.smp.data.model.CommonColumnsLengths.MAX_FREE_TEXT_LENGTH;
import static eu.europa.ec.edelivery.smp.data.model.CommonColumnsLengths.MAX_IDENTIFIER_VALUE_LENGTH;

/**
 * Created by gutowpa on 16/01/2018.
 */
@Entity
@Table(name = "smp_domain")
@Audited
public class DBDomain implements BaseEntity{

    private String domainId;
    private String bdmslClientCertHeader;
    private String bdmslClientCertAlias;
    private String bdmslSmpId;
    private String signatureCertAlias;

    public DBDomain() {
    }

    public DBDomain(String domainId, String bdmslClientCertHeader, String bdmslClientCertAlias, String bdmslSmpId, String signatureCertAlias) {
        this.domainId = domainId;
        this.bdmslClientCertHeader = bdmslClientCertHeader;
        this.bdmslClientCertAlias = bdmslClientCertAlias;
        this.bdmslSmpId = bdmslSmpId;
        this.signatureCertAlias = signatureCertAlias;
    }

    @Id
    @Column(name = "domainId", length = MAX_IDENTIFIER_VALUE_LENGTH)
    @Override
    public String getId() {
        return domainId;
    }

    @Column(name = "bdmslClientCertHeader", length = MAX_FREE_TEXT_LENGTH)
    public String getBdmslClientCertHeader() {
        return bdmslClientCertHeader;
    }

    @Column(name = "bdmslClientCertAlias", length = MAX_IDENTIFIER_VALUE_LENGTH)
    public String getBdmslClientCertAlias() {
        return bdmslClientCertAlias;
    }

    @Column(name = "bdmslSmpId", length = MAX_IDENTIFIER_VALUE_LENGTH, nullable = false)
    public String getBdmslSmpId() {
        return bdmslSmpId;
    }

    @Column(name = "signatureCertAlias", length = MAX_IDENTIFIER_VALUE_LENGTH)
    public String getSignatureCertAlias() {
        return signatureCertAlias;
    }

    public void setId(String domainId) {
        this.domainId = domainId;
    }

    public void setBdmslClientCertHeader(String bdmslClientCertHeader) {
        this.bdmslClientCertHeader = bdmslClientCertHeader;
    }

    public void setBdmslClientCertAlias(String bdmslClientCertAlias) {
        this.bdmslClientCertAlias = bdmslClientCertAlias;
    }

    public void setBdmslSmpId(String bdmslSmpId) {
        this.bdmslSmpId = bdmslSmpId;
    }

    public void setSignatureCertAlias(String signatureCertAlias) {
        this.signatureCertAlias = signatureCertAlias;
    }

}
