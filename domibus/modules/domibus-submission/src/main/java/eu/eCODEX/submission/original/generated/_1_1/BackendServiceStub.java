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
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.client.Stub;
import org.apache.axis2.client.async.AxisCallback;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.databinding.ADBException;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.OutInAxisOperation;
import org.apache.axis2.description.WSDL2Constants;
import org.apache.axis2.util.CallbackReceiver;
import org.apache.axis2.util.Utils;
import org.apache.axis2.wsdl.WSDLConstants;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE;

import javax.xml.namespace.QName;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*
 *  BackendServiceStub java implementation
 */

/**
 * The Class BackendServiceStub.
 */
public class BackendServiceStub extends Stub {

    /**
     * The _operations.
     */
    protected AxisOperation[] _operations;
    //hashmaps to keep the fault mapping
    /**
     * The fault exception name map.
     */
    private final HashMap faultExceptionNameMap = new HashMap();

    /**
     * The fault exception class name map.
     */
    private final HashMap faultExceptionClassNameMap = new HashMap();

    /**
     * The fault message map.
     */
    private final HashMap faultMessageMap = new HashMap();

    /**
     * The counter.
     */
    private static int counter = 0;

    /**
     * Gets the unique suffix.
     *
     * @return the unique suffix
     */
    private static synchronized String getUniqueSuffix() {
        // reset the counter if it is greater than 99999
        if (counter > 99999) {
            counter = 0;
        }
        counter = counter + 1;
        return Long.toString(System.currentTimeMillis()) + "_" + counter;
    }

    /**
     * Populate axis service.
     *
     * @throws AxisFault the axis fault
     */
    private void populateAxisService() throws AxisFault {
        //creating the Service with a unique name
        this._service = new AxisService("BackendService" + getUniqueSuffix());
        addAnonymousOperations();
        //creating the operations
        AxisOperation __operation;
        this._operations = new AxisOperation[4];
        __operation = new OutInAxisOperation();
        __operation.setName(new QName("http://org.ecodex.backend/1_1/", "sendMessageWithReference"));
        this._service.addOperation(__operation);
        this._operations[0] = __operation;
        __operation = new OutInAxisOperation();
        __operation.setName(new QName("http://org.ecodex.backend/1_1/", "sendMessage"));
        this._service.addOperation(__operation);
        this._operations[1] = __operation;
        __operation = new OutInAxisOperation();
        __operation.setName(new QName("http://org.ecodex.backend/1_1/", "listPendingMessages"));
        this._service.addOperation(__operation);
        this._operations[2] = __operation;
        __operation = new OutInAxisOperation();
        __operation.setName(new QName("http://org.ecodex.backend/1_1/", "downloadMessage"));
        this._service.addOperation(__operation);
        this._operations[3] = __operation;
    }

    //populates the faults

    /**
     * Populate faults.
     */
    private void populateFaults() {
        this.faultExceptionNameMap.put(new QName("http://org.ecodex.backend/1_1/", "FaultDetail"),
                                       "eu.domibus.backend.module.SendMessageWithReferenceFault");
        this.faultExceptionClassNameMap.put(new QName("http://org.ecodex.backend/1_1/", "FaultDetail"),
                                            "eu.domibus.backend.module.SendMessageWithReferenceFault");
        this.faultMessageMap
                .put(new QName("http://org.ecodex.backend/1_1/", "FaultDetail"), "backend.ecodex.org._1_1.FaultDetail");
        this.faultExceptionNameMap.put(new QName("http://org.ecodex.backend/1_1/", "FaultDetail"),
                                       "eu.domibus.backend.module.SendMessageFault");
        this.faultExceptionClassNameMap.put(new QName("http://org.ecodex.backend/1_1/", "FaultDetail"),
                                            "eu.domibus.backend.module.SendMessageFault");
        this.faultMessageMap
                .put(new QName("http://org.ecodex.backend/1_1/", "FaultDetail"), "backend.ecodex.org._1_1.FaultDetail");
        this.faultExceptionNameMap.put(new QName("http://org.ecodex.backend/1_1/", "FaultDetail"),
                                       "eu.domibus.backend.module.ListPendingMessagesFault");
        this.faultExceptionClassNameMap.put(new QName("http://org.ecodex.backend/1_1/", "FaultDetail"),
                                            "eu.domibus.backend.module.ListPendingMessagesFault");
        this.faultMessageMap
                .put(new QName("http://org.ecodex.backend/1_1/", "FaultDetail"), "backend.ecodex.org._1_1.FaultDetail");
        this.faultExceptionNameMap.put(new QName("http://org.ecodex.backend/1_1/", "FaultDetail"),
                                       "eu.domibus.backend.module.DownloadMessageFault");
        this.faultExceptionClassNameMap.put(new QName("http://org.ecodex.backend/1_1/", "FaultDetail"),
                                            "eu.domibus.backend.module.DownloadMessageFault");
        this.faultMessageMap
                .put(new QName("http://org.ecodex.backend/1_1/", "FaultDetail"), "backend.ecodex.org._1_1.FaultDetail");
    }

