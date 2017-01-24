package eu.europa.ec.cipa.smp.server.security;

import eu.europa.ec.cipa.smp.server.AbstractTest;
import eu.europa.ec.cipa.smp.server.data.dbms.model.DBUser;
import eu.europa.ec.cipa.smp.server.services.IBlueCoatCertificateService;
import eu.europa.ec.cipa.smp.server.util.CertificateUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by rodrfla on 21/01/2017.
 */
public class CustomAuthenticationProviderTest extends AbstractTest {

    @Test
    public void authenticationForBlueCoat() throws Exception {
        String serial = "123ABCD";
        String issuer = "CN=ENTITY SERVICE METADATA PUBLISHER TEST CA,OU=FOR TEST PURPOSES ONLY,O=NATIONAL IT AND TELECOM AGENCY,C=DK";
        String subject = "O=DG-DIGIT,CN=SMP_1000000007,C=BE";

        Calendar validFrom = Calendar.getInstance();
        validFrom.set(validFrom.get(Calendar.YEAR) - 2, 1, 1);
        Calendar validTo = Calendar.getInstance();
        validTo.set(validTo.get(Calendar.YEAR) + 3, 1, 1);
        DateFormat df = new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", Locale.US);
        String headerCertificate = "serial=" + serial + "&subject=" + subject + "&validFrom=" + df.format(validFrom.getTime()) + "&validTo=" + df.format(validTo.getTime()) + "&issuer=" + issuer;
        CertificateDetails certificateDetails = CertificateUtils.getCommonNameFromCalculateHeaderCertificateId(headerCertificate);
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"classpath:applicationContext.xml"});
        IBlueCoatCertificateService blueCoatCertificateService = (IBlueCoatCertificateService) context.getBean("blueCoatCertificateServiceImpl");
        boolean isValid = blueCoatCertificateService.isBlueCoatClientCertificateValid(certificateDetails);

        Assert.assertTrue(isValid);
    }

    @Test
    public void authenticationForBlueCoatCertificateNotFound() throws Exception {
        String serial = "123ABCD";
        String issuer = "CN=ENTITY SERVICE METADATA PUBLISHER TEST CA,OU=FOR TEST PURPOSES ONLY,O=NATIONAL IT AND TELECOM AGENCY,C=DK";
        String subject = "O=DG-DIGIT,CN=SMP_10951843963,C=BE";
        Calendar validFrom = Calendar.getInstance();
        validFrom.set(validFrom.get(Calendar.YEAR) - 2, 1, 1);
        Calendar validTo = Calendar.getInstance();
        validTo.set(validTo.get(Calendar.YEAR) + 3, 1, 1);
        DateFormat df = new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", Locale.US);
        String headerCertificate = "serial=" + serial + "&subject=" + subject + "&validFrom=" + df.format(validFrom.getTime()) + "&validTo=" + df.format(validTo.getTime()) + "&issuer=" + issuer;
        CertificateDetails certificateDetails = CertificateUtils.getCommonNameFromCalculateHeaderCertificateId(headerCertificate);

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"classpath:applicationContext.xml"});
        IBlueCoatCertificateService blueCoatCertificateService = (IBlueCoatCertificateService) context.getBean("blueCoatCertificateServiceImpl");
        boolean isValid = blueCoatCertificateService.isBlueCoatClientCertificateValid(certificateDetails);

        Assert.assertFalse(isValid);
    }

    @Test
    public void authenticationForBlueCoatRevocakedCertificate() throws Exception {
        String serial = "123ABCD";
        String issuer = "CN=ENTITY SERVICE METADATA PUBLISHER TEST CA,OU=FOR TEST PURPOSES ONLY,O=NATIONAL IT AND TELECOM AGENCY,C=DK";
        String subject = "O=DG-DIGIT,CN=SMP_10951843963,C=BE";
        Calendar validFrom = Calendar.getInstance();
        validFrom.set(validFrom.get(Calendar.YEAR) - 3, 1, 1);
        Calendar validTo = Calendar.getInstance();
        validTo.set(validTo.get(Calendar.YEAR) - 1, 1, 1);
        DateFormat df = new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", Locale.US);
        String headerCertificate = "serial=" + serial + "&subject=" + subject + "&validFrom=" + df.format(validFrom.getTime()) + "&validTo=" + df.format(validTo.getTime()) + "&issuer=" + issuer;
        CertificateDetails certificateDetails = CertificateUtils.getCommonNameFromCalculateHeaderCertificateId(headerCertificate);

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"classpath:applicationContext.xml"});
        IBlueCoatCertificateService blueCoatCertificateService = (IBlueCoatCertificateService) context.getBean("blueCoatCertificateServiceImpl");
        boolean isValid = blueCoatCertificateService.isBlueCoatClientCertificateValid(certificateDetails);

        Assert.assertFalse(isValid);
    }

    @Test
    public void authenticationForBlueCoatNotValidYetCertificate() throws Exception {
        String serial = "123ABCD";
        String issuer = "CN=ENTITY SERVICE METADATA PUBLISHER TEST CA,OU=FOR TEST PURPOSES ONLY,O=NATIONAL IT AND TELECOM AGENCY,C=DK";
        String subject = "O=DG-DIGIT,CN=SMP_10951843963,C=BE";
        Calendar validFrom = Calendar.getInstance();
        validFrom.set(validFrom.get(Calendar.YEAR) + 3, 1, 1);
        Calendar validTo = Calendar.getInstance();
        validTo.set(validTo.get(Calendar.YEAR) - 1, 1, 1);
        DateFormat df = new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", Locale.US);
        String headerCertificate = "serial=" + serial + "&subject=" + subject + "&validFrom=" + df.format(validFrom.getTime()) + "&validTo=" + df.format(validTo.getTime()) + "&issuer=" + issuer;
        CertificateDetails certificateDetails = CertificateUtils.getCommonNameFromCalculateHeaderCertificateId(headerCertificate);

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"classpath:applicationContext.xml"});
        IBlueCoatCertificateService blueCoatCertificateService = (IBlueCoatCertificateService) context.getBean("blueCoatCertificateServiceImpl");
        boolean isValid = blueCoatCertificateService.isBlueCoatClientCertificateValid(certificateDetails);

        Assert.assertFalse(isValid);
    }
}
