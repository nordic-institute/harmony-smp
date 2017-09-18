/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.validation;

import eu.europa.ec.cipa.smp.server.conversion.ServiceMetadataConverter;
import eu.europa.ec.edelivery.smp.error.exceptions.BadRequestException;
import eu.europa.ec.smp.api.exceptions.XmlInvalidAgainstSchemaException;
import eu.europa.ec.smp.api.validators.BdxSmpOasisValidator;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static eu.europa.ec.edelivery.smp.error.ErrorBusinessCode.OUT_OF_RANGE;
import static eu.europa.ec.edelivery.smp.error.ErrorBusinessCode.WRONG_FIELD;
import static eu.europa.ec.smp.api.Identifiers.*;

/**
 * Created by gutowpa on 11/09/2017.
 */
@Component
public class ServiceMetadataValidator {

    private static final Logger log = LoggerFactory.getLogger(ServiceMetadataValidator.class);

    public void validate(String serviceGroupIdStr,
                         String serviceMetadataIdStr,
                         String serviceMetadataBody
    ) throws XmlInvalidAgainstSchemaException {

        BdxSmpOasisValidator.validateXSD(serviceMetadataBody);

        ParticipantIdentifierType serviceGroupId = asParticipantId(serviceGroupIdStr);
        DocumentIdentifier serviceMetadataId = asDocumentId(serviceMetadataIdStr);
        ServiceMetadata serviceMetadata = ServiceMetadataConverter.unmarshal(serviceMetadataBody);

        ServiceInformationType serviceInformation = serviceMetadata.getServiceInformation();

        if (serviceInformation == null) {
            return;
        }

        if (!serviceGroupId.equals(serviceInformation.getParticipantIdentifier())) {
            String errorMessage = String.format("Save service metadata was called with bad Participant ID parameters. Message body param: %s URL param: %s",
                    asString(serviceInformation.getParticipantIdentifier()),
                    asString(serviceGroupId));
            log.info(errorMessage);
            throw new BadRequestException(WRONG_FIELD, errorMessage);
        }

        if (!serviceMetadataId.equals(serviceInformation.getDocumentIdentifier())) {
            String errorMessage = String.format("Save service metadata was called with bad Document ID parameters. Message body param: %s URL param: %s",
                    asString(serviceInformation.getDocumentIdentifier()),
                    asString(serviceMetadataId));
            log.info(errorMessage);
            throw new BadRequestException(WRONG_FIELD, errorMessage);
        }

        validateServiceInformation(serviceInformation);
    }

    private void validateServiceInformation(ServiceInformationType serviceInformation) {
        ProcessListType processList = serviceInformation.getProcessList();
        if (processList == null) {
            return;
        }

        for (ProcessType process : processList.getProcesses()) {
            validateProcess(process);
        }
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
