package eu.domibus.ebms3.packaging;

import org.apache.axiom.om.OMElement;
import eu.domibus.common.soap.Element;
import eu.domibus.ebms3.module.Constants;

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
            addChild(mi);
        } else {
            addChild(new MessageInfo(mi.getMessageId(), mi.getRefToMessageId()));
        }
        if (mpc != null && !mpc.trim().equals("")) {
            addChild(new PullRequest(mpc));
        }
        if (receipt != null) {
            addChild(receipt);
        }
        if (errors == null || errors.length == 0) {
            return;
        }
        for (final Error error : errors) {
            addChild(error);
        }
    }

    public SignalMessage(final MessageInfo mi, final Error error) {
        super(Constants.SIGNAL_MESSAGE, Constants.NS, Constants.PREFIX);
        if (mi != null) {
            addChild(mi);
        } else {
            addChild(new MessageInfo(mi.getMessageId(), mi.getRefToMessageId()));
        }
        if (error != null) {
            addChild(error);
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
        addChild(new MessageInfo(null, null));
        addChild(new PullRequest(mpc));
    }

    public SignalMessage(final MessageInfo mi, final String mpc) {
        super(Constants.SIGNAL_MESSAGE, Constants.NS, Constants.PREFIX);
        if (mi != null) {
            addChild(mi);
        } else {
            addChild(new MessageInfo(mi.getMessageId(), mi.getRefToMessageId()));
        }
        addChild(new PullRequest(mpc));
    }
}