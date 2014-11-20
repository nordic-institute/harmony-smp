/*
 * 
 */
package eu.eCODEX.submission.original.generated._1_1;

import backend.ecodex.org._1_1.*;
import eu.eCODEX.submission.original.generated._1_1.exception.DownloadMessageFault;
import eu.eCODEX.submission.original.generated._1_1.exception.ListPendingMessagesFault;
import eu.eCODEX.submission.original.generated._1_1.exception.SendMessageFault;
import eu.eCODEX.submission.original.generated._1_1.exception.SendMessageWithReferenceFault;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.databinding.ADBException;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.receivers.AbstractInOutMessageReceiver;
import org.apache.axis2.util.JavaUtils;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The Class BackendServiceMessageReceiverInOut.
 */
public class BackendServiceMessageReceiverInOut extends AbstractInOutMessageReceiver {

    /* (non-Javadoc)
     * @see org.apache.axis2.receivers.AbstractInOutMessageReceiver#invokeBusinessLogic(org.apache.axis2.context.MessageContext, org.apache.axis2.context.MessageContext)
     */
    public void invokeBusinessLogic(final MessageContext msgContext, final MessageContext newMsgContext)
            throws AxisFault {
        try {
            // get the implementation class for the Web Service
            final Object obj = getTheImplementationObject(msgContext);
            final BackendServiceSkeleton skel = (BackendServiceSkeleton) obj;
            //Out Envelop
            SOAPEnvelope envelope = null;
            //Find the axisOperation that has been set by the Dispatch phase.
            final AxisOperation op = msgContext.getOperationContext().getAxisOperation();
            if (op == null) {
                throw new AxisFault(
                        "Operation is not located, if this is doclit style the SOAP-ACTION should specified via the SOAP Action to use the RawXMLProvider");
            }
            final String methodName;
            if ((op.getName() != null) &&
                ((methodName = JavaUtils.xmlNameToJavaIdentifier(op.getName().getLocalPart())) != null)) {
                if ("sendMessageWithReference".equals(methodName)) {
                    SendResponse sendResponse = null;

                    final SendRequestURL wrappedParam =
                            (SendRequestURL) fromOM(msgContext.getEnvelope().getBody().getFirstElement(),
                                                    SendRequestURL.class,
                                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                    final MessagingE messaging =
                            (MessagingE) fromOM(msgContext.getEnvelope().getHeader().getFirstElement(),
                                                MessagingE.class, getEnvelopeNamespaces(msgContext.getEnvelope()));
                    sendResponse = skel.sendMessageWithReference(messaging, wrappedParam);
                    envelope = toEnvelope(getSOAPFactory(msgContext), sendResponse, false);
                } else if ("sendMessage".equals(methodName)) {
                    SendResponse sendResponse = null;

                    final SendRequest wrappedParam =
                            (SendRequest) fromOM(msgContext.getEnvelope().getBody().getFirstElement(),
                                                 SendRequest.class, getEnvelopeNamespaces(msgContext.getEnvelope()));
                    final MessagingE messaging =
                            (MessagingE) fromOM(msgContext.getEnvelope().getHeader().getFirstElement(),
                                                MessagingE.class, getEnvelopeNamespaces(msgContext.getEnvelope()));
                    sendResponse = skel.sendMessage(messaging, wrappedParam);
                    envelope = toEnvelope(getSOAPFactory(msgContext), sendResponse, false);
                } else if ("listPendingMessages".equals(methodName)) {
                    ListPendingMessagesResponse listPendingMessagesResponse10 = null;
                    final ListPendingMessagesRequest wrappedParam =
                            (ListPendingMessagesRequest) fromOM(msgContext.getEnvelope().getBody().getFirstElement(),
                                                                ListPendingMessagesRequest.class,
                                                                getEnvelopeNamespaces(msgContext.getEnvelope()));
                    listPendingMessagesResponse10 = skel.listPendingMessages(wrappedParam);
                    envelope = toEnvelope(getSOAPFactory(msgContext), listPendingMessagesResponse10, false);
                } else if ("downloadMessage".equals(methodName)) {
                    MessagingE messagingE12 = null;
                    final DownloadMessageResponse downloadMessageResponse = new DownloadMessageResponse();
                    final DownloadMessageRequest wrappedParam =
                            (DownloadMessageRequest) fromOM(msgContext.getEnvelope().getBody().getFirstElement(),
                                                            DownloadMessageRequest.class,
                                                            getEnvelopeNamespaces(msgContext.getEnvelope()));
                    messagingE12 = skel.downloadMessage(downloadMessageResponse, wrappedParam);
                    envelope = toEnvelope(getSOAPFactory(msgContext), messagingE12, downloadMessageResponse, false);
                } else {
                    throw new RuntimeException("method not found");
                }
                newMsgContext.setEnvelope(envelope);
            }
        } catch (SendMessageFault e) {
            msgContext.setProperty(Constants.FAULT_NAME, "FaultDetail");
            final AxisFault f = createAxisFault(e);
            if (e.getFaultMessage() != null) {
                f.setDetail(toOM(e.getFaultMessage(), false));
            }
            throw f;
        } catch (ListPendingMessagesFault e) {
            msgContext.setProperty(Constants.FAULT_NAME, "FaultDetail");
            final AxisFault f = createAxisFault(e);
            if (e.getFaultMessage() != null) {
                f.setDetail(toOM(e.getFaultMessage(), false));
            }
            throw f;
        } catch (SendMessageWithReferenceFault e) {
            msgContext.setProperty(Constants.FAULT_NAME, "FaultDetail");
            final AxisFault f = createAxisFault(e);
            if (e.getFaultMessage() != null) {
                f.setDetail(toOM(e.getFaultMessage(), false));
            }
            throw f;
        } catch (DownloadMessageFault e) {
            msgContext.setProperty(Constants.FAULT_NAME, "FaultDetail");
            final AxisFault f = createAxisFault(e);
            if (e.getFaultMessage() != null) {
                f.setDetail(toOM(e.getFaultMessage(), false));
            }
            throw f;
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
    }

    //

    /**
     * To om.
     *
     * @param param           the param
     * @param optimizeContent the optimize content
     * @return the org.apache.axiom.om. om element
     * @throws AxisFault the axis fault
     */
    private OMElement toOM(final SendRequestURL param, final boolean optimizeContent) throws AxisFault {
        try {
            return param.getOMElement(SendRequestURL.MY_QNAME, OMAbstractFactory.getOMFactory());
        } catch (ADBException e) {
            throw AxisFault.makeFault(e);
        }
    }

    /**
     * To om.
     *
     * @param param           the param
     * @param optimizeContent the optimize content
     * @return the org.apache.axiom.om. om element
     * @throws AxisFault the axis fault
     */
    private OMElement toOM(final FaultDetail param, final boolean optimizeContent) throws AxisFault {
        try {
            return param.getOMElement(FaultDetail.MY_QNAME, OMAbstractFactory.getOMFactory());
        } catch (ADBException e) {
            throw AxisFault.makeFault(e);
        }
    }

    /**
     * To om.
     *
     * @param param           the param
     * @param optimizeContent the optimize content
     * @return the org.apache.axiom.om. om element
     * @throws AxisFault the axis fault
     */
    private OMElement toOM(final MessagingE param, final boolean optimizeContent) throws AxisFault {
        try {
            return param.getOMElement(MessagingE.MY_QNAME, OMAbstractFactory.getOMFactory());
        } catch (ADBException e) {
            throw AxisFault.makeFault(e);
        }
    }

    /**
     * To om.
     *
     * @param param           the param
     * @param optimizeContent the optimize content
     * @return the org.apache.axiom.om. om element
     * @throws AxisFault the axis fault
     */
    private OMElement toOM(final SendRequest param, final boolean optimizeContent) throws AxisFault {
        try {
            return param.getOMElement(SendRequest.MY_QNAME, OMAbstractFactory.getOMFactory());
        } catch (ADBException e) {
            throw AxisFault.makeFault(e);
        }
    }

    /**
     * To om.
     *
     * @param param           the param
     * @param optimizeContent the optimize content
     * @return the org.apache.axiom.om. om element
     * @throws AxisFault the axis fault
     */
    private SOAPEnvelope toEnvelope(final SOAPFactory factory, final SendResponse param, final boolean optimizeContent)
            throws AxisFault {
        try {
            final SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            if (param != null) {
                emptyEnvelope.getBody().addChild(param.getOMElement(SendResponse.MY_QNAME, factory));
            }
            return emptyEnvelope;
        } catch (ADBException e) {
            throw AxisFault.makeFault(e);
        }
    }

    /**
     * To om.
     *
     * @param param           the param
     * @param optimizeContent the optimize content
     * @return the org.apache.axiom.om. om element
     * @throws AxisFault the axis fault
     */
    private OMElement toOM(final ListPendingMessagesRequest param, final boolean optimizeContent) throws AxisFault {


        try {
            return param.getOMElement(ListPendingMessagesRequest.MY_QNAME, OMAbstractFactory.getOMFactory());
        } catch (ADBException e) {
            throw AxisFault.makeFault(e);
        }


    }

    /**
     * To om.
     *
     * @param param           the param
     * @param optimizeContent the optimize content
     * @return the org.apache.axiom.om. om element
     * @throws AxisFault the axis fault
     */
    private OMElement toOM(final ListPendingMessagesResponse param, final boolean optimizeContent) throws AxisFault {
        try {
            return param.getOMElement(ListPendingMessagesResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
        } catch (ADBException e) {
            throw AxisFault.makeFault(e);
        }
    }

    /**
     * To om.
     *
     * @param param           the param
     * @param optimizeContent the optimize content
     * @return the org.apache.axiom.om. om element
     * @throws AxisFault the axis fault
     */
    private OMElement toOM(final DownloadMessageRequest param, final boolean optimizeContent) throws AxisFault {
        try {
            return param.getOMElement(DownloadMessageRequest.MY_QNAME, OMAbstractFactory.getOMFactory());
        } catch (ADBException e) {
            throw AxisFault.makeFault(e);
        }
    }

    /**
     * To om.
     *
     * @param param           the param
     * @param optimizeContent the optimize content
     * @return the org.apache.axiom.om. om element
     * @throws AxisFault the axis fault
     */
    private OMElement toOM(final DownloadMessageResponse param, final boolean optimizeContent) throws AxisFault {
        try {
            return param.getOMElement(DownloadMessageResponse.MY_QNAME, OMAbstractFactory.getOMFactory());
        } catch (ADBException e) {
            throw AxisFault.makeFault(e);
        }
    }

    /**
     * To envelope.
     *
     * @param factory         the factory
     * @param param           the param
     * @param optimizeContent the optimize content
     * @return the org.apache.axiom.soap. soap envelope
     * @throws AxisFault the axis fault
     */
    private SOAPEnvelope toEnvelope(final SOAPFactory factory, final ListPendingMessagesResponse param,
                                    final boolean optimizeContent) throws AxisFault {
        try {
            final SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            if (param != null) {
                emptyEnvelope.getBody().addChild(param.getOMElement(ListPendingMessagesResponse.MY_QNAME, factory));
            }
            return emptyEnvelope;
        } catch (ADBException e) {
            throw AxisFault.makeFault(e);
        }
    }

    /**
     * Wraplist pending messages.
     *
     * @return the backend.ecodex.org._1_1. list pending messages response
     */
    private ListPendingMessagesResponse wraplistPendingMessages() {
        final ListPendingMessagesResponse wrappedElement = new ListPendingMessagesResponse();
        return wrappedElement;
    }

    /**
     * To envelope.
     *
     * @param factory         the factory
     * @param messaging       the messaging
     * @param param           the param
     * @param optimizeContent the optimize content
     * @return the org.apache.axiom.soap. soap envelope
     * @throws AxisFault the axis fault
     */
    private SOAPEnvelope toEnvelope(final SOAPFactory factory, final MessagingE messaging,
                                    final DownloadMessageResponse param, final boolean optimizeContent)
            throws AxisFault {
        try {
            final SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            if (messaging != null) {
                emptyEnvelope.getHeader().addChild(messaging.getOMElement(MessagingE.MY_QNAME, factory));
            }
            if (param != null) {
                emptyEnvelope.getBody().addChild(param.getOMElement(DownloadMessageResponse.MY_QNAME, factory));
            }
            return emptyEnvelope;
        } catch (ADBException e) {
            throw AxisFault.makeFault(e);
        }
    }

    /**
     * Wrapdownload message.
     *
     * @return the backend.ecodex.org._1_1. download message response
     */
    private DownloadMessageResponse wrapdownloadMessage() {
        final DownloadMessageResponse wrappedElement = new DownloadMessageResponse();
        return wrappedElement;
    }

    /**
     * To envelope.
     *
     * @param factory the factory
     * @return the org.apache.axiom.soap. soap envelope
     */
    private SOAPEnvelope toEnvelope(final SOAPFactory factory) {
        return factory.getDefaultEnvelope();
    }

    /**
     * From om.
     *
     * @param param           the param
     * @param type            the type
     * @param extraNamespaces the extra namespaces
     * @return the java.lang. object
     * @throws AxisFault the axis fault
     */
    private Object fromOM(final OMElement param, final Class type, final Map extraNamespaces) throws AxisFault {
        try {
            if (SendRequestURL.class.equals(type)) {
                return SendRequestURL.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }
            if (FaultDetail.class.equals(type)) {
                return FaultDetail.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }
            if (MessagingE.class.equals(type)) {
                return MessagingE.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }
            if (SendRequest.class.equals(type)) {
                return SendRequest.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }
            if (SendResponse.class.equals(type)) {
                return SendResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }
            if (FaultDetail.class.equals(type)) {
                return FaultDetail.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }
            if (MessagingE.class.equals(type)) {
                return MessagingE.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }
            if (ListPendingMessagesRequest.class.equals(type)) {
                return ListPendingMessagesRequest.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }
            if (ListPendingMessagesResponse.class.equals(type)) {
                return ListPendingMessagesResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }
            if (FaultDetail.class.equals(type)) {
                return FaultDetail.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }
            if (DownloadMessageRequest.class.equals(type)) {
                return DownloadMessageRequest.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }
            if (DownloadMessageResponse.class.equals(type)) {
                return DownloadMessageResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }
            if (FaultDetail.class.equals(type)) {
                return FaultDetail.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }
            if (MessagingE.class.equals(type)) {
                return MessagingE.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }
        } catch (Exception e) {
            throw AxisFault.makeFault(e);
        }
        return null;
    }

    /**
     * Gets the envelope namespaces.
     *
     * @param env the env
     * @return the envelope namespaces
     */
    private Map getEnvelopeNamespaces(final SOAPEnvelope env) {
        final Map returnMap = new HashMap();
        final Iterator namespaceIterator = env.getAllDeclaredNamespaces();
        while (namespaceIterator.hasNext()) {
            final OMNamespace ns = (OMNamespace) namespaceIterator.next();
            returnMap.put(ns.getPrefix(), ns.getNamespaceURI());
        }
        return returnMap;
    }

    /**
     * Creates the axis fault.
     *
     * @param e the e
     * @return the org.apache.axis2. axis fault
     */
    private AxisFault createAxisFault(final Exception e) {
        final AxisFault f;
        final Throwable cause = e.getCause();
        if (cause != null) {
            f = new AxisFault(e.getMessage(), cause);
        } else {
            f = new AxisFault(e.getMessage());
        }
        return f;
    }
}//end of class
