/*
 * 
 */
package eu.eCODEX.submission.original.generated._1_1.exception;

import backend.ecodex.org._1_1.FaultDetail;

/**
 * The Class DownloadMessageFault.
 */
public class DownloadMessageFault extends Exception {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = -68841730797017753L;

    /**
     * The fault message.
     */
    private FaultDetail faultMessage;

    /**
     * Instantiates a new download message fault.
     */
    public DownloadMessageFault() {
        super("DownloadMessageFault");
    }

    /**
     * Instantiates a new download message fault.
     *
     * @param s the s
     */
    public DownloadMessageFault(final String s) {
        super(s);
    }

    /**
     * Instantiates a new download message fault.
     *
     * @param s  the s
     * @param ex the ex
     */
    public DownloadMessageFault(final String s, final Throwable ex) {
        super(s, ex);
    }

    /**
     * Instantiates a new download message fault.
     *
     * @param cause the cause
     */
    public DownloadMessageFault(final Throwable cause) {
        super(cause);
    }

    /**
     * Sets the fault message.
     *
     * @param msg the new fault message
     */
    public void setFaultMessage(final FaultDetail msg) {
        this.faultMessage = msg;
    }

    /**
     * Gets the fault message.
     *
     * @return the fault message
     */
    public FaultDetail getFaultMessage() {
        return this.faultMessage;
    }
}
