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

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.UriBuilder;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * Created by gutowpa on 31/01/2017.
 */
@RunWith(JUnitParamsRunner.class)
public class XForwardedHttpHeadersHandlerTest {

    //@formatter:off
    private Object parametersForXForwardedHeadersTest() {
        return new Object[][]{
                {"http://localdomain.com:8080/any", "https", "externaldomain.com", "443", "https://externaldomain.com/any"     },
                {"http://localdomain.com:8080/any", "https", "externaldomain.com", "80",  "https://externaldomain.com/any"     },
                {"http://localdomain.com:8080/any", "https", "externaldomain.com", "777", "https://externaldomain.com:777/any" },
                {"http://localdomain.com:8080/any", null,    null,                 null,  "http://localdomain.com:8080/any"    },
                {"http://localdomain.com:8080/any", "https", null,                 null,  "http://localdomain.com:8080/any"    },
                {"http://localdomain.com:8080/any", "https", "externaldomain.com", null,  "https://externaldomain.com/any"     },
                {"http://localdomain.com:8080/any", "https", null,                 "777", "http://localdomain.com:8080/any"    },
                {"http://localdomain.com:8080/any", null,    "externaldomain.com", "443", "http://externaldomain.com/any"      },
                {"http://localdomain.com:8080/any", "http",  null,                 null,  "http://localdomain.com:8080/any"    },
                {"https://localdomain.com/any",     "http",  "externaldomain.com", "777", "http://externaldomain.com:777/any"  }
        };
    }
    //@formatter:on

    @Test
    @Parameters
    public void xForwardedHeadersTest(String localUrl, String xProtocol, String xHost, String xPort, String expectedUrl) {
        //given
        DefaultHttpHeader headers = new DefaultHttpHeader();
        if (xProtocol != null) {
            headers.addRequestHeader("X-Forwarded-Proto", asList(xProtocol));
        }
        if (xHost != null) {
            headers.addRequestHeader("X-Forwarded-Host", asList(xHost));
        }
        if (xPort != null) {
            headers.addRequestHeader("X-Forwarded-Port", asList(xPort));
        }
        UriBuilder uriBuilder = UriBuilder.fromUri(localUrl);

        //when
        XForwardedHttpHeadersHandler.applyReverseProxyParams(uriBuilder, headers);

        //then
        assertEquals(expectedUrl, uriBuilder.build().toString());
    }
}
