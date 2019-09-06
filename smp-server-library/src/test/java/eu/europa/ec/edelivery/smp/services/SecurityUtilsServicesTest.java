package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.utils.SecurityUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Security;
import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class SecurityUtilsServicesTest {
    ;

    @Before
    public void setup(){
        Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    @Test
    public void testGenerateMasterKeyWitIV() throws Exception {
        String tempPrivateKey = System.currentTimeMillis() + ".private";
        Path resourcePath = Paths.get("target", tempPrivateKey);

        SecurityUtils.generatePrivateSymmetricKey(resourcePath.toFile());

        byte[] buff = Files.readAllBytes(resourcePath);
        Assert.assertTrue(buff.length > SecurityUtils.IV_GCM_SIZE);
        // start tag
        Assert.assertEquals('#', buff[0]);
        // end IV tag
        Assert.assertEquals('#', buff[SecurityUtils.IV_GCM_SIZE + 1]);
        byte[] keyBytes = Arrays.copyOfRange(buff, SecurityUtils.IV_GCM_SIZE + 2, buff.length);


        SecretKey privateKey = new SecretKeySpec(keyBytes, "AES");
        Assert.assertNotNull(privateKey);
        Assert.assertTrue(privateKey instanceof SecretKey);

    }


    @Test
    public void encryptDefault() throws IOException {
        // given
        File f = generateRandomPrivateKey();
        String password = "TEST11002password1@!."+System.currentTimeMillis();

        // when
        String encPassword = SecurityUtils.encrypt(f, password);
        //then
        assertNotNull(encPassword);
        assertNotEquals(password, encPassword);
    }

    @Test
    public void encryptWithSetupKeyWithoutIV() {
        // given
        Path resourceFile = Paths.get("src", "test", "resources", "keystores","encryptionKey.key");
        String password = "test123";

        // when
        String encPassword = SecurityUtils.encrypt(resourceFile.toFile(), password);
        String decPassword = SecurityUtils.decrypt(resourceFile.toFile(), encPassword);
        //then
        assertNotNull(encPassword);
        assertNotEquals(password, encPassword);
        assertEquals(password, decPassword);
    }

    @Test
    public void encryptWithSetupKeyWitIV() {
        // given
        Path resourceFile = Paths.get("src", "test", "resources", "keystores","masterKeyWithIV.key");
        String password = "test123";

        // when
        String encPassword = SecurityUtils.encrypt(resourceFile.toFile(), password);
        String decPassword = SecurityUtils.decrypt(resourceFile.toFile(), encPassword);
        //then
        assertNotNull(encPassword);
        assertNotEquals(password, encPassword);
        assertEquals(password, decPassword);
    }


    @Test
    public void decryptDefault() throws IOException {
        // given
        File f = generateRandomPrivateKey();
        String password = "TEST11002password1@!." + System.currentTimeMillis();
        String encPassword = SecurityUtils.encrypt(f, password);

        // when
        String decPassword = SecurityUtils.decrypt(f, encPassword);
        //then
        assertNotNull(decPassword);
        assertEquals(password, decPassword);
    }


    private File generateRandomPrivateKey() throws IOException{
        File resource = File.createTempFile( "test-key", ".key");
        resource.deleteOnExit();

        SecurityUtils.generatePrivateSymmetricKey(resource);
        return resource;

    }

}