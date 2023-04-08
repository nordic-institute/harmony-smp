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
import gen.eu.europa.ec.ddc.api.smp20.ServiceMetadata;
import gen.eu.europa.ec.ddc.api.smp20.aggregate.Endpoint;
import gen.eu.europa.ec.ddc.api.smp20.aggregate.ProcessMetadata;
import gen.eu.europa.ec.ddc.api.smp20.basic.ParticipantID;
import gen.eu.europa.ec.ddc.api.smp20.basic.ServiceID;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static eu.europa.ec.smp.spi.exceptions.ResourceException.ErrorCode.INVALID_PARAMETERS;


/**
 * Simple Service metadata validator
 */
@Component
public class ServiceMetadata20Validator {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceMetadata20Validator.class);

    final SmpIdentifierServiceApi smpIdentifierApi;

    public ServiceMetadata20Validator(SmpIdentifierServiceApi smpIdentifierApi) {
        this.smpIdentifierApi = smpIdentifierApi;
    }

    public void validate(ResourceIdentifier participantIdentifierFromUrl,
                         ResourceIdentifier documentIdentifierFromUrl,
                         ServiceMetadata serviceMetadata
    ) throws ResourceException {
        LOG.debug("Validate service metadata for participant [{}], document [{}]", participantIdentifierFromUrl, documentIdentifierFromUrl);

        final ParticipantID participantId = serviceMetadata.getParticipantID();
        final ServiceID documentId = serviceMetadata.getServiceID();
        ResourceIdentifier xmlResourceIdentifier = smpIdentifierApi.normalizeResourceIdentifier(participantId.getValue(), participantId.getSchemeID());
        ResourceIdentifier xmlSubresourceIdentifier = smpIdentifierApi.normalizeSubresourceIdentifier(documentId.getValue(), documentId.getSchemeID());
        if (!xmlResourceIdentifier.equals(participantIdentifierFromUrl)) {
            // Business identifier must equal path
            throw new ResourceException(INVALID_PARAMETERS, "Participant identifiers don't match between URL parameter [" + participantIdentifierFromUrl + "] and XML body: [" + xmlResourceIdentifier + "]");
        }

        if (!xmlSubresourceIdentifier.equals(documentIdentifierFromUrl)) {
            // Business identifier must equal path
            throw new ResourceException(INVALID_PARAMETERS, "Document identifiers don't match between URL parameter [" + documentIdentifierFromUrl + "] and XML body: [" + xmlSubresourceIdentifier + "]");
        }

        List<ProcessMetadata> processMetadata = serviceMetadata.getProcessMetadatas();
        validateProcesses(processMetadata);
/*
        if (serviceInformation == null && serviceMetadata.getRedirect() != null) {
            LOG.debug("Redirect serviceMetadata, skip document/participant identifier validation");
            return;
        }*/


    }


    private void validateProcesses(List<ProcessMetadata> processMetadata) throws ResourceException {
        LOG.debug("Validate service metadata processes!");

        if (processMetadata.isEmpty()) {
            LOG.debug("No processes found!");
            return;
        }

        for (ProcessMetadata process : processMetadata) {
            validateProcess(process);
        }
    }

    private void validateProcess(ProcessMetadata process) throws ResourceException {
        LOG.debug("Validate process found!");
        List<Endpoint> serviceEndpoints = process.getEndpoints();
        if (serviceEndpoints == null) {
            LOG.warn("No endpoint for the process!");
            return;
        }

        Set<String> transportProfiles = new HashSet<>();
        for (Endpoint endpoint : serviceEndpoints) {
            if (endpoint.getTransportProfileID() == null || StringUtils.isBlank(endpoint.getTransportProfileID().getValue())) {
                throw new ResourceException(INVALID_PARAMETERS, "Empty Transport Profile!");
            }
            String profileId = endpoint.getTransportProfileID().getValue();

            if (!transportProfiles.add(profileId)) {
                throw new ResourceException(INVALID_PARAMETERS, "Duplicated Transport Profile: " + profileId);
            }

            OffsetDateTime activationDate = endpoint.getActivationDate() != null ? endpoint.getActivationDate().getValue() : null;
            OffsetDateTime expirationDate = endpoint.getExpirationDate() != null ? endpoint.getExpirationDate().getValue() : null;
            LOG.debug("Validate validity for the process with activation date [{}] and expiration date [{}]!", activationDate, expirationDate);

            if (activationDate != null && expirationDate != null && activationDate.isAfter(expirationDate)) {
                throw new ResourceException(INVALID_PARAMETERS, "[OUT_OF_RANGE] Expiration date is before Activation date");
            }

            if (expirationDate != null && expirationDate.isBefore(OffsetDateTime.now())) {
                throw new ResourceException(INVALID_PARAMETERS, "[OUT_OF_RANGE] Expiration date has passed");
            }
        }
    }
}
