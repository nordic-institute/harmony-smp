package eu.domibus.submission.routing;

import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Service;
import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;

/**
 * Service criteria for user messages
 * <p/>
 * Created by walcz01 on 29.07.2015.
 */

public class ServiceRoutingCriteriaFactory implements CriteriaFactory {

    private static final String NAME = "SEVICE";

    @Override
    public IRoutingCriteria getInstance() {
        return new ServiceRoutingCriteria(NAME);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private class ServiceRoutingCriteria extends AbstractRoutingCriteria {
        private ServiceRoutingCriteria(String name) {
            super(name);
        }

        @Override
        public boolean matches(UserMessage userMessage) {
            Service service = userMessage.getCollaborationInfo().getService();
            return matches(service.getValue() + ":" + service.getType());
        }

    }
}