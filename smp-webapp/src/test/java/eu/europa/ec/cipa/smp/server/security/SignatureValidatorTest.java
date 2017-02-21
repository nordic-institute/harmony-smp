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
        //given
        String serviceGroupId = "ehealth-actorid-qns::urn:australia:ncpb";
        String documentTypeId = "ehealth-resid-qns::urn::epsos##services:extended:epsos::107";
        ServiceMetadataInterface serviceMetadataInterface = new ServiceMetadataInterface();
        String clientCertHeader = "sno=48%3Ab6%3A81%3Aee%3A8e%3A0d%3Acc%3A08&amp;subject=C%3DBE%2C+O%3DEuropean+Commission%2C+OU%3DCEF_eDelivery.europa.eu%2C+OU%3DeHealth%2C+CN%3DEHEALTH_SMP_TEST_BRAZIL%2FemailAddress%3DCEF-EDELIVERY-SUPPORT%40ec.europa.eu&amp;validfrom=Feb++1+14%3A20%3A18+2017+GMT&amp;validto=Jul++9+23%3A59%3A00+2019+GMT&amp;issuer=C%3DDE%2C+O%3DT-Systems+International+GmbH%2C+OU%3DT-Systems+Trust+Center%2C+ST%3DNordrhein+Westfalen%2FpostalCode%3D57250%2C+L%3DNetphen%2Fstreet%3DUntere+Industriestr.+20%2C+CN%3DShared+Business+CA+4&amp;policy_oids=1.3.6.1.4.1.7879.13.25";
        DefaultHttpHeader defaultHttpHeader = new DefaultHttpHeader();
        defaultHttpHeader.addRequestHeader("Client-Cert", Arrays.asList(clientCertHeader));
        SecurityContextHolder.getContext().setAuthentication(new BlueCoatClientCertificateAuthentication(clientCertHeader));
        serviceMetadataInterface.setHeaders(defaultHttpHeader);

        //Sign w/ Customized Signature
        Document docPutRequest = SignatureUtil.loadDocument("/input/ServiceMetadata.xml");
        Element serviceInfExtension = SignatureUtil.findExtensionInServiceInformation(docPutRequest);
        SignatureUtil.sign("", serviceInfExtension, C14N_METHOD);
        String signedByCustomizedSignature = SignatureUtil.marshall(docPutRequest);

        //Save ServiceMetadata
        serviceMetadataInterface.saveServiceRegistration(serviceGroupId, documentTypeId, SignatureUtil.marshall(docPutRequest));

        //Retrieve saved ServiceMetadata
        Document response = serviceMetadataInterface.getServiceRegistration(serviceGroupId, documentTypeId);

        //Validate Customized Signature
        Element smNode = SignatureUtil.findFirstElementByName(response, "ServiceMetadata");
        Document docUnwrapped = SignatureUtil.buildDocWithGivenRoot(smNode);
        Element siSigPointer = SignatureUtil.findServiceInfoSig(docUnwrapped);
        SignatureUtil.validateSignature(siSigPointer);

        //Validate Default Signature
        SignatureSigner signatureValidator = new SignatureSigner(SignatureUtil.loadPrivateKey(), (X509Certificate) SignatureUtil.loadCertificate());
        signatureValidator.signXML(response.getDocumentElement());

        //Validate Default Signature
        Element smpSigPointer = SignatureUtil.findSignatureByParentNode(response.getDocumentElement());
        SignatureUtil.validateSignature(smpSigPointer);

        //Check signed document
        Assert.assertEquals(signedByCustomizedSignature, SignatureUtil.loadDocumentAsString("/expected_output/PUT_ServiceMetadata_request.xml"));
        Assert.assertEquals(SignatureUtil.marshall(response), SignatureUtil.loadDocumentAsString("/expected_output/GET_SignedServiceMetadata_response.xml"));
    }

    @Test
    public void validateLinarizedSignature() throws Throwable {
        //given
        String serviceGroupId = "ehealth-actorid-qns::urn:brazil:ncpb";
        String documentTypeId = "ehealth-resid-qns::urn::epsos##services:extended:epsos::107";
        ServiceMetadataInterface serviceMetadataInterface = new ServiceMetadataInterface();
        String clientCertHeader = "sno=f7%3A1e%3Ae8%3Ab1%3A1c%3Ab3%3Ab7%3A87&amp;subject=C%3DBE%2C+O%3DEuropean+Commission%2C+OU%3DCEF_eDelivery.europa.eu%2C+OU%3DeHealth%2C+OU%3DSMP_TEST%2C+CN%3DEHEALTH_SMP_EC%2FemailAddress%3DCEF-EDELIVERY-SUPPORT%40ec.europa.eu&amp;validfrom=Dec++6+17%3A41%3A42+2016+GMT&amp;validto=Jul++9+23%3A59%3A00+2019+GMT&amp;issuer=C%3DDE%2C+O%3DT-Systems+International+GmbH%2C+OU%3DT-Systems+Trust+Center%2C+ST%3DNordrhein+Westfalen%2FpostalCode%3D57250%2C+L%3DNetphen%2Fstreet%3DUntere+Industriestr.+20%2C+CN%3DShared+Business+CA+4&amp;policy_oids=1.3.6.1.4.1.7879.13.25\n";
        DefaultHttpHeader defaultHttpHeader = new DefaultHttpHeader();
        defaultHttpHeader.addRequestHeader("Client-Cert", Arrays.asList(clientCertHeader));
        SecurityContextHolder.getContext().setAuthentication(new BlueCoatClientCertificateAuthentication(clientCertHeader));
        serviceMetadataInterface.setHeaders(defaultHttpHeader);

        //Sign w/ Customized Signature
        Document docPutRequest = SignatureUtil.loadDocument("/input/ServiceMetadata_linarized.xml");
        Element serviceInfExtension = SignatureUtil.findExtensionInServiceInformation(docPutRequest);
        SignatureUtil.sign("", serviceInfExtension, C14N_METHOD);
        String signedByCustomizedSignature = SignatureUtil.marshall(docPutRequest);

        //when
        //Save ServiceMetadata
        serviceMetadataInterface.saveServiceRegistration(serviceGroupId, documentTypeId, SignatureUtil.marshall(docPutRequest));

        //Retrieve saved ServiceMetadata
        Document response = serviceMetadataInterface.getServiceRegistration(serviceGroupId, documentTypeId);

        //Validate Customized Signature
        Element smNode = SignatureUtil.findFirstElementByName(response, "ServiceMetadata");
        Document docUnwrapped = SignatureUtil.buildDocWithGivenRoot(smNode);
        Element siSigPointer = SignatureUtil.findServiceInfoSig(docUnwrapped);
        SignatureUtil.validateSignature(siSigPointer);

        //Validate Default Signature
        SignatureSigner signatureValidator = new SignatureSigner(SignatureUtil.loadPrivateKey(), (X509Certificate) SignatureUtil.loadCertificate());
        signatureValidator.signXML(response.getDocumentElement());

        //Validate Default Signature
        Element smpSigPointer = SignatureUtil.findSignatureByParentNode(response.getDocumentElement());
        SignatureUtil.validateSignature(smpSigPointer);

        //Check signed document
      //  Assert.assertEquals(signedByCustomizedSignature, SignatureUtil.loadDocumentAsString("/expected_output/PUT_ServiceMetadata_request_linarized.xml"));
       // Assert.assertEquals(SignatureUtil.marshall(response), SignatureUtil.loadDocumentAsString("/expected_output/GET_SignedServiceMetadata_response_linarized.xml"));
    }
}
