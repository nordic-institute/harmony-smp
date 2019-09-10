package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.exceptions.CertificateNotTrustedException;
import eu.europa.ec.edelivery.smp.services.AbstractServiceIntegrationTest;
import eu.europa.ec.edelivery.smp.services.CRLVerifierService;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.testutil.X509CertificateTestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.cert.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;



@RunWith(SpringJUnit4ClassRunner.class)
public class UITruststoreServiceTest  extends AbstractServiceIntegrationTest {

    public static final String S_SUBJECT_PEPPOL = "CN=POP000004,OU=PEPPOL TEST AP,O=European Commission,C=BE";
    public static final String S_SUBJECT_PEPPOL_EXPANDED = "serialNumber=12345,emailAddress=test@mail.com,CN=POP000004,OU=PEPPOL TEST AP,O=European Commission,street=My Street,C=BE";
    public static final String S_SUBJECT_PEPPOL_NOT_TRUSTED = "CN=POP000005,OU=PEPPOL TEST AP,O=European Commission,C=BE";

    public static final String S_SUBJECT_TEST = "CN=SMP test,O=DIGIT,C=BE";


    Path resourceDirectory = Paths.get("src", "test", "resources",  "truststore");
    Path targetDirectory = Paths.get("target","truststore");

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Autowired
    protected UITruststoreService testInstance;

    @Autowired
    protected ConfigurationService configurationService;

    @Autowired
    protected CRLVerifierService crlVerifierService;

    @Before
    public void setup() throws IOException {
        configurationService = Mockito.spy(configurationService);
        crlVerifierService = Mockito.spy(crlVerifierService);

        ReflectionTestUtils.setField(testInstance,"crlVerifierService",crlVerifierService);

        ReflectionTestUtils.setField(testInstance,"configurationService",configurationService);

        File truststoreFile = new File(targetDirectory.toFile(), "smp-truststore.jks");
        Mockito.doReturn("test123").when(configurationService).getTruststoreCredentialToken();
        Mockito.doReturn(truststoreFile).when(configurationService).getTruststoreFile();
        Mockito.doReturn(targetDirectory.toFile()).when(configurationService).getConfigurationFolder();
        resetKeystore();

        testInstance.refreshData();
    }

    public void resetKeystore() throws IOException {
        FileUtils.deleteDirectory(targetDirectory.toFile());
        FileUtils.copyDirectory(resourceDirectory.toFile(), targetDirectory.toFile());
    }

    @Test
    public void testGetKeystoreEntriesList() {
        List<String> lst = testInstance.getNormalizedTrustedList();
        assertEquals(2, lst.size());
        assertEquals(S_SUBJECT_PEPPOL, lst.get(0));
        assertEquals(S_SUBJECT_TEST, lst.get(1));
    }

    @Test
    public void testSubjectValid() {
        // given when
        // then
        assertTrue(testInstance.isSubjectOnTrustedList(S_SUBJECT_PEPPOL));
    }

    @Test
    public void testSubjectValidExpanded() {
        // given when
        // then
        assertTrue(testInstance.isSubjectOnTrustedList(S_SUBJECT_PEPPOL_EXPANDED));
    }

    @Test
    public void testSubjectNotTrusted() {
        // given when
        // then
        assertFalse(testInstance.isSubjectOnTrustedList(S_SUBJECT_PEPPOL_NOT_TRUSTED));
    }

    @Test
    public void testAddCertificate()  throws Exception {
        // given
        String certSubject="CN=SMP Test,OU=eDelivery,O=DIGITAL,C=BE";
        String alias =UUID.randomUUID().toString();
        X509Certificate certificate = X509CertificateTestUtils.createX509CertificateForTest(certSubject);
        int iSize = testInstance.getNormalizedTrustedList().size();
        assertFalse(testInstance.isSubjectOnTrustedList(certSubject));
        // when
        testInstance.addCertificate(alias, certificate );

        // then
        assertEquals(iSize+1, testInstance.getNormalizedTrustedList().size());
        assertTrue(testInstance.isSubjectOnTrustedList(certSubject));
    }

