package eu.europa.ec.cipa.common.logging.impl;

import eu.europa.ec.cipa.common.exception.Severity;
import eu.europa.ec.cipa.common.logging.ILogEvent;
import eu.europa.ec.cipa.common.logging.ILogger;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by feriaad on 16/07/2015.
 */
public abstract class AbstractLoggerImpl implements ILogger {

    @Autowired
    private ILogEvent logEvent;

    protected abstract Logger getLogger();

    @Override
    public void categoryLog(Severity severity, String category, String code, Throwable t, String... params) {
        String message = logEvent.getMessage(code);
        if (params != null) {
            try {
                message = String.format(message, (Object[]) params);
            } catch (final Throwable throwable) {
                getLogger().debug("Wrong use of the logging message of code " + code);
                message = logEvent.getMessage(code);
            }
        }

        if (Severity.WARN.equals(severity)) {
            getLogger().warn("[" + category + " - " + code + "] " + message);
        } else if (Severity.INFO.equals(severity)){
            getLogger().info("[" + category + " - " + code + "] " + message);
        } else if (Severity.ERROR.equals(severity)){
            getLogger().error("[" + category + " - " + code + "] " + message);
        }
    }
}
