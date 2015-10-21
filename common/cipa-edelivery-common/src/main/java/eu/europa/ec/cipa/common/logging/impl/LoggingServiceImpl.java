package eu.europa.ec.cipa.common.logging.impl;

import eu.europa.ec.cipa.common.exception.Severity;
import eu.europa.ec.cipa.common.logging.ILogEvent;
import eu.europa.ec.cipa.common.logging.ILogger;
import eu.europa.ec.cipa.common.logging.ILoggingService;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by feriaad on 15/06/2015.
 */
@Service
public class LoggingServiceImpl implements ILoggingService {

    private Logger logger = Logger.getLogger(LoggingServiceImpl.class);

    @Autowired
    private ILogger loggerBusiness;

    @Autowired
    private ILogger loggerSecurity;

    private void categoryLog(Severity severity, String category, String code, Throwable t, String... params) {
        // Also log into the specific file
        if (ILogEvent.CATEGORY_BUSINESS.equals(category)) {
            loggerBusiness.categoryLog(severity, category, code, t, params);
        } else if (ILogEvent.CATEGORY_SECURITY.equals(category)) {
            loggerSecurity.categoryLog(severity, category, code, t, params);
        }
        if (t != null) {
            logger.error(t.getMessage(), t);
        }
    }

    @Override
    public void securityLog(String code, String... params) {
        categoryLog(Severity.INFO, ILogEvent.CATEGORY_SECURITY, code, null, params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void businessLog(String code, String... params) {
        categoryLog(Severity.INFO, ILogEvent.CATEGORY_BUSINESS, code, null, params);
    }

    @Override
    public void businessLog(Severity severity, String code, String... params) {
        categoryLog(severity, ILogEvent.CATEGORY_BUSINESS, code, null, params);
    }

    @Override
    public void businessLog(String code, Throwable t, String... params) {
        categoryLog(Severity.ERROR, ILogEvent.CATEGORY_BUSINESS, code, t, params);
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void debug(String message) {
        logger.debug(message);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    @Override
    public void warn(String message, Throwable t) {
        logger.warn(message, t);
    }

    @Override
    public void error(String message, Throwable t) {
        logger.error(message, t);
    }

    @Override
    public void putMDC(String key, String value) {
        MDC.put(key, value);
    }

}
