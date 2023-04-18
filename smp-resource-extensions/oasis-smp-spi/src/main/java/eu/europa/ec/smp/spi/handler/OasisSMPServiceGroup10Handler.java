package eu.europa.ec.smp.spi.handler;

import eu.europa.ec.dynamicdiscovery.core.extension.impl.OasisSMP10ServiceGroupReader;
import eu.europa.ec.dynamicdiscovery.core.validator.OasisSmpSchemaValidator;
import eu.europa.ec.dynamicdiscovery.exception.TechnicalException;
import eu.europa.ec.dynamicdiscovery.exception.XmlInvalidAgainstSchemaException;
import eu.europa.ec.smp.spi.api.SmpDataServiceApi;
import eu.europa.ec.smp.spi.api.SmpIdentifierServiceApi;
import eu.europa.ec.smp.spi.api.model.RequestData;
import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;
import eu.europa.ec.smp.spi.api.model.ResponseData;
import eu.europa.ec.smp.spi.def.OasisSMPServiceMetadata10;
import eu.europa.ec.smp.spi.exceptions.ResourceException;
import gen.eu.europa.ec.ddc.api.smp10.ParticipantIdentifierType;
import gen.eu.europa.ec.ddc.api.smp10.ServiceGroup;
import gen.eu.europa.ec.ddc.api.smp10.ServiceMetadataReferenceCollectionType;
import gen.eu.europa.ec.ddc.api.smp10.ServiceMetadataReferenceType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.xml.bind.JAXBException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static eu.europa.ec.smp.spi.exceptions.ResourceException.ErrorCode.*;

@Component
public class OasisSMPServiceGroup10Handler extends AbstractOasisSMPHandler {

    private static final Logger LOG = LoggerFactory.getLogger(OasisSMPServiceGroup10Handler.class);

    OasisSMP10ServiceGroupReader reader = new OasisSMP10ServiceGroupReader();

    final SmpDataServiceApi smpDataApi;
    final SmpIdentifierServiceApi smpIdentifierApi;

    final OasisSMP10ServiceGroupReader serviceGroupReader;

    public OasisSMPServiceGroup10Handler(SmpDataServiceApi smpDataApi, SmpIdentifierServiceApi smpIdentifierApi) {
        this.smpDataApi = smpDataApi;
        this.smpIdentifierApi = smpIdentifierApi;
        serviceGroupReader = new OasisSMP10ServiceGroupReader();
    }

