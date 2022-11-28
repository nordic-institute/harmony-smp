package eu.europa.ec.smp.api.identifiers.types;

import org.apache.commons.lang3.StringUtils;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.trim;

/**
 * Simple OSASIS SMP party identifier formatter.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class OasisSMPFormatterType implements FormatterType {
    private static final String SEPARATOR = "::";

    @Override
    public boolean isTypeByScheme(final String scheme) {
        // by default format all identifier as defined in OasisSMP
        return true;
    }

    @Override
    public boolean isType(final String value){
        // the value should start with valid scheme
        return true;
    }

    @Override
    public String format(final String scheme, final String identifier) {
        return (isEmpty(scheme) ? "" : scheme + SEPARATOR) + identifier;
    }

    @Override
    public String[] parse(final String value) {
        String pValue = trim(value);
        String[] splitValue = StringUtils.splitByWholeSeparator(pValue, SEPARATOR, 2);
        // if only one value is returned set it to identifier
        // else the first element is scheme and second identifier
        String scheme = trim(splitValue.length == 1 ? null : splitValue[0]);
        String identifier = trim(splitValue[splitValue.length == 1 ? 0 : 1]);
        return new String[]{scheme, identifier};

    }
}