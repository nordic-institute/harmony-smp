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
