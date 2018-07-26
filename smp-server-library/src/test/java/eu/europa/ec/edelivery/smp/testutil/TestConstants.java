package eu.europa.ec.edelivery.smp.testutil;

import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;

import static eu.europa.ec.smp.api.Identifiers.asParticipantId;

public class TestConstants {

    public static final String SERVICE_GROUP_XML_PATH = "/eu/europa/ec/edelivery/smp/services/ServiceGroupPoland.xml";
    public static final ParticipantIdentifierType SERVICE_GROUP_ID = asParticipantId("participant-scheme-qns::urn:poland:ncpb");

    public static final String ADMIN_USERNAME = "test_admin";
    public static final String CERT_USER="CN=comon name,O=org,C=BE:0000000000000066";
    public static final String CERT_USER_ENCODED="CN%3Dcomon%20name%2CO%3Dorg%2CC%3DBE%3A0000000000000066";


    public static final String SECOND_DOMAIN_ID = "domain2";
    public static final String SECOND_DOMAIN_CERT_HEADER = "client-cert-header-value";
    public static final String SECOND_DOMAIN_SIGNING_ALIAS = "signature-alias";
    public static final String SECOND_DOMAIN_SMP_ID = "SECOND-SMP-ID";
}
