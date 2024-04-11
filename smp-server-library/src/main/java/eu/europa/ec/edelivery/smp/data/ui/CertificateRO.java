/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.data.ui;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class CertificateRO extends BaseRO {

    private static final long serialVersionUID = 9008583888835630004L;

    private String certificateId;
    private String alias;
    private String publicKeyType;
    private String subject;
    private String issuer;
    private String serialNumber;
    private String crlUrl;
    private String encodedValue;
    private String clientCertHeader;
    private boolean isInvalid;
    private boolean isError;

    private boolean isContainingKey;

    private final List<String> certificatePolicies = new ArrayList<>();
    private String invalidReason;
    private OffsetDateTime validFrom;
    private OffsetDateTime validTo;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public String getCertificateId() {
        return certificateId;
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

    public String getPublicKeyType() {
        return publicKeyType;
    }

    public void setPublicKeyType(String publicKeyType) {
        this.publicKeyType = publicKeyType;
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

    public boolean isContainingKey() {
        return isContainingKey;
    }

    public void setContainingKey(boolean containingKey) {
        isContainingKey = containingKey;
    }

    public boolean isInvalid() {
        return isInvalid;
    }

    public void setInvalid(boolean invalid) {
        isInvalid = invalid;
    }

    public boolean isError() {
        return isError;
    }

    /**
     * Set blocking error
     * @param error
     */
    public void setError(boolean error) {
        isError = error;
    }

    public String getInvalidReason() {
        return invalidReason;
    }

    public void setInvalidReason(String invalidReason) {
        this.invalidReason = invalidReason;
    }

    public List<String> getCertificatePolicies() {
        return certificatePolicies;
    }
}
