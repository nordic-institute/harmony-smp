package eu.domibus.ebms3.workers;

import java.util.Map;

/**
 * @author Hamid Ben Malek
 */
public interface Task extends Runnable {
    public void setParameters(Map<String, String> parameters);
}