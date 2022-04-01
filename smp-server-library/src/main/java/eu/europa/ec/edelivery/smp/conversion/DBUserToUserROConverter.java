package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.model.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.CertificateRO;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/**
 * @author Sebastian-Ion TINCU
 */
@Component
public class DBUserToUserROConverter implements Converter<DBUser, UserRO> {

    @Autowired
    private ConversionService conversionService;

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

        target.setActive(source.isActive());
        // do not expose internal id
        target.setUserId(SessionSecurityUtils.encryptedEntityId(source.getId()));
        if (source.getCertificate() != null) {
            CertificateRO certificateRO = conversionService.convert(source.getCertificate(), CertificateRO.class);
            target.setCertificate(certificateRO);
            if(StringUtils.equalsIgnoreCase(source.getCertificate().getCertificateId(), source.getUsername())) {
                // clear username if is the same as certificate id.
                // username as cert id is set to database to force unique users
                // and to fix issue with mysql - where null value is also unique...
                target.setUsername(null);
            }
        }
        return target;
    }


    private boolean isPasswordExpired(DBUser source) {
        return StringUtils.isNotEmpty(source.getPassword())
                && (isPasswordRecentlyReset(source) || isPasswordChangedLongerThanThreeMonthsAgo(source));
    }

    private boolean isPasswordRecentlyReset(DBUser source) {
        return source.getPasswordChanged() == null;
    }

    private boolean isPasswordChangedLongerThanThreeMonthsAgo(DBUser source) {
        return LocalDateTime.now().minusMonths(3).isAfter(source.getPasswordChanged());
    }
}
