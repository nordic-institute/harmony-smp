package eu.europa.ec.edelivery.smp.identifiers;


import eu.europa.ec.edelivery.smp.identifiers.types.EBCorePartyIdFormatterType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
@RunWith(Parameterized.class)
public class ParticipantIdentifierFormatterFormatTests {

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection participantIdentifierCases() {
        return Arrays.asList(new Object[][]{
                {"ebCore unregistered",
                        new Identifier("ec.europa.eu", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:domain"),
                        "urn:oasis:names:tc:ebcore:partyid-type:unregistered:domain:ec.europa.eu",
                        "urn%3Aoasis%3Anames%3Atc%3Aebcore%3Apartyid-type%3Aunregistered%3Adomain%3Aec.europa.eu"},
                {"ebCore iso6523",
                        new Identifier("123456789", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088"),
                        "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088:123456789",
                        "urn%3Aoasis%3Anames%3Atc%3Aebcore%3Apartyid-type%3Aiso6523%3A0088%3A123456789"},
                {"Double colon basic",
                        new Identifier("b", "a"),
                        "a::b",
                        "a%3A%3Ab"},
                {"Double colon twice", new Identifier("b::c", "a"),
                        "a::b::c",
                        "a%3A%3Ab%3A%3Ac"},
                {"Double colon iso6523",
                        new Identifier("0002:12345", "iso6523-actorid-upis"),
                        "iso6523-actorid-upis::0002:12345",
                        "iso6523-actorid-upis%3A%3A0002%3A12345"},
                {"Double colon eHealth",
                        new Identifier("urn:poland:ncpb", "ehealth-actorid-qns"),
                        "ehealth-actorid-qns::urn:poland:ncpb",
                        "ehealth-actorid-qns%3A%3Aurn%3Apoland%3Ancpb"},
                {"Identifier with spaces -  formatted to uri with '%20",
                        new Identifier("urn ncpb test", "ehealth-actorid-qns"),
                        "ehealth-actorid-qns::urn ncpb test",
                        "ehealth-actorid-qns%3A%3Aurn%20ncpb%20test"},
        });
    }

    // input parameters
    @Parameterized.Parameter
    public String name;
    @Parameterized.Parameter(1)
    public Identifier participantIdentifierType;
    @Parameterized.Parameter(2)
    public String formattedIdentifier;
    @Parameterized.Parameter(3)
    public String uriFormattedIdentifier;

    IdentifierFormatter testInstance = IdentifierFormatter.Builder.create().addFormatterTypes(new EBCorePartyIdFormatterType()).build();

    @Test
    public void testFormat() {

        String result = testInstance.format(participantIdentifierType);
        String uriResult = testInstance.urlEncodedFormat(participantIdentifierType);

        Assert.assertEquals(formattedIdentifier, result);
        Assert.assertEquals(uriFormattedIdentifier, uriResult);
    }
}
