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

package eu.europa.ec.cipa.smp.server.conversion;

import eu.europa.ec.cipa.smp.server.util.ConfigFile;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gutowpa on 23/02/2017.
 */
@Component
public class CaseSensitivityNormalizer {

    private static final String CASE_SENSITIVE_PARTICIPANT_SCHEMES = "identifiersBehaviour.caseSensitive.ParticipantIdentifierSchemes";
    private static final String CASE_SENSITIVE_DOCUMENT_SCHEMES = "identifiersBehaviour.caseSensitive.DocumentIdentifierSchemes";

    private List<String> caseSensitiveParticipantSchemes;
    private List<String> caseSensitiveDocumentSchemes;

    @Autowired
    protected ConfigFile config;

    @PostConstruct
    public void init() {
        caseSensitiveParticipantSchemes = new ArrayList<>();
        caseSensitiveDocumentSchemes = new ArrayList<>();
        for (String scheme : config.getStringList(CASE_SENSITIVE_PARTICIPANT_SCHEMES)) {
            caseSensitiveParticipantSchemes.add(scheme.toLowerCase().trim());
        }
        for (String scheme : config.getStringList(CASE_SENSITIVE_DOCUMENT_SCHEMES)) {
            caseSensitiveDocumentSchemes.add(scheme.toLowerCase().trim());
        }
    }

    public ParticipantIdentifierType normalize(final ParticipantIdentifierType participantIdentifier) {
        String scheme = participantIdentifier.getScheme();
        String value = participantIdentifier.getValue();
        if (!caseSensitiveParticipantSchemes.contains(scheme.toLowerCase())) {
            scheme = scheme.toLowerCase();
            value = value.toLowerCase();
        }
        return new ParticipantIdentifierType(value, scheme);
    }

    public DocumentIdentifier normalize(final DocumentIdentifier documentIdentifier) {
        String scheme = documentIdentifier.getScheme();
        String value = documentIdentifier.getValue();
        if (!caseSensitiveDocumentSchemes.contains(scheme.toLowerCase())) {
            scheme = scheme.toLowerCase();
            value = value.toLowerCase();
        }
        return new DocumentIdentifier(value, scheme);
    }

    public void normalizeParticipantId(ServiceGroup servicGroup){

    }
}
