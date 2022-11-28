package eu.europa.ec.smp.api.identifiers.types;

import eu.europa.ec.smp.api.exceptions.MalformedIdentifierException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.apache.commons.lang3.StringUtils.trim;
import static org.junit.Assert.*;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
@RunWith(Parameterized.class)
public class EBCorePartyIdFormatterTypeTest {


    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection participantIdentifierPositiveCases() {
        return Arrays.asList(new Object[][]{
                {
                        "unregistered with <scheme-in-catalog",
                        true,
                        "urn:oasis:names:tc:ebcore:partyid-type:unregistered:domain:ec.europa.eu",
                        "urn:oasis:names:tc:ebcore:partyid-type:unregistered:domain",
                        "ec.europa.eu",
                        null, null
                },
                {
                        "Case insensitive schema",
                        true,
                        "urn:OASIS:names:tC:eBcore:partyId-type:unregistered:domain:ec.europa.eu",
                        "urn:OASIS:names:tC:eBcore:partyId-type:unregistered:domain",
                        "ec.europa.eu",
                        null, null
                },
                {
                        "unregistered without <scheme-in-catalog",
                        true,
                        "urn:oasis:names:tc:ebcore:partyid-type:unregistered:ec.europa.eu",
                        "urn:oasis:names:tc:ebcore:partyid-type:unregistered",
                        "ec.europa.eu",
                        null, null},
                {
                        "iso6523",
                        true,
                        "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088:123456789",
                        "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088",
                        "123456789",
                        null, null},
                {"with spaces",
                        true,
                        "  urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088:123456789 ",
                        "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088", "123456789",
                        null, null},
                {"with spaces in the identifier",
                        true,
                        "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088: 123456789 ",
                        "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088", "123456789",
                        null, null},
                {"Parse eDelivery URN format",
                        true,
                        "::urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088:123456789",
                        "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088", "123456789",
                        null, null},
                {"Parse peppol URN format 1",
                        true,
                        "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088::123456789",
                        "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088", "123456789",
                        null, null},
                {"Parse peppol URN format 2",
                        true,
                        "urn:oasis:names:tc:ebcore:partyid-type:iso6523::0088:123456789",
                        "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088", "123456789",
                        null, null},
                {
                        "invalid catalog identifier",
                        true,
                        "urn:oasis:names:tc:ebcore:partyid-type:invalid-catalog:ec.europa.eu",
                        "urn:oasis:names:tc:ebcore:partyid-type:invalid-catalog",
                        "ec.europa.eu",
                        IllegalArgumentException.class, "Invalid ebCore id "},
                {
                        "Not ebcore party id",
                        false,
                        "urn:ehealth:invalid-catalog:ec.europa.eu",
                        "urn:ehealth:invalid-catalog:ec.europa.eu",
                        null,
                        MalformedIdentifierException.class, "Malformed identifier"},
                {
                        "Not ebcore party id iso6523",
                        true,
                        "urn:oasis:names:tc:ebcore:partyid-type:iso6523::0088",
                        "urn:oasis:names:tc:ebcore:partyid-type:iso6523::0088",
                        null,
                        IllegalArgumentException.class, "Invalid ebCore id"},
        });
    }

    EBCorePartyIdFormatterType testInstance = new EBCorePartyIdFormatterType();

    // input parameters
    @Parameterized.Parameter
    public String testName;
    @Parameterized.Parameter(1)
    public boolean isEBCorePartyId;
    @Parameterized.Parameter(2)
    public String toParseIdentifier;
    @Parameterized.Parameter(3)
    public String schemaPart;
    @Parameterized.Parameter(4)
    public String idPart;
    @Parameterized.Parameter(5)
    public Class errorClass;
    @Parameterized.Parameter(6)
    public String containsErrorMessage;


    @Test
    public void isTypeByScheme() {

        boolean result = testInstance.isTypeByScheme(schemaPart);
        assertEquals(isEBCorePartyId, result);
    }

    @Test
    public void isType() {

        boolean result = testInstance.isType(toParseIdentifier);
        assertEquals(isEBCorePartyId, result);
    }

    @Test
    public void format() {
        // skip format for not ebcore party ids
        if (!isEBCorePartyId) {
            return;
        }

        String result = testInstance.format(idPart, schemaPart);
        assertEquals(trim(idPart) + ":" + trim(schemaPart), result);
    }

    @Test
    public void parse() {
        // skip parse not ebcore party ids
        if (!isEBCorePartyId) {
            IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () -> testInstance.parse(toParseIdentifier));
            MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString(containsErrorMessage));
        }
        if (errorClass != null) {
            Throwable result = assertThrows(errorClass, () -> testInstance.parse(toParseIdentifier));
            MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString(containsErrorMessage));
        } else {

            String[] result = testInstance.parse(toParseIdentifier);
            assertNotNull(result);
            assertEquals(2, result.length);
            assertEquals(schemaPart, result[0]);
            assertEquals(idPart, result[1]);
        }
    }
}