package eu.europa.ec.edelivery.smp.utils;

import eu.europa.ec.edelivery.smp.auth.SMPAuthenticationToken;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class SessionSecurityUtils {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SessionSecurityUtils.class);

    public static String encryptedEntityId(Long id) {
        if (id == null) {
            return null;
        }
        SecurityUtils.Secret secret = getAuthenticationSecret();
        String idValue = id.toString();
        return secret != null ? SecurityUtils.encryptURLSafe(secret, idValue) : idValue;
    }


    public static Long decryptEntityId(String id) {
        if (id == null) {
            return null;
        }
        SecurityUtils.Secret secret = getAuthenticationSecret();
        String value = secret != null ? SecurityUtils.decryptUrlSafe(secret, id) : id;
        return new Long(value);
    }

    public static SecurityUtils.Secret getAuthenticationSecret() {
        if (SecurityContextHolder.getContext() == null) {
            LOG.warn("No Security context!");
            return null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            LOG.warn("No active Authentication!");
            return null;
        }
        if (!(authentication instanceof SMPAuthenticationToken)) {
            LOG.warn("Authentication is not class type: SMPAuthenticationToken!");
            return null;
        }
        return ((SMPAuthenticationToken) authentication).getSecret();
    }

    public static String getAuthenticationName() {
        if (SecurityContextHolder.getContext() == null) {
            LOG.debug("No Security context!");
            return null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            LOG.debug("No active Authentication!");
            return null;
        }
        return authentication.getName();

    }
}
