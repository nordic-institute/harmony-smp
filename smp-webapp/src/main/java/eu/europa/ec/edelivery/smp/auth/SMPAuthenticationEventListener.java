package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.config.PropertiesConfig;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.util.Collection;

/**
 * The class implements ApplicationListener listener for AuthenticationSuccessEvent. Purpose of the class is to setup
 * the time, in seconds, between client requests before the SMP will invalidate session for admin role (ROLE_SYSTEM_ADMIN)
 * and for user roles (ROLE_SMP_ADMIN, ROLE_SERVICE_GROUP_ADMIN)
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */

@Component
public class SMPAuthenticationEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPAuthenticationEventListener.class);

    private ConfigurationService configurationService;

    @Autowired
    public SMPAuthenticationEventListener(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * On successful authentication method validates the roles and set max session idle time before it invalidates the session.
     * @param event
     */
    @Override
    public void onApplicationEvent (AuthenticationSuccessEvent event) {
        Collection<? extends GrantedAuthority> authorities = event.getAuthentication().getAuthorities();
        boolean hasAdminRole = authorities.stream().anyMatch(grantedAuthority -> StringUtils.equalsIgnoreCase(grantedAuthority.getAuthority(), SMPAuthority.S_AUTHORITY_SYSTEM_ADMIN.getAuthority()));
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession();
        int idleTimeout =(hasAdminRole ? configurationService.getSessionIdleTimeoutForAdmin():configurationService.getSessionIdleTimeoutForUser());
        LOG.debug("Set session idle timeout [{}] for user [{}]", idleTimeout,event.getAuthentication().getName());
        session.setMaxInactiveInterval(idleTimeout);
    }
}