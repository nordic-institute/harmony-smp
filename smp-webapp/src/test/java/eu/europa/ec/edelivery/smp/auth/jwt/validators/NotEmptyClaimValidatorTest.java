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

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotEmptyClaimValidatorTest {

    @ParameterizedTest
    @CsvSource({
            // claimName, hasClaim, expectedSuccess, expectedMessage
            "'testClaim', true, true, ''",
            "'testClaim', false, false, 'must exists'"
    })
    void testValidateHasClaim(String claimName, boolean hasClaim, boolean expectedSuccess, String expectedMessage) {
        NotEmptyClaimValidator validator = new NotEmptyClaimValidator(claimName);

        Jwt jwt = org.mockito.Mockito.mock(Jwt.class);
        org.mockito.Mockito.when(jwt.hasClaim(claimName)).thenReturn(hasClaim);

        OAuth2TokenValidatorResult result = validator.validateHasClaim(jwt, "https://datatracker.ietf.org/doc/html/rfc9068#name-data-structure");

        assertEquals(expectedSuccess, !result.hasErrors());
        String errorMsg = result.getErrors().isEmpty() ? "" : result.getErrors().iterator().next().getDescription();
        org.hamcrest.MatcherAssert.assertThat(errorMsg, org.hamcrest.Matchers.containsString(expectedMessage));
    }

    @ParameterizedTest
    @CsvSource({
            // claimName, hasClaim, claimValue, expectedSuccess, expectedMessage
            "'testClaim', false, '', false, 'must exists'",
            "'testClaim', true, '', false, 'must exists and have not blank value'",
            "'testClaim', true, '   ', false, 'must exists and have not blank value'",
            "'testClaim', true, 'validValue', true, ''"
    })
    void testValidate(String claimName, boolean hasClaim, String claimValue, boolean expectedSuccess, String expectedMessage) {
        NotEmptyClaimValidator validator = new NotEmptyClaimValidator(claimName);

        Jwt jwt = org.mockito.Mockito.mock(Jwt.class);
        org.mockito.Mockito.when(jwt.hasClaim(claimName)).thenReturn(hasClaim);
        org.mockito.Mockito.when(jwt.getClaimAsString(claimName)).thenReturn(claimValue);

        OAuth2TokenValidatorResult result = validator.validate(jwt);

        assertEquals(expectedSuccess, !result.hasErrors());
        String errorMsg = result.getErrors().isEmpty() ? "" : result.getErrors().iterator().next().getDescription();
        MatcherAssert.assertThat(errorMsg, org.hamcrest.Matchers.containsString(expectedMessage));

    }
}