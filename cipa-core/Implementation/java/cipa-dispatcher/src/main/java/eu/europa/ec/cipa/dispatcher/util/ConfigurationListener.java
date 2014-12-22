package eu.europa.ec.cipa.dispatcher.util;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.net.Authenticator;
import java.util.Properties;

public class ConfigurationListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Properties properties = PropertiesUtil.getProperties();
        try {
            Boolean useProxy = Boolean.valueOf(properties.getProperty(PropertiesUtil.USE_PROXY));

            if (useProxy) {
                logger.info("Usage of Proxy required");
                System.setProperty("java.net.useSystemProxies", "false");

                String httpProxyHost = properties.getProperty(PropertiesUtil.HTTP_PROXY_HOST);
                String httpProxyPort = properties.getProperty(PropertiesUtil.HTTP_PROXY_PORT);
                if (!Strings.isNullOrEmpty(httpProxyHost) && !Strings.isNullOrEmpty(httpProxyPort)) {
                    System.setProperty("http.proxyHost", httpProxyHost);
                    System.setProperty("http.proxyPort", httpProxyPort);
                }
                String httpsProxyHost = properties.getProperty(PropertiesUtil.HTTPS_PROXY_HOST);
                String httpsProxyPort = properties.getProperty(PropertiesUtil.HTTPS_PROXY_PORT);
                if (!Strings.isNullOrEmpty(httpsProxyHost) && !Strings.isNullOrEmpty(httpsProxyPort)) {
                    System.setProperty("https.proxyHost", httpsProxyHost);
                    System.setProperty("https.proxyPort", httpsProxyPort);
                }

                String proxyUser = properties.getProperty(PropertiesUtil.PROXY_USER);
                String proxyPassword = properties.getProperty(PropertiesUtil.PROXY_PASSW);
                if (!Strings.isNullOrEmpty(proxyUser) && !Strings.isNullOrEmpty(proxyPassword)) {
                    Authenticator.setDefault(new ProxyAuthenticator(proxyUser, proxyPassword));
                    System.setProperty("http.proxyUser", proxyUser);
                    System.setProperty("http.proxyPassword", proxyPassword);
                }
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            logger.error("Error occurred during dispatcher initialisation ", exc);
        }


    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // TODO Auto-generated method stub

    }

}
