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
import eu.europa.ec.edelivery.smp.data.enums.DocumentVersionEventType;
import eu.europa.ec.edelivery.smp.data.enums.DocumentVersionStatusType;
import eu.europa.ec.edelivery.smp.data.enums.EventSourceType;
import eu.europa.ec.edelivery.smp.data.model.DBDomainResourceDef;
import eu.europa.ec.edelivery.smp.data.model.doc.*;
import eu.europa.ec.edelivery.smp.data.model.ext.DBSubresourceDef;
import eu.europa.ec.edelivery.smp.data.ui.*;
import eu.europa.ec.edelivery.smp.data.ui.enums.EntityROStatus;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.resource.DocumentVersionService;
import eu.europa.ec.edelivery.smp.services.resource.ResourceHandlerService;
import eu.europa.ec.edelivery.smp.services.spi.data.SpiResponseData;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import eu.europa.ec.smp.spi.api.model.RequestData;
import eu.europa.ec.smp.spi.api.model.ResponseData;
import eu.europa.ec.smp.spi.enums.TransientDocumentPropertyType;
import eu.europa.ec.smp.spi.exceptions.ResourceException;
import eu.europa.ec.smp.spi.resource.ResourceHandlerSpi;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static eu.europa.ec.smp.spi.enums.TransientDocumentPropertyType.*;

