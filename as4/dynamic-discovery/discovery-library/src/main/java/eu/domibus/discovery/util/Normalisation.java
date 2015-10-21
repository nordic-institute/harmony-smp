/*
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * 
 * You may obtain a copy of the Licence at:
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the Licence is distributed on an "AS IS" basis, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * See the Licence for the specific language governing permissions and limitations
 * under the Licence.
 */
package eu.domibus.discovery.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;


/**
 * Utility methods to normalise names.
 * 
 * <p>Supports any message digest algorithms for normalisation.</p>
 * <p>Supports prefixes for normalised names.</p>
 *
 * @author Thorsten Niedzwetzki
 */
public class Normalisation {

	
	/**
	 * Default Message digest to use to normalise identifiers.
	 *
	 * <p><a href="http://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#MessageDigest">Message
	 * Digest Algorithms</a></p>
	 * 
	 */
	private static final String DEFAULT_MESSAGE_DIGEST_ALGORITHM_NAME = "MD5";
	

	/**
	 * Supports hex string display of normalised end entity identifiers.
	 * 
	 * @see #byteArrayToHexString(byte[])
	 */
	protected static final String HEX_CHARACTERS = "0123456789abcdef";


	/** Prefix normalised identifiers. */
	private static final String DEFAULT_NORMALISE_PREFIX = "B-";


	/**
	 * Assemble a readable hex string of a normalised (hashed) end entity identifier.
	 * 
	 * @param bytes normalised end entity identifier
	 * @return readable hex string
	 * @see #setNormalisation(String)
	 */
	public static String byteArrayToHexString(final byte[] bytes) {
		final StringBuilder sb = new StringBuilder(bytes.length << 1);
		for (final byte bits : bytes) {
			sb.append(HEX_CHARACTERS.charAt((bits & 0xF0) >> 4));
			sb.append(HEX_CHARACTERS.charAt(bits & 0x0F));
		}
		return sb.toString();
	}


	/**
	 * Normalises a name according to the default message digest algorithm.
	 * 
	 * @param name name to normalize according to selected normalization algorithm
	 * @return normalized name
	 * 
	 * @see #DEFAULT_MESSAGE_DIGEST_ALGORITHM_NAME
	 */
	public static String normaliseName(final String name) {
		return normaliseName(name, DEFAULT_MESSAGE_DIGEST_ALGORITHM_NAME, DEFAULT_NORMALISE_PREFIX);
	}


	/**
	 * Normalises a name according to the default message digest algorithm.
	 * 
	 * @param name name to normalize according to selected normalization algorithm
	 * @param algorithm message digest algorithm to use or {@code null} to do nothing
	 * @return normalized name
	 * 
	 * @see #DEFAULT_MESSAGE_DIGEST_ALGORITHM_NAME
	 */
	public static String normaliseName(final String name, final String algorithm) {
		return normaliseName(name, algorithm, DEFAULT_NORMALISE_PREFIX);
	}


	/**
	 * Normalises a name according to the selected normalisation algorithm.
	 * Returns the name without any normalisation if no normalisation algorithm is selected.
	 * 
	 * @param name name to normalize according to selected normalization algorithm
	 * @param algorithm message digest algorithm to use or {@code null} to do nothing
	 * @param prefix prefix to prepend after normalisation or {@code null} to prepend nothing
	 * @return normalized or unmodified name, if algorithm is {@code null}
	 */
	public static String normaliseName(final String name, final String algorithm, final String prefix) {
		if (algorithm == null) {
			return name;
		}
		final MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance(algorithm);
		} catch (final NoSuchAlgorithmException e) {
			throw new RuntimeException("MessageDigest not available: " + algorithm, e);
		}
		final byte[] nameBytes = name.toLowerCase(Locale.ROOT).getBytes();
		final byte[] nameDigest = messageDigest.digest(nameBytes);
		return prefix + byteArrayToHexString(nameDigest);
	}

}
