package eu.europa.ec.smp.spi.utils;

import gen.eu.europa.ec.ddc.api.cppa.*;
import org.w3._2000._09.xmldsig_.KeyInfo;
import org.w3._2000._09.xmldsig_.ObjectFactory;
import org.w3._2000._09.xmldsig_.X509Data;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;

public class CPPUtils {


    private static final gen.eu.europa.ec.ddc.api.cppa.ObjectFactory CPPA_OBJECT_FACTORY = new gen.eu.europa.ec.ddc.api.cppa.ObjectFactory();

    protected CPPUtils() {
    }


    public static PartyIdType createPartyTypeIdType(String identifierValue, String scheme) {
        PartyIdType partyIdType = new PartyIdType();
        partyIdType.setType(scheme);
        partyIdType.setValue(identifierValue);
        return partyIdType;
    }

    public static Certificate createCertificate(String certId, String keyInfoID) {
        Certificate certificate = new Certificate();

        certificate.setId(certId);
        certificate.setKeyInfo(createCertificateKeyInfo(keyInfoID));
        return certificate;
    }

    public static CertificateDefaults createCertificateDefaults(Certificate signingCertId, Certificate encCertId) {
        CertificateDefaults certificateDefaults = new CertificateDefaults();
        CertificateRefType signingRefType = new CertificateRefType();
        signingRefType.setCertId(signingCertId);
        certificateDefaults.setSigningCertificateRef(signingRefType);

        CertificateRefType encRefType = new CertificateRefType();
        encRefType.setCertId(encCertId);
        certificateDefaults.setEncryptionCertificateRef(encRefType);

        return certificateDefaults;
    }

    public static KeyInfo createCertificateKeyInfo(String keyInfoId) {
        KeyInfo keyInfo = new KeyInfo();
        keyInfo.setId(keyInfoId);

        ObjectFactory xmldSigObjectFactory = new ObjectFactory();
        keyInfo.getContent().add(xmldSigObjectFactory.createKeyName("cn=" + keyInfoId));
        X509Data data = new X509Data();
        data.getX509IssuerSerialsAndX509SKISAndX509SubjectNames().add(xmldSigObjectFactory.createX509DataX509Certificate(("Replace " + keyInfoId + " this with real certificate!").getBytes()));
        keyInfo.getContent().add(data);
        return keyInfo;
    }

    public static ServiceSpecification createServiceSpecification(String serviceName, String partyRole, String counterPartyRole) {
        ServiceSpecification serviceSpecification = new ServiceSpecification();
        serviceSpecification.setName(serviceName);
        serviceSpecification.setPartyRole(new RoleType());
        serviceSpecification.setCounterPartyRole(new RoleType());
        serviceSpecification.getPartyRole().setName(partyRole);
        serviceSpecification.getCounterPartyRole().setName(counterPartyRole);
        return serviceSpecification;
    }

    public static ServiceBinding createServiceBinding(String service, String serviceType) {
        ServiceBinding serviceBinding = new ServiceBinding();
        serviceBinding.setService(new Service());
        serviceBinding.getService().setValue(service);
        serviceBinding.getService().setType(serviceType);
        return serviceBinding;
    }

    public static ActionBinding createActionBinding(String actionId, String action, PayloadProfile payloadProfileId, ChannelType channelBindingId) {

        ActionBinding actionBinding = new ActionBinding();
        actionBinding.setSendOrReceive(SendOrReceiveType.SEND);
        actionBinding.setId(actionId);
        actionBinding.setAction(action);
        actionBinding.getPayloadProfileIds().add(CPPA_OBJECT_FACTORY.createPayloadProfileId(payloadProfileId));
        actionBinding.getChannelIds().add(CPPA_OBJECT_FACTORY.createChannelId(channelBindingId));
        return actionBinding;
    }

    public static PayloadProfile createPayloadProfileWithOnePartType(String payloadProfileId, String profileDesc, String partName, String mimeType, BigInteger minOccurs, BigInteger maxOccurs) {
        PayloadProfile mailPayloadProfile = new PayloadProfile();
        mailPayloadProfile.setDescription(new Description());
        mailPayloadProfile.getDescription().setValue(profileDesc);
        mailPayloadProfile.setId(payloadProfileId);
        PayloadPart mailPart = new PayloadPart();
        mailPart.setPartName(partName);
        mailPart.setMaxOccurs(maxOccurs != null ? maxOccurs.toString(10) : null);
        mailPart.setMinOccurs(minOccurs);
        mailPart.setMIMEContentType(mimeType);
        mailPayloadProfile.getPayloadParts().add(mailPart);

        return mailPayloadProfile;
    }

    public static JAXBElement<? extends ChannelType> convertEbMS3ChannelType(EbMS3ChannelType ebMS3ChannelType ) {
        return CPPA_OBJECT_FACTORY.createEbMS3Channel(ebMS3ChannelType);
    }
    public static EbMS3ChannelType createEbMS3ChannelType(String channelId, String channelProfileCode, TransportType transportId) {
        EbMS3ChannelType ebMS3ChannelType = new EbMS3ChannelType();
        ebMS3ChannelType.setId(channelId);
        ebMS3ChannelType.setTransport(transportId);
        ebMS3ChannelType.setChannelProfile(channelProfileCode);
        return ebMS3ChannelType;
    }


    public static HTTPTransport createHTTPTransport(String transportId, String endpointUrl) {
        HTTPTransport httpTransport = new HTTPTransport();
        httpTransport.setId(transportId);
        httpTransport.setEndpoint(endpointUrl);
        httpTransport.setChunkedTransferCoding(true);
        return httpTransport;
    }

    public static JAXBElement<? extends TransportType> convertHTTPTransport(HTTPTransport transport) {

        return CPPA_OBJECT_FACTORY.createHTTPTransport(transport);
    }
}
