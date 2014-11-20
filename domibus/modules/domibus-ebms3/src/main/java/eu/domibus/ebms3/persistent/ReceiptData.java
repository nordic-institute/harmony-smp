package eu.domibus.ebms3.persistent;

import eu.domibus.common.persistent.AbstractBaseEntity;
import eu.domibus.common.soap.Element;
import eu.domibus.common.util.XMLUtil;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.packaging.PackagingFactory;
import eu.domibus.ebms3.packaging.Receipt;
import eu.domibus.ebms3.packaging.SignalMessage;
import org.apache.axiom.om.OMElement;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * This class represent a Receipt that should be sent back to the Sender of
 * the Usermessage.
 * <p/>
 * When the Usermessage is received as a result of a pull the Receipt needs
 * to be sent on the next request with the same P-Mode. Therefore it must be
 * stored in the database.
 *
 * @author Sander Fieten
 * @author Hamid Ben Malek
 */
@Entity
@Table(name = "TB_RECEIPT")
@NamedQueries({@NamedQuery(name = "ReceiptData.getUnsentCallbackReceiptData",
                           query = "SELECT c FROM ReceiptData c WHERE c.sent = FALSE AND c.failed = FALSE AND c.replyPattern = 'Callback' ORDER BY c.timestamp ASC"),
               @NamedQuery(name = "ReceiptData.getNextReceiptDataForPmode",
                           query = "SELECT c FROM ReceiptData c WHERE c.sent = FALSE AND c.failed = FALSE AND pmode = :PMODE ORDER BY c.timestamp ASC"),
               @NamedQuery(name = "ReceiptData.findAllForMessageId",
                           query = "SELECT r from ReceiptData r WHERE r.refToMessageId = :MESSAGE_ID")})

public class ReceiptData extends AbstractBaseEntity implements Serializable {
    private static final long serialVersionUID = 7109238203948576152L;
    @Column(name = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date timestamp;
    @Column(name = "MESSAGE_ID")
    protected String messageId;
    @Column(name = "REF_TO_MESSAGE_ID")
    protected String refToMessageId;
    @Column(name = "PMODE")
    protected String pmode;
    @Column(name = "REPLY_PATTERN", length = 8)
    protected String replyPattern;
    @Lob
    @Column(name = "RECEIPT_CONTENT", length = 9999999)
    @Basic(fetch = FetchType.EAGER)
    protected String receiptContent;
    @Column(name = "TO_URL")
    protected String toURL;
    @Column(name = "SENT")
    protected boolean sent;

    public boolean isFailed() {
        return this.failed;
    }

    public void setFailed(final boolean failed) {
        this.failed = failed;
    }

    @Column(name = "FAILED")
    protected boolean failed;
    @Transient
    protected OMElement content;


    public ReceiptData() {
        this.timestamp = new Date();
    }


    private ReceiptData(final String refToMessageId) {
        this();
        this.refToMessageId = refToMessageId;
    }

    /**
     * Generate a NRR (non-repudiation of receipt) receipt.
     * <p/>
     * <p>This is a receipt used for Non Repudation of Receipt, so it will contain
     * the ebpp NonRepudiationInformation element (see also section 5.1.8 of the AS4 profile)</p>
     * <p/>
     * <p>The references parameter should contain the list of elements to include</p>
     *
     * @param refToMessageId             Message identifier of the user message
     * @param signatureReferenceElements ds:Signature/ds:Reference elements (will be cloned)
     */
    public ReceiptData(final String refToMessageId, final List<OMElement> signatureReferenceElements) {
        this(refToMessageId);
        this.content = new Element(Constants.NON_REPUDIATION_INFORMATION, Constants.ebbpNS, Constants.ebbp_PREFIX)
                .getElement();
        for (final OMElement signatureReferenceElement : signatureReferenceElements) {
            final Element messagePartNRInformation =
                    new Element(Constants.MESSAGE_PART_NR_INFORMATION, Constants.ebbpNS, Constants.ebbp_PREFIX);
            messagePartNRInformation.addChild(signatureReferenceElement.cloneOMElement());
            this.content.addChild(messagePartNRInformation.getElement());
        }
        this.receiptContent = XMLUtil.toString(this.content);
    }


    /**
     * Generate a RA (reception awareness) receipt.
     * <p/>
     * <p>This receipt is only used as an acknowledgement of receipt so it will contain
     * a copy of the eb:UserMessage element from the received message (see also section 5.1.8 of
     * the AS4 profile)</p>
     *
     * @param refToMessageId Message identifier of the user message
     * @param userMessage    the eb:Messaging/eb:UserMessage element (will be cloned)
     */
    public ReceiptData(final String refToMessageId, final OMElement userMessage) {
        this(refToMessageId);
        this.content = userMessage.cloneOMElement();
        this.receiptContent = XMLUtil.toString(this.content);
    }

    public String getRefToMessageId() {
        return this.refToMessageId;
    }

    public void setRefToMessageId(final String refToMessageId) {
        this.refToMessageId = refToMessageId;
    }

    public String getToURL() {
        return this.toURL;
    }

    public void setToURL(final String toURL) {
        this.toURL = toURL;
    }

    public boolean isSent() {
        return this.sent;
    }

    public void setSent(final boolean sent) {
        this.sent = sent;
    }

    public SignalMessage getSignalMessage() {
        return PackagingFactory.createReceipt(this.timestamp, this.refToMessageId, this.getReceiptContent());
    }

    public Receipt getReceipt() {
        return new Receipt(this.getReceiptContent());
    }

    public OMElement getReceiptContent() {
        if (this.content != null) {
            return this.content;
        }
        if ((this.receiptContent == null) || "".equals(this.receiptContent.trim())) {
            return null;
        }
        this.content = XMLUtil.toOMElement(this.receiptContent);
        return this.content;
    }

    public String getPmode() {
        return this.pmode;
    }

    public void setPmode(final String pmode) {
        this.pmode = pmode;
    }

    public String getReplyPattern() {
        return this.replyPattern;
    }

    public void setReplyPattern(final String replyPattern) {
        this.replyPattern = replyPattern;
    }

    @Transient
    public boolean isReplyPatternResponse() {
        return (this.replyPattern != null) && this.replyPattern.equalsIgnoreCase(Constants.RESPONSE_REPLY_PATTERN_NAME);
    }

    @Transient
    public boolean isReplyPatternCallback() {
        return (this.replyPattern != null) && this.replyPattern.equalsIgnoreCase(Constants.CALLBACK_REPLY_PATTERN_NAME);
    }

    @Transient
    public void setReplyPatternCallback() {
        this.replyPattern = Constants.CALLBACK_REPLY_PATTERN_NAME;
    }

    @Transient
    public void setReplyPatternResponse() {
        this.replyPattern = Constants.RESPONSE_REPLY_PATTERN_NAME;
    }

}