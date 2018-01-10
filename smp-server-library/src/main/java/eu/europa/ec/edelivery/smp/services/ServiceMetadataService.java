/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence attached in file: LICENCE-EUPL-v1.2.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.edelivery.smp.conversion.CaseSensitivityNormalizer;
import eu.europa.ec.edelivery.smp.conversion.ServiceGroupConverter;
import eu.europa.ec.edelivery.smp.data.dao.ServiceGroupDao;
import eu.europa.ec.edelivery.smp.data.dao.ServiceMetadataDao;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.data.model.DBServiceMetadata;
import eu.europa.ec.edelivery.smp.data.model.DBServiceMetadataId;
import eu.europa.ec.edelivery.smp.exceptions.NotFoundException;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

import static eu.europa.ec.edelivery.smp.conversion.ServiceMetadataConverter.toDbModel;
import static eu.europa.ec.edelivery.smp.conversion.ServiceMetadataConverter.toSignedServiceMetadatadaDocument;
import static eu.europa.ec.smp.api.Identifiers.asString;

/**
 * Created by gutowpa on 14/11/2017.
 */
@Service
public class ServiceMetadataService {

    @Autowired
    private CaseSensitivityNormalizer caseSensitivityNormalizer;

    @Autowired
    private ServiceMetadataDao serviceMetadataDao;

    @Autowired
    private ServiceGroupDao serviceGroupDao;

    @Autowired
    private ServiceMetadataSigner signatureFilter;

    public Document getServiceMetadataDocument(ParticipantIdentifierType serviceGroupId, DocumentIdentifier documentId) {
        ParticipantIdentifierType normalizedServiceGroupId = caseSensitivityNormalizer.normalize(serviceGroupId);
        DocumentIdentifier normalizedDocId = caseSensitivityNormalizer.normalize(documentId);

        DBServiceMetadata serviceMetadata = serviceMetadataDao.find(toDbModel(normalizedServiceGroupId, normalizedDocId));

        if (serviceMetadata == null || serviceMetadata.getXmlContent() == null) {
            throw new NotFoundException("ServiceMetadata not found, ServiceGroupID: '%s', DocumentID: '%s'", asString(serviceGroupId), asString(documentId));
        }

        Document aSignedServiceMetadata = toSignedServiceMetadatadaDocument(serviceMetadata.getXmlContent());
        signatureFilter.sign(aSignedServiceMetadata);
        return aSignedServiceMetadata;
    }

    /**
     * Creates or updates ServiceMetadata
     *
     * @return True if new ServiceMetadata was created. False if existing one was updated.
     */
    @Transactional
    public boolean saveServiceMetadata(ParticipantIdentifierType serviceGroupId, DocumentIdentifier documentId, String xmlContent) {
        ParticipantIdentifierType normalizedServiceGroupId = caseSensitivityNormalizer.normalize(serviceGroupId);
        DocumentIdentifier normalizedDocId = caseSensitivityNormalizer.normalize(documentId);

        DBServiceGroup serviceGroup = serviceGroupDao.find(ServiceGroupConverter.toDbModel(normalizedServiceGroupId));
        if (serviceGroup == null) {
            throw new NotFoundException("ServiceGroup not found: '%s'", asString(serviceGroupId));
        }

        DBServiceMetadataId dbServiceMetadataId = toDbModel(normalizedServiceGroupId, normalizedDocId);
        boolean alreadyExisted = serviceMetadataDao.removeById(dbServiceMetadataId);

        DBServiceMetadata dbServiceMetadata = new DBServiceMetadata();
        dbServiceMetadata.setId(dbServiceMetadataId);

        dbServiceMetadata.setXmlContent(xmlContent);
        serviceMetadataDao.save(dbServiceMetadata);
        return !alreadyExisted;
    }

    @Transactional
    public void deleteServiceMetadata(ParticipantIdentifierType serviceGroupId, DocumentIdentifier documentId) {
        ParticipantIdentifierType normalizedServiceGroupId = caseSensitivityNormalizer.normalize(serviceGroupId);
        DocumentIdentifier normalizedDocId = caseSensitivityNormalizer.normalize(documentId);

        DBServiceMetadataId dbServiceMetadataId = toDbModel(normalizedServiceGroupId, normalizedDocId);
        boolean serviceMetadataRemoved = serviceMetadataDao.removeById(dbServiceMetadataId);

        if (!serviceMetadataRemoved) {
            throw new NotFoundException("ServiceGroup not found: '%s'", asString(serviceGroupId));
        }
    }

    public List<DocumentIdentifier> findServiceMetadataIdentifiers(ParticipantIdentifierType participantId) {
        ParticipantIdentifierType normalizedServiceGroupId = caseSensitivityNormalizer.normalize(participantId);
        List<DBServiceMetadataId> metadataIds = serviceMetadataDao.findIdsByServiceGroup(
                normalizedServiceGroupId.getScheme(),
                normalizedServiceGroupId.getValue());

        List<DocumentIdentifier> documentIds = new ArrayList();
        for (DBServiceMetadataId metadataId : metadataIds) {
            DocumentIdentifier documentIdentifier = new DocumentIdentifier(metadataId.getDocumentIdentifier(), metadataId.getDocumentIdentifierScheme());
            documentIds.add(documentIdentifier);
        }
        return documentIds;
    }
}
