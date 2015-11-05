package eu.europa.ec.cipa.bdmsl.ws.soap;

import ec.services.wsdl.bdmsl.data._1.PrepareChangeCertificateType;
import eu.europa.ec.cipa.bdmsl.AbstractTest;
import eu.europa.ec.cipa.bdmsl.dao.ICertificateDAO;
import eu.europa.ec.cipa.bdmsl.security.BlueCoatClientCertificateAuthentication;
import eu.europa.ec.cipa.bdmsl.security.UnsecureAuthentication;
import eu.europa.ec.cipa.bdmsl.security.UnsecureAuthenticationRoleSMP;
import eu.europa.ec.cipa.common.util.Constant;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by feriaad on 09/07/2015.
 */
public class CipaServiceWSChangeCertificateTest extends AbstractTest {

    @Autowired
    private ICipaServiceWS cipaServiceWS;

    @Autowired
    private ICertificateDAO certificateDAO;

    private String newCertPublicKey = "-----BEGIN CERTIFICATE-----\n" +
            "MIICpTCCAg6gAwIBAgIBATANBgkqhkiG9w0BAQUFADB4MQswCQYDVQQGEwJCRTEL\n" +
            "MAkGA1UECAwCQkUxETAPBgNVBAcMCEJydXNzZWxzMQ4wDAYDVQQKDAVESUdJVDEL\n" +
            "MAkGA1UECwwCQjQxDzANBgNVBAMMBnJvb3RDTjEbMBkGCSqGSIb3DQEJARYMcm9v\n" +
            "dEB0ZXN0LmJlMB4XDTE1MDMxNzE2MTkwN1oXDTI1MDMxNDE2MTkwN1owfDELMAkG\n" +
            "A1UEBhMCQkUxCzAJBgNVBAgMAkJFMREwDwYDVQQHDAhCcnVzc2VsczEOMAwGA1UE\n" +
            "CgwFRElHSVQxCzAJBgNVBAsMAkI0MREwDwYDVQQDDAhzZW5kZXJDTjEdMBsGCSqG\n" +
            "SIb3DQEJARYOc2VuZGVyQHRlc3QuYmUwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJ\n" +
            "AoGBANxLUPjIn7R0CsHf86kIwNzCu+6AdmWM8fBLUHL+VXT6ayr1kwgGbFMb/vUU\n" +
            "X6a46jRCiZBM+9IK1Hpjg9QX/QIQiWtvD+yDr6jUxahZ/w13kqFG/K81IVu9DwLB\n" +
            "oiNwDvQ6l6UbvMvV+1nWy3gjRcKlFs/C+E2uybgJxSM/sMkbAgMBAAGjOzA5MB8G\n" +
            "A1UdIwQYMBaAFHCVSh4WnWR8MGBGedr+bJH96tc4MAkGA1UdEwQCMAAwCwYDVR0P\n" +
            "BAQDAgTwMA0GCSqGSIb3DQEBBQUAA4GBAK6idNRxyeBmqPoSKxq7Ck3ej6R2QPyW\n" +
            "bwZ+6/S7iCRt8PfgOu++Yu5YEjlUX1hlkbQKF/JuKTLqxNnKIE6Ef65+JP2ZaI9O\n" +
            "2wdzpRclAhAd00XbNKpyipr4jMdWmu2U8vyBBwn/utG1ZrLhAUiqnPvmaQrResiG\n" +
            "HM2xzCmVwtse\n" +
            "-----END CERTIFICATE-----\n";

