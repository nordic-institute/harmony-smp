package eu.europa.ec.edelivery.smp.identifiers.types;

import eu.europa.ec.edelivery.smp.exceptions.MalformedIdentifierException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static org.apache.commons.lang3.StringUtils.*;


/**
 * EBCorePartyID implementation Formatter type.  The formatter supports
 *
 * <ul>
 * <li><b>Basic:</b>urn:oasis:names:tc:ebcore:partyid-type:<catalog-identifier>:(<scheme-in-catalog>)?:<scheme-specific-identifier></li>
 * <li><b>"Empty "SMP scheme" started with double colon (eDelivery URL variant):</b> ::urn:oasis:names:tc:ebcore:partyid-type:<catalog-identifier>:(<scheme-in-catalog>)?:<scheme-specific-identifier></li>
 * <li><b>Double colon separator (Oasis SMP variant):</b> urn:oasis:names:tc:ebcore:partyid-type:<catalog-identifier>::(<scheme-in-catalog>)?:<scheme-specific-identifier></li>
 * </ul>
 * <p>
 *  Currently allowed <catalog-identifier> are  iso6523 and unregistered
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class EBCorePartyIdFormatterType implements FormatterType {
    private static final Logger LOG = LoggerFactory.getLogger(EBCorePartyIdFormatterType.class);

    public static final String EBCORE_IDENTIFIER_PREFIX = "urn:oasis:names:tc:ebcore:partyid-type:";
    public static final String EBCORE_IDENTIFIER_ISO6523_SCHEME = "iso6523";
    public static final String EBCORE_IDENTIFIER_UNREGISTERED_SCHEME = "unregistered";
    private static final String EBCORE_SEPARATOR = ":";
    private static final String OASIS_SMP_SEPARATOR = "::";


    @Override
    public boolean isTypeByScheme(final String scheme) {
        String partyIdPrivate = StringUtils.trim(scheme);
        if (StringUtils.isBlank(scheme)) {
            LOG.debug("EBCorePartyIdFormatterType does not support identifiers with Null/Blank scheme");
            return false;
        }
        partyIdPrivate = removeStart(partyIdPrivate, OASIS_SMP_SEPARATOR);

        return startsWithIgnoreCase(partyIdPrivate, EBCORE_IDENTIFIER_PREFIX);
    }

    @Override
    public boolean isType(final String value) {
        // the value should start with valid scheme
        return isTypeByScheme(value);
    }

    @Override
    public String format(String scheme, String identifier, boolean noDelimiterOnEmptyScheme) {
        return (isBlank(scheme) && noDelimiterOnEmptyScheme ? "" : trimToEmpty(scheme) + EBCORE_SEPARATOR) + trimToEmpty(identifier);
    }

    @Override
    public String format(final String scheme, final String identifier) {
        return format(scheme, identifier, true);
    }

    @Override
    public String[] parse(final String value) {

        String partyIdPrivate = trimToEmpty(value);
        // ebcore party can start with OASIS_SMP_SEPARATOR - remove it of exists.
        partyIdPrivate = removeStart(partyIdPrivate, OASIS_SMP_SEPARATOR);

        // replace first OASIS_SMP_SEPARATOR  :: with  OASIS_SMP_SEPARATOR
        partyIdPrivate = StringUtils.replace(partyIdPrivate, OASIS_SMP_SEPARATOR, EBCORE_SEPARATOR, 1);

        if (!StringUtils.startsWithIgnoreCase(partyIdPrivate, EBCORE_IDENTIFIER_PREFIX)) {
            throw new MalformedIdentifierException(value, null);
        }
        boolean isIso6523 = StringUtils.startsWithIgnoreCase(partyIdPrivate, EBCORE_IDENTIFIER_PREFIX + EBCORE_IDENTIFIER_ISO6523_SCHEME +EBCORE_SEPARATOR);
        boolean isUnregistered = StringUtils.startsWithIgnoreCase(partyIdPrivate, EBCORE_IDENTIFIER_PREFIX + EBCORE_IDENTIFIER_UNREGISTERED_SCHEME + EBCORE_SEPARATOR);
        if (!isIso6523 && !isUnregistered) {
            throw new MalformedIdentifierException("Invalid ebCore id [" + partyIdPrivate + "] ebcoreId <scheme-in-catalog> must be  must one from the list " + Arrays.asList(EBCORE_IDENTIFIER_ISO6523_SCHEME, EBCORE_IDENTIFIER_UNREGISTERED_SCHEME) + "!");
        }

        int isSchemeDelimiter = partyIdPrivate.indexOf(EBCORE_SEPARATOR, EBCORE_IDENTIFIER_PREFIX.length());
        if (isSchemeDelimiter < 0) {
            // invalid scheme
            throw new MalformedIdentifierException(String.format("Invalid ebCore id [%s] ebcoreId must have prefix 'urn:oasis:names:tc:ebcore:partyid-type', " +
                    "and parts <catalog-identifier>, <scheme-in-catalog>, <scheme-specific-identifier> separated by colon.  " +
                    "Example: urn:oasis:names:tc:ebcore:partyid-type:<catalog-identifier>:(<scheme-in-catalog>)?:<scheme-specific-identifier>.", partyIdPrivate));
        }
        int isPartDelimiter = partyIdPrivate.indexOf(':', isSchemeDelimiter + 1);

        String[] result = new String[2];
        if (isPartDelimiter < 0 && isIso6523) { // for iso scheme-in-catalog is mandatory
            // invalid scheme
            throw new MalformedIdentifierException(String.format("Invalid ebCore id [%s] ebcoreId must have prefix 'urn:oasis:names:tc:ebcore:partyid-type', " +
                    "and parts <catalog-identifier>, <scheme-in-catalog>, <scheme-specific-identifier> separated by colon.  " +
                    "Example: urn:oasis:names:tc:ebcore:partyid-type:<catalog-identifier>:(<scheme-in-catalog>)?:<scheme-specific-identifier>.", partyIdPrivate));
        } else if (isPartDelimiter < 0) {
            result[0] = partyIdPrivate.substring(0, isSchemeDelimiter).trim();
            result[1] = partyIdPrivate.substring(isSchemeDelimiter + 1).trim();
        } else {
            result[0] = partyIdPrivate.substring(0, isPartDelimiter).trim();
            result[1] = partyIdPrivate.substring(isPartDelimiter + 1).trim();
        }

        // Final cleaning: remove separator on end of scheme part and start of id part
        result[1] = removeStart(result[1], EBCORE_SEPARATOR);
        result[0] = StringUtils.removeEnd(result[0], EBCORE_SEPARATOR);

        return result;
    }
}
