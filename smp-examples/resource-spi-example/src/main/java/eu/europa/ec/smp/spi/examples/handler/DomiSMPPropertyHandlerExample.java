package eu.europa.ec.smp.spi.examples.handler;

import eu.europa.ec.edelivery.security.utils.CertificateKeyType;
import eu.europa.ec.edelivery.security.utils.X509CertificateUtils;
import eu.europa.ec.smp.spi.api.SmpDataServiceApi;
import eu.europa.ec.smp.spi.api.SmpIdentifierServiceApi;
import eu.europa.ec.smp.spi.api.SmpXmlSignatureApi;
import eu.europa.ec.smp.spi.api.model.RequestData;
import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;
import eu.europa.ec.smp.spi.api.model.ResponseData;
import eu.europa.ec.smp.spi.exceptions.ResourceException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bouncycastle.operator.OperatorCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static eu.europa.ec.smp.spi.exceptions.ResourceException.ErrorCode.*;

@Component
public class DomiSMPPropertyHandlerExample extends AbstractHandler {


    private static final Logger LOG = LoggerFactory.getLogger(DomiSMPPropertyHandlerExample.class);

    private static final String PROPERTY_IDENTIFIER = "domismp.extension.example.identifier";
    private static final String PROPERTY_URL = "domismp.extension.example.url";
    private static final String PROPERTY_EMAIL = "domismp.extension.example.email";
    private static final String PROPERTY_CERTIFICATE = "domismp.extension.example.certificate";


    final SmpDataServiceApi smpDataApi;
    final SmpIdentifierServiceApi smpIdentifierApi;

    final SmpXmlSignatureApi signatureApi;


    public DomiSMPPropertyHandlerExample(SmpDataServiceApi smpDataApi,
                                         SmpIdentifierServiceApi smpIdentifierApi,
                                         SmpXmlSignatureApi signatureApi) {
        this.smpDataApi = smpDataApi;
        this.smpIdentifierApi = smpIdentifierApi;
        this.signatureApi = signatureApi;
    }

    public void generateResource(RequestData resourceData, ResponseData responseData, List<String> fields) throws ResourceException {

        ResourceIdentifier identifier = getResourceIdentifier(resourceData);


        try {
            String identifierString = smpIdentifierApi.formatResourceIdentifier(identifier);
            Properties properties = new Properties();
            properties.setProperty(PROPERTY_IDENTIFIER, identifierString);
            properties.setProperty(PROPERTY_URL, "http://example.local/test");

            properties.setProperty(PROPERTY_EMAIL, "test.address@example.local");
            X509Certificate cert = createX509Certificate("CN="+identifierString+",O=edelivery,C=EU");
            properties.setProperty(PROPERTY_CERTIFICATE, Base64.getEncoder().encodeToString(cert.getEncoded()));

            properties.store(responseData.getOutputStream(), "DomiSMP property extension example");

        } catch (IOException | CertificateEncodingException e) {
            throw new ResourceException(PARSE_ERROR, "Can not marshal properties: [" + identifier + "]. Error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }
    }

    public static X509Certificate createX509Certificate(String subject) throws ResourceException {
        try {
            KeyPair key = X509CertificateUtils.generateKeyPair(CertificateKeyType.RSA_2048);
            return X509CertificateUtils.generateCertificate(
                    BigInteger.TEN, key.getPublic(), subject, OffsetDateTime.now().minusDays(1),
                    OffsetDateTime.now().plusYears(1), subject,
                    key.getPrivate(), false, -1, null,
                    Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | IOException |
                 CertificateException | OperatorCreationException e) {
            throw new ResourceException(INTERNAL_ERROR, "Error occurred at sample certificate generation!", e);
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

        try {
            inputStream.reset();
        } catch (IOException e) {
            throw new ResourceException(PARSE_ERROR, "Can not reset input stream", e);
        }

        try {
            StreamUtils.copy(inputStream, responseData.getOutputStream());
        } catch (IOException e) {
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
        Properties properties = validateAndParse(resourceData);
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

    public Properties validateAndParse(RequestData resourceData) throws ResourceException {
        // get service group identifier
        ResourceIdentifier identifier = getResourceIdentifier(resourceData);
        Properties properties = new Properties();
        // validate by schema

        try {
            properties.load(resourceData.getResourceInputStream());
        } catch (IOException ex) {
            throw new ResourceException(INVALID_RESOURCE, "Error occurred while reading example property document: [" + identifier + "] with error: " + ExceptionUtils.getRootCauseMessage(ex), ex);
        }

        if ( !properties.containsKey(PROPERTY_IDENTIFIER)){
            throw new ResourceException(INVALID_RESOURCE, "Missing  property document: [" + PROPERTY_IDENTIFIER + "]" );
        }
        if ( !properties.containsKey(PROPERTY_URL)){
            throw new ResourceException(INVALID_RESOURCE, "Missing  property document: [" + PROPERTY_URL + "]" );
        }
        if ( !properties.containsKey(PROPERTY_EMAIL)){
            throw new ResourceException(INVALID_RESOURCE, "Missing  property document: [" + PROPERTY_EMAIL + "]" );
        }
        if ( !properties.containsKey(PROPERTY_CERTIFICATE)){
            throw new ResourceException(INVALID_RESOURCE, "Missing  property document: [" + PROPERTY_CERTIFICATE + "]" );
        }
        String identifierString = smpIdentifierApi.formatResourceIdentifier(identifier);
        if (!StringUtils.equalsIgnoreCase(properties.getProperty(PROPERTY_IDENTIFIER),identifierString )){
            throw new ResourceException(INVALID_RESOURCE, "Property: [" + PROPERTY_IDENTIFIER + "] does not match value for the resource ["+identifierString+"]" );
        }

        try {
            new URL(properties.getProperty(PROPERTY_URL));
        } catch (MalformedURLException e) {
            throw new ResourceException(INVALID_RESOURCE, "Bad property value: [" + PROPERTY_URL + "]!. Value ["+properties.getProperty(PROPERTY_URL)+"]  is not URL" );
        }


        return properties;
    }

}
