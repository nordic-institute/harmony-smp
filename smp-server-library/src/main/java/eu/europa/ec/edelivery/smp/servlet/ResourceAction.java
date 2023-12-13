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
package eu.europa.ec.edelivery.smp.servlet;

import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;

import java.util.stream.Stream;

/**
 * User action on the resource
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public enum ResourceAction {
    READ(HttpMethod.GET),
    CREATE_UPDATE(HttpMethod.PUT),
    DELETE(HttpMethod.DELETE);

    final HttpMethod httpMethod;

    ResourceAction(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }


    /**
     * Resolve the given http  method value to an {@code UserAction}.
     *
     * @param method the http method value as a String
     * @return the corresponding {@code UserAction}, or {@code null} if not found
     */
    @Nullable
    public static ResourceAction resolveForHeader(@Nullable String method) {
        return Stream.of(values()).filter(ua -> ua.httpMethodMatches(method))
                .findFirst()
                .orElse(null);
    }


    /**
     * Determine whether this {@code UserAction} matches the given http method value.
     *
     * @param method the HTTP method as a String
     * @return {@code true} if it matches, {@code false} otherwise
     */
    public boolean httpMethodMatches(String method) {
        return getHttpMethod().matches(method);
    }
}
