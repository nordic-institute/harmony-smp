package eu.europa.ec.cipa.bdmsl.ws.soap.impl;

import eu.europa.ec.cipa.bdmsl.common.exception.*;
import eu.europa.ec.cipa.bdmsl.ws.soap.BadRequestFault;
import eu.europa.ec.cipa.bdmsl.ws.soap.InternalErrorFault;
import eu.europa.ec.cipa.bdmsl.ws.soap.NotFoundFault;
import eu.europa.ec.cipa.bdmsl.ws.soap.UnauthorizedFault;
import eu.europa.ec.cipa.common.exception.BusinessException;
import eu.europa.ec.cipa.common.exception.TechnicalException;
import eu.europa.ec.cipa.common.logging.ILoggingService;
import org.busdox.servicemetadata.locator._1.FaultType;
import org.busdox.servicemetadata.locator._1.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Created by feriaad on 12/06/2015.
 */
public abstract class AbstractWSImpl {

    @Autowired
    protected ILoggingService loggingService;

    protected void handleException(final Exception e) throws NotFoundFault,
            UnauthorizedFault,
            BadRequestFault,
            InternalErrorFault {
        final ObjectFactory objectFactory = new ObjectFactory();
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        if (e instanceof BadRequestException) {
            final FaultType faultInfo = objectFactory.createFaultType();
            faultInfo.setFaultMessage(e.getMessage());
            throw new BadRequestFault(sessionId, faultInfo, e);
        } else if (e instanceof SmpNotFoundException) {
            final FaultType faultInfo = objectFactory.createFaultType();
            faultInfo.setFaultMessage(e.getMessage());
            throw new NotFoundFault(sessionId, faultInfo, e);
        } else if (e instanceof ParticipantNotFoundException) {
            final FaultType faultInfo = objectFactory.createFaultType();
            faultInfo.setFaultMessage(e.getMessage());
            throw new NotFoundFault(sessionId, faultInfo, e);
        } else if (e instanceof MigrationNotFoundException) {
            final FaultType faultInfo = objectFactory.createFaultType();
            faultInfo.setFaultMessage(e.getMessage());
            throw new NotFoundFault(sessionId, faultInfo, e);
        } else if (e instanceof UnauthorizedException) {
            final FaultType faultInfo = objectFactory.createFaultType();
            faultInfo.setFaultMessage(e.getMessage());
            throw new UnauthorizedFault(sessionId, faultInfo, e);
        } else if (e instanceof CertificateAuthenticationException) {
            final FaultType faultInfo = objectFactory.createFaultType();
            faultInfo.setFaultMessage(e.getMessage());
            throw new UnauthorizedFault(sessionId, faultInfo, e);
        } else if (e instanceof MigrationPlannedException) {
            final FaultType faultInfo = objectFactory.createFaultType();
            faultInfo.setFaultMessage(e.getMessage());
            throw new UnauthorizedFault(sessionId, faultInfo, e);
        } else if (e instanceof AccessDeniedException) {
            final FaultType faultInfo = objectFactory.createFaultType();
            faultInfo.setFaultMessage(e.getMessage());
            throw new UnauthorizedFault(sessionId, faultInfo, e);
        } else if (e instanceof BusinessException) {
            final FaultType faultInfo = objectFactory.createFaultType();
            faultInfo.setFaultMessage(e.getMessage());
            throw new InternalErrorFault(sessionId /* e.getMessage() */, faultInfo, e);
        } else if (e instanceof TechnicalException) {
            final FaultType faultInfo = objectFactory.createFaultType();
            faultInfo.setFaultMessage(e.getMessage());
            throw new InternalErrorFault(sessionId /* e.getMessage() */, faultInfo, e);
        } else {
            // All others as internal errors
            final FaultType faultInfo = objectFactory.createFaultType();
            faultInfo.setFaultMessage("Internal error");
            throw new InternalErrorFault(sessionId /* e.getMessage() */, faultInfo, e);
        }
    }
}