    @Test
    public void testDeleteCertificate()  throws Exception {
        // given
        List<CertificateRO> list = testInstance.getCertificateROEntriesList();
        int iSize = list.size();
        assertTrue(list.size()>0);
        CertificateRO certificateRO = list.get(0);
        assertTrue(testInstance.isSubjectOnTrustedList(certificateRO.getSubject()));
        // when
        testInstance.deleteCertificate(certificateRO.getAlias());

        // then
        assertEquals(iSize-1, testInstance.getNormalizedTrustedList().size());
        assertFalse(testInstance.isSubjectOnTrustedList(certificateRO.getSubject()));
    }

    @Test
    public void testIsTruststoreChanged()  throws Exception {
        // given
        String certSubject="CN=SMP Test,OU=eDelivery,O=DIGITAL,C=BE";
        String alias =UUID.randomUUID().toString();
        X509Certificate certificate = X509CertificateTestUtils.createX509CertificateForTest(certSubject);
        testInstance.addCertificate(alias, certificate );
        assertTrue(testInstance.isSubjectOnTrustedList(certSubject));
        // when rollback truststore
        resetKeystore();
        // then it should detect file change and refresh certificates
        assertFalse(testInstance.isSubjectOnTrustedList(certSubject));
    }


    @Test
    public void testGetCertificateDataPEM() throws IOException, CertificateException {
        // given
        byte[] buff = IOUtils.toByteArray(UIUserServiceIntegrationTest.class.getResourceAsStream("/truststore/SMPtest.crt"));

        // when
        CertificateRO cer = testInstance.getCertificateData(buff);

        //then
        assertEquals("CN=SMP test,O=DIGIT,C=BE:0000000000000003", cer.getCertificateId());
        assertEquals("CN=Intermediate CA,O=DIGIT,C=BE", cer.getIssuer());
        assertEquals("1.2.840.113549.1.9.1=#160c736d7040746573742e636f6d,CN=SMP test,O=DIGIT,C=BE", cer.getSubject());
        assertEquals("3", cer.getSerialNumber());
        assertNotNull(cer.getValidFrom());
        assertNotNull(cer.getValidTo());
        assertTrue(cer.getValidFrom().before(cer.getValidTo()));
    }

    @Test
    public void testGetCertificateDataPEMWithHeader() throws IOException, CertificateException {
        // given
        byte[] buff = IOUtils.toByteArray(UIUserServiceIntegrationTest.class.getResourceAsStream("/truststore/pem-with-header.crt"));

        // when
        CertificateRO cer = testInstance.getCertificateData(buff);

        //then
        assertEquals("CN=alice,O=www.freelan.org,C=FR:0000000000000001", cer.getCertificateId());
        assertEquals("1.2.840.113549.1.9.1=#1613636f6e7461637440667265656c616e2e6f7267,CN=Freelan Sample Certificate Authority,OU=freelan,O=www.freelan.org,L=Strasbourg,ST=Alsace,C=FR", cer.getIssuer());
        assertEquals("1.2.840.113549.1.9.1=#1613636f6e7461637440667265656c616e2e6f7267,CN=alice,OU=freelan,O=www.freelan.org,ST=Alsace,C=FR", cer.getSubject());
        assertEquals("1", cer.getSerialNumber());
        assertNotNull(cer.getValidFrom());
        assertNotNull(cer.getValidTo());
        assertTrue(cer.getValidFrom().before(cer.getValidTo()));
    }

