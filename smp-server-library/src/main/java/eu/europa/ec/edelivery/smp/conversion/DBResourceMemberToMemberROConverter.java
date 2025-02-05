/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.conversion;

import eu.europa.ec.edelivery.smp.data.enums.MemberOfType;
import eu.europa.ec.edelivery.smp.data.model.user.DBResourceMember;
import eu.europa.ec.edelivery.smp.data.ui.MemberRO;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.BooleanUtils.isTrue;


/**
 *
 */
@Component
public class DBResourceMemberToMemberROConverter implements Converter<DBResourceMember, MemberRO> {

    @Override
    public MemberRO convert(DBResourceMember source) {
        MemberRO target = new MemberRO();
        target.setMemberOf(MemberOfType.RESOURCE);
        target.setUsername(source.getUser().getUsername());
        target.setFullName(source.getUser().getFullName());
        target.setRoleType(source.getRole());
        target.setMemberId(SessionSecurityUtils.encryptedEntityId(source.getId()));
        target.setHasPermissionReview(isTrue(source.hasPermissionToReview()));
        return target;
    }
}
