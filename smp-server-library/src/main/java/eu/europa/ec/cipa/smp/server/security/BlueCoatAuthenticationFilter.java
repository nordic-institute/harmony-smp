package eu.europa.ec.cipa.smp.server.security;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Locale.US;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

/**
 * Blue Coat is a reverse-proxy used in European Commission's network.
 * If sender provides valid and trusted SSL certificate, then Blue Coat
 * adds "Client-Cert" HTTP header to each positively 2-way-SSL authenticated request.
 * <p>
 * This {@code {@link BlueCoatAuthenticationFilter}} is a pre-authenticated filter which obtains the user's certificate from a request header.
 * <p>
 * As with most pre-authenticated scenarios, it is essential that the external
 * authentication system is set up correctly as this filter does no authentication
 * whatsoever. All the protection is assumed to be provided externally and if this filter
 * is included inappropriately in a configuration, it would be possible to assume the
 * identity of a user merely by setting the header value.
 * <p>
 * If the header is missing from the request, {@code getPreAuthenticatedPrincipal} returns null value,
 * so other authentication mechanisms could still authenticate request.
 * <p>
 * By default {@code {@link BlueCoatAuthenticationFilter}} is turned off, which means "Client-Cert" header is skipped.
 * It can be turend on by setting {@code setBlueCoatEnabled()} to true.
 * <p>
 * <b>IMPIRTANT:</b> Never turn this filter ON in environments that do not stay behind well configured reverse-proxy.
 * In such case attacker can provide whatever header value and authenticate as any chosen user.
 *
 * @author Pawel Gutowski
 */
public class BlueCoatAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter implements AuthenticationDetailsSource<HttpServletRequest, PreAuthenticatedCertificatePrincipal> {

    private static final String[] HEADER_ATTR_SUBJECT = {"subject"};
    private static final String[] HEADER_ATTR_SERIAL = {"serial", "sno"};
    private static final String[] HEADER_ATTR_VALID_FROM = {"validFrom"};
    private static final String[] HEADER_ATTR_VALID_TO = {"validTo"};
    private static final String[] HEADER_ATTR_ISSUER = {"issuer"};

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", US);

    private boolean blueCoatEnabled = false;

    public BlueCoatAuthenticationFilter() {
        super.setAuthenticationDetailsSource(this);
    }

    @Override
    protected PreAuthenticatedCertificatePrincipal getPreAuthenticatedPrincipal(HttpServletRequest request) {

        String certHeader = request.getHeader("Client-Cert");
        if (!blueCoatEnabled || certHeader == null) {
            return null;
        }
        logger.debug("Initializing BlueCoat authentication with header: " + certHeader);

        PreAuthenticatedCertificatePrincipal principal = buildPrincipal(certHeader);

        return principal;
    }

    /**
     * Not used, see implementation of {@code {@link RequestHeaderAuthenticationFilter}}
     */
    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }

    @Override
    public PreAuthenticatedCertificatePrincipal buildDetails(HttpServletRequest request) {
        PreAuthenticatedCertificatePrincipal principal = getPreAuthenticatedPrincipal(request);
        logger.info("Successfully extracted user details from 'Client-Cert' header: " + principal);
        return principal;
    }

    private PreAuthenticatedCertificatePrincipal buildPrincipal(String certHeader) {
        try {
            certHeader = decodeHeader(certHeader);
            validateCertDates(certHeader);

            String subjectDN = getField(certHeader, HEADER_ATTR_SUBJECT);
            String issuerDN = getField(certHeader, HEADER_ATTR_ISSUER);
            String serial = getField(certHeader, HEADER_ATTR_SERIAL);

            return new PreAuthenticatedCertificatePrincipal(subjectDN, issuerDN, serial);

        } catch (Exception e) {
            logger.error("Malformed BlueCoat 'Client-Cert' authentication header. This might be a bug or environment configuration: " + certHeader, e);
            return null;
        }
    }

    private String decodeHeader(String certHeader) throws UnsupportedEncodingException {
        certHeader = URLDecoder.decode(certHeader, StandardCharsets.UTF_8.name());
        certHeader = StringEscapeUtils.unescapeHtml(certHeader);
        return certHeader;
    }

    private String getField(String header, String[] fieldAlternativeNames) {
        for (String fieldName : fieldAlternativeNames) {
            // Header consists of "key=value" map separated by "&"
            // Pattern matches value preceded by "key=" and followed by separator "&" or end-of-line "$"
            Pattern fieldValuePattern = Pattern.compile(".*" + fieldName + "=(?<value>[^&]+).*$", CASE_INSENSITIVE);
            Matcher matcher = fieldValuePattern.matcher(header);
            if (matcher.matches()) {
                String fieldValue = matcher.group("value");
                return clearGarbage(fieldValue);
            }
        }
        throw new BadCredentialsException("BlueCoat authentication header does not contain mandatory field: " + Arrays.toString(fieldAlternativeNames) + ": " + header);
    }

    private String clearGarbage(String dirtyValue) {
        // BlueCoat introduces garbage by mixing fields
        // One that we noticed is the "E=a@b.pl" value added at the and of CN: "CN=common name/emailAddress\=a@b.pl"
        String cleanValue = dirtyValue.replaceAll("/emailAddress\\\\=[^,]+", "");
        return cleanValue;
    }


    private void validateCertDates(String header) {
        Date validFrom = getDateField(header, HEADER_ATTR_VALID_FROM);
        Date validTo = getDateField(header, HEADER_ATTR_VALID_TO);

        if (validFrom.after(new Date())) {
            throw new BadCredentialsException("Certificate is not yet valid, dateFrom= " + DATE_FORMAT.format(validFrom));
        }
        if (validTo.before(new Date())) {
            throw new BadCredentialsException("Certificate has expired, dateTo= " + DATE_FORMAT.format(validTo));
        }
    }

    private Date getDateField(String header, String[] fieldName) {
        String dateStr = getField(header, fieldName);
        try {
            return DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            throw new BadCredentialsException("Invalid date format provided in BlueCoat header: " + Arrays.toString(fieldName) + "=" + dateStr);
        }
    }

    public void setBlueCoatEnabled(boolean isEnabled) {
        this.blueCoatEnabled = isEnabled;
    }

}
