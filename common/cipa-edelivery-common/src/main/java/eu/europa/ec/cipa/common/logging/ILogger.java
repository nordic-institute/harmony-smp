package eu.europa.ec.cipa.common.logging;

import eu.europa.ec.cipa.common.exception.Severity;

/**
 * Created by feriaad on 16/07/2015.
 */
public interface ILogger {

    void categoryLog(Severity severity, String category, String code, Throwable t, String... params);

}
