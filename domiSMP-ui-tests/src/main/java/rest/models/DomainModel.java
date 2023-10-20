package rest.models;

import ddsl.enums.ResponseCertificates;
import utils.Generator;
import utils.Utils;

import static ddsl.enums.ResponseCertificates.SMP_DOMAIN_01;
import static ddsl.enums.ResponseCertificates.SMP_DOMAIN_02;

public class DomainModel {

    private String smlSmpId;
    private String domainCode;
    private boolean smlRegistered;
    private String visibility;
    private String smlClientKeyAlias;
    private String signatureKeyAlias;
    private String smlSubdomain;
    private String smlParticipantIdentifierRegExp;
    private boolean smlClientCertAuth;
    private int status;

    public DomainModel() {
    }

    public static DomainModel generatePublicDomainModelWithoutSML() {
        DomainModel domainModel = new DomainModel();
        domainModel.domainCode = "AUTDom" + Generator.randomAlphaNumeric(6);
        domainModel.signatureKeyAlias = Utils.randomEnum(ResponseCertificates.values()).getName();
        domainModel.visibility = "PUBLIC";
        return domainModel;
    }

    public static DomainModel generatePublicDomainModelWithSML() {
        DomainModel domainModel = new DomainModel();
        domainModel.domainCode = "AUTDom" + Generator.randomAlphaNumeric(6);
        domainModel.signatureKeyAlias = Utils.randomEnum(new ResponseCertificates[]{SMP_DOMAIN_01, SMP_DOMAIN_02}).getName();
        domainModel.visibility = "PUBLIC";
        domainModel.smlClientCertAuth = true;
        domainModel.smlSubdomain = "AUTDomSML" + Generator.randomAlphaNumeric(6);
        domainModel.smlSmpId = "AUTSMLSMP" + Generator.randomAlphaNumeric(4);
        domainModel.smlClientKeyAlias = Utils.randomEnum(new ResponseCertificates[]{SMP_DOMAIN_01, SMP_DOMAIN_02}).toString().toLowerCase();
        return domainModel;
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

    public int getStatus() {
        return status;
    }
}


