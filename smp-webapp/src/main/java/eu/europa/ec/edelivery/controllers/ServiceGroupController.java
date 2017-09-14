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

package eu.europa.ec.edelivery.controllers;

import eu.europa.ec.cipa.smp.server.conversion.CaseSensitivityNormalizer;
import eu.europa.ec.cipa.smp.server.conversion.ServiceGroupConverter;
import eu.europa.ec.cipa.smp.server.data.IDataManager;
import eu.europa.ec.cipa.smp.server.services.BaseServiceGroupInterfaceImpl;
import eu.europa.ec.cipa.smp.server.services.BaseServiceMetadataInterfaceImpl;
import eu.europa.ec.edelivery.validation.ServiceGroupValidator;
import eu.europa.ec.smp.api.Identifiers;
import eu.europa.ec.smp.api.exceptions.XmlInvalidAgainstSchemaException;
import eu.europa.ec.smp.api.validators.BdxSmpOasisValidator;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceMetadataReferenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static eu.europa.ec.smp.api.Identifiers.asString;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.slf4j.helpers.Util.getCallingClass;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;

/**
 * Created by gutowpa on 12/07/2017.
 */

@RestController
@RequestMapping("/{serviceGroupId}")
@Order
public class ServiceGroupController {

    private static final Logger log = LoggerFactory.getLogger(getCallingClass());

    @Autowired
    ServiceGroupValidator serviceGroupValidator;

    @Autowired
    private CaseSensitivityNormalizer caseSensitivityNormalizer;

    //TODO Migrate to Service (add one more level)
    @Autowired
    private IDataManager dataManager;

    @Autowired
    private ServiceMetadataPathBuilder pathBuilder;

    @Autowired
    private BaseServiceGroupInterfaceImpl serviceGroupService;

    @Autowired
    private BaseServiceMetadataInterfaceImpl serviceMetadataService;


    @GetMapping(produces = TEXT_XML_VALUE)
    public ServiceGroup getServiceGroup(@PathVariable String serviceGroupId) {
        log.info("GET ServiceGrooup: " + serviceGroupId);

        ServiceGroup serviceGroup = serviceGroupService.getServiceGroup(serviceGroupId);
        addReferences(serviceGroup);

        log.info("Finished GET ServiceGrooup: " + serviceGroupId);
        return serviceGroup;
    }


    @PutMapping
    @Secured("ROLE_SMP_ADMIN")
    public ResponseEntity saveServiceGroup(
            @PathVariable String serviceGroupId,
            @RequestHeader(name = "ServiceGroup-Owner", required = false) String serviceGroupOwner,
            @RequestBody String body) throws XmlInvalidAgainstSchemaException {

        log.info("PUT ServiceGroup: %s\n%s", serviceGroupId, body);

        // Validations
        BdxSmpOasisValidator.validateXSD(body);
        final ServiceGroup serviceGroup = ServiceGroupConverter.unmarshal(body);
        serviceGroupValidator.validate(serviceGroupId, serviceGroup);

        // Service action
        String newOwnerName = isNotBlank(serviceGroupOwner) ? serviceGroupOwner : SecurityContextHolder.getContext().getAuthentication().getName();
        final ServiceGroup normalizedServiceGroup = normalizeIdentifierCaseSensitivity(serviceGroup);
        boolean newServiceGroupCreated = dataManager.saveServiceGroup(normalizedServiceGroup, newOwnerName);

        log.info("Finished PUT ServiceGroup: %s", serviceGroupId);

        return newServiceGroupCreated ? created(pathBuilder.getCurrentUri()).build() : ok().build();
    }

    @DeleteMapping
    @Secured("ROLE_SMP_ADMIN")
    public void deleteServiceGroup(@PathVariable String serviceGroupId) {

        log.info("DELETE ServiceGroup: %s", serviceGroupId);

        final ParticipantIdentifierType aServiceGroupID = Identifiers.asParticipantId(serviceGroupId);
        dataManager.deleteServiceGroup(aServiceGroupID);

        log.info("Finished DELETE ServiceGroup: %s", serviceGroupId);
    }

    private void addReferences(ServiceGroup serviceGroup) {
        ParticipantIdentifierType participantId = serviceGroup.getParticipantIdentifier();
        List<DocumentIdentifier> docIds = serviceMetadataService.getMetadataIdentifiers(asString(participantId));
        List<ServiceMetadataReferenceType> referenceIds = serviceGroup.getServiceMetadataReferenceCollection().getServiceMetadataReferences();
        for (DocumentIdentifier docId : docIds) {
            String url = pathBuilder.buildSelfUrl(participantId, docId);
            referenceIds.add(new ServiceMetadataReferenceType(url));
        }
    }


    private ServiceGroup normalizeIdentifierCaseSensitivity(ServiceGroup serviceGroup) {
        final ServiceGroup sg = new ServiceGroup();
        sg.setParticipantIdentifier(caseSensitivityNormalizer.normalize(serviceGroup.getParticipantIdentifier()));
        sg.setServiceMetadataReferenceCollection(serviceGroup.getServiceMetadataReferenceCollection());
        sg.getExtensions().addAll(serviceGroup.getExtensions());
        return sg;
    }


}
