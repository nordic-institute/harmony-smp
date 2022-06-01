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

import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.springframework.test.util.ReflectionTestUtils;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by gutowpa on 06/03/2017.
 */
@RunWith(JUnitParamsRunner.class)
public class CaseSensitivityNormalizerTest {

    private CaseSensitivityNormalizer normalizer;

    @Before
    public void init() {
        ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);
        normalizer = new CaseSensitivityNormalizer(configurationService);

        Mockito.doReturn(asList(new String[]{"case-SENSITIVE-scheme-1", "Case-SENSITIVE-Scheme-2"})).when(configurationService).getCaseSensitiveDocumentScheme();
        Mockito.doReturn(asList(new String[]{"case-sensitive-scheme-1", "Case-SENSITIVE-Scheme-2"})).when(configurationService).getCaseSensitiveParticipantScheme();

    }

    @SuppressWarnings("unused")
    private static Object[] testCases() {
        return new Object[][]{
                {"scheme", "value", "scheme", "value"},
                {"SCHEME", "VALUE", "scheme", "value"},
                {"SchemE", "ValuE", "scheme", "value"},
                {"case-sensitive-scheme-1", "Case-Sensitive-Value", "case-sensitive-scheme-1", "Case-Sensitive-Value"},
                {"CASE-SENSITIVE-SCHEME-1", "Case-Sensitive-Value", "CASE-SENSITIVE-SCHEME-1", "Case-Sensitive-Value"}, //scheme itself checked case-insensitively if should be case-sensitive or not
                {"case-sensitive-scheme-2", "Case-Sensitive-Value", "case-sensitive-scheme-2", "Case-Sensitive-Value"},
                {"CASE-SENSITIVE-SCHEME-2", "Case-Sensitive-Value", "CASE-SENSITIVE-SCHEME-2", "Case-Sensitive-Value"}, //scheme itself checked case-insensitively if should be case-sensitive or not
        };
    }

    @Test
    @Parameters(method = "testCases")
    public void testParticipantIdsCaseNormalization(String inputScheme, String inputValue, String expectedScheme, String expectedValue) {
        //given
        ParticipantIdentifierType inputParticpantId = new ParticipantIdentifierType(inputValue, inputScheme);

        //when
        ParticipantIdentifierType outputParticipantId = normalizer.normalize(inputParticpantId);

        //then
        assertEquals(expectedScheme, outputParticipantId.getScheme());
        assertEquals(expectedValue, outputParticipantId.getValue());

        //input stays untouched
        assertFalse(inputParticpantId == outputParticipantId);
        assertEquals(inputScheme, inputParticpantId.getScheme());
        assertEquals(inputValue, inputParticpantId.getValue());
    }

    @Test
    @Parameters(method = "testCases")
    public void testDocumentIdsCaseNormalization(String inputScheme, String inputValue, String expectedScheme, String expectedValue) {
        //given
        DocumentIdentifier inputDocId = new DocumentIdentifier(inputValue, inputScheme);

        //when
        DocumentIdentifier outputDocId = normalizer.normalize(inputDocId);

        //then
        assertEquals(expectedScheme, outputDocId.getScheme());
        assertEquals(expectedValue, outputDocId.getValue());

        //input stays untouched
        assertFalse(inputDocId == outputDocId);
        assertEquals(inputScheme, inputDocId.getScheme());
        assertEquals(inputValue, inputDocId.getValue());
    }
}