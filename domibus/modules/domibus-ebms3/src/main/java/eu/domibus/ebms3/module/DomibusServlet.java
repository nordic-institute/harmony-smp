package eu.domibus.ebms3.module;

import eu.domibus.common.util.FileUtil;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.util.UIDGenerator;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.AddressingHelper;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.SessionContext;
import org.apache.axis2.deployment.WarBasedAxisConfigurator;
import org.apache.axis2.description.*;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.engine.AxisEngine;
import org.apache.axis2.engine.Handler.InvocationResponse;
import org.apache.axis2.engine.ListenerManager;
import org.apache.axis2.transport.RequestResponseTransport;
import org.apache.axis2.transport.TransportListener;
import org.apache.axis2.transport.TransportUtils;
import org.apache.axis2.transport.http.*;
import org.apache.axis2.transport.http.util.RESTUtil;
import org.apache.axis2.util.JavaUtils;
import org.apache.axis2.util.MessageContextBuilder;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import java.io.*;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author Hamid Ben Malek
 */
public class DomibusServlet extends HttpServlet implements TransportListener {
    // private static final Log log = LogFactory.getLog(AxisServlet.class);
    private static final Logger LOG = Logger.getLogger(AxisServlet.class);
    public static final String CONFIGURATION_CONTEXT = "CONFIGURATION_CONTEXT";
    public static final String SESSION_ID = "SessionId";
    protected transient ConfigurationContext configContext;
    protected transient AxisConfiguration axisConfiguration;

    protected transient ServletConfig servletConfig;

    private transient MyListingAgent agent;
    private String contextRoot;

    protected boolean disableREST;
    private static final String LIST_SERVICES_SUFIX = "/services/listServices";
    private static final String LIST_FAUKT_SERVICES_SUFIX = "/services/ListFaultyServices";
    private boolean closeReader = true;

    private static final int BUFFER_SIZE = 1024 * 8;

