/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.cipa.smp.server.security;

import eu.europa.ec.cipa.smp.server.util.XmlTestUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sun.misc.IOUtils;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by gutowpa on 28/11/2016.
 */
public class SignatureUtil {

    private final static String keystorePath = Thread.currentThread().getContextClassLoader().getResource("signature_keys.jks").getFile();
    private static final String OASIS_NS = "http://docs.oasis-open.org/bdxr/ns/SMP/2016/05";
    private static final String KEYSTORE_PATH = "/signature_keys.jks";
    private static final String KEYSTORE_PASS = "mock";

    private static final String KEY_PAIR_NI_ALIAS = "sample_national_infrastructure";
    private static final String KEY_PAIR_NI_PASS = "mock";


    private static XMLSignatureFactory sigFactory;

    private static KeyStore.PrivateKeyEntry privateKeyEntry;
    private static KeyInfo keyInfo;

    private static void setupSigner(String keystoreResPath, String keystorePass, String keyPairAlias, String keyPairPass) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableEntryException, IOException {
        // Initialize all stuff needed for signing: Load keys from keystore and prepare signature factory
        sigFactory = XMLSignatureFactory.getInstance("DOM");
        KeyStore ks = KeyStore.getInstance("JKS");
        InputStream keystoreStream = SignatureUtil.class.getResourceAsStream(keystoreResPath);
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

    public static void sign(String refUri, Element xtPointer, String c14nMethod) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, MarshalException, XMLSignatureException, CertificateException, UnrecoverableEntryException, KeyStoreException, IOException {
        setupSigner(KEYSTORE_PATH, KEYSTORE_PASS, KEY_PAIR_NI_ALIAS, KEY_PAIR_NI_PASS);
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

    public static void validateSignature(Element sigPointer) throws Exception {
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

        // Create a DOMValidateContext and specify a KeySelector and document context.
        DOMValidateContext valContext = new DOMValidateContext(new X509KeySelector(), sigPointer);

        valContext.setProperty("javax.xml.crypto.dsig.cacheReference", Boolean.TRUE);
        // Unmarshal the XMLSignature.
        XMLSignature signature = fac.unmarshalXMLSignature(valContext);
        // Validate the XMLSignature.
        boolean coreValidity = signature.validate(valContext);

        Iterator i = signature.getSignedInfo().getReferences().iterator();
        for (int j = 0; i.hasNext(); j++) {
            InputStream is = ((Reference) i.next()).getDigestInputStream();
            // Display the data.
            byte[] a = IOUtils.readFully(is, 0, true);
            System.out.println(new String(a));
        }

        // Check core validation status.
        if (coreValidity == false) {
            printErrorDetails(valContext, signature);
            throw new Exception("+++ Signature not valild +++");
        } else {
            System.out.println("+++ Signature passed core validation +++");
        }

    }

    private static void printErrorDetails(DOMValidateContext valContext, XMLSignature signature) throws XMLSignatureException {
        System.err.println("Signature failed core validation");
        boolean sv = signature.getSignatureValue().validate(valContext);
        System.out.println("signature validation status: " + sv);
        if (sv == false) {
            // Check the validation status of each Reference.
            Iterator i1 = signature.getSignedInfo().getReferences().iterator();
            for (int j = 0; i1.hasNext(); j++) {
                boolean refValid = ((Reference) i1.next()).validate(valContext);
                System.out.println("ref[" + j + "] validity status: " + refValid);
            }
        }
    }

    public static KeyStore loadKeyStore() throws Exception {
        KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(new FileInputStream(keystorePath), KEYSTORE_PASS.toCharArray());
        return keystore;
    }

    public static PrivateKey loadPrivateKey() throws Exception {
        KeyStore keystore = loadKeyStore();
        String alias = "smp_mock";
        return (PrivateKey) keystore.getKey(alias, KEYSTORE_PASS.toCharArray());
    }

    public static java.security.cert.Certificate loadCertificate() throws Exception {
        KeyStore keystore = loadKeyStore();
        String alias = "smp_mock";
        return keystore.getCertificate(alias);
    }



    public static Element findServiceInfoSig(Document doc) throws ParserConfigurationException, SAXException, IOException {
        Element extension = findExtensionInServiceInformation(doc);
        return findSignatureByParentNode(extension);
    }

    public static Document buildDocWithGivenRoot(Element smNode) throws ParserConfigurationException, TransformerException, IOException, SAXException {
        Document docUnwrapped = XmlTestUtils.getDocumentBuilder().newDocument();
        Node sm = docUnwrapped.importNode(smNode, true);
        docUnwrapped.appendChild(sm);

        // Marshalling and parsing the document - signature validation fails without this stinky "magic".
        // _Probably_ SUN's implementation doesn't import correctly signatures between two different documents.
        String strUnwrapped = marshall(docUnwrapped);
        System.out.println(strUnwrapped);
        return parseDocument(strUnwrapped);
    }

    public static Element findSignatureByParentNode(Element sigParent) {
        for (Node child = sigParent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if ("Signature".equals(child.getLocalName()) && "http://www.w3.org/2000/09/xmldsig#".equals(child.getNamespaceURI())) {
                return (Element) child;
            }
        }
        throw new RuntimeException("Signature not found in given node.");
    }

    public static Document parseDocument(String docContent) throws IOException, SAXException, ParserConfigurationException {
        InputStream inputStream = new ByteArrayInputStream(docContent.getBytes());
        return XmlTestUtils.getDocumentBuilder().parse(inputStream);
    }

    public static Element findExtensionInServiceInformation(Document doc) throws ParserConfigurationException, SAXException, IOException {
        Element serviceInformation = findFirstElementByName(doc, "ServiceInformation");

        Element extension = null;
        for (Node child = serviceInformation.getFirstChild(); child != null; child = child.getNextSibling()) {
            if ("Extension".equals(child.getLocalName()) && OASIS_NS.equals(child.getNamespaceURI())) {
                extension = (Element) child;
            }
        }

        if (extension == null) {
            throw new RuntimeException("Could not find Extension in ServiceInformation tag.");
        }

        return extension;
    }

    public static Element findFirstElementByName(Document doc, String elementName) {
        NodeList elements = doc.getElementsByTagNameNS(OASIS_NS, elementName);
        return (Element) elements.item(0);
    }

    public static String marshall(Document doc) throws TransformerException, UnsupportedEncodingException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        trans.transform(new DOMSource(doc), new StreamResult(stream));
        return stream.toString("UTF-8");
    }

    public static String loadDocumentAsString(String docResourcePath) throws IOException {
        InputStream inputStream = SignatureUtil.class.getResourceAsStream(docResourcePath);
        return org.apache.commons.io.IOUtils.toString(inputStream, "UTF-8");
    }

}
