package eu.europa.ec.smp.api.identifiers;

import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;

/**
 * Formatter for the DocumentIdentifier with default null split regular expression and
 * '::' as split separator. For details see the {@link AbstractIdentifierFormatter}
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class DocumentIdentifierFormatter extends AbstractIdentifierFormatter<DocumentIdentifier> {


    @Override
    protected String getSchemeFromObject(DocumentIdentifier object) {
        return object != null ? object.getScheme() : null;
    }

    @Override
    protected String getIdentifierFromObject(DocumentIdentifier object) {
        return object != null ? object.getValue() : null;
    }

    @Override
    protected DocumentIdentifier createObject(String scheme, String identifier) {
        DocumentIdentifier identifierObject = new DocumentIdentifier();
        identifierObject.setScheme(scheme);
        identifierObject.setValue(identifier);
        return identifierObject;
    }

    @Override
    protected void updateObject(DocumentIdentifier identifierObject, String scheme, String identifier) {
        identifierObject.setScheme(scheme);
        identifierObject.setValue(identifier);
    }
}