    /**
     * Implementaion of POST interface
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        final String msgFile = this.saveRequest(request);
        final InputStream in = this.getInputStream(msgFile);

        // set the initial buffer for a larger value
        response.setBufferSize(DomibusServlet.BUFFER_SIZE);

        this.initContextRoot(request);

        final MessageContext msgContext;
        final OutputStream out = response.getOutputStream();
        final String contentType = request.getContentType();
        if (HTTPTransportUtils.isRESTRequest(contentType)) {
            if (this.disableREST) {
                this.showRestDisabledErrorMessage(response);
            } else {
                new RestRequestProcessor(Constants.Configuration.HTTP_METHOD_POST, request, response)
                        .processXMLRequest();
            }
        } else {
            msgContext = this.createMessageContext(request, response);
            msgContext.setProperty("RequestFile", msgFile);
            msgContext.setProperty("ContentType", contentType);
            msgContext.setProperty(Constants.Configuration.CONTENT_TYPE, contentType);
            try {
                // adding ServletContext into msgContext;
                final InvocationResponse pi = HTTPTransportUtils
                        .processHTTPPostRequest(msgContext, new BufferedInputStream(in), new BufferedOutputStream(out),
                                                contentType, request.getHeader(HTTPConstants.HEADER_SOAP_ACTION),
                                                request.getRequestURL().toString());

                final Boolean holdResponse = (Boolean) msgContext.getProperty(RequestResponseTransport.HOLD_RESPONSE);

                if (pi.equals(InvocationResponse.SUSPEND) ||
                    ((holdResponse != null) && Boolean.TRUE.equals(holdResponse))) {
                    ((RequestResponseTransport) msgContext.getProperty(RequestResponseTransport.TRANSPORT_CONTROL))
                            .awaitResponse();
                }
                response.setContentType(
                        "text/xml; charset=" + msgContext.getProperty(Constants.Configuration.CHARACTER_SET_ENCODING));
                // if data has not been sent back and this is not a signal
                // response
                if (!TransportUtils.isResponseWritten(msgContext) &&
                    (((RequestResponseTransport) msgContext.getProperty(RequestResponseTransport.TRANSPORT_CONTROL))
                             .getStatus() != RequestResponseTransport.RequestResponseTransportStatus.SIGNALLED)) {
                    response.setStatus(HttpServletResponse.SC_ACCEPTED);
                }

            } catch (AxisFault e) {
                this.setResponseState(msgContext, response);
                DomibusServlet.LOG.debug(e);
                if (msgContext != null) {
                    this.processAxisFault(msgContext, response, out, e);
                } else {
                    throw new ServletException(e);
                }
            } catch (Throwable t) {
                DomibusServlet.LOG.error(t.getMessage(), t);
                try {
                    // If the fault is not going along the back channel we
                    // should be 202ing
                    if (AddressingHelper.isFaultRedirected(msgContext)) {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                        final AxisBindingOperation axisBindingOperation =
                                (AxisBindingOperation) msgContext.getProperty(Constants.AXIS_BINDING_OPERATION);
                        if (axisBindingOperation != null) {
                            final AxisBindingMessage axisBindingMessage = axisBindingOperation
                                    .getFault((String) msgContext.getProperty(Constants.FAULT_NAME));
                            if (axisBindingMessage != null) {
                                final Integer code =
                                        (Integer) axisBindingMessage.getProperty(WSDL2Constants.ATTR_WHTTP_CODE);
                                if (code != null) {
                                    response.setStatus(code.intValue());
                                }
                            }
                        }
                    }
                    this.handleFault(msgContext, out, new AxisFault(t.toString(), t));
                } catch (AxisFault e2) {
                    DomibusServlet.LOG.info(e2);
                    throw new ServletException(e2);
                }
            } finally {
                this.closeStaxBuilder(msgContext);
                TransportUtils.deleteAttachments(msgContext);
            }
        }
    }

    /**
     * Implementation for GET interface
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */

    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {

        this.initContextRoot(request);

        // this method is also used to serve for the listServices request.

        final String requestURI = request.getRequestURI();
        final String query = request.getQueryString();

        // There can be three different request coming to this.
        // 1. wsdl, wsdl2 and xsd requests
        // 2. list services requests
        // 3. REST requests.
        if ((query != null) &&
            ((query.indexOf("wsdl2") >= 0) || (query.indexOf("wsdl") >= 0) || (query.indexOf("xsd") >= 0) ||
             (query.indexOf("policy") >= 0))) {
            // handling meta data exchange stuff
            this.agent.initTransportListener(request);
            this.agent.processListService(request, response);
        } else if (requestURI.endsWith(".xsd") || requestURI.endsWith(".wsdl")) {
            this.agent.processExplicitSchemaAndWSDL(request, response);
        } else if (requestURI.endsWith(DomibusServlet.LIST_SERVICES_SUFIX) ||
                   requestURI.endsWith(DomibusServlet.LIST_FAUKT_SERVICES_SUFIX)) {
            // handling list services request
            try {
                this.agent.handle(request, response);
            } catch (Exception e) {
                throw new ServletException(e);
            }
        } else if (!this.disableREST) {
            new RestRequestProcessor(Constants.Configuration.HTTP_METHOD_GET, request, response).processURLRequest();
        } else {
            this.showRestDisabledErrorMessage(response);
        }
    }

    /**
     * Implementation of DELETE interface
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */

