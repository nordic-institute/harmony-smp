/*
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package eu.peppol.start.client;

import eu.peppol.start.soap.SOAPHeaderObject;
import com.sun.xml.ws.Closeable;
import eu.peppol.start.exception.AccessPointClientException;
import eu.peppol.start.soap.handler.SOAPOutboundHandler;
import eu.peppol.start.util.Configuration;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;
import org.apache.log4j.Logger;
import org.w3._2009._02.ws_tra.AccesspointService;
import org.w3._2009._02.ws_tra.Create;
import org.w3._2009._02.ws_tra.FaultMessage;
import org.w3._2009._02.ws_tra.Resource;

/**
 * The accesspointClient class aims to hold all the processes required for
 * consuming an AccessPoint.
 *
 * @author  Dante Malaga(dante@alfa1lab.com)
 *          Jose Gorvenia Narvaez(jose@alfa1lab.com)
 *          Marcelo Tataje Salinas(marcelo@alfa1lab.com)
 *          George Reátegui Ravina (jorge@alfa1lab.com)
 */
public class accesspointClient {

    /** Logger to follow this class behavior. */
    private static final Logger logger4J =  Logger.getLogger(accesspointClient.class);

    /** String that represents the SSL security provided. */
    private static final  String SECURITY_PROVIDER = "SSL";

    /** String that represents the SSL Certificate provided. */
    private X509Certificate metadataCertificate = null;

    /** Loader of configuration properties file. */
    private Configuration config;

    /** Instance of an accesspoint client (service consumer) */
    private static accesspointClient instance;

    /**
     * Private constructor to avoid normal instance.
     * Initialize configuration loader to access properties.
     */
    private accesspointClient() {
        config = Configuration.getInstance();
    }

    /**
     * Synchronized static method to get an instance of a service consumer.
     * @return new Accesspoint client.
     */
    public synchronized static  accesspointClient getInstance() {

        if (instance == null) {
            instance = new accesspointClient();
        }
        return instance;
    }

    /**
     * Enables the monitorization of SOAP messages.
     *
     * @param value whether or not the monitorization is wanted.
     */
    public final void printSOAPLogging(final boolean value) {
        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump",
                String.valueOf(value));
    }

    public void setMetadataCertificate(X509Certificate metadataCertificate) {
        this.metadataCertificate = metadataCertificate;
    }

    /**
     * Sets up the certficate TrustManager.
     * @throws Exception
     *          Generic exception.
     */
    private void setupCertificateTrustManager() throws Exception{

        String acceptedCommonNames = null;
        if (metadataCertificate != null) {
            acceptedCommonNames = metadataCertificate.getSubjectX500Principal().getName();
            logger4J.debug("Certificate common names: " + acceptedCommonNames);
        }
        TrustManager[] trustManagers = new TrustManager[]{new AccessPointX509TrustManager(acceptedCommonNames, null)};
        SSLContext sc = SSLContext.getInstance(SECURITY_PROVIDER);
        sc.init(null, trustManagers, new java.security.SecureRandom());

        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    /**
     * Sets up the default hostname verifier.
     */
    private void setupHostNameVerifier() {
        HostnameVerifier hv = new HostnameVerifier() {
            @Override
            public boolean verify(final String hostname, final SSLSession session) {
                logger4J.debug("HostName verification done");
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }

    /**
     * Configures and returns a port that points to the a specific endpoint
     * address.
     *
     * @param address the address of the webservice.
     *
     * @return the port.
     */
    public final Resource getPort(final String address) {
        Resource port = null;
        try {
            HostnameVerifier hv = new HostnameVerifier() {
            @Override
            public boolean verify(final String hostname, final SSLSession session) {
                logger4J.debug("HostName verification done");
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
            port = setupEndpointAddress(address);
            logger4J.debug("Port: " + port);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(accesspointClient.class.getName()).log(Level.SEVERE, ex.getMessage());
            logger4J.error(ex.getMessage(), ex);

            String erroMSG = config.getProperty("error.message.client.port") + address;
            throw new AccessPointClientException(erroMSG, ex);
        }
        return port;
    }

    /**
     * Gets and configures a port that points to a given webservice address.
     *
     * @param address the address of the webservice.
     *
     * @return the configured port.
     */
    private Resource setupEndpointAddress(final String address) throws Exception{
        System.out.println("address: " + address);
        AccesspointService service = new AccesspointService();
        Map<String, Object> requestContext = null;
        Resource port = null;

        setupHostNameVerifier();
        logger4J.debug("Set HostVerifier");
        setupCertificateTrustManager();
        logger4J.debug("Set CertificateTrustManager");

        service.setHandlerResolver(new HandlerResolver() {

            @Override
            public List<Handler> getHandlerChain(final PortInfo pi) {
                List<Handler> handlerList = new ArrayList<Handler>();
                handlerList.add(new SOAPOutboundHandler(metadataCertificate));
                return handlerList;
            }
        });

        port = service.getResourceBindingPort();
        requestContext = ((BindingProvider) port).getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, address);

        return port;
    }

    /**
     * Sends a Create object using a given port and attaching the given
     * SOAPHeaderObject data to the SOAP-envelope.
     *
     * @param port the port which will be used to send the message.
     * @param soapHeader the SOAPHeaderObject holding the BUSDOX headers
     *      information that will be attached into the SOAP-envelope.
     * @param body Create object holding the SOAP-envelope payload.
     */
    public final void send(final Resource port,
            final SOAPHeaderObject soapHeader, final Create body) {

        SOAPOutboundHandler.setSoapHeader(soapHeader);
        logger4J.info("MessageIdentifier: " + soapHeader.getMessageIdentifier());
        logger4J.info("ChannelIdentifier: " + soapHeader.getChannelIdentifier());
        logger4J.info("RecipientIdentifier: " + soapHeader.getRecipientIdentifier().getValue());
        logger4J.info("SenderIdentifier: " + soapHeader.getSenderIdentifier().getValue());
        logger4J.info("DocumentIdentifier: " + soapHeader.getDocumentIdentifier().getValue());
        logger4J.info("ProcessIdentifier: " + soapHeader.getProcessIdentifier().getValue());

        try {
            port.create(body);
            logger4J.debug("Message " + soapHeader.getMessageIdentifier()
                        + " has been successfully delivered!");
        } catch (FaultMessage ex) {
            logger4J.error("Error while sending the message.", ex);
            java.util.logging.Logger.getLogger(accesspointClient.class.getName()).log(Level.SEVERE, ex.getMessage());

            String erroMSG = config.getProperty("error.message.client.send")
                        + soapHeader.getRecipientIdentifier().getValue();

            throw new AccessPointClientException(erroMSG, ex);
        } finally {
            if (port != null) {
                ((Closeable) port).close();
            }
        }
    }
}
