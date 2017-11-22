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
import eu.europa.ec.cipa.smp.server.conversion.ServiceGroupConverter;
import eu.europa.ec.edelivery.smp.exceptions.NotFoundException;
import eu.europa.ec.edelivery.smp.exceptions.UnknownUserException;
import eu.europa.ec.cipa.smp.server.hook.IRegistrationHook;
import eu.europa.ec.edelivery.smp.data.dao.OwnershipDao;
import eu.europa.ec.edelivery.smp.data.dao.ServiceGroupDao;
import eu.europa.ec.edelivery.smp.data.dao.UserDao;
import eu.europa.ec.edelivery.smp.data.model.*;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private ServiceGroupDao serviceGroupDao;

    @Autowired
    private OwnershipDao ownershipDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private IRegistrationHook m_aHook;

    public ServiceGroup getServiceGroup(ParticipantIdentifierType serviceGroupId) {
        ParticipantIdentifierType normalizedServiceGroupId = caseSensitivityNormalizer.normalize(serviceGroupId);

        DBServiceGroup dbServiceGroup = serviceGroupDao.find(normalizedServiceGroupId);
        if (dbServiceGroup == null) {
            throw new NotFoundException("ServiceGroup not found: '%s'", asString(serviceGroupId));
        }
        return ServiceGroupConverter.toServiceGroup(dbServiceGroup);
    }

    @Transactional
    public boolean saveServiceGroup(ServiceGroup serviceGroup, String newOwnerName) {
        ServiceGroup normalizedServiceGroup = normalizeIdentifierCaseSensitivity(serviceGroup);
        ParticipantIdentifierType normalizedParticipantId = normalizedServiceGroup.getParticipantIdentifier();

        DBUser newOwner = userDao.find(newOwnerName);
        if (newOwner == null) {
            throw new UnknownUserException(newOwnerName);
        }

        DBServiceGroup aDBServiceGroup = serviceGroupDao.find(normalizedParticipantId);

        String extensions = ServiceGroupConverter.extractExtensionsPayload(normalizedServiceGroup);

        if (aDBServiceGroup != null) {
            aDBServiceGroup.setExtension(extensions);
            serviceGroupDao.update(aDBServiceGroup);
            return false;
        } else {
            // Register in SML (DNS)
            m_aHook.create(normalizedParticipantId);

            aDBServiceGroup = new DBServiceGroup(new DBServiceGroupID(normalizedParticipantId));
            aDBServiceGroup.setExtension(extensions);
            serviceGroupDao.save(aDBServiceGroup);

            // Save the ownership information
            final DBOwnershipID ownershipID = new DBOwnershipID(newOwnerName, normalizedParticipantId);
            final DBOwnership ownership = new DBOwnership(ownershipID, newOwner, aDBServiceGroup);
            //TODO trye to save ownership in one dbUpdate request
            ownershipDao.save(ownership);
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

    @Transactional
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
