package eu.europa.ec.edelivery.smp.logging;

import eu.europa.ec.edelivery.smp.logging.api.CategoryLogger;
import eu.europa.ec.edelivery.smp.logging.api.MessageConverter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.Map;


/**
 * A custom SLF4J logger specialized in logging using business and security events using specific Domibus message codes
 *
 * @author Cosmin Baciu (DomibusLogger, domibus 3.3.)
 * @since 4.1
 */
public class SMPLogger extends CategoryLogger {

    public static final String MDC_USER = "userId";
    public static final String MDC_SESSION_ID = "messageId";
    public static final String MDC_DOMAIN = "domain";


    public static final String MDC_PROPERTY_PREFIX = "smp_";

    public static final Marker BUSINESS_MARKER = MarkerFactory.getMarker("BUSINESS");
    public static final Marker SECURITY_MARKER = MarkerFactory.getMarker("SECURITY");


    public SMPLogger(Logger logger, MessageConverter messageConverter) {
        super(logger, SMPLogger.class.getName(),messageConverter, MDC_PROPERTY_PREFIX);
    }

    public SMPLogger(Logger logger) {
        this(logger, new DefaultMessageConverter());
    }

    public void businessTrace(SMPMessageCode key, Object... args) {
        markerTrace(BUSINESS_MARKER, key, null, args);
    }

    public void businessDebug(SMPMessageCode key, Object... args) {
        markerDebug(BUSINESS_MARKER, key, null, args);
    }

    public void businessInfo(SMPMessageCode key, Object... args) {
        markerInfo(BUSINESS_MARKER, key, null, args);
    }

    public void businessWarn(SMPMessageCode key, Object... args) {
        businessWarn(key, null, args);
    }

    public void businessWarn(SMPMessageCode key, Throwable t, Object... args) {
        markerWarn(BUSINESS_MARKER, key, t, args);
    }

    public void businessError(SMPMessageCode key, Object... args) {
        businessError(key, null, args);
    }

    public void businessError(SMPMessageCode key, Throwable t, Object... args) {
        markerError(BUSINESS_MARKER, key, t, args);
    }

    public void securityTrace(SMPMessageCode key, Object... args) {
        markerTrace(SECURITY_MARKER, key, null, args);
    }

    public void securityDebug(SMPMessageCode key, Object... args) {
        markerDebug(SECURITY_MARKER, key, null, args);
    }

    public void securityInfo(SMPMessageCode key, Object... args) {
        markerInfo(SECURITY_MARKER, key, null, args);
    }

    public void securityWarn(SMPMessageCode key, Object... args) {
        securityWarn(key, null, args);
    }

    public void securityWarn(SMPMessageCode key, Throwable t, Object... args) {
        markerWarn(SECURITY_MARKER, key, t, args);
    }

    public void securityError(SMPMessageCode key, Object... args) {
        securityError(key, null, args);
    }

    public void securityError(SMPMessageCode key, Throwable t, Object... args) {
        markerError(SECURITY_MARKER, key, t, args);
    }

    protected void markerTrace(Marker marker, SMPMessageCode key, Throwable t, Object... args) {
        // log with no marker and stacktrace (if there is one)
        trace(null, key, t, args);

        //log with marker and without stacktrace
        trace(marker, key, args);
    }

    protected void markerDebug(Marker marker, SMPMessageCode key, Throwable t, Object... args) {
        // log with no marker and stacktrace (if there is one)
        debug(null, key, t, args);

        //log with marker and without stacktrace
        debug(marker, key, args);
    }

    protected void markerInfo(Marker marker, SMPMessageCode key, Throwable t, Object... args) {
        // log with no marker and stacktrace (if there is one)
        info(null, key, t, args);

        //log with marker and without stacktrace
        info(marker, key, args);
    }

    protected void markerWarn(Marker marker, SMPMessageCode key, Throwable t, Object... args) {
        // log with no marker and stacktrace (if there is one)
        warn(null, key, t, args);

        //log with marker and without stacktrace
        warn(marker, key, args);
    }

    protected void markerError(Marker marker, SMPMessageCode key, Throwable t, Object... args) {
        // log with no marker and stacktrace (if there is one)
        error(null, key, t, args);

        //log with marker and without stacktrace
        error(marker, key, args);
    }

    public void error(String msg, Throwable th){
        super.error(msg +
                (th!=null? "RootCauseMessage: " +ExceptionUtils.getRootCauseMessage(th):""), th);

    }
    public Map<String, String> getCopyOfContextMap() {
        return MDC.getCopyOfContextMap();
    }
}
