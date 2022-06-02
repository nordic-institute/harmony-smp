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

package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import org.apache.commons.lang3.StringUtils;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.springframework.stereotype.Component;

import java.util.List;

import static eu.europa.ec.smp.api.Identifiers.asParticipantId;
import static eu.europa.ec.smp.api.Identifiers.asString;

/**
 * Created by gutowpa on 23/02/2017.
 */
@Component
public class CaseSensitivityNormalizer {

    protected static ConfigurationService configurationService;

    public CaseSensitivityNormalizer(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public ParticipantIdentifierType normalizeParticipantIdentifier(String scheme, String value) {
        List<String> caseSensitiveParticipantSchemes = configurationService.getCaseSensitiveParticipantScheme();
        if (scheme==null || caseSensitiveParticipantSchemes == null || !caseSensitiveParticipantSchemes.stream().anyMatch(scheme::equalsIgnoreCase)) {
            scheme = StringUtils.lowerCase(scheme);
            value = StringUtils.lowerCase(value);
        }
        return new ParticipantIdentifierType(value, scheme);
    }

    public ParticipantIdentifierType normalize(final ParticipantIdentifierType participantIdentifier, boolean schemeMandatory) {
        ParticipantIdentifierType prtId = asParticipantId(asString(participantIdentifier),
                schemeMandatory);
        String scheme = prtId.getScheme();
        String value = prtId.getValue();
        return normalizeParticipantIdentifier(scheme, value);
    }

    public ParticipantIdentifierType normalize(final ParticipantIdentifierType participantIdentifier) {
        return normalize(participantIdentifier,  configurationService.getParticipantSchemeMandatory());
    }

    public DocumentIdentifier normalize(final DocumentIdentifier documentIdentifier) {
        String scheme = documentIdentifier.getScheme();
        String value = documentIdentifier.getValue();
        return normalizeDocumentIdentifier(scheme, value);
    }

    public DocumentIdentifier normalizeDocumentIdentifier(String scheme, String value) {
        List<String> caseSensitiveDocumentSchemes = configurationService.getCaseSensitiveDocumentScheme();
        if (scheme==null || caseSensitiveDocumentSchemes == null || !caseSensitiveDocumentSchemes.stream().anyMatch(scheme::equalsIgnoreCase)) {
            scheme = StringUtils.lowerCase(scheme);
            value = StringUtils.lowerCase(value);
        }
        return new DocumentIdentifier(value, scheme);
    }

    public String normalizeParticipantId(String participantId) {
        return asString(normalizeParticipant(participantId));
    }

    public ParticipantIdentifierType normalizeParticipant(String participantId) {
        return normalize(asParticipantId(participantId, configurationService.getParticipantSchemeMandatory()));
    }
}