@Service
public class UIDocumentService {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIDocumentService.class);
    final ResourceDao resourceDao;
    final SubresourceDao subresourceDao;
    final DocumentDao documentDao;
    final ResourceHandlerService resourceHandlerService;
    final DocumentVersionService documentVersionService;
    final ConversionService conversionService;

    public UIDocumentService(ResourceDao resourceDao,
                             SubresourceDao subresourceDao,
                             DocumentDao documentDao,
                             ResourceHandlerService resourceHandlerService,
                             DocumentVersionService documentVersionService,
                             ConversionService conversionService) {
        this.resourceDao = resourceDao;
        this.subresourceDao = subresourceDao;
        this.documentDao = documentDao;
        this.resourceHandlerService = resourceHandlerService;
        this.documentVersionService = documentVersionService;
        this.conversionService = conversionService;
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
    public DocumentRO publishDocumentVersionForResource(Long resourceId, Long documentId, int version) {
        LOG.info("Publish Document For Resource [{}], document [{}], version [{}]", resourceId, documentId, version);
        DBResource resource = resourceDao.find(resourceId);
        DBDocument document = resource.getDocument();
        if (!Objects.equals(document.getId(), documentId)) {
            LOG.warn("Document id [{}] does not match the resource document id [{}]", documentId, document.getId());
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DocumentIdMismatch", "Document id does not match the resource document id");
        }
        return publishDocumentVersion(document, version, resource.isReviewEnabled(), getInitialProperties(resource));
    }

    @Transactional
    public DocumentRO publishDocumentVersionForSubresource(Long subresourceId, Long resourceId, Long documentId, int version) {
        LOG.info("Publish Document For subresource [{}], resource [{}], version [{}]", subresourceId, resourceId, version);

        DBSubresource subresource = subresourceDao.find(subresourceId);
        DBResource resource = resourceDao.find(resourceId);
        DBDocument document = subresource.getDocument();
        if (!Objects.equals(document.getId(), documentId)) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DocumentIdMismatch", "Document id does not match the resource document id");
        }
        return publishDocumentVersion(document, version, resource.isReviewEnabled(), getInitialProperties(subresource));
    }


    private DocumentRO publishDocumentVersion(DBDocument document, int version, boolean isReviewEnabled, List<DocumentPropertyRO> initialProperties) {

        DBDocumentVersion documentVersion = document.getDocumentVersions().stream()
                .filter(dv -> dv.getVersion() == version)
                .findFirst().orElse(null);
        if (documentVersion == null) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DocumentVersionNotFound", "Document version not found");
        }
        if (isReviewEnabled && documentVersion.getStatus() != DocumentVersionStatusType.APPROVED) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DocumentVersionAlreadyPublished", "Document version has wrong status");
        }
        if (document.getDocumentVersions() != null && document.getCurrentVersion() == version) {
            LOG.warn("Document version [{}] is already current version for the document [{}]", version, document.getId());
            return convertWithVersion(document, version, initialProperties);
        }
        //retire all other versions
        document.getDocumentVersions().stream()
                .filter(dv -> dv.getVersion() != version)
                .filter(dv -> dv.getStatus() == DocumentVersionStatusType.PUBLISHED)
                .forEach(dv -> documentVersionService.retireDocumentVersion(dv, EventSourceType.UI, "Retire document version"));
        document.setCurrentVersion(documentVersion.getVersion());
        documentVersionService.publishDocumentVersion(documentVersion, EventSourceType.UI, true);
        // return the document with the new version
        return convertWithVersion(document, version, initialProperties);
    }

    @Transactional
    public DocumentRO requestReviewDocumentVersionForResource(Long resourceId, Long documentId, int version) {
        LOG.info("Request review Document For Resource [{}], version [{}]", resourceId, version);
        DBResource resource = resourceDao.find(resourceId);
        DBDocument document = resource.getDocument();
        if (!Objects.equals(document.getId(), documentId)) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DocumentIdMismatch", "Document id does not match the resource document id");
        }
        return requestReviewDocumentVersion(document, version, resource.isReviewEnabled(), getInitialProperties(resource));
    }

    @Transactional
    public DocumentRO requestReviewDocumentVersionForSubresource(Long subresourceId, Long resourceId, Long documentId, int version) {
        LOG.info("Request review Document For subResource [{}], resource [{}],  version [{}]", subresourceId, resourceId, version);
        DBResource resource = resourceDao.find(resourceId);
        DBSubresource subresource = subresourceDao.find(subresourceId);
        DBDocument document = subresource.getDocument();
        if (!Objects.equals(document.getId(), documentId)) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DocumentIdMismatch", "Document id does not match the resource document id");
        }
        return requestReviewDocumentVersion(document, version, resource.isReviewEnabled(), getInitialProperties(subresource));
    }


    private DocumentRO requestReviewDocumentVersion(DBDocument document, int version, boolean isReviewEnabled, List<DocumentPropertyRO> initialProperties) {
        DBDocumentVersion documentVersion = document.getDocumentVersions().stream()
                .filter(dv -> dv.getVersion() == version)
                .findFirst().orElse(null);
        if (documentVersion == null) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DocumentVersionNotFound", "Document version not found");
        }

        if (!isReviewEnabled) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DocumentReviewNotEnabled", "Document Review is not enabled for the document");
        }

        if (documentVersion.getStatus() == DocumentVersionStatusType.PUBLISHED) {
            LOG.warn("Document version [{}] request review action for document [{}] is not allowed. Wrong status", version, document.getId());
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DocumentReviewNotAllowed", "Document Review is not allowed for the document");
        }

        if (documentVersion.getStatus() == DocumentVersionStatusType.UNDER_REVIEW) {
            LOG.warn("Document version review [{}] for document [{}] is already under review", version, document.getId());
            return convertWithVersion(document, version, initialProperties);
        }
        //retire all other versions
        documentVersionService.requestReviewDocumentVersion(documentVersion, EventSourceType.UI);
        // return the document with the new version

        return convertWithVersion(document, version, initialProperties);
    }

    @Transactional
    public DocumentRO reviewActionDocumentVersionForResource(Long resourceId, Long documentId, int version, DocumentVersionEventType action, String message) {
        LOG.info("Approve review Document version For Resource [{}], version [{}]", resourceId, version);
        DBResource resource = resourceDao.find(resourceId);
        DBDocument document = resource.getDocument();
        if (!Objects.equals(document.getId(), documentId)) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DocumentIdMismatch", "Document id does not match the resource document id");
        }
        return reviewActionDocumentVersion(document, version, resource.isReviewEnabled(), action, message, getInitialProperties(resource));
    }

    @Transactional
    public DocumentRO reviewActionDocumentVersionForSubresource(Long subresourceId, Long resourceId, Long documentId, int version, DocumentVersionEventType action, String message) {
        LOG.info("Approve review Document version For subResource [{}], resource [{}], version [{}]", subresourceId, resourceId, version);
        DBResource resource = resourceDao.find(resourceId);
        DBSubresource subresource = subresourceDao.find(subresourceId);
        DBDocument document = subresource.getDocument();
        if (!Objects.equals(document.getId(), documentId)) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DocumentIdMismatch", "Document id does not match the subresource document id");
        }
        return reviewActionDocumentVersion(document, version, resource.isReviewEnabled(), action, message, getInitialProperties(subresource));
    }


    private DocumentRO reviewActionDocumentVersion(DBDocument document,
                                                   int version,
                                                   boolean reviewEnabled,
                                                   DocumentVersionEventType action,
                                                   String message,
                                                   List<DocumentPropertyRO> initialProperties) {

        DBDocumentVersion documentVersion = document.getDocumentVersions().stream()
                .filter(dv -> dv.getVersion() == version)
                .findFirst()
                .orElseThrow(() -> new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DocumentVersionNotFound", "Document version not found"));

        if (!reviewEnabled) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DocumentReviewNotEnabled", "Document Review is not enabled for the document");
        }

        if (documentVersion.getStatus() != DocumentVersionStatusType.UNDER_REVIEW
                && documentVersion.getStatus() != DocumentVersionStatusType.APPROVED) {
            LOG.warn("Document version [{}]  action for document [{}] not allowed. Wrong status", version, initialProperties);
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DocumentReviewActionNotAllowed", "Document Review action is not allowed for the document");
        }

        if (action == DocumentVersionEventType.APPROVE) {
            documentVersionService.approveDocumentVersion(documentVersion, EventSourceType.UI, message);
        } else if (action == DocumentVersionEventType.REJECT) {
            documentVersionService.rejectDocumentVersion(documentVersion, EventSourceType.UI, message);
        } else {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DocumentReviewActionNotAllowed", "Document Review action is not allowed for the document");
        }
        // return the document with the new version
        return convertWithVersion(document, version, initialProperties);
    }

    @Transactional
    public DocumentRO generateDocumentForResource(Long resourceId) {
        LOG.info("generate Document For Resource");
        DBResource resource = resourceDao.find(resourceId);
        // generate document and write to output stream
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        generateDocumentForResource(resource, bos);

        String genDoc = bos.toString();
        DocumentRO result = new DocumentRO();
        result.setDocumentConfiguration(new DocumentConfigurationRO());
        result.setPayload(genDoc);
        return result;
    }

    public void generateDocumentForResource(DBResource resource, OutputStream outputStream) {
        LOG.info("generate new Document For domainResourceDef");
        DBDomainResourceDef domainResourceDef = resource.getDomainResourceDef();

        ResourceHandlerSpi resourceHandler = resourceHandlerService.getResourceHandler(domainResourceDef.getResourceDef());
        RequestData data = resourceHandlerService.buildRequestDataForResource(domainResourceDef.getDomain(),
                resource, null);

        generateDocumentWithHandler(resourceHandler, data, outputStream);
    }

    @Transactional
    public DocumentRO generateDocumentForSubresource(Long subresourceId, Long resourceId) {
        LOG.info("generate Document For Subresource identifier [{}]", subresourceId);
        DBResource parentEntity = resourceDao.find(resourceId);
        DBSubresource entity = subresourceDao.find(subresourceId);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        generateDocumentForSubresource(parentEntity, entity, bos);

        String genDoc = bos.toString();
        DocumentRO result = new DocumentRO();
        result.setPayload(genDoc);
        return result;
    }

    public void generateDocumentForSubresource(DBResource parentEntity, DBSubresource entity, OutputStream outputStream) {
        LOG.info("generate Document For Subresource");
        DBSubresourceDef subresourceDef = entity.getSubresourceDef();

        ResourceHandlerSpi resourceHandler = resourceHandlerService.getSubresourceHandler(subresourceDef, subresourceDef.getResourceDef());
        RequestData data = resourceHandlerService.buildRequestDataForSubResource(parentEntity.getDomainResourceDef().getDomain(),
                parentEntity, entity, null);

        generateDocumentWithHandler(resourceHandler, data, outputStream);
    }

    /**
     * Generate document with ResourceHandlerSpi. Method invokes the given handler to generate the document.
     *
     * @param resourceHandler handler to generate document
     * @param data            request data
     * @return DocumentRo with payload
     */
    private void generateDocumentWithHandler(ResourceHandlerSpi resourceHandler, RequestData data, OutputStream outputStream) {

        ResponseData responseData = new SpiResponseData(outputStream);
        try {
            resourceHandler.generateResource(data, responseData, Collections.emptyList());
        } catch (ResourceException e) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "StoreResourceValidation", ExceptionUtils.getRootCauseMessage(e));
        }
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
     * Method stores the payload for the given resource. If the resource has status New then new document version is created
     * else the existing document version is updated with the new payload.
     * <p>
     * The method invokes the ResourceHandlerSpi to update/validate the payload before storing it to database.
     *
     * @param resource   resource to store the payload
     * @param document   the resource database document entity
     * @param documentRo document RO the with new payload
     */
    private DBDocumentVersion storeResourcePayload(DBResource resource, DBDocument document, DocumentRO documentRo) {
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

        DBDocumentVersion documentVersion;
        if (documentRo.getPayloadVersion() == null) {
            documentVersion = createNewDocumentVersion(document, baos.toByteArray());
        } else {
            documentVersion = document.getDocumentVersions().stream()
                    .filter(dv -> dv.getVersion() == documentRo.getPayloadVersion())
                    .findFirst().orElse(null);
            if (documentVersion == null) {
                throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DocumentVersionNotFound", "Document version not found");
            }
            documentVersion.setContent(baos.toByteArray());
        }
        return documentVersion;

    }

    private DBDocumentVersion storeSubresourcePayload(DBSubresource subresource, DBResource parentResource, DBDocument document, DocumentRO documentRo) {

        DBSubresourceDef subresourceDef = subresource.getSubresourceDef();

        ResourceHandlerSpi resourceHandler = resourceHandlerService.getSubresourceHandler(subresourceDef, subresourceDef.getResourceDef());
        RequestData data = resourceHandlerService.buildRequestDataForSubResource(
                parentResource.getDomainResourceDef().getDomain(), parentResource,
                subresource, new ByteArrayInputStream(documentRo.getPayload().getBytes()));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResponseData responseData = new SpiResponseData(baos);
        try {
            resourceHandler.storeResource(data, responseData);
        } catch (ResourceException e) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "StoreSubresourceValidation", ExceptionUtils.getRootCauseMessage(e));
        }

        DBDocumentVersion documentVersion = null;
        if (documentRo.getPayloadVersion() == null) {
            documentVersion = createNewDocumentVersion(document, baos.toByteArray());
        } else {
            documentVersion = document.getDocumentVersions().stream()
                    .filter(dv -> dv.getVersion() == documentRo.getPayloadVersion())
                    .findFirst().orElse(null);
            if (documentVersion == null) {
                throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DocumentVersionNotFound", "Document version not found");
            }
            documentVersion.setContent(baos.toByteArray());
        }
        return documentVersion;

    }

    public DBDocumentVersion createNewDocumentVersion(DBDocument document, byte[] payload) {
        // create new version to document
        int version = document.getDocumentVersions().stream().mapToInt(DBDocumentVersion::getVersion)
                .max().orElse(0);

        DBDocumentVersion documentVersion = documentVersionService.createDocumentVersionForCreate(EventSourceType.UI,
                "Create and publish resource by group admin", false);
        documentVersion.setVersion(version + 1);
        documentVersion.setDocument(document);
        documentVersion.setContent(payload);
        documentVersion.setStatus(DocumentVersionStatusType.DRAFT);
        // to get the current persist time
        documentVersion.prePersist();
        document.getDocumentVersions().add(0, documentVersion);
        return documentVersion;
    }


    @Transactional
    public DocumentRO saveDocumentForResource(Long resourceId, DocumentRO documentRo) {

        final DBResource resource = resourceDao.find(resourceId);
        final DBDocument document = resource.getDocument();
        return saveDocument(resource, null, document, documentRo);

    }

    /**
     * Method saves the document for resource or  subresource. if the subresource is null then it is resource document
     * otherwise it is subresource document.
     *
     * @param resource
     * @param subresource
     * @param document
     * @param documentRo
     * @return
     */
    public DocumentRO saveDocument(DBResource resource, DBSubresource subresource, DBDocument document, DocumentRO documentRo) {

        // check if the document is new or existing document. If payload version is not null then
        // return the current payload version otherwise return the current version
        int returnDocVersion = documentRo.getPayloadVersion() != null ?
                documentRo.getPayloadVersion() :
                document.getCurrentVersion();

        boolean isPayloadChanged = documentRo.getPayloadStatus() != EntityROStatus.PERSISTED.getStatusNumber();
        if (isPayloadChanged) {
            LOG.debug("Store (sub) resource payload for resource [{}], subresource [{}]", resource, subresource);
            DBDocumentVersion docVersion = subresource == null ?
                    storeResourcePayload(resource, document, documentRo)
                    : storeSubresourcePayload(subresource, resource, document, documentRo);
            ;
            returnDocVersion = docVersion.getVersion();
        }

        if (isDocumentPropertiesChanged(documentRo)) {
            // persist non-transient properties
            documentRo.getProperties().stream().filter(p ->
                            TransientDocumentPropertyType.fromPropertyName(p.getProperty()) == null)
                    .forEach(p -> persistDocumentProperty(p, document));
        }

        DocumentConfigurationRO docConfig = documentRo.getDocumentConfiguration();
        if (docConfig != null) {

            if (Boolean.TRUE.equals(docConfig.getSharingEnabled()) && StringUtils.isNotBlank(docConfig.getReferenceDocumentId())) {
                throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DocumentSharingNotAllowed", "Document sharing is not allowed for the document with reference document");
            }
            document.setSharingEnabled(docConfig.getSharingEnabled());
            document.setName(docConfig.getName());

            // update document reference
            updateDocumentReferenceToDocument(document, docConfig);
            docConfig = new DocumentConfigurationRO();
        }

        List<DocumentPropertyRO> initialProperties = subresource == null ? getInitialProperties(resource) : getInitialProperties(subresource);
        return convertWithVersion(document, returnDocVersion, initialProperties);
    }

    /**
     * Method updates the document reference to the document.
     *
     * @param document                target document
     * @param documentConfigurationRO document configuration from request
     */
    public void updateDocumentReferenceToDocument(DBDocument document, DocumentConfigurationRO documentConfigurationRO) {
        if (documentConfigurationRO == null) {
            return;
        }
        String referenceDocumentEncId = documentConfigurationRO.getReferenceDocumentId();
        Long documentReferenceId = null;
        if (StringUtils.isNotBlank(referenceDocumentEncId)) {
            documentReferenceId = SessionSecurityUtils.decryptEntityId(referenceDocumentEncId);
        }

        if (documentReferenceId != null) {
            if (Objects.equals(documentReferenceId, document.getId())) {
                throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DocumentReferenceNotAllowed", "Document reference cannot be the same as the document id");
            }
            DBDocument documentReferenceEntity = documentDao.find(documentReferenceId);
            if (documentReferenceEntity == null) {
                throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DocumentReferenceNotFound", "Document reference not found");
            }

            // compare Boolean.TRUE to catch null values
            if (!Boolean.TRUE.equals(documentReferenceEntity.getSharingEnabled())) {
                throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DocumentReferenceNotValid", "Can not reference to not shared document");
            }

            if (documentReferenceEntity.getReferenceDocument() != null) {
                throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "DocumentReferenceNotValid", "Can not reference to a document that already has a reference");
            }
            document.setReferenceDocument(documentReferenceEntity);
            LOG.info("Document [{}] is referencing to document [{}] and reference url [{}]", document.getId(), documentReferenceEntity.getId(), documentConfigurationRO.getReferenceDocumentUrl());
            document.setReferenceDocumentUrl(documentConfigurationRO.getReferenceDocumentUrl());
        } else {
            document.setReferenceDocument(null);
            document.setReferenceDocumentUrl(null);
        }
    }

    @Transactional
    public DocumentRO saveSubresourceDocumentForResource(Long subresource, Long resourceId, DocumentRO documentRo) {

        DBResource parentResource = resourceDao.find(resourceId);
        DBSubresource entity = subresourceDao.find(subresource);
        DBDocument document = entity.getDocument();
        return saveDocument(parentResource, entity, document, documentRo);
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
        DocumentRO result = convertWithVersion(document, version, getInitialProperties(resource));
        return result;
    }

    @Transactional
    public DocumentRO getDocumentForSubResource(Long subresourceId, Long resourceId, int version) {
        DBSubresource subresource = subresourceDao.find(subresourceId);
        DBDocument document = subresource.getDocument();
        return convertWithVersion(document, version, getInitialProperties(subresource));
    }


    /**
     * Method returns the list of reference documents for the given resource and filter paramters
     *
     * @param targetResourceId         resource id for which the reference documents are searched
     * @param page                     page number
     * @param pageSize                 page size
     * @param searchResourceIdentifier
     * @param searchResourceScheme
     * @return
     */
    @Transactional
    public ServiceResult<SearchReferenceDocumentRO> getSearchReferenceDocumentListForResource(Long targetResourceId, int page, int pageSize,
                                                                                              String searchResourceIdentifier, String searchResourceScheme) {
        DBResource targetResource = resourceDao.find(targetResourceId);
        ServiceResult<SearchReferenceDocumentRO> result = new ServiceResult<>();
        result.setPage(page);
        result.setPageSize(pageSize);
        Long count = documentDao.getSearchReferenceDocumentResourcesCount(targetResource, searchResourceIdentifier, searchResourceScheme);
        if (count < 1) {
            result.setCount(0L);
            return result;
        }
        result.setCount(count);
        List<SearchReferenceDocumentRO> refList = documentDao.getSearchReferenceDocumentResources(targetResource,
                        searchResourceIdentifier, searchResourceScheme, page, pageSize).stream()
                .map(doc -> conversionService.convert(doc, SearchReferenceDocumentRO.class))
                .collect(Collectors.toList());
        result.getServiceEntities().addAll(refList);
        return result;
    }

    /**
     * Method returns the list of reference documents for the given subresource and filter paramters by searchResourceIdentifier and searchResourceScheme
     * and searchSubresourceIdentifier and searchSubresourceScheme.
     *
     * @param targetSubresourceId         the subresource id for which the reference documents are searched
     * @param page                        the page index/number
     * @param pageSize                    number of items per page
     * @param searchResourceIdentifier    the partial identifier of the resource to search for
     * @param searchResourceScheme        the partial scheme of the resource to search for
     * @param searchSubresourceIdentifier the partial identifier of the subresource to search for
     * @param searchSubresourceScheme     the partial scheme of the subresource to search for
     * @return list of reference documents
     */
    @Transactional
    public ServiceResult<SearchReferenceDocumentRO> getSearchReferenceDocumentListForSubresource(Long targetSubresourceId, Long resourceId, int page, int pageSize,
                                                                                                 String searchResourceIdentifier, String searchResourceScheme,
                                                                                                 String searchSubresourceIdentifier, String searchSubresourceScheme) {
        DBSubresource targetResource = subresourceDao.find(targetSubresourceId);
        if (!Objects.equals(targetResource.getResource().getId(), resourceId)) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "ResourceMismatch", "Resource id does not match the subresource resource id");
        }
        ServiceResult<SearchReferenceDocumentRO> result = new ServiceResult<>();
        result.setPage(page);
        result.setPageSize(pageSize);
        Long count = documentDao.getSearchReferenceDocumentSubresourceCount(targetResource, searchResourceIdentifier,
                searchResourceScheme, searchSubresourceIdentifier, searchSubresourceScheme);
        if (count < 1) {
            result.setCount(0L);
            return result;
        }
        result.setCount(count);
        List<SearchReferenceDocumentRO> refList = documentDao.getSearchReferenceDocumentSubresource(targetResource,
                        searchResourceIdentifier, searchResourceScheme, searchSubresourceIdentifier, searchSubresourceScheme, page, pageSize).stream()
                .map(doc -> conversionService.convert(doc, SearchReferenceDocumentRO.class))
                .collect(Collectors.toList());
        result.getServiceEntities().addAll(refList);
        return result;
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


    public DocumentRO convertWithVersion(DBDocument document, int version, List<DocumentPropertyRO> initialProperties) {
        DBDocumentVersion documentVersion = getDocumentVersionOrCurrentVersion(document, version);
        return convert(document, documentVersion, initialProperties);
    }

    /**
     * Method returns the documentVersion for the given version. If the version does not exist
     * return the current version
     *
     * @param document with list of versions
     * @param version  to get the document version
     * @return documentVersion for the given version or current version
     */
    public DBDocumentVersion getDocumentVersionOrCurrentVersion(DBDocument document, int version) {
        DBDocumentVersion documentVersion = document.getDocumentVersions().stream()
                .filter(dv -> dv.getVersion() == version)
                .findFirst()
                .orElse(document.getDocumentVersions().stream()
                        .filter(dv -> dv.getVersion() == document.getCurrentVersion())
                        .findFirst()
                        .orElse(document.getDocumentVersions().isEmpty()
                                ? null : document.getDocumentVersions().get(document.getDocumentVersions().size() - 1)));

        return documentVersion;
    }


    /**
     * Convert DBDocument to DocumentRo with given document version
     *
     * @param document          to convert to DocumentRo
     * @param version           to set as version
     * @param initialProperties
     * @return
     */
    public DocumentRO convert(DBDocument document, DBDocumentVersion version, List<DocumentPropertyRO> initialProperties) {
        DocumentRO documentRo = new DocumentRO();
        documentRo.addProperty(DOCUMENT_NAME.getPropertyName(),
                document.getName(), DOCUMENT_MIMETYPE.getPropertyDescription(), SMPPropertyTypeEnum.STRING, true);
        documentRo.addProperty(DOCUMENT_MIMETYPE.getPropertyName(),
                document.getMimeType(), DOCUMENT_MIMETYPE.getPropertyDescription(), SMPPropertyTypeEnum.STRING, true);
        documentRo.getProperties().addAll(initialProperties);

        documentRo.setDocumentId(SessionSecurityUtils.encryptedEntityId(document.getId()));
        DocumentConfigurationRO docConfigRo = new DocumentConfigurationRO();
        DBDocument reference = document.getReferenceDocument();
        if (reference != null) {
            docConfigRo.setReferenceDocumentId(SessionSecurityUtils.encryptedEntityId(reference.getId()));
            docConfigRo.setReferenceDocumentName(reference.getName());
            docConfigRo.setReferenceDocumentUrl(document.getReferenceDocumentUrl());
            docConfigRo.setReferenceDocumentAccessible(Boolean.TRUE.equals(reference.getSharingEnabled()));
            // allways get default version for referenced document
            DBDocumentVersion referencedVersion = getDocumentVersionOrCurrentVersion(document.getReferenceDocument(), -1);
            documentRo.setReferencePayload(new String(referencedVersion.getContent()));
        }

        docConfigRo.setSharingEnabled(document.getSharingEnabled());
        docConfigRo.setMimeType(document.getMimeType());
        docConfigRo.setPublishedVersion(document.getCurrentVersion());
        docConfigRo.setName(document.getName());
        // set list of versions
        document.getDocumentVersions().forEach(dv -> {
            docConfigRo.getAllVersions().add(dv.getVersion());
        });
        documentRo.setDocumentConfiguration(docConfigRo);

        document.getDocumentVersions().forEach(dv -> {
            documentRo.getAllVersions().add(dv.getVersion());
            documentRo.getDocumentVersions().add(conversionService.convert(dv, DocumentVersionRO.class));
        });
        documentRo.setMimeType(document.getMimeType());
        documentRo.setName(document.getName());
        documentRo.setCurrentResourceVersion(document.getCurrentVersion());
        // set list of versions
        document.getDocumentProperties().stream()
                .forEach(p -> {
                    documentRo.addProperty(p.getProperty(),
                            p.getValue(),
                            p.getDescription(),
                            p.getType(), false);
                    LOG.info("Document property [{}] added to document [{}]", p);
                });

        docConfigRo.setMimeType(document.getMimeType());

        if (version != null) {
            documentRo.setPayloadCreatedOn(version.getCreatedOn());
            documentRo.setPayloadVersion(version.getVersion());
            documentRo.setPayload(new String(version.getContent()));
            documentRo.setDocumentVersionStatus(version.getStatus());
            // set ven
            version.getDocumentVersionEvents().stream().forEach(e -> {
                DocumentVersionEventRO eventRo = new DocumentVersionEventRO();
                eventRo.setEventType(e.getEventType());
                eventRo.setEventOn(e.getEventOn());
                eventRo.setUsername(e.getUsername());
                eventRo.setEventSourceType(e.getEventSourceType());
                eventRo.setDetails(e.getDetails());
                documentRo.addDocumentVersionEvent(eventRo);
            });
        }
        return documentRo;
    }

}
