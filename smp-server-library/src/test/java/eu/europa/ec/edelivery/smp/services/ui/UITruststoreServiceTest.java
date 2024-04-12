/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
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
package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.exceptions.CertificateNotTrustedException;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.services.CRLVerifierService;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.testutil.X509CertificateTestUtils;
import org.apache.commons.io.FileUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.core.convert.ConversionService;

import javax.security.auth.x500.X500Principal;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.*;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UITruststoreServiceTest {
    // test data
    protected Path resourceDirectory = Paths.get("src", "test", "resources", "truststore");
    protected Path targetDirectory = Paths.get("target", "test-uitruststoreservice");
    protected Path targetTruststore = targetDirectory.resolve("smp-truststore.jks");

    String truststorePassword = "test123";
    // mocked services
    ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);
    CRLVerifierService crlVerifierService = Mockito.mock(CRLVerifierService.class);
    ConversionService conversionService = Mockito.mock(ConversionService.class);
    UserDao userDao = Mockito.mock(UserDao.class);

    UITruststoreService testInstance = spy(new UITruststoreService(configurationService, crlVerifierService, conversionService, userDao));

    @BeforeEach
    public void setup() throws IOException {
        testInstance.init();
        resetKeystore();
    }

    @Test
    void validateCertificateNotUsedOk() throws CertificateException {
        String certId = "cn=test" + UUID.randomUUID() + ",o=test,c=eu:123456";
        CertificateRO certificateRO = new CertificateRO();
        certificateRO.setCertificateId(certId);
        doReturn(Optional.empty()).when(userDao).findUserByCertificateId(ArgumentMatchers.anyString());
        // when
        testInstance.validateCertificateNotUsed(certificateRO);
        //then no error is thrown because
        ArgumentCaptor<String> certIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(userDao, times(1))
                .findUserByCertificateId(certIdCaptor.capture());
        assertEquals(certId, certIdCaptor.getValue());
    }

    @Test
    void validateCertificateNotUsedIsUsed() {
        String certId = "cn=test" + UUID.randomUUID() + ",o=test,c=eu:123456";
        CertificateRO certificateRO = new CertificateRO();
        certificateRO.setCertificateId(certId);
        doReturn(Optional.of(new DBUser())).when(userDao).findUserByCertificateId(ArgumentMatchers.anyString());
        // when
        CertificateException result = assertThrows(CertificateException.class, () -> testInstance.validateCertificateNotUsed(certificateRO));
        assertEquals("Certificate: [" + certId + "] is already used!", result.getMessage());
    }

    @Test
    void validateNewCertificateOk() throws CertificateException {
        X509Certificate cert = Mockito.mock(X509Certificate.class);
        CertificateRO certData = new CertificateRO();
        doNothing().when(testInstance).checkFullCertificateValidity(cert);
        doNothing().when(testInstance).validateCertificateNotUsed(certData);

        testInstance.validateCertificate(cert, certData);

        assertFalse(certData.isInvalid());
        assertNull(certData.getInvalidReason());
    }

    @Test
    void validateNewCertificateCertificateExpiredException() throws CertificateException {
        X509Certificate cert = Mockito.mock(X509Certificate.class);
        CertificateRO certData = new CertificateRO();
        doThrow(new CertificateExpiredException("Expired")).when(testInstance).checkFullCertificateValidity(cert);

        testInstance.validateCertificate(cert, certData);

        assertTrue(certData.isInvalid());
        assertEquals("Certificate is expired!", certData.getInvalidReason());
    }

    @Test
    void validateNewCertificateCertificateCertificateNotYetValidException() throws CertificateException {
        X509Certificate cert = Mockito.mock(X509Certificate.class);
        CertificateRO certData = new CertificateRO();
        doThrow(new CertificateNotYetValidException("Error")).when(testInstance).checkFullCertificateValidity(cert);

        testInstance.validateCertificate(cert, certData);

        assertTrue(certData.isInvalid());
        assertEquals("Certificate is not yet valid!", certData.getInvalidReason());
    }

    @Test
    void validateNewCertificateCertificateCertificateRevokedException() throws CertificateException {
        X509Certificate cert = Mockito.mock(X509Certificate.class);
        CertificateRO certData = new CertificateRO();
        doThrow(Mockito.mock(CertificateRevokedException.class)).when(testInstance).checkFullCertificateValidity(cert);

        testInstance.validateCertificate(cert, certData);

        assertTrue(certData.isInvalid());
        assertEquals("Certificate is revoked!", certData.getInvalidReason());
    }

    @Test
    void validateNewCertificateCertificateCertificateNotTrustedException() throws CertificateException {
        X509Certificate cert = Mockito.mock(X509Certificate.class);
        CertificateRO certData = new CertificateRO();
        doThrow(Mockito.mock(CertificateNotTrustedException.class)).when(testInstance).checkFullCertificateValidity(cert);

        testInstance.validateCertificate(cert, certData);

        assertTrue(certData.isInvalid());
        assertEquals("Certificate is not trusted!", certData.getInvalidReason());
    }

    @Test
    void validateNewCertificateCertPathValidatorException() throws CertificateException {
        X509Certificate cert = Mockito.mock(X509Certificate.class);
        CertificateRO certData = new CertificateRO();
        doThrow(new CertificateException(Mockito.mock(CertPathValidatorException.class))).when(testInstance).checkFullCertificateValidity(cert);

        testInstance.validateCertificate(cert, certData);

        assertTrue(certData.isInvalid());
        assertEquals("Certificate is not trusted! Invalid certificate policy path!", certData.getInvalidReason());
    }

    @Test
    void validateNewCertificateCertificateException() throws CertificateException {
        String errorMessage = "Error Message";
        X509Certificate cert = Mockito.mock(X509Certificate.class);
        CertificateRO certData = new CertificateRO();
        doThrow(new CertificateException(errorMessage)).when(testInstance).checkFullCertificateValidity(cert);

        testInstance.validateCertificate(cert, certData);

        assertTrue(certData.isInvalid());
        assertEquals(errorMessage, certData.getInvalidReason());
    }

    @Test
    void validateCertificateSubjectExpressionLegacyIfNullSkip() throws CertificateException {
        X509Certificate cert = Mockito.mock(X509Certificate.class);
        doReturn(null).when(configurationService).getCertificateSubjectRegularExpression();
        testInstance.validateCertificateSubjectExpressionLegacy(cert);
    }

    @Test
    void validateCertificateSubjectExpressionLegacyValidatedNotMatch() throws Exception {
        String regularExpression = ".*CN=SomethingNotExists.*";
        String subject = "CN=Something,O=test,C=EU";
        X509Certificate certificate = X509CertificateTestUtils.createX509CertificateForTest(subject);
        doReturn(Pattern.compile(regularExpression)).when(configurationService).getCertificateSubjectRegularExpression();
        CertificateException resultException = assertThrows(CertificateException.class, () -> testInstance.validateCertificateSubjectExpressionLegacy(certificate));

        assertEquals("Certificate subject ["
                        + certificate.getSubjectX500Principal().getName(X500Principal.RFC2253)
                        + "] does not match the regular expression configured [" + regularExpression + "]",
                resultException.getMessage());
    }


    @Test
    void validateCertificateSubjectExpressionLegacyValidatedMatch() throws Exception {
        String regularExpression = ".*CN=Something.*";
        String subject = "CN=Something,O=test,C=EU";
        X509Certificate certificate = X509CertificateTestUtils.createX509CertificateForTest(subject);
        doReturn(Pattern.compile(regularExpression)).when(configurationService).getCertificateSubjectRegularExpression();

        testInstance.validateCertificateSubjectExpressionLegacy(certificate);
        // no error is thrown
    }

    @Test
    void loadTruststoreDoNotThrowError() {
        // test for null file
        KeyStore result = testInstance.loadTruststore(null);
        assertNull(result);
        // test for file not exists
        result = testInstance.loadTruststore(new File(UUID.randomUUID().toString()));
        assertNull(result);
        // test for file credentials not exist

        assertTrue(targetTruststore.toFile().exists());
        doReturn(null).when(configurationService).getTruststoreCredentialToken();
        result = testInstance.loadTruststore(targetTruststore.toFile());
        assertNull(result);
    }

    @Test
    void testTruststoreNotConfiguredNotConfigured() {
        doReturn(null).when(configurationService).getTruststoreFile();
        boolean result = testInstance.truststoreNotConfigured();
        assertTrue(result);
    }

    @Test
    void testTruststoreNotConfiguredConfigured() {

        doReturn(targetTruststore.toFile()).when(configurationService).getTruststoreFile();
        boolean result = testInstance.truststoreNotConfigured();
        assertFalse(result);
    }

    @Test
    void testRefreshDataNoConfiguration() {

        doReturn(null).when(configurationService).getTruststoreFile();
        // must not throw error
        testInstance.refreshData();
        //then
        assertNull(testInstance.getTrustStore());
        assertNull(testInstance.getTrustManagers());
        assertTrue(testInstance.getNormalizedTrustedList().isEmpty());
    }

    @Test
    void testRefreshDataOk() {

        doReturn(targetTruststore.toFile()).when(configurationService).getTruststoreFile();
        doReturn(truststorePassword).when(configurationService).getTruststoreCredentialToken();
        // must not throw error
        testInstance.refreshData();
        //then
        assertNotNull(testInstance.getTrustStore());
        assertNotNull(testInstance.getTrustManagers());
        assertFalse(testInstance.getNormalizedTrustedList().isEmpty());
    }

    @Test
    void getCertificateDataNullEmpty() {

        // must not throw error
        CertificateRO certificateRO = testInstance.getCertificateData(null);
        //then
        assertNotNull(certificateRO);
        assertTrue(certificateRO.isInvalid());
        assertTrue(certificateRO.isError());
        assertEquals("Can not read [null/empty] certificate!", certificateRO.getInvalidReason());
    }

    @Test
    void getCertificateDataInvalidCertificate() {

        // must not throw error
        CertificateRO certificateRO = testInstance.getCertificateData("notACertificate".getBytes());
        //then
        assertNotNull(certificateRO);
        assertTrue(certificateRO.isInvalid());
        assertTrue(certificateRO.isError());
        assertEquals("Can not read the certificate!", certificateRO.getInvalidReason());
    }

    @Test
    void getCertificateDataNoValidationOK() throws Exception {
        String subject = "CN=Something,O=test,C=EU";
        X509Certificate certificate = X509CertificateTestUtils.createX509CertificateForTest(subject);
        CertificateRO convertedCert = Mockito.mock(CertificateRO.class);
        doReturn(convertedCert).when(conversionService).convert(certificate, CertificateRO.class);
        // must not throw error
        CertificateRO certificateRO = testInstance.getCertificateData(certificate.getEncoded());
        //then
        assertNotNull(certificateRO);
        assertEquals(convertedCert, certificateRO);
    }

    @Test
    void getCertificateDataNoValidationBase64OK() throws Exception {
        String subject = "CN=Something,O=test,C=EU";
        X509Certificate certificate = X509CertificateTestUtils.createX509CertificateForTest(subject);
        String base64Cert = Base64.getEncoder().encodeToString(certificate.getEncoded());
        CertificateRO convertedCert = Mockito.mock(CertificateRO.class);
        doReturn(convertedCert).when(conversionService).convert(certificate, CertificateRO.class);

        // must not throw error
        CertificateRO certificateRO = testInstance.getCertificateData(base64Cert, false, false);
        //then
        assertNotNull(certificateRO);
        assertEquals(convertedCert, certificateRO);
    }

    @Test
    void getCertificateDataValidateOK() throws Exception {
        String subject = "CN=Something,O=test,C=EU";
        X509Certificate certificate = X509CertificateTestUtils.createX509CertificateForTest(subject);
        CertificateRO convertedCert = Mockito.mock(CertificateRO.class);
        doReturn(convertedCert).when(conversionService).convert(certificate, CertificateRO.class);
        // must not throw error
        CertificateRO certificateRO = testInstance.getCertificateData(certificate.getEncoded(), true, true);
        //then
        assertNotNull(certificateRO);
        assertEquals(convertedCert, certificateRO);
    }

    @Test
    void testValidateCertificateWithTruststoreNull() {
        // when
        CertificateException result = assertThrows(CertificateException.class, () -> testInstance.validateCertificateWithTruststore(null));
        // then
        MatcherAssert.assertThat(result.getMessage(), Matchers.containsString("The X509Certificate is null"));
    }

    @Test
    void testValidateCertificateWithTruststoreNoTruststoreConfigured() throws Exception {
        String subject = "CN=Something,O=test,C=EU";
        X509Certificate certificate = X509CertificateTestUtils.createX509CertificateForTest(subject);
        doReturn(null).when(configurationService).getTruststoreFile();
        // when
        testInstance.refreshData();
        testInstance.validateCertificateWithTruststore(certificate);
        // then
        // no error is thrown
        Mockito.verify(configurationService, Mockito.times(1)).getTruststoreFile();
    }

    @Test
    void testValidateCertificateWithTruststoreNotTrusted() throws Exception {
        String subject = "CN=Something,O=test,C=EU";
        X509Certificate certificate = X509CertificateTestUtils.createX509CertificateForTest(subject);
        doReturn(targetTruststore.toFile()).when(configurationService).getTruststoreFile();
        doReturn(truststorePassword).when(configurationService).getTruststoreCredentialToken();
        // when
        testInstance.refreshData();
        CertificateException result = assertThrows(CertificateException.class,
                () -> testInstance.validateCertificateWithTruststore(certificate));
        // then
        MatcherAssert.assertThat(result.getMessage(), Matchers.containsString("is not trusted!"));
    }

    @Test
    void testValidateAllowedCertificateKeyTypes() throws Exception {
        String subject = "CN=Something,O=test,C=EU";
        X509Certificate certificate = X509CertificateTestUtils.createX509CertificateForTest(subject);
        doReturn(Collections.singletonList("FutureKeyAlgorithm")).when(configurationService).getAllowedCertificateKeyTypes();
        //when
        CertificateException result = assertThrows(CertificateException.class,
                () -> testInstance.validateAllowedCertificateKeyTypes(certificate));
        // then
        MatcherAssert.assertThat(result.getMessage(), Matchers.containsString("Certificate does not have allowed key algorithm type!"));
    }


    @Test
    void testAddCertificate() throws Exception {
        String subject = "CN=Something,O=test,C=EU";
        X509Certificate certificate = X509CertificateTestUtils.createX509CertificateForTest(subject);

        doReturn(targetTruststore.toFile()).when(configurationService).getTruststoreFile();
        doReturn(truststorePassword).when(configurationService).getTruststoreCredentialToken();
        testInstance.refreshData();
        int count = testInstance.getNormalizedTrustedList().size();
        // when
        testInstance.addCertificate(null, certificate);

        assertEquals(count + 1, testInstance.getNormalizedTrustedList().size());
    }

    @Test
    void testAddCertificate_DuplicateCertificate() throws Exception {
        String alias = "duplicate";
        String subject = "CN=Something,O=test,C=EU";
        X509Certificate certificate = X509CertificateTestUtils.createX509CertificateForTest(subject);

        doReturn(targetTruststore.toFile()).when(configurationService).getTruststoreFile();
        doReturn(targetTruststore.toFile()).when(configurationService).getTruststoreFile();
        doReturn(truststorePassword).when(configurationService).getTruststoreCredentialToken();
        testInstance.refreshData();
        int count = testInstance.getNormalizedTrustedList().size();

        // when
        testInstance.addCertificate(alias, certificate);
        SMPRuntimeException smpRuntimeException = assertThrows(SMPRuntimeException.class, () -> testInstance.addCertificate(alias, certificate));

        // then
        Assertions.assertEquals("Certificate error [duplicate]. Error: The certificate you are trying to upload already exists under the [duplicate] entry!", smpRuntimeException.getMessage());
    }

    @Test
    void testDeleteCertificate() throws Exception {
        String subject = "CN=Something,O=test,C=EU";
        X509Certificate certificate = X509CertificateTestUtils.createX509CertificateForTest(subject);
        doReturn(targetTruststore.toFile()).when(configurationService).getTruststoreFile();
        doReturn(truststorePassword).when(configurationService).getTruststoreCredentialToken();
        String alias = "testInstanceCertificate";
        testInstance.addCertificate(alias, certificate);
        int count = testInstance.getNormalizedTrustedList().size();

        //then
        X509Certificate result = testInstance.deleteCertificate(alias);
        //when
        assertNotNull(result);
        assertEquals(count - 1, testInstance.getNormalizedTrustedList().size());
    }

    protected void resetKeystore() throws IOException {
        FileUtils.deleteDirectory(targetDirectory.toFile());
        FileUtils.copyDirectory(resourceDirectory.toFile(), targetDirectory.toFile());
    }
}
