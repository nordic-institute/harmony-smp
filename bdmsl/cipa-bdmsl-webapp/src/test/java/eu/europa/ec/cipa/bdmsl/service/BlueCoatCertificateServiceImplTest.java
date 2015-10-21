package eu.europa.ec.cipa.bdmsl.service;

import eu.europa.ec.cipa.bdmsl.AbstractTest;
import eu.europa.ec.cipa.bdmsl.security.CertificateDetails;
import eu.europa.ec.cipa.common.exception.BusinessException;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by feriaad on 22/06/2015.
 */
public class BlueCoatCertificateServiceImplTest extends AbstractTest {
    @Autowired
    private IBlueCoatCertificateService blueCoatCertificateService;

    private CertificateDetails certificateDetails;

    /**
     * For convenience puropose, builds a default certificate that we use in the tests
     * @throws ParseException
     */
    @Before
    public void setUp() throws ParseException {
        String serial = "123ABCD";
        String issuer = "CN=PEPPOL SERVICE METADATA PUBLISHER TEST CA,OU=FOR TEST PURPOSES ONLY,O=NATIONAL IT AND TELECOM AGENCY,C=DK";
        String subject = "O=DG-DIGIT,CN=SMP_1000000007,C=BE";
        DateFormat df = new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", Locale.US);
        Date validFrom = df.parse("Jun 01 10:37:53 2015 CEST");
        Date validTo = df.parse("Jun 01 10:37:53 2035 CEST");

        certificateDetails = new CertificateDetails();
        certificateDetails.setIssuer(issuer);
        certificateDetails.setSubject(subject);
        certificateDetails.setSerial(serial);
        certificateDetails.setValidFrom(DateUtils.toCalendar(validFrom));
        certificateDetails.setValidTo(DateUtils.toCalendar(validTo));
    }

    /**
     * Valid certificate
     * @throws TechnicalException
     * @throws BusinessException
     */
    @Test
    public void testIsBlueCoatClientCertificateValid() throws TechnicalException, BusinessException{
        Assert.assertTrue(blueCoatCertificateService.isBlueCoatClientCertificateValid(certificateDetails));
    }

    /**
     * Certificate is expired
     * @throws TechnicalException
     * @throws BusinessException
     * @throws ParseException
     */
    @Test
    public void testIsBlueCoatClientCertificateExpired() throws TechnicalException, BusinessException, ParseException {
        DateFormat df = new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", Locale.US);
        Date validFrom = df.parse("Jun 01 10:37:53 2009 CEST");
        Date validTo = df.parse("Jun 01 10:37:53 2010 CEST");
        certificateDetails.setValidFrom(DateUtils.toCalendar(validFrom));
        certificateDetails.setValidTo(DateUtils.toCalendar(validTo));
        Assert.assertFalse(blueCoatCertificateService.isBlueCoatClientCertificateValid(certificateDetails));
    }

    /**
     * Certificate is expired
     * @throws TechnicalException
     * @throws BusinessException
     * @throws ParseException
     */
    @Test
    public void testIsBlueCoatClientCertificateRevoked() throws TechnicalException, BusinessException, ParseException {
        certificateDetails.setSerial("04 00 00 00 00 01 1e 44 a5 e4 04");
        Assert.assertFalse(blueCoatCertificateService.isBlueCoatClientCertificateValid(certificateDetails));
    }
}
