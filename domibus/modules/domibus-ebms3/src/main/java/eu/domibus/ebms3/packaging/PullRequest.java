package eu.domibus.ebms3.packaging;

import eu.domibus.common.soap.Element;
import eu.domibus.ebms3.module.Constants;

/**
 * @author Hamid Ben Malek
 */
public class PullRequest extends Element {
    private static final long serialVersionUID = 5027936549906007202L;

    public PullRequest() {
        super(Constants.PULL_REQUEST, Constants.NS, Constants.PREFIX);
    }

    public PullRequest(final String mpc) {
        this();
        this.addAttribute("mpc", mpc);
    }

    public String getPartition() {
        return this.getAttributeValue("mpc");
    }

    public void setPartition(final String mpc) {
        this.setAttribute("mpc", mpc);
    }
}