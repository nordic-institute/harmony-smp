package eu.domibus.ebms3.packaging;

import eu.domibus.common.soap.Element;
import eu.domibus.ebms3.module.Constants;
import org.apache.axiom.om.OMElement;

/**
 * @author Hamid Ben Malek
 */
public class SignalMessage extends Element {
    private static final long serialVersionUID = 4454856102058186344L;

    //  public SignalMessage(String mpc, Error[] errors)
    //  {
    //    super(Constants.SIGNAL_MESSAGE, Constants.NS, Constants.PREFIX);
    //    addChild(new MessageInfo());
    //    if ( mpc != null && !mpc.trim().equals("") )
    //         addChild(new PullRequest(mpc));
    //    if ( errors == null || errors.length == 0 ) return;
    //    for (Error error : errors) addChild(error);
    //  }

    public SignalMessage(final MessageInfo mi, final String mpc, final OMElement receipt, final Error[] errors) {
        super(Constants.SIGNAL_MESSAGE, Constants.NS, Constants.PREFIX);
        if (mi != null) {
            this.addChild(mi);
        } else {
            this.addChild(new MessageInfo(mi.getMessageId(), mi.getRefToMessageId()));
        }
        if ((mpc != null) && !"".equals(mpc.trim())) {
            this.addChild(new PullRequest(mpc));
        }
        if (receipt != null) {
            this.addChild(receipt);
        }
        if ((errors == null) || (errors.length == 0)) {
            return;
        }
        for (final Error error : errors) {
            this.addChild(error);
        }
    }

    public SignalMessage(final MessageInfo mi, final Error error) {
        super(Constants.SIGNAL_MESSAGE, Constants.NS, Constants.PREFIX);
        if (mi != null) {
            this.addChild(mi);
        } else {
            this.addChild(new MessageInfo(mi.getMessageId(), mi.getRefToMessageId()));
        }
        if (error != null) {
            this.addChild(error);
        }
    }

    //  public SignalMessage(Error error)
    //  {
    //    super(Constants.SIGNAL_MESSAGE, Constants.NS, Constants.PREFIX);
    //    addChild(new MessageInfo());
    //    if ( error != null ) addChild(error);
    //  }

    //  public SignalMessage(Error[] errors)
    //  {
    //    super(Constants.SIGNAL_MESSAGE, Constants.NS, Constants.PREFIX);
    //    addChild(new MessageInfo());
    //    if ( errors == null || errors.length == 0 ) return;
    //    for (Error error : errors) addChild(error);
    //  }

    public SignalMessage(final String mpc) {
        super(Constants.SIGNAL_MESSAGE, Constants.NS, Constants.PREFIX);
        this.addChild(new MessageInfo(null, null));
        this.addChild(new PullRequest(mpc));
    }

    public SignalMessage(final MessageInfo mi, final String mpc) {
        super(Constants.SIGNAL_MESSAGE, Constants.NS, Constants.PREFIX);
        if (mi != null) {
            this.addChild(mi);
        } else {
            this.addChild(new MessageInfo(mi.getMessageId(), mi.getRefToMessageId()));
        }
        this.addChild(new PullRequest(mpc));
    }
}