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

import eu.europa.ec.edelivery.smp.conversion.IdentifierService;
import eu.europa.ec.edelivery.smp.data.dao.ResourceDao;
import eu.europa.ec.edelivery.smp.data.dao.SubresourceDao;
import eu.europa.ec.edelivery.smp.identifiers.Identifier;
import eu.europa.ec.edelivery.smp.services.spi.SmpXmlSignatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

import java.util.Collections;
import java.util.List;


/**
 * Created by gutowpa on 14/11/2017.
 */
@Service
public class ServiceMetadataService {

    @Autowired
    private IdentifierService identifierService;

    @Autowired
    private SubresourceDao serviceMetadataDao;

    @Autowired
    private ResourceDao serviceGroupDao;

    @Autowired
    private DomainService domainService;

    @Autowired
    private SmpXmlSignatureService signer;


    @Transactional
    public Document getServiceMetadataDocument(Identifier serviceGroupId, Identifier documentId) {
/*
        Identifier normalizedServiceGroupId = identifierService.normalizeParticipant(serviceGroupId);
        Identifier normalizedDocId = identifierService.normalizeDocument(documentId);

        Optional<DBSubresource> osmd = serviceMetadataDao.findServiceMetadata(normalizedServiceGroupId.getValue(),
                normalizedServiceGroupId.getScheme(),normalizedDocId.getValue(),normalizedDocId.getScheme());


        if (!osmd.isPresent() || osmd.get().getXmlContent() == null) {
            throw new SMPRuntimeException(METADATA_NOT_EXISTS,normalizedServiceGroupId.getValue(),
                    normalizedServiceGroupId.getScheme(),normalizedDocId.getValue(),normalizedDocId.getScheme());
        }
        DBSubresource smd = osmd.get();

        Document signedServiceMetadata = toSignedServiceMetadataDocument(smd.getXmlContent());
        DBDomain resourceDomain = smd.getResource().getDomainResourceDef().getDomain();
        String sigCertAlias = resourceDomain.getSignatureKeyAlias();
        String signatureAlgorithm = resourceDomain.getSignatureAlgorithm();
        String signatureDigestMethod = resourceDomain.getSignatureDigestMethod();

        signer.sign(signedServiceMetadata, sigCertAlias, signatureAlgorithm,signatureDigestMethod );
        return signedServiceMetadata;

 */
        return null;
    }

    /**
     * Creates or updates ServiceMetadata
     *
     * @return True if new ServiceMetadata was created. False if existing one was updated.
     */
    @Transactional
    public boolean saveServiceMetadata(String domain, Identifier serviceGroupId, Identifier documentId, byte[] xmlContent) {
/*
        Identifier normalizedServiceGroupId = identifierService.normalizeParticipant(serviceGroupId);
        Identifier normalizedDocId = identifierService.normalizeDocument(documentId);

        Optional<DBResource> serviceGroup = serviceGroupDao.findServiceGroup(normalizedServiceGroupId.getValue(),
                normalizedServiceGroupId.getScheme());
        if (!serviceGroup.isPresent()) {
            throw new SMPRuntimeException(SG_NOT_EXISTS, normalizedServiceGroupId.getValue(),
                    normalizedServiceGroupId.getScheme());
        }
        //test and retrieve domain
        DBDomain dbDomain = domainService.getDomain(domain);

        Optional<DBSubresource> doc =  serviceMetadataDao.findServiceMetadata(normalizedServiceGroupId.getValue(),
                normalizedServiceGroupId.getScheme(), normalizedDocId.getValue(), normalizedDocId.getScheme());

        boolean alreadyExisted;
        if (doc.isPresent()){
            DBSubresource smd = doc.get();
            smd.setXmlContent(xmlContent);
            serviceMetadataDao.update(smd);
            alreadyExisted = true;
        } else {
            DBResource sg = serviceGroup.get();
            DBSubresource smd = new DBSubresource();
            smd.setDocumentIdentifier(normalizedDocId.getValue());
            smd.setDocumentIdentifierScheme(normalizedDocId.getScheme());
            smd.setXmlContent(xmlContent);
            Optional<DBDomainResourceDef> osgd =  sg.getServiceGroupForDomain(domain);
            DBDomainResourceDef sgd = osgd.isPresent()?osgd.get(): sg.addDomain(dbDomain);
            sgd.addServiceMetadata(smd);
            serviceGroupDao.update(sg);
            alreadyExisted = false;
        }

        return !alreadyExisted;

 */
        return  false;

    }

    @Transactional
    public void deleteServiceMetadata(String domain, Identifier serviceGroupId, Identifier documentId) {
/*
        ParticipantIdentifierType normalizedServiceGroupId = identifierService.normalizeParticipant(serviceGroupId);
        DocumentIdentifier normalizedDocId = identifierService.normalizeDocument(documentId);


        Optional<DBSubresource> oDoc = serviceMetadataDao.findServiceMetadata(normalizedServiceGroupId.getValue(),
                normalizedServiceGroupId.getScheme(), normalizedDocId.getValue(), normalizedDocId.getScheme());
        if (!oDoc.isPresent()){
            throw new SMPRuntimeException(METADATA_NOT_EXISTS,normalizedServiceGroupId.getValue(),
                    normalizedServiceGroupId.getScheme(),normalizedDocId.getValue(),normalizedDocId.getScheme());
        }
        DBSubresource doc = oDoc.get();
        DBDomainResourceDef sgd = doc.getServiceGroupDomain();
        sgd.removeServiceMetadata(doc);
        serviceGroupDao.update(sgd.getServiceGroup());

 */
    }

    public List<Identifier> findServiceMetadataIdentifiers(Identifier participantId) {
/*
        Identifier normalizedServiceGroupId = identifierService.normalizeParticipant(participantId);
        List<DBSubresource> metadata = serviceMetadataDao.getAllMetadataForServiceGroup(
                normalizedServiceGroupId.getValue(),
                normalizedServiceGroupId.getScheme());

        List<Identifier> documentIds = new ArrayList<>();
        for (DBSubresource md : metadata) {
            Identifier documentIdentifier = new Identifier(md.getIdentifierValue(),
                    md.getIdentifierScheme());
            documentIds.add(documentIdentifier);
        }
        return documentIds;

 */
        return Collections.emptyList();
    }


}
