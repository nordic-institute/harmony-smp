package eu.domibus.ebms3.config;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hamid Ben Malek
 */
@Root(name = "PayloadInfo", strict = false)
public class PayloadInfo implements java.io.Serializable {
    private static final long serialVersionUID = -5593310293832120737L;

    @ElementList(inline = true)
    protected List<MessagePayload> messagePayloads = new ArrayList<MessagePayload>();

    public PayloadInfo() {
    }

    public PayloadInfo(final List<MessagePayload> messagePayloads) {
        this.messagePayloads = messagePayloads;
    }

    public void addMessagePayload(final String label, final String maxSize, final List<Part> parts) {
        final MessagePayload p = new MessagePayload(label, maxSize, parts);
        messagePayloads.add(p);
    }

    public void addMessagePayload(final MessagePayload p) {
        messagePayloads.add(p);
    }

    public List<MessagePayload> getMessagePayloads() {
        return messagePayloads;
    }

    public void setMessagePayloads(final List<MessagePayload> messages) {
        this.messagePayloads = messages;
    }

    /* To serialize objects to Flex UI */
    public MessagePayload[] getMessagePayloadsArray() {
        if (messagePayloads == null) {
            return null;
        }
        final MessagePayload[] res = new MessagePayload[messagePayloads.size()];
        int i = 0;
        for (final MessagePayload p : messagePayloads) {
            res[i] = p;
            i++;
        }
        return res;
    }

    public void setMessagePayloadsArray(final MessagePayload[] list) {
        if (list == null || list.length == 0) {
            if (messagePayloads != null && messagePayloads.size() > 0) {
                messagePayloads.clear();
            }
            return;
        }
        if (messagePayloads == null) {
            messagePayloads = new ArrayList<MessagePayload>();
        }
        if (messagePayloads.size() > 0) {
            messagePayloads.clear();
        }
        for (final MessagePayload p : list) {
            addMessagePayload(p);
        }
    }
}