package eu.europa.ec.cipa.smp.server.util;

import eu.europa.ec.cipa.smp.server.security.CertificateDetails;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by rodrfla on 29/11/2016.
 */
public class CommonUtil {

    public static Date convertStringToDate(String dateStr) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        return formatter.parse(dateStr);
    }

    public static X509Certificate createCertificateForX509(String serialNumber, String issuer, String subject, Date startDate, Date expiryDate) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream(Thread.currentThread().getContextClassLoader().getResource("keystore.jks").getFile()), "test".toCharArray());

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair key = keyGen.generateKeyPair();
        X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(new X500Name(issuer), new BigInteger(serialNumber, 16), startDate, expiryDate, new X500Name(subject), SubjectPublicKeyInfo.getInstance(key.getPublic().getEncoded()));

        //CRL Distribution Points
        DistributionPointName distPointOne = new DistributionPointName(new GeneralNames(
                new GeneralName(GeneralName.uniformResourceIdentifier, Thread.currentThread().getContextClassLoader().getResource("test.crl").toString())));

        DistributionPoint[] distPoints = new DistributionPoint[]{new DistributionPoint(distPointOne, null, null)};
        certBuilder.addExtension(Extension.cRLDistributionPoints, false, new CRLDistPoint(distPoints));

        ContentSigner sigGen = new JcaContentSignerBuilder("SHA1WithRSAEncryption").setProvider("BC").build(key.getPrivate());
        return new JcaX509CertificateConverter().setProvider("BC").getCertificate(certBuilder.build(sigGen));
    }

    public static CertificateDetails createCertificateForBlueCoat(String serialNumber, String issuer, String subject, Date startDate, Date expiryDate) throws Exception {
        DateFormat df = new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", Locale.US);
        String headerCertificate = "serial=" + serialNumber + "&subject=" + subject + "&validFrom=" + df.format(startDate) + "&validTo=" + df.format(expiryDate) + "&issuer=" + issuer;
        return CertificateUtils.getCommonNameFromCalculateHeaderCertificateId(headerCertificate);
    }

    public static String createHeaderCertificateForBlueCoat() throws Exception {
        return createHeaderCertificateForBlueCoat(null);
    }

    public static String createHeaderCertificateForBlueCoat(String tSubject) throws Exception {
        String serial = "123ABCD";
        // different order for the issuer certificate with extra spaces
        String issuer = "CN=PEPPOL SERVICE METADATA PUBLISHER TEST CA,  C=DK, O=NATIONAL IT AND TELECOM AGENCY,    OU=FOR TEST PURPOSES ONLY";
        String subject = "O=DG-DIGIT,C=BE,CN=SMP_123456789";
        if (!StringUtils.isEmpty(tSubject)) {
            subject = tSubject;
        }
        DateFormat df = new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", Locale.US);

        Calendar validFrom = Calendar.getInstance();
        validFrom.set(validFrom.get(Calendar.YEAR) - 2, 1, 1);
        Calendar validTo = Calendar.getInstance();
        validTo.set(validTo.get(Calendar.YEAR) + 3, 1, 1);

        return createHeaderCertificateForBlueCoat(serial, issuer, subject, validFrom.getTime(), validTo.getTime());
    }

    public static String createHeaderCertificateForBlueCoat(String serialNumber, String issuer, String subject, Date startDate, Date expiryDate) throws Exception {
        DateFormat df = new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", Locale.US);
        return "serial=" + serialNumber + "&subject=" + subject + "&validFrom=" + df.format(startDate) + "&validTo=" + df.format(expiryDate) + "&issuer=" + issuer;
    }
}
