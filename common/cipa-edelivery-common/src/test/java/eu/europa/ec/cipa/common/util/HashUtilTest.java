package eu.europa.ec.cipa.common.util;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

/**
 * Created by feriaad on 03/11/2015.
 */
public class HashUtilTest {

    @BeforeClass
    public static void beforeClass() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    @Test
    public void testMD5() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Assert.assertEquals("14cea2fcb97d92b13f41831b61848d33", HashUtil.getMD5Hash("participantId"));
        Assert.assertEquals("d7b4e799115aa883cd726e51db3dafd3", HashUtil.getMD5Hash("LOWERCASESTRING".toLowerCase(Constant.LOCALE)));
    }

    @Test
    public void testSHA224() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Assert.assertEquals("5c8182ecca5bb64c9bf8a1540431bab69eed31e5b220f405c090ccc9", HashUtil.getSHA224Hash("participantId"));
        Assert.assertEquals("15afcd29962ad8f6f0a445be2500cb10703d48d9e60ae4c73eebdac0", HashUtil.getSHA224Hash("LOWERCASESTRING".toLowerCase(Constant.LOCALE)));
    }
}
