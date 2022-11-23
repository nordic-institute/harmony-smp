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

import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import org.apache.commons.lang3.StringUtils;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static eu.europa.ec.smp.api.Identifiers.asParticipantId;
import static eu.europa.ec.smp.api.Identifiers.asString;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * Created by gutowpa on 23/02/2017.
 */
@Component
public class CaseSensitivityNormalizer {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(CaseSensitivityNormalizer.class);

    protected static ConfigurationService configurationService;

    public CaseSensitivityNormalizer(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public ParticipantIdentifierType normalizeParticipantIdentifier(final String scheme, final String partyId) {
        List<String> caseSensitiveParticipantSchemes = configurationService.getCaseSensitiveParticipantScheme();
        String pScheme = trim(scheme);
        String pPartyId = trim(partyId);

        if (isEmpty(pScheme) && !isEmpty(pPartyId)) {
            Pattern pattern = configurationService.getParticipantIdentifierSplitRexExp();
            Matcher matcher = pattern.matcher(pPartyId);
            if (matcher.matches()) {
                pScheme = matcher.group("scheme");
                pPartyId = matcher.group("identifier");
                LOG.debug("Party identifier [{}] match the regular expression to split to scheme [{}]] and identifier [{}]]",
                        partyId, pScheme, pPartyId);
            } else {
                LOG.info("Party identifier [{}] does not match urn regular expression [{}]", partyId, pattern.pattern());
            }
        }

        // set to lower case
        if (pScheme == null
                || caseSensitiveParticipantSchemes == null
                || !caseSensitiveParticipantSchemes.stream().anyMatch(pScheme::equalsIgnoreCase)) {
            pScheme = lowerCase(pScheme);
            pPartyId = lowerCase(pPartyId);
        }
        return new ParticipantIdentifierType(pPartyId, pScheme);
    }

    public ParticipantIdentifierType normalize(final ParticipantIdentifierType participantIdentifier, boolean schemeMandatory) {
        ParticipantIdentifierType prtId = asParticipantId(asString(participantIdentifier),
                schemeMandatory);
        String scheme = prtId.getScheme();
        String value = prtId.getValue();
        return normalizeParticipantIdentifier(scheme, value);
    }

    public ParticipantIdentifierType normalize(final ParticipantIdentifierType participantIdentifier) {
        return normalize(participantIdentifier, configurationService.getParticipantSchemeMandatory());
    }

    public DocumentIdentifier normalize(final DocumentIdentifier documentIdentifier) {
        String scheme = documentIdentifier.getScheme();
        String value = documentIdentifier.getValue();
        return normalizeDocumentIdentifier(scheme, value);
    }

    public DocumentIdentifier normalizeDocumentIdentifier(String scheme, String value) {
        List<String> caseSensitiveDocumentSchemes = configurationService.getCaseSensitiveDocumentScheme();
        if (scheme == null || caseSensitiveDocumentSchemes == null || !caseSensitiveDocumentSchemes.stream().anyMatch(scheme::equalsIgnoreCase)) {
            scheme = lowerCase(scheme);
            value = lowerCase(value);
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
