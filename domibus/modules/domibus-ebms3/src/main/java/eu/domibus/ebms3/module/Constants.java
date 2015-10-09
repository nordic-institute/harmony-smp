package eu.domibus.ebms3.module;

import eu.domibus.common.util.JNDIUtil;
import eu.domibus.ebms3.config.PMode;
import eu.domibus.ebms3.workers.WorkerPool;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisModule;
import org.apache.axis2.description.Parameter;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

//import eu.domibus.ebms3.pmodes.PMode;
//import eu.domibus.ebms3.config.PMode;

/**
 * @author Hamid Ben Malek
 */
public class Constants {

    private static final Logger log = Logger.getLogger(Constants.class);

    public static final String RESPONSE_REPLY_PATTERN_NAME = "Response";
    public static final String CALLBACK_REPLY_PATTERN_NAME = "Callback";
    public static final String DEFAULT_MPC = "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/defaultMPC";
    public static final String PREFIX = "eb";
    public static final String MESSAGING = "Messaging";
    public static final String USER_MESSAGE = "UserMessage";
    public static final String MESSAGE_INFO = "MessageInfo";
    public static final String TIMESTAMP = "Timestamp";
    public static final String MESSAGE_ID = "MessageId";
    public static final String REF_TO_MESSAGE_ID = "RefToMessageId";
    public static final String PARTY_INFO = "PartyInfo";
    public static final String FROM = "From";
    public static final String PARTY_ID = "PartyId";
    public static final String PARTY_ID_TYPE = "type";
    public static final String ROLE = "Role";
    public static final String TO = "To";
    public static final String COLLABORATION_INFO = "CollaborationInfo";
    public static final String AGREEMENT_REF = "AgreementRef";
    public static final String AGREEMENT_REF_PMODE = "pmode";
    public static final String SERVICE = "Service";
    public static final String ACTION = "Action";
    public static final String MPC = "mpc";
    public static final String CONVERSATION_ID = "ConversationId";
    public static final String MESSAGE_PROPERTIES = "MessageProperties";
    public static final String PROPERTY = "Property";
    public static final String PROPERTY_NAME = "name";
    public static final String PAYLOAD_INFO = "PayloadInfo";
    public static final String PART_INFO = "PartInfo";
    public static final String PART_INFO_HREF = "href";
    public static final String PART_INFO_SCHEMA = "Schema";
    public static final String PART_INFO_SCHEMA_LOCATION = "location";
    public static final String PART_INFO_DESCR = "Description";
    public static final String PART_INFO_PART_PROPERTIES = "PartProperties";
    public static final String SIGNAL_MESSAGE = "SignalMessage";
    public static final String ERROR = "Error";
    public static final String PULL_REQUEST = "PullRequest";
    public static final String RECEIPT = "Receipt";
    public static final String NON_REPUDIATION_INFORMATION = "NonRepudiationInformation";
    public static final String MESSAGE_PART_NR_INFORMATION = "MessagePartNRInformation";
    public static final String MESSAGE_PART_IDENTIFIER = "MessagePartIdentifier";
    public static final String ebbp_PREFIX = "ebbp";
    public static final String dsigNS = "http://www.w3.org/2000/09/xmldsig#";
    public static final String IN_PULL_REQUEST = "IN_PULL_REQUEST";
    public static final String OUT_PULL_REQUEST = "OUT_PULL_REQUEST";
    public static final String IN_MESSAGING = "IN_MESSAGING";
    public static final String TO_ADDRESS = "TO_ADDRESS";
    public static final String IN_MSG_INFO = "IN_MSG_INFO";
    public static final String DO_NOT_DELIVER = "DO_NOT_DELIVER";
    public static final String MESSAGE_INFO_SET = "MESSAGE_INFO_SET";
    public static final String MODULES = "MODULES";
    public static final String GATEWAY_CONFIG_LOCATION_PARAMETER = "domibus.module.ebms3.gatewayConfigFile";
    // This parameter holds the received soap header at the server side:
    public static final String IN_SOAP_HEADER = "IN_SOAP_HEADER";
    // This parameter holds the PMode of the received msg at the server side:
    public static final String IN_PMODE = "IN_PMODE";
    // This parameter holds the Leg of the received msg at the server side:
    public static final String IN_LEG = "IN_LEG";
    public static final String IS_USERMESSAGE = "IS_EBMS_USERMESSAGE";
    public static final String RECEIPT_TO = "RECEIPT_TO";
    public static final String SUBMITTED_MESSAGES_FOLDER_PARAMETER = "domibus.module.ebms3.submittedMessagesFolder";
    public static final String RECEIVED_MESSAGES_FOLDER_PARAMETER = "domibus.module.ebms3.receivedMessagesFolder";
    public static final String HOSTNAMES_PARAMETER = "domibus.module.ebms3.hostnames";
    public static final String PMODES_DIR_PARAMETER = "domibus.module.ebms3.PModesDir";
    public static final String GATEWAY_CONFIG_FILE_PARAMETER = "domibus.module.ebms3.gatewayConfigFile";
    public static final String ATTACHMENT_FOLDER_PARAMETER = "domibus.module.ebms3.attachmentFolder";
    public static final String WORKERS_FILE_PARAMETER = "domibus.module.ebms3.workersFile";
    public static final String ENFORCE_1_3_COMPATIBILITY = "domibus.module.ebms3.enforce.1_3.compatibility";
    public static final String MASTER_INSTANCE = "domibus.cluster.masterinstance.name";
    public static final String INSTANCE_NAME_PROPERY = "domibus.cluster.instance.name.property";
    public static final String ENABLE_WHITE_LIST = "domibus.module.ebms3.whitelist";
    public static final String COMPRESSION_PROPERTY_NAME = "CompressionType";
    public static final String COMPRESSION_GZIP_MIMETYPE = "application/gzip";
    public static final String MIMETYPE_PROPERTY_NAME = "MimeType";
    public static final String CHARACTERSET_PROPERTY_NAME = "CharacterSet";
    public static final String MESSAGE_ID_PREFIX = "@e-codex.eu";