    protected void doDelete(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {

        this.initContextRoot(request);
        // this method is also used to serve for the listServices request.
        if (this.disableREST) {
            this.showRestDisabledErrorMessage(response);
        } else {
            new RestRequestProcessor(Constants.Configuration.HTTP_METHOD_DELETE, request, response).processURLRequest();
        }
    }

    /**
     * Implementation of PUT interface
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void doPut(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {

        this.initContextRoot(request);
        // this method is also used to serve for the listServices request.
        if (this.disableREST) {
            this.showRestDisabledErrorMessage(response);
        } else {
            new RestRequestProcessor(Constants.Configuration.HTTP_METHOD_PUT, request, response).processXMLRequest();
        }
    }

    /**
     * Private method that deals with disabling of REST support.
     *
     * @param response
     * @throws IOException
     */
    protected void showRestDisabledErrorMessage(final HttpServletResponse response) throws IOException {
        final PrintWriter writer = new PrintWriter(response.getOutputStream());
        writer.println("<html><body><h2>Please enable REST support in WEB-INF/conf/axis2.xml " +
                       "and WEB-INF/web.xml</h2></body></html>");
        writer.flush();
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
    }

    /**
     * Close the builders.
     *
     * @param messageContext
     * @throws ServletException
     */
    private void closeStaxBuilder(final MessageContext messageContext) throws ServletException {
        if (this.closeReader && (messageContext != null)) {
            try {
                final SOAPEnvelope envelope = messageContext.getEnvelope();
                if (envelope != null) {
                    final StAXBuilder builder = (StAXBuilder) envelope.getBuilder();
                    if (builder != null) {
                        builder.close();
                    }
                }
            } catch (Exception e) {
                DomibusServlet.LOG.error(e.toString(), e);
            }
        }
    }

    /**
     * Processing for faults
     *
     * @param msgContext
     * @param res
     * @param out
     * @param e
     */
    private void processAxisFault(final MessageContext msgContext, final HttpServletResponse res,
                                  final OutputStream out, final AxisFault e) {
        try {
            // If the fault is not going along the back channel we should be
            // 202ing
            if (AddressingHelper.isFaultRedirected(msgContext)) {
                res.setStatus(HttpServletResponse.SC_ACCEPTED);
            } else {

                final String status = (String) msgContext.getProperty(Constants.HTTP_RESPONSE_STATE);
                if (status == null) {
                    res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                } else {
                    res.setStatus(Integer.parseInt(status));
                }

                final AxisBindingOperation axisBindingOperation =
                        (AxisBindingOperation) msgContext.getProperty(Constants.AXIS_BINDING_OPERATION);
                if (axisBindingOperation != null) {
                    final AxisBindingMessage fault =
                            axisBindingOperation.getFault((String) msgContext.getProperty(Constants.FAULT_NAME));
                    if (fault != null) {
                        final Integer code = (Integer) fault.getProperty(WSDL2Constants.ATTR_WHTTP_CODE);
                        if (code != null) {
                            res.setStatus(code.intValue());
                        }
                    }
                }
            }
            this.handleFault(msgContext, out, e);
        } catch (AxisFault e2) {
            DomibusServlet.LOG.error(e2);
        }
    }

    protected void handleFault(final MessageContext msgContext, final OutputStream out, final AxisFault e)
            throws AxisFault {
        msgContext.setProperty(MessageContext.TRANSPORT_OUT, out);

        final MessageContext faultContext = MessageContextBuilder.createFaultMessageContext(msgContext, e);
        // SOAP 1.2 specification mentions that we should send HTTP code 400 in
        // a fault if the
        // fault code Sender
        final HttpServletResponse response =
                (HttpServletResponse) msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLETRESPONSE);
        if (response != null) {

            // TODO : Check for SOAP 1.2!
            final SOAPFaultCode code = faultContext.getEnvelope().getBody().getFault().getCode();

            OMElement valueElement = null;
            if (code != null) {
                valueElement = code.getFirstChildWithName(new QName(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI,
                                                                    SOAP12Constants.SOAP_FAULT_VALUE_LOCAL_NAME));
            }

            if (valueElement != null) {
                if (SOAP12Constants.FAULT_CODE_SENDER.equals(valueElement.getTextAsQName().getLocalPart()) &&
                    !msgContext.isDoingREST()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            }
        }

        AxisEngine.sendFault(faultContext);
    }

    /**
     * Main init method
     *
     * @param config
     * @throws ServletException
     */
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        try {
            this.servletConfig = config;
            final ServletContext servletContext = this.servletConfig.getServletContext();
            this.configContext =
                    (ConfigurationContext) servletContext.getAttribute(DomibusServlet.CONFIGURATION_CONTEXT);
            if (this.configContext == null) {
                this.configContext = this.initConfigContext(config);
                config.getServletContext().setAttribute(DomibusServlet.CONFIGURATION_CONTEXT, this.configContext);
            }
            this.axisConfiguration = this.configContext.getAxisConfiguration();

            final ListenerManager listenerManager = new ListenerManager();
            listenerManager.init(this.configContext);
            final TransportInDescription transportInDescription = new TransportInDescription(Constants.TRANSPORT_HTTP);
            transportInDescription.setReceiver(this);
            listenerManager.addListener(transportInDescription, true);
            listenerManager.start();
            // FIXME ListenerManager.defaultConfigurationContext =
            // configContext;
            this.agent = new MyListingAgent(this.configContext);

            this.initParams();

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * distroy the ConfigurationContext
     */
    public void destroy() {
        // stoping listner manager
        try {
            if (this.configContext != null) {
                this.configContext.terminate();
            }
        } catch (AxisFault axisFault) {
            DomibusServlet.LOG.error(axisFault.getMessage());
        }
        try {
            super.destroy();
        } catch (Exception e) {
            DomibusServlet.LOG.error(e.getMessage());
        }
    }

    /**
     * Initializes the Axis2 parameters.
     */
    protected void initParams() {
        Parameter parameter;
        // do we need to completely disable REST support
        parameter = this.axisConfiguration.getParameter(Constants.Configuration.DISABLE_REST);
        if (parameter != null) {
            this.disableREST = !JavaUtils.isFalseExplicitly(parameter.getValue());
        }

        // Should we close the reader(s)
        parameter = this.axisConfiguration.getParameter("axis2.close.reader");
        if (parameter != null) {
            this.closeReader = JavaUtils.isTrueExplicitly(parameter.getValue());
        }

    }

    /**
     * Convenient method to re-initialize the ConfigurationContext
     *
     * @throws ServletException
     */
    public void init() throws ServletException {
        if (this.servletConfig != null) {
            this.init(this.servletConfig);
        }
    }

    /**
     * Initialize the Axis configuration context
     *
     * @param config Servlet configuration
     * @return ConfigurationContext
     * @throws ServletException
     */
    protected ConfigurationContext initConfigContext(final ServletConfig config) throws ServletException {
        try {
            final ConfigurationContext configContext =
                    ConfigurationContextFactory.createConfigurationContext(new WarBasedAxisConfigurator(config));
            configContext.setProperty(Constants.CONTAINER_MANAGED, Constants.VALUE_TRUE);
            return configContext;
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * Set the context root if it is not set already.
     *
     * @param req
     */
    public void initContextRoot(final HttpServletRequest req) {
        if ((this.contextRoot != null) && !this.contextRoot.trim().isEmpty()) {
            return;
        }
        String contextPath = req.getContextPath();
        // handling ROOT scenario, for servlets in the default (root) context,
        // this method returns ""
        if ((contextPath != null) && contextPath.isEmpty()) {
            contextPath = "/";
        }
        this.contextRoot = contextPath;

        this.configContext.setContextRoot(this.contextRoot);
    }

    /**
     * Get all transport headers.
     *
     * @param req
     * @return Map
     */
    protected Map getTransportHeaders(final HttpServletRequest req) {
        return new TransportHeaders(req);
    }

    public EndpointReference getEPRForService(final String serviceName, final String ip) throws AxisFault {
        return this.getEPRsForService(serviceName, ip)[0];
    }

    public EndpointReference[] getEPRsForService(final String serviceName, String ip) throws AxisFault {
        // RUNNING_PORT
        String port = null;
        // FIXME port = (String)
        // configContext.getProperty(ListingAgent.RUNNING_PORT);
        if (port == null) {
            port = "8080";
        }
        if (ip == null) {
            // TODO check ip = HttpUtils.getIpAddress(axisConfiguration);
            if (ip == null) {
                ip = "localhost";
            }
        }

        String endpointRefernce = "http://" + ip + ":" + port;
        if (this.configContext.getServiceContextPath().startsWith("/")) {
            endpointRefernce = endpointRefernce + this.configContext.getServiceContextPath() + "/" + serviceName;
        } else {
            endpointRefernce = endpointRefernce + '/' + this.configContext.getServiceContextPath() + "/" + serviceName;
        }
        final EndpointReference endpoint = new EndpointReference(endpointRefernce);

        return new EndpointReference[]{endpoint};
    }

    /**
     * init(); start() and stop() wouldn't do anything.
     *
     * @param axisConf
     * @param transprtIn
     * @throws AxisFault
     */
    public void init(final ConfigurationContext axisConf, final TransportInDescription transprtIn) throws AxisFault {
    }

    public void start() throws AxisFault {
    }

    public void stop() throws AxisFault {
    }

    /**
     * @param request
     * @param response
     * @param invocationType : If invocationType=true; then this will be used in SOAP
     *                       message invocation. If invocationType=false; then this will be
     *                       used in REST message invocation.
     * @return MessageContext
     * @throws IOException
     */
    protected MessageContext createMessageContext(final HttpServletRequest request, final HttpServletResponse response,
                                                  final boolean invocationType) throws IOException {
        final MessageContext msgContext = this.configContext.createMessageContext();
        String requestURI = request.getRequestURI();

        String trsPrefix = request.getRequestURL().toString();
        final int sepindex = trsPrefix.indexOf(':');
        if (sepindex > -1) {
            trsPrefix = trsPrefix.substring(0, sepindex);
            msgContext.setIncomingTransportName(trsPrefix);
        } else {
            msgContext.setIncomingTransportName(Constants.TRANSPORT_HTTP);
            trsPrefix = Constants.TRANSPORT_HTTP;
        }
        final TransportInDescription transportIn =
                this.axisConfiguration.getTransportIn(msgContext.getIncomingTransportName());
        // set the default output description. This will be http

        TransportOutDescription transportOut = this.axisConfiguration.getTransportOut(trsPrefix);
        if (transportOut == null) {
            // if the req coming via https but we do not have a https sender
            transportOut = this.axisConfiguration.getTransportOut(Constants.TRANSPORT_HTTP);
        }

        msgContext.setTransportIn(transportIn);
        msgContext.setTransportOut(transportOut);
        msgContext.setServerSide(true);

        if (!invocationType) {
            final String query = request.getQueryString();
            if (query != null) {
                requestURI = requestURI + "?" + query;
            }
        }

        msgContext.setTo(new EndpointReference(requestURI));
        msgContext.setFrom(new EndpointReference(request.getRemoteAddr()));
        msgContext.setProperty(MessageContext.REMOTE_ADDR, request.getRemoteAddr());
        msgContext.setProperty(Constants.OUT_TRANSPORT_INFO, new ServletBasedOutTransportInfo(response));
        // set the transport Headers
        msgContext.setProperty(MessageContext.TRANSPORT_HEADERS, this.getTransportHeaders(request));
        msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST, request);
        msgContext.setProperty(HTTPConstants.MC_HTTP_SERVLETRESPONSE, response);

        // setting the RequestResponseTransport object
        msgContext
                .setProperty(RequestResponseTransport.TRANSPORT_CONTROL, new ServletRequestResponseTransport(response));

        return msgContext;
    }

    /**
     * This method assumes, that the created MessageContext will be used in only
     * SOAP invocation.
     *
     * @param req
     * @param resp
     * @return MessageContext
     * @throws IOException
     */

    protected MessageContext createMessageContext(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {
        return this.createMessageContext(req, resp, true);
    }

    /**
     * Transport session management.
     *
     * @param messageContext
     * @return SessionContext
     */
    public SessionContext getSessionContext(final MessageContext messageContext) {
        final HttpServletRequest req =
                (HttpServletRequest) messageContext.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
        SessionContext sessionContext =
                (SessionContext) req.getSession(true).getAttribute(Constants.SESSION_CONTEXT_PROPERTY);
        final String sessionId = req.getSession().getId();
        if (sessionContext == null) {
            sessionContext = new SessionContext(null);
            sessionContext.setCookieID(sessionId);
            req.getSession().setAttribute(Constants.SESSION_CONTEXT_PROPERTY, sessionContext);
        }
        messageContext.setSessionContext(sessionContext);
        messageContext.setProperty(DomibusServlet.SESSION_ID, sessionId);
        return sessionContext;
    }

    protected class ServletRequestResponseTransport implements RequestResponseTransport {
        private final HttpServletResponse response;
        private boolean responseWritten;
        private final CountDownLatch responseReadySignal = new CountDownLatch(1);
        RequestResponseTransportStatus status = RequestResponseTransportStatus.INITIAL;
        AxisFault faultToBeThrownOut;

        ServletRequestResponseTransport(final HttpServletResponse response) {
            this.response = response;
        }

        public void acknowledgeMessage(final MessageContext msgContext) throws AxisFault {
            DomibusServlet.LOG.debug("Acking one-way request");
            this.response.setContentType(
                    "text/xml; charset=" + msgContext.getProperty(Constants.Configuration.CHARACTER_SET_ENCODING));

            this.response.setStatus(HttpServletResponse.SC_ACCEPTED);
            try {
                this.response.flushBuffer();
            } catch (IOException e) {
                throw new AxisFault("Error sending acknowledgement", e);
            }

            this.signalResponseReady();
        }

        public void awaitResponse() throws InterruptedException, AxisFault {
            DomibusServlet.LOG.debug("Blocking servlet thread -- awaiting response");
            this.status = RequestResponseTransportStatus.WAITING;
            this.responseReadySignal.await();

            if (this.faultToBeThrownOut != null) {
                throw this.faultToBeThrownOut;
            }
        }

        public void signalResponseReady() {
            DomibusServlet.LOG.debug("Signalling response available");
            this.status = RequestResponseTransportStatus.SIGNALLED;
            this.responseReadySignal.countDown();
        }

        public RequestResponseTransportStatus getStatus() {
            return this.status;
        }

        public void signalFaultReady(final AxisFault fault) {
            this.faultToBeThrownOut = fault;
            this.signalResponseReady();
        }

        public boolean isResponseWritten() {
            return this.responseWritten;
        }

        public void setResponseWritten(final boolean responseWritten) {
            this.responseWritten = responseWritten;
        }

    }

    private void setResponseState(final MessageContext messageContext, final HttpServletResponse response) {
        final String state = (String) messageContext.getProperty(Constants.HTTP_RESPONSE_STATE);
        if (state != null) {
            final int stateInt = Integer.parseInt(state);
            if (stateInt == HttpServletResponse.SC_UNAUTHORIZED) { // Unauthorized
                final String realm = (String) messageContext.getProperty(Constants.HTTP_BASIC_AUTH_REALM);
                response.addHeader("WWW-Authenticate", "basic realm=\"" + realm + "\"");
            }
        }
    }

    /**
     * Ues in processing REST related Requests. This is the helper Class use in
     * processing of doGet, doPut , doDelete and doPost.
     */
    protected class RestRequestProcessor {
        private final MessageContext messageContext;
        private final HttpServletRequest request;
        private final HttpServletResponse response;

        public RestRequestProcessor(final String httpMethodString, final HttpServletRequest request,
                                    final HttpServletResponse response) throws IOException {
            this.request = request;
            this.response = response;
            this.messageContext = DomibusServlet.this.createMessageContext(this.request, this.response, false);
            this.messageContext.setProperty(HTTPConstants.HTTP_METHOD, httpMethodString);
        }

        public void processXMLRequest() throws IOException, ServletException {
            try {
                RESTUtil.processXMLRequest(this.messageContext, this.request.getInputStream(),
                                           this.response.getOutputStream(), this.request.getContentType());
                this.checkResponseWritten();
            } catch (AxisFault axisFault) {
                this.processFault(axisFault);
            }
            DomibusServlet.this.closeStaxBuilder(this.messageContext);
        }

        public void processURLRequest() throws IOException, ServletException {
            try {
                RESTUtil.processURLRequest(this.messageContext, this.response.getOutputStream(),
                                           this.request.getContentType());
                this.checkResponseWritten();
            } catch (AxisFault e) {
                DomibusServlet.this.setResponseState(this.messageContext, this.response);
                this.processFault(e);
            }
            DomibusServlet.this.closeStaxBuilder(this.messageContext);

        }

        private void checkResponseWritten() {
            if (!TransportUtils.isResponseWritten(this.messageContext)) {
                this.response.setStatus(HttpServletResponse.SC_ACCEPTED);
            }
        }

        private void processFault(final AxisFault e) throws ServletException, IOException {
            DomibusServlet.LOG.debug(e);
            if (this.messageContext != null) {
                DomibusServlet.this
                        .processAxisFault(this.messageContext, this.response, this.response.getOutputStream(), e);
            } else {
                throw new ServletException(e);
            }

        }

    }

    class MyListingAgent extends ListingAgent {
        public MyListingAgent(final ConfigurationContext configCtx) {
            super(configCtx);
        }

        public void initTransportListener(final HttpServletRequest httpServletRequest) {
            // FIXME super.initTransportListener(httpServletRequest);
        }
    }

    public String saveRequest(final HttpServletRequest req) {
        try {
            final ServletContext servletContext = this.servletConfig.getServletContext();
            final String path = servletContext.getRealPath("/WEB-INF");
            String random = UIDGenerator.generateURNString();
            if (random.indexOf(":") >= 0) {
                random = random.replaceAll(":", "-");
            }
            final String filePath = path + File.separator + "request_" + random;
            final File msg = new File(filePath);
            FileUtil.writeToFile(msg, req.getInputStream());
            return filePath;
        } catch (IOException ex) {
            DomibusServlet.LOG.error("Problem while getting InputStream from HttpServletRequest", ex);
            return null;
        }
    }

    private InputStream getInputStream(final String file) {
        if (file == null) {
            return null;
        }
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            DomibusServlet.LOG.error("Could not create FileInputStream because file was not found", ex);
            return null;
        }
    }
}