package eu.europa.ec.edelivery.smp.security;

import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;
import eu.europa.ec.edelivery.smp.conversion.IdentifierService;
import eu.europa.ec.edelivery.smp.data.dao.DomainMemberDao;
import eu.europa.ec.edelivery.smp.data.dao.GroupMemberDao;
import eu.europa.ec.edelivery.smp.data.dao.ResourceMemberDao;
import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.enums.VisibilityType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.identifiers.Identifier;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.servlet.ResourceAction;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * Service implements logic if user can activate action on the resource
 */

@Service
public class ResourceGuard {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ResourceGuard.class);
    DomainMemberDao domainMemberDao;
    GroupMemberDao groupMemberDao;
    ResourceMemberDao resourceMemberDao;
    IdentifierService identifierService;

    public ResourceGuard(DomainMemberDao domainMemberDao, GroupMemberDao groupMemberDao, ResourceMemberDao resourceMemberDao, IdentifierService identifierService) {
        this.domainMemberDao = domainMemberDao;
        this.groupMemberDao = groupMemberDao;
        this.resourceMemberDao = resourceMemberDao;
        this.identifierService = identifierService;
    }


    /**
     * Method validates if the user is authorized for action on the resource
     * @param user user trying to execute the action
     * @param action resource action
     * @param resource target resource
     * @return
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
           /* case UPDATE:
                return canUpdate(user, subresource);
            case CREATE:
                return canCreate(user, subresource); */
            case DELETE:
                return canDelete(user, subresource);
        }
        throw new SMPRuntimeException(ErrorCode.INTERNAL_ERROR, "Action not supported", "Unknown user action: [" + action + "]");
    }


    public boolean canRead(SMPUserDetails user, DBResource resource) {
        LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] is trying to read resource [{}]", user, resource);

        // if resource is public anybody can see it
        if (resource.getVisibility() == VisibilityType.PUBLIC) {
            LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] authorized to read public resource [{}]", user, resource);
            return true;
        }
        if (user == null || user.getUser() == null) {
            LOG.debug(SMPLogger.SECURITY_MARKER, "Anonymous user [{}] is not authorized to read resource [{}]", user, resource);
            return false;
        }

        if (resource.getVisibility() == null || resource.getVisibility() == VisibilityType.PRIVATE) {
            boolean isResourceMember = resourceMemberDao.isUserResourceMember(user.getUser(), resource);
            LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] authorized: [{}] to read private resource [{}]", user, isResourceMember, resource);
            return isResourceMember;
        }
        // if resource is internal the domain, group members and resource member can see it
        if (resource.getVisibility() == VisibilityType.INTERNAL) {
            boolean isAuthorized =  domainMemberDao.isUserDomainMember(user.getUser(), resource.getDomainResourceDef().getDomain())
                    || groupMemberDao.isUserGroupMember(user.getUser(), resource.getGroups());
            LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] authorized: [{}] to read internal resource [{}]", user, isAuthorized, resource);
            return isAuthorized;
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
            return resource.getId() == null?
                    canCreate(user, resource, domain):
                    canUpdate(user, resource);
    }

    public boolean canUpdate(SMPUserDetails user, DBResource resource) {
        LOG.info(SMPLogger.SECURITY_MARKER, "User [{}] is trying to update resource [{}]", user, resource);
        if (user == null || user.getUser() == null) {
            LOG.warn("Not user is logged in!");
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
            LOG.warn("Not user is logged in!");
            return false;
        }
        return groupMemberDao.isUserAnyDomainGroupResourceMemberWithRole(user.getUser(),
                domain,
                MembershipRoleType.ADMIN);

    }

    public boolean canCreate(SMPUserDetails user, DBSubresource subresource) {
        LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] is trying to create subresource [{}]", user, subresource);
        return canUpdate(user, subresource);
    }

    public boolean canDelete(SMPUserDetails user, DBResource resource, DBDomain domain) {
        LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] is trying to delete resource [{}]", user, resource);
        // same as for create
        if (user == null || user.getUser() == null) {
            LOG.warn("Not user is logged in!");
            return false;
        }
        return canCreate(user, resource, domain);
    }

    public boolean canDelete(SMPUserDetails user, DBSubresource subresource) {
        LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] is trying to delete resource [{}]", user, subresource);
        // Subresource can be created by the resource admin, the same as for update
        return canUpdate(user, subresource);
    }

    /**
     * Method validates if user is member of the resource with admin rights
     *
     * @param userIdentifier
     * @param resourceIdentifier
     */
    public boolean isResourceAdmin(String userIdentifier, String resourceIdentifier) {
        Identifier pt = identifierService.normalizeParticipantIdentifier(resourceIdentifier);
        return isResourceAdmin(userIdentifier, pt.getValue(), pt.getScheme());
    }

    public boolean isResourceAdmin(String userIdentifier, String resourceIdentifierValue, String resourceIdentifierScheme) {
        // TODO
        /**
         *         ParticipantIdentifierType pt = identifierService.normalizeParticipantIdentifier(serviceGroupIdentifier);
         *         Optional<DBResource> osg = serviceGroupDao.findServiceGroup(pt.getValue(), pt.getScheme());
         *         Optional<DBUser> own = userDao.findUserByIdentifier(ownerIdentifier);
         *         return osg.isPresent() && own.isPresent() && osg.get().getUsers().contains(own.get());
         *     }
         */
        return false;
    }

    /**
     * Method validates if any of the service group users contains userID
     *
     * @param userId
     * @param dbServiceGroup
     * @return
     */
    public boolean isResourceAdmin(Long userId, DBResource dbServiceGroup) {
       /* return dbServiceGroup != null &&
                dbServiceGroup.getUsers().stream().filter(user -> user.getId().equals(userId)).findAny().isPresent();

        */
        return false;
    }
}
