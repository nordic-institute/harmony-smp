package eu.domibus.ebms3.module;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.log4j.Logger;
import eu.domibus.ebms3.submit.EbMessage;
import eu.domibus.ebms3.submit.MsgInfoSet;

/**
 * @author Hamid Ben Malek
 */
public class PullWorker extends ControlledPeriodicWorker {
    private static final Logger log = Logger.getLogger(PullWorker.class);

    public PullWorker(final OMElement pullElement, final ConfigurationContext config) {
        super(pullElement, config);
    }

    protected void task() {
        final MsgInfoSet metadata = new MsgInfoSet();
        metadata.setLegNumber(1);
        metadata.setPmode(pmode);
        final EbMessage msg = new EbMessage(metadata);
        log.debug("PullWorker: about to pull from mpc " + mpc);
        //msg.inOut(metadata, getCallback());
        final MessageContext usrMsg = msg.inOut(metadata);
        if (usrMsg != null) {
            getCallback().onMessage(usrMsg);
        }
    }
}