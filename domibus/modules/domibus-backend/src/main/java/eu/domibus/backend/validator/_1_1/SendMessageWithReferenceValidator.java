/*
 * 
 */
package eu.domibus.backend.validator._1_1;

import backend.ecodex.org._1_1.Code;
import backend.ecodex.org._1_1.PayloadURLType;
import org.apache.log4j.Logger;
import eu.domibus.backend.service._1_1.exception.SendMessageServiceException;
import eu.domibus.backend.util.StringUtils;
import eu.domibus.ebms3.config.PMode;
import eu.domibus.ebms3.config.UserService;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * The Class SendMessageWithReferenceValidator.
 */
@Service("SendMessageWithReferenceValidator_1_1")
public class SendMessageWithReferenceValidator {

    /**
     * The Constant log.
     */
    private final static Logger log = Logger.getLogger(SendMessageWithReferenceValidator.class);

    /**
     * Validate.
     *
     * @param messaging      the messaging
     * @param sendRequestURL the send request url
     * @throws SendMessageServiceException the send message service exception
     */
    public void validate(final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE messaging,
                         final backend.ecodex.org._1_1.SendRequestURL sendRequestURL)
            throws SendMessageServiceException {
        log.debug("Validating SendMessageWithReference");

        final PMode pMode = validateMessaging(messaging);
        validateSendRequestURL(sendRequestURL, pMode);
        validateHref(messaging, sendRequestURL);
    }

