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

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpUtilsTest {
    @Test
    public void testDoesTargetMatchNonProxyLocalhostTrue() throws MalformedURLException {
        String crlURL = "http://localhost/url";
        URL targetUrl = new URL(crlURL);
        boolean val = HttpUtils.doesTargetMatchNonProxy(targetUrl.getHost(), "localhost|127.0.0.1");
        assertTrue(val);
    }

    @Test
    public void testDoesTargetMatchNonProxyDomainWithPortTrue() throws MalformedURLException {
        String crlURL = "https://test.ec.europa.eu:8443/url";
        URL targetUrl = new URL(crlURL);
        boolean val = HttpUtils.doesTargetMatchNonProxy(targetUrl.getHost(), "localhost|127.0.0.1|test.ec.europa.eu");
        assertTrue(val);
    }

    @Test
    public void testDoesTargetMatchNonProxyDomainWithPortAndAsterixTrue() throws MalformedURLException {
        String crlURL = "https://test.ec.europa.eu:8443/url";
        URL targetUrl = new URL(crlURL);
        boolean val = HttpUtils.doesTargetMatchNonProxy(targetUrl.getHost(), "localhost|127.0.0.1|*.ec.europa.eu");
        assertTrue(val);
    }

    @Test
    public void testDoesTargetMatchNonProxyDomainWithPortFalse() throws MalformedURLException {
        String crlURL = "https://test.ec.europa.eu:8443/url";
        URL targetUrl = new URL(crlURL);
        boolean val = HttpUtils.doesTargetMatchNonProxy(targetUrl.getHost(), "localhost|127.0.0.1|ec.test.eu");
        assertFalse(val);
    }

    @Test
    public void testDoesTargetMatchNonProxyIPWithPortFalse() throws MalformedURLException {
        String crlURL = "https://192.168.5.4:8443/url";
        URL targetUrl = new URL(crlURL);
        boolean val = HttpUtils.doesTargetMatchNonProxy(targetUrl.getHost(), "localhost|127.0.0.1|ec.test.eu");
        assertFalse(val);
    }

    @Test
    public void testDoesTargetMatchNonProxyIPWithPortTrue() throws MalformedURLException {
        String crlURL = "https://192.168.5.4:8443/url";
        URL targetUrl = new URL(crlURL);
        boolean val = HttpUtils.doesTargetMatchNonProxy(targetUrl.getHost(), "localhost|127.0.0.1|192.168.5.4|ec.test.eu");
        assertTrue(val);
    }

    @Test
    public void testDoesTargetMatchNonProxyIPMaskWithPortTrue() throws MalformedURLException {
        String crlURL = "https://192.168.5.4:8443/url";
        URL targetUrl = new URL(crlURL);
        boolean val = HttpUtils.doesTargetMatchNonProxy(targetUrl.getHost(), "localhost|127.0.0.1|192.168.*|ec.test.eu");
        assertTrue(val);
    }

    @Test
    public void testDoesTargetMatchNonProxyIPMaskWithPortFalse() throws MalformedURLException {
        String crlURL = "https://192.168.5.4:8443/url";
        URL targetUrl = new URL(crlURL);
        boolean val = HttpUtils.doesTargetMatchNonProxy(targetUrl.getHost(), "localhost|127.0.0.1|192.168.4.*|ec.test.eu");
        assertFalse(val);
    }
}
