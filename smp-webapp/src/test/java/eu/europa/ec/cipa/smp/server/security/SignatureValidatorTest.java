package eu.europa.ec.cipa.smp.server.security;

import eu.europa.ec.cipa.smp.server.AbstractTest;
import eu.europa.ec.cipa.smp.server.services.readwrite.ServiceMetadataInterface;
import eu.europa.ec.cipa.smp.server.util.DefaultHttpHeader;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.Arrays;

/**
 * Created by rodrfla on 08/02/2017.
 */
public class SignatureValidatorTest extends AbstractTest {

    private static final String OASIS_NS = "http://docs.oasis-open.org/bdxr/ns/SMP/2016/05";

    private static Signer NATIONAL_INFRASTRUCTURE_SIGNER;
    private static Signer SMP_SIGNER;

    private static final String C14N_METHOD = CanonicalizationMethod.INCLUSIVE;

    private static final String KEYSTORE_PATH = "/signature_keys.jks";
    private static final String KEYSTORE_PASS = "mock";

    private static final String KEY_PAIR_NI_ALIAS = "sample_national_infrastructure";
    private static final String KEY_PAIR_NI_PASS = "mock";

    private static final String KEY_PAIR_SMP_ALIAS = "smp_mock";
    private static final String KEY_PAIR_SMP_PASS = "mock";

    @Before
    public void setUp() throws Exception {
        NATIONAL_INFRASTRUCTURE_SIGNER = new Signer(KEYSTORE_PATH, KEYSTORE_PASS, KEY_PAIR_NI_ALIAS, KEY_PAIR_NI_PASS);
        SMP_SIGNER = new Signer(KEYSTORE_PATH, KEYSTORE_PASS, KEY_PAIR_SMP_ALIAS, KEY_PAIR_SMP_PASS);
    }

    @Test
    public void validateSignatures() throws Throwable {
        String serviceGroupId = "ehealth-actorid-qns::urn:brazil:ncpb";
        String documentTypeId = "ehealth-resid-qns::urn::epsos##services:extended:epsos::107";
        ServiceMetadataInterface serviceMetadataInterface = new ServiceMetadataInterface();
        String clientCertHeader = "sno=f7%3A1e%3Ae8%3Ab1%3A1c%3Ab3%3Ab7%3A87&amp;subject=C%3DBE%2C+O%3DEuropean+Commission%2C+OU%3DCEF_eDelivery.europa.eu%2C+OU%3DeHealth%2C+OU%3DSMP_TEST%2C+CN%3DEHEALTH_SMP_EC%2FemailAddress%3DCEF-EDELIVERY-SUPPORT%40ec.europa.eu&amp;validfrom=Dec++6+17%3A41%3A42+2016+GMT&amp;validto=Jul++9+23%3A59%3A00+2019+GMT&amp;issuer=C%3DDE%2C+O%3DT-Systems+International+GmbH%2C+OU%3DT-Systems+Trust+Center%2C+ST%3DNordrhein+Westfalen%2FpostalCode%3D57250%2C+L%3DNetphen%2Fstreet%3DUntere+Industriestr.+20%2C+CN%3DShared+Business+CA+4&amp;policy_oids=1.3.6.1.4.1.7879.13.25\n";
        DefaultHttpHeader defaultHttpHeader = new DefaultHttpHeader();
        defaultHttpHeader.addRequestHeader("Client-Cert", Arrays.asList(clientCertHeader));
        SecurityContextHolder.getContext().setAuthentication(new BlueCoatClientCertificateAuthentication(clientCertHeader));
        serviceMetadataInterface.setHeaders(defaultHttpHeader);

        //Sign w/ Customized Signature
        Document docPutRequest = loadDocument("/input/ServiceMetadata.xml");
        Element serviceInfExtension = findExtensionInServiceInformation(docPutRequest);
        NATIONAL_INFRASTRUCTURE_SIGNER.sign("", serviceInfExtension, C14N_METHOD);
        String signedByCustomizedSignature = marshall(docPutRequest);

        //Validate Customized Signature
        Element smNode = findFirstElementByName(docPutRequest, "ServiceMetadata");
        Document docUnwrapped = buildDocWithGivenRoot(smNode);
        Element siSigPointer = findServiceInfoSig(docUnwrapped);
        SignatureValidator.validateSignature(siSigPointer);

        //Save ServiceMetadata
        serviceMetadataInterface.saveServiceRegistration(serviceGroupId, documentTypeId, marshall(docPutRequest));

        //Retrieve saved ServiceMetadata
        Document response = serviceMetadataInterface.getServiceRegistration(serviceGroupId, documentTypeId);

        //Sign w/ Default Signature
        SMP_SIGNER.sign("", response.getDocumentElement(), C14N_METHOD);

        //Validate Default Signature
        Element smpSigPointer = findSignatureByParentNode(response.getDocumentElement());
        SignatureValidator.validateSignature(smpSigPointer);

        //Check signed document
        Assert.assertEquals(signedByCustomizedSignature, loadDocumentAsString("/expected_output/PUT_ServiceMetadata_request.xml"));
        Assert.assertEquals(marshall(response), loadDocumentAsString("/expected_output/GET_SignedServiceMetadata_response.xml"));
    }

