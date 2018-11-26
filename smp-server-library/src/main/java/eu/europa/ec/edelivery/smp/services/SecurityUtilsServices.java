package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import org.springframework.stereotype.Component;

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

@Component
public class SecurityUtilsServices {

    private static final String ALGORITHM = "AES";
    private static final int DEFAULT_KEY_SIZE= 256;


    /**
     * Inser keys/certificates from sourceKeystore to targetKeystore. If certificicate with alias alredy exists alias is appended with _%03d as example
     * _001
     * @param targetKeystore
     * @param targetPassword
     * @param sourceKeystore
     * @param sourcePassword
     * @throws KeyStoreException
     * @throws UnrecoverableKeyException
     * @throws NoSuchAlgorithmException
     */
    public void mergeKeystore(KeyStore targetKeystore, String targetPassword, KeyStore sourceKeystore, String sourcePassword) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        // Get all aliases in the old keystore
        Enumeration<String> enumeration = sourceKeystore.aliases();
        while (enumeration.hasMoreElements()) {
            // Determine the current alias
            String alias = enumeration.nextElement();

            String importAlias =  getNewImportAlias(targetKeystore, alias);
            // Get Key & Certificates
            Key key = sourceKeystore.getKey(alias, sourcePassword.toCharArray());
            Certificate[] certs = sourceKeystore.getCertificateChain(alias);
            // Put them altogether in the new keystore also set key password for new store
            targetKeystore.setKeyEntry(importAlias, key, targetPassword.toCharArray(), certs);
        }
    }

    private String getNewImportAlias(KeyStore target, String alias) throws KeyStoreException {
        String newAlias = alias;
        int i=1;
        while(target.containsAlias(newAlias)){
            newAlias = alias+String.format("_%03d", i++);
        }
        return newAlias;
    }

    public void generatePrivateSymmetricKey(File path) {

        try {
            // Generates a random key
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(DEFAULT_KEY_SIZE);
            SecretKey privateKey = keyGenerator.generateKey();

            try (FileOutputStream out = new FileOutputStream(path)) {
                out.write(privateKey.getEncoded());
                out.flush();
            }
        } catch (Exception exc) {
            throw new SMPRuntimeException(INTERNAL_ERROR,exc, "Error occurred while saving key for encryption",  exc.getMessage());
        }
    }

    public String encrypt(File keyPath, String plainTextPassword) {
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

    public String decrypt(File keyPath, String encryptedPassword) {
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
