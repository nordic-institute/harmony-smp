/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.cipa.smp.server.security;

import org.junit.Test;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by gutowpa on 25/04/2017.
 */
public class EDeliveryX509AuthenticationFilterTest {

    String CERT_PEM = "MIICKzCCAZSgAwIBAgIEWP9xdDANBgkqhkiG9w0BAQsFADBOMQswCQYDVQQGEwJQ\n" +
            "TDEUMBIGA1UECgwLZ3V0ZWsgQ29ycC4xDjAMBgNVBAsMBURJR0lUMRkwFwYDVQQD\n" +
            "DBBsb2NhbGhvc3QgdG9tY2F0MB4XDTE3MDQyNTE1NTUzNFoXDTE4MDQyNTE1NTUz\n" +
            "NFowZjEhMB8GCSqGSIb3DQEJARYSZ3V0ZWtAYnV6aWFjemVrLnBsMQswCQYDVQQG\n" +
            "EwJHQjELMAkGA1UECgwCRUMxEjAQBgNVBAsMCURJR0lUIEQuMzETMBEGA1UEAwwK\n" +
            "RHVrZSBHdXRlazCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEApvtylARrq7wm\n" +
            "/G54zCnOB8jDcJ+dfBua8a0oX3efqfiWE2DsE+BGzJHbiXP57ynEEav2dz6I/pud\n" +
            "p0LKfgSsac13pRrEkeLK+WktjNpk0YP9QksC+dIfCJI///L/0+zlcL15Jb9yw8rd\n" +
            "kRERIf2h/44Htr/qoxnG7BBLDJNpSDECAwEAATANBgkqhkiG9w0BAQsFAAOBgQCA\n" +
            "WGul6avOGZNG6lNN3Y5B8sV8WtrZGpD1h3xW6WDKS8YdBkjfiEg9opqem5ayt4Rp\n" +
            "4BaVtEfpE8TlrkpaE5KHJ4TvBdgUNE83+FWDJ2vkgbAWnSYICLI0UGQKh2hayE2P\n" +
            "DgU4+tUu/W2vGvR5cvD6u6G9l+q2VxKhU6yOsqIqAg==";

    private static final String SUBJECT = "CN=Duke Gutek,O=EC,C=GB";
    private static final String NAME = SUBJECT + ":" + "0000000058ff7174";
    private static final String ISSUER = "CN=localhost tomcat,O=gutek Corp.,C=PL";

    private EDeliveryX509AuthenticationFilter filter = new EDeliveryX509AuthenticationFilter();

    @Test
    public void testBuildDetails() throws CertificateException {
        //given
        byte[] certBytes = Base64.decode(CERT_PEM.getBytes());
        Certificate cert = CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(certBytes));
        X509Certificate[] certs = Collections.singletonList(cert).toArray(new X509Certificate[0]);

        HttpServletRequest httpRequest = MockMvcRequestBuilders.put("https://localhost/")
                .requestAttr("javax.servlet.request.X509Certificate", certs)
                .buildRequest(new MockServletContext());

        //when
        PreAuthenticatedCertificatePrincipal principal = filter.buildDetails(httpRequest);

        //then
        assertEquals(SUBJECT, principal.getSubjectDN());
        assertEquals(ISSUER, principal.getIssuerDN());
        assertEquals(NAME, principal.getName());
    }

    @Test
    public void testBuildDetailsWithoutCert() {
        //given
        HttpServletRequest httpRequest = MockMvcRequestBuilders.put("https://localhost/")
                .buildRequest(new MockServletContext());

        //when
        PreAuthenticatedCertificatePrincipal principal = filter.buildDetails(httpRequest);

        //then
        assertNull(principal);
    }
}
