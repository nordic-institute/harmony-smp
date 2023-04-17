package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.model.user.DBResourceMember;
import eu.europa.ec.edelivery.smp.data.ui.MemberRO;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


/**
 *
 */
@Component
public class DBResourceMemberToMemberROConverter implements Converter<DBResourceMember, MemberRO> {

    @Override
    public MemberRO convert(DBResourceMember source) {
        MemberRO target = new MemberRO();
        target.setMemberOf("RESOURCE");
        target.setUsername(source.getUser().getUsername());
        target.setFullName(source.getUser().getFullName());
        target.setRoleType(source.getRole());
        target.setMemberId(SessionSecurityUtils.encryptedEntityId(source.getId()));
        return target;
    }
}
