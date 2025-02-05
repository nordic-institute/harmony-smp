/*-
 * #START_LICENSE#
 * smp-webapp
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

package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.dynamicdiscovery.model.identifiers.types.EBCorePartyIdFormatterType;
import eu.europa.ec.edelivery.smp.identifiers.Identifier;
import eu.europa.ec.edelivery.smp.identifiers.IdentifierFormatter;
import eu.europa.ec.edelivery.smp.services.IdentifierFormatterService;
import eu.europa.ec.edelivery.smp.services.IdentifierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * Created by gutowpa on 06/03/2017.
 */
public class IdentifierServiceTests {

    public static Collection testCases() {
        return Arrays.asList(new Object[][]{
                {"scheme", "value", "scheme", "value"},
                {"SCHEME", "VALUE", "scheme", "value"},
                {"SchemE", "ValuE", "scheme", "value"},
                {"case-sensitive-scheme-1", "Case-Sensitive-Value", "case-sensitive-scheme-1", "Case-Sensitive-Value"},
                {"CASE-SENSITIVE-SCHEME-1", "Case-Sensitive-Value", "CASE-SENSITIVE-SCHEME-1", "Case-Sensitive-Value"}, //scheme itself checked case-insensitively if should be case-sensitive or not
                {"case-sensitive-scheme-2", "Case-Sensitive-Value", "case-sensitive-scheme-2", "Case-Sensitive-Value"},
                {"CASE-SENSITIVE-SCHEME-2", "Case-Sensitive-Value", "CASE-SENSITIVE-SCHEME-2", "Case-Sensitive-Value"}, //scheme itself checked case-insensitively if should be case-sensitive or not
        });
    }

    IdentifierFormatterService mockIdentifierFormatterService = Mockito.mock(IdentifierFormatterService.class);

    private final IdentifierService testInstance = new IdentifierService(mockIdentifierFormatterService);

    @BeforeEach
    public void init() {
        IdentifierFormatter resourceIdentifierFormatter = IdentifierFormatter.Builder
                .create()
                .addFormatterTypes(new EBCorePartyIdFormatterType())
                .build();
        resourceIdentifierFormatter.setCaseSensitiveSchemas(asList("case-sensitive-scheme-1", "Case-SENSITIVE-Scheme-2"));
        resourceIdentifierFormatter.setSchemeMandatory(false);
        resourceIdentifierFormatter.setSchemeValidationPattern(Pattern.compile(".*"));

        IdentifierFormatter subresourceIdentifierFormatter = IdentifierFormatter.Builder
                .create()
                .build();

        subresourceIdentifierFormatter.setCaseSensitiveSchemas(asList("case-SENSITIVE-scheme-1", "Case-SENSITIVE-Scheme-2"));


        Mockito.when(mockIdentifierFormatterService.getResourceIdentifierFormatter(ArgumentMatchers.anyString()))
                .thenReturn(resourceIdentifierFormatter);


        Mockito.when(mockIdentifierFormatterService.getSubresourceIdentifierFormatter(ArgumentMatchers.anyString()))
                .thenReturn(subresourceIdentifierFormatter);

      /*  testInstance.configureDocumentIdentifierFormatter(
                asList("case-SENSITIVE-scheme-1", "Case-SENSITIVE-Scheme-2"));
        testInstance.configureParticipantIdentifierFormatter(
                asList("case-sensitive-scheme-1", "Case-SENSITIVE-Scheme-2"), false, null);

       */
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void testParticipantIdsCaseNormalization(String inputScheme,
                                             String inputValue,
                                             String expectedScheme,
                                             String expectedValue) {
        //given
        Identifier inputParticpantId = new Identifier(inputValue, inputScheme);

        //when
        Identifier outputParticipantId = testInstance.normalizeParticipant("testdomain", inputParticpantId);

        //then
        assertEquals(expectedScheme, outputParticipantId.getScheme());
        assertEquals(expectedValue, outputParticipantId.getValue());

        //input stays untouched
        assertNotSame(inputParticpantId, outputParticipantId);
        assertEquals(inputScheme, inputParticpantId.getScheme());
        assertEquals(inputValue, inputParticpantId.getValue());
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void testDocumentIdsCaseNormalization(String inputScheme,
                                          String inputValue,
                                          String expectedScheme,
                                          String expectedValue) {
        //given
        Identifier inputDocId = new Identifier(inputValue, inputScheme);

        //when
        Identifier outputDocId = testInstance.normalizeDocument("testdomain", inputDocId);

        //then
        assertEquals(expectedScheme, outputDocId.getScheme());
        assertEquals(expectedValue, outputDocId.getValue());

        //input stays untouched
        assertNotSame(inputDocId, outputDocId);
        assertEquals(inputScheme, inputDocId.getScheme());
        assertEquals(inputValue, inputDocId.getValue());
    }
}
