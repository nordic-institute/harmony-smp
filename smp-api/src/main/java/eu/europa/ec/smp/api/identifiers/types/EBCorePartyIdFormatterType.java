package eu.europa.ec.smp.api.identifiers.types;

import eu.europa.ec.smp.api.exceptions.MalformedIdentifierException;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;


/**
 * EBCorePartyID implementation Formatter type.  The formatter supports
 *
 * <ul>
 * <li><b>Basic:</b>urn:oasis:names:tc:ebcore:partyid-type:<catalog-identifier>:(<scheme-in-catalog>)?:<scheme-specific-identifier></li>
 * <li><b>"Empty "SMP scheme" started with double colon (eDelivery URL variant):</b> ::urn:oasis:names:tc:ebcore:partyid-type:<catalog-identifier>:(<scheme-in-catalog>)?:<scheme-specific-identifier></li>
 * <li><b>Double colon separator (Oasis SMP variant):</b> urn:oasis:names:tc:ebcore:partyid-type:<catalog-identifier>::(<scheme-in-catalog>)?:<scheme-specific-identifier></li>
 * </ul>
 *
 *  Currently allowed <catalog-identifier> are  iso6523 and unregistered
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class EBCorePartyIdFormatterType implements FormatterType {
    public static final String EBCORE_IDENTIFIER_PREFIX = "urn:oasis:names:tc:ebcore:partyid-type:";
    public static final String EBCORE_IDENTIFIER_ISO6523_SCHEME = "iso6523";
    public static final String EBCORE_IDENTIFIER_UNREGISTERED_SCHEME = "unregistered";
    private static final String EBCORE_SEPARATOR = ":";


    @Override
    public boolean isTypeByScheme(final String scheme) {
        String partyIdPrivate = StringUtils.trim(scheme);
        if (StringUtils.isBlank(scheme)){
            return false;
        }
        if (partyIdPrivate.startsWith("::")) {
            partyIdPrivate = StringUtils.removeStart(partyIdPrivate, "::");
        }
        return startsWithIgnoreCase(partyIdPrivate, EBCORE_IDENTIFIER_PREFIX);
    }

    @Override
    public boolean isType(final String value){
        // the value should start with valid scheme
        return isTypeByScheme(value);
    }

    @Override
    public String format(final String scheme, final String identifier) {
        return scheme + EBCORE_SEPARATOR + identifier;
    }

    @Override
    public String[] parse(final String value) {

        String partyIdPrivate = value.trim();
        if (partyIdPrivate.startsWith("::")) {
            partyIdPrivate = StringUtils.removeStart(partyIdPrivate, "::");
        }
        // replace first double :: with  :
        partyIdPrivate = StringUtils.replace(partyIdPrivate, "::",":", 1);
        if (!StringUtils.startsWithIgnoreCase(partyIdPrivate, EBCORE_IDENTIFIER_PREFIX)) {
            throw new MalformedIdentifierException(value, null);
        }
        boolean isIso6523 = StringUtils.startsWithIgnoreCase(partyIdPrivate,EBCORE_IDENTIFIER_PREFIX + EBCORE_IDENTIFIER_ISO6523_SCHEME + ":");
        boolean isUnregistered = StringUtils.startsWithIgnoreCase(partyIdPrivate,EBCORE_IDENTIFIER_PREFIX + EBCORE_IDENTIFIER_UNREGISTERED_SCHEME + ":");
        if (!isIso6523 && !isUnregistered ) {
            throw new MalformedIdentifierException("Invalid ebCore id ["+ partyIdPrivate+"] ebcoreId <scheme-in-catalog> must be  must one from the list "+ Arrays.asList(EBCORE_IDENTIFIER_ISO6523_SCHEME, EBCORE_IDENTIFIER_UNREGISTERED_SCHEME) +"!");
        }

        int isSchemeDelimiter = partyIdPrivate.indexOf(':', EBCORE_IDENTIFIER_PREFIX.length());
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

        //check if double colon was used for identifier separator in ebecoreid
        if (result[1].startsWith(":")) {
            result[1] = StringUtils.removeStart(result[1], ":");
        }
        //check if double colon was used for identifier separator in ebecoreid
        if (result[0].endsWith(":")) {
            result[0] = StringUtils.removeEnd(result[0], ":");
        }
        return result;
    }
}