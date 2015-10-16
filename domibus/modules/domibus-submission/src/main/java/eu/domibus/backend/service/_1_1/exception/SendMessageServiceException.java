/*
 * 
 */
package eu.domibus.backend.service._1_1.exception;

import backend.ecodex.org._1_1.Code;
import backend.ecodex.org._1_1.FaultDetail;

/**
 * The Class SendMessageServiceException.
 */
public class SendMessageServiceException extends RuntimeException {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = -3117224315509851312L;

    /**
     * The code.
     */
    private Code code;

    /**
     * Instantiates a new send message service exception.
     */
    public SendMessageServiceException() {
        super();
    }

    /**
     * Instantiates a new send message service exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public SendMessageServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new send message service exception.
     *
     * @param message the message
     */
    public SendMessageServiceException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new send message service exception.
     *
     * @param cause the cause
     */
    public SendMessageServiceException(final Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new send message service exception.
     *
     * @param code the code
     */
    public SendMessageServiceException(final Code code) {
        super();
        this.code = code;
    }

    /**
     * Instantiates a new send message service exception.
     *
     * @param message the message
     * @param cause   the cause
     * @param code    the code
     */
    public SendMessageServiceException(final String message, final Throwable cause, final Code code) {
        super(message, cause);
        this.code = code;
    }

    /**
     * Instantiates a new send message service exception.
     *
     * @param message the message
     * @param code    the code
     */
    public SendMessageServiceException(final String message, final Code code) {
        super(message);
        this.code = code;
    }

    /**
     * Instantiates a new send message service exception.
     *
     * @param cause the cause
     * @param code  the code
     */
    public SendMessageServiceException(final Throwable cause, final Code code) {
        super(cause);
        this.code = code;
    }

    /**
     * Gets the code.
     *
     * @return the code
     */
    public Code getCode() {
        return this.code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(final Code code) {
        this.code = code;
    }

    /**
     * Gets the fault.
     *
     * @return the fault
     */
    public FaultDetail getFault() {
        final FaultDetail faultDetail = new FaultDetail();
        faultDetail.setCode(this.code);
        faultDetail.setMessage(this.getMessage());
        return faultDetail;
    }
}
