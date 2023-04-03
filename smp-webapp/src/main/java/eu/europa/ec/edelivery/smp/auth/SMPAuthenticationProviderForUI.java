package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.CredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * Authentication provider for the UI authentication.
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
@Component
public class SMPAuthenticationProviderForUI implements AuthenticationProvider {
    protected static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPAuthenticationProviderForUI.class);

    final CredentialService credentialService;

    @Autowired
    public SMPAuthenticationProviderForUI(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    @Override
    public Authentication authenticate(Authentication authenticationToken)
            throws AuthenticationException {

        Authentication authentication = null;
        // PreAuthentication token for the rest service certificate authentication
        LOG.debug("Authenticate authentication token type: [{}]", authenticationToken.getClass());
        if (authenticationToken instanceof UILoginAuthenticationToken) {
            authentication = authenticateByUsernamePassword((UILoginAuthenticationToken) authenticationToken);
        }
        return authentication;
    }

    public Authentication authenticateByUsernamePassword(UILoginAuthenticationToken auth)
            throws AuthenticationException {
        return credentialService.authenticateByUsernamePassword(auth.getName(), auth.getCredentials().toString());
    }


    @Override
    public boolean supports(Class<?> auth) {
        LOG.info("Support authentication: [{}].", auth);
        boolean supportAuthentication = auth.equals(UILoginAuthenticationToken.class);
        if (!supportAuthentication) {
            LOG.warn("SMP does not support authentication type: [{}].", auth);
        }
        return supportAuthentication;
    }
}
