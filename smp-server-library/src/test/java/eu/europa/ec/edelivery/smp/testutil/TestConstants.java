package eu.europa.ec.edelivery.smp.testutil;




public class TestConstants {

    public static final String TEST_GROUP_A = "group-a";
    public static final String TEST_GROUP_B = "group-b";

    public static final String TEST_EXTENSION_IDENTIFIER = "oasis-smp-extension";
    public static final String TEST_RESOURCE_DEF_SMP10 = "oasis-smp-1";
    public static final String TEST_SUBRESOURCE_DEF_SMP10 = "services";
    public static final String TEST_RESOURCE_DEF_CPP = "oasis-cpp";


    public static final String TEST_DOMAIN_CODE_1 = "utestPeppol01";
    public static final String TEST_DOMAIN_CODE_2 = "utestEHhealth02";

    public static final String TEST_SML_SUBDOMAIN_CODE_1 = ""; // peppol subdomain is empty string
    public static final String TEST_SML_SUBDOMAIN_CODE_2 = "ehealth";



    public static final String TEST_SG_ID_1 = "0007:001:utest";
    public static final String TEST_SG_ID_2 = "urn:eu:ncpb:utest";
    public static final String TEST_SG_ID_3 = "0007:002:utest";
    public static final String TEST_SG_ID_4 = "0007:004:utest";
    public static final String TEST_SG_ID_NO_SCHEME = "No-Scheme-Party-Id";
    public static final String TEST_SG_ID_PL = "urn:poland:ncpb:utest";
    public static final String TEST_SG_ID_PL2 = "urn:Poland:ncpb";

    public static final String TEST_SG_SCHEMA_1 = "iso6523-actorid-upis";
    public static final String TEST_SG_SCHEMA_2 = "ehealth-actorid-qns";
    public static final String TEST_SG_SCHEMA_PL2 = "eHealth-participantId-qns";

    public static final String TEST_DOC_SCHEMA_1 = "busdox-docid-qns";
    public static final String TEST_DOC_SCHEMA_2 = "ehealth-resid-qns";
    public static final String TEST_DOC_SCHEMA_PL2 = "eHealth-resId-qns";

    public static final String TEST_DOC_ID_1 = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-12::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0";
    public static final String TEST_DOC_ID_2 = "docid.007";
    public static final String TEST_DOC_ID_PL2 = "DocId.007";


    public static final String TOKEN_PREFIX = "token-";
    public static final String USERNAME_1 = "test-user_001";
    public static final String USERNAME_2 = "test-user_002";
    public static final String USERNAME_3 = "test-user_003";
    public static final String USERNAME_4 = "test-user_004";
    public static final String USERNAME_5 = "test-user_005";
    public static final String USERNAME_TOKEN_1 = TOKEN_PREFIX + USERNAME_1;
    public static final String USERNAME_TOKEN_2 = TOKEN_PREFIX + USERNAME_2;
    public static final String USERNAME_TOKEN_3 = TOKEN_PREFIX + USERNAME_3;

    public static final String USER_CERT_1 = "CN=utest common name 01,O=org,C=BE:0000000000000066";
    public static final String USER_CERT_2 = "CN=utest common name 02,O=org,C=BE:0000000000000077";
    public static final String USER_CERT_3 = "CN=utest common name 03,O=org,C=BE:0000000000000077";


    public static final String SERVICE_GROUP_POLAND_XML_PATH = "/examples/services/ServiceGroupPoland.xml";
    public static final String SERVICE_GROUP_TEST2_XML_PATH = "/examples/services/ServiceGroupTestSgId2.xml";
    public static final String SERVICE_METADATA_XML_PATH = "/examples/services/ServiceMetadataPoland.xml";
    public static final String SIGNED_SERVICE_METADATA_XML_PATH = "/examples/services/SignedServiceMetadataPoland.xml";

    //public static final ParticipantIdentifierType SERVICE_GROUP_ID = new ParticipantIdentifierType("urn:eu:ncpb", "participant-scheme-qns");


    public static final String ADMIN_USERNAME = "test_admin";
    public static final String CERT_USER = "CN=common name,O=org,C=BE:0000000000000066";
    public static final String CERT_USER_ENCODED = "CN%3Dcommon%20name%2CO%3Dorg%2CC%3DBE%3A0000000000000066";

