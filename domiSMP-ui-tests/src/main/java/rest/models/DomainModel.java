package rest.models;

import ddsl.enums.ResponseCertificates;
import org.apache.commons.lang3.StringUtils;
import utils.Generator;
import utils.Utils;

import java.util.List;

import static ddsl.enums.ResponseCertificates.SMP_DOMAIN_01;
import static ddsl.enums.ResponseCertificates.SMP_DOMAIN_02;

public class DomainModel {

    private String smlSmpId;
    private String domainCode;
    private String domainId;
    private boolean smlRegistered;
    private String visibility;
    private String smlClientKeyAlias;
    private String signatureKeyAlias;
    private String smlSubdomain;
    private String smlParticipantIdentifierRegExp;
    private boolean smlClientCertAuth;
    private Object actionMessage;
    private Object defaultResourceTypeIdentifier;
    private List<Object> groups;
    private Long index;
    private List<String> resourceDefinitions;
    private Long status;
    public DomainModel() {
    }
    public String getSmlSmpId() {
        return smlSmpId;
    }

    public String getDomainCode() {
        return domainCode;
    }

    public boolean isSmlRegistered() {
        return smlRegistered;
    }

    public String getVisibility() {
        return visibility.toUpperCase();
    }

    public String getSmlClientKeyAlias() {
        return smlClientKeyAlias;
    }

    public String getSignatureKeyAlias() {
        return signatureKeyAlias;
    }

    public String getSmlSubdomain() {
        return smlSubdomain;
    }

    public String getSmlParticipantIdentifierRegExp() {
        return smlParticipantIdentifierRegExp;
    }

    public boolean isSmlClientCertAuth() {
        return smlClientCertAuth;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public void setResourceDefinitions(List<String> resourceDefinitions) {
        this.resourceDefinitions = resourceDefinitions;
    }

    public void setSmlSmpId(String smlSmpId) {
        this.smlSmpId = smlSmpId;
    }

    public void setDomainCode(String domainCode) {
        this.domainCode = domainCode;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public void setSmlRegistered(boolean smlRegistered) {
        this.smlRegistered = smlRegistered;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public void setSmlClientKeyAlias(String smlClientKeyAlias) {
        this.smlClientKeyAlias = smlClientKeyAlias;
    }

    public void setSignatureKeyAlias(String signatureKeyAlias) {
        this.signatureKeyAlias = signatureKeyAlias;
    }

    public void setSmlSubdomain(String smlSubdomain) {
        this.smlSubdomain = smlSubdomain;
    }

    public void setSmlParticipantIdentifierRegExp(String smlParticipantIdentifierRegExp) {
        this.smlParticipantIdentifierRegExp = smlParticipantIdentifierRegExp;
    }

    public void setSmlClientCertAuth(boolean smlClientCertAuth) {
        this.smlClientCertAuth = smlClientCertAuth;
    }

    public void setActionMessage(Object actionMessage) {
        this.actionMessage = actionMessage;
    }

    public void setDefaultResourceTypeIdentifier(Object defaultResourceTypeIdentifier) {
        this.defaultResourceTypeIdentifier = defaultResourceTypeIdentifier;
    }

    public void setGroups(List<Object> groups) {
        this.groups = groups;
    }

    public static DomainModel generatePublicDomainModelWithSML() {
        DomainModel domainModel = new DomainModel();
        domainModel.domainCode = "AUTDom" + Generator.randomAlphaNumeric(6);
        domainModel.signatureKeyAlias = Utils.randomEnum(new ResponseCertificates[]{SMP_DOMAIN_01, SMP_DOMAIN_02}).getName();
        domainModel.visibility = "PUBLIC";
        domainModel.smlClientCertAuth = true;
        domainModel.smlSubdomain = "AUTDomSML" + Generator.randomAlphaNumeric(6);
        domainModel.smlSmpId = "AUTSMLSMP" + Generator.randomAlphaNumeric(4);
        domainModel.smlClientKeyAlias = StringUtils.lowerCase(Utils.randomEnum(new ResponseCertificates[]{SMP_DOMAIN_01, SMP_DOMAIN_02}).toString());
        return domainModel;
    }

    public static DomainModel generatePublicDomainModelWithoutSML() {
        DomainModel domainModel = new DomainModel();
        domainModel.domainCode = "AUTDom" + Generator.randomAlphaNumeric(6);
        domainModel.signatureKeyAlias = Utils.randomEnum(ResponseCertificates.values()).getName();
        domainModel.visibility = "PUBLIC";
        return domainModel;
    }
}


