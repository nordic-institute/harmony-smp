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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
public class DocumentIdentifierFormatterTest {

    public static Collection documentIdentifierCases() {
        return Arrays.asList(new Object[][]{
                {"Basic example",
                        new Identifier("b", "a"),
                        "a::b",
                        "a%3A%3Ab",
                        "a::b",
                        false},
                {"Double separator example",
                        new Identifier("b::c", "a"),
                        "a::b::c",
                        "a%3A%3Ab%3A%3Ac",
                        "a::b::c",
                        false},
                {"Char : in scheme colon basic",
                        new Identifier("c", "a:b"),
                        "a:b::c",
                        "a%3Ab%3A%3Ac",
                        "a:b::c",
                        false},
                {"No Scheme Identifier ",
                        new Identifier("NoSchemeIdentifier", null),
                        "::NoSchemeIdentifier",
                        "%3A%3ANoSchemeIdentifier",
                        "NoSchemeIdentifier",
                        false},
                {"No Scheme Identifier with ::",
                        new Identifier("NoSchemeIdentifier01", null),
                        "::NoSchemeIdentifier01",
                        "%3A%3ANoSchemeIdentifier01",
                        "::NoSchemeIdentifier01",
                        false},
                {"No Scheme Identifier with no scheme and ::",
                        new Identifier("NoSchemeIdentifier01::test", null),
                        "::NoSchemeIdentifier01::test",
                        "%3A%3ANoSchemeIdentifier01%3A%3Atest",
                        "::NoSchemeIdentifier01::test",
                        false},
                {"Example with ## and double colon",
                        new Identifier("urn::epsos##services:extended:epsos::51", "ehealth-resid-qns"),
                        "ehealth-resid-qns::urn::epsos##services:extended:epsos::51",
                        "ehealth-resid-qns%3A%3Aurn%3A%3Aepsos%23%23services%3Aextended%3Aepsos%3A%3A51",
                        "ehealth-resid-qns::urn::epsos##services:extended:epsos::51",
                        false},
                {"Identifier with spaces -  formatted to uri with '%20",
                        new Identifier("urn ncpb test", "ehealth-actorid-qns"),
                        "ehealth-actorid-qns::urn ncpb test",
                        "ehealth-actorid-qns%3A%3Aurn%20ncpb%20test",
                        "ehealth-actorid-qns::urn ncpb test",
                        false},
                {"Example 01 (parse spaces)", new Identifier("urn:ehealth:pt:ncpb-idp", "scheme"),
                        "scheme::urn:ehealth:pt:ncpb-idp",
                        "scheme%3A%3Aurn%3Aehealth%3Apt%3Ancpb-idp",
                        " scheme::urn:ehealth:pt:ncpb-idp",
                        false},
                {"Example 02 (parse spaces)", new Identifier("urn:ehealth:be:ncpb-idp", "otherscheme"),
                        "otherscheme::urn:ehealth:be:ncpb-idp",
                        "otherscheme%3A%3Aurn%3Aehealth%3Abe%3Ancpb-idp",
                        "otherscheme::urn:ehealth:be:ncpb-idp ",
                        false},
                {"Example 03 (parse spaces in argument)", new Identifier("urn:ehealth:IdentityService::XCPD::CrossGatewayPatientDiscovery##ITI-55", "ehealth-resid-qns"),
                        "ehealth-resid-qns::urn:ehealth:IdentityService::XCPD::CrossGatewayPatientDiscovery##ITI-55",
                        "ehealth-resid-qns%3A%3Aurn%3Aehealth%3AIdentityService%3A%3AXCPD%3A%3ACrossGatewayPatientDiscovery%23%23ITI-55",
                        "ehealth-resid-qns:: urn:ehealth:IdentityService::XCPD::CrossGatewayPatientDiscovery##ITI-55 ",
                        false},
                {"Example 04", new Identifier("urn:XCPD::CrossGatewayPatientDiscovery", "ehealth-resid-qns"),
                        "ehealth-resid-qns::urn:XCPD::CrossGatewayPatientDiscovery",
                        "ehealth-resid-qns%3A%3Aurn%3AXCPD%3A%3ACrossGatewayPatientDiscovery",
                        "ehealth-resid-qns::urn:XCPD::CrossGatewayPatientDiscovery",
                        false},
                {"Example 05", new Identifier("urn:ehealth:PatientService::XCA::CrossGatewayQuery##ITI-38", "ehealth-resid-qns"),
                        "ehealth-resid-qns::urn:ehealth:PatientService::XCA::CrossGatewayQuery##ITI-38",
                        "ehealth-resid-qns%3A%3Aurn%3Aehealth%3APatientService%3A%3AXCA%3A%3ACrossGatewayQuery%23%23ITI-38",
                        "ehealth-resid-qns::urn:ehealth:PatientService::XCA::CrossGatewayQuery##ITI-38",
                        false},
                {"Example 06", new Identifier("urn:XCA::CrossGatewayQuery", "ehealth-resid-qns"),
                        "ehealth-resid-qns::urn:XCA::CrossGatewayQuery",
                        "ehealth-resid-qns%3A%3Aurn%3AXCA%3A%3ACrossGatewayQuery",
                        "ehealth-resid-qns::urn:XCA::CrossGatewayQuery",
                        false},
                {"Example 07", new Identifier("urn:ehealth:OrderService::XCA::CrossGatewayQuery##ITI-38", "ehealth-resid-qns"),
                        "ehealth-resid-qns::urn:ehealth:OrderService::XCA::CrossGatewayQuery##ITI-38",
                        "ehealth-resid-qns%3A%3Aurn%3Aehealth%3AOrderService%3A%3AXCA%3A%3ACrossGatewayQuery%23%23ITI-38",
                        "ehealth-resid-qns::urn:ehealth:OrderService::XCA::CrossGatewayQuery##ITI-38",
                        false},
                {"Example 08", new Identifier("urn:ehealth:DispensationService:Initialize::XDR::ProvideandRegisterDocumentSet-b##ITI-41", "ehealth-resid-qns"),
                        "ehealth-resid-qns::urn:ehealth:DispensationService:Initialize::XDR::ProvideandRegisterDocumentSet-b##ITI-41",
                        "ehealth-resid-qns%3A%3Aurn%3Aehealth%3ADispensationService%3AInitialize%3A%3AXDR%3A%3AProvideandRegisterDocumentSet-b%23%23ITI-41",
                        "ehealth-resid-qns::urn:ehealth:DispensationService:Initialize::XDR::ProvideandRegisterDocumentSet-b##ITI-41",
                        false},
                {"Example 09", new Identifier("urn:XDR::ProvideandRegisterDocumentSet-b", "ehealth-resid-qns"),
                        "ehealth-resid-qns::urn:XDR::ProvideandRegisterDocumentSet-b",
                        "ehealth-resid-qns%3A%3Aurn%3AXDR%3A%3AProvideandRegisterDocumentSet-b",
                        "ehealth-resid-qns::urn:XDR::ProvideandRegisterDocumentSet-b",
                        false},
                {"Example 10", new Identifier("urn:ehealth:DispensationService:Discard::XDR::ProvideandRegisterDocumentSet-b##ITI-41", "ehealth-resid-qns"),
                        "ehealth-resid-qns::urn:ehealth:DispensationService:Discard::XDR::ProvideandRegisterDocumentSet-b##ITI-41",
                        "ehealth-resid-qns%3A%3Aurn%3Aehealth%3ADispensationService%3ADiscard%3A%3AXDR%3A%3AProvideandRegisterDocumentSet-b%23%23ITI-41",
                        "ehealth-resid-qns::urn:ehealth:DispensationService:Discard::XDR::ProvideandRegisterDocumentSet-b##ITI-41",
                        false},

        });
    }

