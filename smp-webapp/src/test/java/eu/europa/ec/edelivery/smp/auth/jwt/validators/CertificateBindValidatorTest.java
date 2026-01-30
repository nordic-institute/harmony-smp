/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2025 - 2025 European Commission | eDelivery | DomiSMP
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
package eu.europa.ec.edelivery.smp.auth.jwt.validators;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.oauth2.jwt.Jwt;

import java.security.cert.X509Certificate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificateBindValidatorTest {


    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.CsvSource({
            "noCnf,false, must have property",
            "cnfNoX5t, true, must have property",
            "cnfX5tNotString,true, must be of type String",
            "cnfX5tStringMismatch,true, does not match"
    })
    void testValidateErrors(String scenario, boolean hasClaim, String expectedErrorMessage) {
        CertificateBindValidator validator = new CertificateBindValidator();
        Jwt jwt = Mockito.mock(Jwt.class);
        org.mockito.Mockito.when(jwt.hasClaim("cnf")).thenReturn(hasClaim);
        Map<String, Object> cnfClaim  = switch (scenario) {
            case "noCnf" ->  null;
            case "cnfNoX5t" -> null;
            case "cnfX5tNotString" -> {
                var map = new java.util.HashMap<String, Object>();
                map.put("x5t#S256", 12345);
                yield map;
            }
            case "cnfX5tStringMismatch" -> {
                var map = new java.util.HashMap<String, Object>();
                map.put("x5t#S256", "wrongThumbprint");
                yield map;
            }
            default -> throw new IllegalArgumentException("Unknown scenario: " + scenario);
        };
        Mockito.when(jwt.getClaimAsMap("cnf")).thenReturn(cnfClaim);

        var result = validator.validate(jwt);
        assertTrue(result.hasErrors());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.getDescription().contains(expectedErrorMessage)));
    }

    @Test
    void getCertificateFromHeader() {
        // Example: Add parameters for header name and expected result
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader("X-Client-Cert")).thenReturn(null);
        X509Certificate cert = CertificateBindValidator.getCertificateFromHeader("X-Client-Cert", request);
        assertNull(cert);
    }

    @Test
    void getCertificateFromAttribute() {
        // Example: Add parameters for attribute name and expected result
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getAttribute("javax.servlet.request.X509Certificate")).thenReturn(null);
        X509Certificate cert = CertificateBindValidator.getCertificateFromAttribute("javax.servlet.request.X509Certificate", request);
        assertNull(cert);
    }
}
