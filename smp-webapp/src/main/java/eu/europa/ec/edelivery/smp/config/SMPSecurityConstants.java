/*-
 * #START_LICENSE#
 * smp-webapp
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
package eu.europa.ec.edelivery.smp.config;

import eu.europa.ec.edelivery.smp.ui.ResourceConstants;

/**
 * SMP security constants as secured endpoints, beans... etc
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class SMPSecurityConstants {

    public static final String SMP_AUTHENTICATION_MANAGER_BEAN = "smpAuthenticationManager";
    public static final String SMP_UI_AUTHENTICATION_MANAGER_BEAN = "smpUIAuthenticationManager";
    // must be "forwardedHeaderTransformer" see the documentation for the ForwardedHeaderTransformer
    public static final String SMP_FORWARDED_HEADER_TRANSFORMER_BEAN = "forwardedHeaderTransformer";
    // CAS BEANS
    public static final String SMP_CAS_PROPERTIES_BEAN = "smpCasServiceProperties";
    public static final String SMP_CAS_FILTER_BEAN = "smpCasAuthenticationFilter";
    public static final String SMP_CAS_KEY = "SMP_CAS_KEY_";


    public static final String SMP_SECURITY_PATH = ResourceConstants.CONTEXT_PATH_PUBLIC + "security";
    public static final String SMP_SECURITY_PATH_AUTHENTICATE = SMP_SECURITY_PATH + "/authentication";
    public static final String SMP_SECURITY_PATH_CAS_AUTHENTICATE = SMP_SECURITY_PATH + "/cas";
}
