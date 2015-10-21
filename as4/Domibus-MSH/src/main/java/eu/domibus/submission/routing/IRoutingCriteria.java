package eu.domibus.submission.routing;

import eu.domibus.common.model.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;

/**
 * Routing Interface for incoming user messages
 * Created by walcz01 on 28.07.2015.
 */
public interface IRoutingCriteria {

    /**
     * Returns if $UserMessage matches expression
     *
     * @param candidate user message to match
     * @return result
     */
    public boolean matches(UserMessage candidate);

    /**
     * Returns name of Routing Criteria
     *
     * @return name of Routing Criteria
     */
    public String getName();

    public String getExpression();

    public void setExpression(String expression);


}
