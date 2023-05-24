package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.edelivery.smp.config.enums.SMPPropertyEnum;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Beans implementing PropertyUpdateListener interfaces are invoked when
 * application properties are updated.
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public interface PropertyUpdateListener {

    void updateProperties(Map<SMPPropertyEnum, Object> properties);

    /**
     * Return list of handled properties
     *
     * @return list of SMPPropertyEnum properties handled by the listener
     */
    List<SMPPropertyEnum> handledProperties();

    /**
     * If the class handles the property
     *
     * @param property
     * @return
     */
    default boolean handlesProperty(SMPPropertyEnum property) {
        return handledProperties() != null && handledProperties().contains(property);
    }

    default void updateProperty(SMPPropertyEnum property, Object value) {
        updateProperties(Collections.singletonMap(property, value));
    }
}
