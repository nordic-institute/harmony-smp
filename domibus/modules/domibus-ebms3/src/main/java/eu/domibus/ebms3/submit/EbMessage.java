package eu.domibus.ebms3.submit;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.*;
import org.apache.axiom.util.UIDGenerator;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.client.async.AxisCallback;
import org.apache.axis2.context.AbstractContext;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.log4j.Logger;
import eu.domibus.common.persistent.AbstractBaseEntity;
import eu.domibus.common.persistent.Attachment;
import eu.domibus.common.util.FileUtil;
import eu.domibus.common.util.XMLUtil;
import eu.domibus.ebms3.config.Leg;
import eu.domibus.ebms3.module.Configuration;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.persistent.EbmsPayload;
import eu.domibus.ebms3.persistent.Payloads;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.persistence.*;
import java.io.File;
import java.util.*;


/**
 * This class is intended to be used only within the same class
 * loader of the ebms3 module.
 *
 * @author Hamid Ben Malek
 */
@Entity
@Table(name = "TB_EBMS_MESSAGE")
@Inheritance(strategy = InheritanceType.JOINED)
public class EbMessage extends AbstractBaseEntity {

    private static final Logger LOG = Logger.getLogger(EbMessage.class);

    @Transient
    protected static ConfigurationContext configContext;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinColumn(name = "MESSAGE_TO_SEND_PK")
    protected Set<Attachment> attachments = new HashSet<Attachment>();

    @Column(name = "MIME_FILE")
    protected String mimeFile = null;

    @Column(name = "CONTENT_TYPE")
    protected String contentType = null;

    @Transient
    protected double soapVersion = 1.1;

    @Transient
    protected String attchmentDir;

    @Transient
    protected MessageContext messageContext;

    @Transient
    protected boolean enableSWA = true;

    @Transient
    protected String storageFolder = null;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, String> contextProps = new HashMap<String, String>();


    public EbMessage() {
        this(Constants.configContext);
    }

    public EbMessage(final ConfigurationContext ctx) {
        EbMessage.configContext = ctx;
        this.messageContext = new MessageContext();
        this.messageContext.setConfigurationContext(EbMessage.configContext);
        EbMessage.configureHttpClient(EbMessage.configContext);
    }

    public EbMessage(final MsgInfoSet mis) {
        this(Constants.configContext);
        this.setMsgInfoSet(mis);
    }

    public EbMessage(final File folder, final MsgInfoSet mis) {
        this(mis);
        final String bodyPayload = mis.getBodyPayload();
        if ((bodyPayload != null) && !bodyPayload.trim().isEmpty()) {
            final String bodyFile = folder.getAbsolutePath() + File.separator + bodyPayload.trim();
            this.addToBody(bodyFile);
        }

        // The payloads are defined by the Metadata/Payloads/EbmsPayload elements
        // in the metadata.xml file
        final Payloads payloadsElement = mis.getPayloads();
        if ((payloadsElement != null) && (payloadsElement.getPayloads() != null)) {
            for (final EbmsPayload payload : payloadsElement.getPayloads()) {
                final String payloadLocalFilename = payload.getFile();
                final String payloadAbsoluteFilename = folder + File.separator + payloadLocalFilename;
                final String existingContentId = mis.getCID(payloadLocalFilename);
                final String contentType = payload.getContentType();
                final String newContentId;
                if (mis.isCompressed(payloadLocalFilename)) {
                    FileUtil.doCompressFile(payloadAbsoluteFilename);
                    newContentId = this.addFileAttachment(payloadAbsoluteFilename + ".gz", existingContentId);
                } else {
                    if ((contentType != null) && !"".equals(contentType)) {
                        newContentId = this.addFileAttachment(payloadAbsoluteFilename, existingContentId, contentType);
                    } else {
                        newContentId = this.addFileAttachment(payloadAbsoluteFilename, existingContentId);
                    }
                }
                if (!newContentId.equals(existingContentId)) {
                    mis.setCID(newContentId, payloadLocalFilename);
                }
            }
        }
    }

    //TODO: remove send logic from entity class

