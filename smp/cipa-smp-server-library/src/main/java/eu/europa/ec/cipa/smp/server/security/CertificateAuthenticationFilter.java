package eu.europa.ec.cipa.smp.server.security;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import eu.europa.ec.cipa.peppol.utils.ConfigFile;
import eu.europa.ec.cipa.smp.server.exception.AuthenticationException;
import eu.europa.ec.cipa.smp.server.exception.UnauthorizedException;
import eu.europa.ec.cipa.smp.server.util.CertificateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.util.List;

/**
 * Created by rodrfla on 17/01/2017.
 */
@Provider
@PreMatching
@Priority(value = Priorities.AUTHENTICATION)
@Component(value = "certificateAuthenticationFilter")
public class CertificateAuthenticationFilter implements ContainerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(CertificateAuthenticationFilter.class);

    private static final String CLIENT_CERT_HEADER_KEY = "Client-Cert";

    @Context
    private HttpServletRequest webRequest;

    @Autowired
    private CustomAuthenticationProvider authenticationProvider;

    @Override
    public ContainerRequest filter(ContainerRequest containerRequest) {
        try {
            final HttpSession session = webRequest.getSession();

            logger.info("user: no-user-yet-logged");
            logger.info("sessionId: " + session.getId());

            System.out.println(" ##### doFilter #######" + containerRequest.getRequestHeaders());
            String baseURIScheme = containerRequest.getBaseUri().getScheme();
            List<String> certHeaderValue = containerRequest.getRequestHeader(CLIENT_CERT_HEADER_KEY);
            List<String> htppBasicAuthentication = containerRequest.getRequestHeader(CLIENT_CERT_HEADER_KEY);
            if ("http".equalsIgnoreCase(baseURIScheme)) {
                if (certHeaderValue != null && !certHeaderValue.isEmpty()) {
                    CertificateDetails certificateDetails = CertificateUtils.calculateCertificateIdFromHeader(certHeaderValue.get(0));
                    Authentication authentication = new BlueCoatClientCertificateAuthentication(certHeaderValue.get(0));
                    authenticate(authentication, webRequest);
                }
            }
            return containerRequest;
        } catch (Exception e) {
            throw new UnauthorizedException(e);
        }
    }

    private void authenticate(Authentication authentication, HttpServletRequest httpRequest) throws AuthenticationException {
        logger.info(String.format("RemoteHost: %s, RequestURL: %s",httpRequest.getRemoteHost(), httpRequest.getRequestURL().toString()));
        Authentication authenticationResult;
        try {
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"classpath:applicationContext.xml"});
            authenticationProvider = (CustomAuthenticationProvider) context.getBean("customAuthenticationProvider");
            authenticationResult = authenticationProvider.authenticate(authentication);
        } catch (org.springframework.security.core.AuthenticationException exc) {
            throw new AuthenticationException("Error while authenticating " + authentication.getName(), exc);
        }

        logger.info(String.format("user: %s ", authenticationResult.getName()));

        if (authenticationResult.isAuthenticated()) {
            logger.info(String.format("SEC_AUTHORIZED_ACCESS | RemoteHost: %s, RequestURL: %s",httpRequest.getRemoteHost(), httpRequest.getRequestURL().toString()));
            SecurityContextHolder.getContext().setAuthentication(authenticationResult);
        } else {
            logger.info(String.format("SEC_UNAUTHORIZED_ACCESS | RemoteHost: %s, RequestURL: %s",httpRequest.getRemoteHost(), httpRequest.getRequestURL().toString()));
            throw new AuthenticationException("The certificate is not valid or is not present or the Admin credentials are invalid");
        }
    }
}
