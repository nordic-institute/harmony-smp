package eu.domibus.ebms3.workers.impl;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.log4j.Logger;
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
    private static final Logger log = Logger.getLogger(ReceiptSender.class.getName());
    private final ReceiptDataDAO rdd = new ReceiptDataDAO();

    public void run() {
        final List<ReceiptData> receiptDataList = rdd.getUnsentCallbackReceiptData();
        for (final ReceiptData receiptData : receiptDataList) {

            if (!doSendReceipt(receiptData)) {
                continue;
            }

            final String toURL = receiptData.getToURL();
            if (toURL == null || toURL.isEmpty()) {
                log.error("Cannot send Callback receipt to empty URL for messageId=" + receiptData.getRefToMessageId());
                receiptData.setFailed(true);
                rdd.update(receiptData);
                continue;
            }
            log.info("About to send an AS4 receipt as a callback to " + receiptData.getToURL());

            final EbMessage message = new EbMessage(Constants.configContext);
            try {
                switchPMode(receiptData);
            } catch (ConfigurationException ce) {
                //TODO: Kein PMode zum zuruecksenden der receipt gefunden. uebertragung als endgueltig gescheitert markieren
            }
            addReceiptHeader(message, receiptData);


            PMode usedPMode = Constants.pmodes.get(receiptData.getPmode());
            if (usedPMode == null) {
                log.error("No PMode with name: " + receiptData.getPmode() + " found");
                throw new ConfigurationException("No PMode with name: " + receiptData.getPmode() + " found");
            }

            Leg theFirstLeg = usedPMode.getLeg(1);
            if (theFirstLeg == null) {
                log.error("No Leg in PMode with name: " + receiptData.getPmode() + " found");
                throw new ConfigurationException("No Leg in PMode with name: " + receiptData.getPmode() + " found");
            }

            String soapAction = theFirstLeg.getSoapAction();
            if (soapAction == null || "".equals(soapAction)) {
                theFirstLeg.getWsaAction();
            }

            MsgInfoSet msgInfoSet = new MsgInfoSet();
            msgInfoSet.setPmode(usedPMode.getName());
            message.insertMsgInfoSet(msgInfoSet);

            message.inOnly(receiptData.getToURL(), soapAction, Constants.engagedModules);
            log.info("AS4 Callback receipt was sent to " + receiptData.getToURL());
            XMLUtil.debug(log, message.getEnvelope());

            receiptData.setSent(true);
            rdd.update(receiptData);
            log.info("Marked receipt as sent for messageId=" + receiptData.getRefToMessageId());
        }
    }


    /**
     * Switch the PMode to a available corresponding pmode for this message. The decision is based up on the receiever
     * pmode information contained in receiptdata and is switched to the corresponding sending pmode inside this method
     *
     * @param receiptData containing the receiving pmode information
     */
    private void switchPMode(ReceiptData receiptData) {
        try {
            final String correspondingPMode = Configuration.getCorrespondingReplyPMode(receiptData.getPmode());

            receiptData.setPmode(correspondingPMode);

        } catch (ConfigurationException ce) {
            log.error("Error during PMode discovery process for callback receipt");
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
        if (deliverySemantics == null || deliverySemantics.equalsIgnoreCase("Received")) {
            // Send a receipt right after reception of the user message.
            // This is the default behavior.
            return true;
        }
        if (deliverySemantics.equalsIgnoreCase("Downloaded")) {
            // Send a receipt only if the user message has already been downloaded
            //FIXME: move from DBStore
            // return Constants.store.checkForDownload(receiptData.getRefToMessageId());
        }

        log.error("Unknown deliverySemantics=" + deliverySemantics + " in PMode=" + pmodeName);
        return false;
    }

    private void addReceiptHeader(final EbMessage message, final ReceiptData receiptData) {
        SOAPEnvelope soapEnvelope = message.getEnvelope();
        if (soapEnvelope == null) {
            log.error("No SOAP-Envelope available");
            throw new NullPointerException("No SOAPEvenvelope available");
        }
        new Messaging(soapEnvelope);
        if (soapEnvelope.getHeader() == null) {
            log.error("No header available in SOAPEnvelope");
            throw new NullPointerException("No header available in SOAP-Envelope");
        }
        final OMElement messaging = XMLUtil.getGrandChildNameNS(soapEnvelope.getHeader(), Constants.MESSAGING, Constants.NS);
        //messaging.addChild( receiptData.getSignalMessage() );
        messaging.addChild(receiptData.getSignalMessage().getElement());
    }
}