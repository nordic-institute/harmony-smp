package eu.europa.ec.edelivery.smp.utils;

import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.util.Base64;
import java.util.Enumeration;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.INTERNAL_ERROR;

public class SecurityUtils {

    private static final String ALGORITHM = "AES";


    public static void mergeKeystores(KeyStore targetKeystore, String targetPassword, KeyStore sourceKeystore, String sourcePassword) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        // Get all aliases in the old keystore
        Enumeration<String> enumeration = sourceKeystore.aliases();
        while (enumeration.hasMoreElements()) {
            // Determine the current alias
            String alias = enumeration.nextElement();
            // Get Key & Certificates
            Key key = sourceKeystore.getKey(alias, sourcePassword.toCharArray());
            Certificate[] certs = sourceKeystore.getCertificateChain(alias);
            // Put them altogether in the new keystore also set key password for new store
            targetKeystore.setKeyEntry(alias, key, targetPassword.toCharArray(), certs);
        }
    }

    public static void generatePrivateSymmetricKey(File path) {

        try {
            // Generates a random key
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(256);
            SecretKey privateKey = keyGenerator.generateKey();

            try (FileOutputStream out = new FileOutputStream(path)) {
                out.write(privateKey.getEncoded());
                out.flush();
            }
        } catch (Exception exc) {
            throw new SMPRuntimeException(INTERNAL_ERROR,exc, "Error occurred while saving key for encryption",  exc.getMessage());
        }
    }

    public static String encrypt(File keyPath, String plainTextPassword) {
        try {
            byte[] key = Files.readAllBytes(keyPath.toPath());
            SecretKey privateKey = new SecretKeySpec(key, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] encryptedData = cipher.doFinal(plainTextPassword.getBytes());

            return Base64.getEncoder().encodeToString(encryptedData);


        } catch (Exception exc) {
            throw new SMPRuntimeException(INTERNAL_ERROR,exc,  "Error occurred while encrypting the password",  exc.getMessage());
        }
    }

    public static String decrypt(File keyPath, String encryptedPassword) {
        try {
            byte[] key = Files.readAllBytes(keyPath.toPath());
            SecretKey privateKey = new SecretKeySpec(key, ALGORITHM);
            byte[] decodedEncryptedPassword = Base64.getDecoder().decode(encryptedPassword.getBytes());
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decrypted = cipher.doFinal(decodedEncryptedPassword);
            return new String(decrypted);
        } catch (BadPaddingException | IllegalBlockSizeException ibse) {
            throw new SMPRuntimeException(INTERNAL_ERROR,ibse,  "Either private key or encrypted password might not be correct. Please check both.",ibse.getMessage());
        } catch (Exception exc) {
            throw new SMPRuntimeException(INTERNAL_ERROR,exc,  "Error occurred while decrypting the password",  exc.getMessage());
        }
    }
}
