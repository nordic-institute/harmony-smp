package eu.eCODEX.transport.dto;

import backend.ecodex.org._1_1.DownloadMessageResponse;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessagingE;

/**
 * Created with IntelliJ IDEA.
 * User: kochc01
 * Date: 03.04.14
 * Time: 12:04
 * To change this template use File | Settings | File Templates.
 */
public class BackendMessageOut {
    private final DownloadMessageResponse response;
    private MessagingE messagingE;

    public BackendMessageOut(final DownloadMessageResponse downloadMessageResponse) {
        this.response = downloadMessageResponse;
    }

    public MessagingE getMessagingE() {
        return this.messagingE;
    }

    public void setMessagingE(final MessagingE messagingE) {
        this.messagingE = messagingE;
    }

    public DownloadMessageResponse getResponse() {
        return this.response;
    }
}
