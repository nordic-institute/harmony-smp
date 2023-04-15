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
import org.apache.commons.lang3.StringUtils;
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

    private final GroupDao groupDao;

    public GroupMemberDao(GroupDao groupDao) {
        this.groupDao = groupDao;
    }

    public boolean isUserGroupMember(DBUser user, List<DBGroup> groups) {
        List<Long> groupIds = groups.stream().map(DBGroup::getId).collect(Collectors.toList());
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
        return query.getResultList().stream().anyMatch(member -> member.getRole() == roleType);
    }

    public boolean isUserAnyDomainGroupResourceMemberWithRole(DBUser user, DBDomain domain, MembershipRoleType roleType) {
        return isUserAnyDomainGroupResourceMemberWithRole(user.getId(), domain.getId(), roleType);
    }

    public boolean isUserAnyDomainGroupResourceMemberWithRole(Long userId, Long domainId, MembershipRoleType roleType) {
        LOG.debug("User [{}], Domain [{}], Role [{}]", userId, domainId, roleType);
        TypedQuery<Long> query = memEManager.createNamedQuery(QUERY_GROUP_MEMBER_BY_USER_DOMAIN_GROUPS_ROLE_COUNT,
                Long.class);
        query.setParameter(PARAM_USER_ID,userId);
        query.setParameter(PARAM_DOMAIN_ID, domainId);
        query.setParameter(PARAM_MEMBERSHIP_ROLE, roleType);
        return query.getSingleResult() > 0;
    }

    public boolean isUserGroupAdministrator(Long userId) {
        return groupDao.getGroupsByUserIdAndRolesCount(userId, MembershipRoleType.ADMIN) > 0;
    }

    public List<DBGroupMember> getGroupMembers(Long groupId, int iPage, int iPageSize, String filter) {
        boolean hasFilter = StringUtils.isNotBlank(filter);
        TypedQuery<DBGroupMember> query = memEManager.createNamedQuery(hasFilter ?
                QUERY_GROUP_MEMBERS_FILTER : QUERY_GROUP_MEMBERS, DBGroupMember.class);

        if (iPageSize > -1 && iPage > -1) {
            query.setFirstResult(iPage * iPageSize);
        }
        if (iPageSize > 0) {
            query.setMaxResults(iPageSize);
        }
        query.setParameter(PARAM_GROUP_ID, groupId);
        if (hasFilter) {
            query.setParameter(PARAM_USER_FILTER, "%" + StringUtils.trim(filter) + "%");
        }
        return query.getResultList();
    }

    public Long getGroupMemberCount(Long groupId, String filter) {
        boolean hasFilter = StringUtils.isNotBlank(filter);
        TypedQuery<Long> query = memEManager.createNamedQuery(hasFilter ? QUERY_GROUP_MEMBERS_FILTER_COUNT : QUERY_GROUP_MEMBERS_COUNT, Long.class);
        query.setParameter(PARAM_GROUP_ID, groupId);
        if (hasFilter) {
            query.setParameter(PARAM_USER_FILTER, "%" + StringUtils.trim(filter) + "%");
        }
        return query.getSingleResult();
    }


    public DBGroupMember addMemberToDomain(DBGroup group, DBUser user, MembershipRoleType role) {
        DBGroupMember groupMember = new DBGroupMember();
        groupMember.setRole(role);
        groupMember.setUser(user);
        groupMember.setGroup(group);
        groupMember = merge(groupMember);
        return groupMember;
    }
}
