package eu.europa.ec.edelivery.smp.utils;


import eu.europa.ec.edelivery.security.PreAuthenticatedCertificatePrincipal;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.*;

import javax.security.auth.x500.X500Principal;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class X509CertificateUtils {


    /**
     * Extracts all CRL distribution point URLs from the
     * "CRL Distribution Point" extension in a X.509 certificate. If CRL
     * distribution point extension is unavailable, returns an empty list.
     */
    public static List<String> getCrlDistributionPoints(X509Certificate cert) {
        byte[] crldpExt = cert.getExtensionValue(Extension.cRLDistributionPoints.getId());
        if (crldpExt == null) {
            return new ArrayList<>();
        }
        ASN1InputStream oAsnInStream = new ASN1InputStream(
                new ByteArrayInputStream(crldpExt));
        ASN1Primitive derObjCrlDP;
        try {
            derObjCrlDP = oAsnInStream.readObject();
        } catch (IOException e) {
            throw new SMPRuntimeException(ErrorCode.CERTIFICATE_ERROR, "Error while extracting CRL distribution point URLs", e);
        }
        DEROctetString dosCrlDP = (DEROctetString) derObjCrlDP;
        byte[] crldpExtOctets = dosCrlDP.getOctets();
        ASN1InputStream oAsnInStream2 = new ASN1InputStream(
                new ByteArrayInputStream(crldpExtOctets));
        ASN1Primitive derObj2;
        try {
            derObj2 = oAsnInStream2.readObject();
        } catch (IOException e) {
            throw new SMPRuntimeException(ErrorCode.CERTIFICATE_ERROR, "Error while extracting CRL distribution point URLs", e);
        }
        CRLDistPoint distPoint = CRLDistPoint.getInstance(derObj2);
        List<String> crlUrls = new ArrayList<>();
        for (DistributionPoint dp : distPoint.getDistributionPoints()) {
            DistributionPointName dpn = dp.getDistributionPoint();
            // Look for URIs in fullName
            if (dpn != null
                    && dpn.getType() == DistributionPointName.FULL_NAME) {
                GeneralName[] genNames = GeneralNames.getInstance(
                        dpn.getName()).getNames();
                // Look for an URI
                for (GeneralName genName : genNames) {
                    if (genName.getTagNo() == GeneralName.uniformResourceIdentifier) {
                        String url = DERIA5String.getInstance(
                                genName.getName()).getString();
                        crlUrls.add(url);
                    }
                }
            }
        }
        return crlUrls;
    }

    /**
     * Method retrieves https. If https does not exist it return http distribution list.
     * (LDAP is not allowed (FW OPEN) in targeted network)
     *
     * @param urlList
     * @return
     */
    public static String extractHttpCrlDistributionPoints(List<String> urlList) {
        String httpsUrl = null;
        String httpUrl = null;
        for (String url : urlList) {
            String newUrl = url.trim();
            if (newUrl.toLowerCase().startsWith("https://")) {
                httpsUrl = newUrl;
            } else if (newUrl.toLowerCase().startsWith("http://")) {
                httpUrl = newUrl;

            }
        }
        return httpsUrl == null ? httpUrl : httpsUrl;
    }


    public static PreAuthenticatedCertificatePrincipal extractPrincipalFromCertificate(X509Certificate cert) {

        String subject = cert.getSubjectX500Principal().getName(X500Principal.RFC2253);
        String issuer = cert.getIssuerX500Principal().getName(X500Principal.RFC2253);
        BigInteger serial = cert.getSerialNumber();

        return new PreAuthenticatedCertificatePrincipal(subject, issuer, serial, cert.getNotBefore(), cert.getNotAfter());
    }

    public static X509Certificate getX509Certificate(byte[] certBytes) {
        try {
            InputStream is = new ByteArrayInputStream(certBytes);
            return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(is);
        } catch (CertificateException exc) {
            throw new SMPRuntimeException(ErrorCode.CERTIFICATE_ERROR, String.format("The certificate is not valid - [%s]", exc.getMessage()), exc);
        }
    }

    public static X509Certificate getX509Certificate(String publicKey) {
        // if certificate has begin certificate - then is PEM encoded
        if (publicKey.contains("BEGIN CERTIFICATE")) {
            return getX509Certificate(publicKey.getBytes());
        } else {
            byte[] buff;
            // try do decode
            try {
                buff = Base64.getDecoder().decode(publicKey.getBytes());
            } catch (java.lang.IllegalArgumentException ex) {
                buff = publicKey.getBytes();
            }
            return getX509Certificate(buff);
        }
    }

}