    /**
     * Instantiates a new backend service stub.
     *
     * @param configurationContext the configuration context
     * @param targetEndpoint       the target endpoint
     * @throws AxisFault the axis fault
     */
    public BackendServiceStub(final ConfigurationContext configurationContext, final String targetEndpoint)
            throws AxisFault {
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
    public BackendServiceStub(final ConfigurationContext configurationContext, final String targetEndpoint,
                              final boolean useSeparateListener) throws AxisFault {
        //To populate AxisService
        populateAxisService();
        populateFaults();
        this._serviceClient = new ServiceClient(configurationContext, this._service);
        this._serviceClient.getOptions().setTo(new EndpointReference(targetEndpoint));
        this._serviceClient.getOptions().setUseSeparateListener(useSeparateListener);
    }

    /**
     * Instantiates a new backend service stub.
     *
     * @param configurationContext the configuration context
     * @throws AxisFault the axis fault
     */
    public BackendServiceStub(final ConfigurationContext configurationContext) throws AxisFault {
        this(configurationContext, "http://www.ecodex.org/eCODEX");
    }

    /**
     * Instantiates a new backend service stub.
     *
     * @throws AxisFault the axis fault
     */
    public BackendServiceStub() throws AxisFault {
        this("http://www.ecodex.org/eCODEX");
    }

    /**
     * Instantiates a new backend service stub.
     *
     * @param targetEndpoint the target endpoint
     * @throws AxisFault the axis fault
     */
    public BackendServiceStub(final String targetEndpoint) throws AxisFault {
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
    public void sendMessageWithReference(final SendRequestURL sendRequestURL66, final MessagingE messaging67)
            throws RemoteException, SendMessageWithReferenceFault {
        MessageContext _messageContext = null;
        try {
            final OperationClient _operationClient = this._serviceClient.createClient(this._operations[0].getName());
            _operationClient.getOptions().setAction("\"\"");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);
            addPropertyToOperationClient(_operationClient, WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");
            // create a message context
            _messageContext = new MessageContext();
            // create SOAP envelope with that payload
            SOAPEnvelope env = null;
            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), sendRequestURL66,
                             optimizeContent(new QName("http://org.ecodex.backend/1_1/", "sendMessageWithReference")));
            env.build();
            // add the children only if the parameter is not null
            if (messaging67 != null) {
                final OMElement omElementmessaging67 = toOM(messaging67, optimizeContent(
                        new QName("http://org.ecodex.backend/1_1/", "sendMessageWithReference")));
                addHeader(omElementmessaging67, env);
            }
            //adding SOAP soap_headers
            this._serviceClient.addHeadersToEnvelope(env);
            // set the message context with that soap envelope
            _messageContext.setEnvelope(env);
            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);
            //execute the operation client
            _operationClient.execute(true);
        } catch (AxisFault f) {
            final OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (this.faultExceptionNameMap.containsKey(faultElt.getQName())) {
                    //make the fault by reflection
                    try {
                        final String exceptionClassName =
                                (String) this.faultExceptionClassNameMap.get(faultElt.getQName());
                        final Class exceptionClass = Class.forName(exceptionClassName);
                        final Exception ex = (Exception) exceptionClass.newInstance();
                        //message class
                        final String messageClassName = (String) this.faultMessageMap.get(faultElt.getQName());
                        final Class messageClass = Class.forName(messageClassName);
                        final Object messageObject = fromOM(faultElt, messageClass, null);
                        final Method m = exceptionClass.getMethod("setFaultMessage", new Class[]{messageClass});
                        m.invoke(ex, new Object[]{messageObject});
                        if (ex instanceof SendMessageWithReferenceFault) {
                            throw (SendMessageWithReferenceFault) ex;
                        }
                        throw new RemoteException(ex.getMessage(), ex);
                    } catch (ClassCastException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (InstantiationException e) {
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
    public void startsendMessageWithReference(final SendRequestURL sendRequestURL66, final MessagingE messaging67,
                                              final BackendServiceCallbackHandler callback) throws RemoteException {
        final OperationClient _operationClient = this._serviceClient.createClient(this._operations[0].getName());
        _operationClient.getOptions().setAction("\"\"");
        _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);
        addPropertyToOperationClient(_operationClient, WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");
        // create SOAP envelope with that payload
        SOAPEnvelope env = null;
        final MessageContext _messageContext = new MessageContext();
        //Style is Doc.
        env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), sendRequestURL66,
                         optimizeContent(new QName("http://org.ecodex.backend/1_1/", "sendMessageWithReference")));
        // add the soap_headers only if they are not null
        if (messaging67 != null) {
            final OMElement omElementmessaging67 = toOM(messaging67, optimizeContent(
                    new QName("http://org.ecodex.backend/1_1/", "sendMessageWithReference")));
            addHeader(omElementmessaging67, env);
        }
        // adding SOAP soap_headers
        this._serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        _messageContext.setEnvelope(env);
        // add the message context to the operation client
        _operationClient.addMessageContext(_messageContext);
        // Nothing to pass as the callback!!!
        CallbackReceiver _callbackReceiver = null;
        if (this._operations[0].getMessageReceiver() == null && _operationClient.getOptions().isUseSeparateListener()) {
            _callbackReceiver = new CallbackReceiver();
            this._operations[0].setMessageReceiver(_callbackReceiver);
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
    public void sendMessage(final SendRequest sendRequest69, final MessagingE messaging70)
            throws RemoteException, SendMessageFault {
        MessageContext _messageContext = null;
        try {
            final OperationClient _operationClient = this._serviceClient.createClient(this._operations[1].getName());
            _operationClient.getOptions().setAction("\"\"");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);
            addPropertyToOperationClient(_operationClient, WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");
            // create a message context
            _messageContext = new MessageContext();
            // create SOAP envelope with that payload
            SOAPEnvelope env = null;
            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), sendRequest69,
                             optimizeContent(new QName("http://org.ecodex.backend/1_1/", "sendMessage")));
            env.build();
            // add the children only if the parameter is not null
            if (messaging70 != null) {
                final OMElement omElementmessaging70 =
                        toOM(messaging70, optimizeContent(new QName("http://org.ecodex.backend/1_1/", "sendMessage")));
                addHeader(omElementmessaging70, env);
            }
            //adding SOAP soap_headers
            this._serviceClient.addHeadersToEnvelope(env);
            // set the message context with that soap envelope
            _messageContext.setEnvelope(env);
            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);
            //execute the operation client
            _operationClient.execute(true);
        } catch (AxisFault f) {
            final OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (this.faultExceptionNameMap.containsKey(faultElt.getQName())) {
                    //make the fault by reflection
                    try {
                        final String exceptionClassName =
                                (String) this.faultExceptionClassNameMap.get(faultElt.getQName());
                        final Class exceptionClass = Class.forName(exceptionClassName);
                        final Exception ex = (Exception) exceptionClass.newInstance();
                        //message class
                        final String messageClassName = (String) this.faultMessageMap.get(faultElt.getQName());
                        final Class messageClass = Class.forName(messageClassName);
                        final Object messageObject = fromOM(faultElt, messageClass, null);
                        final Method m = exceptionClass.getMethod("setFaultMessage", new Class[]{messageClass});
                        m.invoke(ex, new Object[]{messageObject});
                        if (ex instanceof SendMessageFault) {
                            throw (SendMessageFault) ex;
                        }
                        throw new RemoteException(ex.getMessage(), ex);
                    } catch (ClassCastException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (InstantiationException e) {
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
    public void startsendMessage(final SendRequest sendRequest69, final MessagingE messaging70,
                                 final BackendServiceCallbackHandler callback) throws RemoteException {
        final OperationClient _operationClient = this._serviceClient.createClient(this._operations[1].getName());
        _operationClient.getOptions().setAction("\"\"");
        _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);
        addPropertyToOperationClient(_operationClient, WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");
        // create SOAP envelope with that payload
        SOAPEnvelope env = null;
        final MessageContext _messageContext = new MessageContext();
        //Style is Doc.
        env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), sendRequest69,
                         optimizeContent(new QName("http://org.ecodex.backend/1_1/", "sendMessage")));
        // add the soap_headers only if they are not null
        if (messaging70 != null) {
            final OMElement omElementmessaging70 =
                    toOM(messaging70, optimizeContent(new QName("http://org.ecodex.backend/1_1/", "sendMessage")));
            addHeader(omElementmessaging70, env);
        }
        // adding SOAP soap_headers
        this._serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        _messageContext.setEnvelope(env);
        // add the message context to the operation client
        _operationClient.addMessageContext(_messageContext);
        // Nothing to pass as the callback!!!
        CallbackReceiver _callbackReceiver = null;
        if (this._operations[1].getMessageReceiver() == null && _operationClient.getOptions().isUseSeparateListener()) {
            _callbackReceiver = new CallbackReceiver();
            this._operations[1].setMessageReceiver(_callbackReceiver);
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
    public ListPendingMessagesResponse listPendingMessages(
            final ListPendingMessagesRequest listPendingMessagesRequest72)
            throws RemoteException, ListPendingMessagesFault {
        MessageContext _messageContext = null;
        try {
            final OperationClient _operationClient = this._serviceClient.createClient(this._operations[2].getName());
            _operationClient.getOptions().setAction("\"\"");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);
            addPropertyToOperationClient(_operationClient, WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");
            // create a message context
            _messageContext = new MessageContext();
            // create SOAP envelope with that payload
            SOAPEnvelope env = null;
            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
                             listPendingMessagesRequest72,
                             optimizeContent(new QName("http://org.ecodex.backend/1_1/", "listPendingMessages")));
            //adding SOAP soap_headers
            this._serviceClient.addHeadersToEnvelope(env);
            // set the message context with that soap envelope
            _messageContext.setEnvelope(env);
            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);
            //execute the operation client
            _operationClient.execute(true);
            final MessageContext _returnMessageContext =
                    _operationClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            final SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();
            final Object object = fromOM(_returnEnv.getBody().getFirstElement(), ListPendingMessagesResponse.class,
                                         getEnvelopeNamespaces(_returnEnv));
            return (ListPendingMessagesResponse) object;
        } catch (AxisFault f) {
            final OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (this.faultExceptionNameMap.containsKey(faultElt.getQName())) {
                    //make the fault by reflection
                    try {
                        final String exceptionClassName =
                                (String) this.faultExceptionClassNameMap.get(faultElt.getQName());
                        final Class exceptionClass = Class.forName(exceptionClassName);
                        final Exception ex = (Exception) exceptionClass.newInstance();
                        //message class
                        final String messageClassName = (String) this.faultMessageMap.get(faultElt.getQName());
                        final Class messageClass = Class.forName(messageClassName);
                        final Object messageObject = fromOM(faultElt, messageClass, null);
                        final Method m = exceptionClass.getMethod("setFaultMessage", new Class[]{messageClass});
                        m.invoke(ex, new Object[]{messageObject});
                        if (ex instanceof ListPendingMessagesFault) {
                            throw (ListPendingMessagesFault) ex;
                        }
                        throw new RemoteException(ex.getMessage(), ex);
                    } catch (ClassCastException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (InstantiationException e) {
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
    public void startlistPendingMessages(final ListPendingMessagesRequest listPendingMessagesRequest72,
                                         final BackendServiceCallbackHandler callback) throws RemoteException {
        final OperationClient _operationClient = this._serviceClient.createClient(this._operations[2].getName());
        _operationClient.getOptions().setAction("\"\"");
        _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);
        addPropertyToOperationClient(_operationClient, WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");
        // create SOAP envelope with that payload
        SOAPEnvelope env = null;
        final MessageContext _messageContext = new MessageContext();
        //Style is Doc.
        env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), listPendingMessagesRequest72,
                         optimizeContent(new QName("http://org.ecodex.backend/1_1/", "listPendingMessages")));
        // adding SOAP soap_headers
        this._serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        _messageContext.setEnvelope(env);
        // add the message context to the operation client
        _operationClient.addMessageContext(_messageContext);
        _operationClient.setCallback(new AxisCallback() {
            public void onMessage(final MessageContext resultContext) {
                try {
                    final SOAPEnvelope resultEnv = resultContext.getEnvelope();
                    final Object object =
                            fromOM(resultEnv.getBody().getFirstElement(), ListPendingMessagesResponse.class,
                                   getEnvelopeNamespaces(resultEnv));
                    callback.receiveResultlistPendingMessages((ListPendingMessagesResponse) object);
                } catch (AxisFault e) {
                    callback.receiveErrorlistPendingMessages(e);
                }
            }

            public void onError(final Exception error) {
                if (error instanceof AxisFault) {
                    final AxisFault f = (AxisFault) error;
                    final OMElement faultElt = f.getDetail();
                    if (faultElt != null) {
                        if (BackendServiceStub.this.faultExceptionNameMap.containsKey(faultElt.getQName())) {
                            //make the fault by reflection
                            try {
                                final String exceptionClassName =
                                        (String) BackendServiceStub.this.faultExceptionClassNameMap
                                                .get(faultElt.getQName());
                                final Class exceptionClass = Class.forName(exceptionClassName);
                                final Exception ex = (Exception) exceptionClass.newInstance();
                                //message class
                                final String messageClassName =
                                        (String) BackendServiceStub.this.faultMessageMap.get(faultElt.getQName());
                                final Class messageClass = Class.forName(messageClassName);
                                final Object messageObject = fromOM(faultElt, messageClass, null);
                                final Method m = exceptionClass.getMethod("setFaultMessage", new Class[]{messageClass});
                                m.invoke(ex, new Object[]{messageObject});
                                if (ex instanceof ListPendingMessagesFault) {
                                    callback.receiveErrorlistPendingMessages((ListPendingMessagesFault) ex);
                                    return;
                                }
                                callback.receiveErrorlistPendingMessages(new RemoteException(ex.getMessage(), ex));
                            } catch (ClassCastException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorlistPendingMessages(f);
                            } catch (ClassNotFoundException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorlistPendingMessages(f);
                            } catch (NoSuchMethodException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorlistPendingMessages(f);
                            } catch (InvocationTargetException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorlistPendingMessages(f);
                            } catch (IllegalAccessException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorlistPendingMessages(f);
                            } catch (InstantiationException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrorlistPendingMessages(f);
                            } catch (AxisFault e) {
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

            public void onFault(final MessageContext faultContext) {
                final AxisFault fault = Utils.getInboundFaultFromMessageContext(faultContext);
                onError(fault);
            }

            public void onComplete() {
                try {
                    _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                } catch (AxisFault axisFault) {
                    callback.receiveErrorlistPendingMessages(axisFault);
                }
            }
        });
        CallbackReceiver _callbackReceiver = null;
        if (this._operations[2].getMessageReceiver() == null && _operationClient.getOptions().isUseSeparateListener()) {
            _callbackReceiver = new CallbackReceiver();
            this._operations[2].setMessageReceiver(_callbackReceiver);
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
    public DownloadMessageResponse downloadMessage(final DownloadMessageRequest downloadMessageRequest74)
            throws RemoteException, DownloadMessageFault {
        MessageContext _messageContext = null;
        try {
            final OperationClient _operationClient = this._serviceClient.createClient(this._operations[3].getName());
            _operationClient.getOptions().setAction("\"\"");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);
            addPropertyToOperationClient(_operationClient, WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");
            // create a message context
            _messageContext = new MessageContext();
            // create SOAP envelope with that payload
            SOAPEnvelope env = null;
            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), downloadMessageRequest74,
                             optimizeContent(new QName("http://org.ecodex.backend/1_1/", "downloadMessage")));
            //adding SOAP soap_headers
            this._serviceClient.addHeadersToEnvelope(env);
            // set the message context with that soap envelope
            _messageContext.setEnvelope(env);
            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);
            //execute the operation client
            _operationClient.execute(true);
            final MessageContext _returnMessageContext =
                    _operationClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            final SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();
            final Object object = fromOM(_returnEnv.getBody().getFirstElement(), DownloadMessageResponse.class,
                                         getEnvelopeNamespaces(_returnEnv));
            return (DownloadMessageResponse) object;
        } catch (AxisFault f) {
            final OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (this.faultExceptionNameMap.containsKey(faultElt.getQName())) {
                    //make the fault by reflection
                    try {
                        final String exceptionClassName =
                                (String) this.faultExceptionClassNameMap.get(faultElt.getQName());
                        final Class exceptionClass = Class.forName(exceptionClassName);
                        final Exception ex = (Exception) exceptionClass.newInstance();
                        //message class
                        final String messageClassName = (String) this.faultMessageMap.get(faultElt.getQName());
                        final Class messageClass = Class.forName(messageClassName);
                        final Object messageObject = fromOM(faultElt, messageClass, null);
                        final Method m = exceptionClass.getMethod("setFaultMessage", new Class[]{messageClass});
                        m.invoke(ex, new Object[]{messageObject});
                        if (ex instanceof DownloadMessageFault) {
                            throw (DownloadMessageFault) ex;
                        }
                        throw new RemoteException(ex.getMessage(), ex);
                    } catch (ClassCastException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (InstantiationException e) {
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
    public void startdownloadMessage(final DownloadMessageRequest downloadMessageRequest74,
                                     final BackendServiceCallbackHandler callback) throws RemoteException {
        final OperationClient _operationClient = this._serviceClient.createClient(this._operations[3].getName());
        _operationClient.getOptions().setAction("\"\"");
        _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);
        addPropertyToOperationClient(_operationClient, WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");
        // create SOAP envelope with that payload
        SOAPEnvelope env = null;
        final MessageContext _messageContext = new MessageContext();
        //Style is Doc.
        env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), downloadMessageRequest74,
                         optimizeContent(new QName("http://org.ecodex.backend/1_1/", "downloadMessage")));
        // adding SOAP soap_headers
        this._serviceClient.addHeadersToEnvelope(env);
        // create message context with that soap envelope
        _messageContext.setEnvelope(env);
        // add the message context to the operation client
        _operationClient.addMessageContext(_messageContext);
        _operationClient.setCallback(new AxisCallback() {
            public void onMessage(final MessageContext resultContext) {
                try {
                    final SOAPEnvelope resultEnv = resultContext.getEnvelope();
                    final Object object = fromOM(resultEnv.getBody().getFirstElement(), DownloadMessageResponse.class,
                                                 getEnvelopeNamespaces(resultEnv));
                    callback.receiveResultdownloadMessage((DownloadMessageResponse) object);
                } catch (AxisFault e) {
                    callback.receiveErrordownloadMessage(e);
                }
            }

            public void onError(final Exception error) {
                if (error instanceof AxisFault) {
                    final AxisFault f = (AxisFault) error;
                    final OMElement faultElt = f.getDetail();
                    if (faultElt != null) {
                        if (BackendServiceStub.this.faultExceptionNameMap.containsKey(faultElt.getQName())) {
                            //make the fault by reflection
                            try {
                                final String exceptionClassName =
                                        (String) BackendServiceStub.this.faultExceptionClassNameMap
                                                .get(faultElt.getQName());
                                final Class exceptionClass = Class.forName(exceptionClassName);
                                final Exception ex = (Exception) exceptionClass.newInstance();
                                //message class
                                final String messageClassName =
                                        (String) BackendServiceStub.this.faultMessageMap.get(faultElt.getQName());
                                final Class messageClass = Class.forName(messageClassName);
                                final Object messageObject = fromOM(faultElt, messageClass, null);
                                final Method m = exceptionClass.getMethod("setFaultMessage", new Class[]{messageClass});
                                m.invoke(ex, new Object[]{messageObject});
                                if (ex instanceof DownloadMessageFault) {
                                    callback.receiveErrordownloadMessage((DownloadMessageFault) ex);
                                    return;
                                }
                                callback.receiveErrordownloadMessage(new RemoteException(ex.getMessage(), ex));
                            } catch (ClassCastException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrordownloadMessage(f);
                            } catch (ClassNotFoundException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrordownloadMessage(f);
                            } catch (NoSuchMethodException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrordownloadMessage(f);
                            } catch (InvocationTargetException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrordownloadMessage(f);
                            } catch (IllegalAccessException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrordownloadMessage(f);
                            } catch (InstantiationException e) {
                                // we cannot intantiate the class - throw the original Axis fault
                                callback.receiveErrordownloadMessage(f);
                            } catch (AxisFault e) {
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

            public void onFault(final MessageContext faultContext) {
                final AxisFault fault = Utils.getInboundFaultFromMessageContext(faultContext);
                onError(fault);
            }

            public void onComplete() {
                try {
                    _messageContext.getTransportOut().getSender().cleanup(_messageContext);
                } catch (AxisFault axisFault) {
                    callback.receiveErrordownloadMessage(axisFault);
                }
            }
        });
        CallbackReceiver _callbackReceiver = null;
        if (this._operations[3].getMessageReceiver() == null && _operationClient.getOptions().isUseSeparateListener()) {
            _callbackReceiver = new CallbackReceiver();
            this._operations[3].setMessageReceiver(_callbackReceiver);
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
     * The op name array.
     */
    private final QName[] opNameArray = null;

    /**
     * Optimize content.
     *
     * @param opName the op name
     * @return true, if successful
     */
    private boolean optimizeContent(final QName opName) {
        if (this.opNameArray == null) {
            return false;
        }
        for (int i = 0; i < this.opNameArray.length; i++) {
            if (opName.equals(this.opNameArray[i])) {
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
    private SOAPEnvelope toEnvelope(final SOAPFactory factory, final SendRequestURL param,
                                    final boolean optimizeContent) throws AxisFault {
        try {
            final SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(param.getOMElement(SendRequestURL.MY_QNAME, factory));
            return emptyEnvelope;
        } catch (ADBException e) {
            throw AxisFault.makeFault(e);
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
    private SOAPEnvelope toEnvelope(final SOAPFactory factory, final SendRequest param, final boolean optimizeContent)
            throws AxisFault {
        try {
            final SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(param.getOMElement(SendRequest.MY_QNAME, factory));
            return emptyEnvelope;
        } catch (ADBException e) {
            throw AxisFault.makeFault(e);
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
    private SOAPEnvelope toEnvelope(final SOAPFactory factory, final ListPendingMessagesRequest param,
                                    final boolean optimizeContent) throws AxisFault {
        try {
            final SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(param.getOMElement(ListPendingMessagesRequest.MY_QNAME, factory));
            return emptyEnvelope;
        } catch (ADBException e) {
            throw AxisFault.makeFault(e);
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
    private SOAPEnvelope toEnvelope(final SOAPFactory factory, final DownloadMessageRequest param,
                                    final boolean optimizeContent) throws AxisFault {
        try {
            final SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(param.getOMElement(DownloadMessageRequest.MY_QNAME, factory));
            return emptyEnvelope;
        } catch (ADBException e) {
            throw AxisFault.makeFault(e);
        }
    }

	/* methods to provide back word compatibility */

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
}
