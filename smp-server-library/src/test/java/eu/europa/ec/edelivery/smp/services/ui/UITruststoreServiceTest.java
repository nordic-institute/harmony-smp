package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.exceptions.CertificateNotTrustedException;
import eu.europa.ec.edelivery.smp.services.CRLVerifierService;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.testutil.X509CertificateTestUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.core.convert.ConversionService;

import javax.security.auth.x500.X500Principal;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.*;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UITruststoreServiceTest {
    ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);
    CRLVerifierService crlVerifierService = Mockito.mock(CRLVerifierService.class);
    ConversionService conversionService = Mockito.mock(ConversionService.class);
    UserDao userDao = Mockito.mock(UserDao.class);

    UITruststoreService testInstance = spy(new UITruststoreService(configurationService, crlVerifierService, conversionService, userDao));

    @Before
    public void setup() {
        Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    @Test
    public void validateCertificateNotUsedOk() throws CertificateException {
        String certId = "cn=test" + UUID.randomUUID().toString() + ",o=test,c=eu:123456";
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
    public void validateCertificateNotUsedIsUsed() {
        String certId = "cn=test" + UUID.randomUUID().toString() + ",o=test,c=eu:123456";
        CertificateRO certificateRO = new CertificateRO();
        certificateRO.setCertificateId(certId);
        doReturn(Optional.of(new DBUser())).when(userDao).findUserByCertificateId(ArgumentMatchers.anyString());
        // when
        CertificateException result = assertThrows(CertificateException.class, () -> testInstance.validateCertificateNotUsed(certificateRO));
        assertEquals("Certificate: [" + certId + "] is already used!", result.getMessage());
    }

    @Test
    public void validateNewCertificateOk() throws CertificateException {
        X509Certificate cert = Mockito.mock(X509Certificate.class);
        CertificateRO certData = new CertificateRO();
        doNothing().when(testInstance).checkFullCertificateValidity(cert);
        doNothing().when(testInstance).validateCertificateNotUsed(certData);

        testInstance.validateCertificate(cert, certData);

        assertFalse(certData.isInvalid());
        assertNull(certData.getInvalidReason());
    }

    @Test
    public void validateNewCertificateCertificateExpiredException() throws CertificateException {
        X509Certificate cert = Mockito.mock(X509Certificate.class);
        CertificateRO certData = new CertificateRO();
        doThrow(new CertificateExpiredException("Expired")).when(testInstance).checkFullCertificateValidity(cert);

        testInstance.validateCertificate(cert, certData);

        assertTrue(certData.isInvalid());
        assertEquals("Certificate is expired!", certData.getInvalidReason());
    }

    @Test
    public void validateNewCertificateCertificateCertificateNotYetValidException() throws CertificateException {
        X509Certificate cert = Mockito.mock(X509Certificate.class);
        CertificateRO certData = new CertificateRO();
        doThrow(new CertificateNotYetValidException("Error")).when(testInstance).checkFullCertificateValidity(cert);

        testInstance.validateCertificate(cert, certData);

        assertTrue(certData.isInvalid());
        assertEquals("Certificate is not yet valid!", certData.getInvalidReason());
    }

    @Test
    public void validateNewCertificateCertificateCertificateRevokedException() throws CertificateException {
        X509Certificate cert = Mockito.mock(X509Certificate.class);
        CertificateRO certData = new CertificateRO();
        doThrow(Mockito.mock(CertificateRevokedException.class)).when(testInstance).checkFullCertificateValidity(cert);

        testInstance.validateCertificate(cert, certData);

        assertTrue(certData.isInvalid());
        assertEquals("Certificate is revoked!", certData.getInvalidReason());
    }

    @Test
    public void validateNewCertificateCertificateCertificateNotTrustedException() throws CertificateException {
        X509Certificate cert = Mockito.mock(X509Certificate.class);
        CertificateRO certData = new CertificateRO();
        doThrow(Mockito.mock(CertificateNotTrustedException.class)).when(testInstance).checkFullCertificateValidity(cert);

        testInstance.validateCertificate(cert, certData);

        assertTrue(certData.isInvalid());
        assertEquals("Certificate is not trusted!", certData.getInvalidReason());
    }

    @Test
    public void validateNewCertificateCertPathValidatorException() throws CertificateException {
        X509Certificate cert = Mockito.mock(X509Certificate.class);
        CertificateRO certData = new CertificateRO();
        doThrow(new CertificateException(Mockito.mock(CertPathValidatorException.class))).when(testInstance).checkFullCertificateValidity(cert);

        testInstance.validateCertificate(cert, certData);

        assertTrue(certData.isInvalid());
        assertEquals("Certificate is not trusted! Invalid certificate policy path!", certData.getInvalidReason());
    }

    @Test
    public void validateNewCertificateCertificateException() throws CertificateException {
        String errorMessage = "Error Message";
        X509Certificate cert = Mockito.mock(X509Certificate.class);
        CertificateRO certData = new CertificateRO();
        doThrow(new CertificateException(errorMessage)).when(testInstance).checkFullCertificateValidity(cert);

        testInstance.validateCertificate(cert, certData);

        assertTrue(certData.isInvalid());
        assertEquals(errorMessage, certData.getInvalidReason());
    }


    @Test
    public void validateCertificateSubjectExpressionLegacyIfNullSkip() throws CertificateException {
        X509Certificate cert = Mockito.mock(X509Certificate.class);
        doReturn(null).when(configurationService).getCertificateSubjectRegularExpression();
        testInstance.validateCertificateSubjectExpressionLegacy(cert);
    }

    @Test
    public void validateCertificateSubjectExpressionLegacyValidatedNotMatch() throws Exception {
        String regularExpression = ".*CN=SomethingNotExists.*";
        String subject = "CN=Something,O=test,C=EU";
        X509Certificate certificate = X509CertificateTestUtils.createX509CertificateForTest(subject);
        doReturn(Pattern.compile(regularExpression)).when(configurationService).getCertificateSubjectRegularExpression();
        CertificateException resultException = assertThrows(CertificateException.class, () -> testInstance.validateCertificateSubjectExpressionLegacy(certificate));

        assertEquals("Certificate subject ["
                +certificate.getSubjectX500Principal().getName(X500Principal.RFC2253)
                +"] does not match the regular expression configured ["+regularExpression+"]",
                resultException.getMessage());
    }

    @Test
    public void validateCertificateSubjectExpressionLegacyValidatedMatch() throws Exception {
        String regularExpression = ".*CN=Something.*";
        String subject = "CN=Something,O=test,C=EU";
        X509Certificate certificate = X509CertificateTestUtils.createX509CertificateForTest(subject);
        doReturn(Pattern.compile(regularExpression)).when(configurationService).getCertificateSubjectRegularExpression();

        testInstance.validateCertificateSubjectExpressionLegacy(certificate);
        // no error is thrown
    }

    @Test
    public void loadTruststoreDoNotThrowError(){
        // test for null file
        KeyStore result = testInstance.loadTruststore(null);
        assertNull(result);
        // test for file not exists
        result = testInstance.loadTruststore(new File(UUID.randomUUID().toString()));
        assertNull(result);
        // test for file credentials not exist
        Path resourceDirectory = Paths.get("src", "test", "resources", "truststore","smp-truststore.jks");
        assertTrue(resourceDirectory.toFile().exists());
        doReturn(null).when(configurationService).getTruststoreCredentialToken();
        result = testInstance.loadTruststore(resourceDirectory.toFile());
        assertNull(result);
    }
}