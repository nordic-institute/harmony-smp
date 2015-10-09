/*
 * 
 */
package eu.eCODEX.submission.validation.impl;

import javax.activation.DataHandler;

import org.apache.log4j.Logger;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo;
import org.springframework.stereotype.Service;

import backend.ecodex.org._1_1.Code;
import backend.ecodex.org._1_1.DownloadMessageResponse;
import backend.ecodex.org._1_1.PayloadType;
import eu.domibus.backend.service._1_1.exception.SendMessageServiceException;
import eu.domibus.backend.util.IOUtils;
import eu.domibus.backend.util.StringUtils;
import eu.domibus.ebms3.config.PMode;
import eu.domibus.ebms3.config.UserService;
import eu.domibus.ebms3.module.Configuration;
import eu.eCODEX.submission.validation.Validator;
import eu.eCODEX.submission.validation.exception.ValidationException;
import eu.eCODEX.transport.dto.BackendMessageOut;

/**
 * The Class DownloadMessageValidator.
 */
@Service("DownloadMessageValidator_1_1")
public class DownloadMessageValidator implements Validator<BackendMessageOut> {

	/**
     * The Constant LOG.
     */
    private final static Logger LOG = Logger.getLogger(DownloadMessageValidator.class);
    
    @Override
	public void validate(BackendMessageOut message) throws ValidationException {
    	MessagingE messaging = message.getMessagingE();
    	DownloadMessageResponse response = message.getResponse();
    	final PMode pMode = this.validateMessaging(messaging);
        this.validateDownloadMessageResponse(response, pMode);
        this.validateHref(messaging, response);
	}

    /**
     * Validate DownloadMessageResponse.
     *
     * @param response the message reponse
     * @param pMode       the pMode
     * @throws eu.domibus.backend.service._1_1.exception.SendMessageServiceException the send message service exception
     */
    private void validateDownloadMessageResponse(final DownloadMessageResponse response, final PMode pMode)
            throws SendMessageServiceException {
        if (response == null) {
            DownloadMessageValidator.LOG.error("DownloadResponse is empty");

            final SendMessageServiceException sendMessageServiceException =
                    new SendMessageServiceException("DownloadResponse is empty", Code.ERROR_GENERAL_003);
            throw sendMessageServiceException;
        }

        if ((response.getBodyload() == null) || (response.getBodyload().getBase64Binary() == null)) {
            DownloadMessageValidator.LOG.error("Bodyload is empty");

            final SendMessageServiceException sendMessageServiceException =
                    new SendMessageServiceException("Bodyload is empty", Code.ERROR_GENERAL_003);
            throw sendMessageServiceException;
        }

        int counter = 1;

        if ((response.getPayload() != null) && (response.getPayload().length > 0)) {
            for (final PayloadType payloadType : response.getPayload()) {
                if ((payloadType.getPayloadId() == null) ||
                    StringUtils.isEmpty(payloadType.getPayloadId().toString())) {
                    DownloadMessageValidator.LOG.error("The PayloadId of payload[" + counter + "] is empty");

                    final SendMessageServiceException sendMessageServiceException =
                            new SendMessageServiceException("The PayloadId of payload[" + counter + "] is empty",
                                                            Code.ERROR_GENERAL_003);
                    throw sendMessageServiceException;
                }

                counter++;
            }
        }

        long payloadSize = 0;

        {
            final DataHandler dataHandler = response.getBodyload().getBase64Binary();
            payloadSize += IOUtils.getDataSize(dataHandler);
        }

        if ((response.getPayload() != null) && (response.getPayload().length > 0)) {
            for (final PayloadType payloadType : response.getPayload()) {
                final DataHandler dataHandler = payloadType.getBase64Binary();

                payloadSize += IOUtils.getDataSize(dataHandler);
            }
        }

        UserService userService = null;

        if ((pMode.getUserServices() != null) && !pMode.getUserServices().isEmpty()) {
            userService = pMode.getUserServices().get(0);
        } else {
            if ((pMode.getBinding() != null) && (pMode.getBinding().getMep() != null) &&
                (pMode.getBinding().getMep().getLegs() != null) && !pMode.getBinding().getMep().getLegs().isEmpty() &&
                StringUtils.isNotEmpty(pMode.getBinding().getMep().getLegs().get(0).getUserServiceName())) {
                userService = pMode.getUserService(pMode.getBinding().getMep().getLegs().get(0).getUserServiceName());
            }
        }

        if (userService != null) {
            if ((userService.getPayloadsSize() > 0) && (payloadSize > userService.getPayloadsSize())) {
                DownloadMessageValidator.LOG.error("Payloads are too big");

                final SendMessageServiceException sendMessageServiceException =
                        new SendMessageServiceException("Payloads are too big", Code.ERROR_SEND_005);
                throw sendMessageServiceException;
            }
        } else {
            DownloadMessageValidator.LOG
                    .error("Cannot find the appropiate userService. So It cannot be validated the payloadsSize");
        }
    }

