package eu.europa.ec.cipa.smp.server.fault;

/**
 * Created by migueti on 05/01/2017.
 */
public class NotFoundFault extends Exception {

    public NotFoundFault() {
        super();
    }

    public NotFoundFault(String message) {
        super(message);
    }

    public NotFoundFault(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundFault(String message, String uuid) {
        super(uuid + ": " + message);
    }
}
