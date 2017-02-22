package eu.europa.ec.cipa.smp.server.security;


import eu.europa.ec.cipa.smp.server.AbstractTest;
import eu.europa.ec.cipa.smp.server.services.readwrite.ServiceMetadataInterface;
import eu.europa.ec.cipa.smp.server.util.DefaultHttpHeader;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import java.security.cert.X509Certificate;
import java.util.Arrays;

/**
 * Created by rodrfla on 08/02/2017.
 */
public class SignatureValidatorTest extends AbstractTest {

    private static final String C14N_METHOD = CanonicalizationMethod.INCLUSIVE;

    @Test
    public void validateSignature() throws Throwable {
        String serviceGroupId = "ehealth-actorid-qns::urn:australia:ncpb";
        String clientCertHeader = "sno=48%3Ab6%3A81%3Aee%3A8e%3A0d%3Acc%3A08&amp;subject=C%3DBE%2C+O%3DEuropean+Commission%2C+OU%3DCEF_eDelivery.europa.eu%2C+OU%3DeHealth%2C+CN%3DEHEALTH_SMP_TEST_BRAZIL%2FemailAddress%3DCEF-EDELIVERY-SUPPORT%40ec.europa.eu&amp;validfrom=Feb++1+14%3A20%3A18+2017+GMT&amp;validto=Jul++9+23%3A59%3A00+2019+GMT&amp;issuer=C%3DDE%2C+O%3DT-Systems+International+GmbH%2C+OU%3DT-Systems+Trust+Center%2C+ST%3DNordrhein+Westfalen%2FpostalCode%3D57250%2C+L%3DNetphen%2Fstreet%3DUntere+Industriestr.+20%2C+CN%3DShared+Business+CA+4&amp;policy_oids=1.3.6.1.4.1.7879.13.25";//
        String filePathToLoad = "/input/ServiceMetadata.xml";
        String signedByCustomizedSignatureFilePath = "/expected_output/PUT_ServiceMetadata_request.xml";
        String defaultSignatureFilePath = "/expected_output/GET_SignedServiceMetadata_response.xml";

        commonTest(serviceGroupId, clientCertHeader, filePathToLoad, signedByCustomizedSignatureFilePath, defaultSignatureFilePath);
    }

    @Test
    public void validateLinarizedSignature() throws Throwable {
        String serviceGroupId = "ehealth-actorid-qns::urn:brazil:ncpb";
        String clientCertHeader = "sno=f7%3A1e%3Ae8%3Ab1%3A1c%3Ab3%3Ab7%3A87&amp;subject=C%3DBE%2C+O%3DEuropean+Commission%2C+OU%3DCEF_eDelivery.europa.eu%2C+OU%3DeHealth%2C+OU%3DSMP_TEST%2C+CN%3DEHEALTH_SMP_EC%2FemailAddress%3DCEF-EDELIVERY-SUPPORT%40ec.europa.eu&amp;validfrom=Dec++6+17%3A41%3A42+2016+GMT&amp;validto=Jul++9+23%3A59%3A00+2019+GMT&amp;issuer=C%3DDE%2C+O%3DT-Systems+International+GmbH%2C+OU%3DT-Systems+Trust+Center%2C+ST%3DNordrhein+Westfalen%2FpostalCode%3D57250%2C+L%3DNetphen%2Fstreet%3DUntere+Industriestr.+20%2C+CN%3DShared+Business+CA+4&amp;policy_oids=1.3.6.1.4.1.7879.13.25";
        String filePathToLoad = "/input/ServiceMetadata_linarized.xml";
        String signedByCustomizedSignatureFilePath = "/expected_output/PUT_ServiceMetadata_request_linarized.xml";
        String defaultSignatureFilePath = "/expected_output/GET_SignedServiceMetadata_response_linarized.xml";

        commonTest(serviceGroupId, clientCertHeader, filePathToLoad, signedByCustomizedSignatureFilePath, defaultSignatureFilePath);
    }

    private void commonTest(String serviceGroupId, String clientCertHeader, String filePathToLoad, String signedByCustomizedSignatureFilePath, String defaultSignatureFilePath) throws Throwable {
        //given
        String documentTypeId = "ehealth-resid-qns::urn::epsos##services:extended:epsos::107";
        ServiceMetadataInterface serviceMetadataInterface = new ServiceMetadataInterface();
        DefaultHttpHeader defaultHttpHeader = new DefaultHttpHeader();
        defaultHttpHeader.addRequestHeader("Client-Cert", Arrays.asList(clientCertHeader));
        SecurityContextHolder.getContext().setAuthentication(new BlueCoatClientCertificateAuthentication(clientCertHeader));
        serviceMetadataInterface.setHeaders(defaultHttpHeader);

        //Sign w/ Customized Signature
        Document docPutRequest = SignatureUtil.loadDocument(filePathToLoad);
        Element serviceInfExtension = SignatureUtil.findExtensionInServiceInformation(docPutRequest);
        SignatureUtil.sign("", serviceInfExtension, C14N_METHOD);
        String signedByCustomizedSignature = SignatureUtil.marshall(docPutRequest);

        //When
        //Save ServiceMetadata
        serviceMetadataInterface.saveServiceRegistration(serviceGroupId, documentTypeId, signedByCustomizedSignature);

        //Retrieve saved ServiceMetadata
        Document response = serviceMetadataInterface.getServiceRegistration(serviceGroupId, documentTypeId);

        //store Customized Signature for validation
        Element smNode = SignatureUtil.findFirstElementByName(response, "ServiceMetadata");
        Document docUnwrapped = SignatureUtil.buildDocWithGivenRoot(smNode);
        Element adminSignature = SignatureUtil.findServiceInfoSig(docUnwrapped);

        //Default Signature
        Signer signer = new Signer(SignatureUtil.loadPrivateKey(), (X509Certificate) SignatureUtil.loadCertificate());
        signer.signXML(response.getDocumentElement());

        //Then
        //Check signed document
        //Admin signature validation
        SignatureUtil.validateSignature(adminSignature);
        //Default signature validation
        Element smpSigPointer = SignatureUtil.findSignatureByParentNode(response.getDocumentElement());
        SignatureUtil.validateSignature(smpSigPointer);
        Assert.assertEquals(signedByCustomizedSignature, SignatureUtil.loadDocumentAsString(signedByCustomizedSignatureFilePath));
        Assert.assertEquals(SignatureUtil.marshall(response), SignatureUtil.loadDocumentAsString(defaultSignatureFilePath));
    }
}
