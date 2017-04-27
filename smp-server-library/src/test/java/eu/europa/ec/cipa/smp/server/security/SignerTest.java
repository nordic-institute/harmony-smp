package eu.europa.ec.cipa.smp.server.security;

import eu.europa.ec.cipa.smp.server.AbstractTest;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.security.cert.X509Certificate;

/**
 * Created by rodrfla on 20/02/2017.
 */
public class SignerTest extends AbstractTest {

    private Document loadAndSignDocumentForDefault() throws Exception {
        Document documentToSign = SignatureUtil.loadDocument("/input/SignedServiceMetadata_withoutSignature.xml");
        Signer signatureSigner = new Signer(SignatureUtil.loadPrivateKey(), (X509Certificate) SignatureUtil.loadCertificate());
        signatureSigner.signXML(documentToSign.getDocumentElement());

        return documentToSign;
    }

    private void validateSignatureForDefault(Document document) throws Exception {
        Element smpSigPointer = SignatureUtil.findSignatureByParentNode(document.getDocumentElement());
        SignatureUtil.validateSignature(smpSigPointer);
    }


    private Element loadAndSignDocumentForAdmin(String filePath) throws Exception {
        Document response = SignatureUtil.loadDocument(filePath);
        Element smNode = SignatureUtil.findFirstElementByName(response, "ServiceMetadata");
        Document docUnwrapped = SignatureUtil.buildDocWithGivenRoot(smNode);
        Element adminSignature = SignatureUtil.findServiceInfoSig(docUnwrapped);

        return adminSignature;
    }

    @Test
    public void testDefaultSignatureOk() throws Exception {
        Document document = loadAndSignDocumentForDefault();
        validateSignatureForDefault(document);
    }

    @Test(expected = Exception.class)
    public void testDefaultSignatureNotOk() throws Exception {
        Document document = loadAndSignDocumentForDefault();
        String documentStr = SignatureUtil.marshall(document);
        documentStr = documentStr.replace("<Process>", "<Process><DummyElement></DummyElement>");
        validateSignatureForDefault(SignatureUtil.parseDocument(documentStr));
    }

    @Test
    public void testAdminSignatureOk() throws Exception {
        Element adminSignature = loadAndSignDocumentForAdmin("/expected_output/PUT_ServiceMetadata_request.xml");

        SignatureUtil.validateSignature(adminSignature);
    }

    @Test(expected = Exception.class)
    public void testAdminSignatureNotOk() throws Exception {
        Element adminSignature = loadAndSignDocumentForAdmin("/expected_output/PUT_ServiceMetadata_request_not_valid.xml");

        SignatureUtil.validateSignature(adminSignature);
    }
}
