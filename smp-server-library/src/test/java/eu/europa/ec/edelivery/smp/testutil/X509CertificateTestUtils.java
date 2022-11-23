package eu.europa.ec.edelivery.smp.testutil;

import eu.europa.ec.edelivery.security.utils.CertificateKeyType;
import eu.europa.ec.edelivery.security.utils.X509CertificateUtils;
import org.bouncycastle.asn1.x509.KeyUsage;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

public class X509CertificateTestUtils {

    public static X509Certificate createX509CertificateForTest(String subject) throws Exception {
        return createX509CertificateForTest(null, subject, subject,
                OffsetDateTime.now().minusDays(1),
                OffsetDateTime.now().plusYears(1), Collections.emptyList());
    }

    public static X509Certificate createX509CertificateForTest(String serialNumber,
                                                               String issuer, String subject,
                                                               OffsetDateTime startDate, OffsetDateTime expiryDate,
                                                               List<String> distributionList) throws Exception {

        KeyPair key = X509CertificateUtils.generateKeyPair(CertificateKeyType.RSA_2048);
        return X509CertificateUtils.generateCertificate(
                serialNumber == null ? BigInteger.TEN : new BigInteger(serialNumber, 16), key.getPublic(), subject, startDate, expiryDate, issuer,
                key.getPrivate(), false, -1, null,
                distributionList, Collections.emptyList(), Collections.emptyList());
    }


    public static X509Certificate createX509CertificateForTest(String subject, BigInteger serial, List<String> listOfPolicyOIDs) throws Exception {

        KeyPair key = X509CertificateUtils.generateKeyPair(CertificateKeyType.RSA_2048);
        KeyUsage usage = new KeyUsage(244);
        X509Certificate cert = X509CertificateUtils.generateCertificate(serial,
                key.getPublic(), subject, OffsetDateTime.now().minusDays(1L),
                OffsetDateTime.now().plusYears(5L), (String) null,
                key.getPrivate(), false, -1, usage,
                Collections.emptyList(), Collections.emptyList(),
                listOfPolicyOIDs);

        return cert;
    }
}
