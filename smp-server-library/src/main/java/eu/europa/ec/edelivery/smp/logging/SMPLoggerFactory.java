package eu.europa.ec.edelivery.smp.logging;

import org.slf4j.LoggerFactory;

/**
 * @author Cosmin Baciu (SMPLoggerFactory, Domibus 3.3+)
 * @since 4.1
 */
public class SMPLoggerFactory {

    private SMPLoggerFactory() {}

    public static SMPLogger getLogger(String name) {
        return new SMPLogger(LoggerFactory.getLogger(name));
    }

    public static SMPLogger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }
}
