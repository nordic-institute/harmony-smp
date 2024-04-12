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
package eu.europa.ec.edelivery.smp.servlet;

/**
 * The REST API endpoint constants
 *
 * @author Joze Rihtarsic
 * @since 4.1
 */
public class WebConstants {
    public static final int HTTP_RESPONSE_CODE_CREATED = 201;
    public static final int HTTP_RESPONSE_CODE_UPDATED = 200;
    public static final String HTTP_PARAM_DOMAIN = "Domain";
    public static final String HTTP_PARAM_RESOURCE_TYPE = "Resource-Type";
    public static final String HTTP_PARAM_OWNER_OBSOLETE = "ServiceGroup-Owner";
    public static final String HTTP_PARAM_OWNER = "Resource-Owner";

    private WebConstants() {
    }
}
