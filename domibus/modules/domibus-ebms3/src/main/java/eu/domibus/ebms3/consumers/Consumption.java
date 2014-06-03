package eu.domibus.ebms3.consumers;

import eu.domibus.ebms3.config.Party;
import eu.domibus.ebms3.module.EbUtil;
import eu.domibus.ebms3.persistent.MsgInfo;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//import eu.domibus.ebms3.pmodes.Party;

/**
 * @author Hamid Ben Malek
 */
@Root
public class Consumption {
    @Element(required = false)
    protected MsgInfo filter;

    @ElementList(inline = true)
    protected List<ConsumerInfo> consumers = new ArrayList<ConsumerInfo>();

    public MsgInfo getFilter() {
        return filter;
    }

    public void setFilter(final MsgInfo filter) {
        this.filter = filter;
    }

    public List<ConsumerInfo> getConsumers() {
        return consumers;
    }

    public void setConsumers(final List<ConsumerInfo> consumers) {
        this.consumers = consumers;
    }

    public void addConsumerInfo(final ConsumerInfo consumerInfo) {
        if (consumerInfo == null) {
            return;
        }
        consumers.add(consumerInfo);
    }

    public boolean matchesCurrentMessageContext()

    {
        final MsgInfo info = EbUtil.getMsgInfo();
        if (filter == null) {
            return true;
        }
        if (filter.getMpc() != null && !filter.getMpc().equalsIgnoreCase(info.getMpc())) {
            return false;
        }
        if (filter.getMessageId() != null && !filter.getMessageId().equalsIgnoreCase(info.getMessageId())) {
            return false;
        }
        if (filter.getRefToMessageId() != null && !filter.getRefToMessageId().equalsIgnoreCase(info.getRefToMessageId())) {
            return false;
        }
        if (filter.getFromRole() != null && !filter.getFromRole().equalsIgnoreCase(info.getFromRole())) {
            return false;
        }
        if (filter.getToRole() != null && !filter.getToRole().equalsIgnoreCase(info.getToRole())) {
            return false;
        }
        if (filter.getAgreementRef() != null && !filter.getAgreementRef().equalsIgnoreCase(info.getAgreementRef())) {
            return false;
        }
        if (filter.getPmode() != null && !filter.getPmode().equalsIgnoreCase(info.getPmode())) {
            return false;
        }
        if (filter.getService() != null && !filter.getService().equalsIgnoreCase(info.getService())) {
            return false;
        }
        if (filter.getAction() != null && !filter.getAction().equalsIgnoreCase(info.getAction())) {
            return false;
        }
        if (filter.getConversationId() != null && !filter.getConversationId().equalsIgnoreCase(info.getConversationId())) {
            return false;
        }
        if (!subsetOf(filter.getFromParties(), info.getFromParties())) {
            return false;
        }
        if (!subsetOf(filter.getToParties(), info.getToParties())) {
            return false;
        }

        // Need to check messageProperties .....

        return true;
    }

    private boolean subsetOf(final Collection<Party> p1, final Collection<Party> p2) {
        if ((p1 == null || p1.size() == 0) && (p2 == null || p2.size() == 0)) {
            return true;
        }
        if (p2 != null && (p1 == null || p1.size() == 0)) {
            return true;
        }
        if (p1 != null && p1.size() > 0 && (p2 == null || p2.size() == 0)) {
            return false;
        }

        boolean failed = true;
        for (final Party p : p1) {
            failed = true;
            for (final Party q : p2) {
                if (p.equals(q)) {
                    failed = false;
                }
            }
        }
        return !failed;
    }
}