package eu.domibus.ebms3.submit;

import eu.domibus.ebms3.module.Configuration;
import eu.domibus.ebms3.module.Constants;
import eu.domibus.ebms3.module.EbUtil;

//import eu.domibus.ebms3.config.*;
//import eu.domibus.ebms3.pmodes.*;

//import java.util.*;

/**
 * This is a class that submit messages to the MSH. The submitted messages
 * are user messages that may be used by the MS to either make a push
 * (request or response) or respond synchronously to a given request.
 * <p/>
 * This class is not meant to be called from outside the MSH. It is only
 * called by the MSH module class itself to perform submission based on
 * other doors (such as read a file based system for input messages,
 * or read a JMS, or any other input source). In other words, the producer
 * application which is behind the submission does not interact with this
 * class directly (as this class will not run in the same class loader as
 * the producer application, even if the producer application is running
 * in the same JVM and the same stack as the MSH module).
 *
 * @author Hamid Ben Malek
 */
public class SubmitUtil {
    public static final int TO_BE_PUSHED = 1;
    public static final int TO_BE_PULLED = 2;
    public static final int TO_BE_SYNC_RESPONSE = 3;

    /**
     * Utility method that determine if the submitted message is
     * to be pushed, to be pulled, to be sent back in the back channel
     * of user message request. According to what kind of category, it will
     * be saved in the appropriate table.
     *
     * @param mis The Message Metadata (metadata.xml file) tells the MSH what to
     *            do with a submitted message
     * @return the leg number to use for the submitted message.
     */
    public static int msgCategory(final MsgInfoSet mis) {
        if (mis == null) {
            return SubmitUtil.TO_BE_PUSHED;
        }

        final String mep = Configuration.getMep(mis);
        //    Binding binding = getBinding(mis);
        //    String mep = binding.getMep();
        final int ln = SubmitUtil.getLegNumber(mis);
        //getLegNumber(mep, binding.getFirstLeg().getAddress());
        if (mep.equalsIgnoreCase(Constants.ONE_WAY_PUSH)) {
            return SubmitUtil.TO_BE_PUSHED;
        }
        if (mep.equalsIgnoreCase(Constants.ONE_WAY_PULL)) {
            return SubmitUtil.TO_BE_PULLED;
        }
        if (mep.equalsIgnoreCase(Constants.TWO_WAY_SYNC)) {
            if (ln == 1) {
                return SubmitUtil.TO_BE_PUSHED;
            } else {
                return SubmitUtil.TO_BE_SYNC_RESPONSE;
            }
        }
        if (mep.equalsIgnoreCase(Constants.TWO_WAY_PUSH_AND_PUSH)) {
            return SubmitUtil.TO_BE_PUSHED;
        }
        if (mep.equalsIgnoreCase(Constants.TWO_WAY_PUSH_AND_PULL)) {
            if (ln == 1) {
                return SubmitUtil.TO_BE_PUSHED;
            } else {
                return SubmitUtil.TO_BE_PULLED;
            }
        }
        if (mep.equalsIgnoreCase(Constants.TWO_WAY_PULL_AND_PUSH)) {
            if (ln == 2) {
                return SubmitUtil.TO_BE_PULLED;
            } else {
                return SubmitUtil.TO_BE_PUSHED;
            }
        }
        if (mep.equalsIgnoreCase(Constants.TWO_WAY_PULL_AND_Pull)) {
            return SubmitUtil.TO_BE_PULLED;
        }

        // this should never be reached:
        return SubmitUtil.TO_BE_PUSHED;
    }

    public static int getLegNumber(final MsgInfoSet metadata) {
        if (metadata == null) {
            return 1;
        }
        if (metadata.getLegNumber() != -1) {
            return metadata.getLegNumber();
        }
        final String mep = Configuration.getMep(metadata.getPmode());
        final String address = Configuration.getAddress(metadata.getPmode(), 1);
        final int ln = SubmitUtil.getLegNumber(mep, address);
        //    Binding binding = getBinding(metadata);
        //    String mep = binding.getMep();
        //    int ln = getLegNumber(mep, binding.getFirstLeg().getAddress());
        metadata.setLegNumber(ln);
        return ln;
    }

    /*
    public static Leg getLeg(MsgInfoSet metadata)
    {
      if ( metadata == null ) return null;
      //Binding binding = getBinding(metadata);
      //String mep = binding.getMep();
      int legNumber = getLegNumber(metadata);
              //getLegNumber(mep, binding.getFirstLeg().getAddress());
      Map<String, PMode> pmodesMap = Constants.pmodesMap;
      PMode pmode = pmodesMap.get(metadata.getPmode());
      if ( pmode == null ) return null;
      return pmode.getLeg(legNumber, null);
    }

    public static Leg getLeg(MsgInfoSet metadata, int legNumber)
    {
      if ( metadata == null ) return null;
      Binding binding = getBinding(metadata);
      return binding.getLeg(legNumber);
    }

    private static Binding getBinding(MsgInfoSet metadata)
    {
      if ( metadata == null ) return null;
      Map<String, PMode> pmodesMap = Constants.pmodesMap;
      PMode pmode = pmodesMap.get(metadata.getPmode());
      return pmode.getBinding();
    }
    */
    private static int getLegNumber(final String mep, final String firstLegAddress) {
        if (mep.equalsIgnoreCase(Constants.ONE_WAY_PUSH)) {
            return 1;
        }
        if (mep.equalsIgnoreCase(Constants.ONE_WAY_PULL)) {
            return 2;
        }
        if (mep.equalsIgnoreCase(Constants.TWO_WAY_SYNC)) {
            if (EbUtil.isLocal(firstLegAddress, Constants.configContext)) {
                return 2;
            } else {
                return 1;
            }
        }
        if (mep.equalsIgnoreCase(Constants.TWO_WAY_PUSH_AND_PUSH)) {
            if (EbUtil.isLocal(firstLegAddress, Constants.configContext)) {
                return 2;
            } else {
                return 1;
            }
        }
        if (mep.equalsIgnoreCase(Constants.TWO_WAY_PUSH_AND_PULL)) {
            if (EbUtil.isLocal(firstLegAddress, Constants.configContext)) {
                return 3;
            } else {
                return 1;
            }
        }
        if (mep.equalsIgnoreCase(Constants.TWO_WAY_PULL_AND_PUSH)) {
            if (EbUtil.isLocal(firstLegAddress, Constants.configContext)) {
                return 2;
            } else {
                return 3;
            }
        }
        if (mep.equalsIgnoreCase(Constants.TWO_WAY_PULL_AND_Pull)) {
            if (EbUtil.isLocal(firstLegAddress, Constants.configContext)) {
                return 2;
            } else {
                return 4;
            }
        }
        return 1;
    }
}