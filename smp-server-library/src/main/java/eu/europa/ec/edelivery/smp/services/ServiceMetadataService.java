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
import eu.europa.ec.edelivery.smp.data.dao.ServiceGroupDao;
import eu.europa.ec.edelivery.smp.data.dao.ServiceMetadataDao;
import eu.europa.ec.edelivery.smp.data.model.DBDomain;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroup;
import eu.europa.ec.edelivery.smp.data.model.DBServiceGroupDomain;
import eu.europa.ec.edelivery.smp.data.model.DBServiceMetadata;
import eu.europa.ec.edelivery.smp.exceptions.SMPRuntimeException;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static eu.europa.ec.edelivery.smp.conversion.ServiceMetadataConverter.toSignedServiceMetadatadaDocument;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.METADATA_NOT_EXISTS;
import static eu.europa.ec.edelivery.smp.exceptions.ErrorCode.SG_NOT_EXISTS;


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
    private DomainService domainService;

    @Autowired
    private ServiceMetadataSigner signer;


    @Transactional
    public Document getServiceMetadataDocument(ParticipantIdentifierType serviceGroupId, DocumentIdentifier documentId) {

        ParticipantIdentifierType normalizedServiceGroupId = caseSensitivityNormalizer.normalize(serviceGroupId);
        DocumentIdentifier normalizedDocId = caseSensitivityNormalizer.normalize(documentId);

        Optional<DBServiceMetadata> osmd = serviceMetadataDao.findServiceMetadata(normalizedServiceGroupId.getValue(),
                normalizedServiceGroupId.getScheme(),normalizedDocId.getValue(),normalizedDocId.getScheme());


        if (!osmd.isPresent() || osmd.get().getXmlContent() == null) {
            throw new SMPRuntimeException(METADATA_NOT_EXISTS,normalizedServiceGroupId.getValue(),
                    normalizedServiceGroupId.getScheme(),normalizedDocId.getValue(),normalizedDocId.getScheme());
        }
        DBServiceMetadata smd = osmd.get();


        Document signedServiceMetadata = toSignedServiceMetadatadaDocument(smd.getXmlContent());
        String sigCertAlias = smd.getServiceGroupDomain().getDomain().getSignatureKeyAlias();
        signer.sign(signedServiceMetadata, sigCertAlias);
        return signedServiceMetadata;
    }

    /**
     * Creates or updates ServiceMetadata
     *
     * @return True if new ServiceMetadata was created. False if existing one was updated.
     */
    @Transactional
    public boolean saveServiceMetadata(String domain, ParticipantIdentifierType serviceGroupId, DocumentIdentifier documentId, byte[] xmlContent) {

        ParticipantIdentifierType normalizedServiceGroupId = caseSensitivityNormalizer.normalize(serviceGroupId);
        DocumentIdentifier normalizedDocId = caseSensitivityNormalizer.normalize(documentId);

        Optional<DBServiceGroup> serviceGroup = serviceGroupDao.findServiceGroup(normalizedServiceGroupId.getValue(),
                normalizedServiceGroupId.getScheme());
        if (!serviceGroup.isPresent()) {
            throw new SMPRuntimeException(SG_NOT_EXISTS, normalizedServiceGroupId.getValue(),
                    normalizedServiceGroupId.getScheme());
        }
        //test and retrieve domain
        DBDomain dbDomain = domainService.getDomain(domain);

        Optional<DBServiceMetadata> doc =  serviceMetadataDao.findServiceMetadata(normalizedServiceGroupId.getValue(),
                normalizedServiceGroupId.getScheme(), normalizedDocId.getValue(), normalizedDocId.getScheme());

        //TODO: domain for servicegroup!!
        //DBDomain dbDomain = serviceDomain.getDomain(domain);

        boolean alreadyExisted = false;
        if (doc.isPresent()){
            DBServiceMetadata smd = doc.get();
            smd.setXmlContent(xmlContent);
            serviceMetadataDao.update(smd);
            alreadyExisted = true;
        } else {
            DBServiceGroup sg = serviceGroup.get();
            DBServiceMetadata smd = new DBServiceMetadata();
            smd.setDocumentIdentifier(normalizedDocId.getValue());
            smd.setDocumentIdentifierScheme(normalizedDocId.getScheme());
            smd.setXmlContent(xmlContent);
            Optional<DBServiceGroupDomain> osgd =  sg.getServiceGroupForDomain(domain);
            DBServiceGroupDomain sgd = osgd.isPresent()?osgd.get(): sg.addDomain(dbDomain);
            sgd.addServiceMetadata(smd);
            serviceGroupDao.update(sg);
            alreadyExisted = false;
        }

        return !alreadyExisted;
    }

    @Transactional
    public void deleteServiceMetadata(String domain, ParticipantIdentifierType serviceGroupId, DocumentIdentifier documentId) {

        ParticipantIdentifierType normalizedServiceGroupId = caseSensitivityNormalizer.normalize(serviceGroupId);
        DocumentIdentifier normalizedDocId = caseSensitivityNormalizer.normalize(documentId);


        Optional<DBServiceMetadata> oDoc = serviceMetadataDao.findServiceMetadata(normalizedServiceGroupId.getValue(),
                normalizedServiceGroupId.getScheme(), normalizedDocId.getValue(), normalizedDocId.getScheme());
        if (!oDoc.isPresent()){
            throw new SMPRuntimeException(METADATA_NOT_EXISTS,normalizedServiceGroupId.getValue(),
                    normalizedServiceGroupId.getScheme(),normalizedDocId.getValue(),normalizedDocId.getScheme());
        }
        DBServiceMetadata doc = oDoc.get();
        DBServiceGroupDomain sgd = doc.getServiceGroupDomain();
        sgd.removeServiceMetadata(doc);
        serviceGroupDao.update(sgd.getServiceGroup());
    }

    public List<DocumentIdentifier> findServiceMetadataIdentifiers(ParticipantIdentifierType participantId) {

        ParticipantIdentifierType normalizedServiceGroupId = caseSensitivityNormalizer.normalize(participantId);
        List<DBServiceMetadata> metadata = serviceMetadataDao.getAllMetadataForServiceGroup(
                normalizedServiceGroupId.getValue(),
                normalizedServiceGroupId.getScheme());

        List<DocumentIdentifier> documentIds = new ArrayList();
        for (DBServiceMetadata md : metadata) {
            DocumentIdentifier documentIdentifier = new DocumentIdentifier(md.getDocumentIdentifier(),
                    md.getDocumentIdentifierScheme());
            documentIds.add(documentIdentifier);
        }
        return documentIds;
    }


}
