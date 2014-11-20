/*
 * 
 */
package eu.eCODEX.submission.original.generated._1_1.exception;

import backend.ecodex.org._1_1.FaultDetail;

/**
 * The Class ListPendingMessagesFault.
 */
public class ListPendingMessagesFault extends Exception {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 8428617365183557042L;

    /**
     * The fault message.
     */
    private FaultDetail faultMessage;

    /**
     * Instantiates a new list pending messages fault.
     */
    public ListPendingMessagesFault() {
        super("ListPendingMessagesFault");
    }

    /**
     * Instantiates a new list pending messages fault.
     *
     * @param s the s
     */
    public ListPendingMessagesFault(final String s) {
        super(s);
    }

    /**
     * Instantiates a new list pending messages fault.
     *
     * @param s  the s
     * @param ex the ex
     */
    public ListPendingMessagesFault(final String s, final Throwable ex) {
        super(s, ex);
    }

    /**
     * Instantiates a new list pending messages fault.
     *
     * @param cause the cause
     */
    public ListPendingMessagesFault(final Throwable cause) {
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
