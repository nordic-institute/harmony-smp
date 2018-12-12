package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.smp.api.Identifiers;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Base64;

import static org.junit.Assert.*;


@RunWith(JUnitParamsRunner.class)
public class X509CertificateToCertificateROConverterTest {

    private static final Object[] testCases() {
        return new Object[][]{
                // alias, subject, issuer, serial number, blueCoatHeader, certificateId
                {"alias", "subject", "issuer","serialNumber","blueCoat","certificateId"},
                };
    }


    X509CertificateToCertificateROConverter testInstance = new X509CertificateToCertificateROConverter();

    @Test
    @Parameters(method = "testCases")
    public void testconvert(String alias,
                            String subject,
                            String issuer,
                            String serialNumber,
                            String blueCoat,
                            String certificateId) throws CertificateEncodingException {
       /*
        // given
        X509Certificate certificate =  getCertificate(alias);

        // when
        CertificateRO certRo = testInstance.convert(certificate);

        //then
        assertEquals(subject, certRo.getSubject());
        assertEquals(issuer, certRo.getIssuer());
        assertEquals(serialNumber, certRo.getSerialNumber());
        assertEquals(blueCoat, certRo.getBlueCoatHeader());
        assertEquals(certificateId, certRo.getCertificateId());
        assertEquals(Base64.getEncoder().encode(certificate.getEncoded()), certRo.getEncodedValue());
        assertEquals(certificate.getNotBefore(), certRo.getValidFrom());
        assertEquals(certificate.getNotAfter(), certRo.getValidTo());
        */
    }

    @Test
    public void convert() {
    }

    @Test
    public void getCertificateIdFromCertificate() {
    }

    X509Certificate getCertificate(String alias){
        X509Certificate cert = null;
        return cert;
    }
}