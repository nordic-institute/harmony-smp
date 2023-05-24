package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.security.Security;
import java.security.cert.*;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;


@Ignore
public class CRLVerifierServiceTest extends AbstractServiceIntegrationTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Autowired
    private CRLVerifierService crlVerifierServiceInstance;

    @Autowired
    private ConfigurationService configurationService;


    @BeforeClass
    public static void beforeClass() throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    @Before
    public void beforeMethods() {
        crlVerifierServiceInstance = Mockito.spy(crlVerifierServiceInstance);
        configurationService = Mockito.spy(configurationService);
        ReflectionTestUtils.setField(crlVerifierServiceInstance, "configurationService", configurationService);
        // force veifiction
        Mockito.doReturn(true).when(configurationService).forceCRLValidation();
    }


    @Test
    public void verifyCertificateCRLsTest() throws CertificateException, CRLException, IOException {
        // given
        X509Certificate certificate = loadCertificate("smp-crl-test-all.pem");

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509CRL crl = (X509CRL) cf.generateCRL(getClass().getResourceAsStream("/certificates/smp-crl-test.crl"));

        Mockito.doReturn(crl).when(crlVerifierServiceInstance).getCRLByURL("https://localhost/clr");

        // when-then
        crlVerifierServiceInstance.verifyCertificateCRLs(certificate);
        // must not throw exception
    }

    @Test
    public void verifyCertificateCRLRevokedTest() throws CertificateException, CRLException, IOException {
        // given
        X509Certificate certificate = loadCertificate("smp-crl-revoked.pem");


        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509CRL crl = (X509CRL) cf.generateCRL(getClass().getResourceAsStream("/certificates/smp-crl-test.crl"));

        Mockito.doReturn(crl).when(crlVerifierServiceInstance).getCRLByURL("https://localhost/crl");

        CertificateRevokedException result = assertThrows(CertificateRevokedException.class, () -> crlVerifierServiceInstance.verifyCertificateCRLs(certificate));
        assertThat(result.getMessage(), startsWith("Certificate has been revoked, reason: UNSPECIFIED"));
    }

    @Test
    public void verifyCertificateCRLsX509FailsToConnectTest() throws CertificateException {
        // given
        X509Certificate certificate = loadCertificate("smp-crl-test-all.pem");

        expectedEx.expect(SMPRuntimeException.class);
        expectedEx.expectMessage("Certificate error [Error occurred while downloading CRL:'https://localhost/clr']. Error: ConnectException: Connection refused (Connection refused)!");

        // when-then
        crlVerifierServiceInstance.verifyCertificateCRLs(certificate);
    }

    @Test
    public void downloadCRLWrongUrlSchemeTest() throws CertificateException, CRLException, IOException {

        X509CRL crl = crlVerifierServiceInstance.downloadCRL("wrong://localhost/crl", true);

        assertNull(crl);
    }

    @Test
    public void downloadCRLUrlSchemeLdapTest() throws CertificateException, CRLException, IOException {

        X509CRL crl = crlVerifierServiceInstance.downloadCRL("ldap://localhost/crl", true);

        assertNull(crl);
    }

    @Test
    public void verifyCertificateCRLsRevokedSerialTest() throws CertificateException, CRLException, IOException {

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509CRL crl = (X509CRL) cf.generateCRL(getClass().getResourceAsStream("/certificates/smp-crl-test.crl"));

        Mockito.doReturn(crl).when(crlVerifierServiceInstance).downloadCRL("https://localhost/crl", true);

        CertificateRevokedException result = assertThrows(CertificateRevokedException.class, () ->crlVerifierServiceInstance.verifyCertificateCRLs("11", "https://localhost/crl"));
        assertThat(result.getMessage(), startsWith("Certificate has been revoked, reason: UNSPECIFIED"));
    }

    @Test
    public void verifyCertificateCRLsRevokedSerialTestThrowIOExceptionHttps() throws CertificateException, IOException, CRLException {
        String crlURL = "https://localhost/crl";

        Mockito.doThrow(new SMPRuntimeException(ErrorCode.CERTIFICATE_ERROR, "Can not download CRL '" + crlURL + "'", "IOException: Can not access URL")).when(crlVerifierServiceInstance).downloadCRL("https://localhost/crl", true);

        expectedEx.expect(SMPRuntimeException.class);
        expectedEx.expectMessage("Certificate error [Can not download CRL 'https://localhost/crl']. Error: IOException: Can not access URL!");

        // when-then
        crlVerifierServiceInstance.verifyCertificateCRLs("11", "https://localhost/crl");
    }

    private X509Certificate loadCertificate(String filename) throws CertificateException {
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        X509Certificate cer = (X509Certificate)
                fact.generateCertificate(getClass().getResourceAsStream("/certificates/" + filename));
        return cer;
    }
}