    @Test
    public void testGetCertificateDataSMime() throws IOException, CertificateException {
        // given
        byte[] buff = IOUtils.toByteArray(UIUserServiceIntegrationTest.class.getResourceAsStream("/certificates/cert-smime.pem"));

        // when
        CertificateRO cer = testInstance.getCertificateData(buff);

        //then
        assertEquals("CN=edelivery_sml,O=European Commission,C=BE:3cfe6b37e4702512c01e71f9b9175464", cer.getCertificateId());
        assertEquals("CN=PEPPOL SERVICE METADATA PUBLISHER TEST CA - G2,OU=FOR TEST ONLY,O=OpenPEPPOL AISBL,C=BE", cer.getIssuer());
        assertEquals("C=BE,O=European Commission,OU=PEPPOL TEST SMP,CN=edelivery_sml", cer.getSubject());
        assertEquals("3cfe6b37e4702512c01e71f9b9175464", cer.getSerialNumber());
        assertNotNull(cer.getValidFrom());
        assertNotNull(cer.getValidTo());
        assertTrue(cer.getValidFrom().before(cer.getValidTo()));
    }

    @Test
    public void testGetCertificateDataDER() throws IOException, CertificateException {
        // given
        byte[] buff = IOUtils.toByteArray(UIUserServiceIntegrationTest.class.getResourceAsStream("/truststore/NewPeppolAP.crt"));

        // when
        CertificateRO cer = testInstance.getCertificateData(buff);

        //then
        assertEquals("CN=POP000004,O=European Commission,C=BE:474980c51478cf62761667461aef5e8e", cer.getCertificateId());
        assertEquals("CN=PEPPOL ACCESS POINT TEST CA - G2,OU=FOR TEST ONLY,O=OpenPEPPOL AISBL,C=BE", cer.getIssuer());
        assertEquals("C=BE,O=European Commission,OU=PEPPOL TEST AP,CN=POP000004", cer.getSubject());
        assertEquals("474980c51478cf62761667461aef5e8e", cer.getSerialNumber());
        assertNotNull(cer.getValidFrom());
        assertNotNull(cer.getValidTo());
        assertTrue(cer.getValidFrom().before(cer.getValidTo()));
    }

    @Test
    public void testCheckFullCertificateValidityNotYetValid() throws Exception {
        // given
        String certSubject="CN=SMP Test,OU=eDelivery,O=DIGITAL,C=BE";
        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        to.add(Calendar.DAY_OF_YEAR,  2);
        from.add(Calendar.DAY_OF_YEAR, 1);
        X509Certificate certificate = X509CertificateTestUtils.createX509CertificateForTest(
                "10af", certSubject, certSubject, from.getTime(), to.getTime(), Collections.emptyList());

        //then
        expectedEx.expect(CertificateNotYetValidException.class);
        // when
        testInstance.checkFullCertificateValidity(certificate);
    }

    @Test
    public void testCheckFullCertificateValidityExpired() throws Exception {
        // given
        String certSubject="CN=SMP Test,OU=eDelivery,O=DIGITAL,C=BE";
        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        to.add(Calendar.DAY_OF_YEAR,  -1);
        from.add(Calendar.DAY_OF_YEAR, -2);
        X509Certificate certificate = X509CertificateTestUtils.createX509CertificateForTest(
                "10af", certSubject, certSubject, from.getTime(), to.getTime(), Collections.emptyList());

        //then
        expectedEx.expect(CertificateExpiredException.class);
        // when
        testInstance.checkFullCertificateValidity(certificate);
    }

    @Test
    public void testCheckFullCertificateNotTrusted() throws Exception {
        // given
        String crlUrl = "https://localhost/crl";
        String revokedSerialFromList="0011";
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509CRL crl = (X509CRL) cf.generateCRL(getClass().getResourceAsStream("/certificates/smp-crl-test.crl"));

        Mockito.doReturn(crl).when(crlVerifierService).getCRLByURL(crlUrl);

        String certSubject="CN=SMP Test,OU=eDelivery,O=DIGITAL,C=BE";
        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        to.add(Calendar.DAY_OF_YEAR,  1);
        from.add(Calendar.DAY_OF_YEAR, -2);
        X509Certificate certificate = X509CertificateTestUtils.createX509CertificateForTest(
                revokedSerialFromList, certSubject, certSubject, from.getTime(), to.getTime(), Collections.singletonList(crlUrl));
        //then
        expectedEx.expect(CertificateNotTrustedException.class);
        // when
        testInstance.checkFullCertificateValidity(certificate);
    }


