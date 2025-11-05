/*-
 * #START_LICENSE#
 * smp-webapp
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
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

import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.ui.ResourceConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.Strings;
import org.slf4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * URLCsrfMatcher matches the request and validates if request can be ignored for CSRF.
 * As example the non session requests (as SMP REST API) should now have the CSRF tokens.
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class URLCsrfIgnoreMatcher implements RequestMatcher {

    private static final Logger LOG = SMPLoggerFactory.getLogger(
            URLCsrfIgnoreMatcher.class);
    private final List<RegexRequestMatcher> unprotectedMatcherList = new ArrayList<>();

    public URLCsrfIgnoreMatcher() {
        this(null, null);
    }

    public URLCsrfIgnoreMatcher(List<String> regularExpressions, List<HttpMethod> methods) {
        if (regularExpressions == null || regularExpressions.isEmpty()) {
            return;
        }
        regularExpressions.forEach(regexp -> addIgnoreUrl(regexp, methods));
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        // ignore non ui sites!
        String uri = request.getRequestURI();
        LOG.debug("Test CSRF for uri [{}]", uri);
        if (!Strings.CI.startsWithAny(uri, "/ui/", "/smp/ui/")) {
            LOG.debug("URL is not part of the UI  [{}]", uri);
            return false;
        } else if (Strings.CI.equals(request.getMethod(), HttpMethod.POST.name())
                && Strings.CI.endsWithAny(uri,
                ResourceConstants.CONTEXT_PATH_PUBLIC_SECURITY_USER_VALIDATE_RESET_CREDENTIALS,
                ResourceConstants.CONTEXT_PATH_PUBLIC_SECURITY_USER_RESET_CREDENTIALS)) {
            // special case for reset password validation which is POST call redirected from email link
            LOG.debug("HTTP method [{}] for validate-reset-credential is ignored for CSRF!", request.getMethod());
            return false;
        }
        Optional<RegexRequestMatcher> unprotectedMatcher = unprotectedMatcherList.stream().filter(requestMatcher -> requestMatcher.matches(request)).findFirst();
        unprotectedMatcher
                .ifPresent(regexRequestMatcher -> LOG.debug("Ignore CSRF for: [{}] - [{}] with matcher [{}]!",
                        request.getMethod(), request.getRequestURI(), regexRequestMatcher));
        return unprotectedMatcher.isEmpty();
    }


    /**
     * Creates a case-sensitive {@code Pattern} instance to match against the request for  http method(s).
     *
     * @param ignoreUrlPattern the regular expression to match ignore URLs.
     * @param httpMethods      the HTTP method(s) to match. May be null to match all methods.
     */
    public void addIgnoreUrl(String ignoreUrlPattern, HttpMethod... httpMethods) {
        addIgnoreUrl(ignoreUrlPattern, httpMethods == null || httpMethods.length == 0 ? null : Arrays.asList(httpMethods));
    }


    /**
     * Creates a case-sensitive {@code Pattern} instance to match against the request for  http method(s).
     *
     * @param ignoreUrlPattern the regular expression to match ignore URLs.
     * @param httpMethods      list of the HTTP method(s) to match. May be null or empty to match all methods.
     */
    public void addIgnoreUrl(String ignoreUrlPattern, List<HttpMethod> httpMethods) {
        if (httpMethods == null || httpMethods.isEmpty()) {
            unprotectedMatcherList.add(new RegexRequestMatcher(ignoreUrlPattern, null));
        } else {
            httpMethods.forEach(httpMethod -> unprotectedMatcherList.add(new RegexRequestMatcher(ignoreUrlPattern, httpMethod.name())));
        }
    }
}
