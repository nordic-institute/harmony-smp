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
package eu.europa.ec.edelivery.smp.services.resource;

import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;
import eu.europa.ec.edelivery.smp.data.dao.*;
import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBGroup;
import eu.europa.ec.edelivery.smp.data.model.doc.DBDocument;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
import eu.europa.ec.edelivery.smp.data.model.ext.DBResourceDef;
import eu.europa.ec.edelivery.smp.data.model.ext.DBSubresourceDef;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.identifiers.Identifier;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.security.ResourceGuard;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import eu.europa.ec.edelivery.smp.services.IdentifierService;
import eu.europa.ec.edelivery.smp.servlet.ResourceAction;
import eu.europa.ec.edelivery.smp.servlet.ResourceRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.SML_INVALID_IDENTIFIER;
import static eu.europa.ec.edelivery.smp.logging.SMPLogger.SECURITY_MARKER;
import static org.apache.commons.lang3.StringUtils.*;


/**
 * The class resolves the resource/subresource for the given path segment  sequence
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Service
public class ResourceResolverService {

    private static final int MAX_COUNT_COORDINATES = 5;
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ResourceResolverService.class);

    final ResourceGuard resourceGuard;
    final ConfigurationService configurationService;
    final IdentifierService identifierService;
    final DomainDao domainDao;
    final GroupDao groupDao;
    final ResourceDefDao resourceDefinitionDao;
    final DomainResourceDefDao domainResourceDefDao;
    final ResourceDao resourceDao;
    final SubresourceDao subresourceDao;


    public ResourceResolverService(ResourceGuard resourceGuard,
                                   ConfigurationService configurationService,
                                   IdentifierService identifierService,
                                   DomainDao domainDao,
                                   GroupDao groupDao,
                                   DomainResourceDefDao domainResourceDefDao,
                                   ResourceDefDao resourceDefinitionDao,
                                   ResourceDao resourceDao,
                                   SubresourceDao subresourceDao) {

        this.resourceGuard = resourceGuard;
        this.configurationService = configurationService;
        this.identifierService = identifierService;
        this.domainDao = domainDao;
        this.groupDao = groupDao;
        this.domainResourceDefDao = domainResourceDefDao;
        this.resourceDefinitionDao = resourceDefinitionDao;
        this.resourceDao = resourceDao;
        this.subresourceDao = subresourceDao;
    }

    @Transactional
    public ResolvedData resolveAndAuthorizeRequest(SMPUserDetails user, ResourceRequest resourceRequest) {

        validateRequestData(resourceRequest);

        List<String> pathParameters = resourceRequest.getUrlPathParameters();
        DBDomain domain = resourceRequest.getAuthorizedDomain();
        ResolvedData locationVector = new ResolvedData();
        int iParameterIndex = 0;

        // resolve domain
        String currentParameter = pathParameters.get(iParameterIndex);
        locationVector.setDomain(domain);
        // if domain code matches first parameter skip it!
        if (StringUtils.equals(currentParameter, domain.getDomainCode())) {
            if (pathParameters.size() <= ++iParameterIndex) {
                throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, join(pathParameters, ","),
                        "Not enough path parameters to locate resource (The first match the domain)!");
            }
            currentParameter = pathParameters.get(iParameterIndex);
        }

        DBResourceDef resourceDef = resolveResourceType(domain, resourceRequest.getResourceTypeHttpParameter(), currentParameter);
        locationVector.setResourceDef(resourceDef);
        if (StringUtils.equals(currentParameter, resourceDef.getUrlSegment())) {
            if (pathParameters.size() <= ++iParameterIndex) {
                throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, join(pathParameters, ","),
                        "Not enough path parameters to locate resource (The first two match the domain and resource type)!");
            }
            currentParameter = pathParameters.get(iParameterIndex);
        }

        Identifier resourceId = identifierService.normalizeParticipantIdentifier(domain.getDomainCode(), currentParameter);
        // validate identifier
        validateResourceIdentifier(resourceId);
        DBResource resource = resolveResourceIdentifier(domain, resourceDef, resourceId);
        if (resource == null) {
            // the resource must be found because if action is not "create" action nor the last parameter to be resolved
            if (resourceRequest.getAction() != ResourceAction.CREATE_UPDATE
                    || pathParameters.size() > iParameterIndex + 1) {
                throw new SMPRuntimeException(ErrorCode.SG_NOT_EXISTS, resourceId.getValue(), resourceId.getScheme());
            }
            resource = createNewResource(resourceId, resourceDef, domain);
            // determine the group for the resource
            DBGroup group = resolveAdminResourceGroup(user.getUser(), domain,
                    trimToNull(resourceRequest.getResourceGroupParameter()));
            resource.setVisibility(resourceRequest.getResourceVisibilityParameter());
            resource.setGroup(group);
        }

        locationVector.setResource(resource);
        // get ready for next url path parameter.
        iParameterIndex++;
        // check if resource is resolved - no more parameters to be resolved
        locationVector.setResolved(pathParameters.size() == iParameterIndex);
        if (locationVector.isResolved()) {
            // validate if user is authorized for action
            if (resourceGuard.userIsNotAuthorizedForAction(user, resourceRequest.getAction(), resource, domain)) {
                LOG.warn(SECURITY_MARKER, "User [{}] is NOT authorized for action [{}] on the resource [{}]",
                        getUsername(user), resourceRequest.getAction(), resource);
                throw new SMPRuntimeException(ErrorCode.UNAUTHORIZED);
            }
            return locationVector;
        }

        // resolve subresource - expected exactly two parameters
        if (pathParameters.size() != iParameterIndex + 2) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, join(pathParameters, ","),
                    "Invalid remaining subresource parameters (expected only subresourceDef and subresource identifier)");

        }
        String subResourceDefUrl = pathParameters.get(iParameterIndex);
        // test if subresourceDef exists
        DBSubresourceDef subresourceDef = getSubresource(resourceDef, subResourceDefUrl);
        Identifier subResourceId = identifierService.normalizeDocumentIdentifier(
                domain.getDomainCode(),
                pathParameters.get(++iParameterIndex));
        DBSubresource subresource = resolveSubResourceIdentifier(resource, subResourceDefUrl, subResourceId);
        LOG.debug("Got subresource [{}]", subresource);
        if (subresource == null) {
            if (resourceRequest.getAction() != ResourceAction.CREATE_UPDATE) {
                throw new SMPRuntimeException(ErrorCode.METADATA_NOT_EXISTS,
                        resource.getIdentifierValue(), resource.getIdentifierScheme(),
                        subResourceId.getValue(), subResourceId.getScheme());
            }
            subresource = createNewSubResource(subResourceId, resource, subresourceDef);
        }

        if (!resourceGuard.userIsAuthorizedForAction(user, resourceRequest.getAction(), subresource)) {
            LOG.warn(SECURITY_MARKER, "User [{}] is NOT authorized for action [{}] on the subresource resource [{}]",
                    getUsername(user), resourceRequest.getAction(), subresource);
            throw new SMPRuntimeException(ErrorCode.UNAUTHORIZED);
        }
        locationVector.setSubresource(subresource);
        locationVector.setSubResourceDef(subresourceDef);
        locationVector.setResolved(true);
        return locationVector;

    }

    /**
     * Method executes basic  resource request data validation
     *
     * @param resourceRequest the entity to validated.
     */
    public void validateRequestData(ResourceRequest resourceRequest) {
        List<String> pathParameters = resourceRequest.getUrlPathParameters();
        if (pathParameters == null || pathParameters.isEmpty()) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "Null", "Resource Location vector coordinates must not be null!");
        }

        if (pathParameters.size() > MAX_COUNT_COORDINATES) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, join(pathParameters, ","), "More than max. count (5) of Resource Location vector coordinates!");
        }
        if (resourceRequest.getAuthorizedDomain() == null) {
            throw new SMPRuntimeException(ErrorCode.INTERNAL_ERROR, "Null", "Can not resolve resource for unknown domain!");
        }
    }

    /**
     * The process of resolving the resource type starts after the Domain is located. If the domain code was part of the URL path,
     * determining the resource type begins with the "next" path parameter (Below: the current parameter).
     * <p>
     * DomiSMP resolves the resource type for the Domain in the following order.
     *
     * <ol>
     * <li>If only one resource type is registered for the Domain, it sets it by default (legacy)</li>.
     * <li>The next attempt is to determine it via HTTP Header "Resource-Type." If the header is set with the invalid ResourceDef value, it throws the error.</li>
     * <li>The next attempt is with the current path parameter (must be at least two path parameters).</li>
     * <li>The next attempt is to use the default resource type configured for the Domain.</li>
     * <li>If the default resource type is not set, it uses the first registered Domain to the DomiSMP.</li>
     * </ol>
     * <p>
     * NOTE: To enable the url path parameter and the HTTP header to be used at the same time, the "current" path parameter is skipped if it matches the resourceType and there are more than two path parameters left.
     * </p>
     *
     * @param domain
     * @param headerParameter
     * @param pathParameter
     * @return
     */
    public DBResourceDef resolveResourceType(DBDomain domain, String headerParameter, String pathParameter) {
        LOG.debug("Resolve ResourceType for domain [{}] for HTTP header [{}] and path parameter [{}]", domain.getDomainCode(), headerParameter, pathParameter);

        // get single domain
        List<DBResourceDef> resourceDefs = resourceDefinitionDao.getAllResourceDefForDomain(domain);
        if (resourceDefs.isEmpty()) {
            throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, "No resource type is registered for the domain!");
        }

        if (resourceDefs.size() == 1) {
            DBResourceDef resourceDef = resourceDefs.get(0);
            LOG.debug("Only one ResourceDef [{}] is registered to domain [{}]", resourceDef.getIdentifier(), domain.getDomainCode());
            return resourceDefs.get(0);
        }
        // find by path header parameter
        if (StringUtils.isNotBlank(headerParameter)) {
            Optional<DBResourceDef> optResDef = resourceDefs.stream().filter(resdef -> equalsIgnoreCase(headerParameter, resdef.getUrlSegment())).findFirst();
            if (optResDef.isPresent()) {
                LOG.debug("Located ResourceDef for domain [{}] by the http header [{}]", domain.getDomainCode(), headerParameter);
                return optResDef.get();
            } else {
                throw new SMPRuntimeException(ErrorCode.CONFIGURATION_ERROR, "No resource def [" + headerParameter + "] is registered for the domain [" + domain.getDomainCode() + "]");
            }
        }
        // find by path parameter
        Optional<DBResourceDef> optResDef = resourceDefs.stream().filter(resdef -> equalsIgnoreCase(pathParameter, resdef.getUrlSegment())).findFirst();
        if (optResDef.isPresent()) {
            LOG.debug("Located ResourceDef for domain [{}] by the path parameter [{}]", domain.getDomainCode(), pathParameter);
            return optResDef.get();
        }
        // get default parameter
        optResDef = resourceDefs.stream().filter(resdef ->
                equalsIgnoreCase(resdef.getIdentifier(), domain.getDefaultResourceTypeIdentifier())).findFirst();
        if (optResDef.isPresent()) {
            LOG.debug("Located default ResourceDef [{}] for domain [{}] by the path parameter [{}]",
                    domain.getDefaultResourceTypeIdentifier(),
                    domain.getDomainCode(),
                    pathParameter);
            return optResDef.get();
        }
        // return first
        LOG.info("Return first (default) ResourceDef [{}] for domain [{}] by the path parameter [{}]",
                resourceDefs.get(0).getDomainResourceDefs(),
                domain.getDomainCode(),
                pathParameter);
        return resourceDefs.get(0);
    }

    public DBResource resolveResourceIdentifier(DBDomain domain, DBResourceDef resourceDef, Identifier resourceIdentifier) {
        LOG.info("Resolve resourceIdentifier for parameter [{}]", resourceIdentifier);
        // if domain is null get default domain
        Optional<DBResource> optResource = resourceDao.getResource(resourceIdentifier.getValue(), resourceIdentifier.getScheme(), resourceDef, domain);
        return optResource.orElse(null);
    }

    /**
     * Method resolves the group for the given domain, admin user and group name.
     * If the group name is null/not given, the first group is returned. If the group name is provided
     * but the user is not admin for the group, the exception is thrown.
     *
     * @param user        admin user creating the resource
     * @param domain      domain where the resource is created
     * @param domainGroup group name
     * @return DBGroup for the given domain, user and group name.
     * @throws SMPRuntimeException if the user is not admin authorized to create the resource for the given group/domain
     */
    protected DBGroup resolveAdminResourceGroup(DBUser user, DBDomain domain, String domainGroup) {
        LOG.debug("Resolve group for domain [{}] and user [{}] and group [{}]", domain.getDomainCode(),
                user.getUsername(),
                domainGroup);
        List<DBGroup> adminListGroup =
                groupDao.getGroupsByDomainUserIdAndGroupRoles(domain.getId(), user.getId(), MembershipRoleType.ADMIN);
        if (adminListGroup.isEmpty()) {
            throw new SMPRuntimeException(ErrorCode.UNAUTHORIZED,
                    "User [" + user.getUsername() + "] is not admin for any group in domain [" + domain.getDomainCode() + "]");
        }
        if (domainGroup == null) {
            LOG.debug("Set first/default group [{}] for domain [{}]", adminListGroup.get(0).getGroupName(),
                    domain.getDomainCode());
            return adminListGroup.get(0);
        }
        return groupDao.getGroupsByDomainUserIdAndGroupRoles(domain.getId(), user.getId(), MembershipRoleType.ADMIN)
                .stream()
                .filter(group -> equalsIgnoreCase(group.getGroupName(), domainGroup))
                .findFirst()
                .orElseThrow(() -> new SMPRuntimeException(ErrorCode.UNAUTHORIZED,
                        "User [" + user.getUsername() + "] is not authorized for group ["
                                + domainGroup + "] in domain [" + domain.getDomainCode() + "]"));
    }

    /**
     * Resolve subresource for given resource , subresource context and subresouce Identifier
     *
     * @param resource
     * @param subresourceDefCtx
     * @param subResourceId
     * @return
     */
    public DBSubresource resolveSubResourceIdentifier(DBResource resource, String subresourceDefCtx, Identifier subResourceId) {
        LOG.info("Resolve subResourceIdentifier for doctType [{}] identifier [{}]", subresourceDefCtx, subResourceId);
        Optional<DBSubresource> optSubResource = subresourceDao.getSubResource(subResourceId, resource, subresourceDefCtx);
        return optSubResource.orElse(null);
    }

    public DBResource createNewResource(Identifier resourceId, DBResourceDef resourceDef, DBDomain domain) {
        DBResource resource = new DBResource();
        resource.setIdentifierValue(resourceId.getValue());
        resource.setIdentifierScheme(resourceId.getScheme());
        resource.setDocument(new DBDocument());
        resource.getDocument().setName(resourceDef.getName());
        resource.getDocument().setMimeType(resourceDef.getMimeType());
        resource.setDomainResourceDef(domainResourceDefDao.getResourceDefConfigurationForDomainAndResourceDef(domain, resourceDef)
                .orElse(null));
        return resource;
    }

    public DBSubresource createNewSubResource(Identifier resourceId, DBResource resource, DBSubresourceDef subresourceDef) {
        DBSubresource subresource = new DBSubresource();
        subresource.setIdentifierValue(resourceId.getValue());
        subresource.setIdentifierScheme(resourceId.getScheme());
        subresource.setResource(resource);
        subresource.setSubresourceDef(subresourceDef);
        subresource.setDocument(new DBDocument());
        subresource.getDocument().setName(subresourceDef.getName());
        subresource.getDocument().setMimeType(subresourceDef.getMimeType());
        return subresource;
    }

    public DBSubresourceDef getSubresource(DBResourceDef resourceDef, String urlPathSegment) {
        return resourceDef.getSubresources()
                .stream()
                .filter(subresourceDef -> StringUtils.equals(subresourceDef.getUrlSegment(), urlPathSegment))
                .findFirst().orElseThrow(() -> new SMPRuntimeException(ErrorCode.INVALID_REQUEST,
                        urlPathSegment, "Subresource [" + urlPathSegment + "] does not exist for resource type [" + resourceDef.getName() + "]"));
    }

    public void validateResourceIdentifier(Identifier identifier) {
        LOG.debug("Validate resource identifier: [{}]", identifier);
        if (configurationService.getParticipantSchemeMandatory() && StringUtils.isBlank(identifier.getScheme())) {
            throw new SMPRuntimeException(SML_INVALID_IDENTIFIER, identifier.getValue());
        }
    }

    public String getUsername(UserDetails user) {
        return user == null ? "Anonymous" : user.getUsername();
    }
}
