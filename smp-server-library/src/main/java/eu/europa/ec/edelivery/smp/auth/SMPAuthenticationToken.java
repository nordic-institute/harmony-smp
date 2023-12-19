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
package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.security.utils.SecurityUtils;
import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Objects;

/**
 * UI and web service authentication token. The authentication is created by the authentication provider
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class SMPAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPAuthenticationToken.class);
    SMPUserDetails userDetails;

    public SMPAuthenticationToken(Object principal, Object credentials, SMPUserDetails userDetails) {
        super(principal, credentials, userDetails.getAuthorities());
        setDetails(userDetails);
        this.userDetails = userDetails;
    }

    public SecurityUtils.Secret getSecret() {

        if (userDetails == null) {
            LOG.warn("Can not retrieve security token for session. User details is null!");
            return null;
        }
        return userDetails.getSessionSecret();
    }

    public SMPUserDetails getUserDetails() {
        return userDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractAuthenticationToken)) return false;
        if (!super.equals(o)) return false;
        SMPAuthenticationToken that = (SMPAuthenticationToken) o;
        // also check super equals (roles..) which is implemented in AbstractAuthenticationToken
        return Objects.equals(getDetails(), that.getDetails()) && super.equals(that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getDetails());
    }
}
