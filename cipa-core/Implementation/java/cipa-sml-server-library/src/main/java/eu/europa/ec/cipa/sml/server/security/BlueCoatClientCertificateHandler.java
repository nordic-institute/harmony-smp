package eu.europa.ec.cipa.sml.server.security;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.security.auth.x500.X500Principal;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.string.StringHelper;

import eu.europa.ec.cipa.peppol.utils.ConfigFile;

/**
 * This Class is used to validate the client certificate if the 2 way ssl
 * connection id provideed by a blue coat reverse proxy. This specific reverse
 * proxy handles the certificate chain verification an just transmits
 * information extracted from the client certificate to the target SML server.
 * The header
 * 
 * @author orazisa
 * 
 */
@Immutable
public final class BlueCoatClientCertificateHandler {

	private static final Logger s_aLogger = LoggerFactory
			.getLogger(BlueCoatClientCertificateHandler.class);

	public static final String CONFIG_SML_CLIENT_CERTISSUER = "sml.client.certissuer";
	public static final String CONFIG_SML_CLIENT_CERTISSUER_NEW = "sml.client.certissuer.new";
	public static final String CLIENT_CERT_HEADER_KEY = "Client-Cert";

	private static String subject = null;
	private static String issuer = null;
	private static String serial = null;
	private static Date validFrom = null;
	private static Date validTo = null;

	private static void parseClientCertHeader(String clientCertHeaderDecoded) {

		String[] split = clientCertHeaderDecoded.split("&");
		for (int i = 0; i < split.length; i++) {
			System.out.println(split[i]);
		}

		if (split.length != 5) {

			throw new IllegalStateException(
					"Invalid BlueCoat Client Certificate Header Received ");
		}
		serial = split[0].substring(split[0].indexOf('=') + 1);
		subject = split[1].substring(split[1].indexOf('=') + 1);
		DateFormat df = new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz");

		try {
			validFrom = df.parse(split[2].substring(split[2].indexOf('=') + 1));
			validTo = df.parse(split[3].substring(split[3].indexOf('=') + 1));
		} catch (ParseException e) {
			throw new IllegalStateException(
					"Invalid BlueCoat Client Certificate Header Received (Unparsable Date) ");
		}
		issuer = split[4].substring(split[4].indexOf('=') + 1);

	}

	/**
	 * Extract certificate header from request and validate them.
	 * 
	 * @param aHttpRequest
	 *            The HTTP request to use.
	 * @return <code>true</code> if valid, <code>false</code> otherwise.
	 */
	public static boolean isClientCertificateValid(
			@Nonnull final HttpServletRequest aHttpRequest) {
		// This is how to get client certificate from request
		String clientCertHeader = aHttpRequest
				.getHeader(CLIENT_CERT_HEADER_KEY);

		String clientCertHeaderDecoded;
		try {
			clientCertHeaderDecoded = URLDecoder.decode(clientCertHeader,"UTF-8");
			parseClientCertHeader(clientCertHeaderDecoded);
			Date today = Calendar.getInstance().getTime();
			if(! today.after(validFrom) || ! today.before(validTo)){
				s_aLogger.warn("Certificate Is not valid at the curent date "+today );
				s_aLogger.warn("Certificate valid from "+validFrom + "To " + validTo);
				return false;
			}
			if (clientCertHeaderDecoded == null) {
				s_aLogger
						.warn("No Client Cert header found in the request's header");
				return false;
			}
			 final String sIssuerToSearch = ConfigFile.getInstance ().getString (CONFIG_SML_CLIENT_CERTISSUER);
			 if (sIssuerToSearch == null || sIssuerToSearch.isEmpty())
			        throw new IllegalStateException ("The configuration file is missing the entry '" +
			                                         CONFIG_SML_CLIENT_CERTISSUER +
			                                         "'");
		     final String sAlternativeIssuerToSearch = ConfigFile.getInstance ().getString (CONFIG_SML_CLIENT_CERTISSUER_NEW);
		     if (sAlternativeIssuerToSearch == null || sAlternativeIssuerToSearch.isEmpty())
			        throw new IllegalStateException ("The configuration file is missing the entry '" +
			        									CONFIG_SML_CLIENT_CERTISSUER_NEW +
			                                         "'");
		     if(sIssuerToSearch.equals(issuer)||sAlternativeIssuerToSearch.equals(issuer) ){
		    	 s_aLogger.info("Certificate issuer check successful : "+issuer ); 
		    	 return true;
		     }else{
		    	 return false;
		     }

		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Unable to decode client Cert Header");
		}

		// Main checking
		// return isClientCertificateValid ((X509Certificate []) aValue);
	}
	
	 @Nullable
	  public static String getClientUniqueID (@Nonnull final HttpServletRequest aHttpRequest) {
		 if (subject == null || serial == null){
			 String clientCertHeader = aHttpRequest
						.getHeader(CLIENT_CERT_HEADER_KEY); 
			 String clientCertHeaderDecoded;
			try {
				clientCertHeaderDecoded = URLDecoder.decode(clientCertHeader,"UTF-8");
				parseClientCertHeader(clientCertHeaderDecoded);
			} catch (UnsupportedEncodingException e) {
				throw new IllegalStateException("Unable to decode client Cert Header");
			}
		 }
		 String strippedSerial= serial.replaceAll(":", "");
		 // in the sml database, the subject is stored in a different way than the one in the
		 //client cert header and witout spaces we thus need to rebuild it
		 String[] splittedSubject = subject.split(",");
		 if (splittedSubject.length != 3) {
			 throw new IllegalStateException(
					 "Invalid BlueCoat Client Certificate Header Received ");
		 }
		 String reOrderedSubject = splittedSubject[1].trim().concat(",")
				 .concat(splittedSubject[2].trim()).concat(",").concat(splittedSubject[0].trim());
		
		 return reOrderedSubject+":"+strippedSerial;
	 }

}