    /**
     * Configures the HTTP client to:
     * <ul>
     * <li>allow a larger number of connects per endpoint</li>
     * <li>not retry to connect on errors (will be managed by AS4 reliability)</li>
     * </ul>
     *
     * @param configurationContext the configuration context to setup
     */
    public static void configureHttpClient(final AbstractContext configurationContext) {
        final MultiThreadedHttpConnectionManager conmgr = new MultiThreadedHttpConnectionManager();
        conmgr.getParams().setDefaultMaxConnectionsPerHost(10);
        final HttpClient client = new HttpClient(conmgr);
        configurationContext.setProperty(HTTPConstants.CACHED_HTTP_CLIENT, client);
    }

    public static SOAPEnvelope createEnvelope(final double soapVersion) {
        SOAPFactory omFactory = null;
        if (soapVersion < 1.2) {
            omFactory = OMAbstractFactory.getSOAP11Factory();
        } else {
            omFactory = OMAbstractFactory.getSOAP12Factory();
        }

        final SOAPEnvelope envelope = omFactory.getDefaultEnvelope();
        envelope.declareNamespace("http://www.w3.org/1999/XMLSchema-instance/", "xsi");
        envelope.declareNamespace("http://www.w3.org/1999/XMLSchema", "xsd");
        return envelope;
    }

    @PrePersist
    @PreUpdate
    private void refreshContextProps() {
        contextProps.clear();
        final Iterator<String> propertyIterator = messageContext.getPropertyNames();
        while (propertyIterator.hasNext()) {
            final String key = propertyIterator.next();
            if (messageContext.getProperty(key) instanceof String) {
                contextProps.put(key, (String) messageContext.getProperty(key));
            }
        }


    }


    @PostLoad
    private void refreshMessageContext() {
        for (final Map.Entry<String, String> prop : contextProps.entrySet()) {
            messageContext.setProperty(prop.getKey(), prop.getValue());
        }
    }

    public String getMimeFile() {
        return this.mimeFile;
    }

    public void setMimeFile(final String mimeFile) {
        this.mimeFile = mimeFile;
    }

    public boolean isEnableSWA() {
        return this.enableSWA;
    }

    public void setEnableSWA(final boolean enableSWA) {
        this.enableSWA = enableSWA;
    }

    public ConfigurationContext getConfigurationContext() {
        return EbMessage.configContext;
    }

    public void setConfigurationContext(final ConfigurationContext confCtx) {
        EbMessage.configContext = confCtx;
        if (this.messageContext != null) {
            this.messageContext.setConfigurationContext(confCtx);
            this.messageContext.activate(EbMessage.configContext);
            EbMessage.configureHttpClient(confCtx);
        }
    }

    public double getSoapVersion() {
        return this.soapVersion;
    }

    public void setSoapVersion(final double soapVersion) {
        this.soapVersion = soapVersion;
    }

    public String getAttchmentDir() {
        return this.attchmentDir;
    }

    public void setAttchmentDir(final String attchmentDir) {
        this.attchmentDir = attchmentDir;
    }

    public SOAPEnvelope getEnvelope() {

        if (this.getMessageContext().getEnvelope() == null) {
            try {
                this.messageContext.setEnvelope(EbMessage.createEnvelope(this.soapVersion));
            } catch (AxisFault e) {
                throw new RuntimeException(e);
            }
        }

        return this.getMessageContext().getEnvelope();
    }

    public void setEnvelope(final SOAPEnvelope env) {

        if (env != null) {
            try {
                this.getMessageContext().setEnvelope(env);
            } catch (AxisFault e) {
                throw new RuntimeException(e);
            }
        }

    }

    public SOAPBody getBody() {
        return this.getEnvelope().getBody();
    }

    public void addToBody(final OMElement payload) {
        if (payload != null) {
            this.getBody().addChild(payload);

        }
    }

    public OMElement addToBody(final String xmlDocFile) {
        final File payload = new File(xmlDocFile);
        if (!payload.exists()) {
            return null;
        }
        final OMElement pLoad = XMLUtil.rootElement(payload);
        this.addToBody(pLoad);
        return pLoad;
    }

    public MessageContext getMessageContext() {
        messageContext.setProperty("attachments", attachments);
        return messageContext;
    }

    public void setMessageContext(final MessageContext messageContext) {
        this.messageContext = messageContext;
    }

