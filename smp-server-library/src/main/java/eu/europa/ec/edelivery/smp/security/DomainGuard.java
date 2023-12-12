package eu.europa.ec.edelivery.smp.security;

import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;
import eu.europa.ec.edelivery.smp.data.dao.DomainMemberDao;
import eu.europa.ec.edelivery.smp.data.dao.GroupMemberDao;
import eu.europa.ec.edelivery.smp.data.dao.ResourceMemberDao;
import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.enums.VisibilityType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.resource.DomainResolverService;
import eu.europa.ec.edelivery.smp.servlet.ResourceAction;
import eu.europa.ec.edelivery.smp.servlet.ResourceRequest;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;

@Component
public class DomainGuard {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(DomainGuard.class);

    final DomainResolverService domainResolverService;
    DomainMemberDao domainMemberDao;
    GroupMemberDao groupMemberDao;
    ResourceMemberDao resourceMemberDao;

    public DomainGuard(DomainResolverService domainResolverService, DomainMemberDao domainMemberDao, GroupMemberDao groupMemberDao, ResourceMemberDao resourceMemberDao) {
        this.domainResolverService = domainResolverService;
        this.domainMemberDao = domainMemberDao;
        this.groupMemberDao = groupMemberDao;
        this.resourceMemberDao = resourceMemberDao;
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
        LOG.debug("Authorize check for user [{}], domain [{}] and action [{}]", user, domain, action);
        if (action == null) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "Null http action", "Action cannot be null!");
        }
        switch (action) {
            case READ:
                return canRead(user, domain);
            case CREATE_UPDATE:
                return canCreateUpdate(user, domain);
            case DELETE:
                return canDelete(user, domain);
        }
        throw new SMPRuntimeException(ErrorCode.INTERNAL_ERROR, "Unknown user action: [" + action + "]");
    }

    /**
     * Method validates of the user can read resources on the domain!
     *
     * @param user  user to be authorized for READ action
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

        if (isAuthorized){
            LOG.info(SMPLogger.SECURITY_MARKER, "User: [{}] is authorized to create/update resources from Domain: [{}]", user, domain);
        } else {
            LOG.warn(SMPLogger.SECURITY_MARKER, "User: [{}] is NOT authorized to create/update resources from Domain: [{}]", user, domain);
        }
        return isAuthorized;
    }
}
