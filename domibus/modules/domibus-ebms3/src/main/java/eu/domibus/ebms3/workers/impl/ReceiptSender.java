package eu.domibus.ebms3.workers.impl;

import eu.domibus.common.exceptions.ConfigurationException;
import eu.domibus.common.util.XMLUtil;
import eu.domibus.ebms3.config.As4Receipt;
import eu.domibus.ebms3.config.Leg;
import eu.domibus.ebms3.config.PMode;
import eu.domibus.ebms3.handlers.ReceiptAppender;
import eu.domibus.ebms3.module.AS4ReliabilityUtil;
import eu.domibus.ebms3.module.Configuration;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.packaging.Messaging;
import eu.domibus.ebms3.persistent.ReceiptData;
import eu.domibus.ebms3.persistent.ReceiptDataDAO;
import eu.domibus.ebms3.submit.EbMessage;
import eu.domibus.ebms3.submit.MsgInfoSet;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * This worker sends all unsent call-back receipts.
 * The {@code ReceiptAppender} handler sends response receipts.
 *
 * @author Hamid Ben Malek
 * @see ReceiptAppender
 */
public class ReceiptSender implements Runnable {
    //  private static final Log log = LogFactory.getLog(ReceiptSender.class.getName());
    private static final Logger LOG = Logger.getLogger(ReceiptSender.class.getName());
    private final ReceiptDataDAO rdd = new ReceiptDataDAO();

    public void run() {
        final List<ReceiptData> receiptDataList = this.rdd.getUnsentCallbackReceiptData();
        for (final ReceiptData receiptData : receiptDataList) {

            if (!this.doSendReceipt(receiptData)) {
                continue;
            }

            final String toURL = receiptData.getToURL();
            if ((toURL == null) || toURL.isEmpty()) {
                ReceiptSender.LOG.error("Cannot send Callback receipt to empty URL for messageId=" +
                                        receiptData.getRefToMessageId());
                receiptData.setFailed(true);
                this.rdd.update(receiptData);
                continue;
            }
            ReceiptSender.LOG.debug("About to send an AS4 receipt as a callback to " + receiptData.getToURL());

            final EbMessage message = new EbMessage(Constants.configContext);
            try {
                this.switchPMode(receiptData);
            } catch (ConfigurationException ce) {
                //TODO: Kein PMode zum zuruecksenden der receipt gefunden. uebertragung als endgueltig gescheitert markieren
            }
            this.addReceiptHeader(message, receiptData);


            final PMode usedPMode = Constants.pmodes.get(receiptData.getPmode());
            if (usedPMode == null) {
                ReceiptSender.LOG.error("No PMode with name: " + receiptData.getPmode() + " found");
                throw new ConfigurationException("No PMode with name: " + receiptData.getPmode() + " found");
            }

            final Leg theFirstLeg = usedPMode.getLeg(1);
            if (theFirstLeg == null) {
                ReceiptSender.LOG.error("No Leg in PMode with name: " + receiptData.getPmode() + " found");
                throw new ConfigurationException("No Leg in PMode with name: " + receiptData.getPmode() + " found");
            }

            final String soapAction = theFirstLeg.getSoapAction();
            if ((soapAction == null) || "".equals(soapAction)) {
                theFirstLeg.getWsaAction();
            }

            final MsgInfoSet msgInfoSet = new MsgInfoSet();
            msgInfoSet.setPmode(usedPMode.getName());
            message.insertMsgInfoSet(msgInfoSet);

            message.inOnly(receiptData.getToURL(), soapAction, Constants.engagedModules);
            ReceiptSender.LOG.debug("AS4 Callback receipt was sent to " + receiptData.getToURL());
            XMLUtil.debug(ReceiptSender.LOG, message.getEnvelope());

            receiptData.setSent(true);
            this.rdd.update(receiptData);
            ReceiptSender.LOG.debug("Marked receipt as sent for messageId=" + receiptData.getRefToMessageId());
        }
    }


    /**
     * Switch the PMode to a available corresponding pmode for this message. The decision is based up on the receiever
     * pmode information contained in receiptdata and is switched to the corresponding sending pmode inside this method
     *
     * @param receiptData containing the receiving pmode information
     */
    private void switchPMode(final ReceiptData receiptData) {
        try {
            final String correspondingPMode = Configuration.getCorrespondingReplyPMode(receiptData.getPmode());

            receiptData.setPmode(correspondingPMode);

        } catch (ConfigurationException ce) {
            ReceiptSender.LOG.error("Error during PMode discovery process for callback receipt");
            throw new ConfigurationException("Error during PMode discovery process for callback receipt", ce);
        }
    }

    /**
     * Decide whether to send a receipt depended on the delivery semantics
     * (received or downloaded).
     *
     * @param receiptData containing the PMode reference
     * @return {@code true} if a receipt shall be sent now
     */
    private boolean doSendReceipt(final ReceiptData receiptData) {
        final String pmodeName = receiptData.getPmode();
        if (pmodeName == null) {
            return false;
        }

        final PMode pmode = Constants.pmodes.get(pmodeName);
        if (pmode == null) {
            return false;
        }

        final As4Receipt as4Receipt = AS4ReliabilityUtil.getReceiptConfig(pmodeName);
        if (as4Receipt == null) {
            return false;
        }

        final String deliverySemantics = as4Receipt.getDeliverySemantics();
        if ((deliverySemantics == null) || "Received".equalsIgnoreCase(deliverySemantics)) {
            // Send a receipt right after reception of the user message.
            // This is the default behavior.
            return true;
        }
        if ("Downloaded".equalsIgnoreCase(deliverySemantics)) {
            // Send a receipt only if the user message has already been downloaded
            //FIXME: move from DBStore
            // return Constants.store.checkForDownload(receiptData.getRefToMessageId());
        }

        ReceiptSender.LOG.error("Unknown deliverySemantics=" + deliverySemantics + " in PMode=" + pmodeName);
        return false;
    }

    private void addReceiptHeader(final EbMessage message, final ReceiptData receiptData) {
        final SOAPEnvelope soapEnvelope = message.getEnvelope();
        if (soapEnvelope == null) {
            ReceiptSender.LOG.error("No SOAP-Envelope available");
            throw new NullPointerException("No SOAPEvenvelope available");
        }
        new Messaging(soapEnvelope);
        if (soapEnvelope.getHeader() == null) {
            ReceiptSender.LOG.error("No header available in SOAPEnvelope");
            throw new NullPointerException("No header available in SOAP-Envelope");
        }
        final OMElement messaging =
                XMLUtil.getGrandChildNameNS(soapEnvelope.getHeader(), Constants.MESSAGING, Constants.NS);
        //messaging.addChild( receiptData.getSignalMessage() );
        messaging.addChild(receiptData.getSignalMessage().getElement());
    }
}