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

import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyTypeEnum;
import eu.europa.ec.edelivery.smp.data.dao.DocumentDao;
import eu.europa.ec.edelivery.smp.data.dao.ResourceDao;
import eu.europa.ec.edelivery.smp.data.dao.SubresourceDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomainResourceDef;
import eu.europa.ec.edelivery.smp.data.model.doc.*;
import eu.europa.ec.edelivery.smp.data.model.ext.DBSubresourceDef;
import eu.europa.ec.edelivery.smp.data.ui.DocumentPropertyRO;
import eu.europa.ec.edelivery.smp.data.ui.DocumentRO;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.resource.ResourceHandlerService;
import eu.europa.ec.edelivery.smp.services.spi.data.SpiResponseData;
import eu.europa.ec.smp.spi.api.model.RequestData;
import eu.europa.ec.smp.spi.api.model.ResponseData;
import eu.europa.ec.smp.spi.enums.TransientDocumentPropertyType;
import eu.europa.ec.smp.spi.exceptions.ResourceException;
import eu.europa.ec.smp.spi.resource.ResourceHandlerSpi;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static eu.europa.ec.smp.spi.enums.TransientDocumentPropertyType.*;

@Service
public class UIDocumentService {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIDocumentService.class);
    ResourceDao resourceDao;
    SubresourceDao subresourceDao;
    DocumentDao documentDao;
    ResourceHandlerService resourceHandlerService;

    public UIDocumentService(ResourceDao resourceDao, SubresourceDao subresourceDao, DocumentDao documentDao, ResourceHandlerService resourceHandlerService) {
        this.resourceDao = resourceDao;
        this.subresourceDao = subresourceDao;
        this.documentDao = documentDao;
        this.resourceHandlerService = resourceHandlerService;
    }

    @Transactional
    public void validateDocumentForResource(Long resourceId, DocumentRO documentRo) {
        DBResource resource = resourceDao.find(resourceId);
        DBDomainResourceDef domainResourceDef = resource.getDomainResourceDef();
        ResourceHandlerSpi resourceHandler = resourceHandlerService.getResourceHandler(domainResourceDef.getResourceDef());
        RequestData data = resourceHandlerService.buildRequestDataForResource(domainResourceDef.getDomain(), resource, new ByteArrayInputStream(documentRo.getPayload().getBytes()));
        try {
            resourceHandler.validateResource(data);
        } catch (ResourceException e) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "ResourceValidation", ExceptionUtils.getRootCauseMessage(e));
        }
    }

    @Transactional
    public void validateDocumentForSubresource(Long subresourceId, Long resourceId, DocumentRO documentRo) {
        DBSubresource entity = subresourceDao.find(subresourceId);
        DBResource parentEntity = resourceDao.find(resourceId);
        ResourceHandlerSpi resourceHandler = resourceHandlerService.getSubresourceHandler(entity.getSubresourceDef(), entity.getSubresourceDef().getResourceDef());
        RequestData data = resourceHandlerService.buildRequestDataForSubResource(parentEntity.getDomainResourceDef().getDomain(), parentEntity, entity, new ByteArrayInputStream(documentRo.getPayload().getBytes()));
        try {
            resourceHandler.validateResource(data);
        } catch (ResourceException e) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "ResourceValidation", ExceptionUtils.getRootCauseMessage(e));
        }
    }

    @Transactional
    public DocumentRO generateDocumentForResource(Long resourceId, DocumentRO documentRo) {
        LOG.info("generate Document For Resource");
        DBResource resource = resourceDao.find(resourceId);
        DBDomainResourceDef domainResourceDef = resource.getDomainResourceDef();
        ResourceHandlerSpi resourceHandler = resourceHandlerService.getResourceHandler(domainResourceDef.getResourceDef());
        RequestData data = resourceHandlerService.buildRequestDataForResource(domainResourceDef.getDomain(),
                resource, null);

        return generateDocumentWithHandler(resourceHandler, data);
    }

    @Transactional
    public DocumentRO generateDocumentForSubresource(Long subresourceId, Long resourceId, DocumentRO documentRo) {
        LOG.info("generate Document For Subresource");
        DBResource parentEntity = resourceDao.find(resourceId);
        DBSubresource entity = subresourceDao.find(subresourceId);
        DBSubresourceDef subresourceDef = entity.getSubresourceDef();

        ResourceHandlerSpi resourceHandler = resourceHandlerService.getSubresourceHandler(subresourceDef, subresourceDef.getResourceDef());
        RequestData data = resourceHandlerService.buildRequestDataForSubResource(parentEntity.getDomainResourceDef().getDomain(),
                parentEntity, entity, null);

        return generateDocumentWithHandler(resourceHandler, data);
    }

    /**
     * Generate document with ResourceHandlerSpi. Method invokes the given handler to generate the document.
     *
     * @param resourceHandler handler to generate document
     * @param data            request data
     * @return DocumentRo with payload
     */
    private DocumentRO generateDocumentWithHandler(ResourceHandlerSpi resourceHandler, RequestData data) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ResponseData responseData = new SpiResponseData(bos);
        try {
            resourceHandler.generateResource(data, responseData, Collections.emptyList());
        } catch (ResourceException e) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "StoreResourceValidation", ExceptionUtils.getRootCauseMessage(e));
        }
        String genDoc = bos.toString();
        DocumentRO result = new DocumentRO();
        result.setPayload(genDoc);
        return result;
    }

    @Transactional
    public DocumentRO saveDocumentForResource(Long resourceId, DocumentRO documentRo) {

        final DBResource resource = resourceDao.find(resourceId);
        final DBDocument document = resource.getDocument();

        boolean isPayloadChanged = documentRo.getPayloadStatus() != EntityROStatus.PERSISTED.getStatusNumber();
        if (isPayloadChanged) {
            LOG.debug("Store resource payload for resource [{}]", resource.getIdentifierValue());
            storeResourcePayload(resource, document, documentRo);
        }

        if (isDocumentPropertiesChanged(documentRo)) {
            // persist non-transient properties
            documentRo.getProperties().stream().filter(p ->
                            TransientDocumentPropertyType.fromPropertyName(p.getProperty()) == null)
                    .forEach(p -> persistDocumentProperty(p, document));
        }

        return convertWithVersion(document, document.getCurrentVersion(), getInitialProperties(resource));
    }


    /**
     * Method persists the document property. If property has status DELETED removes the property from database
     * if property is UPDATED, the existing property entity is updated and if property is new then new property is
     * created.
     *
     * @param documentPropertyRO Document Property RO to persist
     * @DBDocument db document to which the property belongs
     */
    private void persistDocumentProperty(DocumentPropertyRO documentPropertyRO, DBDocument dbDocument) {

        EntityROStatus status = EntityROStatus.fromStatusNumber(documentPropertyRO.getStatus());
        status = status == null ? EntityROStatus.PERSISTED : status;


        DBDocumentProperty dbDocumentProperty = dbDocument.getDocumentProperties().stream()
                .filter(p -> p.getProperty().equals(documentPropertyRO.getProperty()))
                .findFirst().orElse(null);

        switch (status) {
            case REMOVED:
                if (dbDocumentProperty != null) {
                    dbDocument.getDocumentProperties().remove(dbDocumentProperty);
                } else {
                    LOG.warn("Document property [{}] not found for document [{}]", documentPropertyRO.getProperty(), dbDocument.getId());
                }
                break;
            case UPDATED:
                if (dbDocumentProperty != null) {
                    dbDocumentProperty.setDescription(documentPropertyRO.getDesc());
                    dbDocumentProperty.setValue(documentPropertyRO.getValue());
                } else {
                    LOG.warn("Document property [{}] not found for document [{}]. property is added", documentPropertyRO.getProperty(), dbDocument.getId());
                    addDocumentProperty(documentPropertyRO, dbDocument);
                }
                break;
            case NEW:
                if (dbDocumentProperty == null) {
                    addDocumentProperty(documentPropertyRO, dbDocument);
                } else {
                    LOG.warn("Document property [{}] already exists for document [{}]. property is updated",
                            documentPropertyRO.getProperty(), dbDocument.getId());
                    dbDocumentProperty.setDescription(documentPropertyRO.getDesc());
                    dbDocumentProperty.setValue(documentPropertyRO.getValue());
                }
                break;
            default:
                LOG.warn("Document property [{}] status [{}] indicates no change!",
                        documentPropertyRO.getProperty(), status);
        }
    }

    /**
     * Method adds new  Document Property to the DBDocument property list
     *
     * @param documentPropertyRO Document Property RO to persist
     * @param dbDocument         db document to which the property belongs
     */
    private void addDocumentProperty(DocumentPropertyRO documentPropertyRO, DBDocument dbDocument) {
        DBDocumentProperty dbDocumentProperty = new DBDocumentProperty();
        dbDocumentProperty.setDocument(dbDocument);
        dbDocumentProperty.setProperty(documentPropertyRO.getProperty());
        dbDocumentProperty.setValue(documentPropertyRO.getValue());
        dbDocumentProperty.setDescription(documentPropertyRO.getDesc());
        dbDocumentProperty.setType(documentPropertyRO.getType());

        dbDocument.getDocumentProperties().add(dbDocumentProperty);
    }

    /**
     * Method validates if any "non-transient" of the document properties are changed. If the document does not have
     * any properties or all exiting properties return status PERSISTED it returns false otherwise true.
     *
     * @param documentRo document to validate
     * @retun true if any of the properties changed otherwise false
     */
    private boolean isDocumentPropertiesChanged(DocumentRO documentRo) {
        return documentRo != null && documentRo.getProperties() != null && documentRo.getProperties().stream()
                .filter(p -> TransientDocumentPropertyType.fromPropertyName(p.getProperty()) == null)
                .anyMatch(p -> p.getStatus() != EntityROStatus.PERSISTED.getStatusNumber());
    }

    /**
     * Method stores the payload for the given resource as a new payload version.
     * The method invokes the ResourceHandlerSpi to update/validate the payload before storing it to database.
     *
     * @param resource   resource to store the payload
     * @param document   the resource database document entity
     * @param documentRo document RO the with new payload
     */
    private void storeResourcePayload(DBResource resource, DBDocument document, DocumentRO documentRo) {
        DBDomainResourceDef domainResourceDef = resource.getDomainResourceDef();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // invoke the resource handler for the document type
        ResourceHandlerSpi resourceHandler = resourceHandlerService.getResourceHandler(
                domainResourceDef.getResourceDef());
        RequestData data = resourceHandlerService.buildRequestDataForResource(domainResourceDef.getDomain(),
                resource, new ByteArrayInputStream(documentRo.getPayload().getBytes()));
        ResponseData responseData = new SpiResponseData(baos);
        try {
            resourceHandler.storeResource(data, responseData);
        } catch (ResourceException e) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "StoreResourceValidation", ExceptionUtils.getRootCauseMessage(e));
        }
        // create new version to document
        int version = document.getDocumentVersions().stream().mapToInt(DBDocumentVersion::getVersion)
                .max().orElse(0);

        DBDocumentVersion documentVersion = new DBDocumentVersion();
        documentVersion.setVersion(version + 1);
        documentVersion.setDocument(document);
        documentVersion.setContent(baos.toByteArray());
        // to get the current persist time
        documentVersion.prePersist();
        document.getDocumentVersions().add(documentVersion);
        document.setCurrentVersion(documentVersion.getVersion());
    }

    @Transactional
    public DocumentRO saveSubresourceDocumentForResource(Long subresource, Long resourceId, DocumentRO documentRo) {

        DBResource parentResource = resourceDao.find(resourceId);
        DBSubresource entity = subresourceDao.find(subresource);
        DBSubresourceDef subresourceDef = entity.getSubresourceDef();
        ResourceHandlerSpi resourceHandler = resourceHandlerService.getSubresourceHandler(subresourceDef, subresourceDef.getResourceDef());
        RequestData data = resourceHandlerService.buildRequestDataForSubResource(
                parentResource.getDomainResourceDef().getDomain(), parentResource,
                entity, new ByteArrayInputStream(documentRo.getPayload().getBytes()));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ResponseData responseData = new SpiResponseData(bos);
        try {
            resourceHandler.storeResource(data, responseData);
        } catch (ResourceException e) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "StoreSubresourceValidation", ExceptionUtils.getRootCauseMessage(e));
        }

        DBDocument document = entity.getDocument();
        return createNewVersionAndConvert(document, bos, getInitialProperties(entity));
    }

    /**
     * return version, if version does not exists return current version. if current version does not exists
     * return last version
     *
     * @param resourceId resource id of the document
     * @param version    version of the payload for the document
     * @return DocumentRo with payload and version
     */
    @Transactional
    public DocumentRO getDocumentForResource(Long resourceId, int version) {
        DBResource resource = resourceDao.find(resourceId);
        DBDocument document = resource.getDocument();


        return convertWithVersion(document, version, getInitialProperties(resource));
    }

    @Transactional
    public DocumentRO getDocumentForSubResource(Long subresourceId, Long resourceId, int version) {
        DBSubresource subresource = subresourceDao.find(subresourceId);
        DBDocument document = subresource.getDocument();
        return convertWithVersion(document, version, getInitialProperties(subresource));
    }

    private List<DocumentPropertyRO> getInitialProperties(DBResource resource) {
        List<DocumentPropertyRO> propertyROS = new ArrayList<>();
        propertyROS.add(new DocumentPropertyRO(RESOURCE_IDENTIFIER_VALUE.getPropertyName(),
                resource.getIdentifierValue(), RESOURCE_IDENTIFIER_VALUE.getPropertyDescription(), true));
        propertyROS.add(new DocumentPropertyRO(RESOURCE_IDENTIFIER_SCHEME.getPropertyName(),
                resource.getIdentifierScheme(), RESOURCE_IDENTIFIER_SCHEME.getPropertyDescription(), true));
        return propertyROS;
    }

    private List<DocumentPropertyRO> getInitialProperties(DBSubresource subresource) {
        List<DocumentPropertyRO> propertyROS = getInitialProperties(subresource.getResource());
        propertyROS.add(new DocumentPropertyRO(SUBRESOURCE_IDENTIFIER_VALUE.getPropertyName(),
                subresource.getIdentifierValue(), SUBRESOURCE_IDENTIFIER_VALUE.getPropertyDescription(), true));
        propertyROS.add(new DocumentPropertyRO(SUBRESOURCE_IDENTIFIER_SCHEME.getPropertyName(),
                subresource.getIdentifierScheme(), SUBRESOURCE_IDENTIFIER_SCHEME.getPropertyDescription(), true));
        return propertyROS;
    }


    /**
     * return Create new Document version and convert DBDocument  to DocumentRo
     *
     * @param document to convert to DocumentRo
     * @param baos     to write document content
     * @return DocumentRo
     */
    private DocumentRO createNewVersionAndConvert(DBDocument document, ByteArrayOutputStream baos, List<DocumentPropertyRO> initialProperties) {

        // get max version
        int version = document.getDocumentVersions().stream().mapToInt(DBDocumentVersion::getVersion)
                .max().orElse(0);

        DBDocumentVersion documentVersion = new DBDocumentVersion();
        documentVersion.setVersion(version + 1);
        documentVersion.setDocument(document);
        documentVersion.setContent(baos.toByteArray());
        // to get the current persist time
        documentVersion.prePersist();

        document.getDocumentVersions().add(documentVersion);
        document.setCurrentVersion(documentVersion.getVersion());
        return convert(document, documentVersion, initialProperties);
    }

    public DocumentRO convertWithVersion(DBDocument document, int version, List<DocumentPropertyRO> initialProperties) {
        DBDocumentVersion currentVersion = null;
        DBDocumentVersion documentVersion = null;
        for (DBDocumentVersion dv : document.getDocumentVersions()) {
            if (dv.getVersion() == version) {
                documentVersion = dv;
            }
            if (dv.getVersion() == document.getCurrentVersion()) {
                currentVersion = dv;
            }
        }
        documentVersion = documentVersion == null ? currentVersion : documentVersion;
        if (documentVersion == null && !document.getDocumentVersions().isEmpty()) {
            documentVersion = document.getDocumentVersions().get(document.getDocumentVersions().size() - 1);
        }
        return convert(document, documentVersion, initialProperties);
    }

    public DocumentRO convert(DBDocument document, DBDocumentVersion version, List<DocumentPropertyRO> initialProperties) {
        DocumentRO documentRo = new DocumentRO();
        documentRo.addProperty(DOCUMENT_NAME.getPropertyName(),
                document.getName(), "Document Name", SMPPropertyTypeEnum.STRING, true);
        documentRo.addProperty(DOCUMENT_MIMETYPE.getPropertyName(),
                document.getMimeType(), "Document Mimetype", SMPPropertyTypeEnum.STRING, true);

        documentRo.getProperties().addAll(initialProperties);

        // set list of versions
        document.getDocumentVersions().forEach(dv ->
                documentRo.getAllVersions().add(dv.getVersion()));

        documentRo.setMimeType(document.getMimeType());
        documentRo.setName(document.getName());
        documentRo.setCurrentResourceVersion(document.getCurrentVersion());
        document.getDocumentProperties().stream()
                .forEach(p -> {
                    documentRo.addProperty(p.getProperty(),
                            p.getValue(),
                            p.getDescription(),
                            p.getType(), false);
                    LOG.info("Document property [{}] added to document [{}]", p);
                });


        if (version != null) {
            documentRo.setPayloadCreatedOn(version.getCreatedOn());
            documentRo.setPayloadVersion(version.getVersion());
            documentRo.setPayload(new String(version.getContent()));
        }

        return documentRo;
    }
}
