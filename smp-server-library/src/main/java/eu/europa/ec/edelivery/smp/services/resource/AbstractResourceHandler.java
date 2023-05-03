package eu.europa.ec.edelivery.smp.services.resource;

import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.model.doc.DBSubresource;
import eu.europa.ec.edelivery.smp.data.model.ext.DBResourceDef;
import eu.europa.ec.edelivery.smp.data.model.ext.DBSubresourceDef;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.spi.SPIUtils;
import eu.europa.ec.edelivery.smp.services.spi.data.SpiRequestData;
import eu.europa.ec.edelivery.smp.servlet.ResourceResponse;
import eu.europa.ec.smp.spi.api.model.RequestData;
import eu.europa.ec.smp.spi.api.model.ResponseData;
import eu.europa.ec.smp.spi.exceptions.ResourceException;
import eu.europa.ec.smp.spi.resource.ResourceDefinitionSpi;
import eu.europa.ec.smp.spi.resource.ResourceHandlerSpi;
import eu.europa.ec.smp.spi.resource.SubresourceDefinitionSpi;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AbstractResourceHandler {
    protected static final SMPLogger LOG = SMPLoggerFactory.getLogger(AbstractResourceHandler.class);
    // the Spring beans for the resource definitions
    final List<ResourceDefinitionSpi> resourceDefinitionSpiList;
    final ResourceStorage resourceStorage;

    public AbstractResourceHandler(List<ResourceDefinitionSpi> resourceDefinitionSpiList, ResourceStorage resourceStorage) {
        this.resourceDefinitionSpiList = resourceDefinitionSpiList;
        this.resourceStorage = resourceStorage;
    }

    public ResourceDefinitionSpi getResourceDefinition(DBResourceDef resourceDef) {
        LOG.debug("Get resource definition for the [{}]", resourceDef);
        Optional<ResourceDefinitionSpi> definitionSpi = resourceDefinitionSpiList.stream()
                .filter(rdspi -> StringUtils.equals(resourceDef.getIdentifier(), rdspi.identifier()))
                .findFirst();

        return definitionSpi.orElseThrow(() -> new SMPRuntimeException(ErrorCode.INTERNAL_ERROR,
                resourceDef.getIdentifier(),
                "Can not find resource definition for identifier: [" + resourceDef.getIdentifier() + "] Registered resource SPI IDs ["
                        + resourceDefinitionSpiList.stream()
                        .map(rd -> rd.identifier())
                        .collect(Collectors.joining(","))
                        + "]"));
    }

    public ResourceHandlerSpi getResourceHandler(DBResourceDef resourceDef) {
        LOG.debug("Get resource handler for the [{}]", resourceDef);
        return getResourceDefinition(resourceDef).getResourceHandler();
    }

    public SubresourceDefinitionSpi getSubresourceDefinition(DBSubresourceDef subresourceDef, DBResourceDef resourceDef) {
        LOG.debug("Get resource definition for the [{}] for resource [{}]", subresourceDef, resourceDef);
        ResourceDefinitionSpi resourceDefinitionSpi = getResourceDefinition(resourceDef);
        String subResourceId = subresourceDef.getIdentifier();
        // get subresource implementation by identifier
        Optional<SubresourceDefinitionSpi> optSubresourceDefinitionSpi = resourceDefinitionSpi.getSuresourceSpiList().stream()
                .filter(def -> StringUtils.equals(def.identifier(), subResourceId)).findFirst();

        return optSubresourceDefinitionSpi.orElseThrow(
                () -> new SMPRuntimeException(ErrorCode.INTERNAL_ERROR, subResourceId,
                        "Can not find subresource definition: [" + subResourceId + "]. Registered subresource IDs ["
                                + resourceDefinitionSpi.getSuresourceSpiList().stream()
                                .map(rd -> rd.identifier())
                                .collect(Collectors.joining(","))
                                + "]"));
    }

    public ResourceHandlerSpi getSubresourceHandler(DBSubresourceDef subresourceDef, DBResourceDef resourceDef) {
        LOG.debug("Get resource handler for the [{}]", subresourceDef);
        return getSubresourceDefinition(subresourceDef, resourceDef).getResourceHandler();
    }

    /**
     * Build handler RequestData and add resource from the database
     *
     * @param domain   for the resource
     * @param resource an entity
     * @return data handler request data
     */
    public RequestData buildRequestDataForResource(DBDomain domain, DBResource resource) {
        byte[] content = resourceStorage.getDocumentContentForResource(resource);
        if (content==null || content.length == 0) {
            throw new SMPRuntimeException(ErrorCode.RESOURCE_DOCUMENT_MISSING, resource.getIdentifierValue(), resource.getIdentifierScheme());
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
        return buildRequestDataForResource(domain,
                resource,
                inputStream);
    }

    public RequestData buildRequestDataForResource(DBDomain domain, DBResource resource, InputStream inputStream) {
        return new SpiRequestData(domain.getDomainCode(),
                SPIUtils.toUrlIdentifier(resource),
                inputStream);
    }

    public RequestData buildRequestDataForSubResource(DBDomain domain, DBResource resource, DBSubresource subresource) {
        byte[] content = resourceStorage.getDocumentContentForSubresource(subresource);
        return new SpiRequestData(domain.getDomainCode(),
                SPIUtils.toUrlIdentifier(resource),
                SPIUtils.toUrlIdentifier(subresource),
                new ByteArrayInputStream(content == null?new byte[]{}:content));
    }

    public RequestData buildRequestDataForSubResource(DBDomain domain, DBResource resource, DBSubresource subresource, InputStream inputStream) {
        return new SpiRequestData(domain.getDomainCode(),
                SPIUtils.toUrlIdentifier(resource),
                SPIUtils.toUrlIdentifier(subresource),
                inputStream);
    }

    public void handleReadResource(ResourceHandlerSpi handlerSpi, RequestData requestData, ResponseData responseData, ResourceResponse resourceResponse) {
        try {
            handlerSpi.readResource(requestData, responseData);
            if (StringUtils.isNotBlank(responseData.getContentType())) {
                resourceResponse.setContentType(responseData.getContentType());
            }
            responseData.getHttpHeaders().entrySet().stream()
                    .forEach(entry -> resourceResponse.setHttpHeader(entry.getKey(), entry.getValue()));

        } catch (ResourceException e) {
            throw new SMPRuntimeException(ErrorCode.INTERNAL_ERROR, "Error occurred while reading the subresource!", e);
        }
    }
}
