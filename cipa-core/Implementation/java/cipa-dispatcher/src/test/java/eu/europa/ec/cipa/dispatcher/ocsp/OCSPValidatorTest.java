package eu.europa.ec.cipa.dispatcher.ocsp;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.X509Certificate;

import org.apache.http.HttpConnection;
import org.junit.Ignore;
import org.junit.Test;

public class OCSPValidatorTest {

	//TODO: this test uses for now the current production certificates should be replaced.
	@Test
	@Ignore
	public void test() {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		try {

			// int i =
			// getResponseCode("http://onsitecrl.verisign.com/DigitaliseringsstyrelsenOpenPEPPOLACCESSPOINTCA/LatestCRL.crl");
			KeyStore keyStore = KeyStore.getInstance("pkcs12", "BC");
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
