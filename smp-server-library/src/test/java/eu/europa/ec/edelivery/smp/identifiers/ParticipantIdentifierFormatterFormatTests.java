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
package eu.europa.ec.edelivery.smp.identifiers;


import eu.europa.ec.dynamicdiscovery.model.identifiers.types.EBCorePartyIdFormatterType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class ParticipantIdentifierFormatterFormatTests {

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

    IdentifierFormatter testInstance = IdentifierFormatter.Builder.create().addFormatterTypes(new EBCorePartyIdFormatterType()).build();

    @ParameterizedTest
    @MethodSource("participantIdentifierCases")
    void testFormat(String name,
                           Identifier participantIdentifierType,
                           String formattedIdentifier,
                           String uriFormattedIdentifier) {

        String result = testInstance.format(participantIdentifierType);
        String uriResult = testInstance.urlEncodedFormat(participantIdentifierType);

        Assertions.assertEquals(formattedIdentifier, result);
        Assertions.assertEquals(uriFormattedIdentifier, uriResult);
    }
}
