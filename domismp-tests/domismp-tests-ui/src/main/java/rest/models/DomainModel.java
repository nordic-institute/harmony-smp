package rest.models;

import ddsl.enums.ResponseCertificates;
import utils.Generator;
import utils.Utils;

import java.util.List;

import static ddsl.enums.ResponseCertificates.SMP_DOMAIN_01;
import static ddsl.enums.ResponseCertificates.SMP_DOMAIN_02;

/**
 * Data model for Domain used for generating domains, mapping domain rest calls.
 */
public class DomainModel {

    private String smlSmpId;
    private String domainCode;
    private Boolean smlRegistered;
    private String visibility;
    private String defaultResourceTypeIdentifier;
    private Integer index;
    private List<Object> groups;
    private String smlClientKeyAlias;
    private String signatureKeyAlias;
    private String smlSubdomain;
    private Boolean smlClientCertAuth;
    private String statusMessage;
    private String domainId;
    private String actionMessage;
    private List<Object> resourceDefinitions;
    private Boolean smlAppendDomainCode;
    private Integer adminMemberCount;
    private Integer status;
    private Boolean smlUrlDomainCodeSuffixEnabled;
    private Boolean domainTrustStoreEnabled;

    public static DomainModel generatePublicDomainModelWithSML() {
        DomainModel domainModel = new DomainModel();
        domainModel.domainCode = "AUTDom" + Generator.randomAlphaNumericValue(6);
        domainModel.signatureKeyAlias = Utils.randomEnum(new ResponseCertificates[]{SMP_DOMAIN_01, SMP_DOMAIN_02}).getAlias();
        domainModel.visibility = "PUBLIC";
        domainModel.smlClientCertAuth = true;
        domainModel.smlSubdomain = "AUTDomSML" + Generator.randomAlphaNumericValue(6);
        domainModel.smlSmpId = "AUTSMLSMP" + Generator.randomAlphaNumericValue(4);
        domainModel.smlClientKeyAlias = Utils.randomEnum(new ResponseCertificates[]{SMP_DOMAIN_01, SMP_DOMAIN_02}).getAlias();
        domainModel.status = 0;
        domainModel.adminMemberCount = 0;
        return domainModel;
    }

    public void setSmlSmpId(String smlSmpId) {
        this.smlSmpId = smlSmpId;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }

    public String getSmlSmpId() {
        return smlSmpId;
    }

    public String getDomainCode() {
        return domainCode;
    }

    public void setSmlRegistered(Boolean smlRegistered) {
        this.smlRegistered = smlRegistered;
    }

    public Boolean isSmlRegistered() {
        return smlRegistered;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Object getDefaultResourceTypeIdentifier() {
        return defaultResourceTypeIdentifier;
    }

    public void setDefaultResourceTypeIdentifier(String defaultResourceTypeIdentifier) {
        this.defaultResourceTypeIdentifier = defaultResourceTypeIdentifier;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public List<Object> getGroups() {
        return groups;
    }

    public void setGroups(List<Object> groups) {
        this.groups = groups;
    }

    public void setSignatureKeyAlias(String signatureKeyAlias) {
        this.signatureKeyAlias = signatureKeyAlias;
    }

    public Object getSmlClientKeyAlias() {
        return smlClientKeyAlias;
    }

    public void setSmlClientKeyAlias(String smlClientKeyAlias) {
        this.smlClientKeyAlias = smlClientKeyAlias;
    }

    public void setSmlSubdomain(String smlSubdomain) {
        this.smlSubdomain = smlSubdomain;
    }

    public String getSignatureKeyAlias() {
        return signatureKeyAlias;
    }

    public String getSmlSubdomain() {
        return smlSubdomain;
    }

    public void setSmlClientCertAuth(Boolean smlClientCertAuth) {
        this.smlClientCertAuth = smlClientCertAuth;
    }

    public Boolean isSmlClientCertAuth() {
        return smlClientCertAuth;
    }

    public Object getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public Object getActionMessage() {
        return actionMessage;
    }

    public void setActionMessage(String actionMessage) {
        this.actionMessage = actionMessage;
    }

    public List<Object> getResourceDefinitions() {
        return resourceDefinitions;
    }

    public void setResourceDefinitions(List<Object> resourceDefinitions) {
        this.resourceDefinitions = resourceDefinitions;
    }

    public void setSmlAppendDomainCode(Boolean smlAppendDomainCode) {
        this.smlAppendDomainCode = smlAppendDomainCode;
    }

    public Boolean isSmlAppendDomainCode() {
        return smlAppendDomainCode;
    }

    public Integer getAdminMemberCount() {
        return adminMemberCount;
    }

    public void setAdminMemberCount(Integer adminMemberCount) {
        this.adminMemberCount = adminMemberCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getSmlUrlDomainCodeSuffixEnabled() {
        return smlUrlDomainCodeSuffixEnabled;
    }

    public void setSmlUrlDomainCodeSuffixEnabled(Boolean smlUrlDomainCodeSuffixEnabled) {
        this.smlUrlDomainCodeSuffixEnabled = smlUrlDomainCodeSuffixEnabled;
    }

    public Boolean getDomainTrustStoreEnabled() {
        return domainTrustStoreEnabled;
    }

    public void setDomainTrustStoreEnabled(Boolean domainTrustStoreEnabled) {
        this.domainTrustStoreEnabled = domainTrustStoreEnabled;
    }

    public static DomainModel generatePublicDomainModelWithoutSML() {
        DomainModel domainModel = new DomainModel();
        domainModel.domainCode = "AUTDom" + Generator.randomAlphaNumericValue(6);
        domainModel.signatureKeyAlias = Utils.randomEnum(ResponseCertificates.values()).getAlias();
        domainModel.visibility = "PUBLIC";
        return domainModel;
    }

    public static DomainModel generatePrivateDomainModelWithoutSML() {
        DomainModel domainModel = new DomainModel();
        domainModel.domainCode = "AUTDom" + Generator.randomAlphaNumericValue(6);
        domainModel.signatureKeyAlias = Utils.randomEnum(ResponseCertificates.values()).getAlias();
        domainModel.visibility = "PRIVATE";
        return domainModel;
    }
}


