package eu.europa.ec.cipa.dispatcher.util;


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
	public static final String INIT_INTERFACE_IMPLEMENTATION_CLASS = "init_interface_implementation_class";
	public static final String DB_DRIVER_NAME = "db_driver_name";
	public static final String DB_URL = "db_url";
	public static final String DB_USER = "db_username";
	public static final String DB_PASS = "db_password";
	public static final String KEYSTORE_PATH = "keystore_path";
	public static final String KEYSTORE_PASS = "keystore_password";
	public static final String KEYSTORE_AP_CA_ALIAS = "keystore_ap_ca_alias";
	public static final String KEYSTORE_AP_ALIAS = "keystore_ap_alias";
	public static final String OCSP_RESPONDER_URL = "ocsp_responder_url";
	public static final String OCSP_VALIDATION_ACTIVATED = "ocsp_validation_activated";
	public static final String TEMP_FOLDER_PATH = "temp_folder_path";
	public static final String SMP_MODE = "smp_mode";
	public static final String SMP_URL = "smp_url";
	public static final String EBMS_WSDL_PATH = "ebms_wsdl_path";
	public static final String EBMS_ENDPOINT_PREFERENCE_ORDER = "ebms_endpoint";
	public static final String START_ENDPOINT_PREFERENCE_ORDER = "start_endpoint";
	public static final String AS2_ENDPOINT_PREFERENCE_ORDER = "as2_endpoint";
	public static final String CACHE_MAX_NUMBER_ENTRIES = "cache_max_number_entries";
	public static final String CACHE_EXPIRE_AFTER_HOURS = "cache_expire_entry_after_hours";
	public static final String AS4_PMODE_FILEPATH = "as4_pmodeFilePath";
	public static final String AS4_ENDPOINT_URL = "as4_endpoint_url";
	

	/** If properties haven't been loaded yet, loads it from context passed as parameter or default file location in case context==null.
	 * @return the loaded properties or null if there was any problem loading the properties file.  
	 */
	public static Properties getProperties(ServletContext context)
	{
		if (properties == null)
		{
			if (context == null)
			{
				try
				{
					properties = new Properties();
					InputStream stream =PropertiesUtil.class.getClassLoader().getResourceAsStream("config/conf.properties");
					properties.load(stream);
					stream.close();
				}
				catch (Exception e)
				{
					properties = null;
				}
			}
			else
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
			}
		}
			
		return properties;
		
	}
	
}
