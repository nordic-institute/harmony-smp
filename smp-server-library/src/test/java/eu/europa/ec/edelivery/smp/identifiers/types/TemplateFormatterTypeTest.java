/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
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

import eu.europa.ec.dynamicdiscovery.model.identifiers.types.TemplateFormatterType;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.trim;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class TemplateFormatterTypeTest {


    public static Collection participantIdentifierPositiveCases() {
        return Arrays.asList(new Object[][]{
                {
                        "Email example ",
                        true,
                        "mailto:test@ec.europa.eu",
                        "mailto",
                        "test@ec.europa.eu",
                        null, null
                },
                {
                        "test URN example ",
                        true,
                        "urn:ehealth:si:1123445",
                        "urn:ehealth:si",
                        "1123445",
                        null, null
                },
                {
                        "test URN example ",
                        false,
                        "urn:justice:si:1123445",
                        "urn:justice:si",
                        "1123445",
                        IllegalArgumentException.class, "does not match regular expression"}
        });
    }

    TemplateFormatterType testInstance
            = new TemplateFormatterType(Pattern.compile("^(?i)\\s*(::)?((urn:ehealth:[a-zA-Z]{2})|mailto).*$"),
            "${scheme}::${identifier}",
            Pattern.compile("^(?i)\\s*(::)?(?<scheme>(urn:ehealth:[a-zA-Z]{2})|mailto):?(?<identifier>.+)?\\s*$")
    );


    // input parameters
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

        String result = testInstance.format(idPart, schemaPart);
        assertEquals(trim(idPart) + "::" + trim(schemaPart), result);
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
