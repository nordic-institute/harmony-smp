package eu.europa.ec.cipa.as2wrapper.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Properties;

public class KeystoreUtil
{

	private Properties properties = PropertiesUtil.getProperties();
	private KeyStore keyStore = null;
	private String pathToFile;
	private String password;
	
	
	public KeystoreUtil() throws Exception
	{
		pathToFile = properties.getProperty(PropertiesUtil.KEYSTORE_PATH);
		password = properties.getProperty(PropertiesUtil.KEYSTORE_PASS);
		
		String[] keystoreTypes = {"pkcs12", "jks", "jceks"};
        
        File inFile = new File(pathToFile);
        FileInputStream inStream = null;
        try
        {
        	if (inFile.exists())
        	{
        		inStream = new FileInputStream(inFile);
        		boolean success = true;
        		for (int i=0 ; i<keystoreTypes.length ; i++)
        		{
        			success = true;
        			if (keystoreTypes[i].equals("pkcs12"))
        				keyStore = KeyStore.getInstance(keystoreTypes[i], "BC");  // BouncyCastle implementation of a keystore allows for the PKCS12 keystore to accept trusted certificates. The default Java provider doesn't.
        			else
        				keyStore = KeyStore.getInstance(keystoreTypes[i]);      //but BouncyCastle doesnt have an implementation for jks! so we try with the default implementation
            		try
            		{
            			keyStore.load(inStream, password.toCharArray());
            		}
            		catch (IOException e)
            		{
            			success = false;
            			inStream.close();
            			inStream = new FileInputStream(inFile);  //we close and reopen the stream so it can be read again
            		}
            		if (success) break;
        		}
        		if (!success)
        			throw new Exception("Couldn't load the keystore");
        		
            }
        }
        finally
        {
            if (inStream != null)
                inStream.close();
        }
	}
	
	
	/** Creates a new certificate in the AS2 endpoint's truststore
	 * @return
	 */
	public String installNewPartnerCertificate (X509Certificate cert, String alias) throws Exception
	{
		//there shouldn't be two similar PEPPOL cetificate Common Names, so the prefered behaviour is to override if we receive an alias that already exists (we assume there's been a certificate renewal)
		//String uniqueAlias = ensureUniqueAliasName(keyStore, alias);
		keyStore.setCertificateEntry(alias, cert);
		
		FileOutputStream output = new FileOutputStream(pathToFile);
		keyStore.store(output, password.toCharArray());
		output.close();
		
		//return uniqueAlias
		return alias;
	}
	
	/**	retrieves the PEPPOL AP CA certificate from the keystore
	 */
	public X509Certificate getApCaCertificate() throws Exception
	{
		return (X509Certificate) keyStore.getCertificate(properties.getProperty(PropertiesUtil.KEYSTORE_AP_AC_ALIAS));
	}
	
	
	
    /**Checks that an alias for an import is unique in this keystore*/
    private String ensureUniqueAliasName(KeyStore keystore, String alias) throws Exception
    {
        int counter = 2;
        String newAlias = alias;
        
        while (keystore.containsAlias(newAlias)) {
            newAlias = alias + "_" + counter;
            counter++;
        }
        alias = newAlias;
        return (alias);
    }
	
}
