package eu.europa.ec.cipa.bdmsl.util;

import com.google.common.base.Strings;
import eu.europa.ec.cipa.bdmsl.common.exception.BadConfigurationException;
import eu.europa.ec.cipa.common.logging.ILoggingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.security.Security;

/**
 * Created by feriaad on 22/06/2015.
 */
@Component
public class ConfigurationListener implements ServletContextListener {

    @Autowired
    protected ILoggingService loggingService;

    @Value("${httpProxyPort}")
    private String httpProxyPort;

    @Value("${httpProxyHost}")
    private String httpProxyHost;

    @Value("${httpsProxyPort}")
    private String httpsProxyPort;

    @Value("${httpsProxyHost}")
    private String httpsProxyHost;

    @Value("${httpProxyUser}")
    private String httpProxyUser;

    @Value("${httpProxyPassword}")
    private String httpProxyPassword;

    @Value("${useProxy}")
    private String useProxy;

    @Value("${dnsClient.SIG0KeyFileName}")
    private String dnsClientSIG0KeyFileName;

    @Value("${keystoreFileName}")
    private String keystoreFileName;

    @Value("${signResponse}")
    private String signResponse;

    @Value("${keystoreAlias}")
    private String keystoreAlias;

    @Value("${keystorePassword}")
    private String keystorePassword;

    @Value("${dnsClient.enabled}")
    private String dnsClientEnabled;

    @Value("${dnsClient.publisherPrefix}")
    private String dnsClientPublisherPrefix;

    @Value("${dnsClient.server}")
    private String dnsClientServer;

    @Value("${dnsClient.SIG0Enabled}")
    private String dnsClientSIG0Enabled;

    @Value("${dnsClient.SIG0PublicKeyName}")
    private String dnsClientSIG0PublicKeyName;

    @Value("${configurationDir}")
    private String configurationDir;


    @Override
    public void contextInitialized(ServletContextEvent sce) {
        WebApplicationContextUtils
                .getRequiredWebApplicationContext(sce.getServletContext())
                .getAutowireCapableBeanFactory()
                .autowireBean(this);

        loggingService.debug("Entering configuration listener...");

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        configureProxy();

        validateConfiguration();

        loggingService.debug("Exiting configuration listener...");
    }

    private void configureProxy() {
        try {
            Boolean useProxyBool = Boolean.parseBoolean(useProxy);

            if (useProxyBool) {
                loggingService.info("Usage of Proxy required");
                System.setProperty("java.net.useSystemProxies", "false");

                if (!Strings.isNullOrEmpty(httpProxyHost) && !Strings.isNullOrEmpty(httpProxyPort)) {
                    System.setProperty("http.proxyHost", httpProxyHost);
                    System.setProperty("http.proxyPort", httpProxyPort);
                }
                if (!Strings.isNullOrEmpty(httpsProxyHost) && !Strings.isNullOrEmpty(httpsProxyPort)) {
                    System.setProperty("https.proxyHost", httpsProxyHost);
                    System.setProperty("https.proxyPort", httpsProxyPort);
                }

                if (!Strings.isNullOrEmpty(httpProxyUser) && !Strings.isNullOrEmpty(httpProxyPassword)) {
                    Authenticator.setDefault(new ProxyAuthenticator(httpProxyUser, httpProxyPassword));
                    System.setProperty("http.proxyUser", httpProxyUser);
                    System.setProperty("http.proxyPassword", httpProxyPassword);
                }
            } else {
                loggingService.info("No proxy configured");
            }

        } catch (Exception exc) {
            loggingService.businessLog(LogEvents.BUS_CONFIGURATION_ERROR, exc, "Couldn't configure the proxy");
            throw new RuntimeException(exc);
        }
    }

    private void validateConfiguration() {
        try {
            if (Boolean.parseBoolean(signResponse)) {
                if (StringUtils.isEmpty(keystoreAlias)) {
                    throw new BadConfigurationException("If you sign the response, then you must set the 'keystoreAlias' property");
                }
                if (StringUtils.isEmpty(keystoreFileName)) {
                    throw new BadConfigurationException("If you sign the response, then you must set the 'keystoreFileName' property");
                }
                if (StringUtils.isEmpty(keystorePassword)) {
                    throw new BadConfigurationException("If you sign the response, then you must set the 'keystorePassword' property");
                }
                checkFileExist(keystoreFileName);
            }

            if (Boolean.parseBoolean(dnsClientEnabled)) {
                if (StringUtils.isEmpty(dnsClientPublisherPrefix)) {
                    throw new BadConfigurationException("If you enable the dns client, then you must set the 'dnsClient.publisherPrefix' property");
                }
                if (StringUtils.isEmpty(dnsClientServer)) {
                    throw new BadConfigurationException("If you enable the dns client, then you must set the 'dnsClient.server' property");
                }
            }

            if (Boolean.parseBoolean(dnsClientSIG0Enabled)) {
                if (StringUtils.isEmpty(dnsClientSIG0KeyFileName)) {
                    throw new BadConfigurationException("If you enable DNSSEC configuration, then you must set the 'dnsClient.SIG0KeyFileName' property");
                }
                if (StringUtils.isEmpty(dnsClientSIG0PublicKeyName)) {
                    throw new BadConfigurationException("If you enable DNSSEC configuration, then you must set the 'dnsClient.SIG0PublicKeyName' property");
                }
                checkFileExist(dnsClientSIG0KeyFileName);
            }
        } catch (Exception exc) {
            loggingService.businessLog(LogEvents.BUS_CONFIGURATION_ERROR, exc);
            throw new RuntimeException(exc);
        }
    }

    private void checkFileExist(String file) throws IOException, BadConfigurationException {

        if (StringUtils.isEmpty(configurationDir)) {
            throw new BadConfigurationException("The folder " + configurationDir + " doesn't exist");
        } else {
            if (!new File(configurationDir).isDirectory()) {
                throw new BadConfigurationException(configurationDir + " must be a directory");
            }
        }

        if (!configurationDir.endsWith("/")) {
            configurationDir += "/";
        }

        String path = configurationDir + file;

        if (!new File(path).exists()) {
            throw new BadConfigurationException("The file " + path + " doesn't exist");
        } else {
            if (!new File(path).isFile()) {
                throw new BadConfigurationException(path + " must be a file");
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    class ProxyAuthenticator extends Authenticator {

        private String user, password;

        public ProxyAuthenticator(String user, String password) {
            this.user = user;
            this.password = password;
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(user, password.toCharArray());
        }
    }
}
