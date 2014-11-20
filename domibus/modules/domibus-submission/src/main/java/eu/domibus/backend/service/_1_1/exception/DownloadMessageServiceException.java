/*
 * 
 */
package eu.domibus.backend.service._1_1.exception;

import backend.ecodex.org._1_1.Code;
import backend.ecodex.org._1_1.FaultDetail;

/**
 * The Class DownloadMessageServiceException.
 */
public class DownloadMessageServiceException extends RuntimeException {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 7776392255041070516L;

    /**
     * The code.
     */
    private Code code;

    /**
     * Instantiates a new download message service exception.
     */
    public DownloadMessageServiceException() {
        super();
    }

    /**
     * Instantiates a new download message service exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public DownloadMessageServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new download message service exception.
     *
     * @param message the message
     */
    public DownloadMessageServiceException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new download message service exception.
     *
     * @param cause the cause
     */
    public DownloadMessageServiceException(final Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new download message service exception.
     *
     * @param code the code
     */
    public DownloadMessageServiceException(final Code code) {
        super();
        this.code = code;
    }

    /**
     * Instantiates a new download message service exception.
     *
     * @param message the message
     * @param cause   the cause
     * @param code    the code
     */
    public DownloadMessageServiceException(final String message, final Throwable cause, final Code code) {
        super(message, cause);
        this.code = code;
    }

    /**
     * Instantiates a new download message service exception.
     *
     * @param message the message
     * @param code    the code
     */
    public DownloadMessageServiceException(final String message, final Code code) {
        super(message);
        this.code = code;
    }

    /**
     * Instantiates a new download message service exception.
     *
     * @param cause the cause
     * @param code  the code
     */
    public DownloadMessageServiceException(final Throwable cause, final Code code) {
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
