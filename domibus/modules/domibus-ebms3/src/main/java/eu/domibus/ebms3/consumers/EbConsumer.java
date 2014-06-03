package eu.domibus.ebms3.consumers;

import java.util.Map;

/**
 * This interface is implemented by any type of consumer party behind an MSH.
 *
 * @author Hamid Ben Malek
 */
public interface EbConsumer {
    public void push();

    public void pull();

    public void setParameters(Map<String, String> parameters);
}