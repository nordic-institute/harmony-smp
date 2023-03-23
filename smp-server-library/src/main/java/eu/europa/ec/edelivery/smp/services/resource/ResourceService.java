package eu.europa.ec.edelivery.smp.services.resource;


import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;
import eu.europa.ec.edelivery.smp.data.dao.SubresourceDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.ext.DBResourceDef;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.servlet.ResourceAction;
import eu.europa.ec.edelivery.smp.servlet.ResourceRequest;
import eu.europa.ec.edelivery.smp.servlet.ResourceResponse;
import eu.europa.ec.smp.spi.resource.ResourceDefinitionSpi;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

/**
 *
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

    public ResourceService(List<ResourceDefinitionSpi> resourceDefinitionSpiList,
                           ResourceResolverService resolverService,
                           ResourceHandlerService resourceHandlerService,
                           SubresourceDao subresourceDao) {
        this.resourceDefinitionSpiList = resourceDefinitionSpiList;
        this.resolverService = resolverService;
        this.resourceHandlerService = resourceHandlerService;
        this.subresourceDao = subresourceDao;
    }

    public void handleRequest(SMPUserDetails user,
                              ResourceRequest resourceRequest,
                              ResourceResponse resourceResponse ) {
        LOG.info("Handle request [{}] for user: [{}]", user, resourceRequest);

        ResolvedData data = resolverService.resolveAndAuthorizeRequest(user, resourceRequest);
        resourceRequest.setResolvedData(data);

        if (data.getSubresource() == null) {
            handleResourceForAction( resourceRequest, resourceResponse);
        } else {
            handleSubresourceForAction(resourceRequest, resourceResponse);
        }
    }

    /**
     * Method handles the action (read, update, create, delete) for the  resource and user. The response is "written" to output stream
     *
     * @param resourceRequest a resolved ResourceRequest with Resource and Resource definition entities
     * @param resourceResponse object to write the response of the request action
     */
    public void handleResourceForAction(ResourceRequest resourceRequest,
                                        ResourceResponse resourceResponse) {
        LOG.info("Handle ResourceRequest [{}] for user  [{}]",
                resourceRequest, resourceResponse);
        switch (resourceRequest.getAction()) {
            case READ:
                resourceHandlerService.readResource(resourceRequest, resourceResponse);
                break;
            case CREATE_UPDATE:
                resourceHandlerService.createResource(resourceRequest, resourceResponse);
                break;
            case DELETE:
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
                //handlerSpi.storeResource();
                //handlerSpi.storeResource(); */
                break;
            case DELETE:
                break;
        }


    }


}
