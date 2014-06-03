package eu.domibus.common.soap;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.soap.*;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.SOAPMessageFormatter;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.log4j.Logger;

import javax.activation.DataHandler;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

//import org.apache.axis2.transport.TransportUtils;
//import org.apache.axiom.attachments.Attachments;

/**
 * @author Hamid Ben Malek
 */
public class Message implements java.io.Serializable {
    private static final long serialVersionUID = -4289356544949842551L;

    private static final Logger log = Logger.getLogger(Message.class);

    private double soapVersion = 1.1;
    private List<OMElement> headerElements = new ArrayList<OMElement>();
    private List<OMElement> bodyElements = new ArrayList<OMElement>();
    private MessageContext msgContext = new MessageContext();

    public Message() {
        msgContext.setDoingSwA(true);
    }

    public Message(final OMElement[] headers, final OMElement bodyElement) {
        this();
        if (headers != null && headers.length > 0) {
            for (final OMElement header : headers) {
                headerElements.add(header);
            }
        }
        if (bodyElement != null) {
            bodyElements.add(bodyElement);
        }
    }

    public Message(final OMElement[] headers, final OMElement bodyElement, final DataHandler[] payloads) {
        this(headers, bodyElement);
        if (payloads != null && payloads.length > 0) {
            for (final DataHandler attachment : payloads) {
                msgContext.addAttachment(attachment);
            }
        }
    }

    public String addAttachment(final DataHandler attachment) {
        if (attachment == null) {
            return null;
        }
        return msgContext.addAttachment(attachment);
    }

    public DataHandler getAttachment(final String cid) {
        return msgContext.getAttachment(cid);
    }

    public void addHeaderElement(final OMElement header) {
        if (header == null) {
            return;
        }
        headerElements.add(header);
    }

    public SOAPHeaderBlock addHeaderElement(final String localName, final String uri, final String prefix) {
        final SOAPEnvelope env = getEnvelope();
        final OMNamespace ns = env.getOMFactory().createOMNamespace(uri, prefix);
        SOAPHeader soapHeader = env.getHeader();
        if (soapHeader == null) {
            soapHeader = ((SOAPFactory) env.getOMFactory()).createSOAPHeader(env);
        }
        return soapHeader.addHeaderBlock(localName, ns);
    }

    public SOAPHeaderBlock createHeaderElement(final String localName, final String uri, final String prefix) {
        final SOAPFactory factory = getSOAPFactory();
        final OMNamespace ns = factory.createOMNamespace(uri, prefix);
        return factory.createSOAPHeaderBlock(localName, ns);
    }

    public void addBodyElement(final OMElement payload) {
        if (payload == null) {
            return;
        }
        bodyElements.add(payload);
    }

    public SOAPEnvelope getEnvelope() {
        final SOAPEnvelope env = createEnvelope();
        try {
            msgContext.setEnvelope(env);
        } catch (AxisFault ex) {
            log.error("Error occured while setting Envelope on MessageContext", ex);
        }

        return env;
    }

    public MessageContext getMessageContext() {
        final SOAPEnvelope env = createEnvelope();
        try {
            msgContext.setEnvelope(env);
        } catch (AxisFault ex) {
            log.error("Error occured while setting Envelope on MessageContext", ex);
        }

        return msgContext;
    }

    public void setSoapVersion(final double version) {
        this.soapVersion = version;
    }

    public double getSoapVersion() {
        return soapVersion;
    }

    private SOAPFactory getSOAPFactory() {
        SOAPFactory omFactory = null;
        if (soapVersion < 1.2) {
            omFactory = OMAbstractFactory.getSOAP11Factory();
        } else {
            omFactory = OMAbstractFactory.getSOAP12Factory();
        }
        return omFactory;
    }

    private SOAPEnvelope createEnvelope() {
        final SOAPFactory omFactory = getSOAPFactory();
        final SOAPEnvelope envelope = omFactory.getDefaultEnvelope();
        envelope.declareNamespace("http://www.w3.org/1999/XMLSchema-instance/", "xsi");
        envelope.declareNamespace("http://www.w3.org/1999/XMLSchema", "xsd");
        if (headerElements != null && headerElements.size() > 0) {
            for (final OMElement header : headerElements) {
                envelope.getHeader().addChild(header);
            }
        }
        if (bodyElements != null && bodyElements.size() > 0) {
            for (final OMElement paylod : bodyElements) {
                envelope.getBody().addChild(paylod);
            }
        }
        return envelope;
    }

    public MessageContext call(final String soapAction, final String endpointURI) {
        if (endpointURI == null || endpointURI.trim().equals("")) {
            return null;
        }
        final EndpointReference targetEPR = new EndpointReference(endpointURI);
        final Options options = new Options();
        options.setAction(soapAction);
        options.setTo(targetEPR);
        options.setProperty(Constants.Configuration.ENABLE_SWA, Constants.VALUE_TRUE);
        if (soapVersion < 1.2) {
            options.setSoapVersionURI(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        } else {
            options.setSoapVersionURI(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        }
        options.setProperty(Constants.Configuration.CACHE_ATTACHMENTS, Constants.VALUE_TRUE);
        File tmpdir = new File(System.getProperty("java.io.tmpdir"));
        if (!tmpdir.exists()) {
            tmpdir = new File("C:\\temp");
        }
        if (!tmpdir.exists()) {
            tmpdir.mkdirs();
        }
        options.setProperty(Constants.Configuration.ATTACHMENT_TEMP_DIR, tmpdir.getAbsolutePath());
        options.setProperty(Constants.Configuration.FILE_SIZE_THRESHOLD, "4000");
        options.setTimeOutInMilliSeconds(10000);
        try {
            final ServiceClient sender = new ServiceClient();
            sender.setOptions(options);
            final OperationClient mepClient = sender.createClient(ServiceClient.ANON_OUT_IN_OP);

            mepClient.addMessageContext(getMessageContext());
            mepClient.execute(true);
            return mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
        } catch (AxisFault ex) {
            log.error("Error while setting up OperationClient", ex);
        }
        return null;
    }

    public void writeTo(final OutputStream out) {
        if (out == null) {
            return;
        }
        final String cth =
                "MIME-Version: 1.0\n" + "Content-Type: Multipart/Related; boundary=MIME_boundary; type=text/xml;\n\n";
        final SOAPMessageFormatter smf = new SOAPMessageFormatter();
        final OMOutputFormat format = new OMOutputFormat();
        format.setDoingSWA(true);
        format.setCharSetEncoding("UTF-8");
        format.setMimeBoundary("MIME_boundary");
        try {
            final BufferedOutputStream bout = new BufferedOutputStream(out);
            bout.write(cth.getBytes());
            smf.writeTo(getMessageContext(), format, bout, true);
            bout.flush();
            bout.close();
        } catch (AxisFault ex) {
            log.error("Error during serialization of message", ex);
        } catch (IOException e) {
            log.error("I/O exception occured while writing message to outputstream", e);
        }
    }
}
