package eu.europa.ec.cipa.sml.server.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.DSAPrivateKey;
import java.security.spec.DSAParameterSpec;
import java.security.spec.DSAPrivateKeySpec;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.utils.base64;

import com.phloc.commons.exceptions.InitializationException;

import eu.europa.ec.cipa.peppol.utils.ConfigFile;

public class SIG0KeyProvider {

	private static final Logger s_aLogger = LoggerFactory.getLogger(SIG0KeyProvider.class);
	public static final String CONFIG_SIG0_FILE = "dnsClient.SIG0KeyFileName";
	private static PrivateKey privateKey;
	private static String sKeyFile = null;

	private static String[] lineStarts = new String[] { "Subprime(q)", "Prime(p)", "Base(g)", "Private_value(x)", "Public_value(y)" };

	static {
		final ConfigFile aConfigFile = ConfigFile.getInstance();
		try {
			sKeyFile = aConfigFile.getString(CONFIG_SIG0_FILE, "SIG0");
		} catch (final Throwable t) {
			final String sErrorMsg = "Failed to read SIG(0) KeyFile from '" + sKeyFile + "'";
			s_aLogger.error(sErrorMsg);
			throw new InitializationException(sErrorMsg, t);
		}
	}

	private PrivateKey getPrivateKey(String filename) throws Exception {
		URL url = this.getClass().getResource("/" + filename);
		File f = new File(url.toURI());
		// FileInputStream fis = new FileInputStream(f);
		// DataInputStream dis = new DataInputStream(fis);
		// byte[] keyBytes = new byte[(int) f.length()];
		// dis.readFully(keyBytes);
		// dis.close();
		// PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		// KeyFactory kf = KeyFactory.getInstance("DSA");
		// return kf.generatePrivate(spec);

		// KeyStore ks = KeyStore.getInstance("PKCS12");
		// ks.load(fis, null);

		// OpenSSLKey opensslkey = new BouncyCastleOpenSSLKey(fis);
		// PrivateKey privateKey = opensslkey.getPrivateKey();

		KeyFactory keyFactory = KeyFactory.getInstance("DSA");
		KeySpec privateKeySpec = readDSAPrivateKey(f);
		PrivateKey key = keyFactory.generatePrivate(privateKeySpec);
		return key;

		// return readPrivateKey(f);
		// PrivateKey pk = (PrivateKey) ks.getKey(args[0], c);
	}

	private PrivateKey readPrivateKey(File file) {
		ObjectInputStream keyIn;
		PrivateKey privkey = null;
		try {
			keyIn = new ObjectInputStream(new FileInputStream(file));
			privkey = (PrivateKey) keyIn.readObject();
			keyIn.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return privkey;
	}

	private DSAPrivateKeySpec readDSAPrivateKey(File file) {
		BigInteger[] values = new BigInteger[6];

		try {

			BufferedReader in = new BufferedReader(new FileReader(file));
			in.readLine(); // skip header
			String[] parts;
			for (int i = 0; i < values.length; ++i) {
				parts = in.readLine().split(": ");
				for (String s : lineStarts) {
					if (parts[0].equals(s)) {
						try {
							byte[] data = base64.fromString(parts[1].trim().replace("\t", ""));
							values[i] = new BigInteger(1, data);
						} catch (NumberFormatException ex) {
							s_aLogger.error(ex.getLocalizedMessage());
							values[i] = new BigInteger("0");
						} catch (IndexOutOfBoundsException e) {
							s_aLogger.error(e.getLocalizedMessage());
							values[i] = new BigInteger("0");
						}
					}
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			// node.printout(Simnet.VC_ALWAYS, Simnet.VL_ERROR, node.id,
			// "Could not read DSA private key for " + file);
		}
		DSAPrivateKeySpec priv = new DSAPrivateKeySpec(values[4], values[1], values[2], values[3]);
		return priv;
	}

	@Nonnull
	public PrivateKey getPrivateSIG0Key() throws Exception {
		return getPrivateKey(sKeyFile);
	}
}
