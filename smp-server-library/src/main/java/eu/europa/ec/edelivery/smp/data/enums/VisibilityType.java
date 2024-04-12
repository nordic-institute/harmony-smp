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
package eu.europa.ec.edelivery.smp.data.enums;

/**
 * Specifies
 *
 * Specifies resource, group or domain visibility .
 * If the enumerated type is not specified or the Enumerated annotation is not used, the EnumType value is assumed to be PUBLIC.
 *
 * @author Joze Rihtarsic
 * @since 5.0
 */
public enum VisibilityType {
    /**
     * Resource, group of domain is marked as PUBLIC.
     */
    PUBLIC,
    /**
     * Access to the resource is within the domain/group. Users must be authenticated and must be members of the domain/group/resource in order to read it.
     */
    INTERNAL,
    /**
     *  Access to the domain, group or  resource is possible only if you are only direct or un-direct   member of the domain, group or resource
     */
    PRIVATE
}
