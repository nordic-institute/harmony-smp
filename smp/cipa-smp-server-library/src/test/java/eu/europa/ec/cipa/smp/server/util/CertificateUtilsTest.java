package eu.europa.ec.cipa.smp.server.util;

import eu.europa.ec.cipa.smp.server.AbstractTest;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by migueti on 06/12/2016.
 */
public class CertificateUtilsTest extends AbstractTest {

    @Test
    public void testConvertBigIntToHexString() {
        Assert.assertEquals("18ee90ff6c373e0ee4e3f0ad2",
                CertificateUtils.convertBigIntToHexString(new BigInteger("123456789012345678901234567890")));
    }

    @Test
    public void testReturnCertificateIdBigInteger() {
        Assert.assertEquals("TEST SUBJECT:c7748819dffb62438d1c67eea",
                CertificateUtils.returnCertificateId("TEST SUBJECT", new BigInteger("987654321098765432109876543210")));
    }

    @Test
    public void testReturnCertificateIdString() {
        Assert.assertEquals("TEST SUBJECT:000000008f658712",
                CertificateUtils.returnCertificateId("TEST SUBJECT","8f658712"));
    }

    @Test
    public void testReturnCertificateIdStringWithHexaHeader() {
        Assert.assertEquals("TEST SUBJECT:000000008f658712",
                CertificateUtils.returnCertificateId("TEST SUBJECT","0x8f658712"));
    }

    @Test
    public void testRemoveHexHeader() {
        Assert.assertEquals("1234567890",CertificateUtils.removeHexHeader("0x1234567890"));
    }
}
