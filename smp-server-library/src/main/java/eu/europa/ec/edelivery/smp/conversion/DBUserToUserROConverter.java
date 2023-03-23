package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.enums.CredentialType;
import eu.europa.ec.edelivery.smp.data.model.user.DBCertificate;
import eu.europa.ec.edelivery.smp.data.model.user.DBCredential;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;


/**
 * @author Sebastian-Ion TINCU
 */
@Component
public class DBUserToUserROConverter implements Converter<DBUser, UserRO> {

    private ConfigurationService configurationService;
    private ConversionService conversionService;

    public DBUserToUserROConverter(ConfigurationService configurationService, @Lazy ConversionService conversionService) {
        this.configurationService = configurationService;
        this.conversionService = conversionService;
    }

    @Override
    public UserRO convert(DBUser source) {

        UserRO target = new UserRO();
        target.setEmailAddress(source.getEmailAddress());
        target.setUsername(source.getUsername());
        target.setActive(source.isActive());
        // do not expose internal id
        target.setUserId(SessionSecurityUtils.encryptedEntityId(source.getId()));
/*
        Optional<DBCredential> optUserPassCred = source.getCredentials().stream().filter(credential -> credential.getCredentialType() == CredentialType.USERNAME_PASSWORD).findFirst();
        Optional<DBCredential> optTokenCred = source.getCredentials().stream().filter(credential -> credential.getCredentialType() == CredentialType.ACCESS_TOKEN).findFirst();
        Optional<DBCredential> optCertCred = source.getCredentials().stream().filter(credential -> credential.getCredentialType() == CredentialType.CERTIFICATE).findFirst();

        if (optUserPassCred.isPresent()){
            DBCredential credential = optUserPassCred.get();
            target.setPassword(credential.getValue());
            target.setPasswordExpireOn(credential.getExpireOn());
            target.setPasswordExpired(isCredentialExpired(credential));
            target.setSequentialLoginFailureCount(credential.getSequentialLoginFailureCount());
            target.setLastFailedLoginAttempt(credential.getLastFailedLoginAttempt());
            target.setSuspendedUtil(getSuspensionUntilDate(credential.getLastFailedLoginAttempt(),credential.getSequentialLoginFailureCount(),
                    configurationService.getLoginSuspensionTimeInSeconds(), configurationService.getLoginMaxAttempts()));
        }

        if (optTokenCred.isPresent()){
            DBCredential credential = optUserPassCred.get();

            target.setAccessTokenId(credential.getName());
            target.setAccessTokenExpireOn(credential.getExpireOn());

            target.setSequentialTokenLoginFailureCount(credential.getSequentialLoginFailureCount());
            target.setLastTokenFailedLoginAttempt(credential.getLastFailedLoginAttempt());
            target.setTokenSuspendedUtil(getSuspensionUntilDate(credential.getLastFailedLoginAttempt(),
                    credential.getSequentialLoginFailureCount(),
                    configurationService.getAccessTokenLoginSuspensionTimeInSeconds(),
                    configurationService.getAccessTokenLoginMaxAttempts()));
        }

        if (optCertCred.isPresent()) {
            DBCredential credential = optCertCred.get();
            DBCertificate certificate = credential.getCertificate();
            CertificateRO certificateRO = conversionService.convert(certificate, CertificateRO.class);
            target.setCertificate(certificateRO);
            if (StringUtils.equalsIgnoreCase(certificate.getCertificateId(), source.getUsername())) {
                // clear username if is the same as certificate id.
                // username as cert id is set to database to force unique users
                // and to fix issue with mysql - where null value is also unique...
                target.setUsername(null);
            }
        }
*/

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

    private boolean isCredentialExpired(DBCredential source) {
        return  (source.getExpireOn() == null
                || OffsetDateTime.now().isAfter(source.getExpireOn()));
    }
}
