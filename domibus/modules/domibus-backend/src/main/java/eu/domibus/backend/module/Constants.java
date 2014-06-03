/*
 * 
 */
package eu.domibus.backend.module;

import org.apache.axis2.description.AxisModule;
import eu.domibus.common.util.JNDIUtil;

/**
 * The Class Constants.
 */
public class Constants {

    /**
     * The Constant MESSAGES_FOLDER_PROPERTY_KEY.
     */
    public static final String MESSAGES_FOLDER_PROPERTY_KEY = "domibus.module.backend.messagesFolder";

    /**
     * The Constant MESSAGES_TIME_LIVE_PROPERTY_KEY.
     */
    public static final String MESSAGES_TIME_LIVE_PROPERTY_KEY = "domibus.module.backend.messagesTimeLive";

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
     * The Constant METADATA_FILE_NAME.
     */
    public static final String METADATA_FILE_NAME = "metadata.xml";

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
    public static final String CID_MESSAGE_FORMAT = "{0}@" + ECODEX_DOMAIN;

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
        return JNDIUtil.getStringEnvironmentParameter(MESSAGES_FOLDER_PROPERTY_KEY);

    }

    /**
     * Gets the messages time live.
     *
     * @return the messages time live
     */
    public static int getMessagesTimeLive() {
        return Integer.parseInt(JNDIUtil.getStringEnvironmentParameter(MESSAGES_TIME_LIVE_PROPERTY_KEY));
    }

    /**
     * Gets the delete messages cron.
     *
     * @return the delete messages cron
     */
    public static String getDeleteMessagesCron() {

        return JNDIUtil.getStringEnvironmentParameter(DELETE_MESSAGES_CRON_PROPERTY_KEY);

    }

    /**
     * Gets the domibus persistence properties.
     *
     * @return the domibus persistence properties
     */
    public static String getDomibusPersistenceProperties() {

        return JNDIUtil.getStringEnvironmentParameter(eu.domibus.common.Constants.DOMIBUS_PERSISTENCE_PROPERTIES);

    }
}