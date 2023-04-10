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
    private String smlClientCertHeader;
    private String smlClientKeyAlias;
    private String signatureKeyAlias;
    private boolean smlClientCertAuth;
    private boolean smlRegistered;
    private VisibilityType visibility;
    private String defaultResourceTypeIdentifier;
    private List<GroupRO> groups = new ArrayList<>();
    private List<String> resourceDefinitions = new ArrayList<>();

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
}