    @Test
    public void validateCustomizedSignature() throws Throwable {
        Document docPutRequest = loadDocument("/input/ServiceMetadata.xml");
        Element serviceInfExtension = findExtensionInServiceInformation(docPutRequest);
        NATIONAL_INFRASTRUCTURE_SIGNER.sign("", serviceInfExtension, C14N_METHOD);
        String signedDocument = marshall(docPutRequest);

        Element smNode = findFirstElementByName(docPutRequest, "ServiceMetadata");
        Document docUnwrapped = buildDocWithGivenRoot(smNode);
        Element siSigPointer = findServiceInfoSig(docUnwrapped);

        SignatureValidator.validateSignature(siSigPointer);
        Assert.assertEquals(loadDocumentAsString("/expected_output/PUT_ServiceMetadata_request.xml"), signedDocument);
    }

    @Test
    public void validateDefaultSignature() throws Throwable {
        Document docuToSign = loadDocument("/input/SignedServiceMetadata_withoutSignature.xml");
        SMP_SIGNER.sign("", docuToSign.getDocumentElement(), C14N_METHOD);

        Element smpSigPointer = findSignatureByParentNode(docuToSign.getDocumentElement());

        SignatureValidator.validateSignature(smpSigPointer);
    }

    private Element findServiceInfoSig(Document doc) throws ParserConfigurationException, SAXException, IOException {
        Element extension = findExtensionInServiceInformation(doc);
        return findSignatureByParentNode(extension);
    }

    private Document buildDocWithGivenRoot(Element smNode) throws ParserConfigurationException, TransformerException, IOException, SAXException {
        Document docUnwrapped = getDocumentBuilder().newDocument();
        Node sm = docUnwrapped.importNode(smNode, true);
        docUnwrapped.appendChild(sm);

        // Marshalling and parsing the document - signature validation fails without this stinky "magic".
        // _Probably_ SUN's implementation doesn't import correctly signatures between two different documents.
        String strUnwrapped = marshall(docUnwrapped);
        System.out.println(strUnwrapped);
        return parseDocument(strUnwrapped);
    }

    private Element findSignatureByParentNode(Element sigParent) {
        for (Node child = sigParent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if ("Signature".equals(child.getLocalName()) && "http://www.w3.org/2000/09/xmldsig#".equals(child.getNamespaceURI())) {
                return (Element) child;
            }
        }
        throw new RuntimeException("Signature not found in given node.");
    }

    private Document parseDocument(String docContent) throws IOException, SAXException, ParserConfigurationException {
        InputStream inputStream = new ByteArrayInputStream(docContent.getBytes());
        return getDocumentBuilder().parse(inputStream);
    }

    private Element findExtensionInServiceInformation(Document doc) throws ParserConfigurationException, SAXException, IOException {
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

    private Element findFirstElementByName(Document doc, String elementName) {
        NodeList elements = doc.getElementsByTagNameNS(OASIS_NS, elementName);
        return (Element) elements.item(0);
    }

    private String marshall(Document doc) throws TransformerException, UnsupportedEncodingException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        trans.transform(new DOMSource(doc), new StreamResult(stream));
        return stream.toString("UTF-8");
    }

    private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        return dbf.newDocumentBuilder();
    }

    private Document loadDocument(String docResourcePath) throws ParserConfigurationException, SAXException, IOException {
        InputStream inputStreamm = this.getClass().getResourceAsStream(docResourcePath);
        return getDocumentBuilder().parse(inputStreamm);
    }

    private String loadDocumentAsString(String docResourcePath) throws IOException {
        InputStream inputStream = SignatureValidatorTest.class.getResourceAsStream(docResourcePath);
        return IOUtils.toString(inputStream, "UTF-8");
    }
}
