package eu.europa.ec.cipa.smp.server.security;

import eu.europa.ec.cipa.smp.server.AbstractTest;
import eu.europa.ec.cipa.smp.server.errors.exceptions.AuthenticationException;
import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by feriaad on 22/06/2015.
 */
public class BlueCoatClientCertificateAuthenticationTest  extends AbstractTest {

    @Test
    public void testRead() throws Exception {
        String serial = "123ABCD";
        String issuer = "CN=PEPPOL SERVICE METADATA PUBLISHER TEST CA,OU=FOR TEST PURPOSES ONLY,O=NATIONAL IT AND TELECOM AGENCY,C=DK";
        String subject = "O=DG-DIGIT,CN=SMP_1000000007,C=BE";
        DateFormat df = new SimpleDateFormat("MMM d kk:mm:ss yyyy zzz", Locale.US);
        Calendar validFrom = Calendar.getInstance();
        validFrom.set(validFrom.get(Calendar.YEAR) - 2, 1, 1);

        Calendar validTo = Calendar.getInstance();
        validTo.set(validTo.get(Calendar.YEAR) + 3, 1, 1);
        String certHeaderValue = "serial=" + serial + "&subject=" + subject + "&validFrom="+ df.format(validFrom.getTime()) +"&validTo=" + df.format(validTo.getTime()) +"&issuer=" + issuer;

        BlueCoatClientCertificateAuthentication bcAuth = new BlueCoatClientCertificateAuthentication(certHeaderValue);

        Assert.assertEquals(serial, ((CertificateDetails) bcAuth.getCredentials()).getSerial());
        Assert.assertEquals(issuer, ((CertificateDetails) bcAuth.getCredentials()).getIssuer());
        Assert.assertEquals("CN=SMP_1000000007,O=DG-DIGIT,C=BE", ((CertificateDetails) bcAuth.getCredentials()).getSubject());
        Assert.assertEquals(validFrom.getTime().toString(), ((CertificateDetails) bcAuth.getCredentials()).getValidFrom().getTime().toString());
        Assert.assertEquals(validTo.getTime().toString(), ((CertificateDetails) bcAuth.getCredentials()).getValidTo().getTime().toString());
        Assert.assertEquals("CN=SMP_1000000007,O=DG-DIGIT,C=BE:000000000123ABCD", bcAuth.getPrincipal().toString());
        Assert.assertNull((List)bcAuth.getAuthorities());

    }

