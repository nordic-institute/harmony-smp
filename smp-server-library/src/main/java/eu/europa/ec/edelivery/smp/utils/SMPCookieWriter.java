package eu.europa.ec.edelivery.smp.utils;

import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class SMPCookieWriter {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(SMPCookieWriter.class);

    public static final String CSRF_COOKIE_NAME = "XSRF-TOKEN";
    public static final String SESSION_COOKIE_NAME = "JSESSIONID";

    private static final String COOKIE_PARAM_DELIMITER = "; ";
    private static final String COOKIE_PARAM_SECURE = "secure";
    private static final String COOKIE_PARAM_MAX_AGE = "Max-Age";
    private static final String COOKIE_PARAM_EXPIRES = "Expires";

    private static final String COOKIE_PARAM_PATH = "Path";
    private static final String COOKIE_PARAM_HTTP_ONLY = "HttpOnly";
    private static final String COOKIE_PARAM_SAME_SITE = "SameSite";

    /**
     * set cookie parameters https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Set-Cookie
     */
    public void writeCookieToResponse(String cookieName, String cookieValue, boolean isSecure, Integer maxAge, String path, String sameSite, HttpServletRequest request, HttpServletResponse response) {
        String cookieHeader = generateSetCookieHeader(cookieName, cookieValue, isSecure, maxAge, path, sameSite, request);
        LOG.info("Set cookie [{}]",cookieHeader);
        response.setHeader(HttpHeaders.SET_COOKIE, cookieHeader);
    }

    /**
     * Method generates set cookie header
     *
     * @param cookieName
     * @param cookieValue
     * @param isSecure
     * @param maxAge
     * @param path
     * @param sameSite
     * @param request
     * @return
     */
    public String generateSetCookieHeader(String cookieName, String cookieValue, boolean isSecure, Integer maxAge, String path, String sameSite, HttpServletRequest request) {


        StringBuilder sb = new StringBuilder();
        sb.append(cookieName)
                .append('=')
                .append(cookieValue);
        // set secure\
        if (isSecure) {
            sb.append(COOKIE_PARAM_DELIMITER)
                    .append(COOKIE_PARAM_SECURE);
        }

        sb.append(COOKIE_PARAM_DELIMITER)
                .append(COOKIE_PARAM_HTTP_ONLY);

        if (maxAge != null && maxAge > -1) {
            sb.append(COOKIE_PARAM_DELIMITER)
                    .append(COOKIE_PARAM_MAX_AGE)
                    .append('=')
                    .append(maxAge.intValue());

            ZonedDateTime expires = (maxAge != 0) ? ZonedDateTime.now().plusSeconds(maxAge)
                    : Instant.EPOCH.atZone(ZoneOffset.UTC);
            sb.append(COOKIE_PARAM_DELIMITER).append(COOKIE_PARAM_EXPIRES)
                    .append('=').append(expires.format(DateTimeFormatter.RFC_1123_DATE_TIME));
        }


        if (StringUtils.isBlank(path)) {
            path = request.getContextPath();
            path = StringUtils.isNotBlank(path)
                    ? path : "/";
        }
        if (StringUtils.isNotBlank(path)) {
            sb.append(COOKIE_PARAM_DELIMITER)
                    .append(COOKIE_PARAM_PATH)
                    .append('=')
                    .append(path);
        }
        if (StringUtils.isNotBlank(sameSite)) {
            sb.append(COOKIE_PARAM_DELIMITER)
                    .append(COOKIE_PARAM_SAME_SITE)
                    .append('=')
                    .append(sameSite);
        }

        LOG.debug("generated set cookie header [{}]", sb.toString());
        return sb.toString();
    }
}