    // parameter: custom string as content
    public static final String SIMPLE_EXTENSION_XML ="<Extension xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\"><ex:dummynode xmlns:ex=\"http://test.eu\">Sample not mandatory extension: %s</ex:dummynode></Extension>";
    //5 parameters: ParticipantScheme, ParticipantIdentifier, DocumentScheme, DocumentIdentifier, custom value
    public static final String SIMPLE_DOCUMENT_XML = "<ServiceMetadata xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\"><ServiceInformation><ParticipantIdentifier scheme=\"%s\">%s</ParticipantIdentifier><DocumentIdentifier scheme=\"%s\">%s</DocumentIdentifier><ProcessList><Process><ProcessIdentifier scheme=\"cenbii-procid-ubl\">urn:www.cenbii.eu:profile:bii04:ver1.0</ProcessIdentifier><ServiceEndpointList><Endpoint transportProfile=\"bdxr-transport-ebms3-as4-v1p0\"><EndpointURI>http://localhost:8080/domibus-weblogic/services/msh</EndpointURI><RequireBusinessLevelSignature>true</RequireBusinessLevelSignature><ServiceActivationDate>2003-01-01T00:00:00</ServiceActivationDate><ServiceExpirationDate>2099-05-01T00:00:00</ServiceExpirationDate>" +
            "<Certificate>MIID7jCCA1egAwIBAgICA+YwDQYJKoZIhvcNAQENBQAwOjELMAkGA1UEBhMCRlIxEzARBgNVBAoMCklIRSBFdXJvcGUxFjAUBgNVBAMMDUlIRSBFdXJvcGUgQ0EwHhcNMTYwNjAxMTQzNTUzWhcNMjYwNjAxMTQzNTUzWjCBgzELMAkGA1UEBhMCUFQxDDAKBgNVBAoMA01vSDENMAsGA1UECwwEU1BNUzENMAsGA1UEKgwESm9hbzEOMAwGA1UEBRMFQ3VuaGExHTAbBgNVBAMMFHFhZXBzb3MubWluLXNhdWRlLnB0MRkwFwYDVQQMDBBTZXJ2aWNlIFByb3ZpZGVyMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1eN4qPSSRZqjVFG9TlcPlxf2WiSimQK9L1nf9Z/s0ezeGQjCukDeDq/Wzqd9fpHhaMMq+XSSOtyEtIr5K/As4kFrViONUUkG12J6UllSWogp0NYFwA4wIqKSFiTnQS5/nRTs05oONCCGILCyJNNeO53JzPlaq3/QbPLssuSAr6XucPE8wBBGM8b/TsB2G/zjG8yuSTgGbhaZekq/Vnf9ftj1fr/vJDDAQgH6Yvzd88Z0DACJPHfW1p4F/OWLI386Bq7g/bo1DUPAyEwlf+CkLgJWRKki3yJlOCIZ9enMA5O7rfeG3rXdgYGmWS7tNEgKXxgC+heiYvi7ZWd7M+/SUwIDAQABo4IBMzCCAS8wPgYDVR0fBDcwNTAzoDGgL4YtaHR0cHM6Ly9nYXplbGxlLmloZS5uZXQvcGtpL2NybC82NDMvY2FjcmwuY3JsMDwGCWCGSAGG+EIBBAQvFi1odHRwczovL2dhemVsbGUuaWhlLm5ldC9wa2kvY3JsLzY0My9jYWNybC5jcmwwPAYJYIZIAYb4QgEDBC8WLWh0dHBzOi8vZ2F6ZWxsZS5paGUubmV0L3BraS9jcmwvNjQzL2NhY3JsLmNybDAfBgNVHSMEGDAWgBTsMw4TyCJeouFrr0N7el3Sd3MdfjAdBgNVHQ4EFgQU1GQ/K1ykIwWFgiONzWJLQzufF/8wDAYDVR0TAQH/BAIwADAOBgNVHQ8BAf8EBAMCBSAwEwYDVR0lBAwwCgYIKwYBBQUHAwEwDQYJKoZIhvcNAQENBQADgYEAZ7t1Qkr9wz3q6+WcF6p/YX7Jr0CzVe7w58FvJFk2AsHeYkSlOyO5hxNpQbs1L1v6JrcqziNFrh2QKGT2v6iPdWtdCT8HBLjmuvVWxxnfzYjdQ0J+kdKMAEV6EtWU78OqL60CCtUZKXE/NKJUq7TTUCFP2fwiARy/t1dTD2NZo8c=</Certificate><ServiceDescription>Sample description of %s</ServiceDescription><TechnicalContactUrl>https://example.com</TechnicalContactUrl></Endpoint></ServiceEndpointList></Process></ProcessList></ServiceInformation></ServiceMetadata>";

    public static final String SIMPLE_REDIRECT_DOCUMENT_XML ="<ServiceMetadata xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\">" +
            "   <Redirect href=\"%s\">" +
            "    <CertificateUID>smptest</CertificateUID>" +
            "  </Redirect>" +
            "</ServiceMetadata>";

}
