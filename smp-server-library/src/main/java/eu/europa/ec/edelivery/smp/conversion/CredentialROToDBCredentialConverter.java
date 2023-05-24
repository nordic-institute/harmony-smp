package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.model.user.DBCredential;
import eu.europa.ec.edelivery.smp.data.ui.CredentialRO;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


/**
 * @author Sebastian-Ion TINCU
 */
@Component
public class CredentialROToDBCredentialConverter implements Converter<CredentialRO, DBCredential> {

    @Override
    public DBCredential convert(CredentialRO source) {

        DBCredential target = new DBCredential();
        if (StringUtils.isNotBlank(source.getCredentialId())) {
            target.setId(SessionSecurityUtils.decryptEntityId(source.getCredentialId()));
        }
        target.setName(source.getName());
        target.setActive(source.isActive());
        target.setDescription(source.getDescription());
        target.setSequentialLoginFailureCount(source.getSequentialLoginFailureCount());
        target.setLastFailedLoginAttempt(source.getLastFailedLoginAttempt());
        target.setActiveFrom(source.getActiveFrom());
        target.setExpireOn(source.getExpireOn());
        target.setChangedOn(source.getUpdatedOn());
        target.setSequentialLoginFailureCount(source.getSequentialLoginFailureCount());
        return target;
    }

}
