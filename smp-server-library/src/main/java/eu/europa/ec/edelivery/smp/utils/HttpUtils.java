/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #END_LICENSE#
 */
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
