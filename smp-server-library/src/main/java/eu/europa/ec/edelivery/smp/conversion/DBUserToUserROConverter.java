package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.model.user.DBCredential;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;


/**
 * @author Sebastian-Ion TINCU
 */
@Component
public class DBUserToUserROConverter implements Converter<DBUser, UserRO> {


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
