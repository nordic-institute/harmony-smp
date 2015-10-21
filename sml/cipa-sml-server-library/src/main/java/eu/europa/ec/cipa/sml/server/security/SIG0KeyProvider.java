package eu.europa.ec.cipa.sml.server.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.DSAPrivateKeySpec;
import java.security.spec.KeySpec;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xbill.DNS.utils.base64;

import com.helger.commons.exceptions.InitializationException;

import eu.europa.ec.cipa.peppol.utils.ConfigFile;

public class SIG0KeyProvider {

  private static final Logger s_aLogger = LoggerFactory.getLogger (SIG0KeyProvider.class);
  public static final String CONFIG_SIG0_FILE = "dnsClient.SIG0KeyFileName";
  private PrivateKey privateKey = null;
  private static String sKeyFile = null;

  private static String [] lineStarts = new String [] { "Subprime(q)",
                                                       "Prime(p)",
                                                       "Base(g)",
                                                       "Private_value(x)",
                                                       "Public_value(y)" };

  static {
       /* TODO : This is a quick and dirty hack to allow the use of a configuration file with an other name if it's
        in the classpath (like smp.config.properties or sml.config.properties).
        If the configuration file defined in applicationContext.xml couldn't be found, then the config.properties inside the war is used as a fallback.
        Needs to be properly refactored */
      ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"classpath:applicationContext.xml"});
      final ConfigFile aConfigFile = (ConfigFile) context.getBean("configFile");
    try {
      sKeyFile = aConfigFile.getString (CONFIG_SIG0_FILE, "SIG0");
    }
    catch (final Throwable t) {
      final String sErrorMsg = "Failed to read SIG(0) KeyFile from '" + sKeyFile + "'";
      s_aLogger.error (sErrorMsg);
      throw new InitializationException (sErrorMsg, t);
    }
  }

  private PrivateKey getPrivateKey (final String filename) throws Exception {
	if (privateKey == null){
	    synchronized(this){
	    	InputStream in = this.getClass().getResourceAsStream("/" + filename);
	    	KeyFactory keyFactory = KeyFactory.getInstance ("DSA");
	    	KeySpec privateKeySpec = readDSAPrivateKey (in);
	    	privateKey = keyFactory.generatePrivate (privateKeySpec);
	    }
	}
	return privateKey;
  }


  private DSAPrivateKeySpec readDSAPrivateKey (final InputStream inputs) {
    final BigInteger [] values = new BigInteger [6];

    try {

      final BufferedReader in = new BufferedReader (new InputStreamReader(inputs));
      in.readLine (); // skip header
      String [] parts;
      for (int i = 0; i < values.length; ++i) {
        parts = in.readLine ().split (": ");
        for (final String s : lineStarts) {
          if (parts[0].equals (s)) {
            try {
              final byte [] data = base64.fromString (parts[1].trim ().replace ("\t", ""));
              values[i] = new BigInteger (1, data);
            }
            catch (final NumberFormatException ex) {
              s_aLogger.error (ex.getLocalizedMessage ());
              values[i] = new BigInteger ("0");
            }
            catch (final IndexOutOfBoundsException e) {
              s_aLogger.error (e.getLocalizedMessage ());
              values[i] = new BigInteger ("0");
            }
          }
        }
      }
    }
    catch (final IOException ioe) {
      ioe.printStackTrace ();
    }
    final DSAPrivateKeySpec priv = new DSAPrivateKeySpec (values[4], values[1], values[2], values[3]);
    return priv;
  }

  @Nonnull
  public PrivateKey getPrivateSIG0Key () throws Exception {
    return getPrivateKey (sKeyFile);
  }
}
