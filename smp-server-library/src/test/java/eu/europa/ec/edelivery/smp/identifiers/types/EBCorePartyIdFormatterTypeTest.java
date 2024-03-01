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

import eu.europa.ec.dynamicdiscovery.exception.MalformedIdentifierException;
import eu.europa.ec.dynamicdiscovery.model.identifiers.types.EBCorePartyIdFormatterType;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;

import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class EBCorePartyIdFormatterTypeTest {

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

    @ParameterizedTest
    @MethodSource("participantIdentifierPositiveCases")
    void isSchemeValid(String testName,
                              boolean isEBCorePartyId,
                              String toParseIdentifier,
                              String schemaPart,
                              String idPart,
                              Class errorClass,
                              String containsErrorMessage) {

        boolean result = testInstance.isSchemeValid(schemaPart);
        assertEquals(isEBCorePartyId, result);
    }

    @ParameterizedTest
    @MethodSource("participantIdentifierPositiveCases")
    void isType(String testName,
                       boolean isEBCorePartyId,
                       String toParseIdentifier,
                       String schemaPart,
                       String idPart,
                       Class errorClass,
                       String containsErrorMessage) {

        boolean result = testInstance.isType(toParseIdentifier);
        assertEquals(isEBCorePartyId, result);
    }

    @ParameterizedTest
    @MethodSource("participantIdentifierPositiveCases")
    void format(String testName,
                       boolean isEBCorePartyId,
                       String toParseIdentifier,
                       String schemaPart,
                       String idPart,
                       Class errorClass,
                       String containsErrorMessage) {
        // skip format for not ebcore party ids
        if (!isEBCorePartyId) {
            return;
        }

        String result = testInstance.format(schemaPart, idPart);

        String schema = trimToEmpty(schemaPart);
        assertEquals(schema + ":" + trimToEmpty(idPart), result);
    }

    @ParameterizedTest
    @MethodSource("participantIdentifierPositiveCases")
    void parse(String testName,
                      boolean isEBCorePartyId,
                      String toParseIdentifier,
                      String schemaPart,
                      String idPart,
                      Class errorClass,
                      String containsErrorMessage) {
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