    /**
     * Validate send request url.
     *
     * @param sendRequestURL the send request url
     * @throws SendMessageServiceException the send message service exception
     */
    private void validateSendRequestURL(final backend.ecodex.org._1_1.SendRequestURL sendRequestURL, final PMode pMode)
            throws SendMessageServiceException {
        if (sendRequestURL == null) {
            log.error("SendRequest is empty");

            final SendMessageServiceException sendMessageServiceException =
                    new SendMessageServiceException("SendRequest is empty", Code.ERROR_GENERAL_003);
            throw sendMessageServiceException;
        }

        if (sendRequestURL.getBodyload() == null || StringUtils.isEmpty(sendRequestURL.getBodyload().getString())) {
            log.error("Bodyload is empty");

            final SendMessageServiceException sendMessageServiceException =
                    new SendMessageServiceException("Bodyload is empty", Code.ERROR_GENERAL_003);
            throw sendMessageServiceException;
        }

        if (sendRequestURL.getBodyload() == null || sendRequestURL.getBodyload().getPayloadId() == null ||
            StringUtils.isEmpty(sendRequestURL.getBodyload().getPayloadId().toString())) {
            log.error("The PayloadId of bodyload is empty");

            final SendMessageServiceException sendMessageServiceException =
                    new SendMessageServiceException("The PayloadId of bodyload is empty", Code.ERROR_GENERAL_003);
            throw sendMessageServiceException;
        }


        //		IF(SENDREQUESTURL.GETPAYLOAD()==NULL || SENDREQUESTURL.GETPAYLOAD().LENGTH==0 ){
        //			LOG.ERROR("PAYLOADS ARE EMPTY");
        //
        //			SENDMESSAGESERVICEEXCEPTION SENDMESSAGESERVICEEXCEPTION = NEW SENDMESSAGESERVICEEXCEPTION(
        //					"PAYLOADS ARE EMPTY", CODE.ERROR_GENERAL_003);
        //			THROW SENDMESSAGESERVICEEXCEPTION;
        //		}

        if (sendRequestURL.getPayload() != null && sendRequestURL.getPayload().length >= 0) {
            int counter0 = 1;
            for (final PayloadURLType payloadURLType : sendRequestURL.getPayload()) {
                if (payloadURLType.getPayloadId() == null ||
                    StringUtils.isEmpty(payloadURLType.getPayloadId().toString())) {
                    log.error("The PayloadId of payload[" + counter0 + "] is empty");

                    final SendMessageServiceException sendMessageServiceException =
                            new SendMessageServiceException("The PayloadId of payload[" + counter0 + "] is empty",
                                                            Code.ERROR_GENERAL_003);
                    throw sendMessageServiceException;
                }

                counter0++;
            }
        }

        int counter = 1;

        long payloadSize = 0;

        {
            final String payload = sendRequestURL.getBodyload().getString();

            if (StringUtils.isEmpty(payload)) {
                log.error("Bodyload " + counter + "  is empty");

                final SendMessageServiceException sendMessageServiceException =
                        new SendMessageServiceException("Bodyload " + counter + "  is empty", Code.ERROR_GENERAL_003);
                throw sendMessageServiceException;
            }

            HttpURLConnection httpURLConnection = null;
            try {
                final URL url = new URL(payload);
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.connect();

                final int contentLength = httpURLConnection.getContentLength();

                if (contentLength == -1) {
                    payloadSize +=
                            eu.domibus.backend.util.IOUtils.getDataSize((InputStream) httpURLConnection.getContent());
                }

                payloadSize += contentLength;
            } catch (MalformedURLException e) {
                log.error("Bodyload url " + counter + " is invalid");

                final SendMessageServiceException sendMessageServiceException =
                        new SendMessageServiceException("Bodyload url " + counter + " is invalid", Code.ERROR_SEND_004);
                throw sendMessageServiceException;
            } catch (IOException e) {
                log.error("Bodyload url " + counter + " is invalid");

                final SendMessageServiceException sendMessageServiceException =
                        new SendMessageServiceException("Bodyload url " + counter + " is invalid", Code.ERROR_SEND_004);
                throw sendMessageServiceException;
            } finally {
                if (httpURLConnection != null) {
                    try {
                        httpURLConnection.disconnect();
                    } catch (Exception e) {
                    }
                }
            }


            counter++;
        }

        counter = 1;
        if (sendRequestURL.getPayload() != null && sendRequestURL.getPayload().length >= 0) {
            for (final PayloadURLType payloadURLType : sendRequestURL.getPayload()) {
                final String payload = payloadURLType.getString();

                if (StringUtils.isEmpty(payload)) {
                    log.error("EbmsPayload " + counter + "  is empty");

                    final SendMessageServiceException sendMessageServiceException =
                            new SendMessageServiceException("EbmsPayload " + counter + "  is empty",
                                                            Code.ERROR_GENERAL_003);
                    throw sendMessageServiceException;
                }

                HttpURLConnection httpURLConnection = null;
                try {
                    final URL url = new URL(payload);
                    httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.connect();

                    final int contentLength = httpURLConnection.getContentLength();

                    if (contentLength == -1) {
                        payloadSize += eu.domibus.backend.util.IOUtils.getDataSize(
                                (InputStream) httpURLConnection.getContent());
                    }

                    payloadSize += contentLength;
                } catch (MalformedURLException e) {
                    log.error("EbmsPayload url " + counter + " is invalid");

                    final SendMessageServiceException sendMessageServiceException =
                            new SendMessageServiceException("EbmsPayload url " + counter + " is invalid",
                                                            Code.ERROR_SEND_004);
                    throw sendMessageServiceException;
                } catch (IOException e) {
                    log.error("EbmsPayload url " + counter + " is invalid");

                    final SendMessageServiceException sendMessageServiceException =
                            new SendMessageServiceException("EbmsPayload url " + counter + " is invalid",
                                                            Code.ERROR_SEND_004);
                    throw sendMessageServiceException;
                } finally {
                    if (httpURLConnection != null) {
                        try {
                            httpURLConnection.disconnect();
                        } catch (Exception e) {
                        }
                    }
                }


                counter++;
            }
        }

        UserService userService = null;

        if (pMode.getUserServices() != null && pMode.getUserServices().size() > 0) {
            userService = pMode.getUserServices().get(0);
        } else {
            if (pMode.getBinding() != null && pMode.getBinding().getMep() != null &&
                pMode.getBinding().getMep().getLegs() != null && pMode.getBinding().getMep().getLegs().size() > 0 &&
                StringUtils.isNotEmpty(pMode.getBinding().getMep().getLegs().get(0).getUserServiceName())) {
                userService = pMode.getUserService(pMode.getBinding().getMep().getLegs().get(0).getUserServiceName());
            }
        }

        if (userService != null) {
            if (userService.getPayloadsSize() > 0 && payloadSize > userService.getPayloadsSize()) {
                log.error("Payloads are too big");

                final SendMessageServiceException sendMessageServiceException =
                        new SendMessageServiceException("Payloads are too big", Code.ERROR_SEND_005);
                throw sendMessageServiceException;
            }
        } else {
            log.error("Cannot find the appropiate userService. So It cannot be validated the payloadsSize");
        }
    }

