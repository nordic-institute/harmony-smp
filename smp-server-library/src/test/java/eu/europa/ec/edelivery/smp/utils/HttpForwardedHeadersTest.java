package eu.europa.ec.edelivery.smp.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.List;

import static eu.europa.ec.edelivery.smp.utils.HttpForwardedHeaders.ForwardedHeaderNameEnum.*;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class HttpForwardedHeadersTest {

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection cookieWriterTestParameters() {
        return asList(new Object[][]{
                {"test-host", "8181","http","172.1.0.1",  "test-host", "8181", "8181", "http","172.1.0.1"},
                {"NormaLIZE-hOst", "8181","http","172.1.0.1",  "normalize-host", "8181", "8181", "http","172.1.0.1"},
                {"default-http", "80","http","172.1.0.1",  "default-http", "80", null, "http","172.1.0.1"},
                {"default-https", "443","https","172.1.0.1",  "default-https", "443", null, "https","172.1.0.1"},
                {"https-host", "8443","https","172.1.0.1",  "https-host", "8443", "8443", "https","172.1.0.1"},
                {"normalize-scheme", "8443","hTTps","172.1.0.1",  "normalize-scheme", "8443", "8443", "https","172.1.0.1"},
                {"host-port:773", null,"https","172.1.0.1",  "host-port", "773", "773", "https","172.1.0.1"},
                {"host-port-equal:773", "773","https","172.1.0.1",  "host-port-equal", "773", "773", "https","172.1.0.1"},
                {"header-port-priority:773", "8843","https","172.1.0.1",  "header-port-priority", "8843", "8843", "https","172.1.0.1"},
        });
    }

    @Parameterized.Parameter(0)
    public String hostName;
    @Parameterized.Parameter(1)
    public String port;
    @Parameterized.Parameter(2)
    public String scheme;
    @Parameterized.Parameter(3)
    public String forCall;
    @Parameterized.Parameter(4)
    public String resultHostName;
    @Parameterized.Parameter(5)
    public String resultPort;
    @Parameterized.Parameter(6)
    public String resultDefaultPort;
    @Parameterized.Parameter(7)
    public String resultScheme;
    @Parameterized.Parameter(8)
    public String resultForCall;


    @Test
    public void getReadForwardHeadersFromRequest() {


        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn(hostName).when(request).getHeader(HOST.getHeaderName());
        Mockito.doReturn(scheme).when(request).getHeader(PROTO.getHeaderName());
        Mockito.doReturn(forCall).when(request).getHeader(FOR.getHeaderName());
        Mockito.doReturn(port).when(request).getHeader(PORT.getHeaderName());

        HttpForwardedHeaders testInstance = new HttpForwardedHeaders(request);

        assertEquals(resultHostName, testInstance.getHost());
        assertEquals(resultPort, testInstance.getPort());
        assertEquals(resultDefaultPort, testInstance.getNonDefaultPort());
        assertEquals(resultScheme, testInstance.getProto());
        assertEquals(resultForCall, testInstance.getForClientHost());

    }

}