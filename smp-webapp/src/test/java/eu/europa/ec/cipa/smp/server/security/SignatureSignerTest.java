package eu.europa.ec.cipa.smp.server.security;

import eu.europa.ec.cipa.smp.server.AbstractTest;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.security.cert.X509Certificate;

/**
 * Created by rodrfla on 20/02/2017.
 */
public class SignatureSignerTest extends AbstractTest {

    @Test
    public void testSignOk() throws Exception {
        Document documentToSign = SignatureUtil.loadDocument("/input/SignedServiceMetadata_withoutSignature.xml");
        SignatureSigner signatureSigner = new SignatureSigner(SignatureUtil.loadPrivateKey(), (X509Certificate) SignatureUtil.loadCertificate());
        signatureSigner.signXML(documentToSign.getDocumentElement());

        Element smpSigPointer = SignatureUtil.findSignatureByParentNode(documentToSign.getDocumentElement());
        SignatureUtil.validateSignature(smpSigPointer);
    }

    @Test(expected = Exception.class)
    public void testSignNotOk() throws Exception {
        Document documentToSign = SignatureUtil.loadDocument("/input/SignedServiceMetadata_withoutSignature.xml");
        SignatureSigner signatureSigner = new SignatureSigner(SignatureUtil.loadPrivateKey(), (X509Certificate) SignatureUtil.loadCertificate());
        signatureSigner.signXML(documentToSign.getDocumentElement());
        String signedDocument = SignatureUtil.marshall(documentToSign);
        signedDocument = signedDocument.replace("<Process>", "<Process><DummyElement></DummyElement>");

        Element smpSigPointer = SignatureUtil.findSignatureByParentNode(SignatureUtil.parseDocument(signedDocument).getDocumentElement());
        SignatureUtil.validateSignature(smpSigPointer);
    }
}
