package eu.europa.ec.smp.api.identifiers;

import org.oasis_open.docs.bdxr.ns.smp._2016._05.ProcessIdentifier;

/**
 * Formatter for the ProcessIdentifier with default null split regular expression and
 * '::' as split separator. For details see the {@link AbstractIdentifierFormatter}
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class ProcessIdentifierFormatter extends AbstractIdentifierFormatter<ProcessIdentifier> {


    @Override
    protected String getSchemeFromObject(ProcessIdentifier object) {
        return object != null ? object.getScheme() : null;
    }

    @Override
    protected String getIdentifierFromObject(ProcessIdentifier object) {
        return object != null ? object.getValue() : null;
    }

    @Override
    protected ProcessIdentifier createObject(String scheme, String identifier) {
        ProcessIdentifier identifierObject = new ProcessIdentifier();
        identifierObject.setScheme(scheme);
        identifierObject.setValue(identifier);
        return identifierObject;
    }

    @Override
    protected void updateObject(ProcessIdentifier identifierObject, String scheme, String identifier) {
        identifierObject.setScheme(scheme);
        identifierObject.setValue(identifier);
    }
}