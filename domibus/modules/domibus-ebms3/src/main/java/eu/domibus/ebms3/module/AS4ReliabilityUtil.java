package eu.domibus.ebms3.module;

import org.apache.log4j.Logger;
import eu.domibus.common.persistent.Attachment;
import eu.domibus.ebms3.config.*;
import eu.domibus.ebms3.persistent.UserMsgToPush;
import eu.domibus.ebms3.persistent.UserMsgToPushDAO;

import java.io.File;
import java.util.Collection;
import java.util.List;

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
     *         none is available
     */
    public static As4Receipt getReceiptConfig(final String pModeName) {
        final PMode pmode = Configuration.getPMode(pModeName);
        if (pmode == null) {
            log.error("Cannot query AS4 reliability info for missing PMode " + pModeName);
            return null;
        }

        final Binding binding = pmode.getBinding();
        if (binding == null) {
            log.error("Cannot query AS4 reliability info for missing binding of PMode " + pmode.getName());
            return null;
        }

        final Collection<Leg> legs = binding.getMep().getLegs();
        if (legs == null || legs.size() == 0) {
            log.error("Cannot query AS4 reliability info for missing legs of binding " +
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
     *         none is available
     */
    public static As4Reliability getReliabilityConfig(final String pModeName) {
        final As4Receipt as4Receipt = getReceiptConfig(pModeName);
        return as4Receipt.getAs4Reliability();
    }

    /**
     * Remove all attached files (attachments) and the folder containing them, if empty.
     *
     * @param refToMessageId ID of the user message whose attached files shall be removed.
     */
    public static void removeAttachedFiles(final String refToMessageId) {
        @SuppressWarnings("unchecked")
        final List<UserMsgToPush> messages = umd.findByMessageId(refToMessageId);
        for (final UserMsgToPush message : messages) {
            for (final Attachment attachment : message.getAttachments()) {
                final String filePath = attachment.getFilePath();

                // Only remove files that reside in the temporary folder.
                // The backend interface stores payload files there.
                final String tempFolder = System.getProperty("java.io.tmpdir");
                if (!filePath.startsWith(tempFolder)) {
                    continue;
                }

                // Remove the payload file.
                new File(filePath).delete();
                final int index = filePath.lastIndexOf(File.separator);
                final String payloadsFolder = filePath.substring(0, index);

                // Remove the payload folder.
                new File(payloadsFolder).delete();
            }
        }
    }

}
