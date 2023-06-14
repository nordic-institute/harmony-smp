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

package eu.europa.ec.edelivery.smp.utils;

import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.services.ConfigurationService;
import org.apache.commons.lang3.StringUtils;
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

/**
 * This class provides tools to generate SMP's URL in responses. The client can use provided URL for another call to the SMP.
 * Because SMP can run behind the reverse proxy, the X-Forwarded-* headers from the request are used for generating the URL.
 * Note: the reverse proxy must set the X-Forwarded-* headers when forwarding the request to the SMP.
 * <p>
 * The X-Forwarded-Host header defines which Host was used in the Client's request. In some RP implementations, it has only domain/ip
 * 'example.com' and (non-standard) X-Forwarded-Port is used for submitting port some implementations is combined with the port
 * as an example 'example.com:443'
 * The X-Forwarded-Proto header defines the protocol (HTTP or HTTPS).
 * The X-Forwarded-For header identifies the originating IP address of a client connecting through reverse proxy/load balancer.
 *
 * @author gutowpa
 * @author Joze Rihtarsic
 * @since 3.0
 */
@Component
public class SmpUrlBuilder {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SmpUrlBuilder.class);

    private ConfigurationService configurationService;


    public SmpUrlBuilder(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public String buildSMPUrlForApplication() {
        HttpServletRequest req = getCurrentRequest();
        HttpForwardedHeaders fh = new HttpForwardedHeaders(req);
        LOG.debug("Generate response uri with headers data: [{}]", fh);
        UriComponentsBuilder uriBuilder = getSMPUrlBuilder();
        if (fh.getHost() != null) {
            LOG.debug("Set response uri for forwarded headers: [{}]", fh);
            uriBuilder = uriBuilder.host(fh.getHost());
            String port = fh.getNonDefaultPort();
            if (!StringUtils.isBlank(port)) {
                uriBuilder = uriBuilder.port(port);
            } else if (!StringUtils.isBlank(fh.getPort())) {
                LOG.debug("Set port to null because it is default port: [{}]", fh);
                uriBuilder = uriBuilder.port(null);
            }
            uriBuilder = uriBuilder.scheme(fh.getProto());
        } else {
            LOG.info("Ignore settings header because host is null!");
        }
        return uriBuilder.build().toUriString();

    }

    /**
     * Method updates the root context of the URL. The schema, hostname, port, and root context using the X-Forwarded-*
     * headers from the request are updated by the ForwardedHeaderTransformer according SMP configuration: parameter 'smp.http.forwarded.headers.enabled'.
     *
     * @return UriComponentsBuilder - the Url Builder
     */
    public UriComponentsBuilder getSMPUrlBuilder() {

        UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromCurrentRequestUri();
        uriBuilder = uriBuilder.replacePath(getUrlContext());
        return uriBuilder;
    }

    private String getUrlContext() {
        if (configurationService.isUrlContextEnabled()) {
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
