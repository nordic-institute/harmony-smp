package eu.domibus.common.util;

import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.util.UIDGenerator;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.OperationContext;
import org.apache.axis2.engine.AxisEngine;
import org.apache.axis2.transport.TransportUtils;
import org.apache.axis2.transport.http.SOAPMessageFormatter;
import org.apache.axis2.util.MessageContextBuilder;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.log4j.Logger;

import javax.activation.DataHandler;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.Iterator;
import java.util.List;

/**
 */
public class WSUtil {

    private static final Logger log = Logger.getLogger(WSUtil.class);

    /**
     * Returns all ContentIDs for a given {@link MessageContext}
     *
     * @param msgContext the {@link MessageContext} from which you want to get the contentIDs
     * @return String[] all ContentIds contained in the {@link MessageContext}
     */
    public static String[] getAllContentIDs(final MessageContext msgContext) {
        final Attachments atts = msgContext.getAttachmentMap();
        return atts.getAllContentIDs();
    }

    /**
     * Method createEnvelope.
     *
     * @param soapVersion double
     * @param headers     OMElement[]
     * @param bodyPayload OMElement
     * @return SOAPEnvelope
     */
    public SOAPEnvelope createEnvelope(final double soapVersion, final OMElement[] headers,
                                       final OMElement bodyPayload) {
        final SOAPEnvelope env = XMLUtil.createEnvelope(soapVersion);
        if ((headers != null) && (headers.length > 0)) {
            for (final OMElement header : headers) {
                env.getHeader().addChild(header);
            }
        }
        if (bodyPayload != null) {
            env.getBody().addChild(bodyPayload);
        }
        return env;
    }

