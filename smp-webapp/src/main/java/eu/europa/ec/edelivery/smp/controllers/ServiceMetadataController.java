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

import eu.europa.ec.edelivery.smp.conversion.IdentifierService;
import eu.europa.ec.edelivery.smp.conversion.ServiceMetadataConverter;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.logging.SMPMessageCode;
import eu.europa.ec.edelivery.smp.services.PayloadValidatorService;
import eu.europa.ec.edelivery.smp.services.ServiceMetadataService;
import eu.europa.ec.edelivery.smp.validation.ServiceMetadataValidator;
import eu.europa.ec.smp.api.exceptions.XmlInvalidAgainstSchemaException;
import org.apache.commons.lang3.StringUtils;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;

import static eu.europa.ec.edelivery.smp.controllers.WebConstants.HTTP_PARAM_DOMAIN;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;

/**
 * @author gutowpa
 * @since 3.0.0
 */
@RestController
@RequestMapping("/{serviceGroupId}/services/{serviceMetadataId}")
public class ServiceMetadataController {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(ServiceMetadataController.class);

    protected final ServiceMetadataValidator serviceMetadataValidator;
    protected final ServiceMetadataService serviceMetadataService;
    protected final SmpUrlBuilder pathBuilder;
    protected final IdentifierService identifierService;
    protected final PayloadValidatorService payloadValidatorService;

    public ServiceMetadataController(ServiceMetadataValidator serviceMetadataValidator,
                                     ServiceMetadataService serviceMetadataService,
                                     SmpUrlBuilder pathBuilder,
                                     IdentifierService identifierService,
                                     PayloadValidatorService payloadValidatorService) {
        this.serviceMetadataValidator = serviceMetadataValidator;
        this.serviceMetadataService = serviceMetadataService;
        this.pathBuilder = pathBuilder;
        this.identifierService = identifierService;
        this.payloadValidatorService = payloadValidatorService;
    }

    @GetMapping(produces = "text/xml; charset=UTF-8")
    public String getServiceMetadata(HttpServletRequest httpReq,
                                     @PathVariable String serviceGroupId,
                                     @PathVariable String serviceMetadataId) throws TransformerException {

        String host = httpReq.getRemoteHost();
        LOG.businessInfo(SMPMessageCode.BUS_HTTP_GET_SERVICE_METADATA, host, serviceGroupId, serviceMetadataId);
        ParticipantIdentifierType participantIdentifierType = identifierService.normalizeParticipantIdentifier(serviceGroupId);
        DocumentIdentifier documentIdentifier = identifierService.normalizeDocumentIdentifier(serviceMetadataId);

        Document serviceMetadata = serviceMetadataService.getServiceMetadataDocument(participantIdentifierType, documentIdentifier);

        LOG.businessInfo(SMPMessageCode.BUS_HTTP_GET_END_SERVICE_METADATA, host, serviceGroupId, serviceMetadataId);
        return ServiceMetadataConverter.toString(serviceMetadata);
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority(T(eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority).S_AUTHORITY_TOKEN_SMP_ADMIN,  " +
            " T(eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority).S_AUTHORITY_TOKEN_WS_SMP_ADMIN) " +
            " OR @serviceGroupService.isServiceGroupOwner(authentication.name, #serviceGroupId)")
    public ResponseEntity saveServiceMetadata(HttpServletRequest httpReq,
                                              @PathVariable String serviceGroupId,
                                              @PathVariable String serviceMetadataId,
                                              @RequestHeader(name = HTTP_PARAM_DOMAIN, required = false) String domain,
                                              @RequestBody byte[] body) throws XmlInvalidAgainstSchemaException {

        String authentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        String host = getRemoteHost(httpReq);
        LOG.businessInfo(SMPMessageCode.BUS_HTTP_PUT_SERVICE_METADATA, authentUser, host, domain, serviceGroupId, serviceMetadataId);
        // validate payload
        payloadValidatorService.validateUploadedContent(new ByteArrayInputStream(body), MimeTypeUtils.APPLICATION_XML_VALUE);

        serviceMetadataValidator.validate(serviceGroupId, serviceMetadataId, body);
        ParticipantIdentifierType participantIdentifierType = identifierService.normalizeParticipantIdentifier(serviceGroupId);
        DocumentIdentifier documentIdentifier = identifierService.normalizeDocumentIdentifier(serviceMetadataId);

        boolean newServiceMetadataCreated = serviceMetadataService.saveServiceMetadata(domain, participantIdentifierType, documentIdentifier, body);

        LOG.businessInfo(SMPMessageCode.BUS_HTTP_PUT_END_SERVICE_METADATA, authentUser, host, domain, serviceGroupId, serviceMetadataId, newServiceMetadataCreated);

        return newServiceMetadataCreated ? created(pathBuilder.getCurrentUri()).build() : ok().build();
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority(T(eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority).S_AUTHORITY_TOKEN_SMP_ADMIN,  " +
            " T(eu.europa.ec.edelivery.smp.data.ui.auth.SMPAuthority).S_AUTHORITY_TOKEN_WS_SMP_ADMIN) " +
            " OR @serviceGroupService.isServiceGroupOwner(authentication.name, #serviceGroupId)")
    public ResponseEntity deleteServiceMetadata(HttpServletRequest httpReq,
                                                @PathVariable String serviceGroupId,
                                                @PathVariable String serviceMetadataId,
                                                @RequestHeader(name = "Domain", required = false) String domain) {


        String authentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        String host = getRemoteHost(httpReq);
        LOG.businessInfo(SMPMessageCode.BUS_HTTP_DELETE_SERVICE_METADATA, authentUser, host, domain, serviceGroupId, serviceMetadataId);
        ParticipantIdentifierType participantIdentifierType = identifierService.normalizeParticipantIdentifier(serviceGroupId);
        DocumentIdentifier documentIdentifier = identifierService.normalizeDocumentIdentifier(serviceMetadataId);
        serviceMetadataService.deleteServiceMetadata(domain, participantIdentifierType, documentIdentifier);

        LOG.businessInfo(SMPMessageCode.BUS_HTTP_DELETE_END_SERVICE_METADATA, authentUser, host, domain, serviceGroupId, serviceMetadataId);
        return ok().build();
    }

    public String getRemoteHost(HttpServletRequest httpReq) {
        String host = httpReq.getHeader("X-Forwarded-For");
        return StringUtils.isBlank(host) ? httpReq.getRemoteHost() : host;
    }
}
