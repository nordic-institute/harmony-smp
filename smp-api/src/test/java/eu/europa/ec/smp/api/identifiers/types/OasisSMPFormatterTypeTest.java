package eu.europa.ec.smp.api.identifiers.types;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.trim;
import static org.junit.Assert.*;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
@RunWith(Parameterized.class)
public class OasisSMPFormatterTypeTest {


    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection participantIdentifierPositiveCases() {
        return Arrays.asList(new Object[][]{
                {
                        "Valid peppol party identifier",
                        true,
                        "iso6523-actorid-upis::0002:12345",
                        "iso6523-actorid-upis",
                        "0002:12345",
                        null, null
                },
                {
                        "no schema",
                        true,
                        "::0002:12345",
                        null,
                        "0002:12345",
                        null, null
                },
                {
                        "test URN example ",
                        true, // allways true - default parser
                        "urn:justice:si:1123445",
                        null,
                        "urn:justice:si:1123445",
                        null, null}
        });
    }

    OasisSMPFormatterType testInstance = new OasisSMPFormatterType();


    // input parameters
    @Parameterized.Parameter
    public String testName;
    @Parameterized.Parameter(1)
    public boolean isValidPartyId;
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
        assertEquals(isValidPartyId, result);
    }

    @Test
    public void isType() {

        boolean result = testInstance.isType(toParseIdentifier);
        assertEquals(isValidPartyId, result);
    }

    @Test
    public void format() {
        // skip format for not ebcore party ids
        if (!isValidPartyId) {
            return;
        }

        String result = testInstance.format(idPart, schemaPart);
        assertEquals(trim(idPart) + "::" + trim(schemaPart), result);
    }

    @Test
    public void parse() {
        // skip parse not ebcore party ids
        if (!isValidPartyId) {
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