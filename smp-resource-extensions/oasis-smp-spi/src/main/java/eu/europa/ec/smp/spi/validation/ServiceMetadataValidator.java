/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.smp.spi.validation;


import eu.europa.ec.smp.spi.api.SmpIdentifierServiceApi;
import eu.europa.ec.smp.spi.api.model.ResourceIdentifier;
import eu.europa.ec.smp.spi.exceptions.ResourceException;
import gen.eu.europa.ec.ddc.api.smp10.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import static eu.europa.ec.smp.spi.exceptions.ResourceException.ErrorCode.INVALID_PARAMETERS;
import static eu.europa.ec.smp.spi.exceptions.ResourceException.ErrorCode.INVALID_RESOURCE;


/**
 * Simple Service metadata validator
 *
 * @author gutowpa
 * @since 3.0.0.
 */
@Component
public class ServiceMetadataValidator {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceMetadataValidator.class);

    final SmpIdentifierServiceApi smpIdentifierApi;

    public ServiceMetadataValidator(SmpIdentifierServiceApi smpIdentifierApi) {
        this.smpIdentifierApi = smpIdentifierApi;
    }

    public void validate(ResourceIdentifier participantIdentifierFromUrl,
                         ResourceIdentifier documentIdentifierFromUrl,
                         ServiceMetadata serviceMetadata
    ) throws ResourceException {
        LOG.debug("Validate service metadata for participant [{}], document [{}]", participantIdentifierFromUrl, documentIdentifierFromUrl);

        ServiceInformationType serviceInformation = serviceMetadata.getServiceInformation();

        if (serviceInformation == null && serviceMetadata.getRedirect() != null) {
            LOG.debug("Redirect serviceMetadata, skip document/participant identifier validation");
            return;
        }

        if (serviceInformation == null) {
            throw new ResourceException(INVALID_RESOURCE, "Missing element. Add ServiceInformation or Redirect", null);
        }

        validateServiceInformation(participantIdentifierFromUrl, documentIdentifierFromUrl, serviceInformation);

    }

    /**
     * Validate participant identifier in the serviceMetadata
     *
     * @param participantIdentifierFromUrl
     * @param documentIdentifierFromUrl
     * @param serviceInformation
     * @return
     */
    public ServiceInformationType validateServiceInformation(ResourceIdentifier participantIdentifierFromUrl,
                                                             ResourceIdentifier documentIdentifierFromUrl,
                                                             final ServiceInformationType serviceInformation) throws ResourceException {
        LOG.debug("Validate service metadata information for participant [{}], document [{}]", participantIdentifierFromUrl, documentIdentifierFromUrl);

        final ParticipantIdentifierType participantId = serviceInformation.getParticipantIdentifier();
        final DocumentIdentifier documentId = serviceInformation.getDocumentIdentifier();
        ResourceIdentifier xmlResourceIdentifier = smpIdentifierApi.normalizeResourceIdentifier(participantId.getValue(), participantId.getScheme());
        ResourceIdentifier xmlSubresourceIdentifier = smpIdentifierApi.normalizeSubresourceIdentifier(documentId.getValue(), documentId.getScheme());
        if (!xmlResourceIdentifier.equals(participantIdentifierFromUrl)) {
            // Business identifier must equal path
            throw new ResourceException(INVALID_PARAMETERS, "Participant identifiers don't match between URL parameter [" + participantIdentifierFromUrl + "] and XML body: [" + xmlResourceIdentifier + "]");
        }

        if (!xmlSubresourceIdentifier.equals(documentIdentifierFromUrl)) {
            // Business identifier must equal path
            throw new ResourceException(INVALID_PARAMETERS, "Document identifiers don't match between URL parameter [" + documentIdentifierFromUrl + "] and XML body: [" + xmlSubresourceIdentifier + "]");
        }
        validateProcesses(serviceInformation);
        return serviceInformation;
    }

    private void validateProcesses(ServiceInformationType serviceInformation) throws ResourceException {
        LOG.debug("Validate service metadata processes!");
        ProcessListType processList = serviceInformation.getProcessList();
        if (processList == null ||  processList.getProcesses().isEmpty()) {
            LOG.debug("No processes found!");
            return;
        }

        for (ProcessType process : processList.getProcesses()) {
            validateProcess(process);
        }
    }

    private void validateProcess(ProcessType process) throws ResourceException {
        LOG.debug("Validate process found!");
        ServiceEndpointList serviceEndpoints = process.getServiceEndpointList();
        if (serviceEndpoints == null) {
            LOG.warn("No endpoint for the process!");
            return;
        }

        Set<String> transportProfiles = new HashSet<>();
        for (EndpointType endpoint : serviceEndpoints.getEndpoints()) {
            if (!transportProfiles.add(endpoint.getTransportProfile())) {
                throw new ResourceException(INVALID_PARAMETERS, "Duplicated Transport Profile: " + endpoint.getTransportProfile());
            }

            OffsetDateTime activationDate = endpoint.getServiceActivationDate();
            OffsetDateTime expirationDate = endpoint.getServiceExpirationDate();
            LOG.debug("Validate validity for the process with activation date [{}] and expiration date [{}]!", activationDate,expirationDate );

            if (activationDate != null && expirationDate != null && activationDate.isAfter(expirationDate)) {
                throw new ResourceException(INVALID_PARAMETERS, "[OUT_OF_RANGE] Expiration date is before Activation date");
            }

            if (expirationDate != null && expirationDate.isBefore(OffsetDateTime.now())) {
                throw new ResourceException(INVALID_PARAMETERS, "[OUT_OF_RANGE] Expiration date has passed");
            }
        }
    }
}
