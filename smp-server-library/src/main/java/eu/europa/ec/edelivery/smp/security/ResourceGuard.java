package eu.europa.ec.edelivery.smp.security;

import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;
import eu.europa.ec.edelivery.smp.conversion.IdentifierService;
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
import eu.europa.ec.edelivery.smp.identifiers.Identifier;
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
     *
     * @param user     user trying to execute the action
     * @param action   resource action
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
        if (domain.getVisibility() == VisibilityType.PRIVATE &&
                (dbuser == null ||
                        !(domainMemberDao.isUserDomainMember(dbuser, domain)
                                || groupMemberDao.isUserAnyDomainGroupResourceMember(dbuser, domain)
                                || resourceMemberDao.isUserAnyDomainResourceMember(dbuser, domain)))
        ) {
            LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] is not authorized to read internal domain [{}] resources", user, domain);
            return false;
        }
        // if group is internal check if user is member of group, or any group resources,
        if (group.getVisibility() == VisibilityType.PRIVATE &&
                (dbuser == null ||
                        !(groupMemberDao.isUserGroupMember(dbuser, Collections.singletonList(group))
                                || resourceMemberDao.isUserAnyGroupResourceMember(dbuser, group))
                )) {
            LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] is not authorized to read internal group [{}] resources", user, domain);
            return false;
        }

        // if resource is public anybody can see it
        if (resource.getVisibility() == VisibilityType.PUBLIC) {
            LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] authorized to read public resource [{}]", user, resource);
            return true;
        }
        if (dbuser == null) {
            LOG.debug(SMPLogger.SECURITY_MARKER, "Anonymous user [{}] is not authorized to read resource [{}]", user, resource);
            return false;
        }

        if (resource.getVisibility() == null || resource.getVisibility() == VisibilityType.PRIVATE) {
            boolean isResourceMember = resourceMemberDao.isUserResourceMember(user.getUser(), resource);
            LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] authorized: [{}] to read private resource [{}]", user, isResourceMember, resource);
            return isResourceMember;
        }
        /*
        // if resource is internal the domain, group members and resource member can see it
        if (resource.getVisibility() == VisibilityType.INTERNAL) {

            boolean isAuthorized = domainMemberDao.isUserDomainMember(dbuser, resource.getDomainResourceDef().getDomain())
                    || groupMemberDao.isUserGroupMember(dbuser, Collections.singletonList(resource.getGroup()));
            LOG.debug(SMPLogger.SECURITY_MARKER, "User [{}] authorized: [{}] to read internal resource [{}]", user, isAuthorized, resource);
            return isAuthorized;
        }
*/
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