    @Test
    public void testCheckFullCertificateValidityRevoked() throws Exception {
        // given
        String crlUrl = "https://localhost/crl";
        String revokedSerialFromList="0011";
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509CRL crl = (X509CRL) cf.generateCRL(getClass().getResourceAsStream("/certificates/smp-crl-test.crl"));

        Mockito.doReturn(crl).when(crlVerifierService).downloadCRL(crlUrl);

        String certSubject="CN=SMP Test,OU=eDelivery,O=DIGITAL,C=BE";
        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        to.add(Calendar.DAY_OF_YEAR,  1);
        from.add(Calendar.DAY_OF_YEAR, -2);
        X509Certificate certificate = X509CertificateTestUtils.createX509CertificateForTest(
                revokedSerialFromList, certSubject, certSubject, from.getTime(), to.getTime(), Collections.singletonList(crlUrl));
        // add as trusted certificate
         testInstance.addCertificate(UUID.randomUUID().toString(), certificate);


        //then
        expectedEx.expect(CertificateRevokedException.class);
        // when
        testInstance.checkFullCertificateValidity(certificate);
    }

    @Test
    public void testCheckFullCertificateValidityNotForceCRL() throws Exception {
        // given
        String crlUrl = "https://localhost/crl";
        String revokedSerialFromList="0011";
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509CRL crl = (X509CRL) cf.generateCRL(getClass().getResourceAsStream("/certificates/smp-crl-test.crl"));

        Mockito.doThrow(new IOException("Can not connect to " + crlUrl)).when(crlVerifierService).downloadCRL(crlUrl);
        Mockito.doReturn(false).when(configurationService).forceCRLValidation();
        String certSubject="CN=SMP Test,OU=eDelivery,O=DIGITAL,C=BE";
        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        to.add(Calendar.DAY_OF_YEAR,  1);
        from.add(Calendar.DAY_OF_YEAR, -2);
        X509Certificate certificate = X509CertificateTestUtils.createX509CertificateForTest(
                revokedSerialFromList, certSubject, certSubject, from.getTime(), to.getTime(), Collections.singletonList(crlUrl));
        // add as trusted certificate
        testInstance.addCertificate(UUID.randomUUID().toString(), certificate);


        //then sholud be thrown CertificateRevokedException but is not
        // when
        testInstance.checkFullCertificateValidity(certificate);
    }

    @Test
    public void testCheckFullCertificateValidityOK() throws Exception {
        // given
        String crlUrl = "https://localhost/crl";
        String serialNotInList="20011FF";
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509CRL crl = (X509CRL) cf.generateCRL(getClass().getResourceAsStream("/certificates/smp-crl-test.crl"));

        Mockito.doReturn(crl).when(crlVerifierService).downloadCRL(crlUrl);

        String certSubject="CN=SMP Test,OU=eDelivery,O=DIGITAL,C=BE";
        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        to.add(Calendar.DAY_OF_YEAR,  1);
        from.add(Calendar.DAY_OF_YEAR, -2);
        X509Certificate certificate = X509CertificateTestUtils.createX509CertificateForTest(
                serialNotInList, certSubject, certSubject, from.getTime(), to.getTime(), Collections.singletonList(crlUrl));
        // add as trusted certificate
        testInstance.addCertificate(UUID.randomUUID().toString(), certificate);

        // when
        testInstance.checkFullCertificateValidity(certificate);

        // then
        //no erroros should be thrown
    }

}