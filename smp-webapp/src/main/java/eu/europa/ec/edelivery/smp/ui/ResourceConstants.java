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
    private static final char URL_PATH_SEPARATOR = '/';
    public static final String PATH_RESOURCE_TYPE_DOMAIN = "domain";
    public static final String PATH_RESOURCE_TYPE_MEMBER = "member";
    public static final String PATH_RESOURCE_TYPE_GROUP = "group";
    public static final String PATH_RESOURCE_TYPE_RESOURCE = "resource";
    public static final String PATH_RESOURCE_TYPE_SUBRESOURCE = "subresource";
    public static final String PATH_RESOURCE_TYPE_DOCUMENT = "document";
    public static final String PATH_RESOURCE_TYPE_PROPERTY = "property";

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

    public static final String PATH_PARAM_KEYSTORE_TOKEN = "keystore-token";
    public static final String PATH_PARAM_KEYSTORE_TYPE = "keystore-type";

    public static final String PATH_ACTION_DELETE = "delete";
    public static final String PATH_ACTION_UPDATE = "update";
    public static final String PATH_ACTION_CREATE = "create";
    public static final String PATH_ACTION_PUT = "put";
    public static final String PATH_ACTION_VALIDATE = "validate";
    public static final String PATH_ACTION_GENERATE = "generate";
    public static final String PATH_ACTION_UPDATE_RESOURCE_TYPES = "update-resource-types";
    public static final String PATH_ACTION_UPDATE_SML_DATA = "update-sml-integration-data";
    public static final String PATH_ACTION_RESET_CREDENTIAL_REQUEST = "request-reset-credential";
    public static final String PATH_ACTION_RESET_CREDENTIAL = "reset-credential";
    public static final String PATH_ACTION_AUTHENTICATION = "authentication";
    public static final String PATH_ACTION_GENERATE_DNS_QUERY = "generate-dns-query";
    public static final String PATH_ACTION_RETRIEVE = "retrieve";
    public static final String PATH_ACTION_SEARCH = "search";

    public static final String PATH_ACTION_SML_REGISTER = "sml-register";
    public static final String PATH_ACTION_SML_UNREGISTER = "sml-unregister";

    // --------------------------------------
    // context paths
    public static final String CONTEXT_PATH_PUBLIC = "/ui/public/rest/";
    public static final String CONTEXT_PATH_INTERNAL = "/ui/internal/rest/";
    public static final String CONTEXT_PATH_INTERNAL_USERID = CONTEXT_PATH_INTERNAL+ "{" + PATH_PARAM_ENC_USER_ID + "}/";

    public static final String CONTEXT_PATH_EDIT = "/ui/edit/rest/" + "{" + PATH_PARAM_ENC_USER_ID + "}";
    // edit domain data paths
    public static final String CONTEXT_PATH_EDIT_DOMAIN = CONTEXT_PATH_EDIT + URL_PATH_SEPARATOR + PATH_RESOURCE_TYPE_DOMAIN;
    public static final String SUB_CONTEXT_PATH_EDIT_DOMAIN_ADMIN = "{" + PATH_PARAM_ENC_DOMAIN_ID + "}";
    public static final String SUB_CONTEXT_PATH_EDIT_DOMAIN_MEMBER = SUB_CONTEXT_PATH_EDIT_DOMAIN_ADMIN + URL_PATH_SEPARATOR + PATH_RESOURCE_TYPE_MEMBER;
    public static final String SUB_CONTEXT_PATH_EDIT_DOMAIN_MEMBER_PUT = SUB_CONTEXT_PATH_EDIT_DOMAIN_MEMBER + URL_PATH_SEPARATOR + PATH_ACTION_PUT;
    public static final String SUB_CONTEXT_PATH_EDIT_DOMAIN_MEMBER_DELETE = SUB_CONTEXT_PATH_EDIT_DOMAIN_MEMBER + URL_PATH_SEPARATOR
            + "{" + PATH_PARAM_ENC_MEMBER_ID + "}" + URL_PATH_SEPARATOR +  PATH_ACTION_DELETE;
    // domain resource definition
    public static final String SUB_CONTEXT_PATH_EDIT_DOMAIN_RESOURCE_DEF = SUB_CONTEXT_PATH_EDIT_DOMAIN_ADMIN + URL_PATH_SEPARATOR + PATH_RESOURCE_TYPE_RESOURCE_DEFINITION;
    public static final String SUB_CONTEXT_PATH_EDIT_DOMAIN_PROPERTIES= SUB_CONTEXT_PATH_EDIT_DOMAIN_ADMIN + URL_PATH_SEPARATOR + PATH_RESOURCE_TYPE_PROPERTY;
    public static final String SUB_CONTEXT_PATH_EDIT_DOMAIN_PROPERTIES_VALIDATE= SUB_CONTEXT_PATH_EDIT_DOMAIN_PROPERTIES + URL_PATH_SEPARATOR + PATH_ACTION_VALIDATE;

    // ------------------------------------------
    // group management
    public static final String CONTEXT_PATH_EDIT_GROUP = CONTEXT_PATH_EDIT_DOMAIN + URL_PATH_SEPARATOR +  SUB_CONTEXT_PATH_EDIT_DOMAIN_ADMIN
            + URL_PATH_SEPARATOR+ PATH_RESOURCE_TYPE_GROUP;
    public static final String SUB_CONTEXT_PATH_EDIT_GROUP_CREATE =  PATH_ACTION_CREATE;
    public static final String SUB_CONTEXT_PATH_EDIT_GROUP_UPDATE =  "{" + PATH_PARAM_ENC_GROUP_ID + "}" + URL_PATH_SEPARATOR +   PATH_ACTION_UPDATE;
    public static final String SUB_CONTEXT_PATH_EDIT_GROUP_DELETE =  "{" + PATH_PARAM_ENC_GROUP_ID + "}" + URL_PATH_SEPARATOR +  PATH_ACTION_DELETE;
    public static final String SUB_CONTEXT_PATH_EDIT_GROUP_MEMBER =  "{" + PATH_PARAM_ENC_GROUP_ID + "}" + URL_PATH_SEPARATOR +  PATH_RESOURCE_TYPE_MEMBER;
    public static final String SUB_CONTEXT_PATH_EDIT_GROUP_MEMBER_PUT =  SUB_CONTEXT_PATH_EDIT_GROUP_MEMBER+ URL_PATH_SEPARATOR +  PATH_ACTION_PUT;
    public static final String SUB_CONTEXT_PATH_EDIT_GROUP_MEMBER_DELETE = SUB_CONTEXT_PATH_EDIT_GROUP_MEMBER + URL_PATH_SEPARATOR
            + "{" + PATH_PARAM_ENC_MEMBER_ID + "}" + URL_PATH_SEPARATOR +  PATH_ACTION_DELETE;
    public static final String CONTEXT_PATH_EDIT_RESOURCE = CONTEXT_PATH_EDIT_GROUP + URL_PATH_SEPARATOR +  "{" + PATH_PARAM_ENC_GROUP_ID + "}"
            + URL_PATH_SEPARATOR+ PATH_RESOURCE_TYPE_RESOURCE;
    public static final String SUB_CONTEXT_PATH_EDIT_RESOURCE_CREATE =  PATH_ACTION_CREATE;
    public static final String SUB_CONTEXT_PATH_EDIT_RESOURCE_DELETE = "{" + PATH_PARAM_ENC_RESOURCE_ID + "}"
            + URL_PATH_SEPARATOR+ PATH_ACTION_DELETE;
    public static final String SUB_CONTEXT_PATH_EDIT_RESOURCE_UPDATE = "{" + PATH_PARAM_ENC_RESOURCE_ID + "}"
            + URL_PATH_SEPARATOR+ PATH_ACTION_UPDATE;

    public static final String SUB_CONTEXT_PATH_EDIT_RESOURCE_MEMBER =  "{" + PATH_PARAM_ENC_RESOURCE_ID + "}" + URL_PATH_SEPARATOR +  PATH_RESOURCE_TYPE_MEMBER;
    public static final String SUB_CONTEXT_PATH_EDIT_RESOURCE_MEMBER_PUT =  SUB_CONTEXT_PATH_EDIT_RESOURCE_MEMBER+ URL_PATH_SEPARATOR +  PATH_ACTION_PUT;
    public static final String SUB_CONTEXT_PATH_EDIT_RESOURCE_MEMBER_DELETE = SUB_CONTEXT_PATH_EDIT_RESOURCE_MEMBER + URL_PATH_SEPARATOR
            + "{" + PATH_PARAM_ENC_MEMBER_ID + "}" + URL_PATH_SEPARATOR +  PATH_ACTION_DELETE;


    public static final String CONTEXT_PATH_EDIT_RESOURCE_SHORT = CONTEXT_PATH_EDIT + URL_PATH_SEPARATOR +PATH_RESOURCE_TYPE_RESOURCE +
            URL_PATH_SEPARATOR +  "{" + PATH_PARAM_ENC_RESOURCE_ID + "}";

    public static final String CONTEXT_PATH_EDIT_SUBRESOURCE = CONTEXT_PATH_EDIT_RESOURCE_SHORT + URL_PATH_SEPARATOR + PATH_RESOURCE_TYPE_SUBRESOURCE;
    public static final String SUB_CONTEXT_PATH_EDIT_SUBRESOURCE_DELETE =  "{" + PATH_PARAM_ENC_SUBRESOURCE_ID + "}" + URL_PATH_SEPARATOR +  PATH_ACTION_DELETE;

    public static final String CONTEXT_PATH_EDIT_DOCUMENT = CONTEXT_PATH_EDIT + URL_PATH_SEPARATOR +PATH_RESOURCE_TYPE_RESOURCE +URL_PATH_SEPARATOR + "{" + PATH_PARAM_ENC_RESOURCE_ID + "}";
    public static final String SUB_CONTEXT_PATH_EDIT_DOCUMENT_GET =  PATH_RESOURCE_TYPE_DOCUMENT;
    public static final String SUB_CONTEXT_PATH_EDIT_DOCUMENT_VALIDATE =  SUB_CONTEXT_PATH_EDIT_DOCUMENT_GET +  URL_PATH_SEPARATOR + PATH_ACTION_VALIDATE;
    public static final String SUB_CONTEXT_PATH_EDIT_DOCUMENT_GENERATE =  SUB_CONTEXT_PATH_EDIT_DOCUMENT_GET +  URL_PATH_SEPARATOR + PATH_ACTION_GENERATE;

    public static final String SUB_CONTEXT_PATH_EDIT_DOCUMENT_GET_SUBRESOURCE = PATH_RESOURCE_TYPE_SUBRESOURCE +  URL_PATH_SEPARATOR +  "{" + PATH_PARAM_ENC_SUBRESOURCE_ID + "}" +  URL_PATH_SEPARATOR + PATH_RESOURCE_TYPE_DOCUMENT;
    public static final String SUB_CONTEXT_PATH_EDIT_DOCUMENT_SUBRESOURCE_VALIDATE =  SUB_CONTEXT_PATH_EDIT_DOCUMENT_GET_SUBRESOURCE +  URL_PATH_SEPARATOR + PATH_ACTION_VALIDATE;
    public static final String SUB_CONTEXT_PATH_EDIT_DOCUMENT_SUBRESOURCE_GENERATE =  SUB_CONTEXT_PATH_EDIT_DOCUMENT_GET_SUBRESOURCE +  URL_PATH_SEPARATOR + PATH_ACTION_GENERATE;
    // public
    public static final String CONTEXT_PATH_PUBLIC_SEARCH_PARTICIPANT = CONTEXT_PATH_PUBLIC + PATH_ACTION_SEARCH;
    public static final String CONTEXT_PATH_PUBLIC_SEARCH_PARTICIPANT_METADATA = CONTEXT_PATH_PUBLIC_SEARCH_PARTICIPANT + "/metadata";
    public static final String CONTEXT_PATH_PUBLIC_DOMAIN = CONTEXT_PATH_PUBLIC + PATH_RESOURCE_TYPE_DOMAIN;
    public static final String CONTEXT_PATH_PUBLIC_APPLICATION = CONTEXT_PATH_PUBLIC + "application";
    public static final String CONTEXT_PATH_PUBLIC_USER = CONTEXT_PATH_PUBLIC + "user";
    public static final String CONTEXT_PATH_PUBLIC_TRUSTSTORE = CONTEXT_PATH_PUBLIC + "truststore";
    public static final String CONTEXT_PATH_PUBLIC_DNS_TOOLS = CONTEXT_PATH_PUBLIC + "dns-tools";

    public static final String CONTEXT_PATH_PUBLIC_SECURITY = CONTEXT_PATH_PUBLIC + "security";
    public static final String CONTEXT_PATH_PUBLIC_SECURITY_AUTHENTICATION = CONTEXT_PATH_PUBLIC_SECURITY + "/authentication";
    public static final String CONTEXT_PATH_PUBLIC_SECURITY_USER = CONTEXT_PATH_PUBLIC_SECURITY + "/user";
    // --------------------------------------
    //internal
    public static final String CONTEXT_PATH_INTERNAL_DOMAIN = CONTEXT_PATH_INTERNAL_USERID + PATH_RESOURCE_TYPE_DOMAIN;
    public static final String CONTEXT_PATH_INTERNAL_ALERT = CONTEXT_PATH_INTERNAL + "alert";
    public static final String CONTEXT_PATH_INTERNAL_PROPERTY = CONTEXT_PATH_INTERNAL + PATH_RESOURCE_TYPE_PROPERTY;
    public static final String CONTEXT_PATH_INTERNAL_APPLICATION = CONTEXT_PATH_INTERNAL + "application";
    public static final String CONTEXT_PATH_INTERNAL_USER = CONTEXT_PATH_INTERNAL + "user";
    public static final String CONTEXT_PATH_INTERNAL_EXTENSION = CONTEXT_PATH_INTERNAL + "extension";
    public static final String CONTEXT_PATH_INTERNAL_KEYSTORE = CONTEXT_PATH_INTERNAL + "keystore";
    public static final String CONTEXT_PATH_INTERNAL_TRUSTSTORE = CONTEXT_PATH_INTERNAL + "truststore";

    // internal domain paths
    public static final String SUB_CONTEXT_INTERNAL_DOMAIN_PROPERTIES=  "/{" + PATH_PARAM_ENC_DOMAIN_ID + "}/" + PATH_RESOURCE_TYPE_PROPERTY;
    public static final String SUB_CONTEXT_INTERNAL_DOMAIN_CREATE=  "/"+PATH_ACTION_CREATE;
    public static final String SUB_CONTEXT_INTERNAL_DOMAIN_DELETE=  "/{" + PATH_PARAM_ENC_DOMAIN_ID + "}/" + PATH_ACTION_DELETE;
    public static final String SUB_CONTEXT_INTERNAL_DOMAIN_UPDATE=  "/{" + PATH_PARAM_ENC_DOMAIN_ID + "}/" + PATH_ACTION_UPDATE;
    public static final String SUB_CONTEXT_INTERNAL_DOMAIN_UPDATE_RESOURCE_TYPES=  "/{" + PATH_PARAM_ENC_DOMAIN_ID + "}/" + PATH_ACTION_UPDATE_RESOURCE_TYPES;
    public static final String SUB_CONTEXT_INTERNAL_DOMAIN_UPDATE_SML_DATA=  "/{" + PATH_PARAM_ENC_DOMAIN_ID + "}/" + PATH_ACTION_UPDATE_SML_DATA;
    public static final String SUB_CONTEXT_INTERNAL_DOMAIN_UPDATE_SML_REGISTER=  "/{" + PATH_PARAM_ENC_DOMAIN_ID + "}/" + PATH_ACTION_SML_REGISTER;
    public static final String SUB_CONTEXT_INTERNAL_DOMAIN_UPDATE_SML_UNREGISTER=  "/{" + PATH_PARAM_ENC_DOMAIN_ID + "}/" + PATH_ACTION_SML_UNREGISTER;

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
    public static final String PARAM_QUERY_DOCUMENT_TYPE = "documentType";

    private ResourceConstants() {
    }
}
