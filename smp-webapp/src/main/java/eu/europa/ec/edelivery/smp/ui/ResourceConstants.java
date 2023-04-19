package eu.europa.ec.edelivery.smp.ui;


/**
 * Definitions of rest IU control paths. The path is build from domain - to resource
 * Tree basic sub-path
 *  <ul>
 *    <li>/ui/public/rest/: public services without the authentication </li>
 *    <li>/ui/edit/rest/{user-id}: public services where authentication is needed - the id is user session identifier</li>
 *    <li>/ui/internal/rest/: system admin services which should be protected and newer exposed to the internet.</li>
 *  </ul>
 * <p>
 * /ui/edit/rest/[user-id]/domain/[domain-id]/group/[group-id]/resource/[resource-id]/
 *
 * @author Joze Rihtarsic
 * @since 4.2
 */
public class ResourceConstants {

    /**
     * Path resources
     */
    public static final String PATH_RESOURCE_TYPE_DOMAIN = "domain";
    public static final String PATH_RESOURCE_TYPE_MEMBER = "member";
    public static final String PATH_RESOURCE_TYPE_GROUP = "group";
    public static final String PATH_RESOURCE_TYPE_RESOURCE = "resource";
    public static final String PATH_RESOURCE_TYPE_SUBRESOURCE = "subresource";
    public static final String PATH_RESOURCE_TYPE_DOCUMENT = "document";

    public static final String PATH_RESOURCE_TYPE_RESOURCE_DEFINITION = "res-def";
    /**
     * Path parameters
     */
    public static final String PATH_PARAM_ENC_USER_ID = "user-id";
    public static final String PATH_PARAM_ENC_DOMAIN_ID = "domain-id";
    public static final String PATH_PARAM_ENC_MEMBER_ID = "member-id";
    public static final String PATH_PARAM_ENC_GROUP_ID = "group-id";
    public static final String PATH_PARAM_ENC_RESOURCE_ID = "resource-id";
    public static final String PATH_PARAM_ENC_SUBRESOURCE_ID = "subresource-id";
    public static final String PATH_PARAM_CERT_ALIAS = "cert-alias";
    public static final String PATH_PARAM_ENC_CREDENTIAL_ID = "credential-id";
    public static final String PATH_PARAM_ENC_MANAGED_USER_ID = "managed-user-id";
    public static final String PATH_PARAM_SRV_GROUP_ID = "service-group-id";

    public static final String PATH_PARAM_KEYSTORE_TOKEN = "keystore-token";
    public static final String PATH_PARAM_KEYSTORE_TYPE = "keystore-type";

    public static final String PATH_ACTION_DELETE = "delete";
    public static final String PATH_ACTION_UPDATE = "update";
    public static final String PATH_ACTION_CREATE = "create";
    public static final String PATH_ACTION_PUT = "put";
    public static final String PATH_ACTION_VALIDATE = "validate";
    public static final String PATH_ACTION_GENERATE = "generate";

    public static final String PATH_ACTION_RETRIEVE = "retrieve";
    public static final String PATH_ACTION_SEARCH = "search";
    // --------------------------------------
    // context paths
    public static final String CONTEXT_PATH_PUBLIC = "/ui/public/rest/";
    public static final String CONTEXT_PATH_INTERNAL = "/ui/internal/rest/";

    public static final String CONTEXT_PATH_EDIT = "/ui/edit/rest/" + "{" + PATH_PARAM_ENC_USER_ID + "}";
    public static final String CONTEXT_PATH_EDIT_DOMAIN = CONTEXT_PATH_EDIT + "/" + PATH_RESOURCE_TYPE_DOMAIN;
    public static final String SUB_CONTEXT_PATH_EDIT_DOMAIN_ADMIN = "{" + PATH_PARAM_ENC_DOMAIN_ID + "}";
    public static final String SUB_CONTEXT_PATH_EDIT_DOMAIN_MEMBER = SUB_CONTEXT_PATH_EDIT_DOMAIN_ADMIN + "/" + PATH_RESOURCE_TYPE_MEMBER;
    public static final String SUB_CONTEXT_PATH_EDIT_DOMAIN_MEMBER_PUT = SUB_CONTEXT_PATH_EDIT_DOMAIN_MEMBER + "/" + PATH_ACTION_PUT;
    public static final String SUB_CONTEXT_PATH_EDIT_DOMAIN_MEMBER_DELETE = SUB_CONTEXT_PATH_EDIT_DOMAIN_MEMBER + "/"
            + "{" + PATH_PARAM_ENC_MEMBER_ID + "}" + "/" +  PATH_ACTION_DELETE;

