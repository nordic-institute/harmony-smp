package eu.europa.ec.edelivery.smp.services.resource;


import eu.europa.ec.edelivery.smp.data.dao.ResourceMemberDao;
import eu.europa.ec.edelivery.smp.data.model.doc.DBDocument;
import eu.europa.ec.edelivery.smp.data.model.doc.DBDocumentVersion;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
import eu.europa.ec.edelivery.smp.data.model.user.DBUser;
import eu.europa.ec.edelivery.smp.exceptions.BadRequestException;
import eu.europa.ec.edelivery.smp.exceptions.ErrorBusinessCode;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.spi.data.SpiResponseData;
import eu.europa.ec.edelivery.smp.servlet.ResourceRequest;
import eu.europa.ec.edelivery.smp.servlet.ResourceResponse;
import eu.europa.ec.smp.spi.api.model.RequestData;
import eu.europa.ec.smp.spi.api.model.ResponseData;
import eu.europa.ec.smp.spi.exceptions.ResourceException;
import eu.europa.ec.smp.spi.resource.ResourceDefinitionSpi;
import eu.europa.ec.smp.spi.resource.ResourceHandlerSpi;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.util.List;

import static eu.europa.ec.edelivery.smp.servlet.WebConstants.HTTP_RESPONSE_CODE_CREATED;
import static eu.europa.ec.edelivery.smp.servlet.WebConstants.HTTP_RESPONSE_CODE_UPDATED;

