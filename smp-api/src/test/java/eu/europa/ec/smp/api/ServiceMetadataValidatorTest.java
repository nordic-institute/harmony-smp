package eu.europa.ec.smp.api;

import eu.europa.ec.smp.api.exceptions.XsdInvalidException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by migueti on 20/01/2017.
 */
public class ServiceMetadataValidatorTest {

    @Test
    public void testValidateXsdOk() throws XsdInvalidException {
        // given
        ServiceMetadataValidator validator = new ServiceMetadataValidator();

        // when
        boolean result = validator.validateXSD("<ServiceMetadata xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\">\n" +
                "    <ServiceInformation>\n" +
                "        <ParticipantIdentifier scheme=\"iso6523-actorid-upis\">0088:5798000000127</ParticipantIdentifier>\n" +
                "        <DocumentIdentifier scheme=\"iso6523-actorid-upis\">services:extended:epsos::107</DocumentIdentifier>\n" +
                "        <ProcessList>\n" +
                "            <Process>\n" +
                "                <ProcessIdentifier scheme=\"cenbii-procid-ubl\">urn:epsosPatientService::List</ProcessIdentifier>\n" +
                "                <ServiceEndpointList>\n" +
                "                    <Endpoint transportProfile=\"urn:ihe:iti:2013:xcpd\">\n" +
                "                        <EndpointURI>http://poland.pl/ncp/patient/list</EndpointURI>\n" +
                "                        <RequireBusinessLevelSignature>false</RequireBusinessLevelSignature>\n" +
                "                        <MinimumAuthenticationLevel>urn:epSOS:loa:1</MinimumAuthenticationLevel>\n" +
                "                        <ServiceActivationDate>2016-06-06T11:06:02.000</ServiceActivationDate>\n" +
                "                        <ServiceExpirationDate>2026-06-06T11:06:02</ServiceExpirationDate>\n" +
                "                        <Certificate>MIID7jCCA1egAwIBAgICA+YwDQYJKoZIhvcNAQENBQAwOjELMAkGA1UEBhMCRlIxEzARBgNVBAoMCklIRSBFdXJvcGUxFjAUBgNVBAMMDUlIRSBFdXJvcGUgQ0EwHhcNMTYwNjAxMTQzNTUzWhcNMjYwNjAxMTQzNTUzWjCBgzELMAkGA1UEBhMCUFQxDDAKBgNVBAoMA01vSDENMAsGA1UECwwEU1BNUzENMAsGA1UEKgwESm9hbzEOMAwGA1UEBRMFQ3VuaGExHTAbBgNVBAMMFHFhZXBzb3MubWluLXNhdWRlLnB0MRkwFwYDVQQMDBBTZXJ2aWNlIFByb3ZpZGVyMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1eN4qPSSRZqjVFG9TlcPlxf2WiSimQK9L1nf9Z/s0ezeGQjCukDeDq/Wzqd9fpHhaMMq+XSSOtyEtIr5K/As4kFrViONUUkG12J6UllSWogp0NYFwA4wIqKSFiTnQS5/nRTs05oONCCGILCyJNNeO53JzPlaq3/QbPLssuSAr6XucPE8wBBGM8b/TsB2G/zjG8yuSTgGbhaZekq/Vnf9ftj1fr/vJDDAQgH6Yvzd88Z0DACJPHfW1p4F/OWLI386Bq7g/bo1DUPAyEwlf+CkLgJWRKki3yJlOCIZ9enMA5O7rfeG3rXdgYGmWS7tNEgKXxgC+heiYvi7ZWd7M+/SUwIDAQABo4IBMzCCAS8wPgYDVR0fBDcwNTAzoDGgL4YtaHR0cHM6Ly9nYXplbGxlLmloZS5uZXQvcGtpL2NybC82NDMvY2FjcmwuY3JsMDwGCWCGSAGG+EIBBAQvFi1odHRwczovL2dhemVsbGUuaWhlLm5ldC9wa2kvY3JsLzY0My9jYWNybC5jcmwwPAYJYIZIAYb4QgEDBC8WLWh0dHBzOi8vZ2F6ZWxsZS5paGUubmV0L3BraS9jcmwvNjQzL2NhY3JsLmNybDAfBgNVHSMEGDAWgBTsMw4TyCJeouFrr0N7el3Sd3MdfjAdBgNVHQ4EFgQU1GQ/K1ykIwWFgiONzWJLQzufF/8wDAYDVR0TAQH/BAIwADAOBgNVHQ8BAf8EBAMCBSAwEwYDVR0lBAwwCgYIKwYBBQUHAwEwDQYJKoZIhvcNAQENBQADgYEAZ7t1Qkr9wz3q6+WcF6p/YX7Jr0CzVe7w58FvJFk2AsHeYkSlOyO5hxNpQbs1L1v6JrcqziNFrh2QKGT2v6iPdWtdCT8HBLjmuvVWxxnfzYjdQ0J+kdKMAEV6EtWU78OqL60CCtUZKXE/NKJUq7TTUCFP2fwiARy/t1dTD2NZo8c=</Certificate>\n" +
                "                        <ServiceDescription>This is the epSOS Patient Service List for the Polish NCP</ServiceDescription>\n" +
                "                        <TechnicalContactUrl>http://poland.pl/contact</TechnicalContactUrl>\n" +
                "                        <TechnicalInformationUrl>http://poland.pl/contact</TechnicalInformationUrl>\n" +
                "                    </Endpoint>\n" +
                "                </ServiceEndpointList>\n" +
                "            </Process>\n" +
                "        </ProcessList>\n" +
                "    </ServiceInformation>\n" +
                "</ServiceMetadata>");

        // then
        assertTrue(result);
    }

