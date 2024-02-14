/*-
 * #START_LICENSE#
 * smp-webapp
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
package eu.europa.ec.edelivery.smp.auth.cas;


import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class SMPCas20ServiceTicketValidatorTest {
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(SMPCas20ServiceTicketValidatorTest.class);

    @Test
    void testGetUrlSuffix() {
        String casUrl = "https://cas-server.local/cas";
        String casSuffix = "urlSuffix";

        SMPCas20ServiceTicketValidator testInstance = new SMPCas20ServiceTicketValidator(casUrl, casSuffix);
        assertEquals(casSuffix, testInstance.getUrlSuffix());
    }

    @Test
    void testGetUrlSuffixDefault() {
        String casUrl = "https://cas-server.local/cas";
        String casSuffix = null;

        SMPCas20ServiceTicketValidator testInstance = new SMPCas20ServiceTicketValidator(casUrl, casSuffix);

        assertEquals("serviceValidate", testInstance.getUrlSuffix());
    }

    @Test
    void extractNamedAttributes() throws TicketValidationException {
        SMPCas20ServiceTicketValidator testInstance = new SMPCas20ServiceTicketValidator("https://cas-server.local/cas", "urlSuffix");

        String xml = getCasExample("login-authenticationSuccess-001");

        Assertion assertions = testInstance.parseResponseFromServer(xml);
        assertEquals("user", assertions.getPrincipal().getName());
        assertEquals(17, assertions.getPrincipal().getAttributes().size());
    }

    @Test
    void extractNamedListAttributes() throws TicketValidationException {
        SMPCas20ServiceTicketValidator testInstance = new SMPCas20ServiceTicketValidator("https://cas-server.local/cas", "urlSuffix");

        String xml = getCasExample("login-authenticationSuccess-003");

        Assertion assertions = testInstance.parseResponseFromServer(xml);
        assertEquals("user", assertions.getPrincipal().getName());
        assertions.getPrincipal().getAttributes().forEach((k, v) -> LOG.debug("Attribute: [{}], value: [{}]", k, v));
        assertEquals(2, assertions.getPrincipal().getAttributes().size());
        assertTrue(assertions.getPrincipal().getAttributes().containsKey("moniker"));
        assertInstanceOf(List.class, assertions.getPrincipal().getAttributes().get("moniker"));
        assertEquals(3, ((List<?>) assertions.getPrincipal().getAttributes().get("moniker")).size());
    }

    @Test
    void extractNamedAttributes1() throws TicketValidationException {
        SMPCas20ServiceTicketValidator testInstance = new SMPCas20ServiceTicketValidator("https://cas-server.local/cas", "urlSuffix");

        String xml = getCasExample("login-authenticationSuccess-002");

        Assertion assertions = testInstance.parseResponseFromServer(xml);
        assertEquals("username", assertions.getPrincipal().getName());
        assertEquals(3, assertions.getPrincipal().getAttributes().size());
    }

    public static String getCasExample(String name) {
        return getResourceFileAsString("/cas-login/" + name + ".xml");
    }

    /**
     * Read a resource file and return its contents as a string
     *
     * @param filePath path to the resource file
     * @return content of the file as a string or null if the file does not exist
     */
    public static String getResourceFileAsString(String filePath) {
        try (InputStream is = SMPCas20ServiceTicketValidatorTest.class.getResourceAsStream(filePath)) {
            if (is == null) {
                fail("Bad test configuration. Resource file: [" + filePath + "] does not exists!");
            }
            try (InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        } catch (IOException e) {
            fail("Error occurred while reading resource file: [" + filePath + "]. Root cause: [" + ExceptionUtils.getRootCauseMessage(e) + "]");
        }
        return null;
    }
}
