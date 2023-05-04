package eu.europa.ec.smp.spi.handler;

import eu.europa.ec.dynamicdiscovery.core.extension.impl.OasisSMP20ServiceGroupReader;
import eu.europa.ec.dynamicdiscovery.core.validator.OasisSmpSchemaValidator;
import eu.europa.ec.dynamicdiscovery.exception.TechnicalException;
import eu.europa.ec.dynamicdiscovery.exception.XmlInvalidAgainstSchemaException;
import eu.europa.ec.smp.spi.api.SmpDataServiceApi;
import eu.europa.ec.smp.spi.api.SmpIdentifierServiceApi;
import eu.europa.ec.smp.spi.api.SmpXmlSignatureApi;
import eu.europa.ec.smp.spi.api.model.RequestData;
import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;
import eu.europa.ec.smp.spi.api.model.ResponseData;
import eu.europa.ec.smp.spi.converter.DomUtils;
import eu.europa.ec.smp.spi.def.OasisSMPServiceMetadata20;
import eu.europa.ec.smp.spi.exceptions.ResourceException;
import eu.europa.ec.smp.spi.exceptions.SignatureException;
import gen.eu.europa.ec.ddc.api.smp20.ServiceGroup;
import gen.eu.europa.ec.ddc.api.smp20.aggregate.ServiceReference;
import gen.eu.europa.ec.ddc.api.smp20.basic.ID;
import gen.eu.europa.ec.ddc.api.smp20.basic.ParticipantID;
import gen.eu.europa.ec.ddc.api.smp20.basic.SMPVersionID;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static eu.europa.ec.smp.spi.exceptions.ResourceException.ErrorCode.*;

@Component
public class OasisSMPServiceGroup20Handler extends AbstractOasisSMPHandler {

    private static final Logger LOG = LoggerFactory.getLogger(OasisSMPServiceGroup20Handler.class);

    final SmpDataServiceApi smpDataApi;
    final SmpXmlSignatureApi signatureApi;
    final SmpIdentifierServiceApi smpIdentifierApi;
    final OasisSMP20ServiceGroupReader reader;


    public OasisSMPServiceGroup20Handler(SmpDataServiceApi smpDataApi,
                                         SmpIdentifierServiceApi smpIdentifierApi,
                                         SmpXmlSignatureApi signatureApi) {
        this.smpDataApi = smpDataApi;
        this.smpIdentifierApi = smpIdentifierApi;
        this.signatureApi = signatureApi;
        this.reader = new OasisSMP20ServiceGroupReader();
    }


