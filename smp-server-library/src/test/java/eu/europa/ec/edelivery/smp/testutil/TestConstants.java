package eu.europa.ec.edelivery.smp.testutil;

import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;

import static eu.europa.ec.smp.api.Identifiers.asParticipantId;

public class TestConstants {

    public static final String TEST_DOMAIN_CODE_1 = "utestPeppol01";
    public static final String TEST_DOMAIN_CODE_2 = "utestEHhealth02";

    public static final String TEST_SML_SUBDOMAIN_CODE_1 = ""; // peppol subdomain is empty string
    public static final String TEST_SML_SUBDOMAIN_CODE_2 = "ehealth";


    public static final String TEST_SG_ID_1 = "0007:001:utest";
    public static final String TEST_SG_ID_2 = "urn:eu:ncpb:utest";
    public static final String TEST_SG_ID_3 = "0007:002:utest";
    public static final String TEST_SG_ID_4 = "0007:004:utest";
    public static final String TEST_SG_ID_PL= "urn:poland:ncpb:utest";
    public static final String TEST_SG_ID_PL2= "urn:Poland:ncpb";


    public static final String TEST_SG_SCHEMA_1 = "iso6523-actorid-upis";
    public static final String TEST_SG_SCHEMA_2 = "ehealth-actorid-qns";
    public static final String TEST_SG_SCHEMA_PL2 = "eHealth-participantId-qns";




    public static final String TEST_DOC_SCHEMA_1 = "busdox-docid-qns";
    public static final String TEST_DOC_SCHEMA_2 = "ehealth-resid-qns";
    public static final String TEST_DOC_SCHEMA_PL2="eHealth-resId-qns";

    public static final String TEST_DOC_ID_1 = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-12::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0";
    public static final String TEST_DOC_ID_2 = "docid.007";
    public static final String TEST_DOC_ID_PL2 = "DocId.007";


    public static final String USERNAME_1 = "test-user_001";
    public static final String USERNAME_2 = "test-user_002";
    public static final String USERNAME_3 = "test-user_003";

    public static final String USER_CERT_1="CN=utest comon name 01,O=org,C=BE:0000000000000066";
    public static final String USER_CERT_2="CN=utest comon name 02,O=org,C=BE:0000000000000077";
    public static final String USER_CERT_3="CN=utest comon name 03,O=org,C=BE:0000000000000077";


    public static final String SERVICE_GROUP_POLAND_XML_PATH = "/examples/services/ServiceGroupPoland.xml";
    public static final String SERVICE_GROUP_TEST2_XML_PATH = "/examples/services/ServiceGroupTestSgId2.xml";
    public static final String SERVICE_METADATA_XML_PATH = "/examples/services/ServiceMetadataPoland.xml";
    public static final String SIGNED_SERVICE_METADATA_XML_PATH = "/examples/services/SignedServiceMetadataPoland.xml";

    public static final ParticipantIdentifierType SERVICE_GROUP_ID = asParticipantId("participant-scheme-qns::urn:eu:ncpb");

    public static final String ADMIN_USERNAME = "test_admin";
    public static final String CERT_USER="CN=comon name,O=org,C=BE:0000000000000066";
    public static final String CERT_USER_ENCODED="CN%3Dcomon%20name%2CO%3Dorg%2CC%3DBE%3A0000000000000066";

    // parameter: custom string as conntent
    public static final String SIMPLE_EXTENSION_XML ="<Extension xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\"><ex:dummynode xmlns:ex=\"http://test.eu\">Sample not mandatory extension: %s</ex:dummynode></Extension>";
    //5 parameters: ParticipantScheme, ParticipantIdentifier, DocumentScheme, DocumentIdentifier, custom value
    public static final String SIMPLE_DOCUMENT_XML ="<ServiceMetadata xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\"><ServiceInformation><ParticipantIdentifier scheme=\"%s\">%s</ParticipantIdentifier><DocumentIdentifier scheme=\"%s\">%s</DocumentIdentifier><ProcessList><Process><ProcessIdentifier scheme=\"cenbii-procid-ubl\">urn:www.cenbii.eu:profile:bii04:ver1.0</ProcessIdentifier><ServiceEndpointList><Endpoint transportProfile=\"bdxr-transport-ebms3-as4-v1p0\"><EndpointURI>http://localhost:8080/domibus-weblogic/services/msh</EndpointURI><RequireBusinessLevelSignature>true</RequireBusinessLevelSignature><ServiceActivationDate>2003-01-01T00:00:00</ServiceActivationDate><ServiceExpirationDate>2020-05-01T00:00:00</ServiceExpirationDate>" +
            "<Certificate>VGhpcyBpcyB0ZXN0IGNlcnRpZmljYXRlIGlzIHlvdSBiZWxpZXZlIG9yIG5vdC4=</Certificate><ServiceDescription>Sample description of %s</ServiceDescription><TechnicalContactUrl>https://example.com</TechnicalContactUrl></Endpoint></ServiceEndpointList></Process></ProcessList></ServiceInformation></ServiceMetadata>";

}