    public MessageContext inOut(final ServiceClient sender) {
        MessageContext response = null;

        this.getMessageContext().setOptions(sender.getOptions());
        final OperationClient mepClient;
        try {
            mepClient = sender.createClient(ServiceClient.ANON_OUT_IN_OP);

            mepClient.addMessageContext(this.getMessageContext());
            mepClient.execute(true);
            response = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
        } catch (AxisFault e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    public MessageContext inOut(final String toURL, final String action, final String[] modules) {
        MessageContext response = null;
        try {
            final ServiceClient sender = this.createServiceClient(toURL, action, modules);
            this.getMessageContext().setOptions(sender.getOptions());
            final OperationClient mepClient = sender.createClient(ServiceClient.ANON_OUT_IN_OP);
            mepClient.addMessageContext(this.getMessageContext());
            mepClient.execute(true);
            response = mepClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
        } catch (AxisFault ex) {
            throw new RuntimeException(ex);
        }
        return response;
    }

    public void inOut(final String toURL, final String action, final String[] modules, final AxisCallback callback) {

        try {
            final ServiceClient sender = this.createServiceClient(toURL, action, modules);
            this.getMessageContext().setOptions(sender.getOptions());
            final OperationClient mepClient = sender.createClient(ServiceClient.ANON_OUT_IN_OP);
            mepClient.addMessageContext(this.getMessageContext());
            if (callback != null) {
                mepClient.setCallback(callback);
                mepClient.execute(false);
            } else {
                mepClient.execute(true);
            }

        } catch (AxisFault ex) {
            throw new RuntimeException(ex);
        }

    }

    public void inOnly(final String toURL, final String action, final String[] modules) {
        try {
            final ServiceClient sender = this.createServiceClient(toURL, action, modules);
            this.getMessageContext().setOptions(sender.getOptions());
            final OperationClient mepClient = sender.createClient(ServiceClient.ANON_OUT_ONLY_OP);
            mepClient.addMessageContext(this.getMessageContext());
            mepClient.execute(false);
        } catch (AxisFault ex) {
            throw new RuntimeException(ex);
        }
    }

    public void terminate() {
        try {
            EbMessage.configContext.terminate();
        } catch (AxisFault ex) {
            throw new RuntimeException(ex);
        }
    }

    protected ServiceClient createServiceClient(final String toURL, final String action, final String[] modules) {
        final EndpointReference targetEPR = new EndpointReference(toURL);
        final Options options = new Options();
        options.setTo(targetEPR);
        if (this.enableSWA) {
            options.setProperty(org.apache.axis2.Constants.Configuration.ENABLE_SWA,
                    org.apache.axis2.Constants.VALUE_TRUE);
            options.setProperty(org.apache.axis2.Constants.Configuration.CACHE_ATTACHMENTS,
                    org.apache.axis2.Constants.VALUE_TRUE);
            options.setProperty(org.apache.axis2.Constants.Configuration.FILE_SIZE_THRESHOLD, "4000");

            final String attachDir = this.getAttachmentDirectory();

            if (attachDir != null) {
                options.setProperty(org.apache.axis2.Constants.Configuration.ATTACHMENT_TEMP_DIR, this.attchmentDir);
            }
        }
        if (this.soapVersion <= 1.1) {
            options.setSoapVersionURI(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        } else {
            options.setSoapVersionURI(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        }
        options.setTimeOutInMilliSeconds(200000);
        options.setTo(targetEPR);
        options.setAction(action);
        ServiceClient sender = null;
        try {
            sender = new ServiceClient(EbMessage.configContext, null);
            sender.setOptions(options);
            if ((modules != null) && (modules.length > 0)) {
                for (final String module : modules) {
                    sender.engageModule(module);
                }
            }
        } catch (AxisFault ex) {
            throw new RuntimeException(ex);
        }
        return sender;
    }

    public String addFileAttachment(final String file, String cid) {
        if ((file == null) || "".equals(file.trim())) {
            return cid;
        }
        final File data = new File(file);
        if (!data.exists()) {
            return cid;
        }
        if ((cid == null) || "".equals(cid.trim())) {
            cid = UIDGenerator.generateURNString();
        }

        final FileDataSource fileDataSource = new FileDataSource(data);
        fileDataSource.setFileTypeMap(FileUtil.getMimeTypes());
        final DataHandler dataHandler = new DataHandler(fileDataSource);
        final Attachment part = new Attachment(file, cid);
        this.attachments.add(part);
        this.getMessageContext().addAttachment(cid, dataHandler);
        return cid;
    }

    public String addFileAttachment(final String file, String cid, final String mimeType) {
        if ((file == null) || "".equals(file.trim())) {
            return cid;
        }
        final File data = new File(file);
        if (!data.exists()) {
            return cid;
        }
        if ((cid == null) || "".equals(cid.trim())) {
            cid = UIDGenerator.generateURNString();
        }

        final FileDataSource fileDataSource = new FileDataSource(data);
        fileDataSource.setFileTypeMap(FileUtil.getMimeTypes(mimeType));
        final DataHandler dataHandler = new DataHandler(fileDataSource);
        final Attachment part = new Attachment(file, cid);
        part.setContentType(mimeType);
        this.attachments.add(part);
        this.getMessageContext().addAttachment(cid, dataHandler);
        return cid;
    }

    public Collection<Attachment> getAttachments() {
        return this.attachments;
    }

    public void setAttachments(final Set<Attachment> attachments) {
        this.attachments = attachments;
    }

    public String getStorageFolder() {
        return this.storageFolder;
    }

    /**
     * This method needs to be called first before calling getMessageContext()
     * when the message is being resurrected from a persistent media
     */
    public void setStorageFolder(final String storageFolder) {
        this.storageFolder = storageFolder;
    }

    private String getAttachmentDirectory() {

        return (String) EbMessage.configContext.getAxisConfiguration()
                .getParameter(org.apache.axis2.Constants.Configuration.ATTACHMENT_TEMP_DIR).getValue();
    }

    public void insertMsgInfoSet(final MsgInfoSet mis) {
        this.setMsgInfoSet(mis);
    }

    /**
     * END - Methods copied from domibus-common drc/main/java/org/domibus/common/client/Client
     */

    protected void setMsgInfoSet(final MsgInfoSet mis) {
        if (mis == null) {
            return;
        }

        final String ver = Configuration.getSoapVersion(mis);
        if ((ver != null) && !"".equals(ver.trim())) {
            this.soapVersion = Double.parseDouble(ver);
        }

        final String toURL = Configuration.getFinalAddress(mis);
        String action = Configuration.getSoapAction(mis);

        if ((action == null) || "".equals(action.trim())) {
            action = Configuration.getWsaAction(mis);
        }

        if ((action == null) || "".equals(action.trim())) {
            action = "ebms3";
        }
        final EndpointReference targetEPR = new EndpointReference(toURL);
        final Options options = new Options();
        options.setTo(targetEPR);
        options.setAction(action);
        this.getMessageContext().setOptions(options);
        this.getMessageContext().setWSAAction(action);
        this.getMessageContext().setProperty("MESSAGE_INFO_SET", mis);

        final String rel = Configuration.getReliability(mis);
        if (rel != null) {
            this.getMessageContext().setProperty("QUALITY", rel);
        }
        final String security = Configuration.getSecurity(mis);
        if (security != null) {
            this.getMessageContext().setProperty("SECURITY", security);
        }
    }

    public MessageContext inOut(final MsgInfoSet mis) {
        final String[] modules = Constants.engagedModules;

        final String toURL = Configuration.getFinalAddress(mis);
        String action = Configuration.getSoapAction(mis);
        if ((action == null) || "".equals(action.trim())) {
            action = Configuration.getWsaAction(mis);
        }
        this.getMessageContext().setProperty("MESSAGE_INFO_SET", mis);

        final String rel = Configuration.getReliability(mis);
        if (rel != null) {
            this.getMessageContext().setProperty("QUALITY", rel);
        }
        return this.inOut(toURL, action, modules);
    }

    public void inOut(final MsgInfoSet mis, final AxisCallback callback) {
        final String[] modules = Constants.engagedModules;

        final String toURL = Configuration.getFinalAddress(mis);
        String action = Configuration.getSoapAction(mis);
        if ((action == null) || "".equals(action.trim())) {
            action = Configuration.getWsaAction(mis);
        }
        this.getMessageContext().setProperty("MESSAGE_INFO_SET", mis);

        final String rel = Configuration.getReliability(mis);
        if (rel != null) {
            this.getMessageContext().setProperty("QUALITY", rel);
        }
        this.inOut(toURL, action, modules, callback);
    }

    public void inOnly(final MsgInfoSet mis) {
        final String[] modules = Constants.engagedModules;

        final String toURL = Configuration.getFinalAddress(mis);
        String action = Configuration.getSoapAction(mis);
        if ((action == null) || "".equals(action.trim())) {
            action = Configuration.getWsaAction(mis);
        }

        this.getMessageContext().setProperty("MESSAGE_INFO_SET", mis);
        messageContext.setProperty("MESSAGE_ID", mis.getMessageId());

        final String rel = Configuration.getReliability(mis);
        if (rel != null) {
            this.getMessageContext().setProperty("QUALITY", rel);
        }

        this.inOnly(toURL, action, modules);
    }

    public MessageContext send(final MsgInfoSet mis, final AxisCallback callback) {
        if (mis == null) {
            return null;
        }
        if (mis.getMessageId() != null) {
            this.getMessageContext().getOptions().setMessageId(mis.getMessageId());
        }
        final String mep = Configuration.getMep(mis);
        final int ln = mis.getLegNumber();
        final Leg leg = Configuration.getLeg(mis);
        if (mep.equalsIgnoreCase(Constants.ONE_WAY_PUSH) || mep.equalsIgnoreCase(Constants.TWO_WAY_PUSH_AND_PUSH)) {
            if ((leg != null) && (leg.getReceiptReply() != null) &&
                    "Response".equalsIgnoreCase(leg.getReceiptReply())) {
                return this.inOut(mis);
            } else if ((leg != null) && (leg.getReliability() != null)) {
                return this.inOut(mis);
            } else {
                this.inOnly(mis);
            }
            return null;
        } else if (mep.equalsIgnoreCase(Constants.ONE_WAY_PULL)) {
            if (ln == 1) {
                if (callback == null) {
                    return this.inOut(mis);
                } else {
                    this.inOut(mis, callback);
                    return null;
                }
            } else if (ln == 2) {
                this.inOnly(mis);
                return null;
            }
        } else if (mep.equalsIgnoreCase(Constants.TWO_WAY_SYNC)) {
            if (ln == 1) {
                if (callback == null) {
                    return this.inOut(mis);
                } else {
                    this.inOut(mis, callback);
                    return null;
                }
            } else if (ln == 2) {
                this.inOnly(mis);
                return null;
            }
        } else if (mep.equalsIgnoreCase(Constants.TWO_WAY_PUSH_AND_PULL)) {
            if (ln == 1) {
                if ((leg != null) && (leg.getReceiptReply() != null) &&
                        "Response".equalsIgnoreCase(leg.getReceiptReply())) {
                    return this.inOut(mis);
                } else if ((leg != null) && (leg.getReliability() != null)) {
                    return this.inOut(mis);
                } else {
                    this.inOnly(mis);
                }
                return null;
            } else if (ln == 2) {
                if (callback == null) {
                    return this.inOut(mis);
                } else {
                    this.inOut(mis, callback);
                    return null;
                }
            } else if (ln == 3) {
                this.inOnly(mis);
                return null;
            }
        } else if (mep.equalsIgnoreCase(Constants.TWO_WAY_PULL_AND_PUSH)) {
            if (ln == 1) {
                if (callback == null) {
                    return this.inOut(mis);
                } else {
                    this.inOut(mis, callback);
                    return null;
                }
            } else if (ln == 2) {
                this.inOnly(mis);
                return null;
            } else if (ln == 3) {
                if ((leg != null) && (leg.getReceiptReply() != null) &&
                        "Response".equalsIgnoreCase(leg.getReceiptReply())) {
                    return this.inOut(mis);
                } else if ((leg != null) && (leg.getReliability() != null)) {
                    return this.inOut(mis);
                } else {
                    this.inOnly(mis);
                }
                return null;
            }
        } else if (mep.equalsIgnoreCase(Constants.TWO_WAY_PULL_AND_Pull)) {
            if ((ln == 1) || (ln == 3)) {
                if (callback == null) {
                    return this.inOut(mis);
                } else {
                    this.inOut(mis, callback);
                    return null;
                }
            } else if (ln == 2) {
                this.inOnly(mis);
                return null;
            } else if (ln == 4) {
                this.inOnly(mis);
                return null;
            }
        }
        return null;
    }
}