/*
 * Copyright 2018 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.exceptions.DocumentSigningException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.annotation.PostConstruct;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static javax.xml.crypto.dsig.CanonicalizationMethod.INCLUSIVE;
import static javax.xml.crypto.dsig.DigestMethod.SHA256;
import static javax.xml.crypto.dsig.Transform.ENVELOPED;

@Component
public final class ServiceMetadataSigner {

    private static final Logger log = LoggerFactory.getLogger(ServiceMetadataSigner.class);

    private static final String RSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";

    @Value("${xmldsig.keystore.classpath}")
    private String keystoreFilePath;

    @Value("${xmldsig.keystore.password}")
    private String keystorePassword;

    @Value("${xmldsig.keystore.key.alias}")
    private String xmldsigKeystoreKeyAlias;

    @Value("${xmldsig.keystore.key.password}")
    private String xmldsigKeystoreKeyPassword;


    private Key signingKey;
    private X509Certificate signingCertificate;


    private static XMLSignatureFactory getDomSigFactory() {
        // According to Javadoc, only static methods of this factory are thread-safe
        // We cannot share and re-use the same instance in every place
        return XMLSignatureFactory.getInstance("DOM");
    }

    @PostConstruct
    public void init() {
        // Load the KeyStore and get the signing key and certificate.
        try (InputStream keystoreInputStream = new FileInputStream(keystoreFilePath)) {

            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(keystoreInputStream, keystorePassword.toCharArray());

            signingKey = keyStore.getKey(xmldsigKeystoreKeyAlias, xmldsigKeystoreKeyPassword.toCharArray());
            signingCertificate = (X509Certificate) keyStore.getCertificate(xmldsigKeystoreKeyAlias);

            log.info("Successfully loaded signing key and certificate: " + signingCertificate.getSubjectDN().getName());
        } catch (Exception e) {
            throw new IllegalStateException("Could not load signing certificate with private key from keystore file: " + keystoreFilePath, e);
        }
    }

    public void sign(Document serviceMetadataDoc) {
        try {
            XMLSignatureFactory domSigFactory = getDomSigFactory();

            // Create a Reference to the ENVELOPED document
            // URI "" means that the whole document is signed
            Reference reference = domSigFactory.newReference(
                    "",
                    domSigFactory.newDigestMethod(SHA256, null),
                    singletonList(domSigFactory.newTransform(ENVELOPED, (TransformParameterSpec) null)),
                    null,
                    null);

            SignedInfo singedInfo = domSigFactory.newSignedInfo(
                    domSigFactory.newCanonicalizationMethod(INCLUSIVE, (C14NMethodParameterSpec) null),
                    domSigFactory.newSignatureMethod(RSA_SHA256, null),
                    singletonList(reference));

            DOMSignContext domSignContext = new DOMSignContext(signingKey, serviceMetadataDoc.getDocumentElement());

            // Create the XMLSignature, but don't sign it yet
            KeyInfo keyInfo = createKeyInfo();
            XMLSignature signature = domSigFactory.newXMLSignature(singedInfo, keyInfo);

            // Marshal, generate, and sign the enveloped signature
            signature.sign(domSignContext);
        } catch (Exception e) {
            throw new DocumentSigningException("Could not sign serviceMetadata response", e);
        }
    }

    private KeyInfo createKeyInfo() {
        KeyInfoFactory keyInfoFactory = getDomSigFactory().getKeyInfoFactory();
        List content = new ArrayList();
        content.add(signingCertificate.getSubjectX500Principal().getName());
        content.add(signingCertificate);
        X509Data x509Data = keyInfoFactory.newX509Data(content);
        return keyInfoFactory.newKeyInfo(singletonList(x509Data));
    }

    public void setKeystoreFilePath(String keystoreFilePath) {
        this.keystoreFilePath = keystoreFilePath;
    }

    public void setKeystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    public void setXmldsigKeystoreKeyAlias(String xmldsigKeystoreKeyAlias) {
        this.xmldsigKeystoreKeyAlias = xmldsigKeystoreKeyAlias;
    }

    public void setXmldsigKeystoreKeyPassword(String xmldsigKeystoreKeyPassword) {
        this.xmldsigKeystoreKeyPassword = xmldsigKeystoreKeyPassword;
    }

}