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
