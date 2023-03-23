package eu.europa.ec.edelivery.smp.services.resource;


import eu.europa.ec.edelivery.smp.data.model.doc.DBDocument;
import eu.europa.ec.edelivery.smp.data.model.doc.DBDocumentVersion;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
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
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * The class handles the resource actions
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
@Service
public class ResourceHandlerService extends AbstractResourceHandler {
    protected static final SMPLogger LOG = SMPLoggerFactory.getLogger(ResourceHandlerService.class);

    public ResourceHandlerService(List<ResourceDefinitionSpi> resourceDefinitionSpiList, ResourceStorage resourceStorage) {
        super(resourceDefinitionSpiList, resourceStorage);
    }

    public void readResource(ResourceRequest resourceRequest,
                             ResourceResponse resourceResponse) {

        LOG.debug("Handle the READ action for resource request [{}]", resourceRequest);
        ResolvedData resolvedData = resourceRequest.getResolvedData();
        ResourceHandlerSpi handlerSpi = getResourceHandler(resolvedData.getResourceDef());

        RequestData requestData = buildRequestDataForResource(resolvedData.getDomain(), resolvedData.getResource());
        ResponseData responseData = new SpiResponseData(resourceResponse.getOutputStream());
        // get resource byte array

        handleReadResource(handlerSpi, requestData, responseData, resourceResponse);
    }

    public void readSubresource(ResourceRequest resourceRequest,
                                ResourceResponse resourceResponse) {

        LOG.debug("Handle the READ action for subresource request [{}]", resourceRequest);
        ResolvedData resolvedData = resourceRequest.getResolvedData();
        DBSubresource resolvedSubresource = resolvedData.getSubresource();
        ResourceHandlerSpi handlerSpi = getSubresourceHandler(resolvedSubresource.getSubresourceDef(), resolvedData.getResourceDef());
        // generate request and respond
        RequestData requestData = buildRequestDataForSubResource(resolvedData.getDomain(), resolvedData.getResource(), resolvedData.getSubresource());
        ResponseData responseData = new SpiResponseData(resourceResponse.getOutputStream());
        // handle data
        handleReadResource(handlerSpi, requestData, responseData, resourceResponse);
    }

    @Transactional
    public void createResource(ResourceRequest resourceRequest,
                               ResourceResponse resourceResponse) {

        LOG.debug("Handle the Create action for resource request [{}]", resourceRequest);
        // locate the resource handler

        ResolvedData resolvedData = resourceRequest.getResolvedData();
        ResourceHandlerSpi handlerSpi = getResourceHandler(resolvedData.getResourceDef());

        RequestData requestData = buildRequestDataForResource(resolvedData.getDomain(),
                resolvedData.getResource(), resourceRequest.getInputStream());

        // write to response data and save the request
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResponseData responseData = new SpiResponseData(baos);

        try {
            handlerSpi.storeResource(requestData, responseData);
            if (StringUtils.isNotBlank(responseData.getContentType())) {
                resourceResponse.setContentType(responseData.getContentType());
            }
        } catch (ResourceException e) {
            throw new SMPRuntimeException(ErrorCode.INTERNAL_ERROR, "Error occurred while reading the subresource!", e);
        }
        // set headers to response
        responseData.getHttpHeaders().entrySet().stream()
                .forEach(entry -> resourceResponse.setHttpHeader(entry.getKey(), entry.getValue()));

        DBResource resource = resolvedData.getResource();

        if (resource.getDocument() == null) {
            resource.setDocument(new DBDocument());
            resource.getDocument().setName(resolvedData.getResourceDef().getName());
            // set response data from the
            resource.getDocument().setMimeType(StringUtils.getIfEmpty(responseData.getContentType(),
                    () -> resolvedData.getResourceDef().getMimeType()));
        }
        // create new document version
        DBDocumentVersion documentVersion = new DBDocumentVersion();
        documentVersion.setContent(baos.toByteArray());
        resourceStorage.addDocumentVersionForResource(resource, documentVersion);
    }
}
