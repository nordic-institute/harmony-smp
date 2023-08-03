package eu.europa.ec.smp.spi.handler;

import eu.europa.ec.smp.spi.api.SmpDataServiceApi;
import eu.europa.ec.smp.spi.api.SmpIdentifierServiceApi;
import eu.europa.ec.smp.spi.api.SmpXmlSignatureApi;
import eu.europa.ec.smp.spi.api.model.RequestData;
import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;
import eu.europa.ec.smp.spi.api.model.ResponseData;
import eu.europa.ec.smp.spi.exceptions.CPPARuntimeException;
import eu.europa.ec.smp.spi.exceptions.ResourceException;
import eu.europa.ec.smp.spi.exceptions.SignatureException;
import eu.europa.ec.smp.spi.utils.CPPUtils;
import gen.eu.europa.ec.ddc.api.cppa.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBElement;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import static eu.europa.ec.smp.spi.exceptions.ResourceException.ErrorCode.*;

@Component
public class OasisCppa3CppHandler extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(OasisCppa3CppHandler.class);


    final SmpDataServiceApi smpDataApi;
    final SmpIdentifierServiceApi smpIdentifierApi;

    final SmpXmlSignatureApi signatureApi;


    public OasisCppa3CppHandler(SmpDataServiceApi smpDataApi,
                                SmpIdentifierServiceApi smpIdentifierApi,
                                SmpXmlSignatureApi signatureApi) {
        this.smpDataApi = smpDataApi;
        this.smpIdentifierApi = smpIdentifierApi;
        this.signatureApi = signatureApi;
    }

    public void generateResource(RequestData resourceData, ResponseData responseData, List<String> fields) throws ResourceException {


        ResourceIdentifier identifier = getResourceIdentifier(resourceData);
        CPP cpp = new CPP();
        Certificate singCert = CPPUtils.createCertificate("sing-cert-001", "sing-keyInfo-001");
        Certificate encCert = CPPUtils.createCertificate("enc-cert-001", "enc-keyInfo-001");
        HTTPTransport httpTransport = CPPUtils.createHTTPTransport("eDeliverAS4EndpointId", "http://localhost:8080/domibus/msh");
        EbMS3ChannelType channelType = CPPUtils.createEbMS3ChannelType("eDeliverAS4ChannelId", "bdxr-transport-ebms3-as4-v1p0", httpTransport);
        PayloadProfile payloadProfile = CPPUtils.createPayloadProfileWithOnePartType("mailProfileId", "Example mail profile", "MailPart", "text/plain", BigInteger.ONE, BigInteger.valueOf(100L));

        ProfileInfo profileInfo = new ProfileInfo();
        profileInfo.setProfileIdentifier(new ProfileIdentifier());
        profileInfo.setDescription(new Description());
        profileInfo.getProfileIdentifier().setValue("TestProfileId");
        profileInfo.getDescription().setValue("Test profile");
        cpp.setProfileInfo(profileInfo);


        // set party info
        cpp.setPartyInfo(new PartyInfoType());
        PartyName partyName = new PartyName();
        partyName.setValue(identifier.getValue());
        cpp.getPartyInfo().getPartyNames().add(partyName);
        cpp.getPartyInfo().getPartyIds().add(CPPUtils.createPartyTypeIdType(identifier.getValue(), identifier.getScheme()));

        cpp.getPartyInfo().getCertificates().add(singCert);
        cpp.getPartyInfo().getCertificates().add(encCert);

        cpp.getPartyInfo().setCertificateDefaults(CPPUtils.createCertificateDefaults(encCert, encCert));
        cpp.getTransports().add(CPPUtils.convertHTTPTransport(httpTransport));
        cpp.getChannels().add(CPPUtils.convertEbMS3ChannelType(channelType));
        cpp.getPayloadProfiles().add(payloadProfile);


        ServiceSpecification mailService = CPPUtils.createServiceSpecification("MailService", "MailSender", "MailReceiver");
        ServiceBinding mailServiceBinding = CPPUtils.createServiceBinding("SubmitMail", null);
        mailService.getServiceBindings().add(mailServiceBinding);
        mailServiceBinding.getActionBindings().add(CPPUtils.createActionBinding("SubmitMailId", "SubmitMail", payloadProfile, channelType));

        cpp.getServiceSpecifications().add(mailService);

        try {
            serializeNative(cpp, responseData.getOutputStream(), true);
        } catch (CPPARuntimeException e) {
            throw new ResourceException(PARSE_ERROR, "Can not marshal extension for service group: [" + identifier + "]. Error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }

    }


    @Override
    public void readResource(RequestData resourceData, ResponseData responseData) throws ResourceException {

        ResourceIdentifier identifier = getResourceIdentifier(resourceData);
        if (resourceData.getResourceInputStream() == null) {
            LOG.warn("Empty document input stream for service-group [{}]!", identifier);
            return;
        }

        InputStream inputStream = resourceData.getResourceInputStream();
        // reading resource multiple time make sure it can be rest
        if (!inputStream.markSupported()) {
            inputStream = new BufferedInputStream(inputStream);
        }
        inputStream.mark(Integer.MAX_VALUE - 2);
        validateResource(resourceData);

        try {
            inputStream.reset();
        } catch (IOException e) {
            throw new ResourceException(PARSE_ERROR, "Can not reset input stream", e);
        }

        try {
            Document doc = parse(inputStream);
            signatureApi.createEnvelopedSignature(resourceData, doc.getDocumentElement(), Collections.emptyList());
            serialize(doc, responseData.getOutputStream());
        } catch (SignatureException | SAXException | TransformerException | IOException e) {
            throw new ResourceException(PROCESS_ERROR, "Error occurred while signing the cpp documen!: ["
                    + identifier + "]. Error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }


    }

    @Override
    public void storeResource(RequestData resourceData, ResponseData responseData) throws ResourceException {
        InputStream inputStream = resourceData.getResourceInputStream();
        // reading resource multiple time make sure it can be rest
        if (!inputStream.markSupported()) {
            inputStream = new BufferedInputStream(inputStream);
        }

        inputStream.mark(Integer.MAX_VALUE - 2);

        CPP cppDocument = validateAndParse(resourceData);

        try {
            inputStream.reset();
            StreamUtils.copy(inputStream, responseData.getOutputStream());
            // need to save serviceGroup because of the update on the resource identifier values
            //reader.serializeNative(cppDocument, responseData.getOutputStream(), true);
        } catch (IOException e) {
            throw new ResourceException(PARSE_ERROR, "Error occurred while copying the ServiceGroup", e);
        }
    }

    /**
     * Method validates service group
     *
     * @param resourceData the resource data
     */
    @Override
    public void validateResource(RequestData resourceData) throws ResourceException {
        validateAndParse(resourceData);
    }

    private static Transformer createNewSecureTransformer() throws TransformerConfigurationException {
        TransformerFactory factory = TransformerFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        return factory.newTransformer();
    }

    public static void serialize(Document doc, OutputStream outputStream) throws TransformerException {
        Transformer transformer = createNewSecureTransformer();
        transformer.transform(new DOMSource(doc), new StreamResult(outputStream));
    }

    public CPP validateAndParse(RequestData resourceData) throws ResourceException {
        // get service group identifier
        ResourceIdentifier identifier = getResourceIdentifier(resourceData);
        // validate by schema
        byte[] bytearray;
        try {
            bytearray = readFromInputStream(resourceData.getResourceInputStream());
            validateOasisCPPASchema(bytearray);
        } catch (IOException ex) {
            throw new ResourceException(INVALID_RESOURCE, "Error occurred while reading Oasis CPP document: [" + identifier + "] with error: " + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        // if service group
        CPP cppDocument = parseNative(new ByteArrayInputStream(bytearray));
        if (cppDocument.getPartyInfo() == null || cppDocument.getPartyInfo().getPartyIds().isEmpty()) {
            throw new ResourceException(INVALID_RESOURCE, "Error occurred while validation Oasis CPP document. Missing PartyInfo/PartyId definition!");
        }

        boolean hasMatchingPartyId = false;
        final PartyInfoType partyInfo = cppDocument.getPartyInfo();
        for (PartyIdType partyId : partyInfo.getPartyIds()) {
            ResourceIdentifier xmlResourceIdentifier = smpIdentifierApi.normalizeResourceIdentifier(partyId.getValue(), partyId.getType());
            if (xmlResourceIdentifier.equals(identifier)) {
                hasMatchingPartyId = true;
                break;
            }
        }

        if (!hasMatchingPartyId) {
            // Business identifier must equal path
            throw new ResourceException(INVALID_PARAMETERS, "Non of participant identifiers match to URL parameter [" + identifier + "]!");
        }
        return cppDocument;
    }

    public static void validateOasisCPPASchema(byte[] xmlBody) throws ResourceException {
        validateOasisCPPASchema(new ByteArrayInputStream(xmlBody));
    }

    public static void validateOasisCPPASchema(InputStream xmlBody) throws ResourceException {

        try {
            getOasisCPPAValidator().validate(new StreamSource(xmlBody));
        } catch (SAXException | IOException e) {
            throw new ResourceException(INVALID_RESOURCE, "Error occurred while parsing Oasis CPPA3 document. Error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }
    }
}
