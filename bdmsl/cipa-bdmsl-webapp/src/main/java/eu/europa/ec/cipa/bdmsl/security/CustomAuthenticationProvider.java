package eu.europa.ec.cipa.bdmsl.security;

import eu.europa.ec.cipa.bdmsl.service.IBlueCoatCertificateService;
import eu.europa.ec.cipa.bdmsl.service.IX509CertificateService;
import eu.europa.ec.cipa.common.exception.BusinessException;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import eu.europa.ec.cipa.common.logging.ILoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.security.cert.X509Certificate;

/**
 * Created by feriaad on 17/06/2015.
 */
@Component(value = "customAuthenticationProvider")
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private ILoggingService loggingService;

    @Autowired
    private IBlueCoatCertificateService blueCoatCertificateService;

    @Autowired
    private IX509CertificateService x509CertificateService;

    @Value("${unsecureLoginAllowed}")
    private String unsecureLoginAllowed;

    public static final String SMP_ROLE = "ROLE_SMP";

    public static final String PYP_ROLE = "ROLE_PYP";

    public static final String ADMIN_ROLE = "ROLE_ADMIN";

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            if (authentication instanceof X509CertificateAuthentication) {
                loggingService.debug("Authenticating using the X509 certificate from the request");
                authentication.setAuthenticated(x509CertificateService.isClientX509CertificateValid((X509Certificate[]) authentication.getCredentials()));
            } else if (authentication instanceof BlueCoatClientCertificateAuthentication) {
                loggingService.debug("Authenticating using the decoded certificate in the http header");
                authentication.setAuthenticated(blueCoatCertificateService.isBlueCoatClientCertificateValid((CertificateDetails) authentication.getCredentials()));
            } else if (authentication instanceof UnsecureAuthentication) {
                loggingService.debug("Authenticating without any security. Only allowed if 'unsecureLoginAllowed' is set to 'true'. 'unsecureLoginAllowed' is set to " + unsecureLoginAllowed);
                authentication.setAuthenticated(Boolean.parseBoolean(unsecureLoginAllowed));
            }
        } catch (final TechnicalException | BusinessException exception)  {
            throw new AuthenticationServiceException("Couldn't authenticate the principal " + authentication.getPrincipal(), exception);
        }
        return authentication;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return X509CertificateAuthentication.class.equals(clazz) || UnsecureAuthentication.class.equals(clazz) || BlueCoatClientCertificateAuthentication.class.equals(clazz);
    }


}