    /**
     * Method invoke.
     *
     * @param soapAction  String
     * @param env         SOAPEnvelope
     * @param attachments DataHandler[]
     * @param endpointURI String
     * @return MessageContext
     */
    public MessageContext invoke(final String soapAction, final SOAPEnvelope env, final DataHandler[] attachments,
                                 final String endpointURI) {
        //	if (env == null || endpointURI == null || endpointURI.trim().equals(""))
        //	    return null;
        final EndpointReference targetEPR = new EndpointReference(endpointURI);
        final Options options = new Options();
        options.setAction(soapAction);
        options.setTo(targetEPR);
        options.setProperty(Constants.Configuration.ENABLE_SWA, Constants.VALUE_TRUE);
        if (env.getNamespace().getNamespaceURI().equals(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI)) {
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

            final MessageContext mc = new MessageContext();
            mc.setDoingSwA(true);
            mc.setEnvelope(env);
            if ((attachments != null) && (attachments.length > 0)) {
                for (final DataHandler att : attachments) {
                    mc.addAttachment(att);
                }
            }

            mepClient.addMessageContext(mc);
            mepClient.execute(true);
            return mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
        } catch (AxisFault ex) {
            WSUtil.log.error("Problem occured during invoke", ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * Method getHeaderElementByName.
     *
     * @param context   MessageContext
     * @param localName String
     * @return OMElement
     */
    public static OMElement getHeaderElementByName(final MessageContext context, final String localName) {
        if ((context == null) || (localName == null)) {
            return null;
        }
        if (context.getEnvelope() == null) {
            return null;
        }
        final OMElement soapHeader = context.getEnvelope().getHeader();
        if (soapHeader == null) {
            return null;
        }
        final Iterator it = soapHeader.getChildElements();
        OMElement temp = null;
        while ((it != null) && it.hasNext()) {
            temp = XMLUtil.getFirstGrandChildWithName((OMElement) it.next(), localName);
            if (temp != null) {
                return temp;
            }
        }
        return null;
    }


    /**
     * Method getPropertyFromInMsgCtx.
     *
     * @param outMsgCtx MessageContext
     * @param key       String
     * @return Object
     */
    public static Object getPropertyFromInMsgCtx(final MessageContext outMsgCtx, final String key) {
        if ((outMsgCtx == null) || (key == null)) {
            return null;
        }
        try {
            final OperationContext opContext = outMsgCtx.getOperationContext();
            final MessageContext inMsgContext = opContext.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            return inMsgContext.getProperty(key);
        } catch (AxisFault ex) {
            WSUtil.log.error("Error while executing propertyFromInMsgCtx", ex);
            return null;
        }
    }

    /**
     * Method getOutgoingMsgCtxFromResponse.
     *
     * @param respMsgCtx MessageContext
     * @return MessageContext
     */
    public static MessageContext getOutgoingMsgCtxFromResponse(final MessageContext respMsgCtx) {
        try {
            return respMsgCtx.getServiceContext().getLastOperationContext()
                             .getMessageContext(WSDLConstants.MESSAGE_LABEL_OUT_VALUE);
        } catch (AxisFault ex) {
            WSUtil.log.error("Error while executing getOutgoingMsgCtxFromResponse", ex);
        }
        return null;
    }

    /**
     * Method getEnvelopeFromInMsg.
     *
     * @param outMsgContext MessageContext
     * @return SOAPEnvelope
     */
    public static SOAPEnvelope getEnvelopeFromInMsg(final MessageContext outMsgContext) {
        if (outMsgContext == null) {
            return null;
        }
        try {
            final OperationContext opContext = outMsgContext.getOperationContext();
            final MessageContext inMsgContext = opContext.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            return inMsgContext.getEnvelope();
        } catch (AxisFault ex) {
            WSUtil.log.error("Error while executing getEnvelopeFromInMsg", ex);
            return null;
        }
    }

    /**
     * Method createMessageContext.
     *
     * @param requestMessageContext MessageContext
     * @return MessageContext
     * @throws AxisFault
     */
    private static MessageContext createMessageContext(final MessageContext requestMessageContext) throws AxisFault {
        MessageContext outMessage = null;
        final OperationContext opContext = requestMessageContext.getOperationContext();
        if (opContext != null) {
            outMessage = opContext.getMessageContext(WSDLConstants.MESSAGE_LABEL_OUT_VALUE);
        }
        if (outMessage == null) {
            outMessage = MessageContextBuilder.createOutMessageContext(requestMessageContext);
            outMessage.getOperationContext().addMessageContext(outMessage);
        }

        return outMessage;
    }

    /**
     * Method sendMessage.
     *
     * @param outMessage            MessageContext
     * @param response              SOAPEnvelope
     * @param requestMessageContext MessageContext
     * @throws AxisFault
     */
    private static void sendMessage(final MessageContext outMessage, final SOAPEnvelope response,
                                    final MessageContext requestMessageContext) throws AxisFault {
        outMessage.setEnvelope(response);
        outMessage.setResponseWritten(true);
        final ConfigurationContext context = requestMessageContext.getConfigurationContext();
        // TODO check AxisEngine engine = new AxisEngine(context);
        final AxisEngine engine = new AxisEngine();
        engine.send(outMessage);
        requestMessageContext.pause();
    }

    /**
     * Method sendResponse.
     *
     * @param response              SOAPEnvelope
     * @param requestMessageContext MessageContext
     */
    public static void sendResponse(final SOAPEnvelope response, final MessageContext requestMessageContext) {
        try {
            final MessageContext outMessage = WSUtil.createMessageContext(requestMessageContext);
            WSUtil.sendMessage(outMessage, response, requestMessageContext);
        } catch (AxisFault ex) {
            WSUtil.log.error("Error during sendResponse for a given SOAPEnvelope", ex);
        }
    }

    /**
     * Method sendResponse.
     *
     * @param respMessage           MessageContext
     * @param requestMessageContext MessageContext
     */
    public static void sendResponse(final MessageContext respMessage, final MessageContext requestMessageContext) {
        try {
            final MessageContext outMessage = WSUtil.createMessageContext(requestMessageContext);

            if (respMessage.getAttachmentMap() != null) {
                outMessage.setAttachmentMap(respMessage.getAttachmentMap());
            }
            WSUtil.sendMessage(outMessage, respMessage.getEnvelope(), requestMessageContext);
        } catch (AxisFault ex) {
            WSUtil.log.error("Error during sendResponse for a given MessageContext", ex);
        }
    }

    /**
     * Method logPrefix.
     *
     * @param msgCtx MessageContext
     * @return String
     */
    public static String logPrefix(final MessageContext msgCtx) {
        if (msgCtx == null) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        sb.append(msgCtx.isServerSide() ? "ServerSide" : "ClientSide");
        sb.append(", ");
        sb.append((msgCtx.getFLOW() == MessageContext.OUT_FLOW) ? "OutFlow: " : "InFlow: ");
        return sb.toString();
    }

    /**
     * Writes the envelope and attachments to a file in the form of a SwA
     * message on the wire, and returns the content type. To read back the
     * message context, use the readMessage(msgCtx, file, contentType) method.
     *
     * @param ctx  MessageContext
     * @param file File
     * @return String
     */
    public static String writeMessage(final MessageContext ctx, final File file) {
        if ((ctx == null) || (file == null)) {
            return null;
        }
        final long start = System.currentTimeMillis();
        String name = UIDGenerator.generateURNString();
        name = name.substring(name.lastIndexOf(":") + 1);
        final String boundary = "boundary_" + name;
        final OMOutputFormat format = new OMOutputFormat();
        format.setDoingSWA(true);
        format.setDoOptimize(false);
        format.setSOAP11(true);
        format.setCharSetEncoding("UTF-8");
        format.setMimeBoundary(boundary);
        format.setAutoCloseWriter(true);

        final SOAPMessageFormatter formatter = new SOAPMessageFormatter();
        final String ct = formatter.getContentType(ctx, format, null);

        if (ctx.getAttachmentMap() != null) {
            final String cid = ctx.getAttachmentMap().getRootPartContentID();
            ctx.getAttachmentMap().removeDataHandler(cid);
        }

        try {
            final FileWriter fw = new FileWriter(file);
            fw.write("Content-Type: " + ct + "\n\n");
            fw.close();
            final FileOutputStream fos = new FileOutputStream(file, true);
            formatter.writeTo(ctx, format, fos, true);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException fnfe) {
            WSUtil.log.error("Problem while opening FileOutputStream", fnfe);
        } catch (AxisFault af) {
            WSUtil.log.error("Error while writing SOAPMessage to file", af);
        } catch (IOException ioe) {
            WSUtil.log.error("I/O error occured", ioe);
        }
        final long end = System.currentTimeMillis();
        WSUtil.log.debug("--- It took " + (end - start) + " milliseconds to write Message to file");
        return ct;
    }


    /**
     * For this method to work, the message context needs to have a
     * configuration context object. This method populates the provided message
     * context with the data from the file (envelope + attachments)
     *
     * @param mc   : the MessageContext to populate with the message in the file
     * @param file : the file from which to read the message
     * @param ct   : the content type of the message. This is returned by the
     *             writeMessage(msgCtx, file) method.
     * @return MessageContext
     */
    public static MessageContext readMessage(MessageContext mc, final File file, final String ct) {
        if ((file == null) || !file.exists()) {
            return null;
        }
        if (mc == null) {
            mc = new MessageContext();
        }
        try {
            final FileInputStream in = new FileInputStream(file);

            final SOAPEnvelope envelope = TransportUtils.createSOAPMessage(mc, in, ct);
            final String startCID = WSUtil.getStartCID(ct);
            final Attachments atts = mc.getAttachmentMap();
            atts.removeDataHandler(startCID);
            mc.setEnvelope(envelope);
        } catch (FileNotFoundException fnfe) {
            WSUtil.log.error("Problem while opening FileOutputStream", fnfe);
        } catch (AxisFault af) {
            WSUtil.log.error("AxisFault during readMessage", af);
        } catch (OMException ome) {
            WSUtil.log.error("OMFault during readMessage", ome);
        } catch (XMLStreamException xse) {
            WSUtil.log.error("XMLStreamException during readMessage", xse);
        } catch (FactoryConfigurationError fce) {
            WSUtil.log.error("FactoryConfigurationError during readMessage", fce);
        }
        return mc;
    }


    /**
     * Method getStartCID.
     *
     * @param ct String
     * @return String
     */
    private static String getStartCID(final String ct) {
        if (ct == null) {
            return null;
        }
        final int i = ct.indexOf("start=\"");
        String temp = null;
        if (i >= 0) {
            temp = ct.substring(i + 7);
        }
        final int j = temp.indexOf("\"");
        temp = temp.substring(0, j);
        if (temp.startsWith("<")) {
            temp = temp.substring(1, temp.length() - 1);
        }
        return temp;
    }

    /**
     * Method getMessageBytes.
     *
     * @param ctx MessageContext
     * @return byte[]
     */
    public static byte[] getMessageBytes(final MessageContext ctx) {
        if (ctx == null) {
            return null;
        }
        final long start = System.currentTimeMillis();
        String name = UIDGenerator.generateURNString();
        name = name.substring(name.lastIndexOf(":") + 1);
        final String boundary = "boundary_" + name;
        final OMOutputFormat format = new OMOutputFormat();
        format.setDoingSWA(true);
        format.setDoOptimize(false);
        format.setSOAP11(true);
        format.setCharSetEncoding("UTF-8");
        format.setMimeBoundary(boundary);
        format.setAutoCloseWriter(true);

        final SOAPMessageFormatter formatter = new SOAPMessageFormatter();
        final String ct = formatter.getContentType(ctx, format, null);
        // format.setContentType("multipart/related");
        format.setContentType(ct);
        if (ctx.getAttachmentMap() != null) {
            final String cid = ctx.getAttachmentMap().getRootPartContentID();
            ctx.getAttachmentMap().removeDataHandler(cid);
        }

        try {
            return formatter.getBytes(ctx, format);
        } catch (AxisFault af) {
            WSUtil.log.error("Error occured while getting formatted SOAP Message in bytes", af);
        }
        return null;
    }


    /**
     * Method writeAttachments.
     *
     * @param msgCtx       MessageContext
     * @param cids         List<String>
     * @param contentTypes List<String>
     * @param location     File
     */
    public static void writeAttachments(final MessageContext msgCtx, final List<String> cids,
                                        final List<String> contentTypes, final File location) {
        if ((msgCtx == null) || (cids == null) || (cids.size() <= 0)) {
            return;
        }
        final Attachments atts = msgCtx.getAttachmentMap();
        if (atts == null) {
            return;
        }
        final String soapPartCid = atts.getRootPartContentID();
        for (int i = 0; i < cids.size(); i++) {
            if ((cids.get(i) != null) && !cids.get(i).equals(soapPartCid)) {
                String extension1 = null;
                if (contentTypes.get(i) != null) {
                    extension1 = FileUtil.getFileExtension(contentTypes.get(i));
                }
                final DataHandler dh = atts.getDataHandler(cids.get(i));
                String name = cids.get(i);
                if (cids.get(i).indexOf(":") >= 0) {
                    name = name.replaceAll(":", "-");
                }
                if (cids.get(i).indexOf("@") >= 0) {
                    name = name.replaceAll("@", "_");
                }
                final String extension2 = FileUtil.getFileExtension(dh.getContentType());
                final String path = location.getAbsolutePath() + File.separator + name + "." +
                                    ((extension1 == null) ? "" : (extension1 + ".")) + extension2;
                final File att = new File(path);
                FileUtil.writeDataHandlerToFile(dh, att);
            }
        }
    }


}
