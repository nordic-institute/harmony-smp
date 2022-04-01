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
