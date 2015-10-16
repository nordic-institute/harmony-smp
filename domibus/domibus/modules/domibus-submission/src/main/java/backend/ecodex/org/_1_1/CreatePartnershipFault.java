
/**
 * CreatePartnershipFault.java
 * <p/>
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package backend.ecodex.org._1_1;

public class CreatePartnershipFault extends Exception {

    private static final long serialVersionUID = 1435915675022L;

    private FaultDetail faultMessage;


    public CreatePartnershipFault() {
        super("CreatePartnershipFault");
    }

    public CreatePartnershipFault(String s) {
        super(s);
    }

    public CreatePartnershipFault(String s, Throwable ex) {
        super(s, ex);
    }

    public CreatePartnershipFault(Throwable cause) {
        super(cause);
    }


    public void setFaultMessage(FaultDetail msg) {
        faultMessage = msg;
    }

    public FaultDetail getFaultMessage() {
        return faultMessage;
    }
}
    