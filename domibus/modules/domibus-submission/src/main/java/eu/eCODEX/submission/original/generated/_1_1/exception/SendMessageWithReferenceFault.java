/*
 * 
 */
package eu.eCODEX.submission.original.generated._1_1.exception;

import backend.ecodex.org._1_1.FaultDetail;

/**
 * The Class SendMessageWithReferenceFault.
 */
public class SendMessageWithReferenceFault extends Exception {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = -2134373071171345835L;

    /**
     * The fault message.
     */
    private FaultDetail faultMessage;

    /**
     * Instantiates a new send message with reference fault.
     */
    public SendMessageWithReferenceFault() {
        super("SendMessageWithReferenceFault");
    }

    /**
     * Instantiates a new send message with reference fault.
     *
     * @param s the s
     */
    public SendMessageWithReferenceFault(final String s) {
        super(s);
    }

    /**
     * Instantiates a new send message with reference fault.
     *
     * @param s  the s
     * @param ex the ex
     */
    public SendMessageWithReferenceFault(final String s, final Throwable ex) {
        super(s, ex);
    }

    /**
     * Instantiates a new send message with reference fault.
     *
     * @param cause the cause
     */
    public SendMessageWithReferenceFault(final Throwable cause) {
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
