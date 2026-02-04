/*-
 * #START_LICENSE#
 * smp-spi
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
package eu.europa.ec.smp.spi.exceptions;

/**
 * Interface for error code types that provide a message code and a template for localization.
 *
 * @author Joze RIHTARSIC
 * @since 5.2
 */
public interface IErrorCodeType {
    /**
     * Gets the message code associated with this error code type.
     *
     * @return the message code as a String
     */
    String getMessageCode();
    /**
     * Gets the template associated with this error code type.
     *
     * @return the template as a String
     */
    String getTemplate();
}