    IdentifierFormatter testInstance = IdentifierFormatter.Builder.create().build();

    @ParameterizedTest
    @MethodSource("documentIdentifierCases")
    void testFormat(String name,
                           Identifier identifierType,
                           String formattedIdentifier,
                           String uriFormattedIdentifier,
                           String identifierToParse,
                           boolean throwParseError) {

        String result = testInstance.format(identifierType);
        String uriResult = testInstance.urlEncodedFormat(identifierType);

        assertEquals(formattedIdentifier, result);
        assertEquals(uriFormattedIdentifier, uriResult);
    }

    @ParameterizedTest
    @MethodSource("documentIdentifierCases")
    void testParse(String name,
                          Identifier identifierType,
                          String formattedIdentifier,
                          String uriFormattedIdentifier,
                          String identifierToParse,
                          boolean throwParseError) {
        IllegalArgumentException exception = null;
        Identifier result = null;
        if (throwParseError) {
            exception = assertThrows(IllegalArgumentException.class, () -> testInstance.parse(identifierToParse));
        } else {
            result = testInstance.parse(identifierToParse);
        }

        assertNotNull(throwParseError ? exception : result);
        if (!throwParseError) {
            assertEquals(identifierType.getScheme(), result.getScheme());
            assertEquals(identifierType.getValue(), result.getValue());
        }
    }
}
