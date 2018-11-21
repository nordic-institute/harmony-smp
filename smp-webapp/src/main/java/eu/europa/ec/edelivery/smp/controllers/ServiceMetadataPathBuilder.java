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

import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.DocumentIdentifier;
import org.oasis_open.docs.bdxr.ns.smp._2016._05.ParticipantIdentifierType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

import static eu.europa.ec.smp.api.Identifiers.asUrlEncodedString;

/**
 * Created by gutowpa on 13/07/2017.
 */
@Component
public class ServiceMetadataPathBuilder {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger( ServiceMetadataPathBuilder.class);


    @Value("${contextPath.output:true}")
    private boolean keepContext;


    enum ForwardedHeaderNameEnum {
        HOST("X-Forwarded-Host"),
        PORT("X-Forwarded-Port"),
        PROTO("X-Forwarded-Proto"),
        PREFIX("X-Forwarded-Prefix"),
        SSL("X-Forwarded-Ssl"),
        FOR("X-Forwarded-For");

        String headerName;

        ForwardedHeaderNameEnum(String headerName) {
            this.headerName = headerName;
        }

        public String getHeaderName() {
            return headerName;
        }
    }

    public URI getCurrentUri() {
        return ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri();
    }

    public String buildSelfUrl(ParticipantIdentifierType participantId, DocumentIdentifier docId) {

        HttpServletRequest req = getCurrentRequest();
        ForwardedHeaders fh = new ForwardedHeaders(req);
        LOG.info("Generate response uri for forwareded headers: " + fh.toString());

        UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromCurrentRequestUri();
        uriBuilder = uriBuilder.replacePath(getUrlContext());
        if (fh.getHost()!=null) {
            uriBuilder = uriBuilder.host(fh.getHost());
            if (!StringUtils.isBlank(fh.getPort())) {
                uriBuilder = uriBuilder.port(fh.getPort());
            }
            uriBuilder = uriBuilder.scheme(fh.getProto());
        }

        String path = uriBuilder
                .path("/{participantId}/services/{docId}")
                .buildAndExpand(asUrlEncodedString(participantId), asUrlEncodedString(docId))
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

    private static class ForwardedHeaders {
        final String host;
        final String port;
        final String proto;
        final String forClientHost;
        final String ssl;


        public ForwardedHeaders(HttpServletRequest request) {
            if (request != null) {
                host = request.getHeader(ForwardedHeaderNameEnum.HOST.getHeaderName());
                port = request.getHeader(ForwardedHeaderNameEnum.PORT.getHeaderName());;
                proto = request.getHeader(ForwardedHeaderNameEnum.PROTO.getHeaderName());;
                forClientHost = request.getHeader(ForwardedHeaderNameEnum.FOR.getHeaderName());;
                ssl = request.getHeader(ForwardedHeaderNameEnum.SSL.getHeaderName());;
            } else {
                host = null;
                port = null;
                proto = null;
                forClientHost = null;
                ssl = null;
            }
        }

        public String getHost() {
            return host;
        }

        public String getPort() {
            return port;
        }

        public String getProto() {
            return proto;
        }

        public String getForClientHost() {
            return forClientHost;
        }

        public String getSsl() {
            return ssl;
        }

        @Override
        public String toString() {
            return "ForwardedHeaders{" +
                    "host='" + host + '\'' +
                    ", port='" + port + '\'' +
                    ", proto='" + proto + '\'' +
                    ", forClientHost='" + forClientHost + '\'' +
                    ", ssl='" + ssl + '\'' +
                    '}';
        }
    }
}
