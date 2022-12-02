package eu.europa.ec.smp.api.identifiers;


import eu.europa.ec.smp.api.exceptions.MalformedIdentifierException;
import eu.europa.ec.smp.api.identifiers.types.FormatterType;
import eu.europa.ec.smp.api.identifiers.types.OasisSMPFormatterType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * Formatter for printing and parsing identifier objects.
 * <p>
 * This class provides parsing and formatting method for identifier objects as:
 * ParticipantIdentifierType, DocumentIdentifier, and ProcessIdentifier.
 *
 * <b>Parsing the identifier</b>
 * Parse method tries to detect the scheme and identifier part of the identifier string using the
 * regular expression and separator sequence.
 * The regular expression allows setting complex parsing templates, while the split separator is much raster.
 *
 * <ul>
 * <li>Using <b>Regular expressing:</b>Regular expression uses named groups &lt;scheme> and &lt;identifier> to identify the scheme and identifier.
 * if the regular expression is null or does not match, the parsing fallback to "split" with separator."
 * </li>
 * <li>Using <b>separator:</b>Separator splits regular expression on the first occurrence of the 'separator' sequence.</li>
 * </ul>
 * If no parsing is successful, then the scheme is set to null, and the identifier part has the input value.
 * In case the schemeMandatory is set to true and the scheme is null, the MalformedIdentifierException is thrown.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public abstract class AbstractIdentifierFormatter<T> {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractIdentifierFormatter.class);

    protected static final FormatterType DEFAULT_FORMATTER = new OasisSMPFormatterType();

    protected boolean schemeMandatory = false;
    protected Pattern schemeValidationPattern;
    protected List<String> caseSensitiveSchemas;
    protected List<FormatterType> formatterTypes = new ArrayList<>();


    /**
     * Formats the object according to formatTemplate. If template is 'blank' the scheme and identifier are concatenated
     * with separator
     *
     * @param value Identifier object to format it to string
     * @return String representation of the identifier
     */
    public String format(T value) {
        return format(getSchemeFromObject(value), getIdentifierFromObject(value));
    }

    /**
     * Formats the object according to formatTemplate. If template is 'blank' the scheme and identifier are concatenated
     * with separator
     *
     * @param scheme     scheme part to format it to string
     * @param identifier Identifier part to format it to string
     * @return String representation of the identifier
     */
    public String format(String scheme, String identifier) {
        // find the formatter
        Optional<FormatterType> optionalFormatterType = formatterTypes.stream().filter(formatterType ->
                formatterType.isTypeByScheme(scheme)).findFirst();

        if (optionalFormatterType.isPresent()) {
            return optionalFormatterType.get().format(scheme, identifier);
        }
        return DEFAULT_FORMATTER.format(scheme, identifier);
    }

    /**
     * Formats the object according to formatTemplate. If template is 'blank' the scheme and identifier are concatenated
     * with separator
     *
     * @param scheme     scheme part to format it to string
     * @param identifier Identifier part to format it to string
     * @param noDelimiterOnBlankScheme if true not delimiter is added when scheme is blankl
     * @return String representation of the identifier
     */
    public String format(String scheme, String identifier, boolean noDelimiterOnBlankScheme) {
        // find the formatter
        Optional<FormatterType> optionalFormatterType = formatterTypes.stream().filter(formatterType ->
                formatterType.isTypeByScheme(scheme)).findFirst();

        if (optionalFormatterType.isPresent()) {
            return optionalFormatterType.get().format(scheme, identifier, noDelimiterOnBlankScheme);
        }
        return DEFAULT_FORMATTER.format(scheme, identifier, noDelimiterOnBlankScheme);
    }

    /**
     * Parse identifier.
     * <p>
     * Method parse the identifier.
     *
     * @param value
     * @return
     */
    public T parse(final String value) {
        if (isBlank(value)) {
            throw new MalformedIdentifierException("Can not parse empty identifier value!");
        }

        String pValue = trim(value);

        // find the formatter
        Optional<FormatterType> optionalFormatterType = formatterTypes.stream().filter(formatterType ->
                formatterType.isType(pValue)).findFirst();

        String[] parseResult;
        if (optionalFormatterType.isPresent()) {
            parseResult = optionalFormatterType.get().parse(pValue);
        } else {
            parseResult = DEFAULT_FORMATTER.parse(pValue);
        }
        boolean isSchemeBlank = isBlank(parseResult[0]);
        if (isSchemeMandatory() && isSchemeBlank) {
            throw new MalformedIdentifierException("Invalid Identifier: [" + pValue + "]. Can not detect schema!");
        }

        if (!isSchemeBlank && schemeValidationPattern != null) {
            Matcher schemeMatcher = schemeValidationPattern.matcher(parseResult[0]);
            if (!schemeMatcher.matches()) {
                throw new MalformedIdentifierException("Invalid Identifier: [" + pValue + "]. Scheme does not match pattern: [" + schemeValidationPattern.pattern() + "]!");
            }
        }

        return createObject(parseResult[0], parseResult[1]);
    }

    /**
     * Method parses the object then it validates if scheme is case sensitive and lower case the values accordingly.
     *
     * @param value
     * @return
     */
    public T normalizeIdentifier(final String value) {
        T result = parse(value);
        String schema = getSchemeFromObject(result);

        if (isCaseInsensitiveSchema(schema)) {
            String identifier = getIdentifierFromObject(result);
            updateObject(result, lowerCase(schema), lowerCase(identifier));
        }

        return result;
    }

    /**
     * Method normalize the identifier using the format/parse and sets schema and identifier to lower case if
     * identifier is case insensitive.
     *
     * <ul>
     * <li><b>eDelivery example:</b> scheme [null], party id: [urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088:123456789]</li>
     * <li><b>oasis SMP example:</b> scheme [urn:oasis:names:tc:ebcore:partyid-type:iso6523], party id: [0088:123456789]</li>
     * <li><b>ebCore party ID example:</b>scheme [urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088], party id: [123456789]/li>
     * </ul>
     * <p>
     * Must always result in the same normalized object:
     * scheme [urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088]: party id: [123456789]
     *
     * @param value
     * @return
     */
    public T normalize(final T value) {
        return normalize(getSchemeFromObject(value),getIdentifierFromObject(value));
    }

    public T normalize(String scheme, String identifier) {
        return normalizeIdentifier(format(scheme, identifier));
    }

    /**
     * Return true if identifier schema is not defined in list of case sensitive schemas, else return false.
     *
     * @param schema
     * @return
     */
    public boolean isCaseInsensitiveSchema(String schema) {
        if (StringUtils.isEmpty(schema)) {
            LOG.debug("Empty/null schemas are case insensitive.");
            return true;
        }
        if (caseSensitiveSchemas == null || caseSensitiveSchemas.isEmpty()) {
            LOG.debug("Case sensitive schemas are not configure. return default value [false] for schema's [{}] case sensitive validation!", schema);
            return true;
        }
        return caseSensitiveSchemas.stream().noneMatch(schema::equalsIgnoreCase);
    }

    public String urlEncodedFormat(T value) {
        return urlEncode(format(value));
    }

    public String urlEncode(String s) {
        return UriUtils.encode(s, UTF_8.name());
    }

    protected abstract String getSchemeFromObject(T object);

    protected abstract String getIdentifierFromObject(T object);

    protected abstract T createObject(String scheme, String identifier);

    protected abstract void updateObject(T object, String scheme, String identifier);


    public List<String> getCaseSensitiveSchemas() {
        return caseSensitiveSchemas;
    }

    public AbstractIdentifierFormatter<T> caseSensitiveSchemas(List<String> caseSensitiveSchemas) {
        this.caseSensitiveSchemas = caseSensitiveSchemas;
        return this;
    }

    public void setCaseSensitiveSchemas(List<String> caseSensitiveSchemas) {
        this.caseSensitiveSchemas = caseSensitiveSchemas;
    }

    public boolean isSchemeMandatory() {
        return schemeMandatory;
    }

    public void setSchemeMandatory(boolean schemeMandatory) {
        this.schemeMandatory = schemeMandatory;
    }

    public Pattern getSchemeValidationPattern() {
        return schemeValidationPattern;
    }

    public void setSchemeValidationPattern(Pattern schemeValidationPattern) {
        this.schemeValidationPattern = schemeValidationPattern;
    }
}
