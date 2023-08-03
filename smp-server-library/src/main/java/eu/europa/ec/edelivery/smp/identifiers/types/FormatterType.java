package eu.europa.ec.edelivery.smp.identifiers.types;


/**
 * Formatter type interface for formatting and parsing party identifiers
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public interface FormatterType {

    /**
     * Method  returns true if scheme is supported by the formatter for parsing and formatting, else it return false:.
     * @param scheme identifier scheme part
     * @return return true if identifier is supported by the formatter else return false
     */
    boolean isTypeByScheme(final String scheme);

    /**
     * Method  returns true if identifier is supported by the formatter for parsing and formatting, else it return false:.
     * @param value identifier value
     * @return return true if identifier is supported by the formatter else return false
     */
    boolean isType(final String value);

    String format(final String scheme, final String identifier);
    String format(final String scheme, final String identifier, boolean noDelimiterOnEmptyScheme);

    // always returns array size 2 with first element as scheme and second as identifier part.
    String[] parse(final String value);
}
