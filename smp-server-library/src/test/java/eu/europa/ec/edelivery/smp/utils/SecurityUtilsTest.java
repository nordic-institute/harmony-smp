package eu.europa.ec.edelivery.smp.utils;

import org.junit.Assert;
import org.junit.Test;


import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class SecurityUtilsTest {


    @Test
    public void generatePrivateSymmetricKey() throws IOException {
        String tempPrivateKey = "enckey_"+ System.currentTimeMillis() + ".private";
        Path resourceDirectory = Paths.get("target", tempPrivateKey);

        SecurityUtils.generatePrivateSymmetricKey(resourceDirectory.toFile());

        SecretKey privateKey = new SecretKeySpec(Files.readAllBytes(Paths.get(resourceDirectory.toAbsolutePath().toString())), "AES");
        Assert.assertNotNull(privateKey);
        Assert.assertTrue(privateKey instanceof SecretKey);
        File file = new File(resourceDirectory.toAbsolutePath().toString());

        assertTrue(file.exists());
        assertTrue(file.length() > 0);


        file.delete();
    }

    @Test
    public void encrypt() throws IOException {
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
    public void encryptWithSetupKey() {
        // given
        File f = new File("src/test/resources/keystores/encryptionKey.key");
        String password = "test123";

        // when
        String encPassword = SecurityUtils.encrypt(f, password);
        //then
        assertNotNull(encPassword);
        assertNotEquals(password, encPassword);
    }


    @Test
    public void decrypt() throws IOException {
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