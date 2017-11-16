/*
 * Copyright 2017 European Commission | CEF eDelivery
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 * or file: LICENCE-EUPL-v1.1.pdf
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package eu.europa.ec.edelivery.smp.services;

import eu.europa.ec.cipa.smp.server.conversion.CaseSensitivityNormalizer;
import eu.europa.ec.cipa.smp.server.conversion.ServiceMetadataConverter;
import eu.europa.ec.edelivery.smp.exceptions.NotFoundException;
import eu.europa.ec.cipa.smp.server.util.SignatureFilter;
import eu.europa.ec.edelivery.smp.data.dao.ServiceGroupDao;
import eu.europa.ec.edelivery.smp.data.dao.ServiceMetadataDao;
import eu.europa.ec.edelivery.smp.data.model.DBServiceMetadata;
import eu.europa.ec.edelivery.smp.data.model.DBServiceMetadataID;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

import static eu.europa.ec.smp.api.Identifiers.*;

/**
 * Created by gutowpa on 14/11/2017.
 */
@Service
public class ServiceMetadataService {

    private static final Logger log = LoggerFactory.getLogger(ServiceMetadataService.class);

    @Autowired
    private CaseSensitivityNormalizer caseSensitivityNormalizer;

    @Autowired
    private ServiceMetadataDao serviceMetadataDao;

    @Autowired
    private ServiceGroupDao serviceGroupDao;

    @Autowired
    private SignatureFilter signatureFilter;

    public Document getServiceMetadataDocument(ParticipantIdentifierType serviceGroupId, DocumentIdentifier documentId) {
        serviceGroupId = caseSensitivityNormalizer.normalize(serviceGroupId);
        documentId = caseSensitivityNormalizer.normalize(documentId);

        DBServiceMetadata serviceMetadata = serviceMetadataDao.find(serviceGroupId, documentId);

        if (serviceMetadata == null || serviceMetadata.getXmlContent() == null) {
            throw new NotFoundException("ServiceMetadata not found, ServiceGroupID: '%s', DocumentID: '%s'", asString(serviceGroupId), asString(documentId));
        }

        Document aSignedServiceMetadata = ServiceMetadataConverter.toSignedServiceMetadatadaDocument(serviceMetadata.getXmlContent());
        signatureFilter.sign(aSignedServiceMetadata);
        return aSignedServiceMetadata;
    }

    public boolean saveServiceMetadata(ParticipantIdentifierType serviceGroupId, DocumentIdentifier documentId, String xmlContent) {
        ParticipantIdentifierType normalizedServiceGroupId = caseSensitivityNormalizer.normalize(serviceGroupId);
        DocumentIdentifier normalizedDocId = caseSensitivityNormalizer.normalize(documentId);

        if (serviceGroupDao.find(normalizedServiceGroupId) == null) {
            throw new NotFoundException("ServiceGroup not found: '%s'", asString(serviceGroupId));
        }

        boolean newServiceCreated = serviceMetadataDao.remove(normalizedServiceGroupId, normalizedDocId);

        DBServiceMetadata serviceMetadata = new DBServiceMetadata();
        serviceMetadata.setId(new DBServiceMetadataID(normalizedServiceGroupId, normalizedDocId));
        serviceMetadata.setXmlContent(xmlContent);
        serviceMetadataDao.save(serviceMetadata);
        return newServiceCreated;
    }

    public void deleteServiceMetadata(ParticipantIdentifierType serviceGroupId, DocumentIdentifier documentId) {
        ParticipantIdentifierType normalizedServiceGroupId = caseSensitivityNormalizer.normalize(serviceGroupId);
        DocumentIdentifier normalizedDocId = caseSensitivityNormalizer.normalize(documentId);

        boolean serviceMetadataRemoved = serviceMetadataDao.remove(normalizedServiceGroupId, normalizedDocId);
        if (!serviceMetadataRemoved) {
            throw new NotFoundException("ServiceGroup not found: '%s'", asString(serviceGroupId));
        }
    }

    public List<DocumentIdentifier> findServiceMetadataIdentifiers(ParticipantIdentifierType participantId) {
        List<DBServiceMetadataID> metadataIds = serviceMetadataDao.findIdsByServiceGroup(participantId);

        List<DocumentIdentifier> documentIds = new ArrayList();
        for (DBServiceMetadataID metadataId : metadataIds) {
            DocumentIdentifier documentIdentifier = new DocumentIdentifier(metadataId.getDocumentTypeIdentifier(), metadataId.getDocumentTypeIdentifierScheme());
            documentIds.add(documentIdentifier);
        }
        return documentIds;
    }
}
