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

package eu.europa.ec.cipa.smp.server.util;

import com.helger.commons.string.StringParser;
import org.springframework.util.StringUtils;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;
import java.util.Arrays;
import java.util.List;

/**
 * Created by gutowpa on 31/01/2017.
 */
public class XForwardedHttpHeadersHandler {

    private static final int UNSET_URI_PORT = -1;
    private static final List<String> DEFAULT_PORTS = Arrays.asList("80", "443");

    public static void applyReverseProxyParams(UriBuilder uriBuilder, HttpHeaders httpHeaders) {
        String host = getHeader(httpHeaders, "X-Forwarded-Host");
        if (!StringUtils.isEmpty(host)) {
            uriBuilder.host(host);

            String port = getHeader(httpHeaders, "X-Forwarded-Port");
            if (StringParser.isUnsignedInt(port) && !DEFAULT_PORTS.contains(port)) {
                uriBuilder.port(Integer.parseInt(port));
            } else {
                uriBuilder.port(UNSET_URI_PORT);
            }

            String protocol = getHeader(httpHeaders, "X-Forwarded-Proto");
            if (!StringUtils.isEmpty(protocol)) {
                uriBuilder.scheme(protocol);
            }
        }
    }

    private static String getHeader(HttpHeaders httpHeaders, String headerKey) {
        List<String> headerValues = httpHeaders.getRequestHeader(headerKey);
        if (headerValues != null && headerValues.size() > 0) {
            return headerValues.get(0);
        } else {
            return null;
        }
    }
}
