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
public class ParticipantIdentifierFormatterParseTests {


    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection participantIdentifierPositiveCases() {
        return Arrays.asList(new Object[][]{
                {"ebCore unregistered", false, "urn:oasis:names:tc:ebcore:partyid-type:unregistered:domain:ec.europa.eu", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:domain", "ec.europa.eu"},
                {"ebCore iso6523", false, "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088:123456789", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088", "123456789"},
                {"ebCore with space 1", false, " urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088:123456789", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088", "123456789"},
                {"ebCore with space 2", false, "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088:123456789 ", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088", "123456789"},
                {"ebCore with space 3", false, "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088::123456789 ", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088", "123456789"},
                {"ebCore unregistered with urn and colons", false, "urn:oasis:names:tc:ebcore:partyid-type:unregistered:ehealth:urn:ehealth:pl:ncp-idp", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:ehealth", "urn:ehealth:pl:ncp-idp"},
                {"ebCore unregistered with dash", false, "urn:oasis:names:tc:ebcore:partyid-type:unregistered:ehealth:pl:ncp-idp", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:ehealth", "pl:ncp-idp"},
                {"ebCore unregistered example double colon", false, "urn:oasis:names:tc:ebcore:partyid-type:unregistered::blue-gw", "urn:oasis:names:tc:ebcore:partyid-type:unregistered", "blue-gw"},
                {"ebCore unregistered example", false, "urn:oasis:names:tc:ebcore:partyid-type:unregistered:blue-gw", "urn:oasis:names:tc:ebcore:partyid-type:unregistered", "blue-gw"},
                {"ebCore unregistered domain example", false, "urn:oasis:names:tc:ebcore:partyid-type:unregistered:ec.europa.eu", "urn:oasis:names:tc:ebcore:partyid-type:unregistered", "ec.europa.eu"},
                {"ebCore unregistered email scheme example", false, "urn:oasis:names:tc:ebcore:partyid-type:unregistered:email:test@my.mail.com", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:email", "test@my.mail.com"},
                {"ebCore unregistered email example", false, "urn:oasis:names:tc:ebcore:partyid-type:unregistered:test@my.mail.com", "urn:oasis:names:tc:ebcore:partyid-type:unregistered", "test@my.mail.com"},
                {"ebCore with double colon", false, " urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088::123456789", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088", "123456789"},
                {"ebCore with double colon start", false, " ::urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088:123456789", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088", "123456789"},
                {"Double colon basic", false, "a::b", "a", "b"},
                {"Double colon twice", false, "a::b::c", "a", "b::c"},
                {"Double colon iso6523", false, "iso6523-actorid-upis::0002:gutek", "iso6523-actorid-upis", "0002:gutek"},
                {"Double colon ehealth", false, "ehealth-actorid-qns::urn:poland:ncpb", "ehealth-actorid-qns", "urn:poland:ncpb"},
                {"Double colon ehealth 2", false, "ehealth-actorid-qns::urn:ehealth:hr:ncpb-idp", "ehealth-actorid-qns", "urn:ehealth:hr:ncpb-idp"},
                {"Double colon ehealth 3", false, "scheme::urn:ehealth:pt:ncpb-idp", "scheme", "urn:ehealth:pt:ncpb-idp"},
                {"Double colon custom scheme", false, "otherscheme::urn:ehealth:be:ncpb-idp", "otherscheme", "urn:ehealth:be:ncpb-idp"},

                {"ebCore iso6523", true, "urn:oasis:names:tc:ebcore:partyid-type:iso6523:Illegal-value-without-scheme", null, null},
                {"ebCore with no catalog", true, " urn:oasis:names:tc:ebcore:partyid-type:0088123456789", null, null},
        });
    }
    // input parameters
    @Parameterized.Parameter
    public String name;
    @Parameterized.Parameter(1)
    public boolean throwError;
    @Parameterized.Parameter(2)
    public String identifier;
    @Parameterized.Parameter(3)
    public String schemaPart;
    @Parameterized.Parameter(4)
    public String idPart;

    IdentifierFormatter testInstance = IdentifierFormatter.Builder.create().addFormatterTypes(new EBCorePartyIdFormatterType()).build();

    @Test
    public void testPartyIdentifierParse() {
        IllegalArgumentException exception = null;
        Identifier result = null;
        if (throwError) {
            exception = Assert.assertThrows(IllegalArgumentException.class, () -> testInstance.parse(identifier));
        } else {
            result = testInstance.parse(identifier);
        }

        Assert.assertNotNull(throwError ? exception : result);
        if (!throwError) {
            Assert.assertEquals(schemaPart, result.getScheme());
            Assert.assertEquals(idPart, result.getValue());
        }
    }


}
