/*
 * 
 */
package eu.eCODEX.submission.original.generated._1_1.exception;

import backend.ecodex.org._1_1.FaultDetail;

/**
 * The Class SendMessageFault.
 */
public class SendMessageFault extends Exception {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 7749407617039423058L;

    /**
     * The fault message.
     */
    private FaultDetail faultMessage;

    /**
     * Instantiates a new send message fault.
     */
    public SendMessageFault() {
        super("SendMessageFault");
    }

    /**
     * Instantiates a new send message fault.
     *
     * @param s the s
     */
    public SendMessageFault(final String s) {
        super(s);
    }

    /**
     * Instantiates a new send message fault.
     *
     * @param s  the s
     * @param ex the ex
     */
    public SendMessageFault(final String s, final Throwable ex) {
        super(s, ex);
    }

    /**
     * Instantiates a new send message fault.
     *
     * @param cause the cause
     */
    public SendMessageFault(final Throwable cause) {
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
