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
                {"cert-escaped-chars.pem", "CN=Escape characters \\,\\\\\\#\\+\\<\\>\\\"\\=,OU=CEF,O=DIGIT,C=BE", "CN=Escape characters \\,\\\\\\#\\+\\<\\>\\\"\\=,OU=CEF,O=DIGIT,C=BE","5c1bb275","sno=5c1bb275&subject=CN%3DEscape+characters+%5C%2C%5C%5C%5C%23%5C%2B%5C%3C%5C%3E%5C%22%5C%3D%2COU%3DCEF%2CO%3DDIGIT%2CC%3DBE&validfrom=Dec+20+16%3A17%3A09+2018+GMT&validto=Dec+17+16%3A17%3A09+2028+GMT&issuer=CN%3DEscape+characters+%5C%2C%5C%5C%5C%23%5C%2B%5C%3C%5C%3E%5C%22%5C%3D%2COU%3DCEF%2CO%3DDIGIT%2CC%3DBE","CN=Escape characters \\,\\\\\\#\\+\\<\\>\\\"\\=,O=DIGIT,C=BE:000000005c1bb275"},
                {"cert-nonAscii.pem", "CN=NonAscii chars:  àøýßĉæãäħ,OU=CEF,O=DIGIT,C=BE", "CN=NonAscii chars:  àøýßĉæãäħ,OU=CEF,O=DIGIT,C=BE","5c1bb38d","sno=5c1bb38d&subject=CN%3DNonAscii+chars%3A++%C3%A0%C3%B8%C3%BD%C3%9F%C4%89%C3%A6%C3%A3%C3%A4%C4%A7%2COU%3DCEF%2CO%3DDIGIT%2CC%3DBE&validfrom=Dec+20+16%3A21%3A49+2018+GMT&validto=Dec+17+16%3A21%3A49+2028+GMT&issuer=CN%3DNonAscii+chars%3A++%C3%A0%C3%B8%C3%BD%C3%9F%C4%89%C3%A6%C3%A3%C3%A4%C4%A7%2COU%3DCEF%2CO%3DDIGIT%2CC%3DBE","CN=NonAscii chars:  aøyßcæaaħ,O=DIGIT,C=BE:000000005c1bb38d"},
                {"cert-with-email.pem", "CN=Cert with email,OU=CEF,O=DIGIT,C=BE", "CN=Cert with email,OU=CEF,O=DIGIT,C=BE","5c1bb358","sno=5c1bb358&subject=CN%3DCert+with+email%2COU%3DCEF%2CO%3DDIGIT%2CC%3DBE&validfrom=Dec+20+16%3A20%3A56+2018+GMT&validto=Dec+17+16%3A20%3A56+2028+GMT&issuer=CN%3DCert+with+email%2COU%3DCEF%2CO%3DDIGIT%2CC%3DBE","CN=Cert with email,O=DIGIT,C=BE:000000005c1bb358"},
                {"cert-smime.pem", "C=BE,O=European Commission,OU=PEPPOL TEST SMP,CN=edelivery_sml", "CN=PEPPOL SERVICE METADATA PUBLISHER TEST CA - G2,OU=FOR TEST ONLY,O=OpenPEPPOL AISBL,C=BE","3cfe6b37e4702512c01e71f9b9175464","sno=3cfe6b37e4702512c01e71f9b9175464&subject=C%3DBE%2CO%3DEuropean+Commission%2COU%3DPEPPOL+TEST+SMP%2CCN%3Dedelivery_sml&validfrom=Sep+21+02%3A00%3A00+2018+GMT&validto=Sep+11+01%3A59%3A59+2020+GMT&issuer=CN%3DPEPPOL+SERVICE+METADATA+PUBLISHER+TEST+CA+-+G2%2COU%3DFOR+TEST+ONLY%2CO%3DOpenPEPPOL+AISBL%2CC%3DBE","CN=edelivery_sml,O=European Commission,C=BE:3cfe6b37e4702512c01e71f9b9175464"},

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