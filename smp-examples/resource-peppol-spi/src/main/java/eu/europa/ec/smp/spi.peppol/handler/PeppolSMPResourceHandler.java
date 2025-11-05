/*-
 * #START_LICENSE#
 * oasis-smp-spi
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.smp.spi.peppol.handler;

import eu.europa.ec.dynamicdiscovery.core.extension.impl.peppol.PeppolSMPServiceGroupReader;
import eu.europa.ec.dynamicdiscovery.core.validator.PeppolSmpSchemaValidator;
import eu.europa.ec.dynamicdiscovery.exception.TechnicalException;
import eu.europa.ec.dynamicdiscovery.exception.XmlInvalidAgainstSchemaException;
import eu.europa.ec.smp.spi.api.SmpDataServiceApi;
import eu.europa.ec.smp.spi.api.SmpIdentifierServiceApi;
import eu.europa.ec.smp.spi.api.model.RequestData;
import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;
import eu.europa.ec.smp.spi.api.model.ResponseData;
import eu.europa.ec.smp.spi.enums.TransientDocumentPropertyType;
import eu.europa.ec.smp.spi.exceptions.ResourceException;
import eu.europa.ec.smp.spi.peppol.def.PeppolSMPResource;
import eu.europa.ec.smp.spi.peppol.def.PeppolSMPSubresource;
import eu.europa.ec.smp.spi.utils.DomUtils;
import gen.eu.europa.ec.ddc.api.bcard.BusinessCard;
import gen.eu.europa.ec.ddc.api.bcard.BusinessEntityType;
import gen.eu.europa.ec.ddc.api.bcard.IdentifierType;
import gen.eu.europa.ec.ddc.api.peppol.ExtensionType;
import gen.eu.europa.ec.ddc.api.peppol.ServiceGroup;
import gen.eu.europa.ec.ddc.api.peppol.ServiceMetadataReferenceCollectionType;
import gen.eu.europa.ec.ddc.api.peppol.ServiceMetadataReferenceType;
import gen.eu.europa.ec.ddc.api.peppol.identifiers.transport.ParticipantIdentifierType;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.apache.commons.lang3.Strings;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static eu.europa.ec.smp.spi.enums.TransientDocumentPropertyType.RESOURCE_IDENTIFIER_SCHEME;
import static eu.europa.ec.smp.spi.enums.TransientDocumentPropertyType.RESOURCE_IDENTIFIER_VALUE;
import static eu.europa.ec.smp.spi.exceptions.ResourceException.ErrorCode.*;
import static org.apache.commons.lang3.StringUtils.trim;

/**
 * The ResourceHandlerSpi implementation for the Peppol SMP ServiceGroup document.
 *
 * @author Joze Rihtarsic
 * @since 5.2
 */
@Component
public class PeppolSMPResourceHandler extends AbstractPeppolSMPHandler {

    private static final Logger LOG = LoggerFactory.getLogger(PeppolSMPResourceHandler.class);
    public static final String BUSINESS_CARD = "BusinessCard";
    public static final String BUSINESS_CARD_NS = "http://www.peppol.eu/schema/pd/businesscard/20180621/";


    PeppolSMPServiceGroupReader reader = new PeppolSMPServiceGroupReader();

    final SmpDataServiceApi smpDataApi;
    final SmpIdentifierServiceApi smpIdentifierApi;

    final PeppolSMPServiceGroupReader serviceGroupReader;

    public PeppolSMPResourceHandler(SmpDataServiceApi smpDataApi,
                                    SmpIdentifierServiceApi smpIdentifierApi) {
        this.smpDataApi = smpDataApi;
        this.smpIdentifierApi = smpIdentifierApi;
        serviceGroupReader = new PeppolSMPServiceGroupReader();
    }

