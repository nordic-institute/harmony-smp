package eu.europa.ec.cipa.dispatcher.util;

import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.cipa.dispatcher.exception.CertRevokedException;
import eu.europa.ec.cipa.dispatcher.ocsp.OCSPValidator;

public abstract class CertificateCheck{
	
	static final Logger s_aLogger = LoggerFactory.getLogger(CertificateCheck.class);

	public static void doCheck(X509Certificate cert, boolean ocps) throws CertificateNotYetValidException, CertificateExpiredException, CertRevokedException {
		// check the certificate is not expired
		try {
			cert.checkValidity();
		} catch (CertificateNotYetValidException e) {
			s_aLogger.error(HttpServletResponse.SC_UNAUTHORIZED + "The certificate used to sign is not yet valid");
			throw e;
		} catch (CertificateExpiredException e) {
			s_aLogger.error(HttpServletResponse.SC_UNAUTHORIZED + " The certificate used to sign has expired");
			throw e;
		}
	
		// OCSP validation of the certificate used to sign this message
		boolean valid = true;
		if (ocps) {
			try {
				valid = OCSPValidator.certificateValidate(cert);
			} catch (Exception e) {
				s_aLogger.error("Unable to validate the incoming certificate", e);
				valid = false;
			}
			if (!valid) {
				s_aLogger.error("The certificate used to sign has been revoked");
				throw new CertRevokedException();
			}
		}		
	}

}