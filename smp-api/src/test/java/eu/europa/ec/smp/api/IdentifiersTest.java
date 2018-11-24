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
                {"iso6523-actorid-upis::0002:gutek", "iso6523-actorid-upis", "0002:gutek"},
                {"ehealth-actorid-qns::urn:ehealth:hr:ncpb-idp","ehealth-actorid-qns","urn:ehealth:hr:ncpb-idp"},
                {"scheme::urn:ehealth:pt:ncpb-idp","scheme","urn:ehealth:pt:ncpb-idp"},
                {"otherscheme::urn:ehealth:be:ncpb-idp","otherscheme","urn:ehealth:be:ncpb-idp"},
                {"ehealth-resid-qns::urn:ehealth:IdentityService::XCPD::CrossGatewayPatientDiscovery##ITI-55","ehealth-resid-qns","urn:ehealth:IdentityService::XCPD::CrossGatewayPatientDiscovery##ITI-55"},
                {"ehealth-resid-qns::urn:XCPD::CrossGatewayPatientDiscovery","ehealth-resid-qns","urn:XCPD::CrossGatewayPatientDiscovery"},
                {"ehealth-resid-qns::urn:ehealth:PatientService::XCA::CrossGatewayQuery##ITI-38","ehealth-resid-qns","urn:ehealth:PatientService::XCA::CrossGatewayQuery##ITI-38"},
                {"ehealth-resid-qns::urn:XCA::CrossGatewayQuery","ehealth-resid-qns","urn:XCA::CrossGatewayQuery"},
                {"ehealth-resid-qns::urn:ehealth:OrderService::XCA::CrossGatewayQuery##ITI-38","ehealth-resid-qns","urn:ehealth:OrderService::XCA::CrossGatewayQuery##ITI-38"},
                {"ehealth-resid-qns::urn:ehealth:DispensationService:Initialize::XDR::ProvideandRegisterDocumentSet-b##ITI-41","ehealth-resid-qns","urn:ehealth:DispensationService:Initialize::XDR::ProvideandRegisterDocumentSet-b##ITI-41"},
                {"ehealth-resid-qns::urn:XDR::ProvideandRegisterDocumentSet-b","ehealth-resid-qns","urn:XDR::ProvideandRegisterDocumentSet-b"},
                {"ehealth-resid-qns::urn:ehealth:DispensationService:Discard::XDR::ProvideandRegisterDocumentSet-b##ITI-41","ehealth-resid-qns","urn:ehealth:DispensationService:Discard::XDR::ProvideandRegisterDocumentSet-b##ITI-41"},
                {"ehealth-resid-qns::urn:ehealth:ConsentService:Put::XDR::BPPCProvideandRegisterDocumentSet-b##ITI-41","ehealth-resid-qns","urn:ehealth:ConsentService:Put::XDR::BPPCProvideandRegisterDocumentSet-b##ITI-41"},
                {"ehealth-resid-qns::urn:XDR::BPPCProvideandRegisterDocumentSet-b","ehealth-resid-qns","urn:XDR::BPPCProvideandRegisterDocumentSet-b"},
                {"ehealth-resid-qns::urn:ehealth:ConsentService:Discard::XDR::BPPCProvideandRegisterDocumentSet-b##ITI-41","ehealth-resid-qns","urn:ehealth:ConsentService:Discard::XDR::BPPCProvideandRegisterDocumentSet-b##ITI-41"},
                {"ehealth-resid-qns::urn:ehealth:IdP::identityProvider::HPAuthentication##epsos-91","ehealth-resid-qns","urn:ehealth:IdP::identityProvider::HPAuthentication##epsos-91"},
                {"ehealth-resid-qns::urn:identityProvider::HPAuthentication","ehealth-resid-qns","urn:identityProvider::HPAuthentication"},
                {"ehealth-resid-qns::urn:ehealth:IdP::XUA::ProvideX-UserAssertion##ITI-40","ehealth-resid-qns","urn:ehealth:IdP::XUA::ProvideX-UserAssertion##ITI-40"},
                {"ehealth-resid-qns::urn:XUA::ProvideX-UserAssertion","ehealth-resid-qns","urn:XUA::ProvideX-UserAssertion"},
                {"ehealth-resid-qns::urn:ehealth:VPN::VPNGatewayServer##epsos-105","ehealth-resid-qns","urn:ehealth:VPN::VPNGatewayServer##epsos-105"},
                {"ehealth-resid-qns::urn:ehealth:ncp::vpngateway","ehealth-resid-qns","urn:ehealth:ncp::vpngateway"},
                {"ehealth-resid-qns::urn:ehealth:VPN::VPNGatewayClient##epsos-106","ehealth-resid-qns","urn:ehealth:VPN::VPNGatewayClient##epsos-106"},
                {"ehealth-resid-qns::urn:ehealth:ISM::InternationalSearchMask##epsos-107","ehealth-resid-qns","urn:ehealth:ISM::InternationalSearchMask##epsos-107"},
                {"ehealth-resid-qns::urn:ehealth:ncp::pt:ism","ehealth-resid-qns","urn:ehealth:ncp::pt:ism"}
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
                "::a",
        };
    }

    private static final Object[] documentTestCases() {
        Object[] commonTests = testCases();
        Object[] res = new Object[commonTests.length+2];
        System.arraycopy(commonTests, 0,res, 0, commonTests.length );
        //add new test with empty schema
        res[commonTests.length] = new Object[]{"::a",null,"a"};
        res[commonTests.length+1] = new Object[]{"::urn:ehealth:ncp::pt:ism",null,"urn:ehealth:ncp::pt:ism"};
        return res;
    }

    private static final Object[] negativeDocumentCases() {
        Object[] commonNegativeTests = negativeCases();
        Object[] res = new Object[commonNegativeTests.length-1]; // skip last one
        System.arraycopy(commonNegativeTests, 0,res, 0, commonNegativeTests.length-1 );

        return res;
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

    @Test
    public void testUrlEncodingParticipantId(){
        //given
        ParticipantIdentifierType participantId = new ParticipantIdentifierType("0088:conformance:sg01#", "ehealth:actorid:qns");

        //when-then
        assertEquals("ehealth%3Aactorid%3Aqns%3A%3A0088%3Aconformance%3Asg01%23", Identifiers.asUrlEncodedString(participantId));
    }

    @Test
    public void testUrlEncodingDocumentId(){
        //given
        DocumentIdentifier docId = new DocumentIdentifier("urn::ehealth##services:extended:epsos01::101", "busdox:docid:qns");

        //when-then
        assertEquals("busdox%3Adocid%3Aqns%3A%3Aurn%3A%3Aehealth%23%23services%3Aextended%3Aepsos01%3A%3A101", Identifiers.asUrlEncodedString(docId));
    }

}
