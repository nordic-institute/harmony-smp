package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.dao.DocumentDao;
import eu.europa.ec.edelivery.smp.data.dao.ResourceDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomainResourceDef;
import eu.europa.ec.edelivery.smp.data.model.doc.DBDocument;
import eu.europa.ec.edelivery.smp.data.model.doc.DBDocumentVersion;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.ui.DocumentRo;
import eu.europa.ec.edelivery.smp.exceptions.ErrorCode;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.resource.ResourceHandlerService;
import eu.europa.ec.edelivery.smp.services.spi.data.SpiResponseData;
import eu.europa.ec.smp.spi.api.model.RequestData;
import eu.europa.ec.smp.spi.api.model.ResponseData;
import eu.europa.ec.smp.spi.exceptions.ResourceException;
import eu.europa.ec.smp.spi.resource.ResourceHandlerSpi;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;

@Service
public class UIDocumentService {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(UIDocumentService.class);
    ResourceDao resourceDao;
    DocumentDao documentDao;
    ResourceHandlerService resourceHandlerService;

    public UIDocumentService(ResourceDao resourceDao, DocumentDao documentDao, ResourceHandlerService resourceHandlerService) {
        this.resourceDao = resourceDao;
        this.documentDao = documentDao;
        this.resourceHandlerService = resourceHandlerService;
    }

    @Transactional
    public void validateDocumentForResource(Long resourceId, DocumentRo documentRo) {
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
    public DocumentRo generateDocumentForResource(Long resourceId, DocumentRo documentRo) {
        LOG.info("Generate document");
        DBResource resource = resourceDao.find(resourceId);
        DBDomainResourceDef domainResourceDef = resource.getDomainResourceDef();
        ResourceHandlerSpi resourceHandler = resourceHandlerService.getResourceHandler(domainResourceDef.getResourceDef());
        RequestData data = resourceHandlerService.buildRequestDataForResource(domainResourceDef.getDomain(),
                resource, null);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ResponseData responseData = new SpiResponseData(bos);
        try {
            resourceHandler.generateResource(data, responseData, Collections.emptyList());
        } catch (ResourceException e) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "StoreResourceValidation", ExceptionUtils.getRootCauseMessage(e));
        }
        String genDoc =  new String(bos.toByteArray());
        LOG.info("Generate document [{}]", genDoc);
        DocumentRo result = new DocumentRo();
        result.setPayload(genDoc);
        return result;
    }

    @Transactional
    public DocumentRo saveDocumentForResource(Long resourceId, DocumentRo documentRo) {

        DBResource resource = resourceDao.find(resourceId);
        DBDomainResourceDef domainResourceDef = resource.getDomainResourceDef();
        ResourceHandlerSpi resourceHandler = resourceHandlerService.getResourceHandler(domainResourceDef.getResourceDef());
        RequestData data = resourceHandlerService.buildRequestDataForResource(domainResourceDef.getDomain(), resource, new ByteArrayInputStream(documentRo.getPayload().getBytes()));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ResponseData responseData = new SpiResponseData(bos);
        try {
            resourceHandler.storeResource(data, responseData);
        } catch (ResourceException e) {
            throw new SMPRuntimeException(ErrorCode.INVALID_REQUEST, "StoreResourceValidation", ExceptionUtils.getRootCauseMessage(e));
        }


        DBDocument document = resource.getDocument();
        int version = document.getDocumentVersions().stream().mapToInt(dv -> dv.getVersion())
                .max().orElse(0);

        DBDocumentVersion documentVersion = new DBDocumentVersion();
        documentVersion.setVersion(version + 1);
        documentVersion.setDocument(document);
        documentVersion.setContent(bos.toByteArray());
        document.getDocumentVersions().add(documentVersion);
        document.setCurrentVersion(documentVersion.getVersion());
        return convert(document, documentVersion);
    }

    /**
     * return version, if version does not exists return current version. if current version does not exists
     * return last version
     *
     * @param resourceId
     * @param version
     * @return
     */
    @Transactional
    public DocumentRo getDocumentForResource(Long resourceId, int version) {
        DBResource resource = resourceDao.find(resourceId);
        DBDocument document = resource.getDocument();
        DBDocumentVersion documentVersion = null;
        DBDocumentVersion currentVersion = null;


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
        return convert(document, documentVersion);
    }

    public DocumentRo convert(DBDocument document, DBDocumentVersion version) {
        DocumentRo documentRo = new DocumentRo();
        //documentRo.setDocumentId(SessionSecurityUtils.encryptedEntityId(document.getId()));
        document.getDocumentVersions().forEach(dv ->
                documentRo.getAllVersions().add(dv.getVersion()));

        documentRo.setMimeType(document.getMimeType());
        documentRo.setName(document.getName());
        documentRo.setCurrentResourceVersion(document.getCurrentVersion());
        if (version != null) {
            documentRo.setPayloadVersion(version.getVersion());
            documentRo.setPayload(new String(version.getContent()));
        }
        return documentRo;
    }
}
