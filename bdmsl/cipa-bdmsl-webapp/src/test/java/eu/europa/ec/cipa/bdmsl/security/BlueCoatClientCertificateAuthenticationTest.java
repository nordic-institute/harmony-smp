package eu.europa.ec.cipa.bdmsl.security;

import eu.europa.ec.cipa.common.util.Constant;
import eu.europa.ec.cipa.common.exception.BusinessException;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by feriaad on 22/06/2015.
 */
public class BlueCoatClientCertificateAuthenticationTest {

    @Test
    public void testRead() throws TechnicalException, BusinessException, UnsupportedEncodingException, ParseException {
        String serial = "123ABCD";
        String issuer = "CN=PEPPOL SERVICE METADATA PUBLISHER TEST CA,OU=FOR TEST PURPOSES ONLY,O=NATIONAL IT AND TELECOM AGENCY,C=DK";
        String subject = "O=DG-DIGIT,CN=SMP_1000000007,C=BE";
        DateFormat df = new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", Constant.LOCALE);
        Date validFrom = df.parse("Jun 01 10:37:53 2015 CEST");
        Date validTo = df.parse("Jun 01 10:37:53 2035 CEST");

        String certHeaderValue = "serial=" + serial + "&subject=" + subject + "&validFrom="+ df.format(validFrom) +"&validTo=" + df.format(validTo) +"&issuer=" + issuer;
        BlueCoatClientCertificateAuthentication bcAuth = new BlueCoatClientCertificateAuthentication(certHeaderValue);

        Assert.assertEquals(serial, ((CertificateDetails) bcAuth.getCredentials()).getSerial());
        Assert.assertEquals(issuer, ((CertificateDetails) bcAuth.getCredentials()).getIssuer());
        Assert.assertEquals("CN=SMP_1000000007,O=DG-DIGIT,C=BE", ((CertificateDetails) bcAuth.getCredentials()).getSubject());
        Assert.assertEquals(validFrom, ((CertificateDetails) bcAuth.getCredentials()).getValidFrom().getTime());
        Assert.assertEquals(validTo, ((CertificateDetails) bcAuth.getCredentials()).getValidTo().getTime());

        Assert.assertEquals("CN=SMP_1000000007,O=DG-DIGIT,C=BE:000000000123ABCD", bcAuth.getPrincipal().toString());
        Assert.assertEquals("ROLE_SMP", ((List)bcAuth.getAuthorities()).get(0).toString());

    }

    /**
     * The order of the certificate attributes is different and there are spaces after the commas. The certificate must be valid anyway
     * @throws TechnicalException
     * @throws BusinessException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    @Test
    public void testReadDifferentOrderWithSpaces() throws TechnicalException, BusinessException, UnsupportedEncodingException, ParseException {
        String serial = "123ABCD";
        String issuer = "C=DK, CN=PEPPOL SERVICE METADATA PUBLISHER TEST CA, O=NATIONAL IT AND TELECOM AGENCY, OU=FOR TEST PURPOSES ONLY";
        String subject = "C=BE, O=DG-DIGIT, CN=SMP_1000000007";
        DateFormat df = new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", Constant.LOCALE);
        Date validFrom = df.parse("Jun 01 10:37:53 2015 CEST");
        Date validTo = df.parse("Jun 01 10:37:53 2035 CEST");

        String certHeaderValue = "serial=" + serial + "&subject=" + subject + "&validFrom="+ df.format(validFrom) +"&validTo=" + df.format(validTo) +"&issuer=" + issuer;
        BlueCoatClientCertificateAuthentication bcAuth = new BlueCoatClientCertificateAuthentication(certHeaderValue);

        Assert.assertEquals(serial, ((CertificateDetails) bcAuth.getCredentials()).getSerial());
        Assert.assertEquals(issuer, ((CertificateDetails) bcAuth.getCredentials()).getIssuer());
        Assert.assertEquals("CN=SMP_1000000007,O=DG-DIGIT,C=BE", ((CertificateDetails) bcAuth.getCredentials()).getSubject());
        Assert.assertEquals(validFrom, ((CertificateDetails) bcAuth.getCredentials()).getValidFrom().getTime());
        Assert.assertEquals(validTo, ((CertificateDetails) bcAuth.getCredentials()).getValidTo().getTime());

        Assert.assertEquals("CN=SMP_1000000007,O=DG-DIGIT,C=BE:000000000123ABCD", bcAuth.getPrincipal().toString());
        Assert.assertEquals("ROLE_SMP", ((List)bcAuth.getAuthorities()).get(0).toString());

    }
}
