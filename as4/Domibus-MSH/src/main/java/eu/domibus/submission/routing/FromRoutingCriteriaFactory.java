package eu.domibus.submission.routing;

import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyId;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;

/**
 * From criteria for user messages
 * <p/>
 * Created by walcz01 on 28.07.2015.
 */

public class FromRoutingCriteriaFactory implements CriteriaFactory {


    private static final String NAME = "FROM";

    @Override
    public IRoutingCriteria getInstance() {
        return new FromRoutingCriteria(NAME);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private class FromRoutingCriteria extends AbstractRoutingCriteria implements IRoutingCriteria {

        private FromRoutingCriteria(String name) {
            super(name);
        }

        @Override
        public boolean matches(UserMessage userMessage) {

            for (PartyId partyId : userMessage.getPartyInfo().getFrom().getPartyId()) {
                if (matches(partyId.getValue() + ":" + partyId.getType())) {
                    return true;
                }
            }

            return false;
        }

    }
}

