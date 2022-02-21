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

package eu.europa.ec.edelivery.smp.controllers;

import eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority;
import eu.europa.ec.edelivery.smp.conversion.ServiceGroupConverter;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.logging.SMPMessageCode;
import eu.europa.ec.edelivery.smp.services.ServiceGroupService;
import eu.europa.ec.edelivery.smp.services.ServiceMetadataService;
import eu.europa.ec.edelivery.smp.validation.ServiceGroupValidator;
import eu.europa.ec.smp.api.Identifiers;
import eu.europa.ec.smp.api.exceptions.XmlInvalidAgainstSchemaException;
import eu.europa.ec.smp.api.validators.BdxSmpOasisValidator;
import org.apache.commons.lang.StringUtils;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceMetadataReferenceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static eu.europa.ec.edelivery.smp.controllers.WebConstans.HTTP_PARAM_DOMAIN;
import static eu.europa.ec.edelivery.smp.controllers.WebConstans.HTTP_PARAM_OWNER;
import static eu.europa.ec.smp.api.Identifiers.asParticipantId;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;

/**
 * Created by gutowpa on 12/07/2017.
 */

@RestController
@RequestMapping("/{serviceGroupId:^(?!ui).*}")
public class ServiceGroupController {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ServiceGroupController.class);

    @Autowired
    private ServiceGroupValidator serviceGroupValidator;

    @Autowired
    private SmpUrlBuilder pathBuilder;

    @Autowired
    private ServiceGroupService serviceGroupService;

    @Autowired
    private ServiceMetadataService serviceMetadataService;


    @GetMapping(produces = "text/xml; charset=UTF-8")
    public ServiceGroup getServiceGroup(HttpServletRequest httpReq, @PathVariable String serviceGroupId) {


        String host = httpReq.getRemoteHost();
        LOG.businessInfo(SMPMessageCode.BUS_HTTP_GET_SERVICE_GROUP,host, serviceGroupId);

        ServiceGroup serviceGroup = serviceGroupService.getServiceGroup(asParticipantId(serviceGroupId));
        addReferences(serviceGroup);

        LOG.businessInfo(SMPMessageCode.BUS_HTTP_GET_END_SERVICE_GROUP,host, serviceGroupId);
        return serviceGroup;
    }


    @PutMapping
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN, SMPAuthority.S_AUTHORITY_TOKEN_SMP_ADMIN})
    public ResponseEntity saveServiceGroup(HttpServletRequest httpReq,
            @PathVariable String serviceGroupId,
            @RequestHeader(name = HTTP_PARAM_OWNER, required = false) String serviceGroupOwner,
            @RequestHeader(name = HTTP_PARAM_DOMAIN, required = false) String domain,
            @RequestBody byte[] body) throws XmlInvalidAgainstSchemaException {

        String authentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        String host = getRemoteHost(httpReq);
        LOG.businessInfo(SMPMessageCode.BUS_HTTP_PUT_SERVICE_GROUP,authentUser, host, serviceGroupOwner, domain, serviceGroupId);

        // Validations
        BdxSmpOasisValidator.validateXSD(body);
        final ServiceGroup serviceGroup = ServiceGroupConverter.unmarshal(body);
        serviceGroupValidator.validate(serviceGroupId, serviceGroup);

        // Service action
         boolean newServiceGroupCreated = serviceGroupService.saveServiceGroup(serviceGroup, domain, serviceGroupOwner, authentUser);

        LOG.businessInfo(SMPMessageCode.BUS_HTTP_PUT_SERVICE_GROUP,authentUser, host, serviceGroupOwner, domain, serviceGroupId, newServiceGroupCreated);
        return newServiceGroupCreated ? created(pathBuilder.getCurrentUri()).build() : ok().build();
    }

    @DeleteMapping
    @Secured({SMPAuthority.S_AUTHORITY_TOKEN_SYSTEM_ADMIN, SMPAuthority.S_AUTHORITY_TOKEN_SMP_ADMIN})
    public ResponseEntity deleteServiceGroup(HttpServletRequest httpReq, @PathVariable String serviceGroupId) {
        String authentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        String host = getRemoteHost(httpReq);
        LOG.businessInfo(SMPMessageCode.BUS_HTTP_DELETE_SERVICE_GROUP,authentUser, host, serviceGroupId);


        final ParticipantIdentifierType aServiceGroupID = Identifiers.asParticipantId(serviceGroupId);
        serviceGroupService.deleteServiceGroup(aServiceGroupID);

        LOG.businessInfo(SMPMessageCode.BUS_HTTP_DELETE_END_SERVICE_GROUP,authentUser, host, serviceGroupId);
        return ok().build();
    }

    private void addReferences(ServiceGroup serviceGroup) {
        ParticipantIdentifierType participantId = serviceGroup.getParticipantIdentifier();
        List<DocumentIdentifier> docIds = serviceMetadataService.findServiceMetadataIdentifiers(participantId);
        List<ServiceMetadataReferenceType> referenceIds = serviceGroup.getServiceMetadataReferenceCollection().getServiceMetadataReferences();
        for (DocumentIdentifier docId : docIds) {
            String url = pathBuilder.buildSMPUrlForParticipantAndDocumentIdentifier(participantId, docId);
            referenceIds.add(new ServiceMetadataReferenceType(url));
        }
    }

    public String getRemoteHost(HttpServletRequest httpReq){
        String host = httpReq.getHeader("X-Forwarded-For");
        return StringUtils.isBlank(host)?httpReq.getRemoteHost():host;
    }

}
