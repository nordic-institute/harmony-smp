package eu.europa.ec.smp.spi.handler;

import eu.europa.ec.dynamicdiscovery.core.validator.OasisSmpSchemaValidator;
import eu.europa.ec.dynamicdiscovery.exception.XmlInvalidAgainstSchemaException;
import eu.europa.ec.smp.spi.api.SmpDataServiceApi;
import eu.europa.ec.smp.spi.api.SmpIdentifierServiceApi;
import eu.europa.ec.smp.spi.api.SmpXmlSignatureApi;
import eu.europa.ec.smp.spi.api.model.RequestData;
import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;
import eu.europa.ec.smp.spi.api.model.ResponseData;
import eu.europa.ec.smp.spi.converter.ServiceMetadata20Converter;
import eu.europa.ec.smp.spi.exceptions.ResourceException;
import eu.europa.ec.smp.spi.exceptions.SignatureException;
import eu.europa.ec.smp.spi.validation.ServiceMetadata20Validator;
import gen.eu.europa.ec.ddc.api.smp20.ServiceMetadata;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import static eu.europa.ec.smp.spi.exceptions.ResourceException.ErrorCode.*;

@Component
public class OasisSMPServiceMetadata20Handler extends AbstractOasisSMPHandler {

    private static final Logger LOG = LoggerFactory.getLogger(OasisSMPServiceMetadata20Handler.class);

    final SmpXmlSignatureApi signatureApi;
    final SmpDataServiceApi smpDataApi;
    final SmpIdentifierServiceApi smpIdentifierApi;
    final ServiceMetadata20Validator serviceMetadataValidator;

    public OasisSMPServiceMetadata20Handler(SmpDataServiceApi smpDataApi,
                                            SmpIdentifierServiceApi smpIdentifierApi,
                                            SmpXmlSignatureApi signatureApi,
                                            ServiceMetadata20Validator serviceMetadataValidator) {
        this.signatureApi = signatureApi;
        this.smpDataApi = smpDataApi;
        this.smpIdentifierApi = smpIdentifierApi;
        this.serviceMetadataValidator = serviceMetadataValidator;
    }

    @Override
    public void readResource(RequestData resourceData, ResponseData responseData) throws ResourceException {
        ResourceIdentifier resourceIdentifier = getResourceIdentifier(resourceData);
        ResourceIdentifier subresourceIdentifier = getSubresourceIdentifier(resourceData);

        if (resourceData.getResourceInputStream() == null) {
            LOG.warn("Empty document input stream for service-group: [{}] and service metadata [{}]", resourceIdentifier, subresourceIdentifier);
            return;
        }

        Document docEnvelopedMetadata;
        try {
            byte[] bytearray = readFromInputStream(resourceData.getResourceInputStream());
            docEnvelopedMetadata = ServiceMetadata20Converter.toSignedServiceMetadataDocument(bytearray);
        } catch (IOException e) {
            throw new ResourceException(PARSE_ERROR, "Can not marshal extension for service group: ["
                    + resourceIdentifier + "]. Error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }

        try {
            signatureApi.createEnvelopedSignature(resourceData, docEnvelopedMetadata.getDocumentElement(), Collections.emptyList());
        } catch (SignatureException e) {
            throw new ResourceException(PROCESS_ERROR, "Error occurred while signing the message!: ["
                    + resourceIdentifier + "]. Error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }

        try {
            ServiceMetadata20Converter.serialize(docEnvelopedMetadata, responseData.getOutputStream());
            responseData.setContentType("text/xml");
        } catch (TransformerException e) {
            throw new ResourceException(INTERNAL_ERROR, "Error occurred while writing the message: ["
                    + resourceIdentifier + "]. Error: " + ExceptionUtils.getRootCauseMessage(e), e);
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
        validateResource(resourceData, responseData);

        try {
            inputStream.reset();
        } catch (IOException e) {
            throw new ResourceException(PARSE_ERROR, "Can not reset input stream", e);
        }

        try {
            StreamUtils.copy(inputStream, responseData.getOutputStream());
        } catch (IOException e) {
            throw new ResourceException(PARSE_ERROR, "Error occurred while copying the ServiceGroup", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateResource(RequestData resourceData, ResponseData responseData) throws ResourceException {
        ResourceIdentifier identifier = getResourceIdentifier(resourceData);
        ResourceIdentifier documentIdentifier = getSubresourceIdentifier(resourceData);
        byte[] bytearray;
        try {
            bytearray = readFromInputStream(resourceData.getResourceInputStream());
            OasisSmpSchemaValidator.validateOasisSMP20ServiceMetadataSchema(bytearray);
        } catch (IOException | XmlInvalidAgainstSchemaException e) {
            throw new ResourceException(INVALID_RESOURCE, "Error occurred while validation Oasis SMP 1.0 ServiceMetadata: [" + identifier + "] with error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }

        ServiceMetadata serviceMetadata = ServiceMetadata20Converter.unmarshal(bytearray);
        serviceMetadataValidator.validate(identifier, documentIdentifier, serviceMetadata);

    }

}