/**
 * The class handles the resource actions
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Service
public class ResourceHandlerService extends AbstractResourceHandler {
    protected static final SMPLogger LOG = SMPLoggerFactory.getLogger(ResourceHandlerService.class);

    final ResourceMemberDao resourceMemberDao;

    public ResourceHandlerService(List<ResourceDefinitionSpi> resourceDefinitionSpiList, ResourceStorage resourceStorage,
                                  ResourceMemberDao resourceMemberDao) {
        super(resourceDefinitionSpiList, resourceStorage);
        this.resourceMemberDao = resourceMemberDao;
    }

    public void readResource(ResourceRequest resourceRequest,
                             ResourceResponse resourceResponse) {

        LOG.debug("Handle the READ action for resource request [{}]", resourceRequest);
        ResolvedData resolvedData = resourceRequest.getResolvedData();
        ResourceHandlerSpi handlerSpi = getResourceHandler(resolvedData.getResourceDef());
        // set default mimetype - it can be overwritten by handler
        resourceResponse.setContentType(resolvedData.getResourceDef().getMimeType());

        RequestData requestData = buildRequestDataForResource(resolvedData.getDomain(), resolvedData.getResource());
        ResponseData responseData = new SpiResponseData(resourceResponse.getOutputStream());
        // get resource byte array

        handleReadResource(handlerSpi, requestData, responseData, resourceResponse);

    }

    @Transactional
    public void readSubresource(ResourceRequest resourceRequest,
                                ResourceResponse resourceResponse) {

        LOG.debug("Handle the READ action for subresource request [{}]", resourceRequest);
        ResolvedData resolvedData = resourceRequest.getResolvedData();
        DBSubresource resolvedSubresource = resolvedData.getSubresource();
        // set default mimetype - it can be overwritten by handler
        resourceResponse.setContentType(resolvedSubresource.getSubresourceDef().getMimeType());

        ResourceHandlerSpi handlerSpi = getSubresourceHandler(resolvedSubresource.getSubresourceDef(), resolvedData.getResourceDef());
        // generate request and respond
        RequestData requestData = buildRequestDataForSubResource(resolvedData.getDomain(), resolvedData.getResource(), resolvedData.getSubresource());
        ResponseData responseData = new SpiResponseData(resourceResponse.getOutputStream());
        // handle data
        handleReadResource(handlerSpi, requestData, responseData, resourceResponse);
    }

    @Transactional
    public void createResource(DBUser user, ResourceRequest resourceRequest,
                               ResourceResponse resourceResponse) {

        LOG.debug("Handle the CREATE action for resource request [{}]", resourceRequest);
        // locate the resource handler

        ResolvedData resolvedData = resourceRequest.getResolvedData();
        DBResource resource = resolvedData.getResource();
        ResourceHandlerSpi handlerSpi = getResourceHandler(resolvedData.getResourceDef());

        boolean isNewResource = resource.getId() == null;

        RequestData requestData = buildRequestDataForResource(resolvedData.getDomain(),
                resource, resourceRequest.getInputStream());

        // write to response data and save the request
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResponseData responseData = new SpiResponseData(baos);

        try {
            handlerSpi.storeResource(requestData, responseData);
            if (StringUtils.isNotBlank(responseData.getContentType())) {
                resourceResponse.setContentType(responseData.getContentType());
            }
        } catch (ResourceException e) {
            switch (e.getErrorCode()) {
                case INVALID_PARAMETERS:
                    throw new BadRequestException(ErrorBusinessCode.WRONG_FIELD, e.getMessage());
                case INVALID_RESOURCE:
                    throw new SMPRuntimeException(ErrorCode.INVALID_EXTENSION_FOR_SG, resource.getIdentifierValue(),
                            resource.getIdentifierScheme(),
                            e.getMessage());
                default:
                    throw new SMPRuntimeException(ErrorCode.INTERNAL_ERROR, "Error occurred while reading the resource!", e);
            }
        }
        // set headers to response
        responseData.getHttpHeaders().entrySet().stream()
                .forEach(entry -> resourceResponse.setHttpHeader(entry.getKey(), entry.getValue()));
        // determinate status before resource is stored to database!
        resourceResponse.setHttpStatus(getHttpStatusForCreateUpdate(isNewResource, responseData));

        if (resource.getDocument() == null) {
            resource.setDocument(new DBDocument());
            // set response data
            resource.getDocument().setName(resolvedData.getResourceDef().getName());
            resource.getDocument().setMimeType(StringUtils.getIfEmpty(responseData.getContentType(),
                    () -> resolvedData.getResourceDef().getMimeType()));
        }
        // create new document version
        DBDocumentVersion documentVersion = new DBDocumentVersion();
        documentVersion.setContent(baos.toByteArray());
        DBResource managedResource = resourceStorage.addDocumentVersionForResource(resource, documentVersion);

        if (isNewResource) {
            resourceRequest.getOwnerHttpParameter();
            resourceMemberDao.setAdminMemberShip(user, managedResource);
        }
    }

    @Transactional
    public void createSubresource(ResourceRequest resourceRequest,
                                  ResourceResponse resourceResponse) {

        LOG.debug("Handle the CREATE action for resource request [{}]", resourceRequest);

        // locate the resource handler
        ResolvedData resolvedData = resourceRequest.getResolvedData();

        DBSubresource resolvedSubresource = resolvedData.getSubresource();
        boolean isNewResource = resolvedSubresource.getId() == null;
        ResourceHandlerSpi handlerSpi = getSubresourceHandler(resolvedSubresource.getSubresourceDef(), resolvedData.getResourceDef());
        // generate request and respond
        RequestData requestData = buildRequestDataForSubResource(resolvedData.getDomain(),
                resolvedData.getResource(),
                resolvedData.getSubresource(),
                resourceRequest.getInputStream());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResponseData responseData = new SpiResponseData(baos);

        try {
            handlerSpi.storeResource(requestData, responseData);
            if (StringUtils.isNotBlank(responseData.getContentType())) {
                resourceResponse.setContentType(responseData.getContentType());
            }
        } catch (ResourceException e) {
            switch (e.getErrorCode()) {
                case INVALID_PARAMETERS:
                    throw new BadRequestException(ErrorBusinessCode.WRONG_FIELD, ExceptionUtils.getRootCauseMessage(e));
                case INVALID_RESOURCE:
                    throw new SMPRuntimeException(ErrorCode.INVALID_SMD_XML,
                            ExceptionUtils.getRootCauseMessage(e));
                default:
                    throw new SMPRuntimeException(ErrorCode.INTERNAL_ERROR, "Error occurred while reading the subresource!", e);
            }
        }
        // set headers to response
        responseData.getHttpHeaders().entrySet().stream()
                .forEach(entry -> resourceResponse.setHttpHeader(entry.getKey(), entry.getValue()));
        // determinate status before resource is stored to database!
        resourceResponse.setHttpStatus(getHttpStatusForCreateUpdate(isNewResource, responseData));

        if (resolvedSubresource.getDocument() == null) {
            resolvedSubresource.setDocument(new DBDocument());
            // set response data
            resolvedSubresource.getDocument().setName(resolvedData.getResourceDef().getName());
            resolvedSubresource.getDocument().setMimeType(StringUtils.getIfEmpty(responseData.getContentType(),
                    () -> resolvedData.getResourceDef().getMimeType()));
        }
        // create new document version
        DBDocumentVersion documentVersion = new DBDocumentVersion();
        documentVersion.setContent(baos.toByteArray());
        resourceStorage.addDocumentVersionForSubresource(resolvedSubresource, documentVersion);

    }

    @Transactional
    public void deleteResource(ResourceRequest resourceRequest,
                               ResourceResponse resourceResponse) {

        LOG.debug("Handle the DELETE action for resource request [{}]", resourceRequest);
        // locate the resource handler
        ResolvedData resolvedData = resourceRequest.getResolvedData();
        DBResource resource = resolvedData.getResource();
        resourceStorage.deleteResource(resource);
    }

    @Transactional
    public void deleteSubresource(ResourceRequest resourceRequest,
                                  ResourceResponse resourceResponse) {

        LOG.debug("Handle the DELETE action for resource request [{}]", resourceRequest);
        // locate the resource handler
        ResolvedData resolvedData = resourceRequest.getResolvedData();
        DBSubresource resource = resolvedData.getSubresource();
        resourceStorage.deleteSubresource(resource);
    }

    /**
     * Method determinate the http response code for processed create/update action. If code is defined by resource handler
     * then the code it returned. Else it sets HTTP_RESPONSE_CODE_CREATED if resource is to be created or HTTP_RESPONSE_CODE_UPDATED
     * if resource is updated
     *
     * @param isNewResource is new resource
     * @param responseData  the response data from the Resource handler
     * @return the response code.
     */
    public int getHttpStatusForCreateUpdate(boolean isNewResource, ResponseData responseData) {
        return responseData.getResponseCode() != null ? responseData.getResponseCode() :
                isNewResource ? HTTP_RESPONSE_CODE_CREATED : HTTP_RESPONSE_CODE_UPDATED;
    }
}
