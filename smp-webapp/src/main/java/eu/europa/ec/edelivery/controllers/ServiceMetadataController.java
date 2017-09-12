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

import eu.europa.ec.cipa.smp.server.data.IDataManager;
import eu.europa.ec.cipa.smp.server.services.BaseServiceMetadataInterfaceImpl;
import eu.europa.ec.edelivery.validation.ServiceMetadataValidator;
import eu.europa.ec.smp.api.exceptions.XmlInvalidAgainstSchemaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlRootElement;

import static eu.europa.ec.smp.api.Identifiers.asDocumentId;
import static eu.europa.ec.smp.api.Identifiers.asParticipantId;
import static java.lang.String.format;
import static org.slf4j.helpers.Util.getCallingClass;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;

/**
 * Created by gutowpa on 11/09/2017.
 */
@RestController
@RequestMapping("/{serviceGroupId}/services/{serviceMetadataId}")
public class ServiceMetadataController {

    private static final Logger log = LoggerFactory.getLogger(getCallingClass());

    //TODO Migrate to Service (add one more level)
    @Autowired
    private IDataManager dataManager;

    @Autowired
    ServiceMetadataValidator serviceMetadataValidator;

    @Autowired
    private BaseServiceMetadataInterfaceImpl serviceMetadataService;

    @Autowired
    private ServiceMetadataPathBuilder pathBuilder;

    @GetMapping(/*produces = TEXT_XML_VALUE*/)
    public Document getServiceMetadata(@PathVariable String serviceGroupId,
                                       @PathVariable String serviceMetadataId) {

        log.info(format("GET ServiceMetadata: %s - %s", serviceGroupId, serviceMetadataId));

        Document serviceGroup = serviceMetadataService.getServiceRegistration(serviceGroupId, serviceMetadataId);

//        JAXBElement el = new JAXBElement()


        log.info(format("GET ServiceMetadata finished: %s - %s", serviceGroupId, serviceMetadataId));
        return serviceGroup;
    }

    @PutMapping
    //@Secured("ROLE_SMP_ADMIN")
    public ResponseEntity saveServiceMetadata(
            @PathVariable String serviceGroupId,
            @PathVariable String serviceMetadataId,
            @RequestBody String body) throws XmlInvalidAgainstSchemaException {

        log.info(format("PUT ServiceMetadata: %s - %s\n%s", serviceGroupId, serviceMetadataId, body));

        serviceMetadataValidator.validate(serviceGroupId, serviceMetadataId, body);

        boolean newServiceMetadataCreated = dataManager.saveService(asParticipantId(serviceGroupId), asDocumentId(serviceMetadataId), body);

        log.info(format("PUT ServiceMetadata finished: %s - %s\n%s", serviceGroupId, serviceMetadataId, body));

        return newServiceMetadataCreated ? created(pathBuilder.getCurrentUri()).build() : ok().build();
    }

    @DeleteMapping
    //@Secured("ROLE_SMP_ADMIN")
    public Response deleteServiceRegistration(@PathVariable String serviceGroupId,
                                              @PathVariable String serviceMetadataId) {
        log.info("DELETE ServiceMetadata: %s - %s", serviceGroupId, serviceMetadataId);

        dataManager.deleteService(asParticipantId(serviceGroupId), asDocumentId(serviceMetadataId));

        log.info("DELETE ServiceMetadata finished: %s - %s", serviceGroupId, serviceMetadataId);

        return Response.ok().build();
    }
}
