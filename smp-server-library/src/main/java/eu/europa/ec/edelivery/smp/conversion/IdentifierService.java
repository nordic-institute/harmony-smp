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
import eu.europa.ec.smp.api.identifiers.DocumentIdentifierFormatter;
import eu.europa.ec.smp.api.identifiers.ParticipantIdentifierFormatter;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;


/**
 * Class provides tools to parse, format and normalize Document and Participant identifiers.
 *
 * @author gutowpa
 * @since 3.0.0
 */
@Component
public class IdentifierService {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(IdentifierService.class);

    ParticipantIdentifierFormatter participantIdentifierFormatter = new ParticipantIdentifierFormatter();
    DocumentIdentifierFormatter documentIdentifierFormatter = new DocumentIdentifierFormatter();

    ConfigurationService configurationService;

    public IdentifierService(ConfigurationService configurationService) {
        this.configurationService = configurationService;

        configureParticipantIdentifierFormatter(configurationService.getCaseSensitiveParticipantScheme(),
                configurationService.getParticipantSchemeMandatory(),
                configurationService.getParticipantIdentifierSchemeRexExp());
        configureDocumentIdentifierFormatter(configurationService.getCaseSensitiveDocumentScheme());
    }

    /**
     * Update ParticipantIdentifierFormatter for non null values. Null values are ignored
     *
     * @param caseInsensitiveSchemas
     * @param mandatoryScheme
     */
    public void configureParticipantIdentifierFormatter(List<String> caseInsensitiveSchemas, Boolean mandatoryScheme, Pattern allowedSchemeRegExp) {
        if (caseInsensitiveSchemas != null) {
            participantIdentifierFormatter.setCaseSensitiveSchemas(caseInsensitiveSchemas);
        } else {
            LOG.debug("Skip configure ParticipantIdentifierFormatter.caseInsensitiveSchemas for null value");
        }

        if (mandatoryScheme != null) {
            participantIdentifierFormatter.setSchemeMandatory(mandatoryScheme.booleanValue());
        } else {
            LOG.debug("Skip configure ParticipantIdentifierFormatter.mandatoryScheme for null value");
        }

        if (allowedSchemeRegExp != null) {
            participantIdentifierFormatter.setSchemeValidationPattern(allowedSchemeRegExp);
        } else {
            LOG.debug("Skip configure ParticipantIdentifierFormatter.allowedSchemeRegExp for null value");
        }
    }

    public void configureDocumentIdentifierFormatter(List<String> caseInsensitiveSchemas) {
        if (caseInsensitiveSchemas != null) {
            documentIdentifierFormatter.setCaseSensitiveSchemas(caseInsensitiveSchemas);
        } else {
            LOG.debug("Skip configure DocumentIdentifierFormatter.caseInsensitiveSchemas for null value");
        }
    }

    public DocumentIdentifier normalizeDocument(final DocumentIdentifier documentIdentifier) {
        return documentIdentifierFormatter.normalize(documentIdentifier);
    }

    public DocumentIdentifier normalizeDocument(final String scheme, final String identifier) {
        return documentIdentifierFormatter.normalize(scheme, identifier);
    }

    public DocumentIdentifier normalizeDocumentIdentifier(String value) {
        return documentIdentifierFormatter.normalizeIdentifier(value);
    }

    public ParticipantIdentifierType normalizeParticipant(final String scheme, final String identifier) {
        return participantIdentifierFormatter.normalize(scheme, identifier);
    }

    public ParticipantIdentifierType normalizeParticipant(final ParticipantIdentifierType participantIdentifier) {
        return participantIdentifierFormatter.normalize(participantIdentifier);
    }

    public ParticipantIdentifierType normalizeParticipantIdentifier(final String participantId) {
        return participantIdentifierFormatter.normalizeIdentifier(participantId);
    }

    public String formatParticipant(final ParticipantIdentifierType participantIdentifier) {
        return participantIdentifierFormatter.format(participantIdentifier);
    }

    public String urlEncodedFormatParticipant(final ParticipantIdentifierType participantIdentifier) {
        return participantIdentifierFormatter.urlEncodedFormat(participantIdentifier);
    }

    public String formatParticipant(final String scheme, final String identifier) {
        return participantIdentifierFormatter.format(scheme, identifier);
    }

    public String formatDocument(final DocumentIdentifier documentIdentifier) {
        return documentIdentifierFormatter.format(documentIdentifier);
    }

    public String urlEncodedFormatDocument(final DocumentIdentifier documentIdentifier) {
        return documentIdentifierFormatter.urlEncodedFormat(documentIdentifier);
    }

    public String formatDocument(final String scheme, final String identifier) {
        return documentIdentifierFormatter.format(scheme, identifier);
    }
}
