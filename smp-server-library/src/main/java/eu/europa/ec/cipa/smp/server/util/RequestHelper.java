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

import com.helger.commons.collections.CollectionHelper;
import com.helger.web.http.basicauth.BasicAuthClientCredentials;
import com.helger.web.http.basicauth.HTTPBasicAuth;
import eu.europa.ec.cipa.smp.server.errors.exceptions.UnauthorizedException;
import eu.europa.ec.cipa.smp.server.security.PreAuthenticatedCertificatePrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.ws.rs.core.HttpHeaders;
import java.util.List;

/**
 * This class is used for retrieving the HTTP BASIC AUTH header from the HTTP
 * Authorization Header.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public final class RequestHelper {

    @Nonnull
    public static BasicAuthClientCredentials getAuth(@Nonnull final HttpHeaders headers, boolean overrideByServiceGroupOwnershipHeader) throws UnauthorizedException {
        List<String> aHeaders = headers.getRequestHeader("ServiceGroup-Owner");
        if (overrideByServiceGroupOwnershipHeader && !CollectionHelper.isEmpty(aHeaders)) {
            return new BasicAuthClientCredentials(CollectionHelper.getFirstElement(aHeaders));
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof PreAuthenticatedCertificatePrincipal) {
            return new BasicAuthClientCredentials(auth.getName());
        }

        if (auth instanceof UsernamePasswordAuthenticationToken) {
            aHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
        }

        if (CollectionHelper.isEmpty(aHeaders)) {
            throw new UnauthorizedException("Missing required HTTP header '" +
                    HttpHeaders.AUTHORIZATION +
                    "' for user authentication");
        }

        return HTTPBasicAuth.getBasicAuthClientCredentials(CollectionHelper.getFirstElement(aHeaders));
    }
}
