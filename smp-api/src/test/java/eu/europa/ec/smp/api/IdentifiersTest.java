package eu.europa.ec.smp.api;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ProcessIdentifier;

import static org.junit.Assert.*;

/**
 * Created by gutowpa on 12/01/2017.
 */
@RunWith(JUnitParamsRunner.class)
public class IdentifiersTest {

    public static final String MALFORMED_INPUT_MSG = "Malformed identifier, scheme and id should be delimited by double colon: ";

    private static final Object[] testCases() {
        return new Object[][]{
                {"a::b", "a", "b"},
                {"a::b::c", "a", "b::c"},
                {"a:b::c", "a:b", "c"},
                {"ehealth-actorid-qns::urn:poland:ncpb", "ehealth-actorid-qns", "urn:poland:ncpb"},
                {"ehealth-resid-qns::urn::epsos##services:extended:epsos::51", "ehealth-resid-qns", "urn::epsos##services:extended:epsos::51"},
                {"iso6523-actorid-upis::0002:gutek", "iso6523-actorid-upis", "0002:gutek"}
        };
    }


    @Test
    @Parameters(method = "testCases")
    public void testParticipantIdPositive(String input, String scheme, String value) {
        //when
        ParticipantIdentifierType participantId = Identifiers.asParticipantId(input);

        //then
        assertEquals(scheme, participantId.getScheme());
        assertEquals(value, participantId.getValue());
    }

    @Test
    @Parameters(method = "testCases")
    public void testDocumentIdPositive(String input, String scheme, String value) {
        //when
        DocumentIdentifier documentId = Identifiers.asDocumentId(input);

        //then
        assertEquals(scheme, documentId.getScheme());
        assertEquals(value, documentId.getValue());
    }

    @Test
    @Parameters(method = "testCases")
    public void testProcessIdPositive(String input, String scheme, String value) {
        //when
        ProcessIdentifier processId = Identifiers.asProcessId(input);

        //then
        assertEquals(scheme, processId.getScheme());
        assertEquals(value, processId.getValue());
    }



    private static final Object[] negativeCases() {
        return new Object[]{
                null,
                "",
                "a",
                "abc",
                "a:b",
                "::a",
                "a::",
                "ehealth-actorid-qns",
                "urn:poland:ncpb",
                "ehealth-resid-qns",
                "epsos##services:extended:epsos:51"
        };
    }

    @Test
    @Parameters(method = "negativeCases")
    public void testProcessIdNegative(String negativeInput) {
        try {
            //when
            Identifiers.asProcessId(negativeInput);
        } catch (Exception e) {
            //then
            negativeAssertions(negativeInput, e);
            return;
        }
        fail();
    }

    @Test
    @Parameters(method = "negativeCases")
    public void testDocumentIdNegative(String negativeInput) {
        try {
            //when
            Identifiers.asDocumentId(negativeInput);
        } catch (Exception e) {
            ///then
            negativeAssertions(negativeInput, e);
            return;
        }
        fail();
    }

    @Test
    @Parameters(method = "negativeCases")
    public void testParticipantIdNegative(String negativeInput) {
        try {
            //when
            Identifiers.asParticipantId(negativeInput);
        } catch (Exception e) {
            //then
            negativeAssertions(negativeInput, e);
            return;
        }
        fail();
    }

    private void negativeAssertions(String negativeInput, Exception e) {
        assertTrue(e instanceof MalformedIdentifierException);
        assertEquals(MALFORMED_INPUT_MSG + negativeInput, e.getMessage());
    }

}
