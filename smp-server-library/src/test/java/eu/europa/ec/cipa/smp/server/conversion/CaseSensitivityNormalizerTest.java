package eu.europa.ec.cipa.smp.server.conversion;

import eu.europa.ec.cipa.smp.server.util.ConfigFile;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

/**
 * Created by gutowpa on 06/03/2017.
 */
@RunWith(JUnitParamsRunner.class)
public class CaseSensitivityNormalizerTest {

    private static final String KEY_CASE_SENSITIVE_PARTICIPANT_SCHEMES = "identifiersBehaviour.caseSensitive.ParticipantIdentifierSchemes";
    private static final String KEY_CASE_SENSITIVE_DOCUMENT_SCHEMES = "identifiersBehaviour.caseSensitive.DocumentIdentifierSchemes";

    private static final List<String> CASE_SENSITIVE_PARTICIPANT_SCHEMES = asList(new String[]{"case-sensitive-scheme-1", "Case-SENSITIVE-Scheme-2"});
    private static final List<String> CASE_SENSITIVE_DOCUMENT_SCHEMES = asList(new String[]{"case-sensitive-scheme-1", "Case-SENSITIVE-Scheme-2"});

    @InjectMocks
    private CaseSensitivityNormalizer normalizer;

    @Mock
    private ConfigFile configFile;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(configFile.getStringList(KEY_CASE_SENSITIVE_PARTICIPANT_SCHEMES)).thenReturn(CASE_SENSITIVE_PARTICIPANT_SCHEMES);
        when(configFile.getStringList(KEY_CASE_SENSITIVE_DOCUMENT_SCHEMES)).thenReturn(CASE_SENSITIVE_DOCUMENT_SCHEMES);
        normalizer.init();
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
