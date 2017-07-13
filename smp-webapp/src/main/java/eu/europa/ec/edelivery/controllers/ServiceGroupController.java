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

import eu.europa.ec.cipa.smp.server.services.BaseServiceGroupInterfaceImpl;
import eu.europa.ec.cipa.smp.server.services.BaseServiceMetadataInterfaceImpl;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceGroup;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ServiceMetadataReferenceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static eu.europa.ec.smp.api.Identifiers.asString;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;

/**
 * Created by gutowpa on 12/07/2017.
 */

@Controller
@RequestMapping("/{serviceGroupId}")
@Order
public class ServiceGroupController {

    @Autowired
    private ServiceMetadataPathBuilder serviceMetadataPathBuilder;

    @Autowired
    private BaseServiceGroupInterfaceImpl serviceGroupService;

    @Autowired
    private BaseServiceMetadataInterfaceImpl serviceMetadataService;

    @GetMapping(produces = TEXT_XML_VALUE)
    @ResponseBody
    public ServiceGroup getServiceGroup(@PathVariable String serviceGroupId, HttpServletRequest req) throws Throwable {
        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(req.getServletContext());
        System.out.println(serviceGroupId);

        ServiceGroup serviceGroup = serviceGroupService.getServiceGroup(serviceGroupId);
        addReferences(serviceGroup);

        return serviceGroup;
    }

    private void addReferences(ServiceGroup serviceGroup) {
        ParticipantIdentifierType participantId = serviceGroup.getParticipantIdentifier();
        List<DocumentIdentifier> docIds = serviceMetadataService.getMetadataIdentifiers(asString(participantId));
        List<ServiceMetadataReferenceType>  referenceIds = serviceGroup.getServiceMetadataReferenceCollection().getServiceMetadataReferences();
        for(DocumentIdentifier docId : docIds){
            String url = serviceMetadataPathBuilder.buildSelfUrl(participantId, docId);
            referenceIds.add(new ServiceMetadataReferenceType(url));
        }
    }
}
