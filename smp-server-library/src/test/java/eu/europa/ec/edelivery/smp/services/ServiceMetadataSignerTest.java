/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */

package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.data.dao.AbstractJunit5BaseDao;
import eu.europa.ec.edelivery.smp.services.spi.SmpXmlSignatureService;
import eu.europa.ec.edelivery.smp.services.ui.UIKeystoreService;
import eu.europa.ec.edelivery.smp.testutil.SignatureUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * metadata tests signatures
 *
 * @author Flavio Santos
 * @author Joze Rihtarsic
 * @since 3.0

 */
@ContextConfiguration(classes = { SmpXmlSignatureService.class})
class ServiceMetadataSignerTest extends AbstractJunit5BaseDao{

    Path resourceDirectory = Paths.get("src", "test", "resources",  "keystores");

    ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);

    @Autowired
    UIKeystoreService uiKeystoreService;

    @Autowired
    private SmpXmlSignatureService signer;

    @BeforeEach
    public void setup(){
        configurationService = Mockito.spy(configurationService);
        ReflectionTestUtils.setField(uiKeystoreService,"configurationService",configurationService);
        ReflectionTestUtils.setField(signer,"uiKeystoreService",uiKeystoreService);

        // set keystore properties
        File keystoreFile = new File(resourceDirectory.toAbsolutePath().toFile(), "smp-keystore-all-keys.p12");
        Mockito.doReturn( keystoreFile).when(configurationService).getKeystoreFile();
        Mockito.doReturn( resourceDirectory.toFile()).when(configurationService).getSecurityFolder();
        Mockito.doReturn("test123").when(configurationService).getKeystoreCredentialToken();
        Mockito.doReturn("PKCS12").when(configurationService).getKeystoreType();
        uiKeystoreService.refreshData();
    }

    private Document loadAndSignDocumentForDefault(String alias) throws Exception {
        Document documentToSign = SignatureUtil.loadDocument("/input/SignedServiceMetadata_withoutSignature.xml");
        signer.sign(documentToSign, alias, null, null);
        return documentToSign;
    }

    private void validateSignatureForDefault(Document document) throws Exception {
        Element smpSigPointer = SignatureUtil.findSignatureByParentNode(document.getDocumentElement());
        SignatureUtil.validateSignature(smpSigPointer);
    }

    private Element loadAndSignDocumentForAdmin(String filePath) throws Exception {
        Document response = SignatureUtil.loadDocument(filePath);
        Element adminSignature = SignatureUtil.findServiceInfoSig(response);
        return adminSignature;
    }

    @ParameterizedTest
    @ValueSource(strings = {"sample_key",
            "smp_ecdsa_nist-b409",
            "smp_eddsa_25519",
            "smp_eddsa_448"})
    void testSignatureAndDefaultAlgorithmeDefinitionOk(String alias) throws Exception {
        Document document = loadAndSignDocumentForDefault(alias);
        validateSignatureForDefault(document);
    }

    @Test
    void testAdminSignatureOk() throws Exception {
        Element adminSignature = loadAndSignDocumentForAdmin("/expected_output/PUT_ServiceMetadata_request.xml");
        SignatureUtil.validateSignature(adminSignature);
    }
}
