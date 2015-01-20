package eu.europa.ec.cipa.dispatcher.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;


import eu.europa.ec.cipa.dispatcher.exception.CertRevokedException;
import eu.europa.ec.cipa.dispatcher.util.CertificateCheck;
import eu.europa.ec.cipa.dispatcher.util.KeystoreUtil;
import eu.europa.ec.cipa.dispatcher.util.PropertiesUtil;
import org.apache.log4j.Logger;

public abstract class AbstractReceiverServlet extends HttpServlet {

	protected String classType;
	private Properties properties;
	private boolean debug = false;
	static final Logger s_aLogger = Logger.getLogger(AbstractReceiverServlet.class);

	public AbstractReceiverServlet() {
		super();
	}
	
	public void init() throws UnavailableException {

		properties = PropertiesUtil.getProperties();
		if (properties == null) {
			s_aLogger.error("Error initializing " + classType + " receiver servlet: Couldn't load necessary configuration file");
			throw new UnavailableException("Couldn't load necessary configuration file");
		}

		if ("DEBUG".equalsIgnoreCase(properties.getProperty(PropertiesUtil.SERVER_MODE, "DEBUG")))
			debug = true;

	}

	/**
	 * checks if the user-provided certificate is valid and has been signed by the peppol AP CA certificate
	 * 
	 * @return an error message if the certificate is expired or not trusted, null if everything is ok. Throws exception if there was a problem loading the necessary keystore.
	 */
	protected String checkSignatureTrust(X509Certificate userCert) throws Exception {
		String truststorePath = properties.getProperty(PropertiesUtil.DISPATCHER_TRUSTSTORE_PATH);
		String truststorePassword = properties.getProperty(PropertiesUtil.DISPATCHER_TRUSTSTORE_PASSWORD);
		KeystoreUtil truststoreAccess = new KeystoreUtil(truststorePath, truststorePassword);
		X509Certificate rootCert = truststoreAccess.getApCaCertificate();

		// Verify the current certificate using the issuer certificate
		try {
			boolean valid = CertificateCheck.validateKeyChain(userCert, truststoreAccess.getKeyStore(), rootCert);
			if (!valid){
				return "The current certificate " + userCert.getIssuerDN() + " is not trusted in the truststore " + properties.getProperty(PropertiesUtil.DISPATCHER_TRUSTSTORE_PATH);
			}
		} catch (final Exception e) {
			return e.getMessage();
		}

		// Check time validity
		try {
			userCert.checkValidity();
		} catch (final Exception e) {
			return e.getMessage();
		}

		return null; // means everything went well
	}

	protected ByteArrayOutputStream copyToMarkableBuffer(InputStream in) {
		ByteArrayOutputStream buffer = null;
		try {
			buffer = new ByteArrayOutputStream();
			byte temp[] = new byte[0x10000];
			for (int count = in.read(temp); count > 0; count = in.read(temp))
				buffer.write(temp, 0, count);
			in.close();
		} catch (Exception e) {
			s_aLogger.error("Internal server Error occured", e);
		}

		return buffer;
	}

	protected boolean validateCertificate(HttpServletResponse resp, X509Certificate cert) throws IOException, Exception {
		if (cert == null) {
			s_aLogger.error(HttpServletResponse.SC_BAD_REQUEST + "Unable to retrieve certificate from incoming message");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unable to retrieve certificate from incoming message");
			return false;
		}
		// check the certificate was signed by our AP CA, if not, it's not a
		// valid request
		String trustError = checkSignatureTrust(cert);
		if (trustError != null) {
			s_aLogger.error(HttpServletResponse.SC_UNAUTHORIZED + "Untrusted/invalid signing certificate");
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Untrusted/invalid signing certificate");
			return false;
		}

		try {
			CertificateCheck.doCheck(cert, "true".equalsIgnoreCase(getProperties().getProperty(PropertiesUtil.OCSP_VALIDATION_ACTIVATED)));
		} catch (CertificateNotYetValidException e) {
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The certificate used to sign has expired");
			return false;
		} catch (CertificateExpiredException e) {
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The certificate used to sign has expired");
			return false;
		} catch (CertRevokedException e) {
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The certificate used to sign has been revoked");
			return false;
		}
		return true;
	}
	
	protected Properties getProperties() {
		return properties;
	}

	protected boolean isDebug() {
		return debug;
	}

	protected void setDebug(boolean debug) {
		this.debug = debug;
	}
}