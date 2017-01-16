package eu.europa.ec.cipa.smp.server.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by migueti on 13/01/2017.
 */
public class NotFoundException extends RuntimeException {
    private static final Logger s_aLogger = LoggerFactory.getLogger (NotFoundException.class);

    public NotFoundException (final String sMsg) {
        super (sMsg);

        // Always log!
        s_aLogger.warn (sMsg);
    }

}