    private String notTrustedCert = "-----BEGIN CERTIFICATE-----\n" +
            "MIIDmTCCAwKgAwIBAgIJAJGlA1lAVPGNMA0GCSqGSIb3DQEBBQUAMIGQMQswCQYD\n" +
            "VQQGEwJCRTEQMA4GA1UECBMHQnJ1c3NlbDERMA8GA1UEBxMIQnJ1c3NlbHMxCzAJ\n" +
            "BgNVBAoTAkVVMQ4wDAYDVQQLEwVESUdJVDESMBAGA1UEAxMJRWRlbGl2ZXJ5MSsw\n" +
            "KQYJKoZIhvcNAQkBFhx5b2VyaS5zbWV0c0BleHQuZWMuZXVyb3BlLmV1MB4XDTE0\n" +
            "MDYyNTA4MjE1M1oXDTI0MDYyMjA4MjE1M1owgZAxCzAJBgNVBAYTAkJFMRAwDgYD\n" +
            "VQQIEwdCcnVzc2VsMREwDwYDVQQHEwhCcnVzc2VsczELMAkGA1UEChMCRVUxDjAM\n" +
            "BgNVBAsTBURJR0lUMRIwEAYDVQQDEwlFZGVsaXZlcnkxKzApBgkqhkiG9w0BCQEW\n" +
            "HHlvZXJpLnNtZXRzQGV4dC5lYy5ldXJvcGUuZXUwgZ8wDQYJKoZIhvcNAQEBBQAD\n" +
            "gY0AMIGJAoGBAMt/b35OG6bZbDzWBEMJC29Se3QTC3B+X+BvdvrSkI51PRY5Nloz\n" +
            "Zph1UrgiDL0YDpJKEo4lkQgIB6YNlhA3G9+N8CKupLonlSlOh4zCf09HMxR4b00F\n" +
            "lETgk1p2odGdrHII/yle9kJY31zIIJkn9Ag2Uuo9FkCPRUyo6jkMcMufAgMBAAGj\n" +
            "gfgwgfUwHQYDVR0OBBYEFASzbsKMkeHDTsQd0BcMMswLeJhXMIHFBgNVHSMEgb0w\n" +
            "gbqAFASzbsKMkeHDTsQd0BcMMswLeJhXoYGWpIGTMIGQMQswCQYDVQQGEwJCRTEQ\n" +
            "MA4GA1UECBMHQnJ1c3NlbDERMA8GA1UEBxMIQnJ1c3NlbHMxCzAJBgNVBAoTAkVV\n" +
            "MQ4wDAYDVQQLEwVESUdJVDESMBAGA1UEAxMJRWRlbGl2ZXJ5MSswKQYJKoZIhvcN\n" +
            "AQkBFhx5b2VyaS5zbWV0c0BleHQuZWMuZXVyb3BlLmV1ggkAkaUDWUBU8Y0wDAYD\n" +
            "VR0TBAUwAwEB/zANBgkqhkiG9w0BAQUFAAOBgQA3pD0eEZg3Dioweaq06Or27BA1\n" +
            "UTyvGRQ3H8GTKnYWk+R7dmFOYaoHiErQYeEagXdPs2wZZ0oIX0ShwhnlH8ENkgbu\n" +
            "4dd8aZYccuQZgITFmypn6TCLiw8kZFt+dFnjgM5B7nOknLzWtuq+0msDhA3yXPW7\n" +
            "GymKf2hGQYYB3/EFnQ==\n" +
            "-----END CERTIFICATE-----\n";

    private String certificateId = "CN=SMP_TEST_CHANGE_CERTIFICATE,O=DG-DIGIT,C=BE:000000000123ABCD";

    @BeforeClass
    public static void beforeClass() throws TechnicalException, ParseException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        String serial = "000000000123ABCD";
        String issuer = "CN=rootCN,OU=B4,O=DIGIT,L=Brussels,ST=BE,C=BE";
        String subject = "CN=SMP_TEST_CHANGE_CERTIFICATE,O=DG-DIGIT,C=BE";
        DateFormat df = new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", Constant.LOCALE);
        Date validFrom = df.parse("Jun 01 10:37:53 2015 CEST");
        Date validTo = df.parse("Jun 01 10:37:53 2035 CEST");
        String certHeaderValue = "serial=" + serial + "&subject=" + subject + "&validFrom="+ df.format(validFrom) +"&validTo=" + df.format(validTo) +"&issuer=" + issuer;
        BlueCoatClientCertificateAuthentication authentication = new BlueCoatClientCertificateAuthentication(certHeaderValue);
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void prepareChangeCertificateOk() throws Exception {
        Assert.assertNull(certificateDAO.findCertificateByCertificateId(certificateId).getMigrationDate());
        Assert.assertNull(certificateDAO.findCertificateByCertificateId(certificateId).getNewCertificateId());

        PrepareChangeCertificateType preparePrepareChangeCertificateType = new PrepareChangeCertificateType();
        preparePrepareChangeCertificateType.setNewCertificatePublicKey(newCertPublicKey);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2020);
        preparePrepareChangeCertificateType.setMigrationDate(cal);
        cipaServiceWS.prepareChangeCertificate(preparePrepareChangeCertificateType);

