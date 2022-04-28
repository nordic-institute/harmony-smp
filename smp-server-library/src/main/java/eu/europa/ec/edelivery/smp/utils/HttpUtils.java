package eu.europa.ec.edelivery.smp.utils;

import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.util.Arrays;

public class HttpUtils {

    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(HttpUtils.class);

    /**
     * Method validates if host match non proxy list
     *
     * @param uriHost          target host
     * @param nonProxyHostList non proxy ist
     * @return true if host match nonProxy list else return false.
     */
    public static boolean doesTargetMatchNonProxy(String uriHost, String nonProxyHostList) {
        String[] nonProxyHosts = StringUtils.isBlank(nonProxyHostList) ? null : nonProxyHostList.split("\\|");

        int nphLength = nonProxyHosts != null ? nonProxyHosts.length : 0;
        if (nonProxyHosts == null || nphLength < 1) {
            LOG.debug("host:'" + uriHost + "' : DEFAULT (0 non proxy host)");
            return false;
        }


        for (String nonProxyHost : nonProxyHosts) {
            String mathcRegExp = (nonProxyHost.startsWith("*") ? "." : "") + nonProxyHost;
            if (uriHost.matches(mathcRegExp)) {
                LOG.debug(" host:'" + uriHost + "' matches nonProxyHost '" + mathcRegExp + "' : NO PROXY");
                return true;
            }
        }
        LOG.debug(" host:'" + uriHost + "' : DEFAULT  (no match of " + Arrays.toString(nonProxyHosts) + " non proxy host)");
        return false;
    }


    public static String getServerAddress() {
        String serverAddress;
        try {
            serverAddress = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            serverAddress = StringUtils.EMPTY;
        }
        return serverAddress;
    }

}
