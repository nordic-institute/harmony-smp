package eu.europa.ec.cipa.common.logging;

import eu.europa.ec.cipa.common.exception.Severity;

/**
 * Created by feriaad on 15/06/2015.
 */
public interface ILoggingService {

    void securityLog(String code, String... params);

    void businessLog(String code, String... params);

    void businessLog(Severity severity, String code, String... params);

    void businessLog(String code, Throwable t, String... params);

    void info(String message);

    void debug(String message);

    void warn(String message);

    void warn(String message, Throwable throwable);

    void error(String message, Throwable throwable);

    /**
     * Note that the underlying MDC is managed on a per thread basis.
     * <p>If the current thread does not have a context map it is
     * created as a side effect.
     *
     * @param key   the key identifier
     * @param value context value as identified with the <code>key</code>
     */
    void putMDC(String key, String value);
}
