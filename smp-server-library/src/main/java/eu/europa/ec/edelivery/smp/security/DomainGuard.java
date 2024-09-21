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
import eu.europa.ec.edelivery.smp.data.dao.GroupDao;
import eu.europa.ec.edelivery.smp.data.dao.GroupMemberDao;
import eu.europa.ec.edelivery.smp.data.dao.ResourceMemberDao;
import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.enums.VisibilityType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBGroup;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.resource.DomainResolverService;
import eu.europa.ec.edelivery.smp.servlet.ResourceAction;
import eu.europa.ec.edelivery.smp.servlet.ResourceRequest;
import eu.europa.ec.edelivery.smp.utils.EntityLoggingUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The class is responsible for guarding the domain groups, resources and sub-resources.
 * It validates if users have any "permission to" execute the http action on the domain and groups.
 *
 * @since 5.0
 * @author Joze RIHTARSIC
 */
@Component
public class DomainGuard {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DomainGuard.class);
    public static final String NOT_DEFINED = "Not defined";

    final DomainResolverService domainResolverService;
    final GroupDao groupDao;
    final DomainMemberDao domainMemberDao;
    final GroupMemberDao groupMemberDao;
    final ResourceMemberDao resourceMemberDao;

    public DomainGuard(DomainResolverService domainResolverService,
                       DomainMemberDao domainMemberDao,
                       GroupMemberDao groupMemberDao,
                       ResourceMemberDao resourceMemberDao,
                       GroupDao groupDao) {
        this.domainResolverService = domainResolverService;
        this.domainMemberDao = domainMemberDao;
        this.groupMemberDao = groupMemberDao;
        this.resourceMemberDao = resourceMemberDao;
        this.groupDao = groupDao;
    }


    /**
     * Method resolves the domain and authorize the user for the action on the domain
     *
     * @param resourceRequest a resource request
     * @param user            a user trying to execute the action on the resource
     * @return the DBDomain
     */
    public DBDomain resolveAndAuthorizeForDomain(ResourceRequest resourceRequest, SMPUserDetails user) {
        DBDomain domain = domainResolverService.resolveDomain(
                resourceRequest.getDomainHttpParameter(),
                resourceRequest.getUrlPathParameter(0));

        if (isUserIsAuthorizedForDomainResourceAction(domain, user, resourceRequest.getAction())) {
            resourceRequest.setAuthorizedDomain(domain);
            return domain;
        }

        throw new AuthenticationServiceException("User is not authorized for the domain!");
    }

    /**
     * Method resolves the domain and authorize the user for the action on the domain
     *
     * @param resourceRequest a resource request
     * @param user            a user trying to execute the action on the resource
     * @return the DBDomain
     */
    public List<DBGroup> resolveAndAuthorizeForGroup(ResourceRequest resourceRequest, SMPUserDetails user) {

        List<DBGroup> groups = domainResolverService.resolveGroup(
                user != null ? user.getUser() : null,
                resourceRequest.getAuthorizedDomain(),
                resourceRequest.getResourceGroupParameter()
        );

        if (isUserAuthorizedForGroup(groups, user, resourceRequest.getAction())) {
            resourceRequest.getAuthorizedGroups().addAll(groups);
            return groups;
        }
        throw new AuthenticationServiceException("User is not authorized for the group!");
    }

    /**
     * Purpose of the method is to guard domain resources and sub-resources. It validates if users has any
     * "permission to" execute the http action on the domain resources and subresources. More accurate check is done
     * when the resource and/or subresource are resolved.
     *
     * @param user   user to be authorized
     * @param action action to be executed
     * @param domain domain to be authorized
     * @return true if user is authorized to execute the action on the domain
     */
    public boolean isUserIsAuthorizedForDomainResourceAction(DBDomain domain, SMPUserDetails user, ResourceAction action) {
        String userInfo = user != null ? user.getUsername() : "anonymous";
        LOG.debug("Authorize check for user [{}], domain [{}] and action [{}]", userInfo, domain, action);
        if (action == null) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "Null http action ", "Action cannot be null!");
        }
        switch (action) {
            case READ:
                return canRead(user, domain);
            case CREATE_UPDATE:
                return canCreateUpdate(user, domain);
            case DELETE:
                return canDelete(user, domain);
        }
        throw new SMPRuntimeException(ErrorCode.INTERNAL_ERROR, "Unknown user [" + userInfo + "] action: [" + action + "]");
    }

    /**
     * Method validates of the user can read resources on the domain!
     *
     * @param user   user to be authorized for READ action
     * @param domain domain to be authorized
     * @return true if user is authorized to execute the action on the domain, else it returns false
     */
    public boolean canRead(SMPUserDetails user, DBDomain domain) {
        LOG.info(SMPLogger.SECURITY_MARKER, "User: [{}] is trying to read domain: [{}]", user, domain);

        // if resource is public anybody can see it
        if (domain.getVisibility() == VisibilityType.PUBLIC) {
            LOG.info(SMPLogger.SECURITY_MARKER, "User: [{}] authorized to read public domain[{}]", user, domain);
            return true;
        }
        if (user == null || user.getUser() == null || user.getUser().getId() == null) {
            LOG.warn(SMPLogger.SECURITY_MARKER, "Anonymous user: [{}] is not authorized to read domain: [{}]", user, domain);
            return false;
        }
        // to be able to read internal(private) domain resources it must be member of domain, domain group or domain resources
        boolean isAuthorized = domainMemberDao.isUserDomainMember(user.getUser(), domain)
                || groupMemberDao.isUserAnyDomainGroupResourceMember(user.getUser(), domain)
                || resourceMemberDao.isUserAnyDomainResourceMember(user.getUser(), domain);


        LOG.debug(SMPLogger.SECURITY_MARKER, "User: [{}] is authorized:[{}] to read resources from Domain: [{}]", user, isAuthorized, domain);
        return isAuthorized;
    }

    /**
     * Method validates of the user can delete resources on the domain! Only users with group admin role can delete
     * domain resources
     *
     * @param user   user to be authorized
     * @param domain domain to be authorized
     * @return true if user is authorized to execute the action on the domain
     */
    public boolean canDelete(SMPUserDetails user, DBDomain domain) {
        LOG.info(SMPLogger.SECURITY_MARKER, "User: [{}] is trying to delete resource from domain: [{}]", user, domain);

        if (user == null || user.getUser() == null || user.getUser().getId() == null) {
            LOG.info(SMPLogger.SECURITY_MARKER, "Anonymous user: [{}] is not authorized to delete resources on domain: [{}]", user, domain);
            return false;
        }
        // to be able to delete domain resources it must be member of any group on domain
        boolean isAuthorized = groupMemberDao.isUserAnyDomainGroupResourceMemberWithRole(user.getUser(), domain, MembershipRoleType.ADMIN)
                || resourceMemberDao.isUserAnyDomainResourceMemberWithRole(user.getUser(), domain, MembershipRoleType.ADMIN);
        LOG.info(SMPLogger.SECURITY_MARKER, "User: [{}] is authorized:[{}] to read resources from Domain: [{}]", user, isAuthorized, domain);
        return isAuthorized;
    }

    /**
     * Method validates of the user can create/update resources on the domain! Only users with group admin role can create and users with admin resource role
     * can update
     *
     * @param user   user to be authorized
     * @param domain domain to be authorized
     * @return true if user is authorized to execute the action on the domain
     */
    public boolean canCreateUpdate(SMPUserDetails user, DBDomain domain) {
        LOG.info(SMPLogger.SECURITY_MARKER, "User: [{}] is trying to create/update resource from domain: [{}]", user, domain);

        if (user == null || user.getUser() == null || user.getUser().getId() == null) {
            LOG.warn(SMPLogger.SECURITY_MARKER, "Anonymous user: [{}] is not authorized to create/update resources on domain: [{}]", user, domain);
            return false;
        }
        // to be able to delete domain resources it must be member of any group on domain
        boolean isAuthorized = groupMemberDao.isUserAnyDomainGroupResourceMemberWithRole(user.getUser(), domain, MembershipRoleType.ADMIN)
                || resourceMemberDao.isUserAnyDomainResourceMemberWithRole(user.getUser(), domain, MembershipRoleType.ADMIN);

        if (isAuthorized) {
            LOG.info(SMPLogger.SECURITY_MARKER, "User: [{}] is authorized to create/update resources from Domain: [{}]", user, domain);
        } else {
            LOG.warn(SMPLogger.SECURITY_MARKER, "User: [{}] is NOT authorized to create/update resources from Domain: [{}]", user, domain);
        }
        return isAuthorized;
    }


    public boolean isUserAuthorizedForGroup(List<DBGroup> groups, SMPUserDetails user, ResourceAction action) {
        String userInfo = EntityLoggingUtils.userDetailToString(user);
        LOG.debug("Authorize check for user [{}], group size [{}] and action [{}]", userInfo, groups.size(), action);
        if (action == null) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "Null http action", "Action cannot be null!");
        }
        switch (action) {
            case READ:
                return canRead(user, groups);
            case CREATE_UPDATE:
                return canCreateUpdate(user, groups);
            case DELETE:
                return canDelete(user, groups);
        }
        throw new SMPRuntimeException(ErrorCode.INTERNAL_ERROR, "Unknown user action: [" + action + "]");
    }

    protected boolean canRead(SMPUserDetails user, List<DBGroup> groups) {
        String userInfo = EntityLoggingUtils.userDetailToString(user);
        String groupsInfo = groups.stream().map(DBGroup::getGroupName).reduce((a, b) -> a + ", " + b).orElse(NOT_DEFINED);
        LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] is trying to read groups [{}]", userInfo, groupsInfo);
        if (groups.isEmpty()) {
            LOG.debug(SMPLogger.SECURITY_MARKER, "Group is not defined for user [{}] the READ action. Authorization will be determinate at resource level", userInfo);
            return true;
        }
        // all public groups are visible to all users
        if (groups.stream().anyMatch(group -> group.getVisibility() == VisibilityType.PUBLIC)) {
            // if any group is public, user can read it return true. The rest of the groups will be checked at resource level
            LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] is authorized to read public groups [{}]", userInfo, groupsInfo);
            return true;
        }
        // group is private, only members can read it
        if (user == null || user.getUser() == null) {
            LOG.warn(SMPLogger.SECURITY_MARKER, "Anonymous user [{}] is not authorized to read groups [{}]", userInfo, groupsInfo);
            return false;
        }
        // check if user is admin of any group or member of any group
        boolean isAuthorized = groupMemberDao.isUserGroupMember(user.getUser(), groups)
                || resourceMemberDao.isUserAnyGroupsResourceMember(user.getUser(), groups);
        LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] is authorized:[{}] to read resources from groups [{}]", userInfo, isAuthorized, groupsInfo);
        return isAuthorized;
    }

    protected boolean canCreateUpdate(SMPUserDetails user, List<DBGroup> groups) {
        String userInfo = EntityLoggingUtils.userDetailToString(user);
        String groupsInfo = groups.stream().map(DBGroup::getGroupName).reduce((a, b) -> a + ", " + b).orElse(NOT_DEFINED);
        LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] is trying to create/update group [{}]", userInfo, groupsInfo);
        Long userId = user == null || user.getUser() == null ? null : user.getUser().getId();
        // group is private, only members can read it
        if (userId == null) {
            LOG.warn(SMPLogger.SECURITY_MARKER, "Anonymous user [{}] is not authorized to create/update resources on groups [{}]", userInfo, groupsInfo);
            return false;
        }
        // allow only group admins to create/delete resources and group members to update resources
        List<Long> groupIds = groups.stream().map(DBGroup::getId).collect(Collectors.toList());
        boolean isAuthorized =
                resourceMemberDao.isUserAnyGroupsResourceMemberWithRole(userId, groupIds, MembershipRoleType.ADMIN)
                        || groupMemberDao.isUserGroupMemberWithRole(userId, groupIds, MembershipRoleType.ADMIN);
        LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] is authorized: [{}] to create/update resources from Group [{}]", userInfo, isAuthorized, groups);
        return isAuthorized;
    }

    protected boolean canDelete(SMPUserDetails user, List<DBGroup> groups) {
        String userInfo = EntityLoggingUtils.userDetailToString(user);
        String groupsInfo = groups.stream().map(DBGroup::getGroupName).reduce((a, b) -> a + ", " + b).orElse(NOT_DEFINED);
        LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] is trying to delete resource on groups [{}]", userInfo, groupsInfo);
        if (groups.isEmpty()) {
            LOG.warn(SMPLogger.SECURITY_MARKER, "Group must be specified for action DELETE. User is not authorized [{}],", userInfo);
            return false;
        }

        // group is private, only members can read it
        Long userId = user == null || user.getUser() == null ? null : user.getUser().getId();
        if (userId == null) {
            LOG.warn(SMPLogger.SECURITY_MARKER, "Anonymous user [{}] is not authorized to delete resources on groups [{}]", userInfo, groupsInfo);
            return false;
        }
        // allow only group admins to create/delete resources
        boolean isAuthorized = groupMemberDao.isUserGroupMember(user.getUser(), groups)
                || resourceMemberDao.isUserAnyGroupsResourceMember(user.getUser(), groups);
        LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] is authorized: [{}] to delete resources from groups [{}]", userInfo, isAuthorized, groupsInfo);
        return isAuthorized;
    }
}
