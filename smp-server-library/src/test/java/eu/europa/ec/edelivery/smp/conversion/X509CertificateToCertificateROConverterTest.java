package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.smp.api.Identifiers;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;

import javax.security.auth.x500.X500Principal;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

import static org.junit.Assert.*;


@RunWith(JUnitParamsRunner.class)
public class X509CertificateToCertificateROConverterTest {

    @Before
    public void setup(){
        Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);
    }


    private static final Object[] testCases() {
        return new Object[][]{
                // filename, subject, issuer, serial number, blueCoatHeader, certificateId
                {"cert-escaped-chars.pem", "C=BE,O=DIGIT,OU=CEF,CN=Escape characters \\,\\\\#\\+\\<\\>\\\"\\=", "C=BE,O=DIGIT,OU=CEF,CN=Escape characters \\,\\\\#\\+\\<\\>\\\"\\=","5c1bb275","sno=5c1bb275&subject=C%3DBE%2CO%3DDIGIT%2COU%3DCEF%2CCN%3DEscape+characters+%5C%2C%5C%5C%23%5C%2B%5C%3C%5C%3E%5C%22%5C%3D&validfrom=Dec+20+16%3A17%3A09+2018+GMT&validto=Dec+17+16%3A17%3A09+2028+GMT&issuer=C%3DBE%2CO%3DDIGIT%2COU%3DCEF%2CCN%3DEscape+characters+%5C%2C%5C%5C%23%5C%2B%5C%3C%5C%3E%5C%22%5C%3D","CN=Escape characters \\,\\\\\\#\\+\\<\\>\\\"\\=,O=DIGIT,C=BE:000000005c1bb275"},
                {"cert-nonAscii.pem", "C=BE,O=DIGIT,OU=CEF,CN=NonAscii chars:  àøýßĉæãäħ", "C=BE,O=DIGIT,OU=CEF,CN=NonAscii chars:  àøýßĉæãäħ","5c1bb38d","sno=5c1bb38d&subject=C%3DBE%2CO%3DDIGIT%2COU%3DCEF%2CCN%3DNonAscii+chars%3A++%C3%A0%C3%B8%C3%BD%C3%9F%C4%89%C3%A6%C3%A3%C3%A4%C4%A7&validfrom=Dec+20+16%3A21%3A49+2018+GMT&validto=Dec+17+16%3A21%3A49+2028+GMT&issuer=C%3DBE%2CO%3DDIGIT%2COU%3DCEF%2CCN%3DNonAscii+chars%3A++%C3%A0%C3%B8%C3%BD%C3%9F%C4%89%C3%A6%C3%A3%C3%A4%C4%A7","CN=NonAscii chars:  àøýßĉæãäħ,O=DIGIT,C=BE:000000005c1bb38d"},
                {"cert-with-email.pem", "C=BE,O=DIGIT,OU=CEF,CN=Cert with email", "C=BE,O=DIGIT,OU=CEF,CN=Cert with email","5c1bb358","sno=5c1bb358&subject=C%3DBE%2CO%3DDIGIT%2COU%3DCEF%2CCN%3DCert+with+email&validfrom=Dec+20+16%3A20%3A56+2018+GMT&validto=Dec+17+16%3A20%3A56+2028+GMT&issuer=C%3DBE%2CO%3DDIGIT%2COU%3DCEF%2CCN%3DCert+with+email","CN=Cert with email,O=DIGIT,C=BE:000000005c1bb358"},
                };
    }



    X509CertificateToCertificateROConverter testInstance = new X509CertificateToCertificateROConverter();

    @Test
    @Parameters(method = "testCases")
    public void testconvert(String filename,
                            String subject,
                            String issuer,
                            String serialNumber,
                            String blueCoat,
                            String certificateId) throws CertificateException {




        // given
        X509Certificate certificate =  getCertificate(filename);

        // when
        CertificateRO certRo = testInstance.convert(certificate);

        //then
        assertEquals(subject, certRo.getSubject());
        assertEquals(issuer, certRo.getIssuer());
        assertEquals(serialNumber, certRo.getSerialNumber());
        assertEquals(blueCoat, certRo.getBlueCoatHeader());
        assertEquals(certificateId, certRo.getCertificateId());
        assertNotNull(certRo.getEncodedValue());
        assertEquals(certificate.getNotBefore(), certRo.getValidFrom());
        assertEquals(certificate.getNotAfter(), certRo.getValidTo());

    }


    X509Certificate getCertificate(String filename) throws CertificateException {
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        InputStream is = X509CertificateToCertificateROConverterTest.class.getResourceAsStream("/certificates/" + filename);
        return (X509Certificate) fact.generateCertificate(is);

    }
}