package eu.domibus.submission.routing;

import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;

/**
 * Action criteria for user messages
 * <p/>
 * Created by walcz01 on 03.08.2015.
 */


public class ActionRoutingCriteriaFactory implements CriteriaFactory {

    private static final String NAME = "ACTION";

    @Override
    public IRoutingCriteria getInstance() {
        return new ActionRoutingCriteria(NAME);
    }

    @Override
    public String getName() {
        return NAME;
    }


    private class ActionRoutingCriteria extends AbstractRoutingCriteria implements IRoutingCriteria {

        private ActionRoutingCriteria(String name) {
            super(name);
        }

        @Override
        public boolean matches(UserMessage userMessage) {
            return super.matches(userMessage.getCollaborationInfo().getAction());
        }
    }
}