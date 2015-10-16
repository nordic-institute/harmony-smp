package eu.domibus.ebms3.packaging;

import eu.domibus.common.soap.Element;
import eu.domibus.ebms3.module.Constants;

/**
 * @author Hamid Ben Malek
 */
public class UserMessage extends Element {
    private static final long serialVersionUID = 4124457807040373968L;

    public UserMessage(final MessageInfo mi, final PartyInfo partyInfo, final CollaborationInfo ci,
                       final MessageProperties mp, final PayloadInfo pi) {
        super(Constants.USER_MESSAGE, Constants.NS, Constants.PREFIX);
        if (mi != null) {
            this.addChild(mi);
        } else {
            this.addChild(new MessageInfo(mi.getMessageId(), null));
        }
        if (partyInfo != null) {
            this.addChild(partyInfo);
        }
        if (ci != null) {
            this.addChild(ci);
        }
        if (mp != null) {
            this.addChild(mp);
        }
        if (pi != null) {
            this.addChild(pi);
        }
    }

    public UserMessage(final String mpc, final MessageInfo mi, final PartyInfo partInfo, final CollaborationInfo ci,
                       final MessageProperties mp, final PayloadInfo pi) {
        this(mi, partInfo, ci, mp, pi);
        this.addAttribute("mpc", mpc);
    }

    //  public UserMessage(String mpc, String[] fromPartyIds, String[] toPartyIds,
    //                     String service, String action, String conversationId,
    //                     String[] cids)
    //  {
    //    super(Constants.USER_MESSAGE, Constants.NS, Constants.PREFIX);
    //    if ( mpc != null && !mpc.trim().equals("") ) addAttribute("mpc", mpc);
    //    addChild(new MessageInfo());
    //    PartyInfo partyInfo = new PartyInfo();
    //    partyInfo.addFromParties(fromPartyIds, null);
    //    partyInfo.addToParties(toPartyIds, null);
    //    addChild(partyInfo);
    //    CollaborationInfo ci =
    //      new CollaborationInfo(null, null, service, null, action, conversationId);
    //    addChild(ci);
    //    addChild( new PayloadInfo(cids) );
    //  }
    //
    //  public UserMessage(String mpc, String fromPartyID, String toPartyID,
    //                      String service, String action, String conversationId,
    //                      String[] cids)
    //  {
    //    super(Constants.USER_MESSAGE, Constants.NS, Constants.PREFIX);
    //    if ( mpc != null && !mpc.trim().equals("") ) addAttribute("mpc", mpc);
    //    addChild(new MessageInfo());
    //    PartyInfo partyInfo = new PartyInfo();
    //    partyInfo.addFromParty(fromPartyID, null, null);
    //    partyInfo.addFromParty(toPartyID, null, null);
    //    addChild(partyInfo);
    //    CollaborationInfo ci =
    //      new CollaborationInfo(null, null, service, null, action, conversationId);
    //    addChild(ci);
    //    addChild( new PayloadInfo(cids) );
    //  }
}