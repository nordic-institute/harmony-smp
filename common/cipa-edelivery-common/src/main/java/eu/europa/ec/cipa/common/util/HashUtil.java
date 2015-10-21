package eu.europa.ec.cipa.common.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * Created by feriaad on 26/06/2015.
 */
public class HashUtil {

    /**
     * Returns the MD5 hash of the given String
     *
     * @param stringToBeHashed
     * @return the MD5 hash of the given string
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String getMD5Hash(String stringToBeHashed) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return getHash(stringToBeHashed, "MD5");
    }

    /**
     * Returns the SHA224 hash of the given String
     *
     * @param stringToBeHashed
     * @return the SHA224 hash of the given string
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String getSHA224Hash(String stringToBeHashed) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return getHash(stringToBeHashed, "SHA224");
    }

    /**
     * Returns the hash of the given String
     *
     * @param stringToBeHashed
     * @return the SHA224 hash of the given string
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    private static String getHash(String stringToBeHashed, String algorithm) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.reset();
        md.update(stringToBeHashed.getBytes());
        byte[] hashBytes = md.digest();

        //convert the byte to hex format method 2
        StringBuilder hexString = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String hex = Integer.toHexString(0xff & hashByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
