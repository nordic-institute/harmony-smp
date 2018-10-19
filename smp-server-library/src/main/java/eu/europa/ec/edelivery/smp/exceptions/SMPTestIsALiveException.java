package eu.europa.ec.edelivery.smp.exceptions;

/**
 * Exception for testing database connection - rollback exception
 */
public class SMPTestIsALiveException extends RuntimeException  {

    public SMPTestIsALiveException(String message ) {
        super(message);
    }

}
