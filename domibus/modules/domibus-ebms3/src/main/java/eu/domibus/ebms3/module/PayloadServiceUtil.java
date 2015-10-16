package eu.domibus.ebms3.module;

import eu.domibus.ebms3.config.*;
import org.apache.log4j.Logger;

import java.util.Collection;

/**
 * Helper class for PayloadService (compression) handling
 *
 * @author muell16
 */
public class PayloadServiceUtil {
    private static final Logger LOG = Logger.getLogger(PayloadService.class);

    /**
     * Get PayloadService element of given PMode (by PModeName)
     * @param pmodeName
     * @return PayloadService element if PMode exists and PayloadService element available otherwise <code>null</code>
     */
    public static PayloadService getPayloadService(final String pmodeName) {
        final PMode pmode = Configuration.getPMode(pmodeName);

        if(pmode == null) {
            PayloadServiceUtil.LOG.error("Cannot query PayloadService for missing PMode: " + pmodeName);
            return null;
        }

        final Binding binding = pmode.getBinding();
        if (binding == null) {
            PayloadServiceUtil.LOG
                    .error("Cannot query PayloadService for missing binding of PMode " + pmode.getName());
            return null;
        }

        final Collection<Leg> legs = binding.getMep().getLegs();
        if ((legs == null) || legs.isEmpty()) {
            PayloadServiceUtil.LOG.error("Cannot query PayloadService for missing legs of binding " +
                                         binding.getName() + " of PMode " + pmode.getName());
            return null;
        }

        for (final Leg leg : legs) {
            final PayloadService payloadService = leg.getPayloadService();
            if (payloadService != null && Constants.COMPRESSION_GZIP_MIMETYPE.equals(payloadService.getCompressionType())) {
                return payloadService;
            }
        }

        return null;
    }

}