    /**
     * Validate messaging.
     *
     * @param messaging the messaging
     * @throws eu.domibus.backend.service._1_1.exception.SendMessageServiceException the send message service exception
     */
    private PMode validateMessaging(final MessagingE messaging) throws SendMessageServiceException {
        if (messaging == null) {
            DownloadMessageValidator.LOG.error("Messaging is empty");

            final SendMessageServiceException sendMessageServiceException =
                    new SendMessageServiceException("Messaging is empty", Code.ERROR_GENERAL_002);
            throw sendMessageServiceException;
        }

        if ((messaging.getMessaging().getUserMessage() == null) ||
            (messaging.getMessaging().getUserMessage().length == 0)) {
            DownloadMessageValidator.LOG.error("UserMessage is empty");

            final SendMessageServiceException sendMessageServiceException =
                    new SendMessageServiceException("UserMessage is empty", Code.ERROR_GENERAL_002);
            throw sendMessageServiceException;
        }

        if (messaging.getMessaging().getUserMessage()[0].getCollaborationInfo() == null) {
            DownloadMessageValidator.LOG.error("CollaborationInfo is empty");

            final SendMessageServiceException sendMessageServiceException =
                    new SendMessageServiceException("CollaborationInfo is empty", Code.ERROR_GENERAL_002);
            throw sendMessageServiceException;
        }

        if ((messaging.getMessaging().getUserMessage()[0].getPartyInfo() == null) ||
            (messaging.getMessaging().getUserMessage()[0].getPartyInfo().getTo() == null) ||
            (messaging.getMessaging().getUserMessage()[0].getPartyInfo().getTo().getPartyId() == null) ||
            (messaging.getMessaging().getUserMessage()[0].getPartyInfo().getTo().getPartyId().length == 0) ||
            StringUtils.isEmpty(messaging.getMessaging().getUserMessage()[0].getPartyInfo().getTo().getPartyId()[0]
                                        .getNonEmptyString())

            || (messaging.getMessaging().getUserMessage()[0].getPartyInfo().getFrom() == null) ||
            (messaging.getMessaging().getUserMessage()[0].getPartyInfo().getFrom().getPartyId() == null) ||
            (messaging.getMessaging().getUserMessage()[0].getPartyInfo().getFrom().getPartyId().length == 0) ||
            StringUtils.isEmpty(messaging.getMessaging().getUserMessage()[0].getPartyInfo().getFrom().getPartyId()[0]
                                        .getNonEmptyString())

            || (messaging.getMessaging().getUserMessage()[0].getCollaborationInfo().getService() == null) || StringUtils
                .isEmpty(messaging.getMessaging().getUserMessage()[0].getCollaborationInfo().getService()
                                                                     .getNonEmptyString()) ||
            (messaging.getMessaging().getUserMessage()[0].getCollaborationInfo().getAction() == null) || StringUtils
                .isEmpty(messaging.getMessaging().getUserMessage()[0].getCollaborationInfo().getAction().toString())) {
            DownloadMessageValidator.LOG
                    .error("The parameterers needed to find an appropiate pmode are missing or invalid");

            final SendMessageServiceException sendMessageServiceException = new SendMessageServiceException(
                    "The parameterers needed to find an appropiate pmode are missing or invalid",
                    Code.ERROR_GENERAL_002);
            throw sendMessageServiceException;
        }

        final String action =
                messaging.getMessaging().getUserMessage()[0].getCollaborationInfo().getAction().toString();
        final String fromPartyid = messaging.getMessaging().getUserMessage()[0].getPartyInfo().getFrom().getPartyId()[0]
                .getNonEmptyString();

        String fromPartyidType = null;
        if (messaging.getMessaging().getUserMessage()[0].getPartyInfo().getFrom().getPartyId()[0].getType() != null) {
            DownloadMessageValidator.LOG
                    .trace(messaging.getMessaging().getUserMessage()[0].getPartyInfo().getFrom().getPartyId()[0]
                                   .getType().getNonEmptyString());
            fromPartyidType =
                    messaging.getMessaging().getUserMessage()[0].getPartyInfo().getFrom().getPartyId()[0].getType()
                                                                                                         .getNonEmptyString();
        }

        final String toPartyid =
                messaging.getMessaging().getUserMessage()[0].getPartyInfo().getTo().getPartyId()[0].getNonEmptyString();

        String toPartyidType = null;
        if (messaging.getMessaging().getUserMessage()[0].getPartyInfo().getTo().getPartyId()[0].getType() != null) {
            DownloadMessageValidator.LOG
                    .trace(messaging.getMessaging().getUserMessage()[0].getPartyInfo().getTo().getPartyId()[0].getType()
                                                                                                              .getNonEmptyString());
            toPartyidType =
                    messaging.getMessaging().getUserMessage()[0].getPartyInfo().getTo().getPartyId()[0].getType()
                                                                                                       .getNonEmptyString();
            ;
        }

        DownloadMessageValidator.LOG.debug("ToPartyIdType: " + toPartyidType);
        DownloadMessageValidator.LOG.debug("FromPartyIdType: " + fromPartyidType);
        final String service =
                messaging.getMessaging().getUserMessage()[0].getCollaborationInfo().getService().getNonEmptyString();

        final PMode pmode =
                Configuration.getPModeO(action, service, fromPartyid, fromPartyidType, toPartyid, toPartyidType);

        if (pmode == null) {
            DownloadMessageValidator.LOG.error("Cannot find the appropiate pmode, looking for: action=" + action +
                                           ", service=" + service +
                                           ", fromPartyId=" + fromPartyid + ", fromPartyIdType=" +
                                           fromPartyidType + ", toPartyId=" + toPartyid + ", toPartyIdType=" +
                                           toPartyidType);


            final SendMessageServiceException sendMessageServiceException =
                    new SendMessageServiceException("Cannot find the appropiate pmode", Code.ERROR_GENERAL_002);
            throw sendMessageServiceException;
        }

        return pmode;
    }

