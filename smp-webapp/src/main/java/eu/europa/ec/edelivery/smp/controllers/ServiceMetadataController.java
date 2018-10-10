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
import eu.europa.ec.edelivery.smp.services.ServiceMetadataService;
import eu.europa.ec.edelivery.smp.validation.ServiceMetadataValidator;
import eu.europa.ec.smp.api.exceptions.XmlInvalidAgainstSchemaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import java.io.UnsupportedEncodingException;

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

    private static final Logger log = LoggerFactory.getLogger(ServiceMetadataController.class);

    @Autowired
    private ServiceMetadataValidator serviceMetadataValidator;

    @Autowired
    private ServiceMetadataService serviceMetadataService;

    @Autowired
    private ServiceMetadataPathBuilder pathBuilder;

    @GetMapping(produces = "text/xml; charset=UTF-8")
    public String getServiceMetadata(@PathVariable String serviceGroupId,
                                     @PathVariable String serviceMetadataId) throws TransformerException, UnsupportedEncodingException {

        log.info("GET ServiceMetadata: {} - {}", serviceGroupId, serviceMetadataId);

        Document serviceMetadata = serviceMetadataService.getServiceMetadataDocument(asParticipantId(serviceGroupId), asDocumentId(serviceMetadataId));

        log.info("GET ServiceMetadata finished: {} - {}", serviceGroupId, serviceMetadataId);
        return ServiceMetadataConverter.toString(serviceMetadata);
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SMP_ADMIN', @caseSensitivityNormalizer.normalizeParticipantId(#serviceGroupId))")
    public ResponseEntity saveServiceMetadata(
            @PathVariable String serviceGroupId,
            @PathVariable String serviceMetadataId,
            @RequestHeader(name = "Domain", required = false) String domain,
            @RequestBody byte[] body) throws XmlInvalidAgainstSchemaException {

        log.info("PUT ServiceMetadata: {} - {}\n{}", serviceGroupId, serviceMetadataId, body);

        serviceMetadataValidator.validate(serviceGroupId, serviceMetadataId, body);

        boolean newServiceMetadataCreated = serviceMetadataService.saveServiceMetadata(domain, asParticipantId(serviceGroupId), asDocumentId(serviceMetadataId), body);

        log.info("PUT ServiceMetadata finished: {} - {}\n{}", serviceGroupId, serviceMetadataId, body);

        return newServiceMetadataCreated ? created(pathBuilder.getCurrentUri()).build() : ok().build();
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SMP_ADMIN', @caseSensitivityNormalizer.normalizeParticipantId(#serviceGroupId))")
    public ResponseEntity deleteServiceMetadata(@PathVariable String serviceGroupId,
                                                @PathVariable String serviceMetadataId,
                                                @RequestHeader(name = "Domain", required = false) String domain ) {
        log.info("DELETE ServiceMetadata: {} - {}", serviceGroupId, serviceMetadataId);

        serviceMetadataService.deleteServiceMetadata(domain, asParticipantId(serviceGroupId), asDocumentId(serviceMetadataId));

        log.info("DELETE ServiceMetadata finished: {} - {}", serviceGroupId, serviceMetadataId);

        return ok().build();
    }
}
