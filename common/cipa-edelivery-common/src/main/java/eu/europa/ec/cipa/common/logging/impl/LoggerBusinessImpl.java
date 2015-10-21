package eu.europa.ec.cipa.common.logging.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Created by feriaad on 16/07/2015.
 */
@Component(value = "loggerBusiness")
public class LoggerBusinessImpl extends AbstractLoggerImpl {

    private Logger logger = Logger.getLogger(LoggerBusinessImpl.class);

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
