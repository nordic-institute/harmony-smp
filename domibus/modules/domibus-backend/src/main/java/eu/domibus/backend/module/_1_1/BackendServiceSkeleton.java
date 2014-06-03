/*
 * 
 */
package eu.domibus.backend.module._1_1;

import backend.ecodex.org._1_1.Code;
import backend.ecodex.org._1_1.FaultDetail;
import org.apache.log4j.Logger;
import eu.domibus.backend.module._1_1.exception.DownloadMessageFault;
import eu.domibus.backend.module._1_1.exception.ListPendingMessagesFault;
import eu.domibus.backend.module._1_1.exception.SendMessageFault;
import eu.domibus.backend.module._1_1.exception.SendMessageWithReferenceFault;
import eu.domibus.backend.service._1_1.DownloadMessageService;
import eu.domibus.backend.service._1_1.SendMessageService;
import eu.domibus.backend.service._1_1.exception.DownloadMessageServiceException;
import eu.domibus.backend.service._1_1.exception.SendMessageServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * The Class BackendServiceSkeleton.
 */
public class BackendServiceSkeleton extends eu.domibus.backend.spring.BackendSpringBeanAutowiringSupport {

    /**
     * The Constant log.
     */
    private final static Logger log = Logger.getLogger(BackendServiceSkeleton.class);

    /**
     * The send message service.
     */
    @Autowired
    @Qualifier("SendMessageService_1_1")
    private SendMessageService sendMessageService;

    /**
     * The download message service.
     */
    @Autowired
    @Qualifier("DownloadMessageService_1_1")
    private DownloadMessageService downloadMessageService;

    /**
     * Send message with reference.
     *
     * @param messagingRequest the messaging request
     * @param sendRequestURL   the send request url
     * @throws SendMessageWithReferenceFault the send message with reference fault
     */
    public backend.ecodex.org._1_1.SendResponse sendMessageWithReference(
            final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE messagingRequest,
            final backend.ecodex.org._1_1.SendRequestURL sendRequestURL) throws SendMessageWithReferenceFault {
        try {
            log.debug("Executed BackendService.sendMessageWithReference");

            init();

            return sendMessageService.sendMessageWithReference(messagingRequest, sendRequestURL);
        } catch (SendMessageServiceException serviceException) {
            final SendMessageWithReferenceFault fault = new SendMessageWithReferenceFault(serviceException);
            fault.setFaultMessage(serviceException.getFault());
            throw fault;
        } catch (Exception exception) {
            log.error("Unknown error in BackendService.sendMessageWithReference", exception);
            final FaultDetail faultDetail = new FaultDetail();
            faultDetail.setCode(Code.ERROR_GENERAL_001);
            final SendMessageWithReferenceFault fault = new SendMessageWithReferenceFault(exception);
            fault.setFaultMessage(faultDetail);
            throw fault;
        }
    }

    /**
     * Send message.
     *
     * @param messagingRequest the messaging request
     * @param sendRequest      the send request
     * @throws SendMessageFault the send message fault
     */
    public backend.ecodex.org._1_1.SendResponse sendMessage(
            final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE messagingRequest,
            final backend.ecodex.org._1_1.SendRequest sendRequest) throws SendMessageFault {
        try {
            log.debug("Executed BackendService.sendMessage");

            init();

            return sendMessageService.sendMessage(messagingRequest, sendRequest);
        } catch (SendMessageServiceException serviceException) {
            final SendMessageFault fault = new SendMessageFault(serviceException);
            fault.setFaultMessage(serviceException.getFault());
            throw fault;
        } catch (Exception exception) {
            log.error("Unknown error in BackendService.sendMessage", exception);
            final FaultDetail faultDetail = new FaultDetail();
            faultDetail.setCode(Code.ERROR_GENERAL_001);
            final SendMessageFault fault = new SendMessageFault(exception);
            fault.setFaultMessage(faultDetail);
            throw fault;
        }
    }

    /**
     * List pending messages.
     *
     * @param listPendingMessagesRequest the list pending messages request
     * @return the backend.ecodex.org._1_1. list pending messages response
     * @throws ListPendingMessagesFault the list pending messages fault
     */
    public backend.ecodex.org._1_1.ListPendingMessagesResponse listPendingMessages(
            final backend.ecodex.org._1_1.ListPendingMessagesRequest listPendingMessagesRequest)
            throws ListPendingMessagesFault {
        try {
            log.debug("Executed BackendService.listPendingMessages");

            init();

            final backend.ecodex.org._1_1.ListPendingMessagesResponse listPendingMessagesResponse =
                    downloadMessageService.listPendingMessages(listPendingMessagesRequest);
            return listPendingMessagesResponse;
        } catch (DownloadMessageServiceException serviceException) {
            final ListPendingMessagesFault fault = new ListPendingMessagesFault(serviceException);
            fault.setFaultMessage(serviceException.getFault());
            throw fault;
        } catch (Exception exception) {
            log.error("Unknown error in BackendService.listPendingMessages", exception);
            final FaultDetail faultDetail = new FaultDetail();
            faultDetail.setCode(Code.ERROR_GENERAL_001);
            final ListPendingMessagesFault fault = new ListPendingMessagesFault(exception);
            fault.setFaultMessage(faultDetail);
            throw fault;
        }
    }

    /**
     * Download message.
     *
     * @param downloadMessageResponse the download message response
     * @param downloadMessageRequest  the download message request
     * @return the org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704. messaging e
     * @throws DownloadMessageFault the download message fault
     */
    public org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE downloadMessage(
            final backend.ecodex.org._1_1.DownloadMessageResponse downloadMessageResponse,
            final backend.ecodex.org._1_1.DownloadMessageRequest downloadMessageRequest) throws DownloadMessageFault {
        try {
            log.debug("Executed BackendService.downloadMessage");

            init();

            final org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE messagingE =
                    downloadMessageService.downloadMessage(downloadMessageResponse, downloadMessageRequest);
            return messagingE;
        } catch (DownloadMessageServiceException serviceException) {
            if (serviceException.getCode() == Code.ERROR_DOWNLOAD_003) {
                downloadMessageService.deleteMessage(downloadMessageRequest);
            }

            final DownloadMessageFault fault = new DownloadMessageFault(serviceException);
            fault.setFaultMessage(serviceException.getFault());
            throw fault;
        } catch (Exception exception) {
            log.error("Unknown error in BackendService.downloadMessage", exception);
            final FaultDetail faultDetail = new FaultDetail();
            faultDetail.setCode(Code.ERROR_GENERAL_001);
            final DownloadMessageFault fault = new DownloadMessageFault(exception);
            fault.setFaultMessage(faultDetail);
            throw fault;
        }
    }
}
