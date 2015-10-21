package eu.europa.ec.cipa.common.business;

import eu.europa.ec.cipa.common.logging.ILoggingService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by feriaad on 12/06/2015.
 */
public abstract class AbstractBusinessImpl {

    @Autowired
    protected ILoggingService loggingService;
}

