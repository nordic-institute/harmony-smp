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
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static eu.europa.ec.edelivery.smp.testutil.XmlTestUtils.loadDocument;
import static org.junit.Assert.assertEquals;

/**
 * Created by gutowpa on 24/01/2018.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ServiceMetadataSigner.class})
public class ServiceMetadataSignerMultipleDomainsIntegrationTest extends  AbstractServiceIntegrationTest {

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
        File keystoreFile = new File(resourceDirectory.toFile(), "smp-keystore_multiple_domains.jks");
        Mockito.doReturn( keystoreFile).when(configurationService).getKeystoreFile();
        Mockito.doReturn( resourceDirectory.toFile()).when(configurationService).getConfigurationFolder();
        Mockito.doReturn("test123").when(configurationService).getKeystoreCredentialToken();
        uiKeystoreService.refreshData();
    }

    @Test
    public void testSignatureCalculatedForSecondDomain() throws Exception {
        // given
        Document document = loadDocument("/input/SignedServiceMetadata_withoutSignature.xml");

        // when
        signer.sign(document, "second_domain_alias");
        Element smpSigPointer = SignatureUtil.findSignatureByParentNode(document.getDocumentElement());
        String signingCertSubject = smpSigPointer.getElementsByTagName("X509SubjectName").item(0).getTextContent();

        // then
        SignatureUtil.validateSignature(smpSigPointer);
        assertEquals("CN=Secodn domain,OU=SMP,O=CEF Digital,C=BE", signingCertSubject);
    }
}
