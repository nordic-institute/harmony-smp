/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.cipa.smp.server.security;


import eu.europa.ec.cipa.smp.server.AbstractTest;
import eu.europa.ec.cipa.smp.server.services.readwrite.ServiceMetadataInterface;
import eu.europa.ec.cipa.smp.server.util.DefaultHttpHeader;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Arrays;

/**
 * Created by rodrfla on 08/02/2017.
 */
public class SignatureValidatorTest extends AbstractTest{

    private static final String C14N_METHOD = CanonicalizationMethod.INCLUSIVE;

    @Test
    public void validateSignature() throws Throwable {
        String serviceGroupId = "ehealth-actorid-qns::urn:australia:ncpb";
        Principal principal = new PreAuthenticatedCertificatePrincipal("C=BE, O=European Commission, OU=CEF_eDelivery.europa.eu, OU=eHealth, CN=EHEALTH_SMP_TEST_BRAZIL", "C=DE, O=T-Systems International GmbH, OU=T-Systems Trust Center, ST=Nordrhein Westfalen/postalCode=57250, L=Netphen/street=Untere Industriestr. 20, CN=Shared Business CA 4", "48:b6:81:ee:8e:0d:cc:08");
        String filePathToLoad = "/input/ServiceMetadata.xml";
        String signedByCustomizedSignatureFilePath = "/expected_output/PUT_ServiceMetadata_request.xml";
        String defaultSignatureFilePath = "/expected_output/GET_SignedServiceMetadata_response.xml";

        commonTest(serviceGroupId, principal, filePathToLoad, signedByCustomizedSignatureFilePath, defaultSignatureFilePath);
    }

    @Test
    public void validateLinarizedSignature() throws Throwable {
        String serviceGroupId = "ehealth-actorid-qns::urn:brazil:ncpb";
        Principal principal = new PreAuthenticatedCertificatePrincipal("C=BE, O=European Commission,OU=CEF_eDelivery.europa.eu,OU=eHealth,OU=SMP_TEST,CN=EHEALTH_SMP_EC", "C=DE, O=T-Systems International GmbH, OU=T-Systems Trust Center, ST=Nordrhein Westfalen/postalCode=57250, L=Netphen/street=Untere Industriestr. 20, CN=Shared Business CA 4", "f7:1e:e8:b1:1c:b3:b7:87");
        String filePathToLoad = "/input/ServiceMetadata_linarized.xml";
        String signedByCustomizedSignatureFilePath = "/expected_output/PUT_ServiceMetadata_request_linarized.xml";
        String defaultSignatureFilePath = "/expected_output/GET_SignedServiceMetadata_response_linarized.xml";

        commonTest(serviceGroupId, principal, filePathToLoad, signedByCustomizedSignatureFilePath, defaultSignatureFilePath);
    }

    private void commonTest(String serviceGroupId, Principal principal, String filePathToLoad, String signedByCustomizedSignatureFilePath, String defaultSignatureFilePath) throws Throwable {
        //given
        String documentTypeId = "ehealth-resid-qns::urn::epsos##services:extended:epsos::107";
        ServiceMetadataInterface serviceMetadataInterface = new ServiceMetadataInterface();
        PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(principal, "N/A");
        authentication.setDetails(principal);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        serviceMetadataInterface.setHeaders(new DefaultHttpHeader());

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
