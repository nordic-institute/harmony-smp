package eu.europa.ec.cipa.bdmsl.service;

import eu.europa.ec.cipa.bdmsl.AbstractTest;
import eu.europa.ec.cipa.common.exception.BusinessException;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * Created by feriaad on 22/06/2015.
 */
public class X509CertificateServiceImplTest extends AbstractTest {

    @Autowired
    private IX509CertificateService x509CertificateService;

    private String keystorePath = Thread.currentThread().getContextClassLoader().getResource("keystore.jks").getFile();
    private String keystorePassword = "test";

    @BeforeClass
    public static void beforeClass(){
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    /**
     * Valid certificate
     *
     * @throws TechnicalException
     * @throws BusinessException
     */
    @Test
    public void testIsBlueCoatClientCertificateValid() throws TechnicalException, BusinessException, IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(keystorePath), keystorePassword.toCharArray());
        X509Certificate cert = (X509Certificate) ks.getCertificate("senderalias");
        Assert.assertTrue(x509CertificateService.isClientX509CertificateValid(new X509Certificate[]{cert}));
    }


    @Test
    public void testIsBlueCoatClientCertificateRevoked() throws Exception {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(keystorePath), keystorePassword.toCharArray());
        X509Certificate cert = (X509Certificate) ks.getCertificate("senderalias");
        Date startDate = new Date();
        Date expiryDate = new Date();
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair key = keyGen.generateKeyPair();
        String revokedSerial = "0400000000011E44A5E404";
        X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(new X500Name("C=BE, O=GlobalSign nv-sa, OU=Root CA, CN=GlobalSign Root CA"), new BigInteger(revokedSerial, 16), startDate, expiryDate, new X500Name("CN=RevokedSubject"), SubjectPublicKeyInfo.getInstance(key.getPublic().getEncoded()));

        //CRL Distribution Points
        DistributionPointName distPointOne = new DistributionPointName(new GeneralNames(
                new GeneralName(GeneralName.uniformResourceIdentifier,Thread.currentThread().getContextClassLoader().getResource("test.crl").toString())));

        DistributionPoint[] distPoints = new DistributionPoint[] {new DistributionPoint(distPointOne, null, null)};
        certBuilder.addExtension(Extension.cRLDistributionPoints, false, new CRLDistPoint(distPoints));

        ContentSigner sigGen = new JcaContentSignerBuilder("SHA1WithRSAEncryption").setProvider("BC").build(key.getPrivate());
        X509Certificate certificate = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certBuilder.build(sigGen));

        Assert.assertFalse(x509CertificateService.isClientX509CertificateValid(new X509Certificate[]{certificate}));

    }

}
