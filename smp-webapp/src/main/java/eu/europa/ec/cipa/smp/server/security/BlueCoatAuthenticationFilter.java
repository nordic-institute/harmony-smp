package eu.europa.ec.cipa.smp.server.security;

import eu.europa.ec.cipa.smp.server.errors.exceptions.AuthenticationException;
import eu.europa.ec.cipa.smp.server.util.CertificateUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gutowpa on 30/03/2017.
 */
public class BlueCoatSubjectAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

    private static String[] HEADER_ATTR_SUBJECT = {"subject"};
    private static String[] HEADER_ATTR_SERIAL = {"serial", "sno"};
    private static String[] HEADER_ATTR_VALID_FROM = {"validFrom"};
    private static String[] HEADER_ATTR_VALID_TO = {"validTo"};
    private static String[] HEADER_ATTR_ISSUER = {"issuer"};

    private boolean blueCoatEnabled = false;

    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {

        String certHeader = request.getHeader("Client-Cert");
        if( ! blueCoatEnabled || certHeader == null){
            return null;
        }

        certHeader = decodeHeader(certHeader);

        String subjectDN = getField(certHeader, HEADER_ATTR_SUBJECT);
        String issuerDN = getField(certHeader, HEADER_ATTR_ISSUER);
        String serial = getField(certHeader, HEADER_ATTR_SERIAL);

        PreAuthenticatedCertificatePrincipal principal = new PreAuthenticatedCertificatePrincipal(subjectDN, issuerDN, serial);

        return principal;
    }

    private String decodeHeader(String certHeader) {
        try {
            certHeader = URLDecoder.decode(certHeader, StandardCharsets.UTF_8.name());
            certHeader = StringEscapeUtils.unescapeHtml(certHeader);
            return certHeader;
        } catch (Exception e) {
            throw new BadCredentialsException("Could not decode BlueCoat authentication header: "+certHeader, e);
        }
    }

    /**
     * Not used, see {@code {@link RequestHeaderAuthenticationFilter}}
     * @param request
     * @return
     */
    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }

    private String normalizeBlueCoatPrincipal(String principal) {
        try {
            return CertificateUtils.getCommonNameFromCalculateHeaderCertificateId(principal).getCertificateId();
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return null;
        }
    }


    private String getField(String header, String [] fieldAlternativeNames){
        for(String fieldName : fieldAlternativeNames){
            // Header consists of "key=value" map separated by "&"
            // Pattern matches value preceded by "key=" and followed by separator "&" or end-of-line "$"
            Pattern fieldValuePattern = Pattern.compile(fieldName+"=(.*)&|$");
            Matcher matcher = fieldValuePattern.matcher(header);
            if(matcher.matches()){
                return clearGarbage(matcher.group(0));
            }
        }
        throw new BadCredentialsException("BlueCoat authentication header does not contain mandatory field: " + fieldAlternativeNames.toString());
    }

    private String clearGarbage(String dirtyValue) {
        return dirtyValue;
    }


    public void setBlueCoatEnabled(boolean isEnabled){
        this.blueCoatEnabled = isEnabled;
    }
}
