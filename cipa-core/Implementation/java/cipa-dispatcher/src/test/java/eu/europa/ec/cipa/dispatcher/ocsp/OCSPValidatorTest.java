package eu.europa.ec.cipa.dispatcher.ocsp;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Properties;

import com.google.common.base.Strings;
import eu.europa.ec.cipa.dispatcher.util.PropertiesUtil;
import eu.europa.ec.cipa.dispatcher.util.ProxyAuthenticator;
import org.apache.http.HttpConnection;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class OCSPValidatorTest {

	private static final Logger logger = Logger.getLogger(OCSPValidatorTest.class);

	@BeforeClass
	public static void beforeClass() {
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

	//TODO: this test uses for now the current production certificates should be replaced.
	@Test
	public void test() {

		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		try {

			// int i =
			// getResponseCode("http://onsitecrl.verisign.com/DigitaliseringsstyrelsenOpenPEPPOLACCESSPOINTCA/LatestCRL.crl");
			KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
			keyStore.load(this.getClass().getResourceAsStream("/test.p12"),
					"Europa2011".toCharArray());
			X509Certificate caCert = (X509Certificate) keyStore
					.getCertificate("ap production new pki keypair");
			X509Certificate inter = (X509Certificate) keyStore
					.getCertificate("peppol access point ca (peppol root ca)");
			X509Certificate rootCert = (X509Certificate) keyStore
					.getCertificate("peppol root ca");
			assertTrue(OCSPValidator.check(caCert, inter,
					"http://pki-ocsp.symauth.com:80"));

		} catch (Exception e) {
			e.printStackTrace();
			fail("exception occurred");
		}
	}

	public static int getResponseCode(String urlString)
			throws MalformedURLException, IOException {
		URL u = new URL(urlString);
		HttpURLConnection huc = (HttpURLConnection) u.openConnection();
		huc.setRequestMethod("GET");
		huc.connect();
		return huc.getResponseCode();
	}

}
