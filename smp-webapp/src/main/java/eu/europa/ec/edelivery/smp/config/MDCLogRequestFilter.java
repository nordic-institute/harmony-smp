package eu.europa.ec.edelivery.smp.config;


import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.slf4j.MDC;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

/**
 * SMP MDC logging filter sets the LOG MDC context as user, request id and session id.
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class MDCLogRequestFilter extends GenericFilterBean {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(MDCLogRequestFilter.class);
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        LOG.debug("Set MDC context to request!");
        String username = SessionSecurityUtils.getAuthenticationName();
        String requestId = UUID.randomUUID().toString();
        String sessionId = null;
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            sessionId = httpRequest.getSession() != null ? httpRequest.getSession().getId() : null;
        }
        MDC.put(SMPLogger.MDC_USER, username);
        MDC.put(SMPLogger.MDC_REQUEST_ID, requestId);
        MDC.put(SMPLogger.MDC_SESSION_ID, sessionId);
        //doFilter
        chain.doFilter(request, response);
        LOG.debug("clear MDC context from request!");
        MDC.clear();
    }
}