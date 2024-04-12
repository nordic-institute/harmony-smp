/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.identifiers.types;

import eu.europa.ec.dynamicdiscovery.model.identifiers.types.OasisSMPFormatterType;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;

import static org.apache.commons.lang3.StringUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */

public class OasisSMPFormatterTypeTest {


    public static Collection<Object> participantIdentifierPositiveCases() {
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


    @ParameterizedTest
    @MethodSource("participantIdentifierPositiveCases")
    void isSchemeValid(String testName,
                              boolean isValidPartyId,
                              String toParseIdentifier,
                              String schemaPart,
                              String idPart,
                              Class errorClass,
                              String containsErrorMessage) {

        boolean result = testInstance.isSchemeValid(schemaPart);
        assertEquals(isValidPartyId, result);
    }

    @ParameterizedTest
    @MethodSource("participantIdentifierPositiveCases")
    void isType(String testName,
                       boolean isValidPartyId,
                       String toParseIdentifier,
                       String schemaPart,
                       String idPart,
                       Class errorClass,
                       String containsErrorMessage) {

        boolean result = testInstance.isType(toParseIdentifier);
        assertEquals(isValidPartyId, result);
    }

    @ParameterizedTest
    @MethodSource("participantIdentifierPositiveCases")
    void format(String testName,
                       boolean isValidPartyId,
                       String toParseIdentifier,
                       String schemaPart,
                       String idPart,
                       Class errorClass,
                       String containsErrorMessage) {
        // skip format for not ebcore party ids
        if (!isValidPartyId) {
            return;
        }

        String result = testInstance.format(schemaPart, idPart);
        String resultNoDelimiterForNullSchema = testInstance.format(schemaPart, idPart, true);

        String schema = trimToEmpty(schemaPart);
        assertEquals(schema + "::" + trim(idPart), result);

        assertEquals((isEmpty(schema) ? "" : schema + "::") + trim(idPart), resultNoDelimiterForNullSchema);
    }

    @ParameterizedTest
    @MethodSource("participantIdentifierPositiveCases")
    void parse(String testName,
                      boolean isValidPartyId,
                      String toParseIdentifier,
                      String schemaPart,
                      String idPart,
                      Class errorClass,
                      String containsErrorMessage) {
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
