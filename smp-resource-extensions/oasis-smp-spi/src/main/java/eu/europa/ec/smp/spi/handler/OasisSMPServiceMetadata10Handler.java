package eu.europa.ec.smp.spi.handler;

import eu.europa.ec.dynamicdiscovery.core.validator.OasisSmpSchemaValidator;
import eu.europa.ec.dynamicdiscovery.exception.XmlInvalidAgainstSchemaException;
import eu.europa.ec.smp.spi.api.SmpXmlSignatureApi;
import eu.europa.ec.smp.spi.api.model.RequestData;
import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;
import eu.europa.ec.smp.spi.api.model.ResponseData;
import eu.europa.ec.smp.spi.converter.ServiceMetadataConverter;
import eu.europa.ec.smp.spi.exceptions.ResourceException;
import eu.europa.ec.smp.spi.exceptions.SignatureException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static eu.europa.ec.smp.spi.exceptions.ResourceException.ErrorCode.*;

@Component
public class OasisSMPServiceMetadata10Handler extends AbstractOasisSMP10Handler {

    private static final Logger LOG = LoggerFactory.getLogger(OasisSMPServiceMetadata10Handler.class);


    final SmpXmlSignatureApi signatureApi;

    public OasisSMPServiceMetadata10Handler(SmpXmlSignatureApi signatureApi) {
        this.signatureApi = signatureApi;
    }

    @Override
    public void readResource(RequestData resourceData, ResponseData responseData)  throws ResourceException {
        ResourceIdentifier resourceIdentifier = getResourceIdentifier(resourceData);
        ResourceIdentifier subresourceIdentifier = getSubresourceIdentifier(resourceData);

        if (resourceData.getResourceInputStream() == null) {
            LOG.warn("Empty document input stream for service-group: [{}] and service metadata [{}]", resourceIdentifier, subresourceIdentifier);
            return;
        }

        Document docEnvelopedMetadata;
        try {
            byte[] bytearray = readFromInputStream(resourceData.getResourceInputStream() );
            docEnvelopedMetadata =   ServiceMetadataConverter.toSignedServiceMetadataDocument(bytearray);
        } catch (IOException e) {
            throw new ResourceException(PARSE_ERROR, "Can not marshal extension for service group: [" + resourceIdentifier + "]. Error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }

        try {
            signatureApi.createEnvelopedSignature(resourceData, docEnvelopedMetadata.getDocumentElement(), Collections.emptyList());
        } catch (SignatureException e) {
            throw new ResourceException(PROCESS_ERROR, "Error occurred while signing the message!: [" + resourceIdentifier + "]. Error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }

        try {
            ServiceMetadataConverter.serialize(docEnvelopedMetadata, responseData.getOutputStream());
            responseData.setContentType("application/xml");
        } catch (TransformerException e) {
            throw new ResourceException(INTERNAL_ERROR, "Error occurred while writing the message: [" + resourceIdentifier + "]. Error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }
    }

    @Override
    public void storeResource(RequestData resourceData, ResponseData responseData) throws ResourceException {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateResource(RequestData resourceData, ResponseData responseData) throws ResourceException {
        ResourceIdentifier identifier = getResourceIdentifier(resourceData);
        try {
            OasisSmpSchemaValidator.validateOasisSMP10Schema(resourceData.getResourceInputStream());
        } catch (XmlInvalidAgainstSchemaException e) {
            String ids = identifier != null ?
                    Stream.of(identifier).map(identifier1 -> identifier1.toString()).collect(Collectors.joining(",")) : "";
            throw new ResourceException(INVALID_RESOURCE, "Error occurred while validation Oasis SMP 1.0 ServiceGroup: [" + ids + "] with error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }

        //TODO: validate if the identifiers matches the registered values

    }

}