    public void generateResource(RequestData resourceData, ResponseData responseData, List<String> fields) throws ResourceException {
        ResourceIdentifier identifier = getResourceIdentifier(resourceData);

        ServiceGroup resource = new ServiceGroup();
        resource.setParticipantIdentifier(new ParticipantIdentifierType());
        resource.getParticipantIdentifier().setValue(RESOURCE_IDENTIFIER_VALUE.getPropertyPlaceholder());
        if (identifier.getScheme() != null) {
            resource.getParticipantIdentifier().setScheme(RESOURCE_IDENTIFIER_SCHEME.getPropertyPlaceholder());
        }
        resource.setServiceMetadataReferenceCollection(new ServiceMetadataReferenceCollectionType());
        ExtensionType extension = getExtensionBusinessCard(resource);
        resource.setExtension(extension);

        try {
            reader.serializeNative(resource, responseData.getOutputStream(), true);
        } catch (TechnicalException e) {
            throw new ResourceException(PARSE_ERROR, "Can not marshal extension for service group: [" + identifier + "]. Error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }
    }

    /**
     * Generate business card extension example with basic minimal data
     *
     * @param resource the service group resource
     * @return the extension element
     * @throws ResourceException
     */
    private static ExtensionType getExtensionBusinessCard(ServiceGroup resource) throws ResourceException {
        BusinessCard businessCard = new BusinessCard();
        IdentifierType idType = new IdentifierType();
        idType.setValue(resource.getParticipantIdentifier().getValue());
        idType.setScheme(resource.getParticipantIdentifier().getScheme());
        businessCard.setParticipantIdentifier(idType);
        BusinessEntityType entity = new BusinessEntityType();
        entity.setRegistrationDate(OffsetDateTime.now().minusYears(1));
        entity.setCountryCode("<<Country Code>>");
        entity.setAdditionalInformation("<<Additional Information>>");
        ExtensionType extension = new ExtensionType();
        businessCard.getBusinessEntities().add(entity);

        extension.setAny(jaxbToElement(businessCard));
        return extension;
    }

    @Override
    public void readResource(RequestData resourceData, ResponseData responseData) throws ResourceException {

        ResourceIdentifier identifier = getResourceIdentifier(resourceData);
        if (resourceData.getResourceInputStream() == null) {
            LOG.warn("Empty document input stream for service-group [{}]!", identifier);
            return;
        }
        String requestedResource = null;
        Map<String, String> docAttributes = resourceData.getDocumentAttributes();
        if (docAttributes != null && !docAttributes.isEmpty()) {
            LOG.debug("Document attributes for service-group [{}]: {}", identifier, docAttributes);
            requestedResource = docAttributes.get(TransientDocumentPropertyType.RESOURCE_URL_SEGMENT.getPropertyName());
        }

        ServiceGroup resource;
        try {
            resource = reader.parseNative(resourceData.getResourceInputStream());
        } catch (TechnicalException e) {
            throw new ResourceException(PARSE_ERROR, "Can not parse service group xml for identifier: [" + identifier + "]. Error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }
        if (resource == null) {
            throw new ResourceException(INVALID_RESOURCE, "Parsed service group is null for identifier: [" + identifier + "].");
        }

        if (Strings.CS.equals(requestedResource, PeppolSMPResource.RESOURCE_BUSINESS_CARD)
                && respondWithBusinessCard(responseData, resource, identifier)) {
            // return only business card extension
            return;
        }

        // get references
        resource.setServiceMetadataReferenceCollection(new ServiceMetadataReferenceCollectionType());
        List<ServiceMetadataReferenceType> referenceTypes = buildReferences(resourceData.getDomainCode(), identifier);
        resource.getServiceMetadataReferenceCollection().getServiceMetadataReferences().addAll(referenceTypes);

        try {
            reader.serializeNative(resource, responseData.getOutputStream(), false);
        } catch (TechnicalException e) {
            throw new ResourceException(PARSE_ERROR, "Can not marshal extension for service group: [" + identifier + "]. Error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }
    }

    private boolean respondWithBusinessCard(ResponseData responseData, ServiceGroup resource, ResourceIdentifier identifier) throws ResourceException {
        if (resource.getExtension() == null || resource.getExtension().getAny() == null) {
            LOG.warn("No extension found for service-group [{}]!", identifier);
            return false;
        }
        org.w3c.dom.Element anyElement = resource.getExtension().getAny();

        // write only business card extension
        if (!Strings.CS.equals(BUSINESS_CARD, anyElement.getLocalName())
                || !Strings.CS.equals(BUSINESS_CARD_NS, anyElement.getNamespaceURI())) {
            LOG.warn("Extension element is not a BusinessCard for service-group [{}]!", identifier);
            return false;
        }
        try {
            // serialize DOM Element to  output stream as XML
            writeElementToStream(anyElement, responseData.getOutputStream());
            return true;
        } catch (TransformerException e) {
            throw new ResourceException(PARSE_ERROR, "Can not marshal business card extension for service group: [" + identifier + "]. Error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }
    }

    public static void writeElementToStream(Element element, OutputStream outputStream) throws TransformerException {
        DomUtils.serialize(element, outputStream);
    }


    private List<ServiceMetadataReferenceType> buildReferences(final String domainCode, ResourceIdentifier resourceIdentifier) throws ResourceException {
        LOG.debug("Build build References identifier [{}].", resourceIdentifier);
        // get subresource identifiers for document type
        List<ResourceIdentifier> subResourceIdentifier = smpDataApi.getSubResourceIdentifiers(resourceIdentifier, PeppolSMPSubresource.RESOURCE_IDENTIFIER);

        List<ServiceMetadataReferenceType> referenceIds = new ArrayList<>();
        for (ResourceIdentifier subresId : subResourceIdentifier) {
            URI url = buildSMPURLForParticipantAndDocumentIdentifier(domainCode, resourceIdentifier, subresId);
            ServiceMetadataReferenceType referenceType = new ServiceMetadataReferenceType();
            referenceType.setHref(url.toString());
            referenceIds.add(referenceType);
        }
        return referenceIds;
    }

    /**
     * Convert jaxb object to w3c dom element
     *
     * @param jaxbObject the jaxb object
     * @return the w3c dom element
     * @throws ResourceException in case of error
     */
    public static org.w3c.dom.Element jaxbToElement(Object jaxbObject) throws ResourceException {
        JAXBContext context = null;
        try {
            context = JAXBContext.newInstance(jaxbObject.getClass());

            Marshaller marshaller = context.createMarshaller();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.newDocument();
            marshaller.marshal(jaxbObject, doc);
            return doc.getDocumentElement();
        } catch (JAXBException | ParserConfigurationException e) {
            throw new ResourceException(ResourceException.ErrorCode.INTERNAL_ERROR, "Can not convert jaxb object to element! " + ExceptionUtils.getMessage(e), e);
        }
    }

    public URI buildSMPURLForParticipantAndDocumentIdentifier(final String domainCode, ResourceIdentifier resourceIdentifier, ResourceIdentifier subresourceIdentifier) throws ResourceException {
        LOG.debug("Build SMP url for participant identifier: [{}] and document identifier [{}].", resourceIdentifier, subresourceIdentifier);

        String pathSegment = smpDataApi.getURIPathSegmentForSubresource(PeppolSMPSubresource.RESOURCE_IDENTIFIER);
        String baseUrl = smpDataApi.getResourceUrl();
        String formattedParticipant = smpIdentifierApi.formatResourceIdentifier(domainCode, resourceIdentifier);
        String formattedDocument = smpIdentifierApi.formatSubresourceIdentifier(domainCode, subresourceIdentifier);

        LOG.debug("Build SMP url from base path [{}], participant identifier: [{}] and document identifier [{}].",
                baseUrl, formattedParticipant, formattedDocument);
        try {
            return new URIBuilder(baseUrl)
                    .appendPathSegments(formattedParticipant)
                    .appendPathSegments(pathSegment)
                    .appendPathSegments(formattedDocument).build();
        } catch (URISyntaxException e) {
            throw new ResourceException(INTERNAL_ERROR, "Can not build SMP document URL path! " + ExceptionUtils.getMessage(e), e);
        }
    }


    @Override
    public void storeResource(RequestData resourceData, ResponseData responseData) throws ResourceException {
        LOG.info("Store resource for identifier [{}].", resourceData.getResourceIdentifier());
        InputStream inputStream = resourceData.getResourceInputStream();
        // reading resource multiple time make sure it can be rest
        if (!inputStream.markSupported()) {
            inputStream = new BufferedInputStream(inputStream);
        }

        inputStream.mark(Integer.MAX_VALUE - 2);
        ServiceGroup resource = validateAndParse(resourceData);

        // ServiceMetadataReferenceCollection must be empty because they are automatically generated
        if (resource.getServiceMetadataReferenceCollection() != null
                && !resource.getServiceMetadataReferenceCollection().getServiceMetadataReferences().isEmpty()) {
            throw new ResourceException(INVALID_PARAMETERS, "ServiceMetadataReferenceCollection must be empty!");
        }
        // back-compatibility issue: set participant to "lowercase" to match it as is saved in the database
        ParticipantIdentifierType orgResourceId = resource.getParticipantIdentifier();
        ResourceIdentifier nrmResourceId = resourceData.getResourceIdentifier();
        boolean isSame = Strings.CS.equals(orgResourceId.getValue(), nrmResourceId.getValue())
                && Strings.CS.equals(orgResourceId.getScheme(), nrmResourceId.getScheme());

        if (isSame) {
            try {
                inputStream.reset();
                StreamUtils.copy(inputStream, responseData.getOutputStream());
            } catch (IOException e) {
                throw new ResourceException(PARSE_ERROR, "Error occurred while copying the ServiceGroup", e);
            }
        } else {
            LOG.info("Update ServiceGroup identifier before saving. Old: [{}], New: [{}]", orgResourceId, nrmResourceId);
            if (!Strings.CI.equals(orgResourceId.getValue(), RESOURCE_IDENTIFIER_VALUE.getPropertyPlaceholder())) {
                orgResourceId.setValue(nrmResourceId.getValue());
            }
            if (!Strings.CI.equals(orgResourceId.getScheme(), RESOURCE_IDENTIFIER_SCHEME.getPropertyPlaceholder())) {
                orgResourceId.setScheme(nrmResourceId.getScheme());
            }
            try {
                // need to save resource because of the update on the resource identifier values
                reader.serializeNative(resource, responseData.getOutputStream(), true);
            } catch (TechnicalException e) {
                throw new ResourceException(PARSE_ERROR, "Error occurred while copying the ServiceGroup", e);
            }
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
            PeppolSmpSchemaValidator.validatePeppolSMPSchema(bytearray);
        } catch (IOException | XmlInvalidAgainstSchemaException e) {
            String ids = identifier != null ?
                    Stream.of(identifier).map(Object::toString).collect(Collectors.joining(",")) : "";
            throw new ResourceException(INVALID_RESOURCE, "Error occurred while validation Oasis SMP 1.0 ServiceGroup extension: [" + ids + "] with error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }
        // if service group
        ServiceGroup resource;
        try {
            resource = reader.parseNative(new ByteArrayInputStream(bytearray));
        } catch (TechnicalException e) {
            throw new ResourceException(INVALID_RESOURCE, "Error occurred while parsing Oasis SMP 1.0 ServiceGroup with error: " + ExceptionUtils.getRootCauseMessage(e), e);
        }
        final ParticipantIdentifierType participantId = resource.getParticipantIdentifier();
        String participantIdValue = participantId.getValue();
        String participantIdScheme = participantId.getScheme();
        if (Strings.CI.equals(trim(participantIdValue), RESOURCE_IDENTIFIER_VALUE.getPropertyPlaceholder())) {
            participantIdValue = identifier.getValue();
        }

        if (Strings.CI.equals(trim(participantIdScheme), RESOURCE_IDENTIFIER_SCHEME.getPropertyPlaceholder())) {
            participantIdScheme = identifier.getScheme();
        }

        ResourceIdentifier xmlResourceIdentifier = smpIdentifierApi.normalizeResourceIdentifier(resourceData.getDomainCode(),
                participantIdValue, participantIdScheme);


        if (!xmlResourceIdentifier.equals(identifier)) {
            // Business identifier must equal path
            throw new ResourceException(INVALID_PARAMETERS,
                    "Participant identifiers don't match between URL parameter [" + identifier + "] and XML body: ['" + xmlResourceIdentifier + "']");
        }
        return resource;
    }
}
