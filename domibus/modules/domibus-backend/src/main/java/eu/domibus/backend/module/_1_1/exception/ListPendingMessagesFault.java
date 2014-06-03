/*
 * 
 */
package eu.domibus.backend.module._1_1.exception;

/**
 * The Class ListPendingMessagesFault.
 */
public class ListPendingMessagesFault extends java.lang.Exception {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 8428617365183557042L;

    /**
     * The fault message.
     */
    private backend.ecodex.org._1_1.FaultDetail faultMessage;

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
    public ListPendingMessagesFault(final java.lang.String s) {
        super(s);
    }

    /**
     * Instantiates a new list pending messages fault.
     *
     * @param s  the s
     * @param ex the ex
     */
    public ListPendingMessagesFault(final java.lang.String s, final java.lang.Throwable ex) {
        super(s, ex);
    }

    /**
     * Instantiates a new list pending messages fault.
     *
     * @param cause the cause
     */
    public ListPendingMessagesFault(final java.lang.Throwable cause) {
        super(cause);
    }

    /**
     * Sets the fault message.
     *
     * @param msg the new fault message
     */
    public void setFaultMessage(final backend.ecodex.org._1_1.FaultDetail msg) {
        faultMessage = msg;
    }

    /**
     * Gets the fault message.
     *
     * @return the fault message
     */
    public backend.ecodex.org._1_1.FaultDetail getFaultMessage() {
        return faultMessage;
    }
}
