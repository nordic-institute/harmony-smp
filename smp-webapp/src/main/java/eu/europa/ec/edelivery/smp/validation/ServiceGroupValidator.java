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

import eu.europa.ec.edelivery.smp.conversion.CaseSensitivityNormalizer;
import eu.europa.ec.edelivery.smp.error.exceptions.BadRequestException;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.smp.api.Identifiers;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceMetadataReferenceCollectionType;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorBusinessCode.WRONG_FIELD;
import static org.springframework.util.CollectionUtils.isEmpty;


/**
 * Created by gutowpa on 02/08/2017.
 */
@Component
public class ServiceGroupValidator {


    protected final ConfigurationService configurationService;
    protected final CaseSensitivityNormalizer caseSensitivityNormalizer;

    public ServiceGroupValidator(ConfigurationService configurationService,
                                 CaseSensitivityNormalizer caseSensitivityNormalizer) {
        this.configurationService = configurationService;
        this.caseSensitivityNormalizer = caseSensitivityNormalizer;
    }

    public void validate(String serviceGroupId, ServiceGroup serviceGroup) {

        final ParticipantIdentifierType participantId = caseSensitivityNormalizer.normalize(
                Identifiers.asParticipantId(serviceGroupId, configurationService.getParticipantSchemeMandatory()));
        final ParticipantIdentifierType serviceGroupParticipantId =  caseSensitivityNormalizer.normalize(
                serviceGroup.getParticipantIdentifier());

        if (!participantId.equals(serviceGroupParticipantId)) {
            // Business identifier must equal path
            throw new BadRequestException(WRONG_FIELD, "Service Group Ids don't match between URL parameter and XML body");
        }

        String scheme = serviceGroupParticipantId.getScheme();
        Pattern schemaPattern = configurationService.getParticipantIdentifierSchemeRexExp();

        if (!schemaPattern.matcher(scheme==null?"":scheme).matches()) {
            throw new BadRequestException(WRONG_FIELD, "Service Group scheme does not match allowed pattern: " + schemaPattern.pattern());
        }

        ServiceMetadataReferenceCollectionType references = serviceGroup.getServiceMetadataReferenceCollection();
        if (references != null && !isEmpty(references.getServiceMetadataReferences())) {
            throw new BadRequestException(WRONG_FIELD, "ServiceMetadataReferenceCollection must be empty");
        }
    }

}
