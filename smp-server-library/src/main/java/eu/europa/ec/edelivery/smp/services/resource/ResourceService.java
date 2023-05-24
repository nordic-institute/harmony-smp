package eu.europa.ec.edelivery.smp.services.resource;


import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;
import eu.europa.ec.edelivery.smp.data.dao.SubresourceDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.servlet.ResourceRequest;
import eu.europa.ec.edelivery.smp.servlet.ResourceResponse;
import eu.europa.ec.edelivery.text.DistinguishedNamesCodingUtil;
import eu.europa.ec.smp.spi.resource.ResourceDefinitionSpi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.INVALID_OWNER;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Service
public class ResourceService {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ResourceService.class);

    final List<ResourceDefinitionSpi> resourceDefinitionSpiList;
    final ResourceResolverService resolverService;
    final ResourceHandlerService resourceHandlerService;

    final SubresourceDao subresourceDao;
    final UserDao userDao;


    public ResourceService(List<ResourceDefinitionSpi> resourceDefinitionSpiList,
                           ResourceResolverService resolverService,
                           ResourceHandlerService resourceHandlerService,
                           SubresourceDao subresourceDao,
                           UserDao userDao) {
        this.resourceDefinitionSpiList = resourceDefinitionSpiList;
        this.resolverService = resolverService;
        this.resourceHandlerService = resourceHandlerService;
        this.subresourceDao = subresourceDao;
        this.userDao = userDao;
    }

    /**
     * Method resolves domain, document type, resource and if given subresource type and subresource objects. It validates
     * the authorization for the action and target object and hadles the action
     *
     * @param user             the requestor of the action for the object
     * @param resourceRequest  resource request data
     * @param resourceResponse resource response object
     */
    public void handleRequest(SMPUserDetails user,
                              ResourceRequest resourceRequest,
                              ResourceResponse resourceResponse) {
        LOG.info("Handle request [{}] for user: [{}]", user, resourceRequest);

        ResolvedData data = resolverService.resolveAndAuthorizeRequest(user, resourceRequest);
        resourceRequest.setResolvedData(data);

        if (data.getSubresource() == null) {
            handleResourceForAction(user, resourceRequest, resourceResponse);
        } else {
            handleSubresourceForAction(resourceRequest, resourceResponse);
        }
    }

    /**
     * Method handles the action (read, update, create, delete) for the  resource and user. The response is "written" to output stream
     *
     * @param resourceRequest  a resolved ResourceRequest with Resource and Resource definition entities
     * @param resourceResponse object to write the response of the request action
     */
    public void handleResourceForAction(SMPUserDetails user, ResourceRequest resourceRequest,
                                        ResourceResponse resourceResponse) {
        LOG.info("Handle ResourceRequest [{}] for user  [{}]",
                resourceRequest, resourceResponse);
        switch (resourceRequest.getAction()) {
            case READ:
                resourceHandlerService.readResource(resourceRequest, resourceResponse);
                break;
            case CREATE_UPDATE:
                createOrUpdateResource(user, resourceRequest, resourceResponse);
                break;
            case DELETE:
                resourceHandlerService.deleteResource(resourceRequest, resourceResponse);
                break;
        }
    }

    public void handleSubresourceForAction(ResourceRequest resourceRequest,
                                           ResourceResponse resourceResponse) {
        LOG.info("Handle SubresourceRequest [{}] for user  [{}]", resourceRequest, resourceResponse);
        switch (resourceRequest.getAction()) {
            case READ:
                resourceHandlerService.readSubresource(resourceRequest, resourceResponse);
                break;
            case CREATE_UPDATE:
                resourceHandlerService.createSubresource(resourceRequest, resourceResponse);
                break;
            case DELETE:
                resourceHandlerService.deleteSubresource(resourceRequest, resourceResponse);
                break;
        }
    }

    public void createOrUpdateResource(SMPUserDetails user, ResourceRequest resourceRequest,
                                       ResourceResponse resourceResponse) {
        LOG.debug("Validate owner for CREATE_UPDATE!");
        DBUser ownerUser = user.getUser();
        ResolvedData resolvedData = resourceRequest.getResolvedData();
        boolean isNewResource = resolvedData.getResource().getId() == null;
        String owner = resourceRequest.getOwnerHttpParameter();

        LOG.debug("Resource is new [{}] and owner header is [{}]", isNewResource, owner);
        // the owner can be set via http owner parameter
        if (isNewResource && isNotBlank(owner)) {
            ownerUser = findOwner(owner);
        } else if (isNotBlank(owner)) {
            LOG.warn("Owner [{}] is given for existing resource [{}]. The owner parameter is ignored!", owner, resolvedData.getResource());
        }
        resourceHandlerService.createResource(ownerUser, resourceRequest, resourceResponse);
    }

    protected DBUser findOwner(final String ownerName) {
        Optional<DBUser> optOwnerUser = userDao.findUserByIdentifier(ownerName);
        // if user still not present
        if (!optOwnerUser.isPresent()
                && !StringUtils.isBlank(ownerName) && ownerName.contains(":")) {
            // try harder
            String[] val = splitSerialFromSubject(ownerName);
            String newOwnerName = DistinguishedNamesCodingUtil.normalizeDN(val[0]) + ':' + val[1];
            LOG.info("Owner not found: [{}] try with normalized owner: [{}].", ownerName, newOwnerName);
            optOwnerUser = userDao.findUserByIdentifier(newOwnerName);
        }

        return optOwnerUser.orElseThrow(
                () -> new SMPRuntimeException(ErrorCode.INVALID_OWNER, ownerName));
    }

    public static String[] splitSerialFromSubject(String certificateId) {
        int idx = certificateId.lastIndexOf(":");
        if (idx <= 0) {
            throw new SMPRuntimeException(INVALID_OWNER, certificateId);
        }
        return new String[]{certificateId.substring(0, idx), certificateId.substring(idx + 1)};

    }

}
