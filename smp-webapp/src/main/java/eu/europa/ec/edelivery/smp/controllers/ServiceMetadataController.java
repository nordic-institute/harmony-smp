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

import eu.europa.ec.edelivery.smp.conversion.ServiceMetadataConverter;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.logging.SMPMessageCode;
import eu.europa.ec.edelivery.smp.services.ServiceGroupService;
import eu.europa.ec.edelivery.smp.services.ServiceMetadataService;
import eu.europa.ec.edelivery.smp.validation.ServiceMetadataValidator;
import eu.europa.ec.smp.api.exceptions.XmlInvalidAgainstSchemaException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.TransformerException;
import java.io.UnsupportedEncodingException;

import static eu.europa.ec.edelivery.smp.controllers.WebConstans.HTTP_PARAM_DOMAIN;
import static eu.europa.ec.smp.api.Identifiers.asDocumentId;
import static eu.europa.ec.smp.api.Identifiers.asParticipantId;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;

/**
 * Created by gutowpa on 11/09/2017.
 */
@RestController
@RequestMapping("/{serviceGroupId}/services/{serviceMetadataId}")
public class ServiceMetadataController {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ServiceGroupController.class);

    @Autowired
    private ServiceMetadataValidator serviceMetadataValidator;

    @Autowired
    private ServiceMetadataService serviceMetadataService;


    @Autowired
    private ServiceMetadataPathBuilder pathBuilder;

    @GetMapping(produces = "text/xml; charset=UTF-8")
    public String getServiceMetadata(HttpServletRequest httpReq,
                                     @PathVariable String serviceGroupId,
                                     @PathVariable String serviceMetadataId) throws TransformerException, UnsupportedEncodingException {

        String host = httpReq.getRemoteHost();
        LOG.businessInfo(SMPMessageCode.BUS_HTTP_GET_SERVICE_METADATA,host, serviceGroupId, serviceMetadataId);

        Document serviceMetadata = serviceMetadataService.getServiceMetadataDocument(asParticipantId(serviceGroupId), asDocumentId(serviceMetadataId));

        LOG.businessInfo(SMPMessageCode.BUS_HTTP_GET_END_SERVICE_METADATA,host, serviceGroupId, serviceMetadataId);
        return ServiceMetadataConverter.toString(serviceMetadata);
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority(T(eu.europa.ec.edelivery.smp.auth.SMPAuthority).S_AUTHORITY_TOKEN_SMP_ADMIN) OR" +
            " @serviceGroupService.isServiceGroupOwner(authentication.name, #serviceGroupId)")
    public ResponseEntity saveServiceMetadata(HttpServletRequest httpReq,
            @PathVariable String serviceGroupId,
            @PathVariable String serviceMetadataId,
            @RequestHeader(name = HTTP_PARAM_DOMAIN, required = false) String domain,
            @RequestBody byte[] body) throws XmlInvalidAgainstSchemaException {

        String authentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        String host = getRemoteHost(httpReq);
        LOG.businessInfo(SMPMessageCode.BUS_HTTP_PUT_SERVICE_METADATA,authentUser, host, domain, serviceGroupId, serviceMetadataId);

        serviceMetadataValidator.validate(serviceGroupId, serviceMetadataId, body);

        boolean newServiceMetadataCreated = serviceMetadataService.saveServiceMetadata(domain, asParticipantId(serviceGroupId), asDocumentId(serviceMetadataId), body);

        LOG.businessInfo(SMPMessageCode.BUS_HTTP_PUT_END_SERVICE_METADATA,authentUser, host, domain, serviceGroupId, serviceMetadataId, newServiceMetadataCreated);

        return newServiceMetadataCreated ? created(pathBuilder.getCurrentUri()).build() : ok().build();
    }

    @DeleteMapping
     @PreAuthorize("hasAnyAuthority(T(eu.europa.ec.edelivery.smp.auth.SMPAuthority).S_AUTHORITY_TOKEN_SMP_ADMIN) OR" +
              " @serviceGroupService.isServiceGroupOwner(authentication.name, #serviceGroupId)")
    public ResponseEntity deleteServiceMetadata(HttpServletRequest httpReq,
                                  @PathVariable String serviceGroupId,
                                  @PathVariable String serviceMetadataId,
                                  @RequestHeader(name = "Domain", required = false) String domain ) {


        String authentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        String host = getRemoteHost(httpReq);
        LOG.businessInfo(SMPMessageCode.BUS_HTTP_DELETE_SERVICE_METADATA,authentUser, host, domain, serviceGroupId, serviceMetadataId);

        serviceMetadataService.deleteServiceMetadata(domain, asParticipantId(serviceGroupId), asDocumentId(serviceMetadataId));

        LOG.businessInfo(SMPMessageCode.BUS_HTTP_DELETE_END_SERVICE_METADATA,authentUser, host, domain, serviceGroupId, serviceMetadataId);
        return ok().build();
    }

    public String getRemoteHost(HttpServletRequest httpReq){
        String host = httpReq.getHeader("X-Forwarded-For");
        return StringUtils.isBlank(host)?httpReq.getRemoteHost():host;
    }
}
