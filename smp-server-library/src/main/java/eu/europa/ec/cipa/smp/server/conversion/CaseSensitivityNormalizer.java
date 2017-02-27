package eu.europa.ec.cipa.smp.server.conversion;

import eu.europa.ec.cipa.smp.server.util.ConfigFile;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
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
    ConfigFile config;

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

    public ParticipantIdentifierType normalize(ParticipantIdentifierType participantId) {
        String scheme = participantId.getScheme();
        String value = participantId.getValue();
        if (!caseSensitiveParticipantSchemes.contains(scheme.toLowerCase())) {
            scheme = scheme.toLowerCase();
            value = value.toLowerCase();
        }
        return new ParticipantIdentifierType(value, scheme);
    }

    public DocumentIdentifier normalize(DocumentIdentifier documentIdentifier) {
        String scheme = documentIdentifier.getScheme();
        String value = documentIdentifier.getValue();
        if (!caseSensitiveDocumentSchemes.contains(scheme.toLowerCase())) {
            scheme = scheme.toLowerCase();
            value = value.toLowerCase();
        }
        return new DocumentIdentifier(value, scheme);
    }
}
