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

package eu.europa.ec.smp.api;

import eu.europa.ec.smp.api.exceptions.MalformedIdentifierException;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    public static final String MALFORMED_INPUT_MSG = "Malformed identifier, scheme and id should be delimited by double colon: ";

    private static final Object[] participantIdentifierPositiveCases() {
        return new Object[][]{
                {"ebCore unregistered", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:domain:ec.europa.eu", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:domain", "ec.europa.eu"},
                {"ebCore iso6523", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088:123456789", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088", "123456789"},
                {"ebCore with space 1", " urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088:123456789", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088", "123456789"},
                {"ebCore with space 2", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088:123456789 ", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088", "123456789"},
                {"ebCore with space 3", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088::123456789 ", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088", "123456789"},

                {"ebCore unregistered with urn and colons", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:ehealth:urn:ehealth:pl:ncp-idp", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:ehealth", "urn:ehealth:pl:ncp-idp"},
                {"ebCore unregistered with dash", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:ehealth:pl:ncp-idp", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:ehealth", "pl:ncp-idp"},
                {"ebCore unregistered example double colon", "urn:oasis:names:tc:ebcore:partyid-type:unregistered::blue-gw", "urn:oasis:names:tc:ebcore:partyid-type:unregistered", "blue-gw"},
                {"ebCore unregistered example", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:blue-gw", "urn:oasis:names:tc:ebcore:partyid-type:unregistered", "blue-gw"},
                {"ebCore unregistered domain example", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:ec.europa.eu", "urn:oasis:names:tc:ebcore:partyid-type:unregistered", "ec.europa.eu"},
                {"ebCore unregistered email scheme example", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:email:test@my.mail.com", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:email", "test@my.mail.com"},
                {"ebCore unregistered email example", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:test@my.mail.com", "urn:oasis:names:tc:ebcore:partyid-type:unregistered", "test@my.mail.com"},
                {"ebCore with double colon", " urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088::123456789", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088", "123456789"},
                {"ebCore with double colon start", " ::urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088:123456789", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088", "123456789"},
                {"Double colon basic", "a::b", "a", "b"},
                {"Double colon twice", "a::b::c", "a", "b::c"},
                {"Double colon iso6523", "iso6523-actorid-upis::0002:gutek", "iso6523-actorid-upis", "0002:gutek"},
                {"Double colon ehealth", "ehealth-actorid-qns::urn:poland:ncpb", "ehealth-actorid-qns", "urn:poland:ncpb"},
                {"Double colon ehealth 2", "ehealth-actorid-qns::urn:ehealth:hr:ncpb-idp", "ehealth-actorid-qns", "urn:ehealth:hr:ncpb-idp"},
                {"Double colon ehealth 3", "scheme::urn:ehealth:pt:ncpb-idp", "scheme", "urn:ehealth:pt:ncpb-idp"},
                {"Double colon custom scheme", "otherscheme::urn:ehealth:be:ncpb-idp", "otherscheme", "urn:ehealth:be:ncpb-idp"},
        };
    }

    private static final Object[] participantIdentifierNegativeCases() {
        return new Object[]{
                "Null value", null,
                "Empty value", "",
                "Not double colon and not ebCoreId", "a",
                "Constant and is not ebCoreId 2", "abc",
                "Only one colon", "a:b",
                "Missing identifier", "a::",
                "Not double colon and not ebCoreId", "ehealth-actorid-qns",
                "Not double colon and not ebCoreId", "urn:poland:ncpb",
                "Not double colon and not ebCoreId", "ehealth-resid-qns",
                "Not double colon and not ebCoreId", "epsos##services:extended:epsos:51",
        };
    }

    private static final Object[] testCases() {
        return new Object[][]{
                {"a::b", "a", "b"},
                {"a::b::c", "a", "b::c"},
                {"a:b::c", "a:b", "c"},
                {"ehealth-actorid-qns::urn:poland:ncpb", "ehealth-actorid-qns", "urn:poland:ncpb"},
                {"ehealth-resid-qns::urn::epsos##services:extended:epsos::51", "ehealth-resid-qns", "urn::epsos##services:extended:epsos::51"},
                {"iso6523-actorid-upis::0002:gutek", "iso6523-actorid-upis", "0002:gutek"},
                {"ehealth-actorid-qns::urn:ehealth:hr:ncpb-idp", "ehealth-actorid-qns", "urn:ehealth:hr:ncpb-idp"},
                {"scheme::urn:ehealth:pt:ncpb-idp", "scheme", "urn:ehealth:pt:ncpb-idp"},
                {"otherscheme::urn:ehealth:be:ncpb-idp", "otherscheme", "urn:ehealth:be:ncpb-idp"},
                {"ehealth-resid-qns::urn:ehealth:IdentityService::XCPD::CrossGatewayPatientDiscovery##ITI-55", "ehealth-resid-qns", "urn:ehealth:IdentityService::XCPD::CrossGatewayPatientDiscovery##ITI-55"},
                {"ehealth-resid-qns::urn:XCPD::CrossGatewayPatientDiscovery", "ehealth-resid-qns", "urn:XCPD::CrossGatewayPatientDiscovery"},
                {"ehealth-resid-qns::urn:ehealth:PatientService::XCA::CrossGatewayQuery##ITI-38", "ehealth-resid-qns", "urn:ehealth:PatientService::XCA::CrossGatewayQuery##ITI-38"},
                {"ehealth-resid-qns::urn:XCA::CrossGatewayQuery", "ehealth-resid-qns", "urn:XCA::CrossGatewayQuery"},
                {"ehealth-resid-qns::urn:ehealth:OrderService::XCA::CrossGatewayQuery##ITI-38", "ehealth-resid-qns", "urn:ehealth:OrderService::XCA::CrossGatewayQuery##ITI-38"},
                {"ehealth-resid-qns::urn:ehealth:DispensationService:Initialize::XDR::ProvideandRegisterDocumentSet-b##ITI-41", "ehealth-resid-qns", "urn:ehealth:DispensationService:Initialize::XDR::ProvideandRegisterDocumentSet-b##ITI-41"},
                {"ehealth-resid-qns::urn:XDR::ProvideandRegisterDocumentSet-b", "ehealth-resid-qns", "urn:XDR::ProvideandRegisterDocumentSet-b"},
                {"ehealth-resid-qns::urn:ehealth:DispensationService:Discard::XDR::ProvideandRegisterDocumentSet-b##ITI-41", "ehealth-resid-qns", "urn:ehealth:DispensationService:Discard::XDR::ProvideandRegisterDocumentSet-b##ITI-41"},
                {"ehealth-resid-qns::urn:ehealth:ConsentService:Put::XDR::BPPCProvideandRegisterDocumentSet-b##ITI-41", "ehealth-resid-qns", "urn:ehealth:ConsentService:Put::XDR::BPPCProvideandRegisterDocumentSet-b##ITI-41"},
                {"ehealth-resid-qns::urn:XDR::BPPCProvideandRegisterDocumentSet-b", "ehealth-resid-qns", "urn:XDR::BPPCProvideandRegisterDocumentSet-b"},
                {"ehealth-resid-qns::urn:ehealth:ConsentService:Discard::XDR::BPPCProvideandRegisterDocumentSet-b##ITI-41", "ehealth-resid-qns", "urn:ehealth:ConsentService:Discard::XDR::BPPCProvideandRegisterDocumentSet-b##ITI-41"},
                {"ehealth-resid-qns::urn:ehealth:IdP::identityProvider::HPAuthentication##epsos-91", "ehealth-resid-qns", "urn:ehealth:IdP::identityProvider::HPAuthentication##epsos-91"},
                {"ehealth-resid-qns::urn:identityProvider::HPAuthentication", "ehealth-resid-qns", "urn:identityProvider::HPAuthentication"},
                {"ehealth-resid-qns::urn:ehealth:IdP::XUA::ProvideX-UserAssertion##ITI-40", "ehealth-resid-qns", "urn:ehealth:IdP::XUA::ProvideX-UserAssertion##ITI-40"},
                {"ehealth-resid-qns::urn:XUA::ProvideX-UserAssertion", "ehealth-resid-qns", "urn:XUA::ProvideX-UserAssertion"},
                {"ehealth-resid-qns::urn:ehealth:VPN::VPNGatewayServer##epsos-105", "ehealth-resid-qns", "urn:ehealth:VPN::VPNGatewayServer##epsos-105"},
                {"ehealth-resid-qns::urn:ehealth:ncp::vpngateway", "ehealth-resid-qns", "urn:ehealth:ncp::vpngateway"},
                {"ehealth-resid-qns::urn:ehealth:VPN::VPNGatewayClient##epsos-106", "ehealth-resid-qns", "urn:ehealth:VPN::VPNGatewayClient##epsos-106"},
                {"ehealth-resid-qns::urn:ehealth:ISM::InternationalSearchMask##epsos-107", "ehealth-resid-qns", "urn:ehealth:ISM::InternationalSearchMask##epsos-107"},
                {"ehealth-resid-qns::urn:ehealth:ncp::pt:ism", "ehealth-resid-qns", "urn:ehealth:ncp::pt:ism"}
        };
    }

    private static final Object[] ebCoreIdentifiersCases() {
        return new Object[][]{
                {"ebCore unregistered", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:domain:ec.europa.eu", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:domain", "ec.europa.eu"},
                {"ebCore iso6523", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088:123456789", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088", "123456789"},
                {"ebCore with space 1", " urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088:123456789", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088", "123456789"},
                {"ebCore with space 2", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088:123456789 ", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088", "123456789"},
                {"ebCore unregistered with urn and colons", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:ehealth:urn:ehealth:pl:ncp-idp", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:ehealth", "urn:ehealth:pl:ncp-idp"},
                {"ebCore unregistered with dash", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:ehealth:pl:ncp-idp", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:ehealth", "pl:ncp-idp"},
                {"ebCore unregistered example", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:blue-gw", "urn:oasis:names:tc:ebcore:partyid-type:unregistered", "blue-gw"},
                {"ebCore unregistered domain example", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:ec.europa.eu", "urn:oasis:names:tc:ebcore:partyid-type:unregistered", "ec.europa.eu"},
                {"ebCore unregistered email scheme example", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:email:test@my.mail.com", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:email", "test@my.mail.com"},
                {"ebCore unregistered email example", "urn:oasis:names:tc:ebcore:partyid-type:unregistered:test@my.mail.com", "urn:oasis:names:tc:ebcore:partyid-type:unregistered", "test@my.mail.com"},
                {"ebCore with double colon", " urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088::123456789", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088", "123456789"},
                {"ebCore with double colon start", " ::urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088:123456789", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:0088", "123456789"},

        };
    }

    private static final Object[] ebCoreIdentifiersNegativeCases() {
        return new Object[][]{
                {"Not an ebCore ", "urn:my:space:tc:ebcore:partyid-type:unregistered:domain:ec.europa.eu", MalformedIdentifierException.class},
                {"ebCore iso6523", "urn:oasis:names:tc:ebcore:partyid-type:iso6523:Illegal-value-without-scheme", IllegalArgumentException.class},
                {"ebCore with no catalog", " urn:oasis:names:tc:ebcore:partyid-type:0088123456789", IllegalArgumentException.class},
        };
    }

    private static final Object[] negativeCases() {
        return new Object[]{
                null,
                "",
                "a",
                "abc",
                "a:b",
                "a::",
                "ehealth-actorid-qns",
                "urn:poland:ncpb",
                "ehealth-resid-qns",
                "epsos##services:extended:epsos:51",
        };
    }

    private static final Object[] documentTestCases() {
        Object[] commonTests = testCases();
        Object[] res = new Object[commonTests.length + 2];
        System.arraycopy(commonTests, 0, res, 0, commonTests.length);
        //add new test with empty schema
        res[commonTests.length] = new Object[]{"::a", null, "a"};
        res[commonTests.length + 1] = new Object[]{"::urn:ehealth:ncp::pt:ism", null, "urn:ehealth:ncp::pt:ism"};
        return res;
    }

    private static final Object[] negativeDocumentCases() {
        Object[] commonNegativeTests = negativeCases();
        Object[] res = new Object[commonNegativeTests.length - 1]; // skip last one
        System.arraycopy(commonNegativeTests, 0, res, 0, commonNegativeTests.length - 1);

        return res;
    }

    @Test
    @Parameters(method = "participantIdentifierPositiveCases")
    @TestCaseName("{0}")
    public void testParticipantIdPositive(String caseName, String input, String scheme, String value) {
        //when
        ParticipantIdentifierType participantId = Identifiers.asParticipantId(input);

        //then
        assertEquals(scheme, participantId.getScheme());
        assertEquals(value, participantId.getValue());
    }

    @Test
    @Parameters(method = "ebCoreIdentifiersCases")
    @TestCaseName("{0}")
    public void testSplitEbCoreIdentifier(String caseName, String input, String scheme, String value) {
        //when
        String[] values = Identifiers.splitEbCoreIdentifier(input);

        //then
        assertEquals(2, values.length);
        assertEquals(scheme, values[0]);
        assertEquals(value, values[1]);
    }

    @Test
    @Parameters(method = "participantIdentifierNegativeCases")
    @TestCaseName("{0}")
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

    @Test
    @Parameters(method = "ebCoreIdentifiersNegativeCases")
    @TestCaseName("{0}")
    public void testSplitEbCoreIdentifierNegative(String caseName, String negativeInput, Class clz) {

        expectedEx.expect(clz);
        //when
        Identifiers.asParticipantId(negativeInput);

    }

    @Test
    @Parameters(method = "documentTestCases")
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
    @Parameters(method = "negativeDocumentCases")
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

    private void negativeAssertions(String negativeInput, Exception e) {
        assertTrue(e instanceof MalformedIdentifierException);
        assertEquals(MALFORMED_INPUT_MSG + (StringUtils.isBlank(negativeInput) ? "Null/Empty" : negativeInput), e.getMessage());
    }

    @Test
    public void testUrlEncodingParticipantId() {
        //given
        ParticipantIdentifierType participantId = new ParticipantIdentifierType("0088:conformance:sg01#", "ehealth:actorid:qns");

        //when-then
        assertEquals("ehealth%3Aactorid%3Aqns%3A%3A0088%3Aconformance%3Asg01%23", Identifiers.asUrlEncodedString(participantId));
    }

    @Test
    public void testUrlEncodingParticipantIdWithSpace() {
        //given
        ParticipantIdentifierType participantId = new ParticipantIdentifierType("GPR: 0088:conformance:sg01#", "ehealth:actorid:qns");

        //when-then
        //Because this is path segment spaces must be percent encoded (not with +)!
        assertEquals("ehealth%3Aactorid%3Aqns%3A%3AGPR%3A%200088%3Aconformance%3Asg01%23", Identifiers.asUrlEncodedString(participantId));
    }

    @Test
    public void testUrlEncodingDocumentId() {
        //given
        DocumentIdentifier docId = new DocumentIdentifier("urn::ehealth##services:extended:epsos01::101", "busdox:docid:qns");

        //when-then
        assertEquals("busdox%3Adocid%3Aqns%3A%3Aurn%3A%3Aehealth%23%23services%3Aextended%3Aepsos01%3A%3A101", Identifiers.asUrlEncodedString(docId));
    }

    @Test
    public void testUrlEncodingDocumentIdWithSpace() {
        //given
        DocumentIdentifier docId = new DocumentIdentifier("urn::ehealth##services:extended:epsos01:: 101", "busdox:docid:qns");

        //when-then
        //Because this is path segment spaces must be percent encoded (not with +)!
        assertEquals("busdox%3Adocid%3Aqns%3A%3Aurn%3A%3Aehealth%23%23services%3Aextended%3Aepsos01%3A%3A%20101", Identifiers.asUrlEncodedString(docId));
    }

}
