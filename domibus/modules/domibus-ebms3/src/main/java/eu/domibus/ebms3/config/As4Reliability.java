/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.domibus.ebms3.config;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * This class represents the Reliability child element of the As4Receipt element. When it is included in the configuration it specifies when and how often a message must be retransmitted when no AS4 Receipt is received.
 *
 * @author safi
 */
@Root(name = "Reliability", strict = false)
public class As4Reliability implements Serializable {
    private static final long serialVersionUID = -4943856179465571613L;

    /**
     * Specifies how many times messages should be retransmitted.
     * <p/>
     * As this is only relevant for the Sender of the message the attribute should
     * be specified in the PMode of the Sender.
     */
    @Attribute(required = false)
    private int maxRetries;

    /**
     * The interval between retransmissions in seconds.
     * <p/>
     * As this is only relevant for the Sender of the message the attribute should
     * be specified in the PMode of the Sender.
     */
    @Attribute(required = false)
    private int interval;

    /**
     * The interval between retransmissions in seconds.
     * <p/>
     * As this is only relevant for the Sender of the message the attribute should
     * be specified in the PMode of the Sender.
     */
    @Attribute(required = false)
    private int shutdown;

    /**
     * Enable or disable detecting and eliminating duplicates.
     * <p/>
     * As this is only relevant for the Receiver of the message the attribute
     * should be specified in the PMode of the Receiver.
     */
    @Attribute(required = false)
    private boolean duplicateElimination = true;

    /**
     * Increase the interval by the power of two for each retransmission.
     * <p/>
     * As this is only relevant for the Sender of the message the attribute
     * should be specified in the PMode of the Sender.
     *
     * @see #interval
     */
    @Attribute(required = false)
    private boolean exponentialBackoff = false;

    /**
     * Randomize the interval.
     * <p/>
     * As this is only relevant for the Sender of the message the attribute
     * should be specified in the PMode of the Sender.
     *
     * @see #interval
     */
    @Attribute(required = false)
    private boolean randomize = false;

    /**
     * @return the maxRetries
     */
    public int getMaxRetries() {
        return maxRetries;
    }

    /**
     * @param maxRetries the maxRetries to set
     */
    public void setMaxRetries(final int maxRetries) {
        this.maxRetries = maxRetries;
    }

    /**
     * @return the interval
     */
    public int getInterval() {
        return interval;
    }

    /**
     * @param interval the interval to set
     */
    public void setInterval(final int interval) {
        this.interval = interval;
    }

    /**
     * @return the shutdown interval
     */
    public int getShutdown() {
        return shutdown;
    }

    /**
     * @param interval the shutdown interval to set
     */
    public void setShutdown(final int shutdown) {
        this.shutdown = shutdown;
    }

    /**
     * @return the duplicateElimination
     */
    public boolean isDuplicateElimination() {
        return duplicateElimination;
    }

    /**
     * @param duplicateElimination the duplicateElimination to set
     */
    public void setDuplicateElimination(final boolean duplicateElimination) {
        this.duplicateElimination = duplicateElimination;
    }

    public boolean isExponentialBackoff() {
        return exponentialBackoff;
    }

    public void setExponentialBackoff(final boolean exponentialBackoff) {
        this.exponentialBackoff = exponentialBackoff;
    }

    public boolean isRandomize() {
        return randomize;
    }

    public void setRandomize(final boolean randomize) {
        this.randomize = randomize;
    }

}
