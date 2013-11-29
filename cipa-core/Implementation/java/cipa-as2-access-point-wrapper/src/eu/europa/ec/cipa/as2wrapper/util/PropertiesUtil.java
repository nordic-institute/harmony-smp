package eu.europa.ec.cipa.as2wrapper.util;


import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;


public class PropertiesUtil
{

	private static Properties properties = null;
	
	
	public static final String AS2_ENDPOINT_URL = "as2_endpoint_url";
	public static final String SERVER_MODE = "server_mode";
	public static final String SSL_TRUSTSTORE = "ssl_truststore";
	public static final String SSL_TRUSTSTORE_PASSWORD = "ssl_truststore_password";
	public static final String PARTNER_INTERFACE_IMPLEMENTATION_CLASS = "partner_interface_implementation_class";
	public static final String SEND_INTERFACE_IMPLEMENTATION_CLASS = "send_interface_implementation_class";
	public static final String DB_DRIVER_NAME = "db_driver_name";
	public static final String DB_URL = "db_url";
	public static final String DB_USER = "db_username";
	public static final String DB_PASS = "db_password";
	public static final String KEYSTORE_PATH = "keystore_path";
	public static final String KEYSTORE_PASS = "keystore_password";
	public static final String KEYSTORE_AP_AC_ALIAS = "keystore_ap_ca_alias";
	public static final String SMP_MODE = "smp_mode";
	public static final String SMP_URL = "smp_url";
	
	
	/** Loads the properties file using the servlet context passed as parameter
	 * @return null if there was any problem loading the properties file.
	 */
	public static Properties initializeProperties(ServletContext context)
	{
		try
		{
			properties = new Properties();
			InputStream stream = context.getResourceAsStream("/WEB-INF/conf/conf.properties");
			properties.load(stream);
			stream.close();
		}
		catch (Exception e)
		{
			properties = null;
		}
		
		return properties;
		
	}
	

	public static Properties getProperties()
	{
		return properties;
	}
	
}