    // domain edit data
    public static final String SUB_CONTEXT_PATH_EDIT_DOMAIN_RESOURCE_DEF = SUB_CONTEXT_PATH_EDIT_DOMAIN_ADMIN + "/" + PATH_RESOURCE_TYPE_RESOURCE_DEFINITION;
    // ------------------------------------------
    // group management
    public static final String CONTEXT_PATH_EDIT_GROUP = CONTEXT_PATH_EDIT_DOMAIN + "/" +  SUB_CONTEXT_PATH_EDIT_DOMAIN_ADMIN
            + "/"+ PATH_RESOURCE_TYPE_GROUP;
    public static final String SUB_CONTEXT_PATH_EDIT_GROUP_CREATE =  PATH_ACTION_CREATE;
    public static final String SUB_CONTEXT_PATH_EDIT_GROUP_UPDATE =  "{" + PATH_PARAM_ENC_GROUP_ID + "}" + "/" +   PATH_ACTION_UPDATE;
    public static final String SUB_CONTEXT_PATH_EDIT_GROUP_DELETE =  "{" + PATH_PARAM_ENC_GROUP_ID + "}" + "/" +  PATH_ACTION_DELETE;
    public static final String SUB_CONTEXT_PATH_EDIT_GROUP_MEMBER =  "{" + PATH_PARAM_ENC_GROUP_ID + "}" + "/" +  PATH_RESOURCE_TYPE_MEMBER;
    public static final String SUB_CONTEXT_PATH_EDIT_GROUP_MEMBER_PUT =  SUB_CONTEXT_PATH_EDIT_GROUP_MEMBER+ "/" +  PATH_ACTION_PUT;
    public static final String SUB_CONTEXT_PATH_EDIT_GROUP_MEMBER_DELETE = SUB_CONTEXT_PATH_EDIT_GROUP_MEMBER + "/"
            + "{" + PATH_PARAM_ENC_MEMBER_ID + "}" + "/" +  PATH_ACTION_DELETE;
    public static final String CONTEXT_PATH_EDIT_RESOURCE = CONTEXT_PATH_EDIT_GROUP + "/" +  "{" + PATH_PARAM_ENC_GROUP_ID + "}"
            + "/"+ PATH_RESOURCE_TYPE_RESOURCE;
    public static final String SUB_CONTEXT_PATH_EDIT_RESOURCE_CREATE =  PATH_ACTION_CREATE;
    public static final String SUB_CONTEXT_PATH_EDIT_RESOURCE_DELETE = "{" + PATH_PARAM_ENC_RESOURCE_ID + "}"
            + "/"+ PATH_ACTION_DELETE;
    public static final String SUB_CONTEXT_PATH_EDIT_RESOURCE_UPDATE = "{" + PATH_PARAM_ENC_RESOURCE_ID + "}"
            + "/"+ PATH_ACTION_UPDATE;

    public static final String SUB_CONTEXT_PATH_EDIT_RESOURCE_MEMBER =  "{" + PATH_PARAM_ENC_RESOURCE_ID + "}" + "/" +  PATH_RESOURCE_TYPE_MEMBER;
    public static final String SUB_CONTEXT_PATH_EDIT_RESOURCE_MEMBER_PUT =  SUB_CONTEXT_PATH_EDIT_RESOURCE_MEMBER+ "/" +  PATH_ACTION_PUT;
    public static final String SUB_CONTEXT_PATH_EDIT_RESOURCE_MEMBER_DELETE = SUB_CONTEXT_PATH_EDIT_RESOURCE_MEMBER + "/"
            + "{" + PATH_PARAM_ENC_MEMBER_ID + "}" + "/" +  PATH_ACTION_DELETE;


    public static final String CONTEXT_PATH_EDIT_RESOURCE_SHORT = CONTEXT_PATH_EDIT + "/" +PATH_RESOURCE_TYPE_RESOURCE +
            "/" +  "{" + PATH_PARAM_ENC_RESOURCE_ID + "}";

    public static final String CONTEXT_PATH_EDIT_SUBRESOURCE = CONTEXT_PATH_EDIT_RESOURCE_SHORT + "/" + PATH_RESOURCE_TYPE_SUBRESOURCE;
    public static final String SUB_CONTEXT_PATH_EDIT_SUBRESOURCE_DELETE =  "{" + PATH_PARAM_ENC_SUBRESOURCE_ID + "}" + "/" +  PATH_ACTION_DELETE;

    public static final String CONTEXT_PATH_EDIT_DOCUMENT = CONTEXT_PATH_EDIT + "/" +PATH_RESOURCE_TYPE_RESOURCE +"/" + "{" + PATH_PARAM_ENC_RESOURCE_ID + "}";
    public static final String SUB_CONTEXT_PATH_EDIT_DOCUMENT_GET =  PATH_RESOURCE_TYPE_DOCUMENT;
    public static final String SUB_CONTEXT_PATH_EDIT_DOCUMENT_VALIDATE =  SUB_CONTEXT_PATH_EDIT_DOCUMENT_GET +  "/" + PATH_ACTION_VALIDATE;
    public static final String SUB_CONTEXT_PATH_EDIT_DOCUMENT_GENERATE =  SUB_CONTEXT_PATH_EDIT_DOCUMENT_GET +  "/" + PATH_ACTION_GENERATE;