    /**
     * Validate messaging.
     *
     * @param messaging the messaging
     * @throws SendMessageServiceException the send message service exception
     */
    private PMode validateMessaging(final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE messaging)
            throws SendMessageServiceException {
        if (messaging == null) {
            log.error("Messaging is empty");

            final SendMessageServiceException sendMessageServiceException =
                    new SendMessageServiceException("Messaging is empty", Code.ERROR_GENERAL_002);
            throw sendMessageServiceException;
        }

        if (messaging.getMessaging().getUserMessage() == null ||
            messaging.getMessaging().getUserMessage().length == 0) {
            log.error("UserMessage is empty");

            final SendMessageServiceException sendMessageServiceException =
                    new SendMessageServiceException("UserMessage is empty", Code.ERROR_GENERAL_002);
            throw sendMessageServiceException;
        }

        if (messaging.getMessaging().getUserMessage()[0].getCollaborationInfo() == null) {
            log.error("CollaborationInfo is empty");

            final SendMessageServiceException sendMessageServiceException =
                    new SendMessageServiceException("CollaborationInfo is empty", Code.ERROR_GENERAL_002);
            throw sendMessageServiceException;
        }

        //		if(messaging.getMessaging().getUserMessage()[0].getCollaborationInfo().getAgreementRef()==null){
        //			log.error("AgreementRef is empty");
        //
        //			SendMessageServiceException sendMessageServiceException = new SendMessageServiceException(
        //					"AgreementRef is empty", Code.ERROR_GENERAL_002);
        //			throw sendMessageServiceException;
        //		}

        if (messaging.getMessaging().getUserMessage()[0].getPartyInfo() == null ||
            messaging.getMessaging().getUserMessage()[0].getPartyInfo().getTo() == null ||
            messaging.getMessaging().getUserMessage()[0].getPartyInfo().getTo().getPartyId() == null ||
            messaging.getMessaging().getUserMessage()[0].getPartyInfo().getTo().getPartyId().length == 0 || StringUtils
                .isEmpty(messaging.getMessaging().getUserMessage()[0].getPartyInfo().getTo().getPartyId()[0]
                                 .getNonEmptyString())
            //				|| messaging.getMessaging().getUserMessage()[0].getPartyInfo().getTo().getPartyId()[0].getType()==null
            //				|| StringUtils.isEmpty(messaging.getMessaging().getUserMessage()[0].getPartyInfo().getTo().getPartyId()[0].getType().getNonEmptyString())

            || messaging.getMessaging().getUserMessage()[0].getPartyInfo().getFrom() == null ||
            messaging.getMessaging().getUserMessage()[0].getPartyInfo().getFrom().getPartyId() == null ||
            messaging.getMessaging().getUserMessage()[0].getPartyInfo().getFrom().getPartyId().length == 0 ||
            StringUtils.isEmpty(messaging.getMessaging().getUserMessage()[0].getPartyInfo().getFrom().getPartyId()[0]
                                        .getNonEmptyString())
            //				|| messaging.getMessaging().getUserMessage()[0].getPartyInfo().getFrom().getPartyId()[0].getType()==null
            //				|| StringUtils.isEmpty(messaging.getMessaging().getUserMessage()[0].getPartyInfo().getFrom().getPartyId()[0].getType().getNonEmptyString())

            || messaging.getMessaging().getUserMessage()[0].getCollaborationInfo().getService() == null || StringUtils
                .isEmpty(messaging.getMessaging().getUserMessage()[0].getCollaborationInfo().getService()
                                                                     .getNonEmptyString()) ||
            messaging.getMessaging().getUserMessage()[0].getCollaborationInfo().getAction() == null || StringUtils
                .isEmpty(messaging.getMessaging().getUserMessage()[0].getCollaborationInfo().getAction().toString())) {
            log.error("The parameterers needed to find an appropiate pmode are missing or invalid");

            final SendMessageServiceException sendMessageServiceException = new SendMessageServiceException(
                    "The parameterers needed to find an appropiate pmode are missing or invalid",
                    Code.ERROR_GENERAL_002);
            throw sendMessageServiceException;
        }

        final String action =
                messaging.getMessaging().getUserMessage()[0].getCollaborationInfo().getAction().toString();
        final String fromPartyid = messaging.getMessaging().getUserMessage()[0].getPartyInfo().getFrom().getPartyId()[0]
                .getNonEmptyString();
        final String fromPartyidType = null;
        //		String fromPartyidType = messaging.getMessaging().getUserMessage()[0].getPartyInfo().getFrom().getPartyId()[0].getType().getNonEmptyString();
        final String toPartyid =
                messaging.getMessaging().getUserMessage()[0].getPartyInfo().getTo().getPartyId()[0].getNonEmptyString();
        final String toPartyidType = null;
        //		String toPartyidType = messaging.getMessaging().getUserMessage()[0].getPartyInfo().getTo().getPartyId()[0].getType().getNonEmptyString();
        final String service =
                messaging.getMessaging().getUserMessage()[0].getCollaborationInfo().getService().getNonEmptyString();

        final PMode pmode = eu.domibus.ebms3.module.Configuration
                                                     .getPModeO(action, service, fromPartyid, fromPartyidType,
                                                                toPartyid, toPartyidType);

        if (pmode == null) {
            log.error("Cannot find the appropiate pmode");

            final SendMessageServiceException sendMessageServiceException =
                    new SendMessageServiceException("Cannot find the appropiate pmode", Code.ERROR_GENERAL_002);
            throw sendMessageServiceException;
        }

        return pmode;
    }

