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
        return this.filter;
    }

    public void setFilter(final MsgInfo filter) {
        this.filter = filter;
    }

    public List<ConsumerInfo> getConsumers() {
        return this.consumers;
    }

    public void setConsumers(final List<ConsumerInfo> consumers) {
        this.consumers = consumers;
    }

    public void addConsumerInfo(final ConsumerInfo consumerInfo) {
        if (consumerInfo == null) {
            return;
        }
        this.consumers.add(consumerInfo);
    }

    public boolean matchesCurrentMessageContext()

    {
        final MsgInfo info = EbUtil.getMsgInfo();
        if (this.filter == null) {
            return true;
        }
        if ((this.filter.getMpc() != null) && !this.filter.getMpc().equalsIgnoreCase(info.getMpc())) {
            return false;
        }
        if ((this.filter.getMessageId() != null) && !this.filter.getMessageId().equalsIgnoreCase(info.getMessageId())) {
            return false;
        }
        if ((this.filter.getRefToMessageId() != null) &&
            !this.filter.getRefToMessageId().equalsIgnoreCase(info.getRefToMessageId())) {
            return false;
        }
        if ((this.filter.getFromRole() != null) && !this.filter.getFromRole().equalsIgnoreCase(info.getFromRole())) {
            return false;
        }
        if ((this.filter.getToRole() != null) && !this.filter.getToRole().equalsIgnoreCase(info.getToRole())) {
            return false;
        }
        if ((this.filter.getAgreementRef() != null) &&
            !this.filter.getAgreementRef().equalsIgnoreCase(info.getAgreementRef())) {
            return false;
        }
        if ((this.filter.getPmode() != null) && !this.filter.getPmode().equalsIgnoreCase(info.getPmode())) {
            return false;
        }
        if ((this.filter.getService() != null) && !this.filter.getService().equalsIgnoreCase(info.getService())) {
            return false;
        }
        if ((this.filter.getAction() != null) && !this.filter.getAction().equalsIgnoreCase(info.getAction())) {
            return false;
        }
        if ((this.filter.getConversationId() != null) &&
            !this.filter.getConversationId().equalsIgnoreCase(info.getConversationId())) {
            return false;
        }
        if (!this.subsetOf(this.filter.getFromParties(), info.getFromParties())) {
            return false;
        }
        if (!this.subsetOf(this.filter.getToParties(), info.getToParties())) {
            return false;
        }

        // Need to check messageProperties .....

        return true;
    }

    private boolean subsetOf(final Collection<Party> p1, final Collection<Party> p2) {
        if (((p1 == null) || p1.isEmpty()) && ((p2 == null) || p2.isEmpty())) {
            return true;
        }
        if ((p2 != null) && ((p1 == null) || p1.isEmpty())) {
            return true;
        }
        if ((p1 != null) && !p1.isEmpty() && ((p2 == null) || p2.isEmpty())) {
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