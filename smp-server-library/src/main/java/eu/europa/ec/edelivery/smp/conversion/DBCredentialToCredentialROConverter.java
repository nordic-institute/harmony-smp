package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.model.user.DBCredential;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.data.ui.CredentialRO;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;


/**
 * @author Sebastian-Ion TINCU
 */
@Component
public class DBCredentialToCredentialROConverter implements Converter<DBCredential, CredentialRO>{

    private final ConfigurationService configurationService;
    public DBCredentialToCredentialROConverter(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public CredentialRO convert(DBCredential source) {

        CredentialRO target = new CredentialRO();
        target.setCredentialId(SessionSecurityUtils.encryptedEntityId(source.getId()));
        target.setName(source.getName());
        target.setActive(source.isActive());
        target.setDescription(source.getDescription());
        target.setSequentialLoginFailureCount(source.getSequentialLoginFailureCount());
        target.setLastFailedLoginAttempt(source.getLastFailedLoginAttempt());
        target.setActiveFrom(source.getActiveFrom());
        target.setExpireOn(source.getExpireOn());
        target.setUpdatedOn(source.getChangedOn());
        target.setSequentialLoginFailureCount(source.getSequentialLoginFailureCount());
        target.setSuspendedUtil(getSuspensionUtilDate(source));
        target.setExpired(isCredentialExpired(source));
        return target;
    }

    public OffsetDateTime getSuspensionUtilDate(DBCredential credential) {
        Integer suspensionTime = null;
        Integer maxAllowedAttempts = null;
        switch (credential.getCredentialType()) {
            case USERNAME_PASSWORD:
                suspensionTime = configurationService.getLoginSuspensionTimeInSeconds();
                maxAllowedAttempts = configurationService.getLoginMaxAttempts();
                break;
            case ACCESS_TOKEN:
                suspensionTime = configurationService.getAccessTokenLoginSuspensionTimeInSeconds();
                maxAllowedAttempts = configurationService.getAccessTokenLoginMaxAttempts();
                break;
        }

        return getSuspensionUntilDate(credential.getLastFailedLoginAttempt(),
                credential.getSequentialLoginFailureCount(),
                suspensionTime,
                maxAllowedAttempts);
    }


    public OffsetDateTime getSuspensionUntilDate(OffsetDateTime lastAttempt, Integer currentCount,
                                                 Integer suspendedForSec, Integer suspendedFromCount) {
        if (lastAttempt == null || currentCount == null || suspendedForSec == null || suspendedFromCount == null) {
            return null;
        }
        if (currentCount < suspendedFromCount) {
            return null;
        }
        OffsetDateTime suspendedUtil = lastAttempt.plusSeconds(suspendedForSec);
        if (suspendedUtil.isBefore(OffsetDateTime.now())) {
            return null;
        }
        return suspendedUtil;
    }

    private boolean isCredentialExpired(DBCredential source) {
        return (source.getExpireOn() == null
                || OffsetDateTime.now().isAfter(source.getExpireOn()));
    }
}