    // Simple MEPs:
    public static String ONE_WAY_PULL = "One-Way/Pull";
    public static String ONE_WAY_PUSH = "One-Way/Push";
    public static String TWO_WAY_SYNC = "Two-Way/Sync";
    public static String TWO_WAY_PUSH_AND_PUSH = "Two-Way/Push-And-Push";
    public static String TWO_WAY_PUSH_AND_PULL = "Two-Way/Push-And-Pull";
    public static String TWO_WAY_PULL_AND_PUSH = "Two-Way/Pull-And-Push";
    public static String TWO_WAY_PULL_AND_Pull = "Two-Way/Pull-And-Pull";
    public static String NS = "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/";
    public static String ebbpNS = "http://docs.oasis-open.org/ebxml-bp/ebbp-2.0";
    public static AxisModule module;
    public static String[] engagedModules = new String[]{"domibus-ebms3"};
    public static ConfigurationContext configContext;
    public static Map<String, PMode> pmodes;
    public static WorkerPool workerPool;
    public static File workersFile;

    public static final String ECODEX_SERVICE_URI_VALUE = "urn:e-codex:services:";
    public static final String SERVICE_MESSAGE_FORMAT = Constants.ECODEX_SERVICE_URI_VALUE + "{0}";
    public static final String ECODEX_PARTY_ID_URI_VALUE = "urn:e-codex:parties:";
    public static final String PARTY_ID_MESSAGE_FORMAT = Constants.ECODEX_PARTY_ID_URI_VALUE + "{0}";

    public static void setAxisModule(final AxisModule m) {
        Constants.module = m;
    }

    public static String getSubmitFolder() {

        return JNDIUtil.getStringEnvironmentParameter(Constants.SUBMITTED_MESSAGES_FOLDER_PARAMETER);
    }

    public static String getReceivedFolder() {
        return JNDIUtil.getStringEnvironmentParameter(Constants.RECEIVED_MESSAGES_FOLDER_PARAMETER);
    }

    public static String getAttachmentDir() {

        final Parameter attachDirParam = Constants.configContext.getAxisConfiguration().getParameter(
                org.apache.axis2.Constants.Configuration.ATTACHMENT_TEMP_DIR);

        final String attachmentFolder = JNDIUtil.getStringEnvironmentParameter(Constants.ATTACHMENT_FOLDER_PARAMETER);
        final File sub = new File(attachmentFolder);
        if (!sub.exists()) {
            sub.mkdirs();
        }
        if (attachDirParam != null) {
            attachDirParam.setValue(attachmentFolder);
        } else {
            final Parameter p =
                    new Parameter(org.apache.axis2.Constants.Configuration.ATTACHMENT_TEMP_DIR, attachmentFolder);
            try {
                Constants.configContext.getAxisConfiguration().addParameter(p);
            } catch (AxisFault e) {
                Constants.log.error("Error while adding Parameter to AxisConfiguration", e);
            }
        }
        return attachmentFolder;
    }

    public static File getWorkersFile() {
        if (Constants.workersFile != null) {
            return Constants.workersFile;
        }
        final String workersName = JNDIUtil.getStringEnvironmentParameter(Constants.WORKERS_FILE_PARAMETER);
        final File workersFile = new File(workersName);
        if (!workersFile.exists()) {
            throw new RuntimeException(
                    new FileNotFoundException("could not find workers file at " + workersFile.getAbsolutePath()));
        }
        return workersFile;
    }

    public static boolean getProperty(final MessageContext msgCtx, final String propertyIdentifier,
                                      final boolean defaultValue) {
        final Object property = msgCtx.getProperty(propertyIdentifier);
        if (!(property instanceof Boolean)) {
            return defaultValue;
        }
        return ((Boolean) property);
    }


}