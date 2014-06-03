/*
 * 
 */
package eu.domibus.backend.module._1_1;

import org.apache.axis2.AxisFault;
import eu.domibus.backend.module._1_1.exception.DownloadMessageFault;
import eu.domibus.backend.module._1_1.exception.ListPendingMessagesFault;
import eu.domibus.backend.module._1_1.exception.SendMessageFault;
import eu.domibus.backend.module._1_1.exception.SendMessageWithReferenceFault;

import java.rmi.RemoteException;

/*
 *  BackendServiceStub java implementation
 */

/**
 * The Class BackendServiceStub.
 */
public class BackendServiceStub extends org.apache.axis2.client.Stub {

    /**
     * The _operations.
     */
    protected org.apache.axis2.description.AxisOperation[] _operations;
    //hashmaps to keep the fault mapping
    /**
     * The fault exception name map.
     */
    private java.util.HashMap faultExceptionNameMap = new java.util.HashMap();

    /**
     * The fault exception class name map.
     */
    private java.util.HashMap faultExceptionClassNameMap = new java.util.HashMap();

    /**
     * The fault message map.
     */
    private java.util.HashMap faultMessageMap = new java.util.HashMap();

    /**
     * The counter.
     */
    private static int counter = 0;

    /**
     * Gets the unique suffix.
     *
     * @return the unique suffix
     */
    private static synchronized java.lang.String getUniqueSuffix() {
        // reset the counter if it is greater than 99999
        if (counter > 99999) {
            counter = 0;
        }
        counter = counter + 1;
        return java.lang.Long.toString(System.currentTimeMillis()) + "_" + counter;
    }

