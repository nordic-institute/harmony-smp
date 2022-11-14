package eu.europa.ec.edelivery.smp.utils;

import eu.europa.ec.edelivery.smp.data.ui.AccessTokenRO;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Random;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.INTERNAL_ERROR;

public class SecurityUtils {
    public static final SMPLogger LOG = SMPLoggerFactory.getLogger(SecurityUtils.class);

    public static class Secret {
        final byte[] vector;
        final SecretKey key;
        AlgorithmParameterSpec ivParameter = null;

        public Secret(byte[] vector, SecretKey key) {
            this.vector = vector;
            this.key = key;

        }

        public byte[] getVector() {
            return vector;
        }

        public AlgorithmParameterSpec getIVParameter() {
            if (ivParameter == null && vector != null) {
                this.ivParameter = new GCMParameterSpec(GCM_TAG_LENGTH_BIT, vector);
            }
            return ivParameter;
        }

        public SecretKey getKey() {
            return key;
        }
    }

    private static final String VALID_PW_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+{}[]|:;<>?,./";
    private static final String VALID_USER_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int DEFAULT_PASSWORD_LENGTH = 16;
    private static final int DEFAULT_USER_LENGTH = 8;

    public static final String ALGORITHM_KEY = "AES";
    public static final String ALGORITHM_ENCRYPTION = "AES/GCM/NoPadding";
    public static final String ALGORITHM_ENCRYPTION_OBSOLETE = "AES/CBC/PKCS5Padding";
    public static final int KEY_SIZE = 256;
    public static final int GCM_TAG_LENGTH_BIT = 128;
    // for te gcm iv size is 12
    public static final int IV_GCM_SIZE = 12;
    // NULL IV is for CBC which  has IV size 16!
    private static final IvParameterSpec NULL_IV = new IvParameterSpec(new byte[16]);

    public static final String DECRYPTED_TOKEN_PREFIX = "{DEC}{";


    public static String getNonEncryptedValue(String value) {
        return value.substring(DECRYPTED_TOKEN_PREFIX.length(), value.lastIndexOf("}"));
    }

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


    public static String generateAuthenticationToken(boolean devMode) {
        String newKeyPassword;
        try {
            newKeyPassword = RandomStringUtils.random(DEFAULT_PASSWORD_LENGTH, 0, VALID_PW_CHARS.length(),
                    false, false,
                    VALID_PW_CHARS.toCharArray(), devMode ? new Random() : SecureRandom.getInstanceStrong());

        } catch (NoSuchAlgorithmException e) {
            String msg = "Error occurred while generation test password: No strong random algorithm. Error:"
                    + ExceptionUtils.getRootCauseMessage(e);
            throw new SMPRuntimeException(INTERNAL_ERROR, e, msg, e.getMessage());
        }
        return newKeyPassword;
    }

    public static String generateAuthenticationTokenIdentifier() {
        String newKeyPassword;
        try {
            newKeyPassword = RandomStringUtils.random(DEFAULT_USER_LENGTH, 0, VALID_USER_CHARS.length(),
                    true, false,
                    VALID_USER_CHARS.toCharArray(), SecureRandom.getInstanceStrong());

        } catch (NoSuchAlgorithmException e) {
            String msg = "Error occurred while generation test password: No strong random algorithm. Error:"
                    + ExceptionUtils.getRootCauseMessage(e);
            throw new SMPRuntimeException(INTERNAL_ERROR, e, msg, e.getMessage());
        }
        return newKeyPassword;
    }

    public static AccessTokenRO generateAccessToken(boolean testMode) {
        AccessTokenRO accessToken = new AccessTokenRO();
        accessToken.setGeneratedOn(OffsetDateTime.now());
        accessToken.setIdentifier(generateAuthenticationTokenIdentifier());
        accessToken.setValue(generateAuthenticationToken(testMode));
        return accessToken;
    }

    public static Secret generatePrivateSymmetricKey() {
        // Generates a random key
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance(ALGORITHM_KEY);
        } catch (NoSuchAlgorithmException exc) {
            throw new SMPRuntimeException(INTERNAL_ERROR, exc, "Error occurred while generating secret key for encryption", exc.getMessage());
        }
        keyGenerator.init(KEY_SIZE);
        SecretKey privateKey = keyGenerator.generateKey();

        SecureRandom rnd = new SecureRandom();
        // Using setSeed(byte[]) to reseed a Random object
        byte[] seed = rnd.generateSeed(IV_GCM_SIZE);
        rnd.setSeed(seed);

        byte[] buffIV = new byte[IV_GCM_SIZE];
        rnd.nextBytes(buffIV);

