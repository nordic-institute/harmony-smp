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
package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.dao.*;
import eu.europa.ec.edelivery.smp.data.enums.DocumentVersionStatusType;
import eu.europa.ec.edelivery.smp.data.enums.EventSourceType;
import eu.europa.ec.edelivery.smp.data.enums.MembershipRoleType;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBDomainResourceDef;
import eu.europa.ec.edelivery.smp.data.model.DBGroup;
import eu.europa.ec.edelivery.smp.data.model.doc.*;
import eu.europa.ec.edelivery.smp.data.model.ext.DBResourceDef;
import eu.europa.ec.edelivery.smp.data.model.user.DBResourceMember;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.data.ui.DocumentReferenceInfoRO;
import eu.europa.ec.edelivery.smp.data.ui.MemberRO;
import eu.europa.ec.edelivery.smp.data.ui.ResourceRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import eu.europa.ec.edelivery.smp.exceptions.ErrorMessageArgument;
import eu.europa.ec.edelivery.smp.exceptions.ErrorMessageType;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.identifiers.Identifier;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.IdentifierService;
import eu.europa.ec.edelivery.smp.services.SMLIntegrationService;
import eu.europa.ec.edelivery.smp.services.SMPExceptionLanguageService;
import eu.europa.ec.edelivery.smp.services.mail.DocumentMailService;
import eu.europa.ec.edelivery.smp.services.mail.prop.MailDocumentActionType;
import eu.europa.ec.edelivery.smp.services.resource.DocumentVersionService;
import eu.europa.ec.edelivery.smp.utils.LocaleUtils;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */

@Service
public class UIResourceService {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIResourceService.class);

    private final ResourceDao resourceDao;

    private final GroupDao groupDao;
    private final ResourceMemberDao resourceMemberDao;
    private final UserDao userDao;
    private final DocumentDao documentDao;
    private final ResourceDefDao resourceDefDao;
    private final DomainResourceDefDao domainResourceDefDao;
    private final IdentifierService identifierService;
    private final ConversionService conversionService;
    private final SMLIntegrationService smlIntegrationService;
    private final UIDocumentService uiDocumentService;
    private final DocumentVersionService documentVersionService;
    private final DocumentMailService documentMailService;
    private final SMPExceptionLanguageService smpExceptionLanguageService;

    public UIResourceService(ResourceDao resourceDao,
                             ResourceMemberDao resourceMemberDao,
                             ResourceDefDao resourceDefDao,
                             DocumentDao documentDao,
                             DomainResourceDefDao domainResourceDefDao, UserDao userDao, GroupDao groupDao,
                             IdentifierService identifierService,
                             ConversionService conversionService,
                             SMLIntegrationService smlIntegrationService,
                             UIDocumentService uiDocumentService,
                             DocumentVersionService documentVersionService,
                             DocumentMailService documentMailService,
                             SMPExceptionLanguageService smpExceptionLanguageService) {
        this.resourceDao = resourceDao;
        this.resourceMemberDao = resourceMemberDao;
        this.resourceDefDao = resourceDefDao;
        this.documentDao = documentDao;
        this.domainResourceDefDao = domainResourceDefDao;
        this.groupDao = groupDao;
        this.userDao = userDao;
        this.identifierService = identifierService;
        this.conversionService = conversionService;
        this.smlIntegrationService = smlIntegrationService;
        this.uiDocumentService = uiDocumentService;
        this.documentVersionService = documentVersionService;
        this.documentMailService = documentMailService;
        this.smpExceptionLanguageService = smpExceptionLanguageService;
    }


    @Transactional
    public ServiceResult<ResourceRO> getGroupResources(Long groupId, int page, int pageSize, String filterValue) {

        DBGroup group = groupDao.find(groupId);
        if (group == null) {
            throw new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_RESOURCE_LIST_GROUP_NOT_EXISTS);
        }

        DBResourceFilter filter = DBResourceFilter.createBuilder()
                .group(group)
                .identifierFilter(StringUtils.trimToNull(filterValue))
                .build();

        Long count = resourceDao.getResourcesForFilterCount(filter);

        ServiceResult<ResourceRO> result = new ServiceResult<>();
        result.setPage(page);
        result.setPageSize(pageSize);
        if (count < 1) {
            result.setCount(0L);
            return result;
        }
        result.setCount(count);
        List<DBResource> resources = resourceDao.getResourcesForFilter(page, pageSize, filter);
        List<ResourceRO> resourceROS = resources.stream()
                .map(this::convertResourceWithReferenceData)
                .toList();
        resourceDao.getResourcesForFilter(page, pageSize, filter);
        result.getServiceEntities().addAll(resourceROS);
        return result;
    }


    @Transactional
    public ServiceResult<ResourceRO> getResourcesForUserAndGroup(Long userId, MembershipRoleType role, Long groupId, int page, int pageSize, String filterValue) {

        DBGroup group = groupDao.find(groupId);
        if (group == null) {
            throw new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_RESOURCE_LIST_GROUP_NOT_EXISTS);
        }
        DBUser user = userDao.find(userId);
        if (user == null) {
            throw new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_RESOURCE_LIST_USER_NOT_EXISTS);
        }

        DBResourceFilter filter = DBResourceFilter.createBuilder()
                .user(user)
                .membershipRoleType(role)
                .group(group)
                .identifierFilter(StringUtils.trimToNull(filterValue))
                .build();

        Long count = resourceDao.getResourcesForFilterCount(filter);

        ServiceResult<ResourceRO> result = new ServiceResult<>();
        result.setPage(page);
        result.setPageSize(pageSize);
        if (count < 1) {
            result.setCount(0L);
            return result;
        }
        result.setCount(count);
        List<DBResource> resources = resourceDao.getResourcesForFilter(page, pageSize, filter);
        List<ResourceRO> resourceROS = resources.stream()
                .map(this::convertResourceWithReferenceData)
                .toList();
        result.getServiceEntities().addAll(resourceROS);
        return result;
    }

    @Transactional
    public ResourceRO deleteResourceFromGroup(Long resourceId, Long groupId, Long domainId) {
        DBResource resource = resourceDao.find(resourceId);
        if (resource == null) {
            throw new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_RESOURCE_REMOVE_RESOURCE_NOT_EXISTS);
        }
        if (!Objects.equals(resource.getGroup().getId(), groupId)) {
            throw new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_RESOURCE_REMOVE_RESOURCE_NOT_PART_OF_GROUP);
        }
        if (!Objects.equals(resource.getGroup().getDomain().getId(), domainId)) {
            throw new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_RESOURCE_REMOVE_GROUP_NOT_PART_OF_DOMAIN);
        }
        DBDomain resourceDomain = resource.getGroup().getDomain();
        if (smlIntegrationService.isSMLIntegrationEnabled() &&
                resourceDomain.isSmlRegistered() && resource.isSmlRegistered()) {
            smlIntegrationService.unregisterParticipant(resource, resourceDomain);
        }

        // remove all documents where resource is used as reference
        documentDao.unlinkDocument(resource.getDocument());

        List<DBUser> resourceAdmins = userDao.getResourceAdminUsers(resource);
        resourceDao.remove(resource);
        documentMailService.sendDocumentActionNotification(resource, null, MailDocumentActionType.DELETED,
                resource.getDocument().getCurrentVersion(),
                resource.getDocument().getName(), SessionSecurityUtils.getSessionUserDetails(), resourceAdmins);
        return conversionService.convert(resource, ResourceRO.class);
    }

    @Transactional
    public ResourceRO createResourceForGroup(ResourceRO resourceRO, Long groupId, Long domainId, Long userId) {

        DBGroup group = groupDao.find(groupId);
        if (group == null) {
            throw new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_RESOURCE_CREATE_GROUP_NOT_EXISTS);
        }

        DBDomain domain = group.getDomain();
        if (!Objects.equals(domain.getId(), domainId)) {
            throw new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_RESOURCE_CREATE_GROUP_NOT_PART_OF_DOMAIN);
        }

        Optional<DBResourceDef> optRedef = resourceDefDao.getResourceDefByIdentifier(resourceRO.getResourceTypeIdentifier());
        if (optRedef.isEmpty()) {
            throw new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_RESOURCE_CREATE_RESOURCE_NOT_EXISTS)
                    .addParam(ErrorMessageArgument.IDENTIFIER, resourceRO.getResourceTypeIdentifier());
        }

        Optional<DBDomainResourceDef> optDoredef = domainResourceDefDao.getResourceDefConfigurationForDomainAndResourceDef(group.getDomain(), optRedef.get());
        if (optDoredef.isEmpty()) {
            throw new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_RESOURCE_CREATE_RESOURCE_NOT_PART_OF_DOMAIN)
                    .addParam(ErrorMessageArgument.IDENTIFIER, resourceRO.getResourceTypeIdentifier());
        }
        Identifier resourceIdentifier = identifierService.normalizeParticipant(
                domain.getDomainCode(),
                resourceRO.getIdentifierScheme(),
                resourceRO.getIdentifierValue());
        boolean isResourceIdentifierCaseSensitive = identifierService.isResourceIdentifierCaseSensitive(resourceIdentifier, domain.getDomainCode());

        Optional<DBResource> existResource = resourceDao.getResource(resourceIdentifier.getValue(),
                resourceIdentifier.getScheme(),
                optRedef.get(),
                group.getDomain(),
                isResourceIdentifierCaseSensitive);
        if (existResource.isPresent()) {
            throw new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_RESOURCE_CREATE_RESOURCE_ALREADY_EXISTS)
                    .addParam(ErrorMessageArgument.IDENTIFIER, resourceRO.getIdentifierValue())
                    .addParam(ErrorMessageArgument.SCHEME, resourceRO.getIdentifierScheme());
        }

        DBResource resource = new DBResource();
        resource.setIdentifierScheme(resourceIdentifier.getScheme());
        resource.setIdentifierValue(resourceIdentifier.getValue());
        resource.setVisibility(resourceRO.getVisibility());
        resource.setReviewEnabled(resourceRO.isReviewEnabled());
        resource.setGroup(group);
        resource.setDomainResourceDef(optDoredef.get());
        resource.setReviewEnabled(resourceRO.isReviewEnabled());

        DBDocument document = createDocumentForNewResource(resource);
        resource.setDocument(document);
        resourceDao.persist(resource);
        // create first member as admin user
        DBUser user = userDao.find(userId);
        DBResourceMember dbResourceMember = new DBResourceMember();
        dbResourceMember.setRole(MembershipRoleType.ADMIN);
        dbResourceMember.setResource(resource);
        dbResourceMember.setUser(user);
        resourceMemberDao.persist(dbResourceMember);
        // try to register it to
        DBDomain resourceDomain = resource.getGroup().getDomain();
        if (smlIntegrationService.isSMLIntegrationEnabled() &&
                resourceDomain.isSmlRegistered()) {
            smlIntegrationService.registerParticipant(resource, resourceDomain);
        }

        documentMailService.sendDocumentActionNotification(resource, null, MailDocumentActionType.CREATED,
                document.getCurrentVersion(),
                document.getName(), SessionSecurityUtils.getSessionUserDetails());

        return conversionService.convert(resource, ResourceRO.class);
    }

    /**
     * Method allows Group admin and Resource admin to change resource visibility and enable/disable review flow.
     *
     * @param resourceRO input resource data to update
     * @param resourceId resource id to update
     * @param groupId    group id of the resource
     * @param domainId   domain id of the group
     * @return updated resource RO
     */
    @Transactional
    public ResourceRO updateResourceForGroup(ResourceRO resourceRO, Long resourceId, Long groupId, Long domainId) {

        DBGroup group = groupDao.find(groupId);
        if (group == null) {
            throw new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_RESOURCE_UPDATE_GROUP_NOT_EXISTS);
        }

        if (!Objects.equals(group.getDomain().getId(), domainId)) {
            throw new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_RESOURCE_UPDATE_GROUP_NOT_PART_OF_DOMAIN);
        }

        Optional<DBResourceDef> optRedef = resourceDefDao.getResourceDefByIdentifier(resourceRO.getResourceTypeIdentifier());
        if (optRedef.isEmpty()) {
            throw new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_RESOURCE_UPDATE_GROUP_RESOURCE_NOT_EXISTS)
                    .addParam(ErrorMessageArgument.IDENTIFIER, resourceRO.getResourceTypeIdentifier());
        }

        Optional<DBDomainResourceDef> optDoredef = domainResourceDefDao.getResourceDefConfigurationForDomainAndResourceDef(group.getDomain(), optRedef.get());
        if (optDoredef.isEmpty()) {
            throw new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_RESOURCE_UPDATE_GROUP_RESOURCE_NOT_PART_OF_DOMAIN)
                    .addParam(ErrorMessageArgument.IDENTIFIER, resourceRO.getResourceTypeIdentifier());
        }

        // at the moment only visibility and review enabled
        // can be updated for the resource
        DBResource resource = resourceDao.find(resourceId);
        resource.setVisibility(resourceRO.getVisibility());
        if (resourceRO.isReviewEnabled() != null) {
            boolean newValue = isTrue(resourceRO.isReviewEnabled());
            boolean oldValue = isTrue(resource.isReviewEnabled());
            // update resource review enabled in case if it was null before
            resource.setReviewEnabled(newValue);
            // check if new status is disabled  and changed
            if (oldValue != newValue && !newValue) {
                // update all document versions to non review status
                uiDocumentService.updateToNonReviewStatuses(resource.getDocument());
                // update statuses for all subresources
                resource.getSubresources().forEach(subResource ->
                        uiDocumentService.updateToNonReviewStatuses(subResource.getDocument()));
            }
            resource.setReviewEnabled(isTrue(resourceRO.isReviewEnabled()));
        }
        ResourceRO resourceROResult = conversionService.convert(resource, ResourceRO.class);
        if (StringUtils.isNotBlank(resourceRO.getResourceId()) && resourceROResult != null) {
            // return the same encrypted id so the UI can use update old resource
            resourceROResult.setResourceId(resourceRO.getResourceId());
        }
        return resourceROResult;
    }

    @Transactional
    public ServiceResult<MemberRO> getResourceMembers(Long resourceId, Long groupId, int page, int pageSize,
                                                      String filter) {

        validateGroupAndResource(resourceId, groupId,
                ErrorMessageType.INVALID_REQUEST_RESOURCE_MEMBERSHIP_GET_MEMBERS_RESOURCE_NOT_EXISTS,
                ErrorMessageType.INVALID_REQUEST_RESOURCE_MEMBERSHIP_GET_MEMBERS_GROUP_NOT_PART_OF_DOMAIN);
        Long count = resourceMemberDao.getResourceMemberCount(resourceId, filter);
        ServiceResult<MemberRO> result = new ServiceResult<>();
        result.setPage(page);
        result.setPageSize(pageSize);
        if (count < 1) {
            result.setCount(0L);
            return result;
        }
        result.setCount(count);
        List<DBResourceMember> memberROS = resourceMemberDao.getResourceMembers(resourceId, page, pageSize, filter);
        List<MemberRO> memberList = memberROS.stream().map(member -> conversionService.convert(member, MemberRO.class)).toList();

        result.getServiceEntities().addAll(memberList);
        return result;
    }

    /**
     * Add or update a member to a resource
     *
     * @param resourceId resource id to add member to
     * @param groupId    group id to add member to
     * @param memberRO   member data
     * @param memberId   member id (optional) if null then add member, if not null then update member
     * @return added member RO
     */
    @Transactional
    public MemberRO addUpdateMemberToResource(Long resourceId, Long groupId, MemberRO memberRO, Long memberId) {
        LOG.info("Add member [{}] to resource [{}]", memberRO.getUsername(), resourceId);
        validateGroupAndResource(resourceId, groupId,
                ErrorMessageType.INVALID_REQUEST_RESOURCE_MEMBERSHIP_ADD_MEMBER_RESOURCE_NOT_EXISTS,
                ErrorMessageType.INVALID_REQUEST_RESOURCE_MEMBERSHIP_ADD_MEMBER_GROUP_NOT_PART_OF_DOMAIN);

        DBUser user = userDao.findUserByUsername(memberRO.getUsername())
                .orElseThrow(() -> new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_RESOURCE_MEMBERSHIP_ADD_USER_NOT_EXISTS)
                        .addParam(ErrorMessageArgument.USERNAME, memberRO.getUsername()));

        DBResourceMember member;
        if (memberId != null) {
            member = resourceMemberDao.find(memberId);
            member.setRole(memberRO.getRoleType());
            member.setHasPermissionToReview(memberRO.getHasPermissionReview());
        } else {
            DBResource resource = resourceDao.find(resourceId);
            if (resourceMemberDao.isUserResourceMember(user, resource)) {
                throw new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_RESOURCE_MEMBERSHIP_ADD_USER_ALREADY_MEMBER)
                        .addParam(ErrorMessageArgument.USERNAME, memberRO.getUsername());
            }
            member = resourceMemberDao.addMemberToResource(resource, user,
                    memberRO.getRoleType(),
                    isTrue(memberRO.getHasPermissionReview())
            );
        }
        return conversionService.convert(member, MemberRO.class);
    }

    @Transactional
    public MemberRO deleteMemberFromResource(Long resourceId, Long groupId, Long memberId) {
        LOG.info("Delete member [{}] from resource [{}]", memberId, resourceId);
        validateGroupAndResource(resourceId, groupId,
                ErrorMessageType.INVALID_REQUEST_RESOURCE_MEMBERSHIP_REMOVE_MEMBER_RESOURCE_NOT_EXISTS,
                ErrorMessageType.INVALID_REQUEST_RESOURCE_MEMBERSHIP_REMOVE_MEMBER_GROUP_NOT_PART_OF_DOMAIN);
        DBResourceMember resourceMember = resourceMemberDao.find(memberId);
        if (resourceMember == null) {
            throw new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_RESOURCE_MEMBERSHIP_REMOVE_USER_NOT_MEMBER);
        }
        if (!Objects.equals(resourceMember.getResource().getId(), resourceId)) {
            throw new SMPRuntimeException(ErrorMessageType.INVALID_REQUEST_RESOURCE_MEMBERSHIP_REMOVE_USER_NOT_PART_OF_RESOURCE);
        }

        resourceMemberDao.remove(resourceMember);
        return conversionService.convert(resourceMember, MemberRO.class);
    }

    public void validateGroupAndResource(Long resourceId, Long groupId, ErrorMessageType nonexistentResourceTranslationMessageCode, ErrorMessageType groupNotPartOfDomainTranslationMessageCode) {
        DBResource resource = resourceDao.find(resourceId);
        if (resource == null) {
            throw new SMPRuntimeException(nonexistentResourceTranslationMessageCode);
        }
        if (!Objects.equals(groupId, resource.getGroup().getId())) {
            throw new SMPRuntimeException(groupNotPartOfDomainTranslationMessageCode);
        }
    }

    /**
     * Create document for new resource. Method is called when GroupAdmin creates new resource via
     * UI.
     *
     * @param resource resource to create document for
     * @return created document
     */
    public DBDocument createDocumentForNewResource(DBResource resource) {
        DBResourceDef domainResourceDef = resource.getDomainResourceDef().getResourceDef();
        DBDocument document = new DBDocument();

        document.setMimeType(domainResourceDef.getMimeType());
        document.setName(StringUtils.left(resource.getIdentifierValue(), 255));
        // create first version of the document
        DBDocumentVersion version = documentVersionService.initializeDocumentVersionByGroupAdmin(EventSourceType.UI);
        // The first version is always published.
        version.setStatus(DocumentVersionStatusType.PUBLISHED);
        version.setDocument(document);
        version.setVersion(1);
        document.setCurrentVersion(1);
        // generate document content
        document.addNewDocumentVersion(version);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        uiDocumentService.generateDocumentForResource(resource, baos);
        version.setContent(baos.toByteArray());
        return document;
    }

    private ResourceRO convertResourceWithReferenceData(DBResource resource) {
        ResourceRO resourceRO = conversionService.convert(resource, ResourceRO.class);
        DBDocumentReferenceData docRefData = resourceDao.getDocumentReferenceData(resource);
        if (docRefData != null && resourceRO != null) {
            DocumentReferenceInfoRO docRefInfo = new DocumentReferenceInfoRO();
            docRefInfo.setReferencedByCount(docRefData.getReferencedByCount());
            docRefInfo.setReferencedDocumentExists(docRefData.getReferencedDocumentId() != null);
            docRefInfo.setReferenceUrlPath(docRefData.getReferenceUrlPath());
            docRefInfo.setSharingEnabled(docRefData.isSharingEnabled());
            resourceRO.setDocumentReferenceInfo(docRefInfo);
            if (StringUtils.isNotBlank(docRefData.getReferenceUrlPath()) && docRefData.getReferencedDocumentId() == null) {
                resourceRO.setStatus(EntityROStatus.ERROR.getStatusNumber());
                String currentLocale = LocaleUtils.getCurrentLocale();
                resourceRO.setStatusMessage(this.smpExceptionLanguageService
                        .getMessageTranslation(ErrorMessageType.UI_RESOURCE_INVALID_REFERENCE.getMessageCode(), currentLocale)
                );
            }
        }
        return resourceRO;
    }
}
