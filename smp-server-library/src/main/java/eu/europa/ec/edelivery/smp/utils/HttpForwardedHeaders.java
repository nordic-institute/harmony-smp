package eu.europa.ec.edelivery.smp.utils;

import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * This class is initialized from HttpServletRequest's X-Forwarded-* headers.
 *
 * The X-Forwarded-Host header defines which Host was used in the Client's request. In some RP implementations, it has only domain/ip
 * 'example.com' and (non-standard) X-Forwarded-Port is used for submitting port some implementations is combined with the port
 * as an example 'example.com:443'
 * The X-Forwarded-Proto header defines the protocol (HTTP or HTTPS).
 * The X-Forwarded-For header identifies the originating IP address of a client connecting through reverse proxy/load balancer.
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class HttpForwardedHeaders {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(HttpForwardedHeaders.class);
    private static final char HOST_PORT_SEPARATOR = ':';


    enum ForwardedHeaderNameEnum {
        HOST("X-Forwarded-Host"),
        PROTO("X-Forwarded-Proto"),
        FOR("X-Forwarded-For"),
        // non standard headers
        PORT("X-Forwarded-Port"),
        PREFIX("X-Forwarded-Prefix"),
        SSL("X-Forwarded-Ssl");

        final String headerName;

        ForwardedHeaderNameEnum(String headerName) {
            this.headerName = headerName;
        }

        public String getHeaderName() {
            return headerName;
        }
    }


    final String host;
    final String port;
    final String proto;
    final String forClientHost;
    final String ssl;

    public HttpForwardedHeaders(HttpServletRequest request) {

        if (request == null) {
            host = null;
            port = null;
            proto = null;
            forClientHost = null;
            ssl = null;
            return;
        }
        // read the values
        // the domain is case insensitive.
        String hostPrivate = getNormalizedHeader(request, ForwardedHeaderNameEnum.HOST);
        String portPrivate = getNormalizedHeader(request, ForwardedHeaderNameEnum.PORT);
        proto = getNormalizedHeader(request, ForwardedHeaderNameEnum.PROTO);
        forClientHost = getNormalizedHeader(request, ForwardedHeaderNameEnum.FOR);
        ssl = getNormalizedHeader(request, ForwardedHeaderNameEnum.SSL);
        // normalize
        if (StringUtils.contains(hostPrivate,HOST_PORT_SEPARATOR)) {
            String hostCombined = hostPrivate;
            hostPrivate = StringUtils.substringBefore(hostCombined,HOST_PORT_SEPARATOR);
            portPrivate = validatePort(portPrivate,StringUtils.substringAfter(hostCombined,HOST_PORT_SEPARATOR));
        }
        host = hostPrivate;
        port = portPrivate;
    }

    private String getNormalizedHeader(HttpServletRequest request, ForwardedHeaderNameEnum header) {

        return StringUtils.lowerCase(StringUtils.trim(request.getHeader(header.getHeaderName())));
    }

    private String validatePort(String headerPort, String hostPort){

        if (StringUtils.isBlank(headerPort)){
            LOG.debug("Header X-Forwarded-Port is empty. Use port from X-Forwarded-Host [{}].",hostPort);
            return hostPort;
        }

        if (StringUtils.equals(headerPort, hostPort)){
            LOG.debug("Header X-Forwarded-Port is and port from X-Forwarded-Host [{}] are equal.",hostPort);
            return headerPort;
        }

        LOG.warn("Header X-Forwarded-Port [{}] is and port from X-Forwarded-Host [{}] mismatch. Fix the RP/load balancer configuration! " +
                "Host port will be used as default value!",headerPort, hostPort);
        return headerPort;
    }


    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    /**
     * Method returns null if the port is default port for given proto/schema.
     * For HTTP default port is 80 and for HTTPS default port is 443

     * @return non default ports or null
     */
    public String getNonDefaultPort() {
        if (StringUtils.equals("http", proto) && StringUtils.equals("80", port)
            || StringUtils.equals("https", proto) && StringUtils.equals("443", port)) {
            LOG.debug("Ignore default port [{}] for proto/schema [{}].",port, proto);
            return null;
        }
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
        return "HttpForwardedHeaders{" +
                "host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", proto='" + proto + '\'' +
                ", forClientHost='" + forClientHost + '\'' +
                ", ssl='" + ssl + '\'' +
                '}';
    }
}
