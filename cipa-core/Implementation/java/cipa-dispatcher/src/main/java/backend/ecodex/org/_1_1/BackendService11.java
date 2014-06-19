package backend.ecodex.org._1_1;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

import eu.europa.ec.cipa.dispatcher.util.PropertiesUtil;

/**
 * This class was generated by Apache CXF 2.6.1
 * 2014-03-06T09:46:21.729+01:00
 * Generated source version: 2.6.1
 * 
 */
@WebServiceClient(name = "BackendService_1_1", targetNamespace = "http://org.ecodex.backend/1_1/") 
public class BackendService11 extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://org.ecodex.backend/1_1/", "BackendService_1_1");
    public final static QName BackendPort = new QName("http://org.ecodex.backend/1_1/", "BackendPort");
    static {
        URL url = null;
        String wsdlPath = "";
        try {
        	Properties properties = PropertiesUtil.getProperties();
        	wsdlPath = properties.getProperty(PropertiesUtil.EBMS_WSDL_PATH);
        	if (wsdlPath.startsWith("http") || wsdlPath.startsWith("HTTP"))
        		url = new URL(wsdlPath);
        	else
        		url = new File(wsdlPath).toURI().toURL();
        	
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(BackendService11.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "file:" + wsdlPath);
        }
        WSDL_LOCATION = url;
    }

    public BackendService11(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public BackendService11(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public BackendService11() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public BackendService11(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public BackendService11(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public BackendService11(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     *
     * @return
     *     returns BackendInterface
     */
    @WebEndpoint(name = "BackendPort")
    public BackendInterface getBackendPort() {
        return super.getPort(BackendPort, BackendInterface.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns BackendInterface
     */
    @WebEndpoint(name = "BackendPort")
    public BackendInterface getBackendPort(WebServiceFeature... features) {
        return super.getPort(BackendPort, BackendInterface.class, features);
    }

}
