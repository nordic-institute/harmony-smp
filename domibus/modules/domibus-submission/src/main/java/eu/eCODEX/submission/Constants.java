package eu.eCODEX.submission;

import eu.domibus.common.util.JNDIUtil;
import org.apache.axis2.description.AxisModule;

/**
 * The Class Constants.
 */
public class Constants {
    public static final String SUBMISSION_WEBSERVICE_FOLDER = "domibus.module.submission.webserviceFolder";

    public static final String DOMIBUS_SUBMISSION_DOWNLOADED_MESSAGES_TIME_TO_LIVE =
            "domibus.module.submission.downloadedMessagesTimeToLive";
    public static final String SUBMISSION_JMS_MAPMESSAGE_ACTION = "Action";
    public static final String SUBMISSION_JMS_MAPMESSAGE_SERVICE = "Service";
    public static final String SUBMISSION_JMS_MAPMESSAGE_SERVICE_TYPE = "serviceType";

    public static final String SUBMISSION_JMS_DIR = "ffmq.system.home";

    /**
     * The Constant MESSAGES_FOLDER_PROPERTY_KEY.
     */
    public static final String MESSAGES_FOLDER_PROPERTY_KEY = "domibus.module.backend.messagesFolder";

    /**
     * The Constant MESSAGES_TIME_TO_LIVE_PROPERTY_KEY.
     */
    public static final String MESSAGES_TIME_TO_LIVE_PROPERTY_KEY = "domibus.module.backend.messagesTimeToLive";

    /**
     * The Constant DELETE_MESSAGES_CRON_PROPERTY_KEY.
     */
    public static final String DELETE_MESSAGES_CRON_PROPERTY_KEY = "domibus.module.backend.deleteMessagesCron";

    /**
     * The Constant DEFAULT_TIME_LIVE.
     */
    public static final int DEFAULT_TIME_LIVE = 60;// In days

    /**
     * The Constant PAYLOAD_FILE_NAME_FORMAT.
     */
    public static final String PAYLOAD_FILE_NAME_FORMAT = "payload_{0}.bin";

    /**
     * The Constant PAYLOAD_FILE_NAME_FORMAT.
     */
    public static final String BODYLOAD_FILE_NAME_FORMAT = "bodyload.bin";

    /**
     * The Constant MESSAGING_FILE_NAME.
     */
    public static final String MESSAGING_FILE_NAME = "messaging.xml";

    /**
     * The Constant METADATA_ARTIFACT_NAME.
     */
    public static final String METADATA_ARTIFACT_NAME = "metadata.xml";

    /**
     * The Constant PATTERN_DATE_FORMAT.
     */
    public static final String PATTERN_DATE_FORMAT = "yyyy_MM_dd__HH_mm_ss_SSS";

    /**
     * The Constant BINARY_MIME_TYPE.
     */
    public static final String BINARY_MIME_TYPE = "application/octet-stream";

    /**
     * The Constant ECODEX_DOMAIN.
     */
    public static final String ECODEX_DOMAIN = "ecodex.eu";

    /**
     * The Constant PAYLOAD_FILE_NAME_FORMAT.
     */
    public static final String XML_MYMETYPE = "text/xml";

    /**
     * The Constant ENDPOINT_ADDRESS_MESSAGE_PROPERTY.
     */
    public static final String ENDPOINT_ADDRESS_MESSAGE_PROPERTY = "EndpointAddress";

    /**
     * The Constant CID.
     */
    public static final String CID = "cid";

    /**
     * The Constant CID_MESSAGE_FORMAT.
     */
    //	public static final String CID_MESSAGE_FORMAT = CID + ":{0}@" + ECODEX_DOMAIN;
    public static final String CID_MESSAGE_FORMAT = "{0}@" + Constants.ECODEX_DOMAIN;

    /**
     * The module.
     */
    public static AxisModule module;

    /**
     * Gets the persistence unit.
     *
     * @return the persistence unit
     */
    public static String getPersistenceUnit() {
        return JNDIUtil.getStringEnvironmentParameter(eu.domibus.common.Constants.PERSISTENCE_UNIT);
    }

    /**
     * Gets the messages folder.
     *
     * @return the messages folder
     */
    public static String getMessagesFolder() {
        return JNDIUtil.getStringEnvironmentParameter(Constants.MESSAGES_FOLDER_PROPERTY_KEY);

    }

    /**
     * Gets the delete messages cron.
     *
     * @return the delete messages cron
     */
    public static String getDeleteMessagesCron() {

        return JNDIUtil.getStringEnvironmentParameter(Constants.DELETE_MESSAGES_CRON_PROPERTY_KEY);

    }

    /**
     * Gets the domibus persistence properties.
     *
     * @return the domibus persistence properties
     */
    public static String getHolodeckPersistenceProperties() {

        return JNDIUtil.getStringEnvironmentParameter(eu.domibus.common.Constants.DOMIBUS_PERSISTENCE_PROPERTIES);

    }
    public static final String SUBMISSION_JMS_MAPMESSAGE_CONVERSATION_ID = "ConversationID";
    public static final String SUBMISSION_JMS_MAPMESSAGE_AGREEMENT_REF = "AgreementRef";
    public static final String SUBMISSION_JMS_MAPMESSAGE_REF_TO_MESSAGE_ID = "refToMessageId";

    public static final String SUBMISSION_JMS_MAPMESSAGE_FROM_PARTY_ID = "fromPartyID";
    public static final String SUBMISSION_JMS_MAPMESSAGE_FROM_PARTY_TYPE = "fromPartyType";
    public static final String SUBMISSION_JMS_MAPMESSAGE_FROM_ROLE = "fromRole";

    public static final String SUBMISSION_JMS_MAPMESSAGE_TO_PARTY_ID = "toPartyID";
    public static final String SUBMISSION_JMS_MAPMESSAGE_TO_PARTY_TYPE = "toPartyType";
    public static final String SUBMISSION_JMS_MAPMESSAGE_TO_ROLE = "toRole";

    public static final String SUBMISSION_JMS_MAPMESSAGE_PROPERTY_ORIGINAL_SENDER = "originalSender";
    public static final String SUBMISSION_JMS_MAPMESSAGE_PROPERTY_FINAL_RECIPIENT = "finalRecipient";
    public static final String SUBMISSION_JMS_MAPMESSAGE_PROPERTY_ENDPOINT = "endPointAddress";

    public static final String SUBMISSION_JMS_MAPMESSAGE_PROTOCOL = "protocol";
    public static final String SUBMISSION_JMS_MAPMESSAGE_TOTAL_NUMBER_OF_PAYLOADS = "totalNumberOfPayloads";

    private static final String SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_NAME_PREFIX = "payload-";
    private static final String SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_DESCRIPTION_SUFFIX = "-description";
    private static final String SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_MIME_TYPE_SUFFIX = "-MimeType";
    private static final String SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_MIME_CONTENT_ID_SUFFIX = "-MimeContentID";

    public static final String SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_NAME_FORMAT = SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_NAME_PREFIX + "{0}";
    public static final String SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_DESCRIPTION_FORMAT =  SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_NAME_FORMAT + SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_DESCRIPTION_SUFFIX;
    public static final String SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_MIME_TYPE_FORMAT =  SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_NAME_FORMAT + SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_MIME_TYPE_SUFFIX;
    public static final String SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_MIME_CONTENT_ID_FORMAT =  SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_NAME_FORMAT + SUBMISSION_JMS_MAPMESSAGE_PAYLOAD_MIME_CONTENT_ID_SUFFIX;


}
