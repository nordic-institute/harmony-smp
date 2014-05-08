
package backend.ecodex.org._1_1;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.6.1
 * 2014-03-06T09:46:21.632+01:00
 * Generated source version: 2.6.1
 */

@WebFault(name = "FaultDetail", targetNamespace = "http://org.ecodex.backend/1_1/")
public class SendMessageWithReferenceFault extends Exception {
    
    private backend.ecodex.org._1_1.FaultDetail faultDetail;

    public SendMessageWithReferenceFault() {
        super();
    }
    
    public SendMessageWithReferenceFault(String message) {
        super(message);
    }
    
    public SendMessageWithReferenceFault(String message, Throwable cause) {
        super(message, cause);
    }

    public SendMessageWithReferenceFault(String message, backend.ecodex.org._1_1.FaultDetail faultDetail) {
        super(message);
        this.faultDetail = faultDetail;
    }

    public SendMessageWithReferenceFault(String message, backend.ecodex.org._1_1.FaultDetail faultDetail, Throwable cause) {
        super(message, cause);
        this.faultDetail = faultDetail;
    }

    public backend.ecodex.org._1_1.FaultDetail getFaultInfo() {
        return this.faultDetail;
    }
}
