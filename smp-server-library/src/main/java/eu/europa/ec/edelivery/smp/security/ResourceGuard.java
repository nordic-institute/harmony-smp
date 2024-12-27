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
package eu.europa.ec.edelivery.smp.security;

import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;
import eu.europa.ec.edelivery.smp.data.dao.DomainMemberDao;
import eu.europa.ec.edelivery.smp.data.dao.GroupMemberDao;
import eu.europa.ec.edelivery.smp.data.dao.ResourceMemberDao;
import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.enums.VisibilityType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBGroup;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.servlet.ResourceAction;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Service implements logic if user can activate action on the resource
 */

@Service
public class ResourceGuard {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ResourceGuard.class);
    private static final String LOG_NOT_LOGGED_IN = "Anonymous users are not permitted to execute action [{}]!";
    private DomainMemberDao domainMemberDao;
    private GroupMemberDao groupMemberDao;
    private ResourceMemberDao resourceMemberDao;

    public ResourceGuard(DomainMemberDao domainMemberDao, GroupMemberDao groupMemberDao, ResourceMemberDao resourceMemberDao) {
        this.domainMemberDao = domainMemberDao;
        this.groupMemberDao = groupMemberDao;
        this.resourceMemberDao = resourceMemberDao;
    }

    /**
     * Method validates if the user is authorized for action on the resource
     *
     * @param user     user trying to execute the action
     * @param action   resource action
     * @param resource target resource
     * @return true if user is not authorized for the http action on the resource, else false.
     */
    public boolean userIsNotAuthorizedForAction(SMPUserDetails user, ResourceAction action, DBResource resource, DBDomain domain) {
        return !userIsAuthorizedForAction(user, action, resource, domain);
    }

    public boolean userIsAuthorizedForAction(SMPUserDetails user, ResourceAction action, DBResource resource, DBDomain domain) {
        switch (action) {
            case READ:
                return canRead(user, resource);
            case CREATE_UPDATE:
                return canCreateOrUpdate(user, resource, domain);
            case DELETE:
                return canDelete(user, resource, domain);
        }
        throw new SMPRuntimeException(ErrorCode.INTERNAL_ERROR, "Action not supported", "Unknown user action: [" + action + "]");
    }

    public boolean userIsAuthorizedForAction(SMPUserDetails user, ResourceAction action, DBSubresource subresource) {
        switch (action) {
            case READ:
                return canRead(user, subresource);
            case CREATE_UPDATE:
                return canCreateUpdate(user, subresource);
            case DELETE:
                return canDelete(user, subresource);
        }
        throw new SMPRuntimeException(ErrorCode.INTERNAL_ERROR, "Action not supported", "Unknown user action: [" + action + "]");
    }


    public boolean canRead(SMPUserDetails user, DBResource resource) {
        LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] is trying to read resource [{}]", user, resource);

        DBGroup group = resource.getGroup();
        DBDomain domain = group.getDomain();
        DBUser dbuser = user == null ? null : user.getUser();
        // if domain is internal check if user is member of domain, or any internal resources, groups

        if (resource.getVisibility() == VisibilityType.PRIVATE ) {
            LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] is trying to read private resource [{}]", user, resource);
            return dbuser!=null && resourceMemberDao.isUserResourceMember(dbuser, resource);
        }

        if (group.getVisibility() == VisibilityType.PRIVATE) {
            LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] is trying to read public resource in a private group [{}]", user, group);
            return dbuser!=null &&  (groupMemberDao.isUserGroupMember(dbuser, Collections.singletonList(group)) ||
                    resourceMemberDao.isUserAnyGroupResourceMember(dbuser, group));
        }

        if ((resource.getVisibility() == null || domain.getVisibility() == VisibilityType.PRIVATE)
                && (dbuser == null ||
                !(domainMemberDao.isUserDomainMember(dbuser, domain)
                        || groupMemberDao.isUserAnyDomainGroupResourceMember(dbuser, domain)
                        || resourceMemberDao.isUserAnyDomainResourceMember(dbuser, domain)))) {
            LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] is not authorized to read internal domain [{}] resources", user, domain);
            return false;
        }

        // if resource is public anybody can see it
        if (resource.getVisibility() == VisibilityType.PUBLIC) {
            LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] authorized to read public resource [{}]", user, resource);
            return true;
        }
        LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] is not authorized to read resource [{}]", user, resource);
        return false;
    }

    public boolean canRead(SMPUserDetails user, DBSubresource subresource) {
        // same read rights as for resource
        LOG.info(SMPLogger.SECURITY_MARKER, "User [{}] is trying to read subresource [{}]", user, subresource);
        return canRead(user, subresource.getResource());
    }

    public boolean canCreateOrUpdate(SMPUserDetails user, DBResource resource, DBDomain domain) {
        return resource.getId() == null ?
                canCreate(user, resource, domain) :
                canUpdate(user, resource);
    }

    public boolean canUpdate(SMPUserDetails user, DBResource resource) {
        LOG.info(SMPLogger.SECURITY_MARKER, "User [{}] is trying to update resource [{}]", user, resource);
        if (user == null || user.getUser() == null) {
            LOG.warn(LOG_NOT_LOGGED_IN, "UPDATE");
            return false;
        }
        // only resource member with admin rights can update resource
        return resourceMemberDao.isUserResourceMemberWithRole(user.getUser().getId(), resource.getId(), MembershipRoleType.ADMIN);
    }

    public boolean canUpdate(SMPUserDetails user, DBSubresource subresource) {
        LOG.info(SMPLogger.SECURITY_MARKER, "User [{}] is trying to update subresource [{}]", user, subresource);
        return canUpdate(user, subresource.getResource());
    }

    // only group admin can create resource
    public boolean canCreate(SMPUserDetails user, DBResource resource, DBDomain domain) {
        LOG.info(SMPLogger.SECURITY_MARKER, "User [{}] is trying to create resource [{}]", user, resource);
        if (user == null || user.getUser() == null) {
            LOG.warn(LOG_NOT_LOGGED_IN, "CREATE");
            return false;
        }
        return groupMemberDao.isUserAnyDomainGroupResourceMemberWithRole(user.getUser(),
                domain,
                MembershipRoleType.ADMIN);

    }

    public boolean canDelete(SMPUserDetails user, DBResource resource, DBDomain domain) {
        LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] is trying to delete resource [{}]", user, resource);
        // same as for create
        if (user == null || user.getUser() == null) {
            LOG.warn(LOG_NOT_LOGGED_IN, "DELETE");
            return false;
        }
        return canCreate(user, resource, domain);
    }

    public boolean canDelete(SMPUserDetails user, DBSubresource subresource) {
        LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] is trying to delete subresource [{}]", user, subresource);
        // Subresource can be created by the resource admin, the same as for update
        return canUpdate(user, subresource);
    }

    public boolean canCreateUpdate(SMPUserDetails user, DBSubresource subresource) {
        LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] is trying to create/update subresource [{}]", user, subresource);
        // Subresource can be created by the resource admin, the same as for update
        return canUpdate(user, subresource);
    }
}