    public void generateResource(RequestData resourceData, ResponseData responseData, List<String> fields) throws ResourceException {
        ResourceIdentifier identifier = getResourceIdentifier(resourceData);


        ServiceGroup serviceGroup = new ServiceGroup();
        serviceGroup.setSMPVersionID(new SMPVersionID());
        serviceGroup.getSMPVersionID().setValue("2.0");
        serviceGroup.setParticipantID(new ParticipantID());
        serviceGroup.getParticipantID().setValue(identifier.getValue());
        serviceGroup.getParticipantID().setSchemeID(identifier.getScheme());

        try {
            reader.serializeNative(serviceGroup, responseData.getOutputStream(), true);
        } catch (TechnicalException e) {
            throw new ResourceException(PARSE_ERROR, "Can not marshal service group: [" + identifier + "]. Error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }
    }

    @Override
    public void readResource(RequestData resourceData, ResponseData responseData) throws ResourceException {

        ResourceIdentifier identifier = getResourceIdentifier(resourceData);
        if (resourceData.getResourceInputStream() == null) {
            LOG.warn("Empty document input stream for service-group [{}]!", identifier);
            return;
        }
        ServiceGroup serviceGroup = null;
        try {
            serviceGroup = reader.parseNative(resourceData.getResourceInputStream());
        } catch (TechnicalException e) {
            throw new ResourceException(PARSE_ERROR, "Can not read service group: [" + identifier + "]. Error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }
        // get references
        serviceGroup.getServiceReferences().clear();
        serviceGroup.getServiceReferences().addAll(buildReferences(identifier));


        try {
            Document doc = reader.objectToDocument(serviceGroup);
            signatureApi.createEnvelopedSignature(resourceData, doc.getDocumentElement(), Collections.emptyList());
            DomUtils.serialize(doc, responseData.getOutputStream());
        } catch (SignatureException | TechnicalException | TransformerException e) {
            throw new ResourceException(PROCESS_ERROR, "Error occurred while signing the service group 2.0 message!: ["
                    + identifier + "]. Error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }
    }


    private List<ServiceReference> buildReferences(ResourceIdentifier resourceIdentifier) {
        LOG.debug("Build build References identifier [{}].", resourceIdentifier);
        // get subresource identifiers for document type
        List<ResourceIdentifier> subResourceIdentifier = smpDataApi.getSubResourceIdentifiers(resourceIdentifier,
                OasisSMPServiceMetadata20.RESOURCE_IDENTIFIER);
        List<ServiceReference> referenceIds = new ArrayList<>();
        for (ResourceIdentifier subresId : subResourceIdentifier) {
            ServiceReference reference = new ServiceReference();
            ID id = new ID();
            id.setSchemeID(subresId.getScheme());
            id.setValue(subresId.getValue());
            reference.setID(id);
            referenceIds.add(reference);
        }
        return referenceIds;
    }


    @Override
    public void storeResource(RequestData resourceData, ResponseData responseData) throws ResourceException {
        InputStream inputStream = resourceData.getResourceInputStream();
        // reading resource multiple time make sure it can be rest
        if (!inputStream.markSupported()) {
            inputStream = new BufferedInputStream(inputStream);
        }
        inputStream.mark(Integer.MAX_VALUE - 2);
        ServiceGroup serviceGroup = validateAndParse(resourceData);

        // ServiceMetadataReferenceCollection must be empty because they are automatically generated
        if (!serviceGroup.getServiceReferences().isEmpty()) {
            throw new ResourceException(INVALID_PARAMETERS, "ServiceReferences must be empty!");
        }
        // set participant to "lowercase" to match it as is saved in the database
        // this is just for back-compatibility issue!
        serviceGroup.getParticipantID().setValue(resourceData.getResourceIdentifier().getValue());
        serviceGroup.getParticipantID().setSchemeID(resourceData.getResourceIdentifier().getScheme());

        try {
            reader.serializeNative(serviceGroup, responseData.getOutputStream(), false);
        } catch (TechnicalException e) {
            throw new ResourceException(PARSE_ERROR, "Error occurred while copying the ServiceGroup", e);
        }
    }

    /**
     * Method validates service group
     *
     * @param resourceData the resource data*
     */
    @Override
    public void validateResource(RequestData resourceData) throws ResourceException {

        validateAndParse(resourceData);
    }

    public ServiceGroup validateAndParse(RequestData resourceData) throws ResourceException {
        // get service group identifier
        ResourceIdentifier identifier = getResourceIdentifier(resourceData);
        // validate by schema
        byte[] bytearray;
        try {
            bytearray = readFromInputStream(resourceData.getResourceInputStream());
            OasisSmpSchemaValidator.validateOasisSMP20ServiceGroupSchema(bytearray);
        } catch (IOException | XmlInvalidAgainstSchemaException e) {
            String ids = identifier != null ?
                    Stream.of(identifier).map(identifier1 -> identifier1.toString()).collect(Collectors.joining(",")) : "";
            throw new ResourceException(INVALID_RESOURCE, "Error occurred while validation Oasis SMP 2.0 ServiceGroup: [" + ids + "] with error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }
        // if service group
        ServiceGroup serviceGroup = null;
        try {
            serviceGroup = reader.parseNative(new ByteArrayInputStream(bytearray));
        } catch (TechnicalException e) {
            throw new ResourceException(INVALID_RESOURCE, "Error occurred while reading the Oasis SMP 2.0 ServiceGroup with error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }
        final ParticipantID participantId = serviceGroup.getParticipantID();
        ResourceIdentifier xmlResourceIdentifier = smpIdentifierApi.normalizeResourceIdentifier(participantId.getValue(), participantId.getSchemeID());

        if (!xmlResourceIdentifier.equals(identifier)) {
            // Business identifier must equal path
            throw new ResourceException(INVALID_PARAMETERS, "Participant identifiers don't match between URL parameter [" + identifier + "] and XML body: [ scheme: '" + participantId.getSchemeID() + "', value: '" + participantId.getValue() + "']");
        }
        return serviceGroup;
    }
}
