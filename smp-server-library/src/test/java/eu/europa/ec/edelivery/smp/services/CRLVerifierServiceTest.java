package eu.europa.ec.edelivery.smp.services;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;


public class CRLVerifierServiceTest extends AbstractServiceIntegrationTest {


    @Autowired
    private CRLVerifierService crlVerifierServiceInstance;

    @Test
    public void testDoesTargetMatchNonProxyLocalhostTrue() throws URISyntaxException, MalformedURLException {
        String crlURL = "http://localhost/url";
        URL targetUrl = new URL(crlURL);
        boolean val = crlVerifierServiceInstance.doesTargetMatchNonProxy(targetUrl.getHost(), "localhost|127.0.0.1");
        Assert.assertTrue(val);
    }

    @Test
    public void testDoesTargetMatchNonProxyDomainWithPortTrue() throws URISyntaxException, MalformedURLException {
        String crlURL = "https://test.ec.europa.eu:8443/url";
        URL targetUrl = new URL(crlURL);
        boolean val = crlVerifierServiceInstance.doesTargetMatchNonProxy(targetUrl.getHost(), "localhost|127.0.0.1|test.ec.europa.eu");
        Assert.assertTrue(val);
    }

    @Test
    public void testDoesTargetMatchNonProxyDomainWithPortAndAsterixTrue() throws URISyntaxException, MalformedURLException {
        String crlURL = "https://test.ec.europa.eu:8443/url";
        URL targetUrl = new URL(crlURL);
        boolean val = crlVerifierServiceInstance.doesTargetMatchNonProxy(targetUrl.getHost(), "localhost|127.0.0.1|*.ec.europa.eu");
        Assert.assertTrue(val);
    }

    @Test
    public void testDoesTargetMatchNonProxyDomainWithPortFalse() throws URISyntaxException, MalformedURLException {
        String crlURL = "https://test.ec.europa.eu:8443/url";
        URL targetUrl = new URL(crlURL);
        boolean val = crlVerifierServiceInstance.doesTargetMatchNonProxy(targetUrl.getHost(), "localhost|127.0.0.1|ec.test.eu");
        Assert.assertFalse(val);
    }

    @Test
    public void testDoesTargetMatchNonProxyIPWithPortFalse() throws URISyntaxException, MalformedURLException {
        String crlURL = "https://192.168.5.4:8443/url";
        URL targetUrl = new URL(crlURL);
        boolean val = crlVerifierServiceInstance.doesTargetMatchNonProxy(targetUrl.getHost(), "localhost|127.0.0.1|ec.test.eu");
        Assert.assertFalse(val);
    }

    @Test
    public void testDoesTargetMatchNonProxyIPWithPortTrue() throws URISyntaxException, MalformedURLException {
        String crlURL = "https://192.168.5.4:8443/url";
        URL targetUrl = new URL(crlURL);
        boolean val = crlVerifierServiceInstance.doesTargetMatchNonProxy(targetUrl.getHost(), "localhost|127.0.0.1|192.168.5.4|ec.test.eu");
        Assert.assertTrue(val);
    }

    @Test
    public void testDoesTargetMatchNonProxyIPMaskWithPortTrue() throws URISyntaxException, MalformedURLException {
        String crlURL = "https://192.168.5.4:8443/url";
        URL targetUrl = new URL(crlURL);
        boolean val = crlVerifierServiceInstance.doesTargetMatchNonProxy(targetUrl.getHost(), "localhost|127.0.0.1|192.168.*|ec.test.eu");
        Assert.assertTrue(val);
    }

    @Test
    public void testDoesTargetMatchNonProxyIPMaskWithPortFalse() throws URISyntaxException, MalformedURLException {
        String crlURL = "https://192.168.5.4:8443/url";
        URL targetUrl = new URL(crlURL);
        boolean val = crlVerifierServiceInstance.doesTargetMatchNonProxy(targetUrl.getHost(), "localhost|127.0.0.1|192.168.4.*|ec.test.eu");
        Assert.assertFalse(val);
    }
}