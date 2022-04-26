package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.config.SMPSecurityConstants;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SMPAuthenticationService {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPAuthenticationService.class);

    @Autowired
    @Qualifier(SMPSecurityConstants.SMP_UI_AUTHENTICATION_MANAGER_BEAN)
    private AuthenticationManager authenticationManager;

    @Transactional(noRollbackFor = AuthenticationException.class)
    public Authentication authenticate(String username, String password) {
        LOG.debug("Authenticate: [{}]", username);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        SMPAuthenticationToken authentication = (SMPAuthenticationToken) authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }
}