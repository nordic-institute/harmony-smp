package eu.domibus.ebms3.module;

import eu.domibus.common.exceptions.ConfigurationException;
import eu.domibus.common.util.JNDIUtil;
import eu.domibus.common.util.XMLUtil;
import eu.domibus.ebms3.config.*;
import eu.domibus.ebms3.packaging.PackagingFactory;
import eu.domibus.ebms3.persistent.MsgInfo;
import eu.domibus.ebms3.submit.MsgInfoSet;
import eu.domibus.security.config.model.RemoteSecurityConfig;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.context.MessageContext;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * @author Hamid Ben Malek
 */
public class Configuration extends Constants {
    //  private static Log log = LogFactory.getLog(Configuration.class);
    private static final Logger log = Logger.getLogger(Configuration.class);

    /**
     * Indicates a no-match between P-Mode configuration and message meta-data
     */
    private static final int NO_MATCH = 0;
    /**
     * Indicates a match between P-Mode configuration and message meta-data
     */
    private static final int MATCH = 1;
    /**
     * Indicates neither a match or no-match between P-Mode configuration and
     * message meta-data because information elements were unavailable
     */
    private static final int UNAVAIL = -1;
    /**
     * Wildcard for PartyIds and their types, Service and Action.
     */
    private static final String WILDCARD = "*";


    public static void addPModePool(final PModePool pool) {
        if (Constants.pmodes == null) {
            Constants.pmodes = new HashMap<String, PMode>();
        }
        for (final PMode pm : pool.getPmodes()) {
            Constants.pmodes.put(pm.getName(), pm);
            Configuration.log.debug("PMode " + pm.getName() + " has been loaded.");
        }
    }

    /*public static String getSecurity(final String receivingPModeName, final int legNumber) {
        final Leg leg = getLeg(receivingPModeName, legNumber);
        if (leg == null) {
            return null;
        }
        return leg.getSecurity();
    }*/

    public static String getSecurity(final MsgInfoSet metadata) {
        final Leg leg = Configuration.getLeg(metadata);
        if (leg == null) {
            return null;
        }
        return leg.getSecurity();
    }

    /**
     * Retrieve the P-Mode configuration for P-Mode with given name
     *
     * @param pmodeName The P-Mode name
     * @return PMode       null if no P-Mode with this name exists
     * The PMode object that represents the P-Mode configuration
     */
    public static PMode getPMode(final String pmodeName) {
        if ((pmodeName == null) || "".equals(pmodeName.trim())) {
            return null;
        }

        if (Constants.pmodes != null) {
            return Constants.pmodes.get(pmodeName);
        } else {
            return null;
        }
    }

    public static PMode getPMode(final MsgInfoSet metadata) {
        if (metadata == null) {
            return null;
        }
        return Configuration.getPMode(metadata.getPmode());
    }

    /**
     * This helper function determines if there is a P-Mode in the current configuration
     * that matches the meta-data from the UserMessage.
     *
     * @param metadata The message meta data to find a P-Mode for
     * @return PMode     null if no P-Mode can be found based on the given meta-data
     * PMode object for the P-Mode that corresponds to the given meta-data
     */
    public static PMode getPMode(final MsgInfo metadata) {
        // If either no message meta-data is available or no P-Modes are configured,
        //  no P-Mode can be found
        if ((metadata == null) || (pmodes == null)) {
            return null;
        }

        // Check each configured P-Mode for a match until a matching one has been found
        for (final String pmodeName : Constants.pmodes.keySet()) {
            final PMode pmode = Configuration.getPMode(pmodeName);
            if (Configuration.match(pmode, metadata)) {
                return pmode;
            }
        }

        // No matching P-Mode found
        return null;
    }

    /**
     * This helper function determines if there is a Leg in the currently configurated
     * P-Modes that matches the meta-data from the UserMessage.
     *
     * @param metadata The message meta data to find a P-Mode for
     * @return null if there is no Leg in any of the P-Mode that matches to the given meta-data<br>
     * Leg object for the Leg that corresponds to the given meta-data
     */
    public static Leg getLeg(final MsgInfo metadata) {
        // If either no message meta-data is available or no P-Modes are configured,
        //  no Leg can be found
        if ((metadata == null) || (pmodes == null)) {
            return null;
        }

        // Check in each configured P-Mode for a matching Leg until a matching one has been found
        for (final String pmodeName : Constants.pmodes.keySet()) {
            final PMode pmode = Configuration.getPMode(pmodeName);
            if (pmode == null) {
                Configuration.log.error("pmode is null for : " + pmodeName);
            }
            final Binding b = pmode.getBinding();
            if (b == null) {
                Configuration.log.error("binding is null for : " + pmodeName);
            }
            final MEP m = b.getMep();
            final Collection<Leg> legs = m.getLegs();


            for (final Leg leg : legs) {
                if (Configuration.match(leg, metadata) == Configuration.MATCH) {
                    return leg;
                }
            }

        }

        throw new ConfigurationException("no leg found for pmode");
    }

    /**
     * Helper function to determine if there are {@link Leg}s in the currently configurated
     * P-Modes that match to the given mpc and authorization info contained.
     *
     * @param mpc      The mpc that was specified in the PullRequest
     * @param authInfo The authorization information contained in the PullReqeust
     * @return null if there is no Leg in any of the P-Mode that matches to the given data<br>
     * A list of Leg objects containing all Leg that corresponds to the given data
     */
    public static List<Leg> getLegs(final String mpc, final Authorization authInfo) {

        if (Constants.pmodes == null) {
            return null;
        }

        // Check in each configured P-Mode for a matching Leg
        final List<Leg> result = new ArrayList<Leg>();

        for (final String pmodeName : Constants.pmodes.keySet()) {
            final PMode pmode = Configuration.getPMode(pmodeName);
            for (final Leg leg : pmode.getBinding().getMep().getLegs()) {
                if (Configuration.match(leg, mpc, authInfo)) {
                    result.add(leg);
                }
            }
        }

        // Return the result of the search
        if (result.isEmpty()) {
            return null;
        } else {
            return result;
        }
    }

