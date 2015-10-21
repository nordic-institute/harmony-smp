package eu.europa.ec.cipa.bdmsl.security;

import eu.europa.ec.cipa.bdmsl.common.exception.CertificateAuthenticationException;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Blue Coat is the name of the reverse proxy at the commission. It forwards the request in HTTP with the certificate details inside the request. This class extracts the data from the header.
 * Created by feriaad on 17/06/2015.
 */
public class BlueCoatClientCertificateAuthentication implements Authentication {

    private boolean authenticated;
    private String certificateId;
    private CertificateDetails certificate;
    private Collection<GrantedAuthority> authorityList;

    public BlueCoatClientCertificateAuthentication(final String certHeaderValue) throws TechnicalException {
        certificate = new CertificateDetails();
        this.certificateId = calculateCertificateId(certHeaderValue);
        this.authorityList = Collections.unmodifiableList(retrieveAuthorities(certificateId));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorityList;
    }

    @Override
    public Object getCredentials() {
        return certificate;
    }

    @Override
    public Object getDetails() {
        return certificate;
    }

    @Override
    public Object getPrincipal() {
        return certificateId;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    @Override
    public String getName() {
        return certificateId;
    }

    private String calculateCertificateId(final String certHeaderValue) throws CertificateAuthenticationException {
        try {
            String clientCertHeaderDecoded = URLDecoder.decode(certHeaderValue, "UTF-8");
            parseClientCertHeader(clientCertHeaderDecoded);
            certificate.setSerial(certificate.getSerial().replaceAll(":", ""));
            // in the sml database, the subject is stored in a different way than the one in the
            //client cert header and witout spaces we thus need to rebuild it
            String[] split = clientCertHeaderDecoded.split("&");
            String subject = split[1].substring(split[1].indexOf('=') + 1);

            LdapName ldapName;
            try {
                ldapName = new LdapName(subject);
            } catch (InvalidNameException exc) {
                throw new CertificateAuthenticationException("Impossible to identify authorities for certificate " + subject, exc);
            }
            // Make a map from type to name
            final Map<String, Rdn> parts = new HashMap<>();
            for (final Rdn rdn : ldapName.getRdns()) {
                parts.put(rdn.getType(), rdn);
            }

            // Re-order - least important item comes first (=reverse order)!
            List<Rdn> list = new ArrayList<>();
            list.add(parts.get("C"));
            list.add(parts.get("O"));
            list.add(parts.get("CN"));
            final String subjectName = new LdapName(list).toString();

            // subject-name + ":" + serial number hexstring
            String serialNumber = StringUtils.leftPad(certificate.getSerial(), 16, "0");
            certificate.setSubject(subjectName);
            return subjectName + ':' + serialNumber;
        } catch (final Exception exc) {
            throw new CertificateAuthenticationException("Impossible to determine the certificate identifier from " + certHeaderValue, exc);
        }
    }

    private List<? extends GrantedAuthority> retrieveAuthorities(String certHeaderValue) throws CertificateAuthenticationException {
        String commonName = null;
        Pattern pattern = Pattern.compile("CN=([^,]*),");
        Matcher matcher = pattern.matcher(certHeaderValue);

        while(matcher.find()) {
            commonName = matcher.group(1);
        }

        List<GrantedAuthority> roles = new ArrayList<>();

        if (commonName.startsWith("SMP_")) {
            // SMP certificate --> SMP_ROLE
            roles.add(new SimpleGrantedAuthority(CustomAuthenticationProvider.SMP_ROLE));
        } else if (commonName.startsWith("PYP_")) {
            // PYP certificate --> PYP_ROLE
            roles.add(new SimpleGrantedAuthority(CustomAuthenticationProvider.PYP_ROLE));
        } else {
            roles = AuthorityUtils.NO_AUTHORITIES;
        }
        return roles;
    }


    private void parseClientCertHeader(String clientCertHeaderDecoded) throws CertificateAuthenticationException {
        String[] split = clientCertHeaderDecoded.split("&");

        if (split.length != 5) {
            throw new CertificateAuthenticationException(
                    "Invalid BlueCoat Client Certificate Header Received ");
        }
        certificate.setSerial(split[0].substring(split[0].indexOf('=') + 1));

        DateFormat df = new SimpleDateFormat("MMM d hh:mm:ss yyyy zzz", Locale.US);

        try {
            certificate.setValidFrom(DateUtils.toCalendar(df.parse(split[2].substring(split[2].indexOf('=') + 1))));
            certificate.setValidTo(DateUtils.toCalendar(df.parse(split[3].substring(split[3].indexOf('=') + 1))));
        } catch (ParseException e) {
            throw new CertificateAuthenticationException(
                    "Invalid BlueCoat Client Certificate Header Received (Unparsable Date) ");
        }
        certificate.setIssuer(split[4].substring(split[4].indexOf('=') + 1));
        certificate.setRootCertificateDN(certificate.getIssuer());
    }


}