        Assert.assertTrue(DateUtils.isSameDay(cal, certificateDAO.findCertificateByCertificateId(certificateId).getMigrationDate()));
        Assert.assertNotNull(certificateDAO.findCertificateByCertificateId(certificateId).getNewCertificateId());
    }

    @Test(expected = BadRequestFault.class)
    public void prepareChangeCertificateNull() throws Exception {
        cipaServiceWS.prepareChangeCertificate(null);
    }

    @Test(expected = BadRequestFault.class)
    public void prepareChangeCertificateEmpty() throws Exception {
        cipaServiceWS.prepareChangeCertificate(new PrepareChangeCertificateType());
    }

    /**
     * If the migrationDate is missing, then the "Valid From" date is extracted from the certificate and is used as the migrationDate.
     * @throws Exception
     */
    @Test(expected = BadRequestFault.class)
    public void prepareChangeCertificateMissingMigrationDate() throws Exception {
        PrepareChangeCertificateType preparePrepareChangeCertificateType = new PrepareChangeCertificateType();
        preparePrepareChangeCertificateType.setNewCertificatePublicKey(newCertPublicKey);
        // The "not before" date of the certificate is in March 2015: it's in the past so an exception is excepted
        cipaServiceWS.prepareChangeCertificate(preparePrepareChangeCertificateType);
    }

    @Test(expected = BadRequestFault.class)
    public void prepareChangeCertificateMigrationDateInThePast() throws Exception {
        PrepareChangeCertificateType preparePrepareChangeCertificateType = new PrepareChangeCertificateType();
        preparePrepareChangeCertificateType.setNewCertificatePublicKey(newCertPublicKey);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2010);
        preparePrepareChangeCertificateType.setMigrationDate(cal);
        cipaServiceWS.prepareChangeCertificate(preparePrepareChangeCertificateType);
    }

    @Test(expected = BadRequestFault.class)
    public void prepareChangeCertificateNewCertFormatInvalid() throws Exception {
        PrepareChangeCertificateType preparePrepareChangeCertificateType = new PrepareChangeCertificateType();
        preparePrepareChangeCertificateType.setNewCertificatePublicKey("InvalidFormat");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        preparePrepareChangeCertificateType.setMigrationDate(cal);
        cipaServiceWS.prepareChangeCertificate(preparePrepareChangeCertificateType);
    }

    @Test(expected = UnauthorizedFault.class)
    public void prepareChangeCertificateNewCertNotTrusted() throws Exception {
        PrepareChangeCertificateType preparePrepareChangeCertificateType = new PrepareChangeCertificateType();
        preparePrepareChangeCertificateType.setNewCertificatePublicKey(notTrustedCert);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        preparePrepareChangeCertificateType.setMigrationDate(cal);
        cipaServiceWS.prepareChangeCertificate(preparePrepareChangeCertificateType);
    }

    @Test(expected = BadRequestFault.class)
    public void prepareChangeCertificateMigrationDateNotWithinValidFromAndValidToDatesFromOfNewCertificate() throws Exception {
        PrepareChangeCertificateType preparePrepareChangeCertificateType = new PrepareChangeCertificateType();
        preparePrepareChangeCertificateType.setNewCertificatePublicKey(newCertPublicKey);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2035);
        preparePrepareChangeCertificateType.setMigrationDate(cal);
        cipaServiceWS.prepareChangeCertificate(preparePrepareChangeCertificateType);
    }


}
