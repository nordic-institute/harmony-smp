package eu.europa.ec.edelivery.smp.config;


import eu.europa.ec.edelivery.smp.logging.SMPLogger;
import eu.europa.ec.edelivery.smp.logging.SMPLoggerFactory;
import eu.europa.ec.edelivery.smp.utils.SessionSecurityUtils;
import org.slf4j.MDC;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class MDCLogRequestFilter extends GenericFilterBean {
    private static final SMPLogger LOG = SMPLoggerFactory.getLogger(MDCLogRequestFilter.class);
/*
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String username = SessionSecurityUtils.getAuthenticationName();
        String requestId = UUID.randomUUID().toString();
        String sessionId =request.getSession() != null ? request.getSession().getId() : null;
        LOG.debug("Set request MDC data: user: [{}], request: [{}], session: [{}]!", username,requestId,sessionId);

        MDC.put(SMPLogger.MDC_USER, username);
        MDC.put(SMPLogger.MDC_REQUEST_ID, UUID.randomUUID().toString());
        MDC.put(SMPLogger.MDC_SESSION_ID, request.getSession() != null ? request.getSession().getId() : null);
        //filterChain.doFilter(request, response);
        //MDC.clear();
    }
*/
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String username = SessionSecurityUtils.getAuthenticationName();
        String requestId = UUID.randomUUID().toString();
        String sessionId =null;
        if (request instanceof  HttpServletRequest){
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            sessionId =httpRequest.getSession() != null ? httpRequest.getSession().getId() : null;
        }
        MDC.put(SMPLogger.MDC_USER, username);
        MDC.put(SMPLogger.MDC_REQUEST_ID, requestId);
        MDC.put(SMPLogger.MDC_SESSION_ID, sessionId);
        //doFilter
        chain.doFilter(request, response);
        MDC.clear();


    }
}