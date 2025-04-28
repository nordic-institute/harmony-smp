/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;


class CRLVerifierServiceTest {

    ConfigurationService mockConfigurationService = Mockito.mock(ConfigurationService.class);

    private CRLVerifierService testInstance = new CRLVerifierService(mockConfigurationService);

    @BeforeEach
    public void beforeMethods() {
        doReturn(true).when(mockConfigurationService).forceCRLValidation();
        // force verification
        testInstance = Mockito.spy(testInstance);
    }


    @Test
    void verifyCertificateCRLsTest() throws CertificateException, CRLException, IOException {
        // given
        X509Certificate certificate = loadCertificate("smp-crl-test-all.pem");

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509CRL crl = (X509CRL) cf.generateCRL(getClass().getResourceAsStream("/certificates/smp-crl-test.crl"));

        doReturn(crl).when(testInstance).getCRLByURL("https://localhost/clr");

        // when-then
        testInstance.verifyCertificateCRLs(certificate);
        // must not throw exception
    }

    @Test
    void verifyCertificateCRLRevokedTest() throws CertificateException, CRLException {
        // given
        X509Certificate certificate = loadCertificate("smp-crl-revoked.pem");

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509CRL crl = (X509CRL) cf.generateCRL(getClass().getResourceAsStream("/certificates/smp-crl-test.crl"));

        doReturn(crl).when(testInstance).getCRLByURL("https://localhost/crl");

        CertificateRevokedException result = assertThrows(CertificateRevokedException.class,
                () -> testInstance.verifyCertificateCRLs(certificate));
        assertThat(result.getMessage(), startsWith("Certificate has been revoked, reason: UNSPECIFIED"));
    }

    @Test
    void verifyCertificateCRLsX509FailsToConnectTest() throws CertificateException {
        // given
        X509Certificate certificate = loadCertificate("smp-crl-test-all.pem");
        // when
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class, () ->
                testInstance.verifyCertificateCRLs(certificate));
        // then
        assertThat(result.getMessage(),
                startsWith("Certificate error [Error occurred while downloading CRL:'https://localhost/clr']. Error: ConnectException: Connection refused (Connection refused)!"));
    }

    @Test
    void downloadCRLWrongUrlSchemeTest()  {

        X509CRL crl = testInstance.downloadCRL("wrong://localhost/crl", true);

        assertNull(crl);
    }

    @Test
    void downloadCRLUrlSchemeLdapTest()  {

        X509CRL crl = testInstance.downloadCRL("ldap://localhost/crl", true);

        assertNull(crl);
    }

    @Test
    void verifyCertificateCRLsRevokedSerialTest() throws CertificateException, CRLException {

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509CRL crl = (X509CRL) cf.generateCRL(getClass().getResourceAsStream("/certificates/smp-crl-test.crl"));

        doReturn(crl).when(testInstance).getCRLByURL("https://localhost/crl");

        CertificateRevokedException result = assertThrows(CertificateRevokedException.class, () -> testInstance.verifyCertificateCRLs("11", "https://localhost/crl"));
        assertThat(result.getMessage(), startsWith("Certificate has been revoked, reason: UNSPECIFIED"));
    }

    @Test
    void verifyCertificateCRLsRevokedSerialTestThrowIOExceptionHttps() {
        String crlURL = "https://localhost/crl";

        doThrow(new SMPRuntimeException(ErrorCode.CERTIFICATE_ERROR, "Can not download CRL '" + crlURL + "'", "IOException: Can not access URL"))
                .when(testInstance).getCRLByURL("https://localhost/crl");
        // when
        SMPRuntimeException result = assertThrows(SMPRuntimeException.class, () -> testInstance.verifyCertificateCRLs("11", "https://localhost/crl"));
        // then
        assertThat(result.getMessage(), startsWith("Certificate error [Can not download CRL 'https://localhost/crl']. Error: IOException: Can not access URL"));
    }

    @ParameterizedTest
    @CsvSource({
            "param1, true",
            "param1|param2, true",
            ", false",
            "'', false",
            "' |test', false",
            "test| |test, false",
    })
    void testIsValidParameter(String values, boolean expectedResult) {
        //given
        String[] parameters = StringUtils.split(values, '|');
        //when
        boolean result = testInstance.isValidParameter(parameters);
        //then
        assertEquals(expectedResult, result);
    }

    @Test
    void testDownloadURLViaProxy() throws IOException {
        //given
        String url = "https://localhost/crl";
        String proxy = "localhost";
        int proxyPort = 8080;
        String proxyUser = "user";
        String proxyPassword = "password";
        InputStream inputStream = Mockito.mock(InputStream.class);
        doReturn(inputStream).when(testInstance).execute(any(), any());
        //when
        InputStream result = testInstance.downloadURLViaProxy(url, proxy, proxyPort, proxyUser, proxyPassword);
        //then
        assertEquals(inputStream, result);
    }

    private X509Certificate loadCertificate(String filename) throws CertificateException {
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        return (X509Certificate)
                fact.generateCertificate(getClass().getResourceAsStream("/certificates/" + filename));
    }
}
