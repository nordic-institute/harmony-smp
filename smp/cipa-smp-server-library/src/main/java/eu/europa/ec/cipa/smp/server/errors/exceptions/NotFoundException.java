package eu.europa.ec.cipa.smp.server.errors.exceptions;

/**
 * Created by migueti on 13/01/2017.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException (final String sMsg) {
        super (sMsg);
    }

}
