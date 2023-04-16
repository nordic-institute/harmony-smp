package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.dao.DomainResourceDefDao;
import eu.europa.ec.edelivery.smp.data.dao.GroupDao;
import eu.europa.ec.edelivery.smp.data.dao.ResourceDao;
import eu.europa.ec.edelivery.smp.data.dao.ResourceDefDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomainResourceDef;
import eu.europa.ec.edelivery.smp.data.model.DBGroup;
import eu.europa.ec.edelivery.smp.data.model.doc.DBDocument;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResourceFilter;
import eu.europa.ec.edelivery.smp.data.model.ext.DBResourceDef;
import eu.europa.ec.edelivery.smp.data.ui.ResourceRO;
import eu.europa.ec.edelivery.smp.data.ui.ServiceResult;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.sml.SmlConnector;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */

@Service
public class UIResourceService {
    private static final String ACTION_RESOURCE_LIST = "GetResourceListForGroup";
    private static final String ACTION_RESOURCE_CREATE = "CreateResourceForGroup";
    private static final String ACTION_RESOURCE_DELETE = "DeleteResourceFromGroup";
    private static final String ACTION_RESOURCE_UPDATE = "UpdateResource";

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIResourceService.class);


    private final ResourceDao resourceDao;
    private final GroupDao groupDao;
    private final ResourceDefDao resourceDefDao;
    private final DomainResourceDefDao domainResourceDefDao;
    private final ConversionService conversionService;
    private final SmlConnector smlConnector;

    public UIResourceService(ResourceDao resourceDao, ResourceDefDao resourceDefDao, DomainResourceDefDao domainResourceDefDao, GroupDao groupDao, ConversionService conversionService, SmlConnector smlConnector) {
        this.resourceDao = resourceDao;
        this.resourceDefDao = resourceDefDao;
        this.domainResourceDefDao = domainResourceDefDao;
        this.groupDao = groupDao;
        this.conversionService = conversionService;
        this.smlConnector = smlConnector;
    }


    @Transactional
    public ServiceResult<ResourceRO> getGroupResources(Long groupId, int page, int pageSize, String filterValue) {

        DBGroup group = groupDao.find(groupId);
        if (group == null) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, ACTION_RESOURCE_LIST, "Group does not exist!");
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
        List<ResourceRO> resourceROS = resources.stream().map(resource -> conversionService.convert(resource, ResourceRO.class)).collect(Collectors.toList());
        resourceDao.getResourcesForFilter(page, pageSize, filter);
        result.getServiceEntities().addAll(resourceROS);
        return result;
    }

    @Transactional
    public ResourceRO deleteResourceFromGroup(Long resourceId, Long groupId,  Long domainId) {
        DBResource resource = resourceDao.find(resourceId);
        if (resource == null) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST,ACTION_RESOURCE_DELETE, "Resource does not exist!");
        }
        if (!Objects.equals(resource.getGroup().getId(), groupId)) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, ACTION_RESOURCE_DELETE, "Resource does not belong to the group!");
        }
        if (!Objects.equals(resource.getGroup().getDomain().getId(), domainId)) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, ACTION_RESOURCE_CREATE, "Group does not belong to the given domain!");
        }

        resourceDao.remove(resource);
        return conversionService.convert(resource, ResourceRO.class);
    }

    @Transactional
    public ResourceRO createResourceForGroup(ResourceRO resourceRO, Long groupId, Long domainId) {

        DBGroup group = groupDao.find(groupId);
        if (group == null) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, ACTION_RESOURCE_CREATE, "Group does not exist!");
        }

        if (!Objects.equals(group.getDomain().getId(), domainId)) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, ACTION_RESOURCE_CREATE, "Group does not belong to the given domain!");
        }

        Optional<DBResourceDef> optRedef = resourceDefDao.getResourceDefByIdentifier(resourceRO.getResourceTypeIdentifier());
        if (!optRedef.isPresent()) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, ACTION_RESOURCE_CREATE, "Resource definition [" + resourceRO.getResourceTypeIdentifier() + "] does not exist!");
        }

        Optional<DBDomainResourceDef> optDoredef = domainResourceDefDao.getResourceDefConfigurationForDomainAndResourceDef(group.getDomain(), optRedef.get());
        if (!optDoredef.isPresent()) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, ACTION_RESOURCE_CREATE, "Resource definition [" + resourceRO.getResourceTypeIdentifier() + "] is not registered for domain!");
        }

        Optional<DBResource> existResource = resourceDao.getResource(resourceRO.getIdentifierValue(), resourceRO.getIdentifierScheme(), optRedef.get(), group.getDomain());
        if (existResource.isPresent()) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, ACTION_RESOURCE_CREATE, "Resource definition [val:" + resourceRO.getIdentifierValue() + " scheme:" + resourceRO.getIdentifierScheme() + "] already exists for domain!");
        }
        DBResource resource = new DBResource();
        resource.setIdentifierScheme(resourceRO.getIdentifierScheme());
        resource.setIdentifierValue(resourceRO.getIdentifierValue());
        resource.setVisibility(resourceRO.getVisibility());
        resource.setGroup(group);
        resource.setDomainResourceDef(optDoredef.get());
        DBDocument document = createDocumentForResourceDef(optRedef.get());
        resource.setDocument(document);
        resourceDao.persist(resource);
        return conversionService.convert(resource, ResourceRO.class);
    }

    @Transactional
    public ResourceRO updateResourceForGroup(ResourceRO resourceRO,  Long resourceId, Long groupId, Long domainId) {

        DBGroup group = groupDao.find(groupId);
        if (group == null) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, ACTION_RESOURCE_UPDATE, "Group does not exist!");
        }

        if (!Objects.equals(group.getDomain().getId(), domainId)) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, ACTION_RESOURCE_UPDATE, "Group does not belong to the given domain!");
        }

        Optional<DBResourceDef> optRedef = resourceDefDao.getResourceDefByIdentifier(resourceRO.getResourceTypeIdentifier());
        if (!optRedef.isPresent()) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, ACTION_RESOURCE_UPDATE, "Resource definition [" + resourceRO.getResourceTypeIdentifier() + "] does not exist!");
        }

        Optional<DBDomainResourceDef> optDoredef = domainResourceDefDao.getResourceDefConfigurationForDomainAndResourceDef(group.getDomain(), optRedef.get());
        if (!optDoredef.isPresent()) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, ACTION_RESOURCE_UPDATE, "Resource definition [" + resourceRO.getResourceTypeIdentifier() + "] is not registered for domain!");
        }

        // at the moment only visibility can be updated for the resource
        DBResource resource =resourceDao.find(resourceId);
        resource.setVisibility(resourceRO.getVisibility());
        return conversionService.convert(resource, ResourceRO.class);
    }

    public DBDocument createDocumentForResourceDef(DBResourceDef resourceDef) {
        DBDocument document = new DBDocument();
        document.setCurrentVersion(1);
        document.setMimeType(resourceDef.getMimeType());
        document.setName(resourceDef.getName());
        return document;
    }

}