    public void generateResource(RequestData resourceData, ResponseData responseData, List<String> fields) throws ResourceException {
        ResourceIdentifier identifier = getResourceIdentifier(resourceData);

        ServiceGroup serviceGroup = new ServiceGroup();
        serviceGroup.setParticipantIdentifier(new ParticipantIdentifierType());
        serviceGroup.getParticipantIdentifier().setValue(identifier.getValue());
        serviceGroup.getParticipantIdentifier().setScheme(identifier.getScheme());
        serviceGroup.setServiceMetadataReferenceCollection(new ServiceMetadataReferenceCollectionType());

        try {
            reader.serializeNative(serviceGroup, responseData.getOutputStream(), true);
        } catch (TechnicalException e) {
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


        ServiceGroup serviceGroup = null;
        try {
            serviceGroup = reader.parseNative(resourceData.getResourceInputStream());
        } catch (TechnicalException e) {
            throw new ResourceException(PARSE_ERROR, "Can not pase service group xml for indentifier: [" + identifier + "]. Error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }
        // get references
        serviceGroup.setServiceMetadataReferenceCollection(new ServiceMetadataReferenceCollectionType());
        List<ServiceMetadataReferenceType> referenceTypes = buildReferences(identifier);
        serviceGroup.getServiceMetadataReferenceCollection().getServiceMetadataReferences().addAll(referenceTypes);

        try {
            reader.serializeNative(serviceGroup, responseData.getOutputStream(), false);
        } catch (TechnicalException e) {
            throw new ResourceException(PARSE_ERROR, "Can not marshal extension for service group: [" + identifier + "]. Error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }
    }


    private List<ServiceMetadataReferenceType> buildReferences(ResourceIdentifier resourceIdentifier) throws ResourceException {
        LOG.debug("Build build References identifier [{}].", resourceIdentifier);
        // get subresource identifiers for document type
        List<ResourceIdentifier> subResourceIdentifier = smpDataApi.getSubResourceIdentifiers(resourceIdentifier, OasisSMPServiceMetadata10.RESOURCE_IDENTIFIER);

        List<ServiceMetadataReferenceType> referenceIds = new ArrayList<>();
        for (ResourceIdentifier subresId : subResourceIdentifier) {
            URI url = buildSMPURLForParticipantAndDocumentIdentifier(resourceIdentifier, subresId);
            ServiceMetadataReferenceType referenceType = new ServiceMetadataReferenceType();
            referenceType.setHref(url.getPath());
            referenceIds.add(referenceType);
        }
        return referenceIds;
    }

    public URI buildSMPURLForParticipantAndDocumentIdentifier(ResourceIdentifier resourceIdentifier, ResourceIdentifier subresourceIdentifier) throws ResourceException {
        LOG.debug("Build SMP url for participant identifier: [{}] and document identifier [{}].", resourceIdentifier, subresourceIdentifier);
        String pathSegment = smpDataApi.getURIPathSegmentForSubresource(OasisSMPServiceMetadata10.RESOURCE_IDENTIFIER);
        String baseUrl = smpDataApi.getResourceUrl();
        String urlEncodedFormatParticipant = smpIdentifierApi.getURLEncodedResourceIdentifier(resourceIdentifier);
        String urlEncodedFormatDocument = smpIdentifierApi.getURLEncodedSubresourceIdentifier(subresourceIdentifier);
        try {
            return new URIBuilder(baseUrl)
                    .appendPathSegments(urlEncodedFormatParticipant)
                    .appendPathSegments(pathSegment)
                    .appendPathSegments(urlEncodedFormatDocument).build();
        } catch (URISyntaxException e) {
            throw new ResourceException(INTERNAL_ERROR, "Can not build SMP document URL path! " + ExceptionUtils.getMessage(e), e);
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
        ServiceGroup serviceGroup = validateAndParse(resourceData);

        // ServiceMetadataReferenceCollection must be empty because they are automatically generated
        if (serviceGroup.getServiceMetadataReferenceCollection()!=null
                && !serviceGroup.getServiceMetadataReferenceCollection().getServiceMetadataReferences().isEmpty() ) {
            throw new ResourceException(INVALID_PARAMETERS, "ServiceMetadataReferenceCollection must be empty!");
        }
        // set participant to "lowercase" to match it as is saved in the database
        // this is just for back-compatibility issue!
        serviceGroup.getParticipantIdentifier().setValue(resourceData.getResourceIdentifier().getValue());
        serviceGroup.getParticipantIdentifier().setScheme(resourceData.getResourceIdentifier().getScheme());

        try {
            StreamUtils.copy(inputStream, responseData.getOutputStream());
            //reader.serializeNative(serviceGroup, responseData.getOutputStream(), false);
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

    public ServiceGroup validateAndParse(RequestData resourceData) throws ResourceException {
        // get service group identifier
        ResourceIdentifier identifier = getResourceIdentifier(resourceData);
        // validate by schema
        byte[] bytearray;
        try {
            bytearray = readFromInputStream(resourceData.getResourceInputStream());
            OasisSmpSchemaValidator.validateOasisSMP10Schema(bytearray);
        } catch (IOException | XmlInvalidAgainstSchemaException e) {
            String ids = identifier != null ?
                    Stream.of(identifier).map(identifier1 -> identifier1.toString()).collect(Collectors.joining(",")) : "";
            throw new ResourceException(INVALID_RESOURCE, "Error occurred while validation Oasis SMP 1.0 ServiceGroup extension: [" + ids + "] with error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }
        // if service group
        ServiceGroup serviceGroup = null;
        try {
            serviceGroup = reader.parseNative(new ByteArrayInputStream(bytearray));
        } catch (TechnicalException e) {
            throw new ResourceException(INVALID_RESOURCE, "Error occurred while parsing Oasis SMP 1.0 ServiceGroup with error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }
        final ParticipantIdentifierType participantId = serviceGroup.getParticipantIdentifier();
        ResourceIdentifier xmlResourceIdentifier = smpIdentifierApi.normalizeResourceIdentifier(participantId.getValue(), participantId.getScheme());

        if (!xmlResourceIdentifier.equals(identifier)) {
            // Business identifier must equal path
            throw new ResourceException(INVALID_PARAMETERS, "Participant identifiers don't match between URL parameter [" + identifier + "] and XML body: [ scheme: '" + participantId.getScheme() + "', value: '" + participantId.getValue() + "']");
        }


        return serviceGroup;
    }
}
