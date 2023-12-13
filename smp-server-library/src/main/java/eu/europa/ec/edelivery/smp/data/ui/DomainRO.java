/*-
 * #%L
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
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
 * #L%
 */
package eu.europa.ec.edelivery.smp.data.ui;


import eu.europa.ec.edelivery.smp.data.enums.VisibilityType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Joze Rihtarsic
 * @since 4.1
 */

public class DomainRO extends BaseRO {

    private static final long serialVersionUID = 9008583888835630008L;

    private String domainId;
    private String domainCode;
    private String smlSubdomain;
    private String smlSmpId;
    private String smlClientKeyAlias;
    private String signatureKeyAlias;
    private boolean smlClientCertAuth;
    private boolean smlRegistered;
    private VisibilityType visibility;
    private String defaultResourceTypeIdentifier;
    private final List<GroupRO> groups = new ArrayList<>();
    private final List<String> resourceDefinitions = new ArrayList<>();
    private long adminMemberCount = -1;

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
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

    public VisibilityType getVisibility() {
        return visibility;
    }

    public void setVisibility(VisibilityType visibility) {
        this.visibility = visibility;
    }

    public String getDefaultResourceTypeIdentifier() {
        return defaultResourceTypeIdentifier;
    }

    public void setDefaultResourceTypeIdentifier(String defaultResourceTypeIdentifier) {
        this.defaultResourceTypeIdentifier = defaultResourceTypeIdentifier;
    }

    public List<GroupRO> getGroups() {
        return groups;
    }

    public List<String> getResourceDefinitions() {
        return resourceDefinitions;
    }

    public long getAdminMemberCount() {
        return adminMemberCount;
    }

    public void setAdminMemberCount(long adminMemberCount) {
        this.adminMemberCount = adminMemberCount;
    }
}
