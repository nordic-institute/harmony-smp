/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.cipa.smp.server.conversion;

import eu.europa.ec.edelivery.smp.config.SmpServicesTestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by gutowpa on 06/03/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SmpServicesTestConfig.class})
public class CaseSensitivityNormalizerTest {

    @Autowired
    private CaseSensitivityNormalizer normalizer;

    @SuppressWarnings("unused")
    private static String[][] testCases() {
        return new String[][]{
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
    public void testParticipantIdsCaseNormalization() {
        for (int i = 0; i < testCases().length; i++) {
            String inputScheme = testCases()[i][0];
            String inputValue = testCases()[i][1];
            String expectedScheme = testCases()[i][2];
            String expectedValue = testCases()[i][3];

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
    }

    @Test
    public void testDocumentIdsCaseNormalization() {
        for (int i = 0; i < testCases().length; i++) {
            String inputScheme = testCases()[i][0];
            String inputValue = testCases()[i][1];
            String expectedScheme = testCases()[i][2];
            String expectedValue = testCases()[i][3];

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

}
