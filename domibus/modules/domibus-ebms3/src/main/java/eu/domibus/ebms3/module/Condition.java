package eu.domibus.ebms3.module;

import org.apache.axis2.client.async.AxisCallback;
import org.apache.axis2.context.ConfigurationContext;

/**
 * @author Hamid Ben Malek
 */
public interface Condition {
    /**
     * If the returned value is null, it means that this condition is
     * not allowing whatever needs to be done. If however, the object
     * returned is not null, this condition is allowing...
     */
    public AxisCallback allowed(ConfigurationContext config);
}