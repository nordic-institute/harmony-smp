package eu.europa.ec.edelivery.smp.ui;


/**
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class ResourceConstants {
    // --------------------------------------
    // context paths
    public static final String CONTEXT_PATH_PUBLIC="/ui/public/rest/";
    public static final String CONTEXT_PATH_INTERNAL ="/ui/internal/rest/";
    // public
    public static final String CONTEXT_PATH_PUBLIC_SEARCH_PARTICIPANT = CONTEXT_PATH_PUBLIC + "search";
    public static final String CONTEXT_PATH_PUBLIC_DOMAIN = CONTEXT_PATH_PUBLIC + "domain";
    public static final String CONTEXT_PATH_PUBLIC_APPLICATION = CONTEXT_PATH_PUBLIC + "application";
    public static final String CONTEXT_PATH_PUBLIC_USER = CONTEXT_PATH_PUBLIC + "user";
    public static final String CONTEXT_PATH_PUBLIC_TRUSTSTORE = CONTEXT_PATH_PUBLIC + "truststore";
    public static final String CONTEXT_PATH_PUBLIC_SERVICE_GROUP = CONTEXT_PATH_PUBLIC + "service-group";
    public static final String CONTEXT_PATH_PUBLIC_SERVICE_METADATA = CONTEXT_PATH_PUBLIC + "service-metadata";
    public static final String CONTEXT_PATH_PUBLIC_SECURITY = CONTEXT_PATH_PUBLIC + "security";

    //internal
    public static final String CONTEXT_PATH_INTERNAL_ALERT = CONTEXT_PATH_INTERNAL + "alert";
    public static final String CONTEXT_PATH_INTERNAL_DOMAIN = CONTEXT_PATH_INTERNAL + "domain";
    public static final String CONTEXT_PATH_INTERNAL_PROPERTY = CONTEXT_PATH_INTERNAL + "property";
    public static final String CONTEXT_PATH_INTERNAL_APPLICATION = CONTEXT_PATH_INTERNAL + "application";
    public static final String CONTEXT_PATH_INTERNAL_USER = CONTEXT_PATH_INTERNAL + "user";
    public static final String CONTEXT_PATH_INTERNAL_KEYSTORE = CONTEXT_PATH_INTERNAL + "keystore";
    public static final String CONTEXT_PATH_INTERNAL_TRUSTSTORE = CONTEXT_PATH_INTERNAL + "truststore";


    // --------------------------------------
    // parameters
    public static final String PARAM_PAGINATION_PAGE="page";
    public static final String PARAM_PAGINATION_PAGE_SIZE="pageSize";
    public static final String PARAM_PAGINATION_ORDER_BY="orderBy";
    public static final String PARAM_PAGINATION_ORDER_TYPE="orderType";


    public static final String PARAM_QUERY_PARTC_ID="participantIdentifier";
    public static final String PARAM_QUERY_PARTC_SCHEME="participantScheme";
    public static final String PARAM_QUERY_DOMAIN_CODE ="domainCode";
    public static final String PARAM_QUERY_USER ="user";
    public static final String PARAM_QUERY_PROPERTY ="property";

}
