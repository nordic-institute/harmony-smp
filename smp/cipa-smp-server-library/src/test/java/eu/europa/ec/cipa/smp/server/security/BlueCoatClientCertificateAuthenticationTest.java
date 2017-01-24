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
        DateFormat df = new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", Locale.US);
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
        DateFormat df = new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", Locale.US);
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
        DateFormat df = new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", Locale.US);
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
