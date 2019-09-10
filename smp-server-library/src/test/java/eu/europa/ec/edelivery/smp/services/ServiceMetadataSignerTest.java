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

import eu.europa.ec.edelivery.smp.services.ui.UIKeystoreService;
import eu.europa.ec.edelivery.smp.testutil.SignatureUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static eu.europa.ec.edelivery.smp.testutil.XmlTestUtils.loadDocument;


/**
 * Created by rodrfla on 20/02/2017.
 */

@ContextConfiguration(classes = { ServiceMetadataSigner.class})
public class ServiceMetadataSignerTest extends AbstractServiceIntegrationTest{

    Path resourceDirectory = Paths.get("src", "test", "resources",  "keystores");

    ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);

    @Autowired
    UIKeystoreService uiKeystoreService;

    @Autowired
    private ServiceMetadataSigner signer;

    @Before
    public void setup(){
        configurationService = Mockito.spy(configurationService);
        ReflectionTestUtils.setField(uiKeystoreService,"configurationService",configurationService);
        ReflectionTestUtils.setField(signer,"uiKeystoreService",uiKeystoreService);

        // set keystore properties
        File keystoreFile = new File(resourceDirectory.toFile(), "smp-keystore.jks");
        Mockito.doReturn( keystoreFile).when(configurationService).getKeystoreFile();
        Mockito.doReturn( resourceDirectory.toFile()).when(configurationService).getConfigurationFolder();
        Mockito.doReturn("test123").when(configurationService).getKeystoreCredentialToken();
        uiKeystoreService.refreshData();
    }

    private Document loadAndSignDocumentForDefault() throws Exception {
        Document documentToSign = loadDocument("/input/SignedServiceMetadata_withoutSignature.xml");
        signer.sign(documentToSign, null);

        return documentToSign;
    }

    private void validateSignatureForDefault(Document document) throws Exception {
        Element smpSigPointer = SignatureUtil.findSignatureByParentNode(document.getDocumentElement());
        SignatureUtil.validateSignature(smpSigPointer);
    }


    private Element loadAndSignDocumentForAdmin(String filePath) throws Exception {
        Document response = loadDocument(filePath);
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
