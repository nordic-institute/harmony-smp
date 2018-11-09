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

import org.apache.commons.lang3.StringUtils;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ListIterator;

import static eu.europa.ec.smp.api.Identifiers.asParticipantId;
import static eu.europa.ec.smp.api.Identifiers.asString;

/**
 * Created by gutowpa on 23/02/2017.
 */
@Component
public class CaseSensitivityNormalizer {

    private List<String> caseSensitiveParticipantSchemes;
    private List<String> caseSensitiveDocumentSchemes;

    @Value("#{'${identifiersBehaviour.caseSensitive.DocumentIdentifierSchemes}'.split('\\|')}")
    public void setCaseSensitiveDocumentSchemes(List<String> caseSensitiveDocumentSchemes) {
        this.caseSensitiveDocumentSchemes = caseSensitiveDocumentSchemes;
        toLowerCaseStringList(this.caseSensitiveDocumentSchemes);
    }

    @Value("#{'${identifiersBehaviour.caseSensitive.ParticipantIdentifierSchemes}'.split('\\|')}")
    public void setCaseSensitiveParticipantSchemes(List<String> caseSensitiveParticipantSchemes) {
        this.caseSensitiveParticipantSchemes = caseSensitiveParticipantSchemes;
        toLowerCaseStringList(this.caseSensitiveParticipantSchemes);
    }

    public ParticipantIdentifierType normalizeParticipantIdentifier(String scheme, String value) {
        if (!caseSensitiveParticipantSchemes.contains(StringUtils.lowerCase(scheme))) {
            scheme = StringUtils.lowerCase(scheme);
            value = StringUtils.lowerCase(value);
        }
        return new ParticipantIdentifierType(value, scheme);
    }

    public ParticipantIdentifierType normalize(final ParticipantIdentifierType participantIdentifier) {
        String scheme = participantIdentifier.getScheme();
        String value = participantIdentifier.getValue();
        return normalizeParticipantIdentifier(scheme, value);
    }

    public DocumentIdentifier normalize(final DocumentIdentifier documentIdentifier) {
        String scheme = documentIdentifier.getScheme();
        String value = documentIdentifier.getValue();
        return normalizeDocumentIdentifier(scheme, value);
    }

    public DocumentIdentifier normalizeDocumentIdentifier( String scheme, String value) {
        if (!caseSensitiveDocumentSchemes.contains(StringUtils.lowerCase(scheme) )) {
            scheme = StringUtils.lowerCase(scheme);
            value = StringUtils.lowerCase(value);
        }
        return new DocumentIdentifier(value, scheme);
    }

    public String normalizeParticipantId(String participantId) {
        return asString(normalizeParticipant(participantId));
    }

    public ParticipantIdentifierType normalizeParticipant(String participantId) {
        return normalize(asParticipantId(participantId));
    }

    private static void toLowerCaseStringList(List<String> strings) {
        ListIterator<String> iterator = strings.listIterator();
        while (iterator.hasNext()) {
            iterator.set(iterator.next().toLowerCase().trim());
        }
    }
}
