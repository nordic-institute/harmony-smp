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
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.user.DBResourceMember;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

import static eu.europa.ec.edelivery.smp.data.dao.QueryNames.*;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Repository
public class ResourceMemberDao extends BaseDao<DBResourceMember> {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ResourceMemberDao.class);

    public boolean isUserResourceMember(DBUser user, DBResource resource) {
        return isUserResourceMember(user.getId(), resource.getId());
    }

    public boolean isUserResourceMember(Long userId, Long resourceId) {
        TypedQuery<Long> query = memEManager.createNamedQuery(QUERY_RESOURCE_MEMBER_BY_USER_RESOURCE_COUNT,
                Long.class);
        query.setParameter(PARAM_USER_ID, userId);
        query.setParameter(PARAM_RESOURCE_ID, resourceId);
        return query.getSingleResult() > 0;
    }

    public boolean isUserResourceMemberWithRole(Long userId, Long resourceId, MembershipRoleType roleType) {
        LOG.debug("User id [{}], Domain id [{}], role [{}]", userId, resourceId, roleType);
        TypedQuery<DBResourceMember> query = memEManager.createNamedQuery(QUERY_RESOURCE_MEMBER_BY_USER_RESOURCE, DBResourceMember.class);

        query.setParameter(PARAM_USER_ID, userId);
        query.setParameter(PARAM_RESOURCE_ID, resourceId);
        return query.getResultList().stream().anyMatch(member -> member.getRole() == roleType);
    }

    public boolean isUserAnyDomainResourceMember(DBUser user, DBDomain domain) {
        LOG.debug("User [{}], Domain [{}]", user, domain);
        TypedQuery<Long> query = memEManager.createNamedQuery(QUERY_RESOURCE_MEMBER_BY_USER_DOMAIN_RESOURCE_COUNT, Long.class);
        query.setParameter(PARAM_USER_ID, user.getId());
        query.setParameter(PARAM_DOMAIN_ID, domain.getId());
        return query.getSingleResult() > 0;
    }

    public boolean isUserAnyDomainResourceMemberWithRole(DBUser user, DBDomain domain, MembershipRoleType roleType) {
        LOG.debug("User [{}], Domain [{}], Role [{}]", user, domain, roleType);
        TypedQuery<Long> query = memEManager.createNamedQuery(QUERY_RESOURCE_MEMBER_BY_USER_DOMAIN_RESOURCE_ROLE_COUNT, Long.class);
        query.setParameter(PARAM_USER_ID, user.getId());
        query.setParameter(PARAM_DOMAIN_ID, domain.getId());
        query.setParameter(PARAM_MEMBERSHIP_ROLE, roleType);
        return query.getSingleResult() > 0;
    }

    public void setAdminMemberShip(DBUser user, DBResource resource) {
        LOG.info(SMPLogger.SECURITY_MARKER, "Set user [{}], resource admin for resource [{}]", user, resource);
        // attach user or resource
        TypedQuery<DBResourceMember> query = memEManager.createNamedQuery(QUERY_RESOURCE_MEMBER_BY_USER_RESOURCE, DBResourceMember.class);
        query.setParameter(PARAM_USER_ID, user.getId());
        query.setParameter(PARAM_RESOURCE_ID, resource.getId());
        List<DBResourceMember> resourceMembers = query.getResultList();
        if (resourceMembers.isEmpty()) {
            DBResource managedResource = memEManager.contains(resource) ? resource : memEManager.find(DBResource.class, resource.getId());
            DBUser managedUser = memEManager.contains(user) ? user : memEManager.find(DBUser.class, user.getId());

            DBResourceMember dbResourceMember = new DBResourceMember();
            dbResourceMember.setResource(managedResource);
            dbResourceMember.setUser(managedUser);
            dbResourceMember.setRole(MembershipRoleType.ADMIN);
            persist(dbResourceMember);
        } else if (resourceMembers.get(0).getRole() != MembershipRoleType.ADMIN) {
            resourceMembers.get(0).setRole(MembershipRoleType.ADMIN);
        }
    }


}