    public static final String SUB_CONTEXT_PATH_EDIT_DOCUMENT_GET_SUBRESOURCE = PATH_RESOURCE_TYPE_SUBRESOURCE +  "/" +  "{" + PATH_PARAM_ENC_SUBRESOURCE_ID + "}" +  "/" + PATH_RESOURCE_TYPE_DOCUMENT;
    public static final String SUB_CONTEXT_PATH_EDIT_DOCUMENT_SUBRESOURCE_VALIDATE =  SUB_CONTEXT_PATH_EDIT_DOCUMENT_GET_SUBRESOURCE +  "/" + PATH_ACTION_VALIDATE;
    public static final String SUB_CONTEXT_PATH_EDIT_DOCUMENT_SUBRESOURCE_GENERATE =  SUB_CONTEXT_PATH_EDIT_DOCUMENT_GET_SUBRESOURCE +  "/" + PATH_ACTION_GENERATE;
    // public
    public static final String CONTEXT_PATH_PUBLIC_SEARCH_PARTICIPANT = CONTEXT_PATH_PUBLIC + "search";
    public static final String CONTEXT_PATH_PUBLIC_DOMAIN = CONTEXT_PATH_PUBLIC + "domain";
    public static final String CONTEXT_PATH_PUBLIC_APPLICATION = CONTEXT_PATH_PUBLIC + "application";
    public static final String CONTEXT_PATH_PUBLIC_USER = CONTEXT_PATH_PUBLIC + "user";
    public static final String CONTEXT_PATH_PUBLIC_TRUSTSTORE = CONTEXT_PATH_PUBLIC + "truststore";

    public static final String CONTEXT_PATH_PUBLIC_SERVICE_GROUP = CONTEXT_PATH_PUBLIC + "service-group";
    public static final String CONTEXT_PATH_PUBLIC_SERVICE_METADATA = CONTEXT_PATH_PUBLIC + "service-metadata";
    public static final String CONTEXT_PATH_PUBLIC_SECURITY = CONTEXT_PATH_PUBLIC + "security";
    public static final String CONTEXT_PATH_PUBLIC_SECURITY_AUTHENTICATION = CONTEXT_PATH_PUBLIC_SECURITY + "/authentication";
    public static final String CONTEXT_PATH_PUBLIC_SECURITY_USER = CONTEXT_PATH_PUBLIC_SECURITY + "/user";

    //internal
    public static final String CONTEXT_PATH_INTERNAL_ALERT = CONTEXT_PATH_INTERNAL + "alert";
    public static final String CONTEXT_PATH_INTERNAL_DOMAIN = CONTEXT_PATH_INTERNAL + "domain";
    public static final String CONTEXT_PATH_INTERNAL_PROPERTY = CONTEXT_PATH_INTERNAL + "property";
    public static final String CONTEXT_PATH_INTERNAL_APPLICATION = CONTEXT_PATH_INTERNAL + "application";
    public static final String CONTEXT_PATH_INTERNAL_USER = CONTEXT_PATH_INTERNAL + "user";
    public static final String CONTEXT_PATH_INTERNAL_EXTENSION = CONTEXT_PATH_INTERNAL + "extension";
    public static final String CONTEXT_PATH_INTERNAL_KEYSTORE = CONTEXT_PATH_INTERNAL + "keystore";
    public static final String CONTEXT_PATH_INTERNAL_TRUSTSTORE = CONTEXT_PATH_INTERNAL + "truststore";


    // --------------------------------------
    // parameters
    public static final String PARAM_PAGINATION_PAGE = "page";
    public static final String PARAM_PAGINATION_PAGE_SIZE = "pageSize";
    public static final String PARAM_PAGINATION_FILTER = "filter";
    public static final String PARAM_PAGINATION_ORDER_BY = "orderBy";
    public static final String PARAM_PAGINATION_ORDER_TYPE = "orderType";

    public static final String PARAM_NAME_TYPE = "type";
    public static final String PARAM_NAME_VERSION = "version";


    public static final String PARAM_ROLE = "role";

    public static final String PARAM_QUERY_PARTC_ID = "participantIdentifier";
    public static final String PARAM_QUERY_PARTC_SCHEME = "participantScheme";
    public static final String PARAM_QUERY_DOMAIN_CODE = "domainCode";
    public static final String PARAM_QUERY_USER = "user";
    public static final String PARAM_QUERY_PROPERTY = "property";

    private ResourceConstants() {
    }
}
