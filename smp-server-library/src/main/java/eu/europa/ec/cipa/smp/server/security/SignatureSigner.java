package eu.europa.ec.cipa.smp.server.security;

import org.w3c.dom.Element;

import javax.annotation.Nonnull;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SignatureSigner {

    private final PrivateKey privateKey;
    private final X509Certificate certificate;

    public SignatureSigner(@Nonnull final PrivateKey privateKey,
                           @Nonnull final X509Certificate certificate) {
        this.privateKey = privateKey;
        this.certificate = certificate;

        if (this.privateKey == null) {
            throw new InvalidParameterException("Private key must be not null.");
        }

        if (this.certificate == null) {
            throw new InvalidParameterException("Certificate must be not null.");
        }
    }

    public void signXML(final Element aElementToSign) throws NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            MarshalException,
            XMLSignatureException {
        // Create a DOM XMLSignatureFactory that will be used to
        // generate the enveloped signature.
        final XMLSignatureFactory aSignatureFactory = XMLSignatureFactory.getInstance("DOM");

        // Create a Reference to the enveloped document (in this case,
        // you are signing the whole document, so a URI of "" signifies
        // that, and also specify the SHA1 digest algorithm and
        // the ENVELOPED Transform)
        final Reference aReference = aSignatureFactory.newReference("",
                aSignatureFactory.newDigestMethod(DigestMethod.SHA256,
                        null),
                Collections.singletonList(aSignatureFactory.newTransform(Transform.ENVELOPED,
                        (TransformParameterSpec) null)),
                null,
                null);

        // Create the SignedInfo.
        final String RSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
        final SignedInfo aSingedInfo = aSignatureFactory.newSignedInfo(aSignatureFactory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE,
                (C14NMethodParameterSpec) null),
                aSignatureFactory.newSignatureMethod(RSA_SHA256,
                        null),
                Collections.singletonList(aReference));

        // Create the KeyInfo containing the X509Data.
        final KeyInfoFactory aKeyInfoFactory = aSignatureFactory.getKeyInfoFactory();
        final List<Object> aX509Content = new ArrayList<Object>();
        aX509Content.add(certificate.getSubjectX500Principal().getName());
        aX509Content.add(certificate);
        final X509Data aX509Data = aKeyInfoFactory.newX509Data(aX509Content);
        final KeyInfo aKeyInfo = aKeyInfoFactory.newKeyInfo(Collections.singletonList(aX509Data));

        // Create a DOMSignContext and specify the RSA PrivateKey and
        // location of the resulting XMLSignature's parent element.
        final DOMSignContext dsc = new DOMSignContext(privateKey, aElementToSign);

        // Create the XMLSignature, but don't sign it yet.
        final XMLSignature signature = aSignatureFactory.newXMLSignature(aSingedInfo, aKeyInfo);

        // Marshal, generate, and sign the enveloped signature.
        signature.sign(dsc);
    }
}
