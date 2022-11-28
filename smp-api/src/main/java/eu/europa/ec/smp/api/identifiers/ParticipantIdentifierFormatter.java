package eu.europa.ec.smp.api.identifiers;

import eu.europa.ec.smp.api.identifiers.types.EBCorePartyIdFormatterType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;

import java.util.regex.Pattern;

/**
 * Formatter for the ParticipantIdentifier with default "ebCoreParty" split regular expression and
 * '::' as split separator. For details see the {@link AbstractIdentifierFormatter}
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class ParticipantIdentifierFormatter extends AbstractIdentifierFormatter<ParticipantIdentifierType> {

    public ParticipantIdentifierFormatter() {

        this.formatterTypes.add(new EBCorePartyIdFormatterType());
    }

    @Override
    protected String getSchemeFromObject(ParticipantIdentifierType object) {
        return object != null ? object.getScheme() : null;
    }

    @Override
    protected String getIdentifierFromObject(ParticipantIdentifierType object) {
        return object != null ? object.getValue() : null;
    }

    @Override
    protected ParticipantIdentifierType createObject(String scheme, String identifier) {
        ParticipantIdentifierType identifierObject = new ParticipantIdentifierType();
        identifierObject.setScheme(scheme);
        identifierObject.setValue(identifier);
        return identifierObject;
    }

    @Override
    protected void updateObject(ParticipantIdentifierType identifierObject, String scheme, String identifier){
        identifierObject.setScheme(scheme);
        identifierObject.setValue(identifier);
    }
}
