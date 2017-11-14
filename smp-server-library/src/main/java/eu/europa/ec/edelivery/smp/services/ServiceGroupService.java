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
import eu.europa.ec.cipa.smp.server.errors.exceptions.NotFoundException;
import eu.europa.ec.cipa.smp.server.errors.exceptions.UnknownUserException;
import eu.europa.ec.cipa.smp.server.hook.IRegistrationHook;
import eu.europa.ec.cipa.smp.server.util.ExtensionUtils;
import eu.europa.ec.cipa.smp.server.util.IdentifierUtils;
import eu.europa.ec.edelivery.smp.data.dao.OwnershipDao;
import eu.europa.ec.edelivery.smp.data.dao.ServiceGroupDao;
import eu.europa.ec.edelivery.smp.data.dao.ServiceMetadataDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.*;
import eu.europa.ec.smp.api.Identifiers;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.List;

import static eu.europa.ec.smp.api.Identifiers.asString;

/**
 * Created by gutowpa on 14/11/2017.
 */
@Service
public class ServiceGroupService {

    private static final Logger log = LoggerFactory.getLogger(ServiceGroupService.class);

    @Autowired
    private CaseSensitivityNormalizer caseSensitivityNormalizer;

    @Autowired
    private ServiceMetadataDao serviceMetadataDao;

    @Autowired
    private ServiceGroupDao serviceGroupDao;

    @Autowired
    private OwnershipDao ownershipDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private IRegistrationHook m_aHook;

    @Autowired
    private EntityManager entityManager;


    public ServiceGroup getServiceGroup(String serviceGroupIdStr) {
        final ParticipantIdentifierType serviceGroupId = Identifiers.asParticipantId(serviceGroupIdStr);

        ParticipantIdentifierType normalizedServiceGroupId = caseSensitivityNormalizer.normalize(serviceGroupId);

        DBServiceGroup dbServiceGroup = serviceGroupDao.find(normalizedServiceGroupId);
        if (dbServiceGroup == null) {
            throw new NotFoundException("ServiceGroup not found: '%s'", asString(serviceGroupId));
        }

        // Convert service group DB to service group service
        final ServiceGroup serviceGroup = new ServiceGroup();
        serviceGroup.setParticipantIdentifier(normalizedServiceGroupId);

        try {
            List<ExtensionType> extensions = ExtensionUtils.unmarshalExtensions(dbServiceGroup.getExtension());
            serviceGroup.getExtensions().addAll(extensions);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        serviceGroup.setServiceMetadataReferenceCollection(new ServiceMetadataReferenceCollectionType(new ArrayList()));
        return serviceGroup;
    }

    public boolean saveServiceGroup(ServiceGroup serviceGroup, String newOwnerName) {
        ServiceGroup normalizedServiceGroup = normalizeIdentifierCaseSensitivity(serviceGroup);
        ParticipantIdentifierType normalizedParticipantId = normalizedServiceGroup.getParticipantIdentifier();

        DBUser newOwner = userDao.findUser(newOwnerName);
        if (newOwner == null) {
            throw new UnknownUserException(newOwnerName);
        }

        DBServiceGroup aDBServiceGroup = serviceGroupDao.find(normalizedParticipantId);

        String extensions = null;
        try {
            extensions = ExtensionUtils.marshalExtensions(normalizedServiceGroup.getExtensions());
        } catch (JAXBException | XMLStreamException e) {
            throw new RuntimeException(e);
        }

        if (aDBServiceGroup != null) {
            aDBServiceGroup.setExtension(extensions);
            serviceGroupDao.update(aDBServiceGroup);
            return false;
        } else {
            // Register in SML
            m_aHook.create(normalizedParticipantId);

            // Did not exist, create it
            aDBServiceGroup = new DBServiceGroup(new DBServiceGroupID(normalizedParticipantId));
            aDBServiceGroup.setExtension(extensions);
            serviceGroupDao.save(aDBServiceGroup);

            // Save the ownership information
            final DBOwnershipID aDBOwnershipID = new DBOwnershipID(newOwnerName, normalizedParticipantId);
            final DBOwnership aDBOwnership = new DBOwnership(aDBOwnershipID, newOwner, aDBServiceGroup);
            //TODO save ownership in one dbUpdate request
            entityManager.persist(aDBOwnership);
            return true;
        }
    }


    private ServiceGroup normalizeIdentifierCaseSensitivity(ServiceGroup serviceGroup) {
        final ServiceGroup sg = new ServiceGroup();
        sg.setParticipantIdentifier(caseSensitivityNormalizer.normalize(serviceGroup.getParticipantIdentifier()));
        sg.setServiceMetadataReferenceCollection(serviceGroup.getServiceMetadataReferenceCollection());
        sg.getExtensions().addAll(serviceGroup.getExtensions());
        return sg;
    }

    public void deleteServiceGroup(ParticipantIdentifierType serviceGroupId) {
        final ParticipantIdentifierType normalizedServiceGroupId = caseSensitivityNormalizer.normalize(serviceGroupId);

        DBServiceGroup dbServiceGroup = serviceGroupDao.find(normalizedServiceGroupId);
        if (dbServiceGroup == null) {
            throw new NotFoundException("ServiceGroup not found: '%s'", asString(serviceGroupId));
        }

        ownershipDao.removeByServiceGroupId(dbServiceGroup.getId());
        serviceGroupDao.remove(dbServiceGroup);

        m_aHook.delete(normalizedServiceGroupId);
    }
}