        return new Secret(buffIV, privateKey);
    }

    public static void generatePrivateSymmetricKey(File path) {
        Secret secret = generatePrivateSymmetricKey();
        try (FileOutputStream out = new FileOutputStream(path)) {
            // first write IV
            out.write('#');
            out.write(secret.getVector());
            out.write('#');
            out.write(secret.getKey().getEncoded());
            out.flush();

        } catch (IOException exc) {
            throw new SMPRuntimeException(INTERNAL_ERROR, exc, "Error occurred while saving key for encryption", exc.getMessage());
        }
    }


    public static String encryptWrappedToken(File encKeyFile, String token) {
        if (!StringUtils.isBlank(token) && token.startsWith(SecurityUtils.DECRYPTED_TOKEN_PREFIX)) {
            String unWrapToken = getNonEncryptedValue(token);
            return SecurityUtils.encrypt(encKeyFile, unWrapToken);
        }
        return token;
    }

    public static String encryptURLSafe(Secret secret, String plainToken) {
        return encrypt(secret, plainToken, Base64.getUrlEncoder().withoutPadding());
    }

    public static String encrypt(Secret secret, String plainToken) {
        return encrypt(secret.getKey(), secret.getIVParameter(), plainToken, Base64.getEncoder());
    }

    public static String encrypt(Secret secret, String plainToken, Base64.Encoder encoder) {
        return encrypt(secret.getKey(), secret.getIVParameter(), plainToken, encoder);
    }

    public static String encrypt(SecretKey privateKey, AlgorithmParameterSpec iv, String plainToken, Base64.Encoder encoder) {
        try {
            Cipher cipher = Cipher.getInstance(iv == NULL_IV ? ALGORITHM_ENCRYPTION_OBSOLETE : ALGORITHM_ENCRYPTION);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey, iv);
            byte[] encryptedData = cipher.doFinal(plainToken.getBytes());
            return new String(encoder.encode(encryptedData));
        } catch (Exception exc) {
            throw new SMPRuntimeException(INTERNAL_ERROR, exc, "Error occurred while encrypting the password", exc.getMessage());
        }
    }

    public static String encrypt(File path, String plainToken) {
        byte[] buff;
        try {
            buff = Files.readAllBytes(path.toPath());
        } catch (Exception exc) {
            throw new SMPRuntimeException(INTERNAL_ERROR, exc, "Error occurred while reading encryption key [" + path.getAbsolutePath() + "]!  Root cause: " + ExceptionUtils.getRootCauseMessage(exc), exc.getMessage());
        }
        AlgorithmParameterSpec iv = getSaltParameter(buff);
        SecretKey privateKey = getSecretKey(buff);
        return encrypt(privateKey, iv, plainToken, Base64.getEncoder());
    }

    public static String decrypt(File keyPath, String encryptedToken) {

        byte[] buff;
        try {
            buff = Files.readAllBytes(keyPath.toPath());
        } catch (IOException exc) {
            throw new SMPRuntimeException(INTERNAL_ERROR, exc, "Error occurred while reading the the key: '" + keyPath.getAbsolutePath() + "'! Root cause: " + ExceptionUtils.getRootCauseMessage(exc), exc.getMessage());
        }
        AlgorithmParameterSpec iv = getSaltParameter(buff);
        SecretKey privateKey = getSecretKey(buff);
        return decrypt(privateKey, iv, encryptedToken, Base64.getDecoder());

    }

    public static String decrypt(Secret secret, String encryptedToken) {
        return decrypt(secret.getKey(), secret.ivParameter, encryptedToken, Base64.getDecoder());
    }

    public static String decryptUrlSafe(Secret secret, String encryptedToken) {
        return decrypt(secret.getKey(), secret.ivParameter, encryptedToken, Base64.getUrlDecoder());
    }

    public static String decrypt(SecretKey privateKey, AlgorithmParameterSpec iv, String encryptedToken, Base64.Decoder decoder) {
        try {
            byte[] decodedEncryptedPassword = decoder.decode(encryptedToken.getBytes());
            // this is for back-compatibility - if key parameter is IV than is CBC else ie GCM
            Cipher cipher = Cipher.getInstance(iv instanceof IvParameterSpec ? ALGORITHM_ENCRYPTION_OBSOLETE : ALGORITHM_ENCRYPTION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey, iv);
            byte[] decrypted = cipher.doFinal(decodedEncryptedPassword);
            return new String(decrypted);
        } catch (BadPaddingException | IllegalBlockSizeException ibse) {
            throw new SMPRuntimeException(INTERNAL_ERROR, ibse, "Either private key or encrypted password might not be correct. Please check both.  Root cause: " + ExceptionUtils.getRootCauseMessage(ibse), ibse.getMessage());
        } catch (Exception exc) {
            throw new SMPRuntimeException(INTERNAL_ERROR, exc, "Error occurred while decrypting the password with the key! Root cause: " + ExceptionUtils.getRootCauseMessage(exc), exc.getMessage());
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
