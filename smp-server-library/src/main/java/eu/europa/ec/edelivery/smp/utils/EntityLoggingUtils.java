/*-
 * #START_LICENSE#
 * smp-server-library
 * %%
 * Copyright (C) 2017 - 2024 European Commission | eDelivery | DomiSMP
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
 * #END_LICENSE#
 */
package eu.europa.ec.edelivery.smp.utils;

import eu.europa.ec.edelivery.smp.auth.SMPUserDetails;

/**
 * Utility class containing common methods for working with entities. Such as creating a string representation of an entity.
 *
 * @since 5.1
 * @author Joze RIHTARSIC
 */
public class EntityLoggingUtils {
    public static final String NULL_STRING = "null";
    public static final String NULL_USER = "Anonymous";

    /**
     * Private constructor to prevent instantiation of the util class. The
     * class contains only static methods and should not be instantiated.
     */
    private EntityLoggingUtils() {
        // Utility class
    }

    public static <T> String entityToString(T entity, String defaultIfNull) {
        return entity == null ? defaultIfNull : entity.toString();
    }

    /**
     * Creates a string representation of an entity. If the entity is null, it returns the string "null".
     * The method can be with logger which throws an exception if the entity is null.
     *
     * @param entity the entity to convert to a string
     * @param <T>
     * @return the string representation of the entity
     */
    public static <T> String entityToString(T entity) {
        return entityToString(entity, NULL_STRING);
    }

    /**
     * Creates a string representation of a user. If the user is null, it returns the string "UserDetails/Anonymous".
     * @param user
     * @return
     */
    public static String userDetailToString(SMPUserDetails user) {
        if (user != null && user.getUser() == null) {
            return "UserDetails/Anonymous";
        }
        return entityToString(user != null && user.getUser() != null ? user.getUser() : null, NULL_USER);
    }
}
