package eu.europa.ec.edelivery.smp.services.ui;

import eu.europa.ec.edelivery.smp.data.dao.DocumentDao;
import eu.europa.ec.edelivery.smp.data.dao.ResourceDao;
import eu.europa.ec.edelivery.smp.data.model.doc.DBDocument;
import eu.europa.ec.edelivery.smp.data.model.doc.DBDocumentVersion;
import eu.europa.ec.edelivery.smp.data.model.doc.DBResource;
import eu.europa.ec.edelivery.smp.data.ui.DocumentRo;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class UIDocumentService {

    ResourceDao resourceDao;
    DocumentDao documentDao;

    public UIDocumentService(ResourceDao resourceDao, DocumentDao documentDao) {
        this.resourceDao = resourceDao;
        this.documentDao = documentDao;
    }

    public void validateDocumentForResource(Long resourceId, DocumentRo documentRo){

    }

    @Transactional
    public DocumentRo saveDocumentForResource(Long resourceId, DocumentRo documentRo){
        DBResource resource = resourceDao.find(resourceId);
        DBDocument document = resource.getDocument();
        int version = document.getDocumentVersions().stream().mapToInt(dv -> dv.getVersion() )
                .max().orElse(0);
        DBDocumentVersion documentVersion = new DBDocumentVersion();
        documentVersion.setVersion(version +1);
        documentVersion.setDocument(document);
        documentVersion.setContent(documentRo.getPayload().getBytes());
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
    public DocumentRo getDocumentForResource(Long resourceId, int version){
        DBResource resource = resourceDao.find(resourceId);
        DBDocument document = resource.getDocument();
        DBDocumentVersion documentVersion  = null;
        DBDocumentVersion currentVersion  = null;


        for (DBDocumentVersion dv : document.getDocumentVersions()) {
            if (dv.getVersion() == version) {
                documentVersion = dv;
            }
            if (dv.getVersion() == document.getCurrentVersion()) {
                currentVersion = dv;
            }
        }
        documentVersion = documentVersion ==null? currentVersion:documentVersion;
        if (documentVersion == null && !document.getDocumentVersions().isEmpty()){
            documentVersion = document.getDocumentVersions().get(document.getDocumentVersions().size()-1);
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
