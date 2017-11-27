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

package eu.europa.ec.edelivery.smp.controllers;

import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

import static eu.europa.ec.smp.api.Identifiers.asString;

/**
 * Created by gutowpa on 13/07/2017.
 */
@Component
public class ServiceMetadataPathBuilder {

    @Value("${contextPath.output:true}")
    private boolean keepContext;

    public URI getCurrentUri() {
        return ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri();
    }

    public String buildSelfUrl(ParticipantIdentifierType participantId, DocumentIdentifier docId) {

        String path = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .replacePath(getUrlContext())
                .path("/{participantId}/services/{docId}")
                .buildAndExpand(asString(participantId), asString(docId))
                .toUriString();

        return path;
    }

    private String getUrlContext() {
        if (keepContext) {
            return new UrlPathHelper().getContextPath(getCurrentRequest());
        } else {
            return "";
        }
    }

    private static HttpServletRequest getCurrentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        Assert.state(requestAttributes != null, "Could not find current request via RequestContextHolder");
        Assert.isInstanceOf(ServletRequestAttributes.class, requestAttributes);
        HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        Assert.state(servletRequest != null, "Could not find current HttpServletRequest");
        return servletRequest;
    }
}
