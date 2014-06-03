/**
 * SendMessageWithReferenceFault.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */
package backend.ecodex.org._1_1;

public class SendMessageWithReferenceFault extends java.lang.Exception {
    private static final long serialVersionUID = 1375793891155L;
    private backend.ecodex.org._1_1.FaultDetail faultMessage;

    public SendMessageWithReferenceFault() {
        super("SendMessageWithReferenceFault");
    }

    public SendMessageWithReferenceFault(final java.lang.String s) {
        super(s);
    }

    public SendMessageWithReferenceFault(final java.lang.String s, final java.lang.Throwable ex) {
        super(s, ex);
    }

    public SendMessageWithReferenceFault(final java.lang.Throwable cause) {
        super(cause);
    }

    public void setFaultMessage(final backend.ecodex.org._1_1.FaultDetail msg) {
        faultMessage = msg;
    }

    public backend.ecodex.org._1_1.FaultDetail getFaultMessage() {
        return faultMessage;
    }
}
