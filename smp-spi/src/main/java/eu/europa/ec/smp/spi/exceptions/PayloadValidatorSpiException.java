package eu.europa.ec.smp.spi.exceptions;

/**
 * @author Joze Rihtarsic
 * @since 4.2
 *
 * The external validation library throws the exception if the payload validation does not pass.
 */
public class PayloadValidatorSpiException extends Exception {
    public PayloadValidatorSpiException(String message) {
        super(message);
    }

    public PayloadValidatorSpiException(String message, Throwable cause) {
        super(message, cause);
    }

    public PayloadValidatorSpiException(Throwable cause) {
        super(cause);
    }
}
