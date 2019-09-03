package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.services.SecurityUtilsServices;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
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
@ContextConfiguration(classes = {SecurityUtilsServices.class})
public class SecurityUtilsServicesTest {


    @Autowired
    protected SecurityUtilsServices testInstance;

    @Before
    public void setup(){
        Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    @Test
    public void testGenerateMasterKeyWitIV() throws Exception {
        String tempPrivateKey = System.currentTimeMillis() + ".private";
        Path resourcePath = Paths.get("target", tempPrivateKey);

        testInstance.generatePrivateSymmetricKey(resourcePath.toFile());

        byte[] buff = Files.readAllBytes(resourcePath);
        Assert.assertTrue(buff.length > SecurityUtilsServices.IV_GCM_SIZE);
        // start tag
        Assert.assertEquals('#', buff[0]);
        // end IV tag
        Assert.assertEquals('#', buff[SecurityUtilsServices.IV_GCM_SIZE + 1]);
        byte[] keyBytes = Arrays.copyOfRange(buff, SecurityUtilsServices.IV_GCM_SIZE + 2, buff.length);


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
        String encPassword = testInstance.encrypt(f, password);
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
        String encPassword = testInstance.encrypt(resourceFile.toFile(), password);
        String decPassword = testInstance.decrypt(resourceFile.toFile(), encPassword);
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
        String encPassword = testInstance.encrypt(resourceFile.toFile(), password);
        String decPassword = testInstance.decrypt(resourceFile.toFile(), encPassword);
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
        String encPassword = testInstance.encrypt(f, password);

        // when
        String decPassword = testInstance.decrypt(f, encPassword);
        //then
        assertNotNull(decPassword);
        assertEquals(password, decPassword);
    }


    private File generateRandomPrivateKey() throws IOException{
        File resource = File.createTempFile( "test-key", ".key");
        resource.deleteOnExit();

        testInstance.generatePrivateSymmetricKey(resource);
        return resource;

    }

}