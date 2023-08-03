package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.SearchUserRO;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


/**
 *
 */
@Component
public class DBUserToSearchUserROConverter implements Converter<DBUser, SearchUserRO> {

    @Override
    public SearchUserRO convert(DBUser source) {
        SearchUserRO target = new SearchUserRO();
        target.setUsername(source.getUsername());
        target.setFullName(source.getFullName());
        target.setUserId(SessionSecurityUtils.encryptedEntityId(source.getId()));
        return target;
    }
}
