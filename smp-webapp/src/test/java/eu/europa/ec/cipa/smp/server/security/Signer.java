package eu.europa.ec.cipa.smp.server.security;

import org.w3c.dom.Element;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by gutowpa on 28/11/2016.
 */
public class Signer {

    private XMLSignatureFactory sigFactory;

    private KeyStore.PrivateKeyEntry privateKeyEntry;
    private KeyInfo keyInfo;

    public Signer(String keystoreResPath, String keystorePass, String keyPairAlias, String keyPairPass) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableEntryException, IOException {
        // Initialize all stuff needed for signing: Load keys from keystore and prepare signature factory
        sigFactory = XMLSignatureFactory.getInstance("DOM");
        KeyStore ks = KeyStore.getInstance("JKS");
        InputStream keystoreStream = this.getClass().getResourceAsStream(keystoreResPath);
        ks.load(keystoreStream, keystorePass.toCharArray());
        KeyStore.PasswordProtection passProtection = new KeyStore.PasswordProtection(keyPairPass.toCharArray());
        privateKeyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(keyPairAlias, passProtection);
        X509Certificate cert = (X509Certificate) privateKeyEntry.getCertificate();
        KeyInfoFactory keyInfoFactory = sigFactory.getKeyInfoFactory();
        List x509Content = new ArrayList();
        x509Content.add(cert.getSubjectX500Principal().getName());
        x509Content.add(cert);
        X509Data x509Data = keyInfoFactory.newX509Data(x509Content);
        keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(x509Data));
    }

    public void sign(String refUri, Element xtPointer, String c14nMethod) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, MarshalException, XMLSignatureException {

        Reference ref = sigFactory.newReference(
                refUri,
                sigFactory.newDigestMethod(DigestMethod.SHA256, null),
                Collections.singletonList(sigFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
                null,
                null);

        SignedInfo signedInfo = sigFactory.newSignedInfo(
                sigFactory.newCanonicalizationMethod(c14nMethod, (C14NMethodParameterSpec) null),
                sigFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
                Collections.singletonList(ref));

        DOMSignContext signContext = new DOMSignContext(privateKeyEntry.getPrivateKey(), xtPointer);

        // Create the XMLSignature, but don't sign it yet.
        XMLSignature signature = sigFactory.newXMLSignature(signedInfo, keyInfo);

        // Marshal, generate, and sign the enveloped signature.
        signature.sign(signContext);
    }
}
