package eu.europa.ec.smp.api;

import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ProcessIdentifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Factory and utility methods for API classes generated from OASIS XSD.
 *
 * Created by gutowpa on 12/01/2017.
 */
public class Identifiers {

    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("^(?<scheme>.+?)::(?<value>.+)$");

    public static ParticipantIdentifierType asParticipantId(String doubleColonDelimitedId) {
        String scheme = extract(doubleColonDelimitedId, "scheme");
        String value = extract(doubleColonDelimitedId, "value");
        return new ParticipantIdentifierType(value, scheme);
    }

    public static DocumentIdentifier asDocumentId(String doubleColonDelimitedId) {
        String scheme = extract(doubleColonDelimitedId, "scheme");
        String value = extract(doubleColonDelimitedId, "value");
        return new DocumentIdentifier(value, scheme);
    }

    public static ProcessIdentifier asProcessId(String doubleColonDelimitedId) {
        String scheme = extract(doubleColonDelimitedId, "scheme");
        String value = extract(doubleColonDelimitedId, "value");
        return new ProcessIdentifier(value, scheme);
    }


    private static String extract(String doubleColonDelimitedId, String groupName) {
        try {
            Matcher m = IDENTIFIER_PATTERN.matcher(doubleColonDelimitedId);
            m.matches();
            return m.group(groupName);
        }catch(Exception e){
            throw new MalformedIdentifierException(doubleColonDelimitedId, e);
        }
    }
}
