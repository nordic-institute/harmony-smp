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
package eu.europa.ec.edelivery.smp.auth.enums;

/**
 *  Authentication types for application accounts supporting automated application functionalities. The application accounts
 *  are used for SMP web-service integrations.
 *
 *  Supported authentication types
 *   - PASSWORD: the user password authentication (Note:automation-user authentication is different than ui-user
 *               password and it can be used only for the UI!).
 *   - SSO: Single sign-on authentication using CAS server. ,
 *
 *  @author Joze Rihtarsic
 *  @since 4.2
 */
public enum SMPUserAuthenticationTypes {
    PASSWORD,
    SSO
}
