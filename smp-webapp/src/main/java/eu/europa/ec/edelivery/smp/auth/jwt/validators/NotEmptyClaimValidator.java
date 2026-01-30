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

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.apache.commons.lang3.StringUtils.isBlank;


/**
 * A custom OAuth2TokenValidator that checks if a specific claim is present in the JWT.
 * If the claim is not present, it returns an error indicating that the claim must have a not empty value.
 *
 * @author Joze Rihtarsic
 * @since 5.2
 */
public class NotEmptyClaimValidator implements OAuth2TokenValidator<Jwt> {

    protected final String claimName;

    public NotEmptyClaimValidator(String claimName) {
        this.claimName = claimName;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        validateHasClaim(token, "https://datatracker.ietf.org/doc/html/rfc9068#name-data-structure");

        if (isBlank(token.getClaimAsString(this.claimName))) {
            return OAuth2TokenValidatorResult
                    .failure(new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN, "Claim: [" + this.claimName + "] must exists and have not blank value",
                            "https://datatracker.ietf.org/doc/html/rfc9068#name-data-structure"));
        }
        return OAuth2TokenValidatorResult.success();
    }

    protected OAuth2TokenValidatorResult validateHasClaim(Jwt token, String exceptionURI) {
        if (token.hasClaim(this.claimName)) {
            return OAuth2TokenValidatorResult.success();
        }
        return OAuth2TokenValidatorResult
                .failure(new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN, "Claim: [" + this.claimName + "'] must exists",
                        exceptionURI));

    }
}
