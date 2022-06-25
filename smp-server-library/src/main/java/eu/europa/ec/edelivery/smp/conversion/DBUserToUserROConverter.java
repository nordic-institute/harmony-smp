package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;


/**
 * @author Sebastian-Ion TINCU
 */
@Component
public class DBUserToUserROConverter implements Converter<DBUser, UserRO> {

    private ConfigurationService configurationService;
    private ConversionService conversionService;

    public DBUserToUserROConverter(ConfigurationService configurationService, ConversionService conversionService) {
        this.configurationService = configurationService;
        this.conversionService = conversionService;
    }

    @Override
    public UserRO convert(DBUser source) {

        UserRO target = new UserRO();
        target.setEmailAddress(source.getEmailAddress());
        target.setUsername(source.getUsername());
        target.setRole(source.getRole());
        target.setPassword(source.getPassword());
        target.setAccessTokenId(source.getAccessTokenIdentifier());
        target.setPasswordExpireOn(source.getPasswordExpireOn());
        target.setAccessTokenExpireOn(source.getAccessTokenExpireOn());
        target.setPasswordExpired(isPasswordExpired(source));

        target.setSequentialLoginFailureCount(source.getSequentialLoginFailureCount());
        target.setLastFailedLoginAttempt(source.getLastFailedLoginAttempt());
        target.setSuspendedUtil(getSuspensionUntilDate(source.getLastFailedLoginAttempt(),source.getSequentialLoginFailureCount(),
                configurationService.getLoginSuspensionTimeInSeconds(), configurationService.getLoginMaxAttempts()));
        target.setSequentialTokenLoginFailureCount(source.getSequentialTokenLoginFailureCount());
        target.setLastTokenFailedLoginAttempt(source.getLastTokenFailedLoginAttempt());
        target.setTokenSuspendedUtil(getSuspensionUntilDate(source.getLastTokenFailedLoginAttempt(),
                source.getSequentialTokenLoginFailureCount(),
                configurationService.getAccessTokenLoginFailDelayInMilliSeconds(),
                configurationService.getAccessTokenLoginMaxAttempts()));

        target.setActive(source.isActive());
        // do not expose internal id
        target.setUserId(SessionSecurityUtils.encryptedEntityId(source.getId()));
        if (source.getCertificate() != null) {
            CertificateRO certificateRO = conversionService.convert(source.getCertificate(), CertificateRO.class);
            target.setCertificate(certificateRO);
            if (StringUtils.equalsIgnoreCase(source.getCertificate().getCertificateId(), source.getUsername())) {
                // clear username if is the same as certificate id.
                // username as cert id is set to database to force unique users
                // and to fix issue with mysql - where null value is also unique...
                target.setUsername(null);
            }
        }
        return target;
    }

    public OffsetDateTime getSuspensionUntilDate(OffsetDateTime lastAttempt, Integer currentCount, Integer suspendedForSec, Integer suspendedFromCount){
        if (lastAttempt ==null || currentCount ==null || suspendedForSec ==null || suspendedFromCount ==null){
            return null;
        }
        if (currentCount < suspendedFromCount){
            return null;
        }
        OffsetDateTime suspendedUtil = lastAttempt.plusSeconds(suspendedForSec);
        if (suspendedUtil.isBefore(OffsetDateTime.now())){
            return null;
        }
        return suspendedUtil;
    }

    private boolean isPasswordExpired(DBUser source) {
        return StringUtils.isNotEmpty(source.getPassword())
                && (source.getPasswordExpireOn() == null
                || OffsetDateTime.now().isAfter(source.getPasswordExpireOn()));
    }
}
