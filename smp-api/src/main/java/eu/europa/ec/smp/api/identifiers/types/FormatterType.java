package eu.europa.ec.smp.api.identifiers.types;


/**
 * Formatter type interface for formatting and parsing party identifiers
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public interface FormatterType {

    boolean isTypeByScheme(final String scheme);
    // check if formatter can parse the value
    boolean isType(final String value);

    String format(final String scheme, final String identifier);

    // always returns array size 2 with first element as scheme and second as identifier part.
    String[] parse(final String value);
}