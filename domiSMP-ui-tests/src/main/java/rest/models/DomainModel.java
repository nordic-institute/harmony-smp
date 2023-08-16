package rest.models;

import ddsl.enums.ResponseCertificates;
import utils.Generator;

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
        domainModel.signatureKeyAlias = ResponseCertificates.getRandomCertificate();
        domainModel.visibility = "PUBLIC";


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
        return visibility;
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