    public static Leg getLeg(final String pmodeName, final int legNumber) {
        final PMode pmode = Configuration.getPMode(pmodeName);
        if (pmode == null) {
            return null;
        }
        final List<Leg> legs = pmode.getBinding().getMep().getLegs();
        if ((legs == null) || legs.isEmpty()) {
            return null;
        }
        for (final Leg leg : legs) {
            if ((legNumber < 0) && (leg.getNumber() == 1)) {
                return leg;
            }
            if (leg.getNumber() == legNumber) {
                return leg;
            }
        }
        return null;
    }

    public static Leg getLeg(final MsgInfoSet metadata) {
        if (metadata == null) {
            return null;
        }
        return Configuration.getLeg(metadata.getPmode(), metadata.getLegNumber());
    }

    public static String getMep(final String pmodeName) {
        final PMode pmode = Configuration.getPMode(pmodeName);
        if (pmode == null) {
            return null;
        }
        final Binding b = pmode.getBinding();
        if (b == null) {
            return null;
        }
        final MEP mep = b.getMep();
        if (mep == null) {
            return null;
        }
        return mep.getName();
    }

    public static String getMep(final MsgInfoSet metadata) {
        if (metadata == null) {
            return null;
        }
        return Configuration.getMep(metadata.getPmode());
    }

    public static Binding getBinding(final MsgInfoSet metadata) {
        if (metadata == null) {
            return null;
        }
        final PMode pmode = Configuration.getPMode(metadata.getPmode());
        if (pmode != null) {
            return pmode.getBinding();
        } else {
            return null;
        }
    }

    public static String getMpc(final String pmodeName, final int legNumber) {
        final Leg leg = Configuration.getLeg(pmodeName, legNumber);
        if (leg == null) {
            return null;
        }
        return leg.getMpc();
    }

    public static String getMpc(final MsgInfoSet metadata) {
        if (metadata == null) {
            return null;
        }
        return Configuration.getMpc(metadata.getPmode(), metadata.getLegNumber());
    }

    public static String getFinalAddress(final String pmodeName, final int legNumber) {
        final Leg leg = Configuration.getLeg(pmodeName, legNumber);
        if (leg == null) {
            return null;
        }

        if ((PackagingFactory.getCurrentEndpointAddress() != null) &&
            !"".equals(PackagingFactory.getCurrentEndpointAddress())) {
            return PackagingFactory.getCurrentEndpointAddress();
        } else {
            return ((leg.getEndpoint() != null) ? leg.getEndpoint().getAddress() : null);
        }
    }

    public static String getAddress(final String pmodeName, final int legNumber) {
        final Leg leg = Configuration.getLeg(pmodeName, legNumber);
        if (leg == null) {
            return null;
        }
        return ((leg.getEndpoint() != null) ? leg.getEndpoint().getAddress() : null);
    }

    public static String getFinalAddress(final MsgInfoSet metadata) {
        if (metadata == null) {
            return null;
        }
        return Configuration.getFinalAddress(metadata.getPmode(), metadata.getLegNumber());
    }

    public static String getAddress(final MsgInfoSet metadata) {
        if (metadata == null) {
            return null;
        }
        return Configuration.getAddress(metadata.getPmode(), metadata.getLegNumber());
    }

    public static String getSoapAction(final String pmodeName, final int legNumber) {
        final Leg leg = Configuration.getLeg(pmodeName, legNumber);
        if (leg == null) {
            return null;
        }
        return leg.getSoapAction();
    }

    public static String getSoapAction(final MsgInfoSet metadata) {
        if (metadata == null) {
            return null;
        }
        return Configuration.getSoapAction(metadata.getPmode(), metadata.getLegNumber());
    }

    public static String getWsaAction(final String pmodeName, final int legNumber) {
        final Leg leg = Configuration.getLeg(pmodeName, legNumber);
        if (leg == null) {
            return null;
        }
        return leg.getWsaAction();
    }

    public static String getWsaAction(final MsgInfoSet metadata) {
        if (metadata == null) {
            return null;
        }
        return Configuration.getWsaAction(metadata.getPmode(), metadata.getLegNumber());
    }

    public static String getReliability(final String pmodeName, final int legNumber) {
        final Leg leg = Configuration.getLeg(pmodeName, legNumber);
        if (leg == null) {
            return null;
        }
        return leg.getReliability();
    }

    public static String getReliability(final MsgInfoSet metadata) {
        if (metadata == null) {
            return null;
        }
        return Configuration.getReliability(metadata.getPmode(), metadata.getLegNumber());
    }

    public static String getSoapVersion(final String pmodeName, final int legNumber) {
        final Leg leg = Configuration.getLeg(pmodeName, legNumber);
        if (leg == null) {
            return null;
        }
        return leg.getSoapVersion();
    }

    public static String getSoapVersion(final MsgInfoSet metadata) {
        if (metadata == null) {
            return null;
        }
        return Configuration.getSoapVersion(metadata.getPmode(), metadata.getLegNumber());
    }