    /**
     * Populate axis service.
     *
     * @throws AxisFault the axis fault
     */
    private void populateAxisService() throws org.apache.axis2.AxisFault {
        //creating the Service with a unique name
        _service = new org.apache.axis2.description.AxisService("BackendService" + getUniqueSuffix());
        addAnonymousOperations();
        //creating the operations
        org.apache.axis2.description.AxisOperation __operation;
        _operations = new org.apache.axis2.description.AxisOperation[4];
        __operation = new org.apache.axis2.description.OutInAxisOperation();
        __operation
                .setName(new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/", "sendMessageWithReference"));
        _service.addOperation(__operation);
        _operations[0] = __operation;
        __operation = new org.apache.axis2.description.OutInAxisOperation();
        __operation.setName(new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/", "sendMessage"));
        _service.addOperation(__operation);
        _operations[1] = __operation;
        __operation = new org.apache.axis2.description.OutInAxisOperation();
        __operation.setName(new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/", "listPendingMessages"));
        _service.addOperation(__operation);
        _operations[2] = __operation;
        __operation = new org.apache.axis2.description.OutInAxisOperation();
        __operation.setName(new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/", "downloadMessage"));
        _service.addOperation(__operation);
        _operations[3] = __operation;
    }

    //populates the faults

    /**
     * Populate faults.
     */
    private void populateFaults() {
        faultExceptionNameMap.put(new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/", "FaultDetail"),
                                  "eu.domibus.backend.module.SendMessageWithReferenceFault");
        faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/", "FaultDetail"),
                                       "eu.domibus.backend.module.SendMessageWithReferenceFault");
        faultMessageMap.put(new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/", "FaultDetail"),
                            "backend.ecodex.org._1_1.FaultDetail");
        faultExceptionNameMap.put(new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/", "FaultDetail"),
                                  "eu.domibus.backend.module.SendMessageFault");
        faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/", "FaultDetail"),
                                       "eu.domibus.backend.module.SendMessageFault");
        faultMessageMap.put(new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/", "FaultDetail"),
                            "backend.ecodex.org._1_1.FaultDetail");
        faultExceptionNameMap.put(new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/", "FaultDetail"),
                                  "eu.domibus.backend.module.ListPendingMessagesFault");
        faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/", "FaultDetail"),
                                       "eu.domibus.backend.module.ListPendingMessagesFault");
        faultMessageMap.put(new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/", "FaultDetail"),
                            "backend.ecodex.org._1_1.FaultDetail");
        faultExceptionNameMap.put(new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/", "FaultDetail"),
                                  "eu.domibus.backend.module.DownloadMessageFault");
        faultExceptionClassNameMap.put(new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/", "FaultDetail"),
                                       "eu.domibus.backend.module.DownloadMessageFault");
        faultMessageMap.put(new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/", "FaultDetail"),
                            "backend.ecodex.org._1_1.FaultDetail");
    }

    /**
     * Instantiates a new backend service stub.
     *
     * @param configurationContext the configuration context
     * @param targetEndpoint       the target endpoint
     * @throws AxisFault the axis fault
     */
    public BackendServiceStub(final org.apache.axis2.context.ConfigurationContext configurationContext,
                              final java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault {
        this(configurationContext, targetEndpoint, false);
    }

    /**
     * Instantiates a new backend service stub.
     *
     * @param configurationContext the configuration context
     * @param targetEndpoint       the target endpoint
     * @param useSeparateListener  the use separate listener
     * @throws AxisFault the axis fault
     */
    public BackendServiceStub(final org.apache.axis2.context.ConfigurationContext configurationContext,
                              final java.lang.String targetEndpoint, final boolean useSeparateListener)
            throws org.apache.axis2.AxisFault {
        //To populate AxisService
        populateAxisService();
        populateFaults();
        _serviceClient = new org.apache.axis2.client.ServiceClient(configurationContext, _service);
        _serviceClient.getOptions().setTo(new org.apache.axis2.addressing.EndpointReference(targetEndpoint));
        _serviceClient.getOptions().setUseSeparateListener(useSeparateListener);
    }

    /**
     * Instantiates a new backend service stub.
     *
     * @param configurationContext the configuration context
     * @throws AxisFault the axis fault
     */
    public BackendServiceStub(final org.apache.axis2.context.ConfigurationContext configurationContext)
            throws org.apache.axis2.AxisFault {
        this(configurationContext, "http://www.ecodex.org/eCODEX");
    }

    /**
     * Instantiates a new backend service stub.
     *
     * @throws AxisFault the axis fault
     */
    public BackendServiceStub() throws org.apache.axis2.AxisFault {
        this("http://www.ecodex.org/eCODEX");
    }

    /**
     * Instantiates a new backend service stub.
     *
     * @param targetEndpoint the target endpoint
     * @throws AxisFault the axis fault
     */
    public BackendServiceStub(final java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault {
        this(null, targetEndpoint);
    }

    /**
     * Send message with reference.
     *
     * @param sendRequestURL66 the send request ur l66
     * @param messaging67      the messaging67
     * @throws RemoteException               the remote exception
     * @throws SendMessageWithReferenceFault the send message with reference fault
     */
    public void sendMessageWithReference(final backend.ecodex.org._1_1.SendRequestURL sendRequestURL66,
                                         final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE messaging67)
            throws java.rmi.RemoteException, eu.domibus.backend.module._1_1.exception.SendMessageWithReferenceFault {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try {
            final org.apache.axis2.client.OperationClient _operationClient =
                    _serviceClient.createClient(_operations[0].getName());
            _operationClient.getOptions().setAction("\"\"");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);
            addPropertyToOperationClient(_operationClient,
                                         org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
                                         "&");
            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();
            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;
            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), sendRequestURL66,
                             optimizeContent(new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/",
                                                                           "sendMessageWithReference")));
            env.build();
            // add the children only if the parameter is not null
            if (messaging67 != null) {
                final org.apache.axiom.om.OMElement omElementmessaging67 = toOM(messaging67, optimizeContent(
                        new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/", "sendMessageWithReference")));
                addHeader(omElementmessaging67, env);
            }
            //adding SOAP soap_headers
            _serviceClient.addHeadersToEnvelope(env);
            // set the message context with that soap envelope
            _messageContext.setEnvelope(env);
            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);
            //execute the operation client
            _operationClient.execute(true);
        } catch (org.apache.axis2.AxisFault f) {
            final org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
                    //make the fault by reflection
                    try {
                        final java.lang.String exceptionClassName =
                                (java.lang.String) faultExceptionClassNameMap.get(faultElt.getQName());
                        final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                        //message class
                        final java.lang.String messageClassName =
                                (java.lang.String) faultMessageMap.get(faultElt.getQName());
                        final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                        final java.lang.reflect.Method m =
                                exceptionClass.getMethod("setFaultMessage", new java.lang.Class[]{messageClass});
                        m.invoke(ex, new java.lang.Object[]{messageObject});
                        if (ex instanceof eu.domibus.backend.module._1_1.exception.SendMessageWithReferenceFault) {
                            throw (eu.domibus.backend.module._1_1.exception.SendMessageWithReferenceFault) ex;
                        }
                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    } catch (java.lang.ClassCastException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                } else {
                    throw f;
                }
            } else {
                throw f;
            }
        } finally {
            _messageContext.getTransportOut().getSender().cleanup(_messageContext);
        }
    }

    /**
     * Startsend message with reference.
     *
     * @param sendRequestURL66 the send request ur l66
     * @param messaging67      the messaging67
     * @param callback         the callback
     * @throws RemoteException the remote exception
     */
    public void startsendMessageWithReference(final backend.ecodex.org._1_1.SendRequestURL sendRequestURL66,
                                              final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE messaging67,
                                              final eu.domibus.backend.module._1_1.BackendServiceCallbackHandler callback)
            throws java.rmi.RemoteException {
        final org.apache.axis2.client.OperationClient _operationClient =
                _serviceClient.createClient(_operations[0].getName());
        _operationClient.getOptions().setAction("\"\"");
        _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);
        addPropertyToOperationClient(_operationClient,
                                     org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
                                     "&");
        // create SOAP envelope with that payload
        org.apache.axiom.soap.SOAPEnvelope env = null;
        final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();
        //Style is Doc.
        env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), sendRequestURL66,
                         optimizeContent(new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/",
                                                                       "sendMessageWithReference")));
        // add the soap_headers only if they are not null
        if (messaging67 != null) {
            final org.apache.axiom.om.OMElement omElementmessaging67 = toOM(messaging67, optimizeContent(
                    new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/", "sendMessageWithReference")));
            addHeader(omElementmessaging67, env);
        }
        // adding SOAP soap_headers
        _serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        _messageContext.setEnvelope(env);
        // add the message context to the operation client
        _operationClient.addMessageContext(_messageContext);
        // Nothing to pass as the callback!!!
        org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;
        if (_operations[0].getMessageReceiver() == null && _operationClient.getOptions().isUseSeparateListener()) {
            _callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
            _operations[0].setMessageReceiver(_callbackReceiver);
        }
        //execute the operation client
        _operationClient.execute(false);
    }

    /**
     * Send message.
     *
     * @param sendRequest69 the send request69
     * @param messaging70   the messaging70
     * @throws RemoteException  the remote exception
     * @throws SendMessageFault the send message fault
     */
    public void sendMessage(final backend.ecodex.org._1_1.SendRequest sendRequest69,
                            final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE messaging70)
            throws java.rmi.RemoteException, eu.domibus.backend.module._1_1.exception.SendMessageFault {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try {
            final org.apache.axis2.client.OperationClient _operationClient =
                    _serviceClient.createClient(_operations[1].getName());
            _operationClient.getOptions().setAction("\"\"");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);
            addPropertyToOperationClient(_operationClient,
                                         org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
                                         "&");
            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();
            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;
            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), sendRequest69,
                             optimizeContent(
                                     new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/", "sendMessage")));
            env.build();
            // add the children only if the parameter is not null
            if (messaging70 != null) {
                final org.apache.axiom.om.OMElement omElementmessaging70 = toOM(messaging70, optimizeContent(
                        new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/", "sendMessage")));
                addHeader(omElementmessaging70, env);
            }
            //adding SOAP soap_headers
            _serviceClient.addHeadersToEnvelope(env);
            // set the message context with that soap envelope
            _messageContext.setEnvelope(env);
            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);
            //execute the operation client
            _operationClient.execute(true);
        } catch (org.apache.axis2.AxisFault f) {
            final org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
                    //make the fault by reflection
                    try {
                        final java.lang.String exceptionClassName =
                                (java.lang.String) faultExceptionClassNameMap.get(faultElt.getQName());
                        final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                        //message class
                        final java.lang.String messageClassName =
                                (java.lang.String) faultMessageMap.get(faultElt.getQName());
                        final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                        final java.lang.reflect.Method m =
                                exceptionClass.getMethod("setFaultMessage", new java.lang.Class[]{messageClass});
                        m.invoke(ex, new java.lang.Object[]{messageObject});
                        if (ex instanceof eu.domibus.backend.module._1_1.exception.SendMessageFault) {
                            throw (eu.domibus.backend.module._1_1.exception.SendMessageFault) ex;
                        }
                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    } catch (java.lang.ClassCastException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                } else {
                    throw f;
                }
            } else {
                throw f;
            }
        } finally {
            _messageContext.getTransportOut().getSender().cleanup(_messageContext);
        }
    }

    /**
     * Startsend message.
     *
     * @param sendRequest69 the send request69
     * @param messaging70   the messaging70
     * @param callback      the callback
     * @throws RemoteException the remote exception
     */
    public void startsendMessage(final backend.ecodex.org._1_1.SendRequest sendRequest69,
                                 final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE messaging70,
                                 final eu.domibus.backend.module._1_1.BackendServiceCallbackHandler callback)
            throws java.rmi.RemoteException {
        final org.apache.axis2.client.OperationClient _operationClient =
                _serviceClient.createClient(_operations[1].getName());
        _operationClient.getOptions().setAction("\"\"");
        _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);
        addPropertyToOperationClient(_operationClient,
                                     org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
                                     "&");
        // create SOAP envelope with that payload
        org.apache.axiom.soap.SOAPEnvelope env = null;
        final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();
        //Style is Doc.
        env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), sendRequest69, optimizeContent(
                new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/", "sendMessage")));
        // add the soap_headers only if they are not null
        if (messaging70 != null) {
            final org.apache.axiom.om.OMElement omElementmessaging70 = toOM(messaging70, optimizeContent(
                    new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/", "sendMessage")));
            addHeader(omElementmessaging70, env);
        }
        // adding SOAP soap_headers
        _serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        _messageContext.setEnvelope(env);
        // add the message context to the operation client
        _operationClient.addMessageContext(_messageContext);
        // Nothing to pass as the callback!!!
        org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;
        if (_operations[1].getMessageReceiver() == null && _operationClient.getOptions().isUseSeparateListener()) {
            _callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
            _operations[1].setMessageReceiver(_callbackReceiver);
        }
        //execute the operation client
        _operationClient.execute(false);
    }

    /**
     * List pending messages.
     *
     * @param listPendingMessagesRequest72 the list pending messages request72
     * @return the backend.ecodex.org._1_1. list pending messages response
     * @throws RemoteException          the remote exception
     * @throws ListPendingMessagesFault the list pending messages fault
     */
    public backend.ecodex.org._1_1.ListPendingMessagesResponse listPendingMessages(
            final backend.ecodex.org._1_1.ListPendingMessagesRequest listPendingMessagesRequest72)
            throws java.rmi.RemoteException, eu.domibus.backend.module._1_1.exception.ListPendingMessagesFault {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try {
            final org.apache.axis2.client.OperationClient _operationClient =
                    _serviceClient.createClient(_operations[2].getName());
            _operationClient.getOptions().setAction("\"\"");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);
            addPropertyToOperationClient(_operationClient,
                                         org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
                                         "&");
            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();
            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;
            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                             listPendingMessagesRequest72, optimizeContent(
                    new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/", "listPendingMessages")));
            //adding SOAP soap_headers
            _serviceClient.addHeadersToEnvelope(env);
            // set the message context with that soap envelope
            _messageContext.setEnvelope(env);
            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);
            //execute the operation client
            _operationClient.execute(true);
            final org.apache.axis2.context.MessageContext _returnMessageContext =
                    _operationClient.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            final org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();
            final java.lang.Object object = fromOM(_returnEnv.getBody().getFirstElement(),
                                                   backend.ecodex.org._1_1.ListPendingMessagesResponse.class,
                                                   getEnvelopeNamespaces(_returnEnv));
            return (backend.ecodex.org._1_1.ListPendingMessagesResponse) object;
        } catch (org.apache.axis2.AxisFault f) {
            final org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
                    //make the fault by reflection
                    try {
                        final java.lang.String exceptionClassName =
                                (java.lang.String) faultExceptionClassNameMap.get(faultElt.getQName());
                        final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                        //message class
                        final java.lang.String messageClassName =
                                (java.lang.String) faultMessageMap.get(faultElt.getQName());
                        final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                        final java.lang.reflect.Method m =
                                exceptionClass.getMethod("setFaultMessage", new java.lang.Class[]{messageClass});
                        m.invoke(ex, new java.lang.Object[]{messageObject});
                        if (ex instanceof eu.domibus.backend.module._1_1.exception.ListPendingMessagesFault) {
                            throw (eu.domibus.backend.module._1_1.exception.ListPendingMessagesFault) ex;
                        }
                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    } catch (java.lang.ClassCastException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                } else {
                    throw f;
                }
            } else {
                throw f;
            }
        } finally {
            _messageContext.getTransportOut().getSender().cleanup(_messageContext);
        }
    }

    /**
     * Startlist pending messages.
     *
     * @param listPendingMessagesRequest72 the list pending messages request72
     * @param callback                     the callback
     * @throws RemoteException the remote exception
     */
    public void startlistPendingMessages(
            final backend.ecodex.org._1_1.ListPendingMessagesRequest listPendingMessagesRequest72,
            final eu.domibus.backend.module._1_1.BackendServiceCallbackHandler callback)
            throws java.rmi.RemoteException {
        final org.apache.axis2.client.OperationClient _operationClient =
                _serviceClient.createClient(_operations[2].getName());
        _operationClient.getOptions().setAction("\"\"");
        _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);
        addPropertyToOperationClient(_operationClient,
                                     org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
                                     "&");
        // create SOAP envelope with that payload
        org.apache.axiom.soap.SOAPEnvelope env = null;
        final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();
        //Style is Doc.
        env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), listPendingMessagesRequest72,
                         optimizeContent(new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/",
                                                                       "listPendingMessages")));
        // adding SOAP soap_headers
        _serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        _messageContext.setEnvelope(env);
        // add the message context to the operation client
        _operationClient.addMessageContext(_messageContext);
        _operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback() {
            public void onMessage(final org.apache.axis2.context.MessageContext resultContext) {
                try {
                    final org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();
                    final java.lang.Object object = fromOM(resultEnv.getBody().getFirstElement(),
                                                           backend.ecodex.org._1_1.ListPendingMessagesResponse.class,
                                                           getEnvelopeNamespaces(resultEnv));
                    callback.receiveResultlistPendingMessages(
                            (backend.ecodex.org._1_1.ListPendingMessagesResponse) object);
                } catch (org.apache.axis2.AxisFault e) {
                    callback.receiveErrorlistPendingMessages(e);
                }
            }

            public void onError(final java.lang.Exception error) {
                if (error instanceof org.apache.axis2.AxisFault) {
                    final org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
                    final org.apache.axiom.om.OMElement faultElt = f.getDetail();
                    if (faultElt != null) {
                        if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
                            //make the fault by reflection
                            try {
                                final java.lang.String exceptionClassName =
                                        (java.lang.String) faultExceptionClassNameMap.get(faultElt.getQName());
                                final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                                final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                                //message class
                                final java.lang.String messageClassName =
                                        (java.lang.String) faultMessageMap.get(faultElt.getQName());
                                final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                                final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                                final java.lang.reflect.Method m = exceptionClass
                                        .getMethod("setFaultMessage", new java.lang.Class[]{messageClass});
                                m.invoke(ex, new java.lang.Object[]{messageObject});
                                if (ex instanceof eu.domibus.backend.module._1_1.exception.ListPendingMessagesFault) {
                                    callback.receiveErrorlistPendingMessages(
                                            (eu.domibus.backend.module._1_1.exception.ListPendingMessagesFault) ex);
                                    return;
                                }
                                callback.receiveErrorlistPendingMessages(
                                        new java.rmi.RemoteException(ex.getMessage(), ex));
                            } catch (java.lang.ClassCastException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorlistPendingMessages(f);
                            } catch (java.lang.ClassNotFoundException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorlistPendingMessages(f);
                            } catch (java.lang.NoSuchMethodException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorlistPendingMessages(f);
                            } catch (java.lang.reflect.InvocationTargetException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorlistPendingMessages(f);
                            } catch (java.lang.IllegalAccessException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorlistPendingMessages(f);
                            } catch (java.lang.InstantiationException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorlistPendingMessages(f);
                            } catch (org.apache.axis2.AxisFault e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorlistPendingMessages(f);
                            }
                        } else {
                            callback.receiveErrorlistPendingMessages(f);
                        }
                    } else {
                        callback.receiveErrorlistPendingMessages(f);
                    }
                } else {
                    callback.receiveErrorlistPendingMessages(error);
                }
            }

            public void onFault(final org.apache.axis2.context.MessageContext faultContext) {
                final org.apache.axis2.AxisFault fault =
                        org.apache.axis2.util.Utils.getInboundFaultFromMessageContext(faultContext);
                onError(fault);
            }

            public void onComplete() {
                try {
                    _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                } catch (org.apache.axis2.AxisFault axisFault) {
                    callback.receiveErrorlistPendingMessages(axisFault);
                }
            }
        });
        org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;
        if (_operations[2].getMessageReceiver() == null && _operationClient.getOptions().isUseSeparateListener()) {
            _callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
            _operations[2].setMessageReceiver(_callbackReceiver);
        }
        //execute the operation client
        _operationClient.execute(false);
    }

    /**
     * Download message.
     *
     * @param downloadMessageRequest74 the download message request74
     * @return the backend.ecodex.org._1_1. download message response
     * @throws RemoteException      the remote exception
     * @throws DownloadMessageFault the download message fault
     */
    public backend.ecodex.org._1_1.DownloadMessageResponse downloadMessage(
            final backend.ecodex.org._1_1.DownloadMessageRequest downloadMessageRequest74)
            throws java.rmi.RemoteException, eu.domibus.backend.module._1_1.exception.DownloadMessageFault {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try {
            final org.apache.axis2.client.OperationClient _operationClient =
                    _serviceClient.createClient(_operations[3].getName());
            _operationClient.getOptions().setAction("\"\"");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);
            addPropertyToOperationClient(_operationClient,
                                         org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
                                         "&");
            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();
            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;
            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), downloadMessageRequest74,
                             optimizeContent(new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/",
                                                                           "downloadMessage")));
            //adding SOAP soap_headers
            _serviceClient.addHeadersToEnvelope(env);
            // set the message context with that soap envelope
            _messageContext.setEnvelope(env);
            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);
            //execute the operation client
            _operationClient.execute(true);
            final org.apache.axis2.context.MessageContext _returnMessageContext =
                    _operationClient.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            final org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();
            final java.lang.Object object = fromOM(_returnEnv.getBody().getFirstElement(),
                                                   backend.ecodex.org._1_1.DownloadMessageResponse.class,
                                                   getEnvelopeNamespaces(_returnEnv));
            return (backend.ecodex.org._1_1.DownloadMessageResponse) object;
        } catch (org.apache.axis2.AxisFault f) {
            final org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
                    //make the fault by reflection
                    try {
                        final java.lang.String exceptionClassName =
                                (java.lang.String) faultExceptionClassNameMap.get(faultElt.getQName());
                        final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                        //message class
                        final java.lang.String messageClassName =
                                (java.lang.String) faultMessageMap.get(faultElt.getQName());
                        final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                        final java.lang.reflect.Method m =
                                exceptionClass.getMethod("setFaultMessage", new java.lang.Class[]{messageClass});
                        m.invoke(ex, new java.lang.Object[]{messageObject});
                        if (ex instanceof eu.domibus.backend.module._1_1.exception.DownloadMessageFault) {
                            throw (eu.domibus.backend.module._1_1.exception.DownloadMessageFault) ex;
                        }
                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    } catch (java.lang.ClassCastException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                } else {
                    throw f;
                }
            } else {
                throw f;
            }
        } finally {
            _messageContext.getTransportOut().getSender().cleanup(_messageContext);
        }
    }

    /**
     * Startdownload message.
     *
     * @param downloadMessageRequest74 the download message request74
     * @param callback                 the callback
     * @throws RemoteException the remote exception
     */
    public void startdownloadMessage(final backend.ecodex.org._1_1.DownloadMessageRequest downloadMessageRequest74,
                                     final eu.domibus.backend.module._1_1.BackendServiceCallbackHandler callback)
            throws java.rmi.RemoteException {
        final org.apache.axis2.client.OperationClient _operationClient =
                _serviceClient.createClient(_operations[3].getName());
        _operationClient.getOptions().setAction("\"\"");
        _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);
        addPropertyToOperationClient(_operationClient,
                                     org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR,
                                     "&");
        // create SOAP envelope with that payload
        org.apache.axiom.soap.SOAPEnvelope env = null;
        final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();
        //Style is Doc.
        env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), downloadMessageRequest74,
                         optimizeContent(
                                 new javax.xml.namespace.QName("http://org.ecodex.backend/1_1/", "downloadMessage")));
        // adding SOAP soap_headers
        _serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        _messageContext.setEnvelope(env);
        // add the message context to the operation client
        _operationClient.addMessageContext(_messageContext);
        _operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback() {
            public void onMessage(final org.apache.axis2.context.MessageContext resultContext) {
                try {
                    final org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();
                    final java.lang.Object object = fromOM(resultEnv.getBody().getFirstElement(),
                                                           backend.ecodex.org._1_1.DownloadMessageResponse.class,
                                                           getEnvelopeNamespaces(resultEnv));
                    callback.receiveResultdownloadMessage((backend.ecodex.org._1_1.DownloadMessageResponse) object);
                } catch (org.apache.axis2.AxisFault e) {
                    callback.receiveErrordownloadMessage(e);
                }
            }

            public void onError(final java.lang.Exception error) {
                if (error instanceof org.apache.axis2.AxisFault) {
                    final org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
                    final org.apache.axiom.om.OMElement faultElt = f.getDetail();
                    if (faultElt != null) {
                        if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
                            //make the fault by reflection
                            try {
                                final java.lang.String exceptionClassName =
                                        (java.lang.String) faultExceptionClassNameMap.get(faultElt.getQName());
                                final java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                                final java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                                //message class
                                final java.lang.String messageClassName =
                                        (java.lang.String) faultMessageMap.get(faultElt.getQName());
                                final java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                                final java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                                final java.lang.reflect.Method m = exceptionClass
                                        .getMethod("setFaultMessage", new java.lang.Class[]{messageClass});
                                m.invoke(ex, new java.lang.Object[]{messageObject});
                                if (ex instanceof eu.domibus.backend.module._1_1.exception.DownloadMessageFault) {
                                    callback.receiveErrordownloadMessage(
                                            (eu.domibus.backend.module._1_1.exception.DownloadMessageFault) ex);
                                    return;
                                }
                                callback.receiveErrordownloadMessage(new java.rmi.RemoteException(ex.getMessage(), ex));
                            } catch (java.lang.ClassCastException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrordownloadMessage(f);
                            } catch (java.lang.ClassNotFoundException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrordownloadMessage(f);
                            } catch (java.lang.NoSuchMethodException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrordownloadMessage(f);
                            } catch (java.lang.reflect.InvocationTargetException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrordownloadMessage(f);
                            } catch (java.lang.IllegalAccessException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrordownloadMessage(f);
                            } catch (java.lang.InstantiationException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrordownloadMessage(f);
                            } catch (org.apache.axis2.AxisFault e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrordownloadMessage(f);
                            }
                        } else {
                            callback.receiveErrordownloadMessage(f);
                        }
                    } else {
                        callback.receiveErrordownloadMessage(f);
                    }
                } else {
                    callback.receiveErrordownloadMessage(error);
                }
            }

            public void onFault(final org.apache.axis2.context.MessageContext faultContext) {
                final org.apache.axis2.AxisFault fault =
                        org.apache.axis2.util.Utils.getInboundFaultFromMessageContext(faultContext);
                onError(fault);
            }

            public void onComplete() {
                try {
                    _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                } catch (org.apache.axis2.AxisFault axisFault) {
                    callback.receiveErrordownloadMessage(axisFault);
                }
            }
        });
        org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;
        if (_operations[3].getMessageReceiver() == null && _operationClient.getOptions().isUseSeparateListener()) {
            _callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
            _operations[3].setMessageReceiver(_callbackReceiver);
        }
        //execute the operation client
        _operationClient.execute(false);
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
     * The op name array.
     */
    private javax.xml.namespace.QName[] opNameArray = null;

    /**
     * Optimize content.
     *
     * @param opName the op name
     * @return true, if successful
     */
    private boolean optimizeContent(final javax.xml.namespace.QName opName) {
        if (opNameArray == null) {
            return false;
        }
        for (int i = 0; i < opNameArray.length; i++) {
            if (opName.equals(opNameArray[i])) {
                return true;
            }
        }
        return false;
    }

    //http://www.ecodex.org/eCODEX

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
                                                          final backend.ecodex.org._1_1.SendRequestURL param,
                                                          final boolean optimizeContent)
            throws org.apache.axis2.AxisFault {
        try {
            final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody()
                         .addChild(param.getOMElement(backend.ecodex.org._1_1.SendRequestURL.MY_QNAME, factory));
            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

	/* methods to provide back word compatibility */

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
                                                          final backend.ecodex.org._1_1.SendRequest param,
                                                          final boolean optimizeContent)
            throws org.apache.axis2.AxisFault {
        try {
            final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(param.getOMElement(backend.ecodex.org._1_1.SendRequest.MY_QNAME, factory));
            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

	/* methods to provide back word compatibility */

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
                                                          final backend.ecodex.org._1_1.ListPendingMessagesRequest param,
                                                          final boolean optimizeContent)
            throws org.apache.axis2.AxisFault {
        try {
            final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(
                    param.getOMElement(backend.ecodex.org._1_1.ListPendingMessagesRequest.MY_QNAME, factory));
            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

	/* methods to provide back word compatibility */

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
                                                          final backend.ecodex.org._1_1.DownloadMessageRequest param,
                                                          final boolean optimizeContent)
            throws org.apache.axis2.AxisFault {
        try {
            final org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(
                    param.getOMElement(backend.ecodex.org._1_1.DownloadMessageRequest.MY_QNAME, factory));
            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

	/* methods to provide back word compatibility */

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
}
