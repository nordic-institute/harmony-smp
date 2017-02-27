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
                {"ehealth-resid-qns::urn:ehealth:ncp::pt:ism","ehealth-resid-qns","urn:ehealth:ncp::pt:ism"},
                {"busdox-docid-qns::urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biitrns014:ver2.0:extended:urn:www.peppol.eu:bis:peppol5a:ver2.0::2.1", "busdox-docid-qns", "urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biitrns014:ver2.0:extended:urn:www.peppol.eu:bis:peppol5a:ver2.0::2.1"}
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
