package eu.europa.ec.edelivery.smp.utils;

import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Enumeration;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.INTERNAL_ERROR;

public class SecurityUtils {

    public static final String ALGORITHM_KEY = "AES";
    public static final String ALGORITHM_ENCRYPTION = "AES/GCM/NoPadding";
    public static final String ALGORITHM_ENCRYPTION_OBSOLETE = "AES/CBC/PKCS5Padding";
    public static final int KEY_SIZE = 256;
    public static final int GCM_TAG_LENGTH_BIT = 128;
    // for te gcm iv size is 12
    public static final int IV_GCM_SIZE = 12;
    // NULL IV is for CBC which  has IV size 16!
    private static final IvParameterSpec NULL_IV = new IvParameterSpec(new byte[16]);


    /**
     * Insert keys/certificates from sourceKeystore to target Keystore. If certificate with alias already exists alias is
     * appended with _%03d as example
     * _001
     *
     * @param targetKeystore
     * @param targetPassword
     * @param sourceKeystore
     * @param sourcePassword
     * @throws KeyStoreException
     * @throws UnrecoverableKeyException
     * @throws NoSuchAlgorithmException
     */
    public static void mergeKeystore(KeyStore targetKeystore, String targetPassword, KeyStore sourceKeystore, String sourcePassword) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        // Get all aliases in the old keystore
        Enumeration<String> enumeration = sourceKeystore.aliases();
        while (enumeration.hasMoreElements()) {
            // Determine the current alias
            String alias = enumeration.nextElement();
            String importAlias = getNewImportAlias(targetKeystore, alias);
            // Get Key & Certificates
            Key key = sourceKeystore.getKey(alias, sourcePassword.toCharArray());
            Certificate[] certs = sourceKeystore.getCertificateChain(alias);
            // Put them altogether in the new keystore also set key password for new store
            targetKeystore.setKeyEntry(importAlias, key, targetPassword.toCharArray(), certs);
        }
    }

    private static String getNewImportAlias(KeyStore target, String alias) throws KeyStoreException {
        String newAlias = alias;
        int i = 1;
        while (target.containsAlias(newAlias)) {
            newAlias = alias + String.format("_%03d", i++);
        }
        return newAlias;
    }

    public static void generatePrivateSymmetricKey(File path) {

        try {
            // Generates a random key
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM_KEY);
            keyGenerator.init(KEY_SIZE);
            SecretKey privateKey = keyGenerator.generateKey();

            SecureRandom rnd = new SecureRandom();
            // Using setSeed(byte[]) to reseed a Random object
            byte[] seed = rnd.generateSeed(IV_GCM_SIZE);
            rnd.setSeed(seed);

            byte[] buffIV = new byte[IV_GCM_SIZE];
            rnd.nextBytes(buffIV);

            try (FileOutputStream out = new FileOutputStream(path)) {
                // first write IV
                out.write('#');
                out.write(buffIV);
                out.write('#');
                out.write(privateKey.getEncoded());
                out.flush();
            }
        } catch (Exception exc) {
            throw new SMPRuntimeException(INTERNAL_ERROR, exc, "Error occurred while saving key for encryption", exc.getMessage());
        }
    }

    public static String encrypt(File path, String plainTextPassword) {
        try {
            byte[] buff = Files.readAllBytes(path.toPath());
            AlgorithmParameterSpec iv = getSaltParameter(buff);
            SecretKey privateKey = getSecretKey(buff);

            Cipher cipher = Cipher.getInstance(iv == NULL_IV ? ALGORITHM_ENCRYPTION_OBSOLETE : ALGORITHM_ENCRYPTION);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey, iv);
            byte[] encryptedData = cipher.doFinal(plainTextPassword.getBytes());
            return new String(Base64.getEncoder().encode(encryptedData));
        } catch (Exception exc) {
            throw new SMPRuntimeException(INTERNAL_ERROR, exc, "Error occurred while encrypting the password", exc.getMessage());
        }
    }

    public static String decrypt(File keyPath, String encryptedPassword) {
        try {
            byte[] buff = Files.readAllBytes(keyPath.toPath());

            AlgorithmParameterSpec iv = getSaltParameter(buff);
            SecretKey privateKey = getSecretKey(buff);

            byte[] decodedEncryptedPassword = Base64.getDecoder().decode(encryptedPassword.getBytes());
            // this is for back-compatibility - if key parameter is IV than is CBC else ie GCM
            Cipher cipher = Cipher.getInstance(iv instanceof IvParameterSpec ? ALGORITHM_ENCRYPTION_OBSOLETE : ALGORITHM_ENCRYPTION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey, iv);
            byte[] decrypted = cipher.doFinal(decodedEncryptedPassword);
            return new String(decrypted);
        } catch (BadPaddingException | IllegalBlockSizeException ibse) {
            throw new SMPRuntimeException(INTERNAL_ERROR, ibse, "Either private key '" + keyPath.getAbsolutePath() + "' or encrypted password might not be correct. Please check both.  Root cause: " + ExceptionUtils.getRootCauseMessage(ibse), ibse.getMessage());
        } catch (Exception exc) {
            throw new SMPRuntimeException(INTERNAL_ERROR, exc, "Error occurred while decrypting the password with the key: '" + keyPath.getAbsolutePath() + "'! Root cause: " + ExceptionUtils.getRootCauseMessage(exc), exc.getMessage());
        }
    }


    public static AlgorithmParameterSpec getSaltParameter(byte[] buff) {
        AlgorithmParameterSpec iv;
        // this is for back compatibility  - older versions were using "CBC" with IV to null
        // the GCM  is a new enforced algorithm  where GCM salt parameter
        if (buff[0] == '#' && buff[IV_GCM_SIZE + 1] == '#') {
            iv = new GCMParameterSpec(GCM_TAG_LENGTH_BIT, Arrays.copyOfRange(buff, 1, IV_GCM_SIZE + 1));
        } else {
            iv = NULL_IV;
        }
        return iv;
    }

    public static SecretKey getSecretKey(byte[] buff) {
        byte[] skey;
        if (buff[0] == '#') {
            // EAS Key value is after salt value following the patter: // #salt#key
            skey = Arrays.copyOfRange(buff, IV_GCM_SIZE + 2, buff.length);
        } else {
            skey = buff;
        }

        return new SecretKeySpec(skey, ALGORITHM_KEY);
    }
}
