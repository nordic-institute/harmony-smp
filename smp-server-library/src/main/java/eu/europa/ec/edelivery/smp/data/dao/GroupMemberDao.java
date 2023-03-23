/*
 * Copyright 2018 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.data.dao;

import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBGroup;
import eu.europa.ec.edelivery.smp.data.model.user.DBGroupMember;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Repository
public class GroupMemberDao extends BaseDao<DBGroupMember> {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(GroupMemberDao.class);
    public boolean isUserGroupMember(DBUser user, List<DBGroup> groups) {
        List<Long> groupIds = groups.stream().map(grop -> grop.getId()).collect(Collectors.toList());
        return isGroupResourceMember(user.getId(), groupIds);
    }

    public boolean isGroupResourceMember(Long userId, List<Long> groupIds) {
        LOG.debug("UserId [{}], groupIds [{}]", userId, groupIds);
        TypedQuery<Long> query = memEManager.createNamedQuery(QUERY_GROUP_MEMBER_BY_USER_GROUPS_COUNT,
                Long.class);
        query.setParameter(PARAM_USER_ID, userId);
        query.setParameter(PARAM_GROUP_IDS, groupIds);
        return query.getSingleResult() > 0;
    }

    public boolean isUserAnyDomainGroupResourceMember(DBUser user, DBDomain domain) {
        LOG.debug("User [{}], domain [{}]", user, domain);
        TypedQuery<Long> query = memEManager.createNamedQuery(QUERY_GROUP_MEMBER_BY_USER_DOMAIN_GROUPS_COUNT,
                Long.class);
        query.setParameter(PARAM_USER_ID, user.getId());
        query.setParameter(PARAM_DOMAIN_ID, domain.getId());
        return query.getSingleResult() > 0;
    }

    public boolean isUserGroupMemberWithRole(Long userId, List<Long> groupIds, MembershipRoleType roleType) {
        LOG.debug("UserId [{}], groupIds [{}], Role [{}]", userId, groupIds, roleType);
        TypedQuery<DBGroupMember> query = memEManager.createNamedQuery(QUERY_GROUP_MEMBER_BY_USER_GROUPS, DBGroupMember.class);

        query.setParameter(PARAM_USER_ID, userId);
        query.setParameter(PARAM_GROUP_IDS, groupIds);
        return query.getResultList().stream().anyMatch(member ->member.getRole() == roleType );
    }

    public boolean isUserAnyDomainGroupResourceMemberWithRole(DBUser user, DBDomain domain, MembershipRoleType roleType) {
        LOG.debug("User [{}], Domain [{}], Role [{}]", user, domain, roleType);
        TypedQuery<Long> query = memEManager.createNamedQuery(QUERY_GROUP_MEMBER_BY_USER_DOMAIN_GROUPS_ROLE_COUNT,
                Long.class);
        query.setParameter(PARAM_USER_ID, user.getId());
        query.setParameter(PARAM_DOMAIN_ID, domain.getId());
        query.setParameter(PARAM_MEMBERSHIP_ROLE, roleType);
        return query.getSingleResult() > 0;
    }
}
