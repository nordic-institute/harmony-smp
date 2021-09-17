package eu.europa.ec.edelivery.smp.auth;

import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;

/**
 * URLCsrfMatcher matches the request and validates if request can be ignored for CSRF.
 * As example the non session requests (as SMP REST API) should now have the CSRF tokens.
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class URLCsrfMatcher implements RequestMatcher {

    private static final Logger LOG = SMPLoggerFactory.getLogger(URLCsrfMatcher.class);
    private List<RequestMatcher> unprotectedMatcherList = new ArrayList<>();



    @Override
    public boolean matches(HttpServletRequest request) {
        Optional<RequestMatcher>  unprotectedMatcher = unprotectedMatcherList.stream().filter(requestMatcher -> requestMatcher.matches(request)).findFirst();
        return !unprotectedMatcher.isPresent();
    }


    /**
     * Creates a case-sensitive {@code Pattern} instance to match against the request for  http method(s).
     * @param ignoreUrlPattern the regular expression to match ignore URLs.
     * @param httpMethods the HTTP method(s) to match. May be null to match all methods.
     */
    public void addIgnoreUrl(String ignoreUrlPattern, HttpMethod ... httpMethods) {
        if (httpMethods==null || httpMethods.length ==0) {
            unprotectedMatcherList.add(new RegexRequestMatcher(ignoreUrlPattern, null));
        } else {
            Arrays.stream(httpMethods).forEach(httpMethod -> {
                unprotectedMatcherList.add(new RegexRequestMatcher(ignoreUrlPattern, httpMethod.name()));
            });
        }

    }
}