    /**
     * Validate hrefs.
     *
     * @param messaging the message
     * @param response the download message response
     * @throws eu.domibus.backend.service._1_1.exception.SendMessageServiceException the send message service exception
     */
    private void validateHref(final MessagingE messaging, final DownloadMessageResponse response)
            throws SendMessageServiceException {
        if ((((messaging.getMessaging().getUserMessage()[0].getPayloadInfo() == null) ||
              (messaging.getMessaging().getUserMessage()[0].getPayloadInfo().getPartInfo() == null) ||
              (messaging.getMessaging().getUserMessage()[0].getPayloadInfo().getPartInfo().length <= 1)) &&
             (response.getPayload() != null) && (response.getPayload().length > 0)) ||
            (((messaging.getMessaging().getUserMessage()[0].getPayloadInfo() != null) &&
              (messaging.getMessaging().getUserMessage()[0].getPayloadInfo().getPartInfo() != null) &&
              (messaging.getMessaging().getUserMessage()[0].getPayloadInfo().getPartInfo().length > 1)) &&
             ((response.getPayload() == null) || (response.getPayload().length == 0))) ||
            ((response.getPayload() != null) && (response.getPayload().length > 0) &&
             (response.getPayload().length !=
              (messaging.getMessaging().getUserMessage()[0].getPayloadInfo().getPartInfo().length - 1)))) {
            DownloadMessageValidator.LOG.error("EbmsPayload infos of header and body do not match");

            final SendMessageServiceException sendMessageServiceException =
                    new SendMessageServiceException("EbmsPayload infos of header and body do not match",
                                                    Code.ERROR_GENERAL_002);
            throw sendMessageServiceException;
        }

        if ((response.getPayload() != null) && (response.getPayload().length > 0)) {
            int counter = 1;
            for (final PayloadType payloadType : response.getPayload()) {
                boolean found = false;

                for (final PartInfo partInfo : messaging.getMessaging().getUserMessage()[0].getPayloadInfo()
                                                                                           .getPartInfo()) {
                    if (((payloadType.getPayloadId() != null) &&
                         StringUtils.isNotEmpty(payloadType.getPayloadId().toString()) &&
                         ((partInfo.getHref() != null) && StringUtils.isNotEmpty(partInfo.getHref().toString())))) {
                        String href = partInfo.getHref().toString();
                        if (href.toLowerCase().startsWith("cid:")) {
                            href = href.substring("cid:".length());
                        }

                        final String payloadId = payloadType.getPayloadId().toString();

                        if (payloadId.equalsIgnoreCase(href)) {
                            found = true;
                        }
                    }
                }

                if (!found) {
                    DownloadMessageValidator.LOG.error("The hrefs of the payload[" + counter + "] do not match");

                    final SendMessageServiceException sendMessageServiceException =
                            new SendMessageServiceException("The hrefs of the payload[" + counter + "] do not match",
                                                            Code.ERROR_GENERAL_002);
                    throw sendMessageServiceException;
                }

                counter++;
            }
        }
    }

	
}