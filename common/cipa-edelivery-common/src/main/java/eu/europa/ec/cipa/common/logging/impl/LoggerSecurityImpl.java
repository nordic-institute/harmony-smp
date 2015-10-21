package eu.europa.ec.cipa.common.logging.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Created by feriaad on 16/07/2015.
 */
@Component(value = "loggerSecurity")
public class LoggerSecurityImpl extends AbstractLoggerImpl {

    private Logger logger = Logger.getLogger(LoggerSecurityImpl.class);

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
