/**
 * (C) Copyright 2018 - European Commission | CEF eDelivery
 * <p>
 * Licensed under the EUPL, Version 1.2 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * \BDMSL\bdmsl-parent-pom\LICENSE-EUPL-v1.2.pdf or https://joinup.ec.europa.eu/sites/default/files/custom-page/attachment/eupl_v1.2_en.pdf
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package eu.europa.ec.edelivery.smp.utils;


import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(JUnitParamsRunner.class)
public class X509CertificateUtilsTest {


    @BeforeClass
    public static void beforeClass() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    private static final Object[] crlTestListCases() {
        return new Object[][]{
                {"smp-crl-test-all.pem", "https://localhost/clr,http://localhost/clr,ldap://localhost/clr"},
                {"smp-crl-test-https.pem", "https://localhost/clr"},
                {"smp-crl-test-ldap.pem", "ldap://localhost/clr"},
                {"smp-crl-test-nolist.pem", null},
        };
    }

    private static final Object[] crlExtractHTTPSTestListCases() {
        return new Object[][]{
                {"ldap://localhost/clr,https://localhost/clr,http://localhost/clr","https://localhost/clr"},
                { "https://localhost/clr","https://localhost/clr"},
                { "http://localhost/clr","http://localhost/clr"},
                { "ldap://localhost/clr", null},
                {"", null},
        };
    }

    private static final Object[] parseTestCases() {
        return new Object[][]{
                {"certificate-pem-with-header.cer"},
                {"PeppolTestSMP-DER-encoded.crt"},
                {"PeppolTestSMP-PEM-encoded.pem"},
                {"PeppolTestSMP-PEM-encoded-CRLF.txt"},
        };
    }

    @Test
    @Parameters(method = "parseTestCases")
    public void parseCertificateTest(String certificateFileName) throws CertificateException, IOException {
        //given
        byte[] buff = getBytes(certificateFileName);

        X509Certificate certificate = X509CertificateUtils.getX509Certificate(buff);

        assertNotNull(certificate);

    }


    @Test
    @Parameters(method = "crlTestListCases")
    public void getCrlDistributionPointsTest(String certificatFileName, String clrLists) throws CertificateException {
        //given
        X509Certificate certificate = loadCertificate(certificatFileName);
        List<String> lstExpected = clrLists == null ? Collections.emptyList() : Arrays.asList(clrLists.split(","));
        //when
        List<String> lstValues = X509CertificateUtils.getCrlDistributionPoints(certificate);
        // then
        assertEquals(lstExpected.size(), lstValues.size());
        lstValues.forEach(crl -> {
            lstExpected.contains(crl);
        });
    }



    @Test
    @Parameters(method = "crlExtractHTTPSTestListCases")
    public void extractHttpCrlDistributionPoints(String clrLists, String value){
        //given
        List<String> urlList = clrLists == null ? Collections.emptyList() : Arrays.asList(clrLists.split(","));
        // when
        String url = X509CertificateUtils.extractHttpCrlDistributionPoint(urlList);
        // then
        assertEquals(value, url);
    }



    public static X509Certificate loadCertificate(String filename) throws CertificateException {
        CertificateFactory fact = CertificateFactory.getInstance("X.509");

        X509Certificate cer = (X509Certificate)
                fact.generateCertificate(X509CertificateUtilsTest.class.getResourceAsStream("/certificates/" + filename));
        return cer;
    }

    public static byte[] getBytes(String filename) throws CertificateException, IOException {
        return IOUtils.toByteArray(X509CertificateUtilsTest.class.getResourceAsStream("/certificates/" + filename));
    }



}
