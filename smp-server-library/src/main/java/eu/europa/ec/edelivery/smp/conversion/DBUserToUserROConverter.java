package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.dao.CredentialDao;
import eu.europa.ec.edelivery.smp.data.enums.CredentialTargetType;
import eu.europa.ec.edelivery.smp.data.enums.CredentialType;
import eu.europa.ec.edelivery.smp.data.model.user.DBCredential;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;


/**
 * @author Sebastian-Ion TINCU
 */
@Component
public class DBUserToUserROConverter implements Converter<DBUser, UserRO> {

    private final CredentialDao credentialDao;
    private final ConfigurationService configurationService;

    public DBUserToUserROConverter(CredentialDao credentialDao, ConfigurationService configurationService) {
        this.credentialDao = credentialDao;
        this.configurationService = configurationService;
    }

    @Override
    public UserRO convert(DBUser source) {

        UserRO target = new UserRO();
        target.setEmailAddress(source.getEmailAddress());
        target.setUsername(source.getUsername());
        target.setActive(source.isActive());
        // do not expose internal id
        target.setUserId(SessionSecurityUtils.encryptedEntityId(source.getId()));
        target.setRole(source.getApplicationRole());
        target.setEmailAddress(source.getEmailAddress());
        target.setFullName(source.getFullName());
        target.setSmpTheme(source.getSmpTheme());
        target.setSmpLocale(source.getSmpLocale());

        List<DBCredential> credentials = credentialDao.findUserCredentialForByUserIdTypeAndTarget(source.getId(), CredentialType.USERNAME_PASSWORD, CredentialTargetType.UI);
        if (!credentials.isEmpty()) {
            // expected only one username/password
            DBCredential credential = credentials.get(0);
            target.setPasswordExpired(credential.getExpireOn() == null || OffsetDateTime.now().isAfter(credential.getExpireOn()));


            target.setPasswordUpdatedOn(credential.getChangedOn());
            target.setPasswordExpireOn(credential.getExpireOn());
            target.setPasswordExpired(isCredentialExpired(credential));
            target.setSequentialLoginFailureCount(credential.getSequentialLoginFailureCount());
            target.setLastFailedLoginAttempt(credential.getLastFailedLoginAttempt());
            target.setSuspendedUtil(getSuspensionUntilDate(credential.getLastFailedLoginAttempt(), credential.getSequentialLoginFailureCount(),
                    configurationService.getLoginSuspensionTimeInSeconds(), configurationService.getLoginMaxAttempts()));
        }
        return target;
    }

    public OffsetDateTime getSuspensionUntilDate(OffsetDateTime lastAttempt, Integer currentCount, Integer suspendedForSec, Integer suspendedFromCount) {
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
