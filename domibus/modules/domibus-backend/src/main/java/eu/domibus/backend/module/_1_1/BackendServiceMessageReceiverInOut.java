/*
 * 
 */
package eu.domibus.backend.module._1_1;

import org.apache.axis2.AxisFault;
import eu.domibus.backend.module._1_1.exception.DownloadMessageFault;
import eu.domibus.backend.module._1_1.exception.ListPendingMessagesFault;
import eu.domibus.backend.module._1_1.exception.SendMessageFault;
import eu.domibus.backend.module._1_1.exception.SendMessageWithReferenceFault;

/**
 * The Class BackendServiceMessageReceiverInOut.
 */
public class BackendServiceMessageReceiverInOut extends org.apache.axis2.receivers.AbstractInOutMessageReceiver {

    /* (non-Javadoc)
     * @see org.apache.axis2.receivers.AbstractInOutMessageReceiver#invokeBusinessLogic(org.apache.axis2.context.MessageContext, org.apache.axis2.context.MessageContext)
     */
    public void invokeBusinessLogic(final org.apache.axis2.context.MessageContext msgContext,
                                    final org.apache.axis2.context.MessageContext newMsgContext)
            throws org.apache.axis2.AxisFault {
        try {
            // get the implementation class for the Web Service
            final Object obj = getTheImplementationObject(msgContext);
            final BackendServiceSkeleton skel = (BackendServiceSkeleton) obj;
            //Out Envelop
            org.apache.axiom.soap.SOAPEnvelope envelope = null;
            //Find the axisOperation that has been set by the Dispatch phase.
            final org.apache.axis2.description.AxisOperation op = msgContext.getOperationContext().getAxisOperation();
            if (op == null) {
                throw new org.apache.axis2.AxisFault(
                        "Operation is not located, if this is doclit style the SOAP-ACTION should specified via the SOAP Action to use the RawXMLProvider");
            }
            final java.lang.String methodName;
            if ((op.getName() != null) &&
                ((methodName = org.apache.axis2.util.JavaUtils.xmlNameToJavaIdentifier(op.getName().getLocalPart())) !=
                 null)) {
                if ("sendMessageWithReference".equals(methodName) ||  "sendWithReference".equals(methodName)) {
                    backend.ecodex.org._1_1.SendResponse sendResponse = null;

                    final backend.ecodex.org._1_1.SendRequestURL wrappedParam =
                            (backend.ecodex.org._1_1.SendRequestURL) fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    backend.ecodex.org._1_1.SendRequestURL.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                    final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE messaging =
                            (org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE) fromOM(
                                    msgContext.getEnvelope().getHeader().getFirstElement(),
                                    org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                    sendResponse = skel.sendMessageWithReference(messaging, wrappedParam);
                    envelope = toEnvelope(getSOAPFactory(msgContext), sendResponse, false);
                } else if ("sendMessage".equals(methodName) || "send".equals(methodName)) {
                    backend.ecodex.org._1_1.SendResponse sendResponse = null;

                    final backend.ecodex.org._1_1.SendRequest wrappedParam =
                            (backend.ecodex.org._1_1.SendRequest) fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    backend.ecodex.org._1_1.SendRequest.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                    final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE messaging =
                            (org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE) fromOM(
                                    msgContext.getEnvelope().getHeader().getFirstElement(),
                                    org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                    sendResponse = skel.sendMessage(messaging, wrappedParam);
                    envelope = toEnvelope(getSOAPFactory(msgContext), sendResponse, false);
                } else if ("listPendingMessages".equals(methodName)) {
                    backend.ecodex.org._1_1.ListPendingMessagesResponse listPendingMessagesResponse10 = null;
                    final backend.ecodex.org._1_1.ListPendingMessagesRequest wrappedParam =
                            (backend.ecodex.org._1_1.ListPendingMessagesRequest) fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    backend.ecodex.org._1_1.ListPendingMessagesRequest.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                    listPendingMessagesResponse10 = skel.listPendingMessages(wrappedParam);
                    envelope = toEnvelope(getSOAPFactory(msgContext), listPendingMessagesResponse10, false);
                } else if ("downloadMessage".equals(methodName)) {
                    org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE messagingE12 = null;
                    final backend.ecodex.org._1_1.DownloadMessageResponse downloadMessageResponse =
                            new backend.ecodex.org._1_1.DownloadMessageResponse();
                    final backend.ecodex.org._1_1.DownloadMessageRequest wrappedParam =
                            (backend.ecodex.org._1_1.DownloadMessageRequest) fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    backend.ecodex.org._1_1.DownloadMessageRequest.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                    messagingE12 = skel.downloadMessage(downloadMessageResponse, wrappedParam);
                    envelope = toEnvelope(getSOAPFactory(msgContext), messagingE12, downloadMessageResponse, false);
                } else {
                    throw new java.lang.RuntimeException("method not found");
                }
                newMsgContext.setEnvelope(envelope);
            }
        } catch (SendMessageFault e) {
            msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME, "FaultDetail");
            final org.apache.axis2.AxisFault f = createAxisFault(e);
            if (e.getFaultMessage() != null) {
                f.setDetail(toOM(e.getFaultMessage(), false));
            }
            throw f;
        } catch (ListPendingMessagesFault e) {
            msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME, "FaultDetail");
            final org.apache.axis2.AxisFault f = createAxisFault(e);
            if (e.getFaultMessage() != null) {
                f.setDetail(toOM(e.getFaultMessage(), false));
            }
            throw f;
        } catch (SendMessageWithReferenceFault e) {
            msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME, "FaultDetail");
            final org.apache.axis2.AxisFault f = createAxisFault(e);
            if (e.getFaultMessage() != null) {
                f.setDetail(toOM(e.getFaultMessage(), false));
            }
            throw f;
        } catch (DownloadMessageFault e) {
            msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME, "FaultDetail");
            final org.apache.axis2.AxisFault f = createAxisFault(e);
            if (e.getFaultMessage() != null) {
                f.setDetail(toOM(e.getFaultMessage(), false));
            }
            throw f;
        } catch (java.lang.Exception e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
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
    private org.apache.axiom.om.OMElement toOM(final backend.ecodex.org._1_1.SendRequestURL param,
                                               final boolean optimizeContent) throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(backend.ecodex.org._1_1.SendRequestURL.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
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
    private org.apache.axiom.om.OMElement toOM(final backend.ecodex.org._1_1.FaultDetail param,
                                               final boolean optimizeContent) throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(backend.ecodex.org._1_1.FaultDetail.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
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
    private org.apache.axiom.om.OMElement toOM(
            final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE param,
            final boolean optimizeContent) throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
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
    private org.apache.axiom.om.OMElement toOM(final backend.ecodex.org._1_1.SendRequest param,
                                               final boolean optimizeContent) throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(backend.ecodex.org._1_1.SendRequest.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
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
    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
                                                          final backend.ecodex.org._1_1.SendResponse param,
                                                          final boolean optimizeContent)
            throws org.apache.axis2.AxisFault {
        try {
            final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            if (param != null) {
                emptyEnvelope.getBody()
                             .addChild(param.getOMElement(backend.ecodex.org._1_1.SendResponse.MY_QNAME, factory));
            }
            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
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
    private org.apache.axiom.om.OMElement toOM(final backend.ecodex.org._1_1.ListPendingMessagesRequest param,
                                               final boolean optimizeContent) throws org.apache.axis2.AxisFault {


        try {
            return param.getOMElement(backend.ecodex.org._1_1.ListPendingMessagesRequest.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
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
    private org.apache.axiom.om.OMElement toOM(final backend.ecodex.org._1_1.ListPendingMessagesResponse param,
                                               final boolean optimizeContent) throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(backend.ecodex.org._1_1.ListPendingMessagesResponse.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
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
    private org.apache.axiom.om.OMElement toOM(final backend.ecodex.org._1_1.DownloadMessageRequest param,
                                               final boolean optimizeContent) throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(backend.ecodex.org._1_1.DownloadMessageRequest.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
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
    private org.apache.axiom.om.OMElement toOM(final backend.ecodex.org._1_1.DownloadMessageResponse param,
                                               final boolean optimizeContent) throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(backend.ecodex.org._1_1.DownloadMessageResponse.MY_QNAME,
                                      org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
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
    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
                                                          final backend.ecodex.org._1_1.ListPendingMessagesResponse param,
                                                          final boolean optimizeContent)
            throws org.apache.axis2.AxisFault {
        try {
            final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            if (param != null) {
                emptyEnvelope.getBody().addChild(
                        param.getOMElement(backend.ecodex.org._1_1.ListPendingMessagesResponse.MY_QNAME, factory));
            }
            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    /**
     * Wraplist pending messages.
     *
     * @return the backend.ecodex.org._1_1. list pending messages response
     */
    private backend.ecodex.org._1_1.ListPendingMessagesResponse wraplistPendingMessages() {
        final backend.ecodex.org._1_1.ListPendingMessagesResponse wrappedElement =
                new backend.ecodex.org._1_1.ListPendingMessagesResponse();
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
    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
                                                          final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE messaging,
                                                          final backend.ecodex.org._1_1.DownloadMessageResponse param,
                                                          final boolean optimizeContent)
            throws org.apache.axis2.AxisFault {
        try {
            final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            if (messaging != null) {
                emptyEnvelope.getHeader().addChild(messaging.getOMElement(
                        org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE.MY_QNAME, factory));
            }
            if (param != null) {
                emptyEnvelope.getBody().addChild(
                        param.getOMElement(backend.ecodex.org._1_1.DownloadMessageResponse.MY_QNAME, factory));
            }
            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    /**
     * Wrapdownload message.
     *
     * @return the backend.ecodex.org._1_1. download message response
     */
    private backend.ecodex.org._1_1.DownloadMessageResponse wrapdownloadMessage() {
        final backend.ecodex.org._1_1.DownloadMessageResponse wrappedElement =
                new backend.ecodex.org._1_1.DownloadMessageResponse();
        return wrappedElement;
    }

    /**
     * To envelope.
     *
     * @param factory the factory
     * @return the org.apache.axiom.soap. soap envelope
     */
    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory) {
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
    private java.lang.Object fromOM(final org.apache.axiom.om.OMElement param, final java.lang.Class type,
                                    final java.util.Map extraNamespaces) throws org.apache.axis2.AxisFault {
        try {
            if (backend.ecodex.org._1_1.SendRequestURL.class.equals(type)) {
                return backend.ecodex.org._1_1.SendRequestURL.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }
            if (backend.ecodex.org._1_1.FaultDetail.class.equals(type)) {
                return backend.ecodex.org._1_1.FaultDetail.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }
            if (org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE.class.equals(type)) {
                return org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE.Factory
                                                                                         .parse(param.getXMLStreamReaderWithoutCaching());
            }
            if (backend.ecodex.org._1_1.SendRequest.class.equals(type)) {
                return backend.ecodex.org._1_1.SendRequest.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }
            if (backend.ecodex.org._1_1.SendResponse.class.equals(type)) {
                return backend.ecodex.org._1_1.SendResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }
            if (backend.ecodex.org._1_1.FaultDetail.class.equals(type)) {
                return backend.ecodex.org._1_1.FaultDetail.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }
            if (org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE.class.equals(type)) {
                return org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE.Factory
                                                                                         .parse(param.getXMLStreamReaderWithoutCaching());
            }
            if (backend.ecodex.org._1_1.ListPendingMessagesRequest.class.equals(type)) {
                return backend.ecodex.org._1_1.ListPendingMessagesRequest.Factory
                                                                         .parse(param.getXMLStreamReaderWithoutCaching());
            }
            if (backend.ecodex.org._1_1.ListPendingMessagesResponse.class.equals(type)) {
                return backend.ecodex.org._1_1.ListPendingMessagesResponse.Factory
                                                                          .parse(param.getXMLStreamReaderWithoutCaching());
            }
            if (backend.ecodex.org._1_1.FaultDetail.class.equals(type)) {
                return backend.ecodex.org._1_1.FaultDetail.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }
            if (backend.ecodex.org._1_1.DownloadMessageRequest.class.equals(type)) {
                return backend.ecodex.org._1_1.DownloadMessageRequest.Factory
                                                                     .parse(param.getXMLStreamReaderWithoutCaching());
            }
            if (backend.ecodex.org._1_1.DownloadMessageResponse.class.equals(type)) {
                return backend.ecodex.org._1_1.DownloadMessageResponse.Factory
                                                                      .parse(param.getXMLStreamReaderWithoutCaching());
            }
            if (backend.ecodex.org._1_1.FaultDetail.class.equals(type)) {
                return backend.ecodex.org._1_1.FaultDetail.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }
            if (org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE.class.equals(type)) {
                return org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE.Factory
                                                                                         .parse(param.getXMLStreamReaderWithoutCaching());
            }
        } catch (java.lang.Exception e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
        return null;
    }

    /**
     * Gets the envelope namespaces.
     *
     * @param env the env
     * @return the envelope namespaces
     */
    private java.util.Map getEnvelopeNamespaces(final org.apache.axiom.soap.SOAPEnvelope env) {
        final java.util.Map returnMap = new java.util.HashMap();
        final java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();
        while (namespaceIterator.hasNext()) {
            final org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator.next();
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
    private org.apache.axis2.AxisFault createAxisFault(final java.lang.Exception e) {
        final org.apache.axis2.AxisFault f;
        final Throwable cause = e.getCause();
        if (cause != null) {
            f = new org.apache.axis2.AxisFault(e.getMessage(), cause);
        } else {
            f = new org.apache.axis2.AxisFault(e.getMessage());
        }
        return f;
    }
}//end of class
