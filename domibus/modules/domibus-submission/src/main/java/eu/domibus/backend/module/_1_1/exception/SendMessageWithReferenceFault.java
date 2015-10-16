/*
 * 
 */
package eu.domibus.backend.module._1_1.exception;

/**
 * The Class SendMessageWithReferenceFault.
 */
public class SendMessageWithReferenceFault extends java.lang.Exception {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = -2134373071171345835L;

    /**
     * The fault message.
     */
    private backend.ecodex.org._1_1.FaultDetail faultMessage;

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
    public SendMessageWithReferenceFault(final java.lang.String s) {
        super(s);
    }

    /**
     * Instantiates a new send message with reference fault.
     *
     * @param s  the s
     * @param ex the ex
     */
    public SendMessageWithReferenceFault(final java.lang.String s, final java.lang.Throwable ex) {
        super(s, ex);
    }

    /**
     * Instantiates a new send message with reference fault.
     *
     * @param cause the cause
     */
    public SendMessageWithReferenceFault(final java.lang.Throwable cause) {
        super(cause);
    }

    /**
     * Sets the fault message.
     *
     * @param msg the new fault message
     */
    public void setFaultMessage(final backend.ecodex.org._1_1.FaultDetail msg) {
        this.faultMessage = msg;
    }

    /**
     * Gets the fault message.
     *
     * @return the fault message
     */
    public backend.ecodex.org._1_1.FaultDetail getFaultMessage() {
        return this.faultMessage;
    }
}
