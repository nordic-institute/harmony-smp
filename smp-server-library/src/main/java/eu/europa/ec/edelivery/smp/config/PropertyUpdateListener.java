/*-
 * #%L
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2023 European Commission | eDelivery | DomiSMP
 * %%
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * [PROJECT_HOME]\license\eupl-1.2\license.txt or https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * #L%
 */
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
