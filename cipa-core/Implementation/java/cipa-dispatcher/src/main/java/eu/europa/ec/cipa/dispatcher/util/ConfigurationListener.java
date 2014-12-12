package eu.europa.ec.cipa.dispatcher.util;

import java.net.Authenticator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.preferences.PreferencesAS2;

public class ConfigurationListener implements ServletContextListener {

	private static final Logger s_aLogger = LoggerFactory.getLogger (ConfigurationListener.class);
	Properties properties;
	Preferences preferences;
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		try {
			properties = PropertiesUtil.getProperties(null);
			System.out.println("Entering config listener");
			String os = System.getProperty("os.name").toLowerCase();
			// on windows systems it is common to use as root, the activation will
			// be system wide. On Linux/Unix systems it is ok to activate the
			// IDE for only one user (lets say user account "mendelson" )
			if (os.startsWith("win")) {
				// windows 7 and windows vista: use system node for the preferences,
				// enables the settings
				// for a single user
				if (os.startsWith("windows 7") || os.startsWith("windows vista")) {
					this.preferences = Preferences
							.userNodeForPackage(AS2ServerVersion.class);
				} else {
					this.preferences = Preferences
							.systemNodeForPackage(AS2ServerVersion.class);
				}
				try {
					// check if the user has the rights to access the system node
					this.preferences.putInt("rights_check", 1);
					this.preferences.flush();
				} catch (BackingStoreException e) {
					// switch back to user preferences, user has no rights to access
					// the system node
					this.preferences = Preferences
							.userNodeForPackage(AS2ServerVersion.class);
				} catch (SecurityException e) {
					// switch back to user preferences, user has no rights to access
					// the system node
					this.preferences = Preferences
							.userNodeForPackage(AS2ServerVersion.class);
				}
			} else {
				this.preferences = Preferences
						.userNodeForPackage(AS2ServerVersion.class);
			}

			
			preferences.put(PreferencesAS2.KEYSTORE,properties.getProperty(PropertiesUtil.KEYSTORE_PATH));
			preferences.put(PreferencesAS2.KEYSTORE_PASS,properties.getProperty(PropertiesUtil.KEYSTORE_PASS));
			preferences.put(PreferencesAS2.DIR_LOG,"../logs");
			

			s_aLogger
			.info("Getting keystore pref " + preferences.get(PreferencesAS2.KEYSTORE, "certificates.p12"));
			
			s_aLogger
			.info("Log directory " + preferences.get(PreferencesAS2.DIR_LOG, "certificates.p12"));
			
			Boolean useProxy = Boolean.valueOf( properties.getProperty(PropertiesUtil.USE_PROXY));
			
			if (useProxy){
				preferences.put(PreferencesAS2.PROXY_USE,"TRUE");
				s_aLogger
				.info("Usage of Proxy required");
				
				if (properties.getProperty(PropertiesUtil.HTTP_PROXY_HOST) != null && properties.getProperty(PropertiesUtil.HTTP_PROXY_PORT)!=null ){
					System.setProperty("http.proxyHost", properties.getProperty(PropertiesUtil.HTTP_PROXY_HOST));
					preferences.put(PreferencesAS2.PROXY_HOST,properties.getProperty(PropertiesUtil.HTTP_PROXY_HOST));
					System.setProperty("http.proxyPort", properties.getProperty(PropertiesUtil.HTTP_PROXY_PORT));
					preferences.put(PreferencesAS2.PROXY_PORT,properties.getProperty(PropertiesUtil.HTTP_PROXY_PORT));
				}
				if (properties.getProperty(PropertiesUtil.HTTPS_PROXY_HOST) != null && properties.getProperty(PropertiesUtil.HTTPS_PROXY_PORT)!=null ){
					System.setProperty("https.proxyHost", properties.getProperty(PropertiesUtil.HTTPS_PROXY_HOST));
					preferences.put(PreferencesAS2.PROXY_HOST,properties.getProperty(PropertiesUtil.HTTP_PROXY_HOST));
					System.setProperty("https.proxyPort", properties.getProperty(PropertiesUtil.HTTPS_PROXY_PORT));
					preferences.put(PreferencesAS2.PROXY_PORT,properties.getProperty(PropertiesUtil.HTTP_PROXY_PORT));
				}
				
				if (properties.getProperty(PropertiesUtil.PROXY_USER) != null && properties.getProperty(PropertiesUtil.PROXY_PASSW)!=null ){
					preferences.put(PreferencesAS2.AUTH_PROXY_USE,"TRUE");
					preferences.put(PreferencesAS2.AUTH_PROXY_USER,properties.getProperty(PropertiesUtil.PROXY_USER));
					preferences.put(PreferencesAS2.AUTH_PROXY_PASS,properties.getProperty(PropertiesUtil.PROXY_PASSW));
					Authenticator.setDefault(new ProxyAuthenticator(properties.getProperty(PropertiesUtil.PROXY_USER),properties.getProperty(PropertiesUtil.PROXY_PASSW)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			s_aLogger.error("Error occured during dispatcher initialisation ",e);
		
		}
	
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		
	}

}