    /**
     * The order of the certificate attributes is different and there are spaces after the commas. The certificate must be valid anyway
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    @Test
    public void testReadDifferentOrderWithSpaces() throws Exception {
        String serial = "123ABCD";
        String issuer = "C=DK, CN=PEPPOL SERVICE METADATA PUBLISHER TEST CA, O=NATIONAL IT AND TELECOM AGENCY, OU=FOR TEST PURPOSES ONLY";
        String subject = "C=BE, O=DG-DIGIT, CN=SMP_1000000007";
        DateFormat df = new SimpleDateFormat("MMM d kk:mm:ss yyyy zzz", Locale.US);
        Calendar validFrom = Calendar.getInstance();
        validFrom.set(validFrom.get(Calendar.YEAR) - 2, 1, 1);
        Calendar validTo = Calendar.getInstance();
        validTo.set(validTo.get(Calendar.YEAR) + 3, 1, 1);
        String certHeaderValue = "serial=" + serial + "&subject=" + subject + "&validFrom="+ df.format(validFrom.getTime()) +"&validTo=" + df.format(validTo.getTime()) +"&issuer=" + issuer;

        BlueCoatClientCertificateAuthentication bcAuth = new BlueCoatClientCertificateAuthentication(certHeaderValue);

        Assert.assertEquals(serial, ((CertificateDetails) bcAuth.getCredentials()).getSerial());
        Assert.assertEquals(issuer, ((CertificateDetails) bcAuth.getCredentials()).getIssuer());
        Assert.assertEquals("CN=SMP_1000000007,O=DG-DIGIT,C=BE", ((CertificateDetails) bcAuth.getCredentials()).getSubject());
        Assert.assertEquals(validFrom.getTime().toString(), ((CertificateDetails) bcAuth.getCredentials()).getValidFrom().getTime().toString());
        Assert.assertEquals(validTo.getTime().toString(), ((CertificateDetails) bcAuth.getCredentials()).getValidTo().getTime().toString());
        Assert.assertEquals("CN=SMP_1000000007,O=DG-DIGIT,C=BE:000000000123ABCD", bcAuth.getPrincipal().toString());
        Assert.assertNull((List)bcAuth.getAuthorities());
    }

    /**
     * The order of the header (serial, subject, etc.) is different
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    @Test
    public void testReadDifferentOrderWithSpaces2() throws Exception {
        String serial = "123ABCD";
        // different order for the issuer certificate with extra spaces
        String issuer = "CN=PEPPOL SERVICE METADATA PUBLISHER TEST CA,  C=DK, O=NATIONAL IT AND TELECOM AGENCY,    OU=FOR TEST PURPOSES ONLY";
        String subject = "C=BE, O=DG-DIGIT, CN=SMP_1000000007";
        DateFormat df = new SimpleDateFormat("MMM d kk:mm:ss yyyy zzz", Locale.US);
        Calendar validFrom = Calendar.getInstance();
        validFrom.set(validFrom.get(Calendar.YEAR) - 2, 1, 1);
        Calendar validTo = Calendar.getInstance();
        validTo.set(validTo.get(Calendar.YEAR) + 3, 1, 1);
        // case insensitivity test
        String certHeaderValue = "iSsUeR=" + issuer  + "&VaLiDFrOm="+ df.format(validFrom.getTime()) + "&sUbJecT=" + subject + "&VALidTo=" + df.format(validTo.getTime())  + "&serIAL=" + serial;

        BlueCoatClientCertificateAuthentication bcAuth = new BlueCoatClientCertificateAuthentication(certHeaderValue);

        Assert.assertEquals(serial, ((CertificateDetails) bcAuth.getCredentials()).getSerial());
        Assert.assertEquals(issuer, ((CertificateDetails) bcAuth.getCredentials()).getIssuer());
        Assert.assertEquals("CN=SMP_1000000007,O=DG-DIGIT,C=BE", ((CertificateDetails) bcAuth.getCredentials()).getSubject());
        Assert.assertEquals(validFrom.getTime().toString(), ((CertificateDetails) bcAuth.getCredentials()).getValidFrom().getTime().toString());
        Assert.assertEquals(validTo.getTime().toString(), ((CertificateDetails) bcAuth.getCredentials()).getValidTo().getTime().toString());
        Assert.assertEquals("CN=SMP_1000000007,O=DG-DIGIT,C=BE:000000000123ABCD", bcAuth.getPrincipal().toString());
        Assert.assertNull((List)bcAuth.getAuthorities());
    }

    /**
     * Test the construction of the Authenticator from real values from the BlueCoat proxy
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    @Test
    public void calculateCertificateId() throws Exception {
        String certHeader = "sno=53%3Aef%3A79%3Ac3%3A54%3A98%3Abb%3A63%3A38%3A35%3A9a%3A19%3A5d%3A2d%3Ad8%3A8c&subject=C%3DBE%2C+O%3DDG-DIGIT%2C+CN%3DSMP_1000000007&validfrom=Oct+21+00%3A00%3A00+2014+GMT&validto=Oct+20+23%3A59%3A59+2016+GMT&issuer=C%3DDK%2C+O%3DNATIONAL+IT+AND+TELECOM+AGENCY%2C+OU%3DFOR+TEST+PURPOSES+ONLY%2C+CN%3DPEPPOL+SERVICE+METADATA+PUBLISHER+TEST+CA";
        BlueCoatClientCertificateAuthentication auth = new BlueCoatClientCertificateAuthentication(certHeader);
        Assert.assertEquals("CN=SMP_1000000007,O=DG-DIGIT,C=BE:53ef79c35498bb6338359a195d2dd88c", auth.getName());
        Assert.assertEquals("CN=SMP_1000000007,O=DG-DIGIT,C=BE:53ef79c35498bb6338359a195d2dd88c", ((CertificateDetails)auth.getDetails()).getCertificateId());
        Assert.assertEquals("CN=SMP_1000000007,O=DG-DIGIT,C=BE", ((CertificateDetails)auth.getDetails()).getSubject());
    }

    /**
     * Test the construction of the Authenticator from real values from the BlueCoat proxy
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    @Test
    public void calculateCertificateIdForEhealth1() throws Exception {
        String certHeader = "sno=48%3Ab6%3A81%3Aee%3A8e%3A0d%3Acc%3A08&amp;subject=C%3DBE%2C+O%3DEuropean+Commission%2C+OU%3DCEF_eDelivery.europa.eu%2C+OU%3DeHealth%2C+CN%3DEHEALTH_SMP_TEST_BRAZIL%2FemailAddress%3DCEF-EDELIVERY-SUPPORT%40ec.europa.eu&amp;validfrom=Feb++1+14%3A20%3A18+2017+GMT&amp;validto=Jul++9+23%3A59%3A00+2019+GMT&amp;issuer=C%3DDE%2C+O%3DT-Systems+International+GmbH%2C+OU%3DT-Systems+Trust+Center%2C+ST%3DNordrhein+Westfalen%2FpostalCode%3D57250%2C+L%3DNetphen%2Fstreet%3DUntere+Industriestr.+20%2C+CN%3DShared+Business+CA+4&amp;policy_oids=1.3.6.1.4.1.7879.13.25";
        BlueCoatClientCertificateAuthentication auth = new BlueCoatClientCertificateAuthentication(certHeader);
        Assert.assertEquals("CN=EHEALTH_SMP_TEST_BRAZIL/emailAddress\\=CEF-EDELIVERY-SUPPORT@ec.europa.eu,O=European Commission,C=BE:48b681ee8e0dcc08", auth.getName());
        Assert.assertEquals("CN=EHEALTH_SMP_TEST_BRAZIL/emailAddress\\=CEF-EDELIVERY-SUPPORT@ec.europa.eu,O=European Commission,C=BE:48b681ee8e0dcc08", ((CertificateDetails)auth.getDetails()).getCertificateId());
        Assert.assertEquals("CN=EHEALTH_SMP_TEST_BRAZIL/emailAddress\\=CEF-EDELIVERY-SUPPORT@ec.europa.eu,O=European Commission,C=BE", ((CertificateDetails)auth.getDetails()).getSubject());
    }

    /**
     * Test the construction of the Authenticator from real values from the BlueCoat proxy
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    @Test
    public void calculateCertificateIdForEhealth2() throws Exception {
        String certHeader = "sno=f7%3A1e%3Ae8%3Ab1%3A1c%3Ab3%3Ab7%3A87&amp;subject=C%3DBE%2C+O%3DEuropean+Commission%2C+OU%3DCEF_eDelivery.europa.eu%2C+OU%3DeHealth%2C+OU%3DSMP_TEST%2C+CN%3DEHEALTH_SMP_EC%2FemailAddress%3DCEF-EDELIVERY-SUPPORT%40ec.europa.eu&amp;validfrom=Dec++6+17%3A41%3A42+2016+GMT&amp;validto=Jul++9+23%3A59%3A00+2019+GMT&amp;issuer=C%3DDE%2C+O%3DT-Systems+International+GmbH%2C+OU%3DT-Systems+Trust+Center%2C+ST%3DNordrhein+Westfalen%2FpostalCode%3D57250%2C+L%3DNetphen%2Fstreet%3DUntere+Industriestr.+20%2C+CN%3DShared+Business+CA+4&amp;policy_oids=1.3.6.1.4.1.7879.13.25";
        BlueCoatClientCertificateAuthentication auth = new BlueCoatClientCertificateAuthentication(certHeader);
        Assert.assertEquals("CN=EHEALTH_SMP_EC/emailAddress\\=CEF-EDELIVERY-SUPPORT@ec.europa.eu,O=European Commission,C=BE:f71ee8b11cb3b787", auth.getName());
        Assert.assertEquals("CN=EHEALTH_SMP_EC/emailAddress\\=CEF-EDELIVERY-SUPPORT@ec.europa.eu,O=European Commission,C=BE:f71ee8b11cb3b787", ((CertificateDetails)auth.getDetails()).getCertificateId());
        Assert.assertEquals("CN=EHEALTH_SMP_EC/emailAddress\\=CEF-EDELIVERY-SUPPORT@ec.europa.eu,O=European Commission,C=BE", ((CertificateDetails)auth.getDetails()).getSubject());
    }

    /**
     * Test the construction of the Authenticator from real values from the BlueCoat proxy
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    @Test
    public void calculateCertificateIdForEhealth3() throws Exception {
        String certHeader = "sno=48%3Ab6%3A81%3Aee%3A8e%3A0d%3Acc%3A08&amp;subject=C%3DBE%2C+O%3DEuropean+Commission%2C+OU%3DCEF_eDelivery.europa.eu%2C+OU%3DeHealth%2C+ST%3DNordrhein+Westfalen%2FpostalCode%3D57250%2C+L%3DNetphen%2Fstreet%3DUntere+Industriestr.+20%2C+CN%3DEHEALTH_SMP_TEST_BRAZIL%2FemailAddress%3DCEF-EDELIVERY-SUPPORT%40ec.europa.eu&amp;validfrom=Feb++1+14%3A20%3A18+2017+GMT&amp;validto=Jul++9+23%3A59%3A00+2019+GMT&amp;issuer=C%3DDE%2C+O%3DT-Systems+International+GmbH%2C+OU%3DT-Systems+Trust+Center%2C+ST%3DNordrhein+Westfalen%2FpostalCode%3D57250%2C+L%3DNetphen%2Fstreet%3DUntere+Industriestr.+20%2C+CN%3DShared+Business+CA+4&amp;policy_oids=1.3.6.1.4.1.7879.13.25";
        BlueCoatClientCertificateAuthentication auth = new BlueCoatClientCertificateAuthentication(certHeader);
        Assert.assertEquals("CN=EHEALTH_SMP_TEST_BRAZIL/emailAddress\\=CEF-EDELIVERY-SUPPORT@ec.europa.eu,O=European Commission,C=BE:48b681ee8e0dcc08", auth.getName());
        Assert.assertEquals("CN=EHEALTH_SMP_TEST_BRAZIL/emailAddress\\=CEF-EDELIVERY-SUPPORT@ec.europa.eu,O=European Commission,C=BE:48b681ee8e0dcc08", ((CertificateDetails)auth.getDetails()).getCertificateId());
        Assert.assertEquals("CN=EHEALTH_SMP_TEST_BRAZIL/emailAddress\\=CEF-EDELIVERY-SUPPORT@ec.europa.eu,O=European Commission,C=BE", ((CertificateDetails)auth.getDetails()).getSubject());
    }

    @Test
    public void calculateCertificateIdForEhealthWithoutEmail() throws Exception {
        String certHeader = "sno=48%3Ab6%3A81%3Aee%3A8e%3A0d%3Acc%3A08&amp;subject=C%3DBE%2C+O%3DEuropean+Commission%2C+OU%3DCEF_eDelivery.europa.eu%2C+OU%3DeHealth%2C+ST%3DNordrhein+Westfalen%2FpostalCode%3D57250%2C+L%3DNetphen%2Fstreet%3DUntere+Industriestr.+20%2C+CN%3DEHEALTH_SMP_TEST_BRAZIL&amp;validfrom=Feb++1+14%3A20%3A18+2017+GMT&amp;validto=Jul++9+23%3A59%3A00+2019+GMT&amp;issuer=C%3DDE%2C+O%3DT-Systems+International+GmbH%2C+OU%3DT-Systems+Trust+Center%2C+ST%3DNordrhein+Westfalen%2FpostalCode%3D57250%2C+L%3DNetphen%2Fstreet%3DUntere+Industriestr.+20%2C+CN%3DShared+Business+CA+4&amp;policy_oids=1.3.6.1.4.1.7879.13.25";
        BlueCoatClientCertificateAuthentication auth = new BlueCoatClientCertificateAuthentication(certHeader);
        Assert.assertEquals("CN=EHEALTH_SMP_TEST_BRAZIL,O=European Commission,C=BE:48b681ee8e0dcc08", auth.getName());
        Assert.assertEquals("CN=EHEALTH_SMP_TEST_BRAZIL,O=European Commission,C=BE:48b681ee8e0dcc08", ((CertificateDetails)auth.getDetails()).getCertificateId());
        Assert.assertEquals("CN=EHEALTH_SMP_TEST_BRAZIL,O=European Commission,C=BE", ((CertificateDetails)auth.getDetails()).getSubject());
    }

    @Test
    public void calculateCertificateIdForEhealthAlreadyDecoded() throws Exception {
        String certHeader = "serial=48:b6:81:ee:8e:0d:cc:08&subject=C=BE, O=European Commission, OU=CEF_eDelivery.europa.eu, OU=eHealth, CN=EHEALTH_SMP_TEST_BRAZIL&validfrom=Feb  1 14:20:18 2017 GMT&validto=Jul  9 23:59:00 2019 GMT&issuer=C=DE, O=T-Systems International GmbH, OU=T-Systems Trust Center, ST=Nordrhein Westfalen/postalCode=57250, L=Netphen/street=Untere Industriestr. 20, CN=Shared Business CA 4&policy_oids=1.3.6.1.4.1.7879.13.25";
        BlueCoatClientCertificateAuthentication auth = new BlueCoatClientCertificateAuthentication(certHeader);
        Assert.assertEquals("CN=EHEALTH_SMP_TEST_BRAZIL,O=European Commission,C=BE:48b681ee8e0dcc08", auth.getName());
        Assert.assertEquals("CN=EHEALTH_SMP_TEST_BRAZIL,O=European Commission,C=BE:48b681ee8e0dcc08", ((CertificateDetails)auth.getDetails()).getCertificateId());
        Assert.assertEquals("CN=EHEALTH_SMP_TEST_BRAZIL,O=European Commission,C=BE", ((CertificateDetails)auth.getDetails()).getSubject());
    }

    @Test
    public void calculateCertificateIdForEhealthWithEmailAlreadyDecoded() throws Exception {
        String certHeader = "serial=48:b6:81:ee:8e:0d:cc:08&subject=EMAILADDRESS=receiver@test.be,C=BE, O=European Commission, OU=CEF_eDelivery.europa.eu, OU=eHealth, CN=EHEALTH_SMP_TEST_BRAZIL&validfrom=Feb  1 14:20:18 2017 GMT&validto=Jul  9 23:59:00 2019 GMT&issuer=C=DE, O=T-Systems International GmbH, OU=T-Systems Trust Center, ST=Nordrhein Westfalen/postalCode=57250, L=Netphen/street=Untere Industriestr. 20, CN=Shared Business CA 4&policy_oids=1.3.6.1.4.1.7879.13.25";
        BlueCoatClientCertificateAuthentication auth = new BlueCoatClientCertificateAuthentication(certHeader);
        Assert.assertEquals("CN=EHEALTH_SMP_TEST_BRAZIL,O=European Commission,C=BE:48b681ee8e0dcc08", auth.getName());
        Assert.assertEquals("CN=EHEALTH_SMP_TEST_BRAZIL,O=European Commission,C=BE:48b681ee8e0dcc08", ((CertificateDetails)auth.getDetails()).getCertificateId());
        Assert.assertEquals("CN=EHEALTH_SMP_TEST_BRAZIL,O=European Commission,C=BE", ((CertificateDetails)auth.getDetails()).getSubject());
    }

    @Test
    public void calculateCertificateIdForPeppol() throws Exception {
        String certHeader = "sno=0001&amp;subject=EMAILADDRESS=receiver@test.be%2C+CN=SMP_receiverCN%2C+OU=B4%2C+O=DIGIT%2C+L=Brussels%2C+ST=BE%2C+C=BE&amp;validfrom=Jun 1 10:37:53 2015 CEST&amp;validto=Jun 1 10:37:53 2035 CEST&amp;issuer=EMAILADDRESS=root@test.be%2C+CN=rootCN%2C+OU=B4%2C+O=DIGIT%2C+L=Brussels%2C+ST=BE%2C+C=BE";
        BlueCoatClientCertificateAuthentication auth = new BlueCoatClientCertificateAuthentication(certHeader);
        Assert.assertEquals("CN=SMP_receiverCN,O=DIGIT,C=BE:0000000000000001", auth.getName());
        Assert.assertEquals("CN=SMP_receiverCN,O=DIGIT,C=BE:0000000000000001", ((CertificateDetails)auth.getDetails()).getCertificateId());
        Assert.assertEquals("CN=SMP_receiverCN,O=DIGIT,C=BE", ((CertificateDetails)auth.getDetails()).getSubject());
    }

    @Test(expected = AuthenticationException.class)
    public void calculateCertificateIdImpossibleToDetermineTheCertificateIdentifier() throws Exception {
        String certHeader = "snf=53%3Aef%3A79%3Ac3%3A54%3A98%3Abb%3A63%3A38%3A35%3A9a%3A19%3A5d%3A2d%3Ad8%3A8c&subject=C%3DBE%2C+O%3DDG-DIGIT%2C+CN%3DSMP_1000000007&validfrom=Oct+21+00%3A00%3A00+2014+GMT&validto=Oct+20+23%3A59%3A59+2016+GMT&issuer=C%3DDK%2C+O%3DNATIONAL+IT+AND+TELECOM+AGENCY%2C+OU%3DFOR+TEST+PURPOSES+ONLY%2C+CN%3DPEPPOL+SERVICE+METADATA+PUBLISHER+TEST+CA";
        new BlueCoatClientCertificateAuthentication(certHeader);
    }

    @Test(expected = AuthenticationException.class)
    public void calculateCertificateIdImpossibleToIdentifyAuthoritiesForCertificate() throws Exception {
        String certHeader = "sno=53%3Aef%3A79%3Ac3%3A54%3A98%3Abb%3A63%3A38%3A35%3A9a%3A19%3A5d%3A2d%3Ad8%3A8c&subject=C%3FPT%2C+O%3DDG-DIGIT%2C+CN%3DSMP_1000000007&validfrom=Oct+21+00%3A00%3A00+2014+GMT&validto=Oct+20+23%3A59%3A59+2016+GMT&issuer=C%3DDK%2C+O%3DNATIONAL+IT+AND+TELECOM+AGENCY%2C+OU%3DFOR+TEST+PURPOSES+ONLY%2C+CN%3DPEPPOL+SERVICE+METADATA+PUBLISHER+TEST+CA";
        BlueCoatClientCertificateAuthentication auth = new BlueCoatClientCertificateAuthentication(certHeader);
        Assert.assertEquals("CN=SMP_1000000007,O=DG-DIGIT,C=BE:53ef79c35498bb6338359a195d2dd88c", auth.getName());
    }
}
