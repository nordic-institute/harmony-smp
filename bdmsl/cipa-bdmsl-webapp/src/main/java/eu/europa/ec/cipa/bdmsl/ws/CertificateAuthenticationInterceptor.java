package eu.europa.ec.cipa.bdmsl.ws;

import eu.europa.ec.cipa.bdmsl.common.exception.CertificateAuthenticationException;
import eu.europa.ec.cipa.bdmsl.security.*;
import eu.europa.ec.cipa.bdmsl.service.IX509CertificateService;
import eu.europa.ec.cipa.bdmsl.util.LogEvents;
import eu.europa.ec.cipa.bdmsl.ws.soap.UnauthorizedFault;
import eu.europa.ec.cipa.common.exception.BusinessException;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import eu.europa.ec.cipa.common.logging.ILoggingService;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.busdox.servicemetadata.locator._1.FaultType;
import org.busdox.servicemetadata.locator._1.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.security.cert.X509Certificate;

/**
 * Created by feriaad on 25/06/2015.
 */
@Component(value = "certificateAuthenticationInterceptor")
public class CertificateAuthenticationInterceptor extends AbstractPhaseInterceptor<Message> {
    @Autowired
    protected ILoggingService loggingService;

    private static final String CLIENT_CERT_HEADER_KEY = "Client-Cert";

    private static final String CLIENT_CERT_ATTRIBUTE_KEY = "javax.servlet.request.X509Certificate";

    @Autowired
    private CustomAuthenticationProvider authenticationProvider;

    @Autowired
    private IX509CertificateService x509CertificateService;

    public CertificateAuthenticationInterceptor() {
        super(Phase.PRE_PROTOCOL);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        HttpServletRequest httpRequest = (HttpServletRequest) message.get("HTTP.REQUEST");
        try {
            loggingService.putMDC("requestId", RequestContextHolder.currentRequestAttributes().getSessionId());
            loggingService.putMDC("user", "no-user-yet-logged");
            final Object certificateAttribute = httpRequest.getAttribute(CLIENT_CERT_ATTRIBUTE_KEY);

            final String certHeaderValue = httpRequest.getHeader(CLIENT_CERT_HEADER_KEY);

            if ("https".equalsIgnoreCase(httpRequest.getScheme())) {
                if (certificateAttribute == null) {
                    throw new UnauthorizedFault("No client certificate present in the request");
                } else if (!(certificateAttribute instanceof X509Certificate[])) {
                    String messageFault = "Request value is not of type X509Certificate[] but of " + certificateAttribute.getClass();
                    UnauthorizedFault fault = buildUnauthorizedFault(messageFault);
                    throw fault;
                } else {
                    final X509Certificate[] certificates = (X509Certificate[]) certificateAttribute;
                    X509Certificate clientCertificate = x509CertificateService.getCertificate(certificates);
                    X509CertificateAuthentication authentication = new X509CertificateAuthentication(certificates, clientCertificate, x509CertificateService.calculateCertificateId(clientCertificate));
                    ((CertificateDetails) authentication.getDetails()).setRootCertificateDN(x509CertificateService.getTrustedRootCertificateDN(certificates));
                    authenticate(authentication, httpRequest);
                }
            } else if ("http".equalsIgnoreCase(httpRequest.getScheme())) {
                if (certHeaderValue == null) {
                    loggingService.debug("There is no client certificate in the request");
                    authenticate(new UnsecureAuthentication(), httpRequest);
                } else {
                    Authentication authentication = new BlueCoatClientCertificateAuthentication(certHeaderValue);
                    authenticate(authentication, httpRequest);
                }
            } else {
                String messageFault = "The request must use HTTP or HTTPS protocol";
                UnauthorizedFault fault = buildUnauthorizedFault(messageFault);
                throw fault;
            }
        } catch (final Exception exc) {
            loggingService.businessLog(LogEvents.BUS_AUTHENTICATION_ERROR, exc);
            if (exc instanceof UnauthorizedFault) {
                throw new Fault(exc);
            } else if (exc instanceof TechnicalException) {
                UnauthorizedFault fault = buildUnauthorizedFault(exc.getMessage());
                throw new Fault(fault);
            } else if (exc instanceof BusinessException) {
                UnauthorizedFault fault = buildUnauthorizedFault(exc.getMessage());
                throw new Fault(fault);
            } else {
                String messageFault = "Internal error during authentication process";
                UnauthorizedFault fault = buildUnauthorizedFault(messageFault);
                throw new Fault(fault);
            }

        }
    }

    private UnauthorizedFault buildUnauthorizedFault(String message) {
        final ObjectFactory objectFactory = new ObjectFactory();
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        final FaultType faultInfo = objectFactory.createFaultType();
        faultInfo.setFaultMessage(message);
        return new UnauthorizedFault(sessionId, faultInfo);
    }

    private void authenticate(Authentication authentication, HttpServletRequest httpRequest) throws TechnicalException {
        loggingService.securityLog(LogEvents.SEC_CONNECTION_ATTEMPT, httpRequest.getRemoteHost(), httpRequest.getRequestURL().toString());
        Authentication authenticationResult;
        try {
            authenticationResult = authenticationProvider.authenticate(authentication);
        } catch (AuthenticationException exc) {
            throw new CertificateAuthenticationException("Error while authenticating " + authentication.getName(), exc);
        }

        loggingService.putMDC("user", authenticationResult.getName());

        if (authenticationResult.isAuthenticated()) {
            loggingService.securityLog(LogEvents.SEC_AUTHORIZED_ACCESS, httpRequest.getRemoteHost(), httpRequest.getRequestURL().toString(), authenticationResult.getAuthorities().toString());
            loggingService.debug("Request authenticated. Storing the authentication result in the security context");
            loggingService.debug("Authentication result: " + authenticationResult);
            SecurityContextHolder.getContext().setAuthentication(authenticationResult);

        } else {
            loggingService.securityLog(LogEvents.SEC_UNAUTHORIZED_ACCESS, httpRequest.getRemoteHost(), httpRequest.getRequestURL().toString());
            throw new CertificateAuthenticationException("The certificate is not valid or is not present");
        }
    }
}
