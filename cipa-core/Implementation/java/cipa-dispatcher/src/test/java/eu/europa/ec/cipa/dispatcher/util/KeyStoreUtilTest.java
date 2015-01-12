package eu.europa.ec.cipa.dispatcher.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.KeyStore.LoadStoreParameter;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.annotation.Nullable;

import org.junit.Test;

import com.helger.commons.string.StringHelper;

public class KeyStoreUtilTest {

	private static Properties properties = PropertiesUtil.getProperties();	
	private KeyStore keyStore = null;

	@Test
	public void test() {
		try {
			KeystoreUtil util = new KeystoreUtil(
					properties.getProperty(PropertiesUtil.KEYSTORE_PATH),
					properties.getProperty(PropertiesUtil.KEYSTORE_PASS));
			assertTrue(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("Not yet implemented");
		}

	}

	@Test
	public void testInstallNewPartnerCertificate() {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		try {
			keyStore = KeyStore.getInstance("pkcs12", "BC");
			keyStore.load(null);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			String cert_aux = getRFC1421CompliantString("MIIEhDCCA2ygAwIBAgIQU+95w1SYu2M4NZoZXS3YjDANBgkqhkiG9w0BAQsFADCBizELMAkGA1UEBhMCREsxJzAlBgNVBAoTHk5BVElPTkFMIElUIEFORCBURUxFQ09NIEFHRU5DWTEfMB0GA1UECxMWRk9SIFRFU1QgUFVSUE9TRVMgT05MWTEyMDAGA1UEAxMpUEVQUE9MIFNFUlZJQ0UgTUVUQURBVEEgUFVCTElTSEVSIFRFU1QgQ0EwHhcNMTQxMDIxMDAwMDAwWhcNMTYxMDIwMjM1OTU5WjA5MQswCQYDVQQGEwJCRTERMA8GA1UECgwIREctRElHSVQxFzAVBgNVBAMMDlNNUF8xMDAwMDAwMDA3MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzq6Q2AaRX3MvsyidYp8pMMeOdlw3AysYleh3U5LPaOyjKbVLDRt3zzmAYOCHyizc5Vb8GjAhOesj/M76/tVAJj9zvWul4Wmjn960b4nojD2Xn+gNKVVIjz2d9MndGW19xcMAFfz3/jxk86oIqY0/LHLKp4Bm0Hp3QQrVLuV2kFnZOhQnxDFcPEfyOk2eKuXgYIi/x3TezoldSnZ51lVoHD+XUCMWNNbxHVYzXpdsF56fMr3KmCkuG+E5l5LE5DpJWwswk3SPfINNWKnRCc5dnsMSkn8DAPzFryyMD+PofUCfrETx8TeYUXjZheSi7tA1K/QuXwMx3RjNZsifTPLfzwIDAQABo4IBMzCCAS8wCQYDVR0TBAIwADALBgNVHQ8EBAMCA7gwgYMGA1UdHwR8MHoweKB2oHSGcmh0dHA6Ly9waWxvdG9uc2l0ZWNybC52ZXJpc2lnbi5jb20vRGlnaXRhbGlzZXJpbmdzc3R5cmVsc2VuUGlsb3RPcGVuUEVQUE9MU0VSVklDRU1FVEFEQVRBUFVCTElTSEVSQ0EvTGF0ZXN0Q1JMLmNybDAfBgNVHSMEGDAWgBTgx4VANTr9HPP2XsIfMNDyZKECdzAdBgNVHQ4EFgQUUo5UXrnVU78SDk9jGkjF97+KnVowOgYIKwYBBQUHAQEELjAsMCoGCCsGAQUFBzABhh5odHRwOi8vcGlsb3Qtb2NzcC52ZXJpc2lnbi5jb20wEwYDVR0lBAwwCgYIKwYBBQUHAwIwDQYJKoZIhvcNAQELBQADggEBAGZIgmMArTKUgKGciNGzfxEpgAFm0gGeNKUoCHYfcWvw0pT+JVbk4Uyj6RCdO6A/4tRUsXzC0dpBS8yvNSZ0Itels0Ellr12MfFhWWxQy5ehFeujsYmQCuhTCgcqdcDjCrUZ6W6mBOmJ/C1AFJJ4t/akJVTbWgNauB62MHKZp19FOZistR6USUMDar1kttFjcnXVGSXYpllO3/eDnNPFy4cV6ECW7RE9AZA8blN8T+KN1C9y51xSxAlvni5kgPkFgAz2ULDwnI5Y7VUK4LbEYuk1IljMGUthd7g1zl//CgJb3txw8YRgE26Zivl55ZMl0u5ltxiHbNYboL659Hvv2YE=");
			if (!cert_aux.startsWith("-----BEGIN CERTIFICATE-----"))
				cert_aux = "-----BEGIN CERTIFICATE-----\n" + cert_aux + "\n-----END CERTIFICATE-----";
			X509Certificate certificate = (X509Certificate) cf
					.generateCertificate(new ByteArrayInputStream(cert_aux
							.getBytes()));
			String cn = KeystoreUtil.extractCN(certificate);
			keyStore.setCertificateEntry(cn, certificate);
			assertNotNull(keyStore.getCertificate("SMP_1000000007"));
			assertTrue(KeystoreUtil.extractCN((X509Certificate)keyStore.getCertificate("SMP_1000000007")).equals("SMP_1000000007"));
		} catch (Exception e) {
			fail("Not yet implemented");
		}
		finally{
			keyStore = null;
		}
		
		try {
			keyStore = KeyStore.getInstance("jks");
			keyStore.load(null);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			String cert_aux = getRFC1421CompliantString("MIIEhDCCA2ygAwIBAgIQU+95w1SYu2M4NZoZXS3YjDANBgkqhkiG9w0BAQsFADCBizELMAkGA1UEBhMCREsxJzAlBgNVBAoTHk5BVElPTkFMIElUIEFORCBURUxFQ09NIEFHRU5DWTEfMB0GA1UECxMWRk9SIFRFU1QgUFVSUE9TRVMgT05MWTEyMDAGA1UEAxMpUEVQUE9MIFNFUlZJQ0UgTUVUQURBVEEgUFVCTElTSEVSIFRFU1QgQ0EwHhcNMTQxMDIxMDAwMDAwWhcNMTYxMDIwMjM1OTU5WjA5MQswCQYDVQQGEwJCRTERMA8GA1UECgwIREctRElHSVQxFzAVBgNVBAMMDlNNUF8xMDAwMDAwMDA3MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzq6Q2AaRX3MvsyidYp8pMMeOdlw3AysYleh3U5LPaOyjKbVLDRt3zzmAYOCHyizc5Vb8GjAhOesj/M76/tVAJj9zvWul4Wmjn960b4nojD2Xn+gNKVVIjz2d9MndGW19xcMAFfz3/jxk86oIqY0/LHLKp4Bm0Hp3QQrVLuV2kFnZOhQnxDFcPEfyOk2eKuXgYIi/x3TezoldSnZ51lVoHD+XUCMWNNbxHVYzXpdsF56fMr3KmCkuG+E5l5LE5DpJWwswk3SPfINNWKnRCc5dnsMSkn8DAPzFryyMD+PofUCfrETx8TeYUXjZheSi7tA1K/QuXwMx3RjNZsifTPLfzwIDAQABo4IBMzCCAS8wCQYDVR0TBAIwADALBgNVHQ8EBAMCA7gwgYMGA1UdHwR8MHoweKB2oHSGcmh0dHA6Ly9waWxvdG9uc2l0ZWNybC52ZXJpc2lnbi5jb20vRGlnaXRhbGlzZXJpbmdzc3R5cmVsc2VuUGlsb3RPcGVuUEVQUE9MU0VSVklDRU1FVEFEQVRBUFVCTElTSEVSQ0EvTGF0ZXN0Q1JMLmNybDAfBgNVHSMEGDAWgBTgx4VANTr9HPP2XsIfMNDyZKECdzAdBgNVHQ4EFgQUUo5UXrnVU78SDk9jGkjF97+KnVowOgYIKwYBBQUHAQEELjAsMCoGCCsGAQUFBzABhh5odHRwOi8vcGlsb3Qtb2NzcC52ZXJpc2lnbi5jb20wEwYDVR0lBAwwCgYIKwYBBQUHAwIwDQYJKoZIhvcNAQELBQADggEBAGZIgmMArTKUgKGciNGzfxEpgAFm0gGeNKUoCHYfcWvw0pT+JVbk4Uyj6RCdO6A/4tRUsXzC0dpBS8yvNSZ0Itels0Ellr12MfFhWWxQy5ehFeujsYmQCuhTCgcqdcDjCrUZ6W6mBOmJ/C1AFJJ4t/akJVTbWgNauB62MHKZp19FOZistR6USUMDar1kttFjcnXVGSXYpllO3/eDnNPFy4cV6ECW7RE9AZA8blN8T+KN1C9y51xSxAlvni5kgPkFgAz2ULDwnI5Y7VUK4LbEYuk1IljMGUthd7g1zl//CgJb3txw8YRgE26Zivl55ZMl0u5ltxiHbNYboL659Hvv2YE=");
			if (!cert_aux.startsWith("-----BEGIN CERTIFICATE-----"))
				cert_aux = "-----BEGIN CERTIFICATE-----\n" + cert_aux + "\n-----END CERTIFICATE-----";
			X509Certificate certificate = (X509Certificate) cf
					.generateCertificate(new ByteArrayInputStream(cert_aux
							.getBytes()));
			String cn = KeystoreUtil.extractCN(certificate);
			keyStore.setCertificateEntry(cn, certificate);
			assertNotNull(keyStore.getCertificate("SMP_1000000007"));
			assertTrue(KeystoreUtil.extractCN((X509Certificate)keyStore.getCertificate("SMP_1000000007")).equals("SMP_1000000007"));
		} catch (Exception e) {
			fail("Not yet implemented");
		}
		finally{
			keyStore = null;
		}
	}

	public static String getRFC1421CompliantString(
			@Nullable final String sCertificate) {
		if (StringHelper.hasNoText(sCertificate))
			return sCertificate;

		// Remove all existing whitespace characters
		String sPlainString = StringHelper.getWithoutAnySpaces(sCertificate);

		// Start building the result
		final int nMaxLineLength = 64;
		final String sCRLF = "\r\n";
		final StringBuilder aSB = new StringBuilder();
		while (sPlainString.length() > nMaxLineLength) {
			// Append line + CRLF
			aSB.append(sPlainString, 0, nMaxLineLength).append(sCRLF);

			// Remove the start of the string
			sPlainString = sPlainString.substring(nMaxLineLength);
		}

		// Append the rest
		aSB.append(sPlainString);

		return aSB.toString();
	}

}
