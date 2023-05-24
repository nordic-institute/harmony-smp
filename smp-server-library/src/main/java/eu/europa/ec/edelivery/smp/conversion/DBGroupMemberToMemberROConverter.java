package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.model.user.DBGroupMember;
import eu.europa.ec.edelivery.smp.data.ui.MemberRO;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


/**
 *
 */
@Component
public class DBGroupMemberToMemberROConverter implements Converter<DBGroupMember, MemberRO> {

    @Override
    public MemberRO convert(DBGroupMember source) {
        MemberRO target = new MemberRO();
        target.setMemberOf("GROUP");
        target.setUsername(source.getUser().getUsername());
        target.setFullName(source.getUser().getFullName());
        target.setRoleType(source.getRole());
        target.setMemberId(SessionSecurityUtils.encryptedEntityId(source.getId()));

        return target;
    }
}
