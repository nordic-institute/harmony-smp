package eu.europa.ec.cipa.smp.server.services;


import eu.europa.ec.cipa.smp.server.AbstractTest;
import eu.europa.ec.cipa.smp.server.errors.exceptions.CertificateNotFoundException;
import eu.europa.ec.cipa.smp.server.errors.exceptions.CertificateRevokedException;
import eu.europa.ec.cipa.smp.server.security.BlueCoatClientCertificateAuthentication;
import eu.europa.ec.cipa.smp.server.security.CertificateDetails;
import eu.europa.ec.cipa.smp.server.util.CommonUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.security.Security;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by feriaad on 22/06/2015.
 */
public class BlueCoatCertificateServiceImplTest extends AbstractTest {


    private IBlueCoatCertificateService blueCoatCertificateService;

    private CertificateDetails certificateDetails;

    /**
     * For convenience puropose, builds a default certificate that we use in the tests
     *
     * @throws ParseException
     */
    @Before
    public void setUp() throws Exception {
        if (blueCoatCertificateService == null) {
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"classpath:applicationContext.xml"});
            blueCoatCertificateService = (IBlueCoatCertificateService) context.getBean("blueCoatCertificateServiceImpl");
        }

        String serial = "123ABCD";
        String issuer = "CN=PEPPOL SERVICE METADATA PUBLISHER TEST CA,OU=FOR TEST PURPOSES ONLY,O=NATIONAL IT AND TELECOM AGENCY,C=DK";
        String subject = "O=DG-DIGIT,CN=SMP_1000000007,C=BE";
        DateFormat df = new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", Locale.US);
        Date validFrom = df.parse("Jun 01 10:37:53 2015 CEST");
        Date validTo = df.parse("Jun 01 10:37:53 2035 CEST");

        certificateDetails = CommonUtil.createCertificateForBlueCoat(serial, issuer, subject, validFrom, validTo);
    }

    /**
     * Valid certificate
     *
     * @throws Exception
     */
    @Test
    public void testIsBlueCoatClientCertificateValid() throws Exception {
        System.out.println("certificateDetails #" + certificateDetails);
        System.out.println("blueCoatCertificateService #" + blueCoatCertificateService);
        Object result = blueCoatCertificateService.isBlueCoatClientCertificateValid(certificateDetails);
        System.out.println("result #" + result);

        // Assert.assertTrue(result);
    }

    /**
     * Valid certificate but the order of the certificate attributes of the subject are different. The certificate must be valid anyway
     *
     * @throws Exception
     */
    @Test
    public void testIsBlueCoatClientCertificateValidSubjectDifferentOrder1() throws Exception {
        certificateDetails.setSubject("C=BE,O=DG-DIGIT,CN=SMP_1000000007");

        Assert.assertTrue(blueCoatCertificateService.isBlueCoatClientCertificateValid(certificateDetails));
    }

    @Test
    public void testIsBlueCoatClientCertificateValidSubjectNotOk() throws Exception {
        certificateDetails.setCertificateId("C=BE,O=DG-DIGIT");
        certificateDetails.setSubject("C=BE,O=DG-DIGIT");

        Assert.assertFalse(blueCoatCertificateService.isBlueCoatClientCertificateValid(certificateDetails));
    }

    /**
     * Valid certificate but the order of the certificate attributes of the subject are different and contain spaces after the commas. The certificate must be valid anyway
     *
     * @throws Exception
     */
    @Test
    public void testIsBlueCoatClientCertificateValidSubjectDifferentOrderWithSpaces() throws Exception {
        certificateDetails.setSubject("C=BE, CN=SMP_1000000007, O=DG-DIGIT");

        Assert.assertTrue(blueCoatCertificateService.isBlueCoatClientCertificateValid(certificateDetails));
    }

    /**
     * Valid certificate but the order of the certificate attributes of the issuer are different. The certificate must be valid anyway
     *
     * @throws Exception
     */
    @Test
    public void testIsBlueCoatClientCertificateValidIssuerDifferentOrder() throws Exception {
        certificateDetails.setIssuer("OU=FOR TEST PURPOSES ONLY,CN=PEPPOL SERVICE METADATA PUBLISHER TEST CA,O=NATIONAL IT AND TELECOM AGENCY,C=DK");

        Assert.assertTrue(blueCoatCertificateService.isBlueCoatClientCertificateValid(certificateDetails));
    }

    /**
     * Valid certificate but the order of the certificate attributes of the issuer are different and contain spaces after the commas. The certificate must be valid anyway
     *
     * @throws Exception
     */
    @Test
    public void testIsBlueCoatClientCertificateValidIssuerDifferentOrderWithSpaces() throws Exception {
        certificateDetails.setIssuer("OU=FOR TEST PURPOSES ONLY, C=DK, CN=PEPPOL SERVICE METADATA PUBLISHER TEST CA, O=NATIONAL IT AND TELECOM AGENCY");

        Assert.assertTrue(blueCoatCertificateService.isBlueCoatClientCertificateValid(certificateDetails));
    }

    /**
     * Certificate is expired
     *
     * @throws Exception
     */
    @Test
    public void testIsBlueCoatClientCertificateExpired() throws Exception {
        DateFormat df = new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", Locale.US);
        Date validFrom = df.parse("Jun 01 10:37:53 2009 CEST");
        Date validTo = df.parse("Jun 01 10:37:53 2010 CEST");
        certificateDetails.setValidFrom(DateUtils.toCalendar(validFrom));
        certificateDetails.setValidTo(DateUtils.toCalendar(validTo));
        Assert.assertFalse(blueCoatCertificateService.isBlueCoatClientCertificateValid(certificateDetails));
    }

    /**
     * Certificate is expired
     *
     * @throws Exception
     */
    @Test
    public void testIsBlueCoatClientCertificateRevoked() throws Exception {
        String serial = "04 00 00 00 00 01 1e 44 a5 e4 04";
        String issuer = "CN=PUBLISHER CA,OU=FOR TEST ONLY,O=NATIONAL AGENCY,C=IT";
        String subject = "O=DG-DIGIT,CN=SMP_951752645454545,C=BE";

        Calendar validFrom = Calendar.getInstance();
        validFrom.set(validFrom.get(Calendar.YEAR) + 5, 1, 1);

        Calendar validTo = Calendar.getInstance();
        validTo.set(validTo.get(Calendar.YEAR) + 10, 1, 1);

        certificateDetails = CommonUtil.createCertificateForBlueCoat(serial, issuer, subject, validFrom.getTime(), validTo.getTime());
        Assert.assertFalse(blueCoatCertificateService.isBlueCoatClientCertificateValid(certificateDetails));
    }

    /**
     * Certificate is expired
     *
     * @throws Exception
     */
    @Test(expected = CertificateRevokedException.class)
    public void testIsBlueCoatClientCertificateRevokedException() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR) + 1, 1, 1);
        certificateDetails.setValidFrom(calendar);
        certificateDetails.setSerial("04 00 00 00 00 01 1e 44 a5 e4 04");
        blueCoatCertificateService.validateBlueCoatClientCertificate(certificateDetails);
    }

    @Test(expected = CertificateNotFoundException.class)
    public void testIsBlueCoatClientCertificateNotFoundException() throws Exception {
        String serial = "951752645454545";
        String issuer = "CN=PUBLISHER CA,OU=FOR TEST ONLY,O=NATIONAL AGENCY,C=IT";
        String subject = "O=DG-DIGIT,CN=SMP_951752645454545,C=BE";
        DateFormat df = new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", Locale.US);
        Date validFrom = df.parse("Jun 01 10:37:53 2015 CEST");
        Date validTo = df.parse("Jun 01 10:37:53 2035 CEST");

        certificateDetails = CommonUtil.createCertificateForBlueCoat(serial, issuer, subject, validFrom, validTo);
        blueCoatCertificateService.validateBlueCoatClientCertificate(certificateDetails);
    }

    /**
     * Valid certificate for NonRootCA
     *
     * @throws Exception
     */
    @Test
    public void testIsBlueCoatClientCertificateValidForNonRootCAOk() throws Exception {
        String serial = "123ABCD";
        String issuer = "CN=123ABCD,C=BE,O=DG-DIGIT";
        String subject = "O=DG-DIGIT,C=BE,CN=EHEALTH_SMP_1000000007";
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.YEAR, -5);
        Calendar nextYearToday = Calendar.getInstance();
        nextYearToday.add(Calendar.YEAR, 20);
        certificateDetails = CommonUtil.createCertificateForBlueCoat(serial, issuer, subject, startDate.getTime(), nextYearToday.getTime());

        Assert.assertTrue(blueCoatCertificateService.isBlueCoatClientCertificateValid(certificateDetails));
    }

    /**
     * Not Valid certificate for NonRootCA
     *
     * @throws Exception
     */
    @Test
    public void testIsBlueCoatClientCertificateValidForNonRootCANotOk() throws Exception {
        String serial = "0123456789101112";
        String issuer = "O=DG-DIGIT,CN=SMP_123456789101112,C=BE";
        String subject = "O=DG-DIGIT,C=BE,CN=SMP_1234567891011121314";
        Calendar nextYearToday = Calendar.getInstance();
        nextYearToday.add(Calendar.YEAR, 20);
        certificateDetails = CommonUtil.createCertificateForBlueCoat(serial, issuer, subject, new Date(), nextYearToday.getTime());
        Assert.assertFalse(blueCoatCertificateService.isBlueCoatClientCertificateValid(certificateDetails));
    }

    /**
     * Valid certificate for RootCA
     *
     * @throws Exception
     */
    @Test
    public void testIsBlueCoatClientCertificateValidForRootCAOk() throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        String serial = "123ABCD";
        String issuer = "CN=PEPPOL SERVICE METADATA PUBLISHER TEST CA,OU=FOR TEST PURPOSES ONLY,O=NATIONAL IT AND TELECOM AGENCY,C=DK";
        String subject = "O=DG-DIGIT,CN=SMP_1000000007,C=BE";
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.YEAR, -5);
        Calendar nextYearToday = Calendar.getInstance();
        nextYearToday.add(Calendar.YEAR, 20);
        certificateDetails = CommonUtil.createCertificateForBlueCoat(serial, issuer, subject, startDate.getTime(), nextYearToday.getTime());

        Assert.assertTrue(blueCoatCertificateService.isBlueCoatClientCertificateValid(certificateDetails));
    }

    /**
     * Valid certificate for RootCA
     *
     * @throws Exception
     */
    @Test
    public void testIsBlueCoatClientCertificateValidForRootCANotOk() throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        String serial = "0123456789101112";
        String issuer = "CN=PEPPOL SERVICE METADATA PUBLISHER,O=NATIONAL IT AND TELECOM AGENCY,C=BE";
        String subject = "O=DG-DIGIT,C=BE,CN=SMP_9999999999999991525";
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.YEAR, -5);
        Calendar nextYearToday = Calendar.getInstance();
        nextYearToday.add(Calendar.YEAR, 20);

        certificateDetails = CommonUtil.createCertificateForBlueCoat(serial, issuer, subject, startDate.getTime(), nextYearToday.getTime());
        Assert.assertFalse(blueCoatCertificateService.isBlueCoatClientCertificateValid(certificateDetails));
    }

    @Test
    public void testIsBlueCoatClientCertificateValidOk() throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        String certHeader = "sno=f7%3A1e%3Ae8%3Ab1%3A1c%3Ab3%3Ab7%3A87&amp;subject=C%3DBE%2C+O%3DEuropean+Commission%2C+OU%3DCEF_eDelivery.europa.eu%2C+OU%3DeHealth%2C+OU%3DSMP_TEST%2C+CN%3DEHEALTH_SMP_EC%2FemailAddress%3DCEF-EDELIVERY-SUPPORT%40ec.europa.eu&amp;validfrom=Dec++6+17%3A41%3A42+2016+GMT&amp;validto=Jul++9+23%3A59%3A00+2019+GMT&amp;issuer=C%3DDE%2C+O%3DT-Systems+International+GmbH%2C+OU%3DT-Systems+Trust+Center%2C+ST%3DNordrhein+Westfalen%2FpostalCode%3D57250%2C+L%3DNetphen%2Fstreet%3DUntere+Industriestr.+20%2C+CN%3DShared+Business+CA+4&amp;policy_oids=1.3.6.1.4.1.7879.13.25";
        BlueCoatClientCertificateAuthentication auth = new BlueCoatClientCertificateAuthentication(certHeader);

        Assert.assertTrue(blueCoatCertificateService.isBlueCoatClientCertificateValid(((CertificateDetails) auth.getDetails())));
    }
}
