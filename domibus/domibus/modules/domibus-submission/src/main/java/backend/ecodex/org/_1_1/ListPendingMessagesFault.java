/**
 * ListPendingMessagesFault.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */
package backend.ecodex.org._1_1;

public class ListPendingMessagesFault extends Exception {
    private static final long serialVersionUID = 1375793891155L;
    private backend.ecodex.org._1_1.FaultDetail faultMessage;

    public ListPendingMessagesFault() {
        super("ListPendingMessagesFault");
    }

    public ListPendingMessagesFault(final String s) {
        super(s);
    }

    public ListPendingMessagesFault(final String s, final Throwable ex) {
        super(s, ex);
    }

    public ListPendingMessagesFault(final Throwable cause) {
        super(cause);
    }

    public void setFaultMessage(final backend.ecodex.org._1_1.FaultDetail msg) {
        this.faultMessage = msg;
    }

    public backend.ecodex.org._1_1.FaultDetail getFaultMessage() {
        return this.faultMessage;
    }
}