    /**
     * Tries to find any PMode that contains the arguments in one of its leg.
     * This is used to figure out the PMode being used when receiving a
     * PullRequest.
     *
     * @param mpc     the mpc of a received PullRequest
     * @param address this is the address URL of the receiving MSH (the MSH
     *                that is receiving the PullRequest)
     * @return the first PMode that contains mpc and address in one its legs
     */
    public static PMode matchPMode(final String mpc, final String address) {
        if (Constants.pmodes == null) {
            return null;
        }
        final Set<String> keys = Constants.pmodes.keySet();
        if ((keys == null) || keys.isEmpty()) {
            return null;
        }
        for (final String n : keys) {
            final PMode pmode = Constants.pmodes.get(n);
            if (Configuration.legExists(pmode, mpc, address)) {
                return pmode;
            }
        }
        return null;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static PMode getPMode(final String mep, final String mpc, final String address) {
        if (Constants.pmodes == null) {
            return null;
        }
        final Set<String> keys = Constants.pmodes.keySet();
        if ((keys == null) || keys.isEmpty()) {
            return null;
        }
        for (final String n : keys) {
            final PMode pmode = Constants.pmodes.get(n);
            if (pmode.getLeg(mep, mpc, address) != null) {
                return pmode;
            }
        }
        return null;
    }

    public static PMode getPMode(final String mep, final String mpc) {
        if (Constants.pmodes == null) {
            return null;
        }
        final Set<String> keys = Constants.pmodes.keySet();
        if ((keys == null) || keys.isEmpty()) {
            return null;
        }
        for (final String n : keys) {
            final PMode pmode = Constants.pmodes.get(n);
            if (pmode.getLeg(mep, mpc) != null) {
                return pmode;
            }
        }
        return null;
    }

    public static PMode getPMode(final int legNumber, final String mep, final String mpc) {
        if (Constants.pmodes == null) {
            return null;
        }
        final Set<String> keys = Constants.pmodes.keySet();
        if ((keys == null) || keys.isEmpty()) {
            return null;
        }
        for (final String n : keys) {
            final PMode pmode = Constants.pmodes.get(n);
            if (pmode.getLeg(legNumber, mep, mpc) != null) {
                return pmode;
            }
        }
        return null;
    }

    public static Leg getLeg(final String pmodeName, final int legNumber, final String mep, final String mpc) {
        if ((pmodeName != null) && !"".equals(pmodeName.trim())) {
            return Configuration.getLeg(pmodeName, legNumber);
        }
        final PMode pmode = Configuration.getPMode(legNumber, mep, mpc);
        if (pmode == null) {
            return null;
        } else {
            return Configuration.getLeg(pmode.getName(), legNumber);
        }
    }

    public static Leg getLeg(final MsgInfo msgInfo, final int legNumber, final String mep) {
        if (msgInfo == null) {
            return null;
        }
        return Configuration.getLeg(msgInfo.getPmode(), legNumber, mep, msgInfo.getMpc());
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static PMode match(final MsgInfo mi, final String address) {
        if (mi == null) {
            return null;
        }
        if ((mi.getPmode() != null) && !"".equals(mi.getPmode().trim())) {
            return Configuration.getPMode(mi.getPmode());
        }
        if (Constants.pmodes == null) {
            return null;
        }
        final Set<String> keys = Constants.pmodes.keySet();
        if ((keys == null) || keys.isEmpty()) {
            return null;
        }
        for (final String n : keys) {
            final PMode pmode = Constants.pmodes.get(n);
            final List<Leg> legs = pmode.getBinding().getMep().getLegs();
            if ((legs == null) || legs.isEmpty()) {
                return null;
            }
            for (final Leg leg : legs) {
                if (Configuration.match(leg, mi, address)) {
                    return pmode;
                }
            }
        }
        return null;
    }

    public static Leg getLegFromServerSideReq(final MessageContext requestMsgCtx) {
        if (requestMsgCtx == null) {
            return null;
        }
        final SOAPHeader header = requestMsgCtx.getEnvelope().getHeader();
        if (header == null) {
            return null;
        }
        final String address =
                //      (String)requestMsgCtx.getProperty(org.apache.axis2.Constants.HTTP_FRONTEND_HOST_URL);
                requestMsgCtx.getTo().getAddress();
        boolean isUserMessage = false;
        final OMElement pullReq = XMLUtil.getGrandChildNameNS(header, Constants.PULL_REQUEST, Constants.NS);
        if (pullReq != null) {
            final String mpc = XMLUtil.getAttributeValue(pullReq, "mpc");
            final PMode pmode = Configuration.matchPMode(mpc, address);
            if (pmode == null) {
                return null;
            }
            return Configuration.getLegFromServerSideReq(pmode, isUserMessage, address);
        } else {
            final OMElement userMessage = XMLUtil.getGrandChildNameNS(header, Constants.USER_MESSAGE, Constants.NS);
            if (userMessage != null) {
                isUserMessage = true;
            }
            final String pm = XMLUtil.getGrandChildAttributeValue(userMessage, Constants.AGREEMENT_REF, "pmode");
            if (pm != null) {
                final PMode pmode = Configuration.getPMode(pm);
                return Configuration.getLegFromServerSideReq(pmode, isUserMessage, address);
            }

            final MsgInfo mi = EbUtil.getMsgInfo(requestMsgCtx);
            final PMode pmode = Configuration.match(mi, address);
            if (pmode == null) {
                return null;
            } else {
                return Configuration.getLegFromServerSideReq(pmode, isUserMessage, address);
            }
        }
    }

    private static Leg getLegFromServerSideReq(final PMode pmode, final boolean userMessage, final String address) {
        if ((pmode == null) || (pmode.getMep() == null)) {
            return null;
        }
        final String mep = pmode.getMep();
        if (userMessage) {
            if (mep.equalsIgnoreCase(Constants.ONE_WAY_PUSH) || mep.equalsIgnoreCase(Constants.TWO_WAY_PUSH_AND_PULL) ||
                mep.equalsIgnoreCase(Constants.TWO_WAY_SYNC)) {
                return Configuration.getLeg(pmode.getName(), 1);
            }
            if (mep.equalsIgnoreCase(Constants.TWO_WAY_PULL_AND_PUSH)) {
                return Configuration.getLeg(pmode.getName(), 3);
            }
            if (mep.equalsIgnoreCase(Constants.TWO_WAY_PUSH_AND_PUSH)) {
                final String adr = Configuration.getAddress(pmode.getName(), 1);
                if ((adr != null) && adr.equals(address)) {
                    return Configuration.getLeg(pmode.getName(), 1);
                } else {
                    return Configuration.getLeg(pmode.getName(), 2);
                }
            }
        } else {
            if (mep.equalsIgnoreCase(Constants.ONE_WAY_PULL) || mep.equalsIgnoreCase(Constants.TWO_WAY_PULL_AND_PUSH)) {
                return Configuration.getLeg(pmode.getName(), 1);
            }
            if (mep.equalsIgnoreCase(Constants.TWO_WAY_PUSH_AND_PULL)) {
                return Configuration.getLeg(pmode.getName(), 2);
            }
            if (mep.equalsIgnoreCase(Constants.TWO_WAY_PULL_AND_Pull)) {
                final String adr = Configuration.getAddress(pmode.getName(), 1);
                if ((adr != null) && adr.equals(address)) {
                    return Configuration.getLeg(pmode.getName(), 1);
                } else {
                    return Configuration.getLeg(pmode.getName(), 3);
                }
            }
        }
        return null;
    }

    /**
     * Checks whether the {@link PMode} matches the meta-data in {@link MsgInfo}
     * <p/>
     * Basicly a PMode matches the meta-data when there exists a Leg in the P-Mode
     * configuration for which all the information elements match to the meta-data.
     * However MsgInfo can be from different source, e.g. a received UserMessage
     * or meta-data file, and not all meta-data may be available. Therefor non
     * available information in the meta-data set does not effect the match result.
     * <p/>
     * Also the P-Mode name (or id) can only decide the match if no other information
     * is available. The reason behind this is that the P-Mode name as specified in
     * a UserMessage may be the id from the P-Mode used by the sending MSH and not
     * therefor not known in the local configuration.
     *
     * @param pmode
     * @param metadata
     * @return true    if the P-Mode matches the meta-data
     * false   otherwise
     */
    private static boolean match(final PMode pmode, final MsgInfo metadata) {

        final List<Leg> legs = pmode.getBinding().getMep().getLegs();

        // Although a P-Mode with Legs it useless, still check for it.
        if ((legs == null) || legs.isEmpty()) {
            return false;
        }

        // Now check if one of Legs matches to the meta-data.
        int match = Configuration.UNAVAIL;
        for (final Leg leg : legs) {
            if ((match = Configuration.match(leg, metadata)) == Configuration.MATCH) {
                return true;
            }
        }

        // Status can now be no-match or unavailable. In last case P-Mode name/id
        // decides the match or no-match
        if (match == Configuration.UNAVAIL) {
            match = pmode.getName().equalsIgnoreCase(metadata.getPmode()) ? Configuration.MATCH :
                    Configuration.NO_MATCH;
        }

        return match == Configuration.MATCH;
    }

    /**
     * Checks if the given {@Leg} of a P-Mode matches to the given mpc and authorization
     * information.
     *
     * @param leg      Leg configuration
     * @param mpc      The mpc to look for
     * @param authInfo The authorization information
     * @return true if the given mpc and authorization info match to the
     * configuration of the Leg
     * false otherwise
     */
    private static boolean match(final Leg leg, String mpc, final Authorization authInfo) {

        // If the Leg has an Endpoint configured then it will not match
        if (leg.getEndpoint() != null) {
            return false;
        }

        // If no mpc is given this equels the default MPC
        if (mpc == null) {
            mpc = Constants.DEFAULT_MPC;
        }

        if (Configuration.match(leg.getMpc(), mpc) == Configuration.NO_MATCH) {
            return false;
        }

        final Authorization legAuth = leg.getAuthorization();

        // If no authorization is specified in both the Leg and the given data, then
        // this decided to be a match
        if ((legAuth == null) && (authInfo == null)) {
            return true;
        }

        // Now to match
        return (Configuration.match(legAuth.getType(), authInfo.getType()) == Configuration.MATCH) &&
               (Configuration.match(legAuth.getUsername(), authInfo.getUsername()) == Configuration.MATCH) &&
               (Configuration.match(legAuth.getPassword(), authInfo.getPassword()) == Configuration.MATCH);
    }

    /**
     * Checks if the given {@Leg} of a P-Mode matches to the message meta-data given
     * in {@MsgInfo}.
     * <p/>
     * The result of this match is not simply true of false but can also be unknown
     * when some information is only known in either the Leg configuration or the
     * message meta data.
     *
     * @param leg      Leg configuration
     * @param metadata Message meta-data
     * @return MATCH when all available information matches<br>
     * NO_MATCH when information available in both the leg and
     * metadata does not match<br>
     * UNAVAIL when there is no information available to compare
     * in both leg and meta-data
     */
    private static int match(final Leg leg, final MsgInfo metadata) {
        Configuration.log.debug("Looking for PMode with LEG\n" + leg + "\nand MsgInfo: \n" + metadata);

        int result = Configuration.UNAVAIL;

        // If either the leg or the meta-data is missing, no match can be determined
        if ((leg == null) || (metadata == null)) {
            return Configuration.UNAVAIL;
        }

        if ((result = Configuration.match(leg.getMpc(), metadata.getMpc())) == Configuration.NO_MATCH) {
            return Configuration.NO_MATCH;
        }

        if ((result = Configuration.match(leg.getProducer().getParties(), metadata.getFromParties())) ==
            Configuration.NO_MATCH) {
            return Configuration.NO_MATCH;
        }

        final UserService us = leg.getUserService();
        if ((result = Configuration.match(us.getToParty().getParties(), metadata.getToParties())) ==
            Configuration.NO_MATCH) {
            return Configuration.NO_MATCH;
        }

        // @todo: When AgreementRef can be configured in P-Mode this should be an additional check
        final CollaborationInfo ci = us.getCollaborationInfo();
        if ((result = Configuration.match(ci.getAction(), metadata.getAction())) == Configuration.NO_MATCH) {
            return Configuration.NO_MATCH;
        }

        // @todo: When Service type becomes available in meta data also check for it
        if ((result = Configuration.match(ci.getService().getValue(), metadata.getService())) ==
            Configuration.NO_MATCH) {
            return Configuration.NO_MATCH;
        }

        return result;
    }

    /**
     * Compares two strings ignoring case. When either of the strings is null but
     * the other is not, no comparison result can be determined and the result is
     * <code>UNAVAIL</code>.
     *
     * @param s1 The first string
     * @param s2 The second string
     * @return MATCH       if both s1 and s2 are null or their values are equal (ignogring case)<br/>
     * NO_MATCH    if both s1 and s2 are not null and their values are different (ignogring case)<br/>
     * UNAVAIL     if one of s1 or s2 is null but the other is not
     */
    private static int match(final String s1, final String s2) {

        if (s1 == null) {
            if (s2 == null) {
                return Configuration.MATCH;
            } else {
                return Configuration.UNAVAIL;
            }
        } else {
            if (s2 == null) {
                return Configuration.UNAVAIL;
            } else {
                return ((s1.equals(WILDCARD) || s2.equals(WILDCARD) || s1.equalsIgnoreCase(s2)) ? MATCH :
                        Configuration.NO_MATCH);
            }
        }
    }

    /**
     * @deprecated
     */
    @Deprecated
    private static boolean match(final Leg leg, final MsgInfo mi, final String address) {
        if ((leg == null) || (mi == null)) {
            return false;
        }
        if (!Configuration.same(mi.getMpc(), leg.getMpc())) {
            return false;
        }
        if ((leg.getUserService() == null) || (leg.getUserService().getCollaborationInfo() == null)) {
            if (((mi.getService() != null) && !"".equals(mi.getService().trim())) ||
                ((mi.getAction() != null) && !"".equals(mi.getAction().trim()))) {
                return false;
            }
        } else {
            final CollaborationInfo ci = leg.getUserService().getCollaborationInfo();
            if (!Configuration.same(ci.getService().getValue(), mi.getService())) {
                return false;
            }
            if (!Configuration.same(ci.getAction(), mi.getAction())) {
                return false;
            }
            if (leg.getUserService().getToParty() == null) {
                if ((mi.getToParties() != null) && !mi.getToParties().isEmpty()) {
                    return false;
                }
                if ((mi.getToRole() != null) && !"".equals(mi.getToRole().trim())) {
                    return false;
                }
            }
            // compare leg.getUserService().getToParty() with mi.getToParties()
            if (!Configuration.match(leg.getUserService().getToParty(), mi.getToParties())) {
                return false;
            }
        }
        if (leg.getEndpoint() != null) {
            if (!Configuration.same(leg.getEndpoint().getAddress(), address)) {
                return false;
            }
        } else if ((address != null) && !"".equals(address.trim())) {
            return false;
        }
        if (leg.getProducer() != null) {
            if ((mi.getFromParties() == null) || mi.getFromParties().isEmpty()) {
                return false;
            }
            // compare mi.getFromParties() with the PartyIds in leg.getProducer()
            if (!Configuration.match(leg.getProducer(), mi.getFromParties())) {
                return false;
            }
            if (!Configuration.same(leg.getProducer().getRole(), mi.getFromRole())) {
                return false;
            }
        }
        return true;
    }

    /**
     * @deprecated
     */
    @Deprecated
    private static boolean match(final ToParty to, final Collection<Party> parties) {
        if ((to == null) && ((parties == null) || parties.isEmpty())) {
            return true;
        }
        if (to == null) {
            return false;
        }
        final List<Party> fp = to.getParties();
        if ((fp == null) || fp.isEmpty() || (fp.size() != parties.size())) {
            return false;
        }
        return parties.containsAll(fp);
    }
    // compare mi.getFromParties() with the PartyIds in leg.getProducer()

    /**
     * @deprecated
     */
    @Deprecated
    private static boolean match(final Producer producer, final Collection<Party> parties) {
        if ((producer == null) && ((parties == null) || parties.isEmpty())) {
            return true;
        }
        if (producer == null) {
            return false;
        }
        final Collection<Party> fp = producer.getParties();
        if ((fp == null) || fp.isEmpty() || (fp.size() != parties.size())) {
            return false;
        }
        return parties.containsAll(fp);
    }

    /**
     * Checks if there is a common Party in two lists of Party elements.
     *
     * @param p1, p2    The lists of parties to check
     * @return MATCH       if there exists a common Party or when both
     * list are empty
     * NO_MATCH    if there exists no common Party and both lists
     * contain at least one Party
     * UNAVAIL     when one of the lists is empty but the other is not
     */
    private static int match(final Collection<Party> p1, final Collection<Party> p2) {

        if ((p1 == null) || p1.isEmpty()) {
            if ((p2 == null) || p2.isEmpty()) {
                return Configuration.MATCH;
            } else {
                return Configuration.UNAVAIL;
            }
        }
        if ((p2 == null) || p2.isEmpty()) {
            return Configuration.UNAVAIL;
        }
        // Check for wildcard match
        for (final Party party : p1) {
            if (party.getPartyId().equals(Configuration.WILDCARD)) {
                return Configuration.MATCH;
            }
        }
        for (final Party party : p2) {
            if (party.getPartyId().equals(Configuration.WILDCARD)) {
                return Configuration.MATCH;
            }
        }
        if (JNDIUtil.getBooleanEnvironmentParameter(Constants.ENFORCE_1_3_COMPATIBILITY)) {
            for (final Party party : p1) {
                if (Configuration.containsIgnoreType(p2, party)) {
                    return Configuration.MATCH;
                }
            }
        }
        // Check for non-wildcard match
        for (final Party party : p1) {
            if (p2.contains(party)) {
                return Configuration.MATCH;
            }
        }

/*                while (i < p1.size() && match == NO_MATCH) {
                    int j = 0;
                    while (j < p2.size() && match == NO_MATCH) {
                        match = p1.get(i).getPartyId().equalsIgnoreCase(p2.get(j).getPartyId()) ? MATCH : NO_MATCH;
                        j++;
                    }
                    i++;
                }*/
        return Configuration.NO_MATCH;
    }

    public static String getPModesDir() {
        return JNDIUtil.getStringEnvironmentParameter(Constants.PMODES_DIR_PARAMETER);

    }

    private static boolean legExists(final PMode pmode, final String mpc, final String address) {
        if (pmode.getBinding() == null) {
            return false;
        }
        final MEP m = pmode.getBinding().getMep();
        if (m == null) {
            return false;
        }
        final List<Leg> legs = m.getLegs();
        if ((legs == null) || legs.isEmpty()) {
            return false;
        }
        for (final Leg leg : legs) {
            if (leg.getEndpoint() == null) {
                if (Configuration.same(leg.getMpc(), mpc) && ((address == null) || "".equals(address.trim()))) {
                    return true;
                }
            } else if (Configuration.same(leg.getMpc(), mpc) &&
                       Configuration.same(address, leg.getEndpoint().getAddress())) {
                return true;
            }
        }
        return false;
    }

    @Deprecated
    private static boolean same(final String v1, final String v2) {
        if (v1 != null) {
            return v1.equals(v2);
        }
        return (v2 == null) || v2.equals(v1);
    }


    /**
     * This method starts a lookup for a pmode that is from a technical point of view (ebMS3) valid to send a receipt back to the sender of a UserMessage
     * A correponding pmode has the following properties:
     * <ul>
     * <li>same action and service as the sending pmode</li>
     * <li>switched producer, toParty</li>
     * <li>same security policy as the sending pmode</li>
     * <li>different endpoint address than the sending pmode</li>
     * </ul>
     *
     * @param receivingPModeName name of the pmode the message was received with at this gateway
     * @return name of the pmode that corresponds as a reply pmode to the given receiving pmode
     */
    public static String getCorrespondingReplyPMode(final String receivingPModeName) {

        final PMode receivingPMode = Configuration.getPMode(receivingPModeName);
        if (receivingPMode == null) {
            Configuration.log.error("Can not find Pmode for name:" + receivingPModeName);
            throw new ConfigurationException("Can not find Pmode for name:" + receivingPModeName);
        }

        for (final PMode pmode : Constants.pmodes.values()) {

            final Binding binding = pmode.getBinding();
            if (binding == null) {
                Configuration.log.error("No binding found for PMode: " + pmode.getName());
                throw new ConfigurationException("No binding found for PMode: " + pmode.getName());
            }

            final MEP mepForBinding = binding.getMep();
            if (mepForBinding == null) {
                Configuration.log.error("No Mep in Binding " + binding.getName() + " of PMode: " + pmode.getName());
                throw new ConfigurationException("No Mep in Binding of PMode: " + pmode.getName());
            }

            final List<Leg> legsForBinding = mepForBinding.getLegs();
            if ((legsForBinding == null) || legsForBinding.isEmpty()) {
                Configuration.log.error("No Leg defined for Binding: " + binding.getName() + " of PMode: " +
                                        pmode.getName());
                throw new ConfigurationException(
                        "No Leg defined for Binding: " + binding.getName() + " of PMode: " + pmode.getName());
            }

            if (legsForBinding.size() > 1) {
                Configuration.log.error("More than one binding found. This is currently not supported");
                throw new ConfigurationException("More than one binding found. This is currently not supported");
            }

            final Leg currentPmodeLeg = legsForBinding.get(0);
            final String securityName = currentPmodeLeg.getSecurity();

            if ("".equals(securityName) || (securityName == null)) {
                Configuration.log.error("No Security defined for PMode: " + pmode.getName());
                throw new ConfigurationException("No Security defined for PMode: " + pmode.getName());
            }

            RemoteSecurityConfig remoteSecurityConfig = null;
            try {
                remoteSecurityConfig = eu.domibus.security.module.Configuration.getRemoteSecurity(securityName);
            } catch (final SecurityException se) {
                Configuration.log.debug("No Security: " + securityName + " found for PMode: " + pmode.getName(), se);
                continue;
            }

            RemoteSecurityConfig receivingPModeRemoteSecurityConfig = null;
            try {
                receivingPModeRemoteSecurityConfig = eu.domibus.security.module.Configuration
                        .getRemoteSecurity(receivingPMode.getBinding().getMep().getLegByNumber(1).getSecurity());
            } catch (final SecurityException se) {
                Configuration.log
                        .error("Error while getting Security for Receiving PMode: " + receivingPMode.getName());
                throw new SecurityException(se);
            }


            if (remoteSecurityConfig.getSecurity().getPolicyFile()
                                    .equals(receivingPModeRemoteSecurityConfig.getSecurity().getPolicyFile())) {
                final Leg receivingLeg = receivingPMode.getBinding().getMep().getLegByNumber(1);
                if (!receivingLeg.getEndpoint().getAddress().equals(currentPmodeLeg.getEndpoint().getAddress())) {
                    final UserService receivingUserService = receivingLeg.getUserService();
                    final UserService currentPmodeUserService = currentPmodeLeg.getUserService();

                    if (receivingUserService == null) {
                        Configuration.log.error("No UserService defined for PMode of received message " +
                                                receivingPMode.getName());
                        throw new ConfigurationException(
                                "No UserService defined for PMode of received message: " + receivingPMode.getName());
                    }

                    if (currentPmodeUserService == null) {
                        Configuration.log.warn("No UserService defined for PMode with name: " + pmode.getName());
                        continue;
                    }

                    final CollaborationInfo receivingCollaborationInfo = receivingUserService.getCollaborationInfo();
                    final CollaborationInfo currentPmodeCollaborationInfo =
                            currentPmodeUserService.getCollaborationInfo();

                    if (receivingCollaborationInfo.getAction().equals(currentPmodeCollaborationInfo.getAction())) {
                        if (receivingCollaborationInfo.getService().getValue()
                                                      .equals(currentPmodeCollaborationInfo.getService().getValue())) {
                            if (receivingUserService.getToParty().getParties().get(0).getPartyId()
                                                    .equals(currentPmodeLeg.getProducer().getName())) {
                                return pmode.getName();
                            }
                        }
                    }


                }
            }


        }

        Configuration.log.error("No corresponding PMode for " + receivingPModeName + " found.");
        throw new ConfigurationException("No corresponding PMode for " + receivingPModeName + " found.");
    }


    /**
     * Find a PMode according to the given parameters.
     * It will find a perfect match or else a not-so-perfect match using wildcards.
     * <p/>
     * <p>It looks for a matching PMode in the following order:</p>
     * <ol>
     * <li>Try to find a perfect match first.</li>
     * <li>Try to find a matching PMode with wildcard "ToPartyId" and "ToPartyIdType".</li>
     * <li>Try to find a matching PMode with wildcard "Action".</li>
     * <li>Try to find a matching PMode with wildcard "Action", "ToPartyId" and "ToPartyIdType".</li>
     * <li>Try to find a matching PMode with wildcard "Service" and "Action".</li>
     * <li>Try to find a matching PMode with wildcard "Service", "Action", "ToPartyId" and "ToPartyIdType".</li>
     * </ol>
     *
     * @param action          Message action identifier
     * @param service         Message service identifier
     * @param fromPartyId     Message FromPartyId identifier
     * @param fromPartyIdType Message FromPartyIdType identifier or {@code null} if not used
     * @param toPartyId       Message ToPartyId identifier
     * @param toPartyIdType   Message ToPartyIdType identifier or {@code null} if not used
     * @return the best matching PMode or {@code null} if no PMode matches, not even if using wildcards.
     */
    public static PMode getPModeO(final String action, final String service, final String fromPartyId,
                                  final String fromPartyIdType, final String toPartyId, final String toPartyIdType) {

        PMode pmode;

        /*
        Strategy 1
            Service match
            Action match
            FromPartyId match
            FromPartyIdType match
            ToPartyId match
            ToPartyIdType match
            enforce13compatibility false
        */
        pmode = Configuration
                .getExactMatchPModeO(action, service, fromPartyId, fromPartyIdType, toPartyId, toPartyIdType, false);
        if (pmode != null) {
            return pmode;
        }

        /*
        Strategy 1.3 compatibility
        If receiving messages from GW previous 1.4, the partyIdType for both parties are null
            Service match
            Action match
            FromPartyId match
            FromPartyIdType null
            ToPartyId match
            ToPartyIdType null
            enforce13compatibility true
        */
        if (JNDIUtil.getBooleanEnvironmentParameter(Constants.ENFORCE_1_3_COMPATIBILITY)) {
            pmode = Configuration.getExactMatchPModeO(action, service, fromPartyId, null, toPartyId, null, true);

            return pmode;
        }


        /*
        Strategy 2
             Service                    match
             Action                     match
             FromPartyId                match
             FromPartyIdType            match
             ToPartyId                  wildcard
             ToPartyIdType              match
             enforce13compatibility     false
         */
        pmode = Configuration.getExactMatchPModeO(action, service, fromPartyId, fromPartyIdType, WILDCARD,
                                                  toPartyIdType, false);
        if (pmode != null) {
            log.info("Strategy 2: " + pmode.getName());
            return pmode;
        }

        /*
        Strategy 3
             Service                    match
             Action                     match
             FromPartyId                match
             FromPartyIdType            match
             ToPartyId                  wildcard
             ToPartyIdType              wildcard
             enforce13compatibility     false
         */
        pmode = Configuration.getExactMatchPModeO(action, service, fromPartyId, fromPartyIdType, WILDCARD, WILDCARD,
                                                  false);
        if (pmode != null) {
            log.info("Strategy 3: " + pmode.getName());
            return pmode;
        }

        /*
        Strategy 4
             Service                    match
             Action                     wildcard
             FromPartyId                match
             FromPartyIdType            match
             ToPartyId                  match
             ToPartyIdType              match
             enforce13compatibility     false
         */
        pmode = Configuration.getExactMatchPModeO(WILDCARD, service, fromPartyId, fromPartyIdType, toPartyId,
                                                  toPartyIdType, false);
        if (pmode != null) {
            log.info("Strategy 4: " + pmode.getName());
            return pmode;
        }

        /*
        Strategy 5
             Service                    match
             Action                     wildcard
             FromPartyId                match
             FromPartyIdType            match
             ToPartyId                  wildcard
             ToPartyIdType              match
             enforce13compatibility     false
         */
        pmode = Configuration.getExactMatchPModeO(WILDCARD, service, fromPartyId, fromPartyIdType, WILDCARD,
                                                  toPartyIdType, false);
        if (pmode != null) {
            log.info("Strategy 5: " + pmode.getName());
            return pmode;
        }

        /*
        Strategy 6
             Service                    match
             Action                     wildcard
             FromPartyId                match
             FromPartyIdType            match
             ToPartyId                  wildcard
             ToPartyIdType              wildcard
             enforce13compatibility     false
         */
        pmode = Configuration.getExactMatchPModeO(WILDCARD, service, fromPartyId, fromPartyIdType, WILDCARD, WILDCARD,
                                                  false);
        if (pmode != null) {
            log.info("Strategy 6: " + pmode.getName());
            return pmode;
        }

        /*
        Strategy 7
             Service                    wildcard
             Action                     wildcard
             FromPartyId                match
             FromPartyIdType            match
             ToPartyId                  match
             ToPartyIdType              match
             enforce13compatibility     false
         */
        pmode = Configuration.getExactMatchPModeO(WILDCARD, WILDCARD, fromPartyId, fromPartyIdType, toPartyId,
                                                  toPartyIdType, false);
        if (pmode != null) {
            log.info("Strategy 7: " + pmode.getName());
            return pmode;
        }

        /*
        Strategy 8
             Service                    wildcard
             Action                     wildcard
             FromPartyId                match
             FromPartyIdType            match
             ToPartyId                  wildcard
             ToPartyIdType              match
             enforce13compatibility     false
         */
        pmode = Configuration.getExactMatchPModeO(WILDCARD, WILDCARD, fromPartyId, fromPartyIdType, WILDCARD,
                                                  toPartyIdType, false);
        if (pmode != null) {
            log.info("Strategy 8: " + pmode.getName());
            return pmode;
        }

        /*
        Strategy 9
             Service                    wildcard
             Action                     wildcard
             FromPartyId                match
             FromPartyIdType            match
             ToPartyId                  wildcard
             ToPartyIdType              wildcard
             enforce13compatibility     false
         */
        pmode = Configuration.getExactMatchPModeO(WILDCARD, WILDCARD, fromPartyId, fromPartyIdType, WILDCARD, WILDCARD,
                                                  false);
        if (pmode != null) {
            log.info("Strategy 9: " + pmode.getName());
            return pmode;
        }


        throw new ConfigurationException(
                "no pMode found for " + action + ":" + service + ":" + fromPartyId + ":" + fromPartyIdType + ":" +
                toPartyId + ":" + toPartyIdType + ":" +
                JNDIUtil.getBooleanEnvironmentParameter(Constants.ENFORCE_1_3_COMPATIBILITY));
    }


    private static PMode getExactMatchPModeO(final String action, final String service, final String fromPartyid,
                                             final String fromPartyidType, final String toPartyid,
                                             final String toPartyidType, final boolean enforce13compatibility) {


        for (final PMode pmode : Constants.pmodes.values()) {
            final Binding binding = pmode.getBinding();
            if ((binding == null) || (binding.getMep() == null)) {
                continue;
            }
            boolean producerFound = false;
            final Party fp = new Party();
            fp.setPartyId(fromPartyid);
            fp.setType(fromPartyidType);
            if (pmode.getProducers() != null) {
                for (final Producer producer : pmode.getProducers()) {
                    if ((producer.getParties() != null) && (!producer.getParties().isEmpty()) &&
                        (producer.getParties().contains(fp))) {
                        producerFound = true;
                        break;
                    }
                }
            }
            if (!producerFound && (binding.getMep().getLegs() != null)) {
                for (final Leg leg : binding.getMep().getLegs()) {
                    if ((leg.getProducer() != null) && (leg.getProducer().getParties() != null) &&
                        (!leg.getProducer().getParties().isEmpty()) &&
                        (leg.getProducer().getParties().contains(fp) || (enforce13compatibility && Configuration
                                .containsIgnoreType(leg.getProducer().getParties(), fp)))) {
                        producerFound = true;
                        break;
                    }
                }
            }
            if (!producerFound) {
                continue;
            }
            boolean toFounded = false;
            if (pmode.getUserServices() != null) {
                for (final UserService userService : pmode.getUserServices()) {
                    if ((userService.getToParty() != null) && (userService.getToParty().getParties() != null) &&
                        (!userService.getToParty().getParties().isEmpty()) &&
                        (userService.getToParty().getParties().get(0).getPartyId() != null) &&
                        userService.getToParty().getParties().get(0).getPartyId().equalsIgnoreCase(toPartyid)) {
                        toFounded = true;
                        break;
                    }
                }
            }
            if (!toFounded && (binding.getMep().getLegs() != null)) {
                for (final Leg leg : binding.getMep().getLegs()) {
                    if ((leg.getUserService().getToParty() != null) &&
                        (leg.getUserService().getToParty().getParties() != null) &&
                        (!leg.getUserService().getToParty().getParties().isEmpty()) &&
                        (leg.getUserService().getToParty().getParties().get(0).getPartyId() != null) &&
                        leg.getUserService().getToParty().getParties().get(0).getPartyId()
                           .equalsIgnoreCase(toPartyid)) {
                        toFounded = true;
                        break;
                    }
                }
            }
            if (!toFounded) {
                continue;
            }
            boolean serviceAndActionFounded = false;
            if (pmode.getUserServices() != null) {
                for (final UserService userService : pmode.getUserServices()) {
                    if ((userService.getCollaborationInfo() != null) &&
                        (userService.getCollaborationInfo().getAction() != null) &&
                        userService.getCollaborationInfo().getAction().equals(action) &&
                        (userService.getCollaborationInfo().getService().getValue() != null) &&
                        userService.getCollaborationInfo().getService().getValue().equals(service)) {
                        serviceAndActionFounded = true;
                        break;
                    }
                }
            }
            if (!serviceAndActionFounded && (binding.getMep().getLegs() != null)) {
                for (final Leg leg : binding.getMep().getLegs()) {
                    if ((leg.getUserService() != null) && (leg.getUserService().getCollaborationInfo() != null) &&
                        (leg.getUserService().getCollaborationInfo().getAction() != null) &&
                        leg.getUserService().getCollaborationInfo().getAction().equals(action) &&
                        (leg.getUserService().getCollaborationInfo().getService().getValue() != null) &&
                        leg.getUserService().getCollaborationInfo().getService().getValue().equals(service)) {
                        serviceAndActionFounded = true;
                        break;
                    }
                }
            }
            if (serviceAndActionFounded) {
                return pmode;
            }
        }
        return null;
    }

    private static boolean containsIgnoreType(final Collection<Party> parties, final Party fp) {
        for (final Party p : parties) {
            if (p.getPartyId().equals(fp.getPartyId())) {
                Configuration.log.debug("Matched Parties: P1:" + fp.getPartyId() + "\tP2:" + p.getPartyId());
                return true;
            }
        }
        return false;
    }

    public static String getPMode(final String action, final String service, final String fromPartyid,
                                  final String fromPartyidType, final String toPartyid, final String toPartyidType) {
        final PMode pmode =
                Configuration.getPModeO(action, service, fromPartyid, fromPartyidType, toPartyid, toPartyidType);
        if (pmode != null) {
            return pmode.getName();
        } else {
            return null;
        }
    }

    public static String getMep(final String action, final String service, final String fromPartyid,
                                final String fromPartyidType, final String toPartyid, final String toPartyidType) {
        final PMode pmode =
                Configuration.getPModeO(action, service, fromPartyid, fromPartyidType, toPartyid, toPartyidType);
        if (pmode != null) {
            return pmode.getBinding().getMep().getName();
        } else {
            return null;
        }
    }

}