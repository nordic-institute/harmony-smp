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

package eu.europa.ec.edelivery.smp.validation;

import eu.europa.ec.edelivery.smp.conversion.IdentifierService;
import eu.europa.ec.edelivery.smp.conversion.ServiceMetadataConverter;
import eu.europa.ec.edelivery.smp.error.exceptions.BadRequestException;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.smp.api.exceptions.XmlInvalidAgainstSchemaException;
import eu.europa.ec.smp.api.validators.BdxSmpOasisValidator;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorBusinessCode.*;

/**
 * Simple Service metadata validator
 *
 * @author gutowpa
 * @since 3.0.0.
 */
@Component
public class ServiceMetadataValidator {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceMetadataValidator.class);

    protected final ConfigurationService configurationService;
    protected final IdentifierService identifierService;

    public ServiceMetadataValidator(ConfigurationService configurationService,
                                    IdentifierService caseSensitivityNormalizer) {
        this.configurationService = configurationService;
        this.identifierService = caseSensitivityNormalizer;
    }

    public void validate(String participantIdentifierFromUrl,
                         String documentIdentifierFromUrl,
                         byte[] serviceMetadataBody
    ) throws XmlInvalidAgainstSchemaException {

        // validate XML serviceMetadata xml against schema
        BdxSmpOasisValidator.validateXSD(serviceMetadataBody);

        // parse serviceMetadataBody
        ServiceMetadata serviceMetadata = ServiceMetadataConverter.unmarshal(serviceMetadataBody);
        ServiceInformationType serviceInformation = serviceMetadata.getServiceInformation();

        if (serviceInformation == null && serviceMetadata.getRedirect() != null) {
            LOG.debug("Redirect serviceMetadata, skip document/participant identifier validation");
            return;
        }

        if (serviceInformation == null) {
            throw new BadRequestException(WRONG_FIELD, "Missing service information or redirect");
        }

        ParticipantIdentifierType serviceGroupId = identifierService.normalizeParticipantIdentifier(participantIdentifierFromUrl);
        DocumentIdentifier documentId = identifierService.normalizeDocumentIdentifier(documentIdentifierFromUrl);
        validateServiceInformation(serviceGroupId, documentId, serviceInformation);

    }

    /**
     * Validate participant identifier in the serviceMetadata
     *
     * @param urlParticipantId
     * @param urlDocumentId
     * @param serviceInformation
     * @return
     */
    public ServiceInformationType validateServiceInformation(final ParticipantIdentifierType urlParticipantId,
                                                             final DocumentIdentifier urlDocumentId,
                                                             final ServiceInformationType serviceInformation) {

        final ParticipantIdentifierType xmlParticipantId = identifierService.normalizeParticipant(
                serviceInformation.getParticipantIdentifier());
        final DocumentIdentifier xmlDocumentId = identifierService.normalizeDocument
                (serviceInformation.getDocumentIdentifier());

        if (!urlParticipantId.equals(xmlParticipantId)) {
            String errorMessage = "Save service metadata was called with bad Participant ID parameters. Message body param: ["
                    + identifierToString(xmlParticipantId) + "] URL param: [" + identifierToString(urlParticipantId) + "]";
            throw new BadRequestException(FORMAT_ERROR, errorMessage);
        }

        if (!urlDocumentId.equals(xmlDocumentId)) {
            String errorMessage = "Save service metadata was called with bad Document ID parameters. Message body param: ["
                    + identifierToString(xmlDocumentId) + "] URL param: [" + identifierToString(urlDocumentId) + "]";
            throw new BadRequestException(FORMAT_ERROR, errorMessage);
        }
        validateProcesses(serviceInformation);
        return serviceInformation;
    }

    private void validateProcesses(ServiceInformationType serviceInformation) {
        ProcessListType processList = serviceInformation.getProcessList();
        if (processList == null) {
            return;
        }

        for (ProcessType process : processList.getProcesses()) {
            validateProcess(process);
        }
    }

    private String identifierToString(ParticipantIdentifierType identifierType) {
        return "ParticipantIdentifier: " + (identifierType == null ? "NULL" : identifierToString(identifierType.getScheme(), identifierType.getValue()));
    }

    private String identifierToString(DocumentIdentifier identifierType) {
        return "DocumentIdentifier: " + (identifierType == null ? "NULL" : identifierToString(identifierType.getScheme(), identifierType.getValue()));
    }

    private String identifierToString(String scheme, String value) {
        return "scheme: '" + scheme + "', value: '" + value + "'";
    }

    private void validateProcess(ProcessType process) {
        ServiceEndpointList serviceEndpoints = process.getServiceEndpointList();
        if (serviceEndpoints == null) {
            return;
        }

        Set<String> transportProfiles = new HashSet<>();
        for (EndpointType endpoint : serviceEndpoints.getEndpoints()) {
            if (!transportProfiles.add(endpoint.getTransportProfile())) {
                throw new BadRequestException(WRONG_FIELD, "Duplicated Transport Profile: " + endpoint.getTransportProfile());
            }

            Date activationDate = endpoint.getServiceActivationDate();
            Date expirationDate = endpoint.getServiceExpirationDate();

            if (activationDate != null && expirationDate != null && activationDate.after(expirationDate)) {
                throw new BadRequestException(OUT_OF_RANGE, "Expiration date is before Activation date");
            }

            if (expirationDate != null && expirationDate.before(new Date())) {
                throw new BadRequestException(OUT_OF_RANGE, "Expiration date has passed");
            }
        }
    }
}
