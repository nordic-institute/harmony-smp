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
package eu.europa.ec.edelivery.smp.utils;


import eu.europa.ec.edelivery.security.utils.X509CertificateUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


import java.io.IOException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author Joze Rihtarsic
 * @since 4.1
 */

public class X509CertificateUtilsTest {


    @BeforeAll
    public static void beforeClass() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    private static Object[] crlTestListCases() {
        return new Object[][]{
                {"smp-crl-test-all.pem", "https://localhost/clr,http://localhost/clr,ldap://localhost/clr"},
                {"smp-crl-test-https.pem", "https://localhost/clr"},
                {"smp-crl-test-ldap.pem", "ldap://localhost/clr"},
                {"smp-crl-test-nolist.pem", null},
        };
    }

    private static Object[] crlExtractHTTPSTestListCases() {
        return new Object[][]{
                {"ldap://localhost/clr,https://localhost/clr,http://localhost/clr", "https://localhost/clr"},
                {"https://localhost/clr", "https://localhost/clr"},
                {"http://localhost/clr", "http://localhost/clr"},
                {"ldap://localhost/clr", null},
                {"", null},
        };
    }

    private static Object[] parseTestCases() {
        return new Object[][]{
                {"certificate-pem-with-header.cer"},
                {"PeppolTestSMP-DER-encoded.crt"},
                {"PeppolTestSMP-PEM-encoded.pem"},
                {"PeppolTestSMP-PEM-encoded-CRLF.txt"},
        };
    }

    @ParameterizedTest
    @MethodSource("parseTestCases")
    void parseCertificateTest(String certificateFileName) throws CertificateException, IOException {
        //given
        byte[] buff = getBytes(certificateFileName);

        X509Certificate certificate = X509CertificateUtils.getX509Certificate(buff);

        assertNotNull(certificate);

    }

    @ParameterizedTest
    @MethodSource("crlTestListCases")
    void getCrlDistributionPointsTest(String certificatFileName, String clrLists) throws CertificateException {
        //given
        X509Certificate certificate = loadCertificate(certificatFileName);
        List<String> lstExpected = clrLists == null ? Collections.emptyList() : Arrays.asList(clrLists.split(","));
        //when
        List<String> lstValues = X509CertificateUtils.getCrlDistributionPoints(certificate);
        // then
        assertEquals(lstExpected.size(), lstValues.size());
        lstValues.forEach(crl -> lstExpected.contains(crl));
    }

    @ParameterizedTest
    @MethodSource("crlExtractHTTPSTestListCases")
    void extractHttpCrlDistributionPoints(String clrLists, String value) {
        //given
        List<String> urlList = clrLists == null ? Collections.emptyList() : Arrays.asList(clrLists.split(","));
        // when
        String url = X509CertificateUtils.extractHttpCrlDistributionPoint(urlList);
        // then
        assertEquals(value, url);
    }

    public static X509Certificate loadCertificate(String filename) throws CertificateException {
        CertificateFactory fact = CertificateFactory.getInstance("X.509");

        return (X509Certificate) fact.generateCertificate(
                X509CertificateUtilsTest.class.getResourceAsStream("/certificates/" + filename));
    }

    public static byte[] getBytes(String filename) throws IOException {
        return IOUtils.toByteArray(X509CertificateUtilsTest.class.getResourceAsStream("/certificates/" + filename));
    }
}
