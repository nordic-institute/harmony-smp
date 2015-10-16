package eu.domibus.ebms3.module;

import eu.domibus.ebms3.config.*;
import eu.domibus.ebms3.persistent.UserMsgToPushDAO;
import org.apache.log4j.Logger;

import java.util.Collection;

public class AS4ReliabilityUtil {
    private static final Logger log = Logger.getLogger(AS4ReliabilityUtil.class);
    private static final UserMsgToPushDAO umd = new UserMsgToPushDAO();

    /**
     * <p>Gets the AS4 receipt configuration for the message based on the PMode.</p>
     * <p><b>NOTE:</b> We assume only One-Way MEP's are used so that the PMode will
     * only contain one As4Receipt element.</p>
     *
     * @param pModeName The PMode name for which the reliabillity information must be get
     * @return The reliability info if one is available in the PMode or null when
     * none is available
     */
    public static As4Receipt getReceiptConfig(final String pModeName) {
        final PMode pmode = Configuration.getPMode(pModeName);
        if (pmode == null) {
            AS4ReliabilityUtil.log.error("Cannot query AS4 reliability info for missing PMode " + pModeName);
            return null;
        }

        final Binding binding = pmode.getBinding();
        if (binding == null) {
            AS4ReliabilityUtil.log
                    .error("Cannot query AS4 reliability info for missing binding of PMode " + pmode.getName());
            return null;
        }

        final Collection<Leg> legs = binding.getMep().getLegs();
        if ((legs == null) || legs.isEmpty()) {
            AS4ReliabilityUtil.log.error("Cannot query AS4 reliability info for missing legs of binding " +
                                         binding.getName() + " of PMode " + pmode.getName());
            return null;
        }

        for (final Leg leg : legs) {
            final As4Receipt as4Receipt = leg.getAs4Receipt();
            if (as4Receipt != null) {
                return as4Receipt;
            }
        }

        return null;
    }

    /**
     * <p>Gets the AS4 reliability configuration for the message based on the PMode.</p>
     * <p><b>NOTE:</b> We assume only One-Way MEP's are used so that the PMode will
     * only contain one As4Reliability element.</p>
     *
     * @param pModeName The PMode name for which the reliabillity information must be get
     * @return The reliability info if one is available in the PMode or null when
     * none is available
     */
    public static As4Reliability getReliabilityConfig(final String pModeName) {
        final As4Receipt as4Receipt = AS4ReliabilityUtil.getReceiptConfig(pModeName);
        return as4Receipt.getAs4Reliability();
    }


}
