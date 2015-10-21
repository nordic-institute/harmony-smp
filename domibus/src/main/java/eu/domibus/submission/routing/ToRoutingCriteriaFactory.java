package eu.domibus.submission.routing;

import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;

/**
 * To criteria for user messages
 * Created by walcz01 on 29.07.2015.
 */
public class ToRoutingCriteriaFactory implements CriteriaFactory {

    private static final String NAME = "TO";

    @Override
    public IRoutingCriteria getInstance() {
        return new ToRoutingCriteria(NAME);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private class ToRoutingCriteria extends AbstractRoutingCriteria implements IRoutingCriteria {

        private ToRoutingCriteria(String name) {
            super(name);
        }

        @Override
        public boolean matches(UserMessage userMessage) {

            for (PartyId partyId : userMessage.getPartyInfo().getTo().getPartyId()) {
                if (matches(partyId.getValue() + ":" + partyId.getType())) {
                    return true;
                }
            }
            return false;
        }

    }

}