    @Test(expected = XsdInvalidException.class)
    public void testValidateXsdFail() throws XsdInvalidException {
        // given
        ServiceMetadataValidator validator = new ServiceMetadataValidator();

        // when
        try {
            validator.validateXSD("<ServiceMetadata xmlns=\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\">\n" +
                    "    <ServiceInformation>\n" +
                    "        <ParticipantIdentifier scheme=\"iso6523-actorid-upis\">0088:5798000000127</ParticipantIdentifier>\n" +
                    "        <DocumentIdentifier scheme=\"iso6523-actorid-upis\">services:extended:epsos::107</DocumentIdentifier>\n" +
                    "        <ProcessList>\n" +
                    "            <Process>\n" +
                    "                <ProcessIdentifier scheme=\"cenbii-procid-ubl\">urn:epsosPatientService::List</ProcessIdentifier>\n" +
                    "                <ServiceEndpointList>\n" +
                    "                    <Endpoint transportProfile=\"urn:ihe:iti:2013:xcpd\">\n" +
                    "                        <EndpointURI>http://poland.pl/ncp/patient/list</EndpointURI>\n" +
                    "                        <RequireBusinessLevelSignature>false</RequireBusinessLevelSignature>\n" +
                    "                        <MinimumAuthenticationLevel>urn:epSOS:loa:1</MinimumAuthenticationLevel>\n" +
                    "                        <ServiceActivationDate>2016-06-06T11:06:02.000</ServiceActivationDate>\n" +
                    "                        <ServiceExpirationDate>2026-06-06T11:06:02</ServiceExpirationDate>\n" +
                    "                        <Certificate>MIID7jCCA1egAwIBAgICA+YwDQYJKoZIhvcNAQENBQAwOjELMAkGA1UEBhMCRlIxEzARBgNVBAoMCklIRSBFdXJvcGUxFjAUBgNVBAMMDUlIRSBFdXJvcGUgQ0EwHhcNMTYwNjAxMTQzNTUzWhcNMjYwNjAxMTQzNTUzWjCBgzELMAkGA1UEBhMCUFQxDDAKBgNVBAoMA01vSDENMAsGA1UECwwEU1BNUzENMAsGA1UEKgwESm9hbzEOMAwGA1UEBRMFQ3VuaGExHTAbBgNVBAMMFHFhZXBzb3MubWluLXNhdWRlLnB0MRkwFwYDVQQMDBBTZXJ2aWNlIFByb3ZpZGVyMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1eN4qPSSRZqjVFG9TlcPlxf2WiSimQK9L1nf9Z/s0ezeGQjCukDeDq/Wzqd9fpHhaMMq+XSSOtyEtIr5K/As4kFrViONUUkG12J6UllSWogp0NYFwA4wIqKSFiTnQS5/nRTs05oONCCGILCyJNNeO53JzPlaq3/QbPLssuSAr6XucPE8wBBGM8b/TsB2G/zjG8yuSTgGbhaZekq/Vnf9ftj1fr/vJDDAQgH6Yvzd88Z0DACJPHfW1p4F/OWLI386Bq7g/bo1DUPAyEwlf+CkLgJWRKki3yJlOCIZ9enMA5O7rfeG3rXdgYGmWS7tNEgKXxgC+heiYvi7ZWd7M+/SUwIDAQABo4IBMzCCAS8wPgYDVR0fBDcwNTAzoDGgL4YtaHR0cHM6Ly9nYXplbGxlLmloZS5uZXQvcGtpL2NybC82NDMvY2FjcmwuY3JsMDwGCWCGSAGG+EIBBAQvFi1odHRwczovL2dhemVsbGUuaWhlLm5ldC9wa2kvY3JsLzY0My9jYWNybC5jcmwwPAYJYIZIAYb4QgEDBC8WLWh0dHBzOi8vZ2F6ZWxsZS5paGUubmV0L3BraS9jcmwvNjQzL2NhY3JsLmNybDAfBgNVHSMEGDAWgBTsMw4TyCJeouFrr0N7el3Sd3MdfjAdBgNVHQ4EFgQU1GQ/K1ykIwWFgiONzWJLQzufF/8wDAYDVR0TAQH/BAIwADAOBgNVHQ8BAf8EBAMCBSAwEwYDVR0lBAwwCgYIKwYBBQUHAwEwDQYJKoZIhvcNAQENBQADgYEAZ7t1Qkr9wz3q6+WcF6p/YX7Jr0CzVe7w58FvJFk2AsHeYkSlOyO5hxNpQbs1L1v6JrcqziNFrh2QKGT2v6iPdWtdCT8HBLjmuvVWxxnfzYjdQ0J+kdKMAEV6EtWU78OqL60CCtUZKXE/NKJUq7TTUCFP2fwiARy/t1dTD2NZo8c=</Certificate>\n" +
                    "                        <ServiceDescription>This is the epSOS Patient Service List for the Polish NCP</ServiceDescription>\n" +
                    "                        <TechnicalContactUrl>http://poland.pl/contact</TechnicalContactUrl>\n" +
                    "                        <TechnicalInformationUrl>http://poland.pl/contact</TechnicalInformationUrl>\n" +
                    "                    </Endpoint>\n" +
                    "                </ServiceEndpointList>\n" +
                    "                <Failed></Failed>" +
                    "            </Process>\n" +
                    "        </ProcessList>\n" +
                    "    </ServiceInformation>\n" +
                    "</ServiceMetadata>");
        } catch (XsdInvalidException ex) {
            // then
            assertEquals("cvc-complex-type.2.4.a: Invalid content was found starting with element 'Failed'. One of '{\"http://docs.oasis-open.org/bdxr/ns/SMP/2016/05\":Extension}' is expected.", ex.getMessage());
            throw ex;
        }
    }

}
