import eu.europa.ec.cipa.bdmsl.ws.soap.ICipaServiceWS;
import eu.europa.ec.cipa.bdmsl.ws.soap.InternalErrorFault;
import eu.europa.ec.cipa.bdmsl.ws.soap.UnauthorizedFault;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;

public class ClientExample {
    public static void main(String[] args) throws MalformedURLException, InternalErrorFault, UnauthorizedFault {
        URL wsdlURL = new URL("http://localhost:8080/edelivery-sml/services/cipaservice?wsdl");
        QName SERVICE_NAME = new QName("ec:services:wsdl:BDMSL:1.0", "CipaServiceWSImplService");
        Service service = Service.create(wsdlURL, SERVICE_NAME);
        ICipaServiceWS client = service.getPort(ICipaServiceWS.class);
        System.out.println(client.listParticipants());
    }
}