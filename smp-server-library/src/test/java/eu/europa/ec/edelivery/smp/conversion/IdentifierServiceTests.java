/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.identifiers.Identifier;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * Created by gutowpa on 06/03/2017.
 */
@RunWith(Parameterized.class)
public class IdentifierServiceTests {

    @Parameterized.Parameters(name = "{index}: {0}")
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

    // input parameters
    @Parameterized.Parameter
    public String inputScheme;
    @Parameterized.Parameter(1)
    public String inputValue;
    @Parameterized.Parameter(2)
    public String expectedScheme;
    @Parameterized.Parameter(3)
    public String expectedValue;

    private IdentifierService testInstance = new IdentifierService(Mockito.mock(ConfigurationService.class));

    @Before
    public void init() {
        testInstance.configureDocumentIdentifierFormatter(asList(new String[]{"case-SENSITIVE-scheme-1", "Case-SENSITIVE-Scheme-2"}));
        testInstance.configureParticipantIdentifierFormatter(asList(new String[]{"case-sensitive-scheme-1", "Case-SENSITIVE-Scheme-2"}), false, null);
    }


    @Test
    public void testParticipantIdsCaseNormalization() {
        //given
        Identifier inputParticpantId = new Identifier(inputValue, inputScheme);

        //when
        Identifier outputParticipantId = testInstance.normalizeParticipant(inputParticpantId);

        //then
        assertEquals(expectedScheme, outputParticipantId.getScheme());
        assertEquals(expectedValue, outputParticipantId.getValue());

        //input stays untouched
        assertNotSame(inputParticpantId, outputParticipantId);
        assertEquals(inputScheme, inputParticpantId.getScheme());
        assertEquals(inputValue, inputParticpantId.getValue());
    }

    @Test
    public void testDocumentIdsCaseNormalization() {
        //given
        Identifier inputDocId = new Identifier(inputValue, inputScheme);

        //when
        Identifier outputDocId = testInstance.normalizeDocument(inputDocId);

        //then
        assertEquals(expectedScheme, outputDocId.getScheme());
        assertEquals(expectedValue, outputDocId.getValue());

        //input stays untouched
        assertNotSame(inputDocId, outputDocId);
        assertEquals(inputScheme, inputDocId.getScheme());
        assertEquals(inputValue, inputDocId.getValue());
    }
}
