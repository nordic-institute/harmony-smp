package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.UserRO;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author Sebastian-Ion TINCU
 */
@Component
public class UserROToDBUserConverter implements Converter<UserRO, DBUser> {

    @Override
    public DBUser convert(UserRO source) {
        DBUser target = new DBUser();
        target.setUsername(source.getUsername());
        target.setActive(source.isActive());
        target.setId(SessionSecurityUtils.decryptEntityId(source.getUserId()));
        target.setApplicationRole(source.getRole());

        target.setEmailAddress(source.getEmailAddress());
        target.setFullName(source.getFullName());
        target.setSmpTheme(source.getSmpTheme());
        target.setSmpLocale(source.getSmpLocale());
        return target;
    }


}
