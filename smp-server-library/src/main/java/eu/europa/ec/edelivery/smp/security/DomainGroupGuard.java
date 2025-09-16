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

import eu.europa.ec.edelivery.security.PreAuthenticatedCertificatePrincipal;
import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;
import eu.europa.ec.edelivery.smp.auth.enums.SMPAutomationAuthenticationTypes;
import eu.europa.ec.edelivery.smp.config.enums.SMPDomainPropertyEnum;
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
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.resource.DomainResolverService;
import eu.europa.ec.edelivery.smp.services.ui.UITruststoreService;
import eu.europa.ec.edelivery.smp.servlet.ResourceAction;
import eu.europa.ec.edelivery.smp.servlet.ResourceRequest;
import eu.europa.ec.edelivery.smp.utils.EntityLoggingUtils;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The class is responsible for guarding the domain groups, resources and sub-resources.
 * It validates if users have any "permission to" execute the http action on the domain and groups.
 *
 * @author Joze RIHTARSIC
 * @since 5.0
 */
@Component
public class DomainGroupGuard {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DomainGroupGuard.class);
    public static final String NOT_DEFINED = "Not defined";

    final DomainResolverService domainResolverService;
    final GroupDao groupDao;
    final DomainMemberDao domainMemberDao;
    final GroupMemberDao groupMemberDao;
    final ResourceMemberDao resourceMemberDao;
    private final UITruststoreService uITruststoreService;
    private final ConfigurationService configurationService;

    public DomainGroupGuard(DomainResolverService domainResolverService,
                            DomainMemberDao domainMemberDao,
                            GroupMemberDao groupMemberDao,
                            ResourceMemberDao resourceMemberDao,
                            GroupDao groupDao, UITruststoreService uITruststoreService, ConfigurationService configurationService) {
        this.domainResolverService = domainResolverService;
        this.domainMemberDao = domainMemberDao;
        this.groupMemberDao = groupMemberDao;
        this.resourceMemberDao = resourceMemberDao;
        this.groupDao = groupDao;
        this.uITruststoreService = uITruststoreService;
        this.configurationService = configurationService;
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

        if (isRequestAuthorizedOnDomain(resourceRequest, domain, user)) {
            resourceRequest.setAuthorizedDomain(domain);
            return domain;
        }

        throw new AuthenticationServiceException("User is not authorized for the domain!");
    }

    /**
     * The purpose of the method is to guard domain resources and sub-resources. It validates if user
     * credentials type are authorized to execute the action on the domain resources and sub-resources.
     *
     * @return true if user is authorized to execute the action on the domain, else it returns false
     */
    protected boolean isRequestAuthorizedOnDomain(ResourceRequest resourceRequest, DBDomain domain, SMPUserDetails user) {
        ResourceAction action = resourceRequest.getAction();
        Object principal = SessionSecurityUtils.getSessionAuthenticationPrincipal();
        String principalClassName = principal != null ? principal.getClass().getSimpleName() : "anonymous";

        if (domain.getVisibility() == VisibilityType.PUBLIC && action == ResourceAction.READ) {
            LOG.debug(SMPLogger.SECURITY_MARKER, "Principal: [{}] is authorized to read public domain [{}]", principalClassName, domain);
            return true;
        }

        if (!isPrincipalAuthorizedForDomain(domain, principal)) {
            LOG.warn(SMPLogger.SECURITY_MARKER, "Principal: [{}] is not authorized for domain [{}]", principalClassName, domain);
            return false;
        }


        return isUserAuthorizedForDomainResourceAction(domain, user, action);
    }

    /**
     * Method validates if the principal type is authorized to be used on the domain.
     */
    public boolean isPrincipalAuthorizedForDomain(DBDomain domain, Object principal) {
        if (principal == null) {
            LOG.warn(SMPLogger.SECURITY_MARKER, "Can not authorize [null] principal on domain [{}]", domain.getDomainCode());
            return false;
        }
        List<SMPAutomationAuthenticationTypes> authorizationTypes = configurationService.getDomainConfigurationValue(domain, SMPDomainPropertyEnum.AUTOMATION_AUTHENTICATION_TYPES);

        if (principal instanceof PreAuthenticatedCertificatePrincipal certificatePrincipal) {
            if (!authorizationTypes.contains(SMPAutomationAuthenticationTypes.CERTIFICATE)) {
                LOG.debug(SMPLogger.SECURITY_MARKER, "Principal type: [{}] is not authorized for domain [{}]", principal.getClass().getSimpleName(), domain.getDomainCode());
                return false;
            }

            X509Certificate x509Certificate = certificatePrincipal.getCertificate();
            if (x509Certificate == null) {
                LOG.warn(SMPLogger.SECURITY_MARKER, "Certificate is [null] for principal [{}] on domain [{}]", principal, domain.getDomainCode());
                return false;
            }
            // check if certificate is in the domain truststore
            if (!isCertificateAuthorizedForDomain(domain, x509Certificate)) {
                LOG.warn(SMPLogger.SECURITY_MARKER, "Certificate with subjectDN [{}] is not in the domain [{}] truststore", x509Certificate.getSubjectDN(), domain.getDomainCode());
                return false;
            }
            return true;
        } else if (principal instanceof Jwt) {
            if (!authorizationTypes.contains(SMPAutomationAuthenticationTypes.JWT)) {
                LOG.debug(SMPLogger.SECURITY_MARKER, "Principal type: [{}] is not authorized for domain [{}]", principal.getClass().getSimpleName(), domain.getDomainCode());
                return true;
            }
        } else {            // principal is not certificate or JWT, it must be username/password or anonymous
            if (!authorizationTypes.contains(SMPAutomationAuthenticationTypes.BASIC_TOKEN)) {
                LOG.debug(SMPLogger.SECURITY_MARKER, "Principal type: [{}] is not authorized for domain [{}]", principal.getClass().getSimpleName(), domain.getDomainCode());
                return false;
            }
        }

        return true;
    }

    private boolean isCertificateAuthorizedForDomain(DBDomain domain, X509Certificate x509Certificate) {
        // check if domain has its own truststore
        boolean hasDomainTruststoreConfig = configurationService.hasCustomDomainConfiguration(domain, SMPDomainPropertyEnum.TRUSTSTORE_FILENAME);
        if (!hasDomainTruststoreConfig) {
            // domain does not have its own truststore, validation against system truststore is already done
            LOG.debug("Domain [{}] does not have its own truststore configured. Skip domain specific truststore validation", domain.getDomainCode());
            return true;
        }
        try {
            uITruststoreService.validateCertificateWithDomainTruststore(domain, x509Certificate);
        } catch (CertificateException e) {
            LOG.warn(SMPLogger.SECURITY_MARKER, "Certificate validation error for domain [{}]: [{}]",
                    domain.getDomainCode(),
                    e.getMessage());
            return false;
        }
        return true;
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
    public boolean isUserAuthorizedForDomainResourceAction(DBDomain domain, SMPUserDetails user, ResourceAction action) {
        String userInfo = user != null ? user.getUsername() : "anonymous";
        LOG.debug("Authorize check for user [{}], domain [{}] and action [{}]", userInfo, domain, action);
        if (action == null) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "error.invalid.request.is.user.authorized.for.resource");
        }
        return switch (action) {
            case READ -> canRead(user, domain);
            case CREATE_UPDATE -> canCreateUpdate(user, domain);
            case DELETE -> canDelete(user, domain);
        };
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
        } else if (user == null) {
            // if resource is private and user is anonymous, it can not read it
            LOG.warn(SMPLogger.SECURITY_MARKER, "Anonymous user:  is not authorized to read domain: [{}]", domain);
            return false;
        }

        if (user.isJwtAuthenticated()) {
            if (user.getAuthorizedScopes().stream().anyMatch(domain.getDomainCode()::equals)) {
                LOG.info(SMPLogger.SECURITY_MARKER, "User: [{}] is authorized to read domain: [{}] by JWT scope", user, domain);
                return true;
            }
            // if user exists in the system, but does not have the scope for the domain, try also with  the SMP authorization
            if (user.getUser() == null) {
                LOG.warn(SMPLogger.SECURITY_MARKER, "User: [{}] is not authorized to read domain: [{}] by JWT scope [{}]", user, domain, user.getAuthorizedScopes());
                return false;
            }
        }

        if (user.getUser() == null || user.getUser().getId() == null) {
            LOG.warn(SMPLogger.SECURITY_MARKER, "Anonymous user: [{}] is not authorized to read domain: [{}]", user, domain);
            return false;
        }
        // to be able to read internal(private) domain resources it must be member of domain, domain group or domain resources
        boolean isAuthorized = domainMemberDao.isUserDomainGroupOrResourceMember(user.getUser(), domain);


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
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "error.invalid.request.is.user.authorized.for.group");
        }
        return switch (action) {
            case READ -> canRead(user, groups);
            case CREATE_UPDATE -> canCreateUpdate(user, groups);
            case DELETE -> canDelete(user, groups);
        };
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
        // allow only group admins to delete resources
        List<Long> groupIds = groups.stream().map(DBGroup::getId).collect(Collectors.toList());
        boolean isAuthorized =
                resourceMemberDao.isUserAnyGroupsResourceMemberWithRole(userId, groupIds, MembershipRoleType.ADMIN)
                        || groupMemberDao.isUserGroupMemberWithRole(userId, groupIds, MembershipRoleType.ADMIN);
        LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] is authorized: [{}] to delete resources from groups [{}]", userInfo, isAuthorized, groupsInfo);
        return isAuthorized;
    }
}
