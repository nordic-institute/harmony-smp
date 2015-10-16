/*
 * 
 */
package eu.domibus.backend.service._1_1.exception;


/**
 * The Class JobServiceException.
 */
public class JobServiceException extends RuntimeException {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = -3465278371193590261L;

    /**
     * Instantiates a new job service exception.
     */
    public JobServiceException() {
        super();
    }

    /**
     * Instantiates a new job service exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public JobServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new job service exception.
     *
     * @param message the message
     */
    public JobServiceException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new job service exception.
     *
     * @param cause the cause
     */
    public JobServiceException(final Throwable cause) {
        super(cause);
    }
}
