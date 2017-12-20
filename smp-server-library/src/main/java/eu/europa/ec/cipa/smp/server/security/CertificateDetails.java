/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.cipa.smp.server.security;

/**
 * Created by feriaad on 18/06/2015.
 */

import java.io.Serializable;
import java.util.Calendar;

public class CertificateDetails implements Serializable {
    private String certificateId;
    private String serial;
    private String subject;
    private Calendar validFrom;
    private Calendar validTo;
    private String issuer;
    private String pemEncoding;
    private String rootCertificateDN;
    private String policyOids;

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Calendar getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Calendar validFrom) {
        this.validFrom = validFrom;
    }

    public Calendar getValidTo() {
        return validTo;
    }

    public void setValidTo(Calendar validTo) {
        this.validTo = validTo;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getPemEncoding() {
        return pemEncoding;
    }

    public void setPemEncoding(String pemEncoding) {
        this.pemEncoding = pemEncoding;
    }

    public String getRootCertificateDN() {
        return rootCertificateDN;
    }

    public void setRootCertificateDN(String rootCertificateDN) {
        this.rootCertificateDN = rootCertificateDN;
    }

    public String getCertificateId() {return certificateId;}

    public void setCertificateId(String certificateId) {this.certificateId = certificateId;}

    public String getPolicyOids() {return policyOids;}

    public void setPolicyOids(String policyOids) {this.policyOids = policyOids;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CertificateDetails that = (CertificateDetails) o;
        if (certificateId != null ? !certificateId.equals(that.certificateId) : that.certificateId != null) return false;
        if (serial != null ? !serial.equals(that.serial) : that.serial != null) return false;
        if (subject != null ? !subject.equals(that.subject) : that.subject != null) return false;
        if (validFrom != null ? !validFrom.equals(that.validFrom) : that.validFrom != null) return false;
        if (validTo != null ? !validTo.equals(that.validTo) : that.validTo != null) return false;
        if (issuer != null ? !issuer.equals(that.issuer) : that.issuer != null) return false;
        if (pemEncoding != null ? !pemEncoding.equals(that.pemEncoding) : that.pemEncoding != null) return false;
        if (policyOids != null ? !policyOids.equals(that.policyOids) : that.policyOids != null) return false;
        return !(rootCertificateDN != null ? !rootCertificateDN.equals(that.rootCertificateDN) : that.rootCertificateDN != null);

    }

    @Override
    public int hashCode() {
        int result = certificateId != null ? certificateId.hashCode() : 0;
        result = 31 * result + (serial != null ? serial.hashCode() : 0);
        result = 31 * result + (subject != null ? subject.hashCode() : 0);
        result = 31 * result + (validFrom != null ? validFrom.hashCode() : 0);
        result = 31 * result + (validTo != null ? validTo.hashCode() : 0);
        result = 31 * result + (issuer != null ? issuer.hashCode() : 0);
        result = 31 * result + (pemEncoding != null ? pemEncoding.hashCode() : 0);
        result = 31 * result + (policyOids != null ? policyOids.hashCode() : 0);
        result = 31 * result + (rootCertificateDN != null ? rootCertificateDN.hashCode() : 0);
        return result;
    }
}