    /**
     * Validate hrefs.
     *
     * @param messaging the messaging
     * @throws SendMessageServiceException the send message service exception
     */
    private void validateHref(final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE messaging,
                              final backend.ecodex.org._1_1.SendRequestURL sendRequest)
            throws SendMessageServiceException {
        if (((messaging.getMessaging().getUserMessage()[0].getPayloadInfo() == null ||
              messaging.getMessaging().getUserMessage()[0].getPayloadInfo().getPartInfo() == null ||
              messaging.getMessaging().getUserMessage()[0].getPayloadInfo().getPartInfo().length <= 1) &&
             sendRequest.getPayload() != null && sendRequest.getPayload().length > 0) ||
            ((messaging.getMessaging().getUserMessage()[0].getPayloadInfo() != null &&
              messaging.getMessaging().getUserMessage()[0].getPayloadInfo().getPartInfo() != null &&
              messaging.getMessaging().getUserMessage()[0].getPayloadInfo().getPartInfo().length > 1) &&
             (sendRequest.getPayload() == null || sendRequest.getPayload().length == 0)) ||
            (sendRequest.getPayload() != null && sendRequest.getPayload().length > 0 &&
             sendRequest.getPayload().length !=
             messaging.getMessaging().getUserMessage()[0].getPayloadInfo().getPartInfo().length - 1)) {
            log.error("EbmsPayload infos of header and body do not match");

            final SendMessageServiceException sendMessageServiceException =
                    new SendMessageServiceException("EbmsPayload infos of header and body do not match",
                                                    Code.ERROR_GENERAL_002);
            throw sendMessageServiceException;
        }

        {
            boolean found = false;

            for (final PartInfo partInfo : messaging.getMessaging().getUserMessage()[0].getPayloadInfo()
                                                                                       .getPartInfo()) {
                if ((sendRequest.getBodyload().getPayloadId() != null &&
                     StringUtils.isNotEmpty(sendRequest.getBodyload().getPayloadId().toString()) &&
                     (partInfo.getHref() != null && StringUtils.isNotEmpty(partInfo.getHref().toString()))) &&
                    (sendRequest.getBodyload().getPayloadId().toString()
                                .equalsIgnoreCase(partInfo.getHref().toString()))) {
                    found = true;
                }
            }

            if (!found) {
                log.error("The hrefs of the bodyload do not match");

                final SendMessageServiceException sendMessageServiceException =
                        new SendMessageServiceException("The hrefs of the bodyload do not match",
                                                        Code.ERROR_GENERAL_002);
                throw sendMessageServiceException;
            }
        }

        if (sendRequest.getPayload() != null && sendRequest.getPayload().length > 0) {
            int counter = 1;
            for (final PayloadURLType payloadType : sendRequest.getPayload()) {
                boolean found = false;

                for (final PartInfo partInfo : messaging.getMessaging().getUserMessage()[0].getPayloadInfo()
                                                                                           .getPartInfo()) {
                    if ((payloadType.getPayloadId() != null &&
                         StringUtils.isNotEmpty(payloadType.getPayloadId().toString()) &&
                         (partInfo.getHref() != null && StringUtils.isNotEmpty(partInfo.getHref().toString())))) {
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
                    log.error("The hrefs of the payload[" + counter + "] do not match");

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
