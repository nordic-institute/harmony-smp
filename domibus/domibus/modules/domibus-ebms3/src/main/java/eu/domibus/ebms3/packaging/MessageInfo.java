package eu.domibus.ebms3.packaging;

import eu.domibus.common.soap.Element;
import eu.domibus.common.util.DateUtil;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.module.EbUtil;
import eu.domibus.ebms3.submit.MsgInfoSet;
import org.apache.axiom.util.UIDGenerator;

import java.util.Date;

/**
 * @author Hamid Ben Malek
 */
public class MessageInfo extends Element {
    private static final long serialVersionUID = -2001342282242828965L;

    public MessageInfo(final String messageId, final String refToMessageId) {
        this(null, messageId, refToMessageId);
    }

    public MessageInfo(final Date time, final String messageId, final String refToMessageId) {
        super(Constants.MESSAGE_INFO, Constants.NS, Constants.PREFIX);
        final Element timestamp = this.addElement(Constants.TIMESTAMP, Constants.PREFIX);
        timestamp.setText((time != null) ? DateUtil.dateToUtc(time) : DateUtil.dateToUtc(new Date()));

        final Element msgId = this.addElement(Constants.MESSAGE_ID, Constants.PREFIX);
        msgId.setText((messageId != null) ? messageId : EbUtil.generateMessageID());

        if ((refToMessageId != null) && !refToMessageId.trim().isEmpty()) {
            final Element refMsgId = this.addElement(Constants.REF_TO_MESSAGE_ID, Constants.PREFIX);
            refMsgId.setText(refToMessageId);
        }
    }

    public MessageInfo(final MsgInfoSet mis) {
        this(new Date(mis.getTimeInMillis()), mis.getMessageId(), mis.getRefToMessageId());
    }

    //  =========================== Getter methods ===============================

    public String getMessageId() {
        return this.getGrandChildValue(Constants.MESSAGE_ID);
    }

    public String getRefToMessageId() {
        return this.getGrandChildValue(Constants.REF_TO_MESSAGE_ID);
    }

    public String getTimestamp() {
        return this.getGrandChildValue(Constants.TIMESTAMP);
    }
}