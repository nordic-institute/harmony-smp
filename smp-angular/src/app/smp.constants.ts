export class SmpConstants {

  public static readonly EXPANDED_MENU_WIDTH: string = "180px"
  public static readonly COLLAPSED_MENU_WIDTH: string = "50px"
  public static readonly NULL_VALUE: string = "-----------"
  public static readonly DATE_TIME_FORMAT = 'dd/MM/yyyy HH:mm:ss z';
  public static readonly DATE_FORMAT = 'dd/MM/yyyy';

  /* URL resource actions */
  public static readonly PATH_ACTION_DELETE: string = 'delete';
  public static readonly PATH_ACTION_UPDATE: string = 'update';
  public static readonly PATH_ACTION_CREATE: string = 'create';

  public static readonly PATH_ACTION_GENERATE: string = 'generate';
  public static readonly PATH_ACTION_VALIDATE: string = 'validate';
  public static readonly PATH_ACTION_PUT: string = 'put';
  public static readonly PATH_ACTION_RETRIEVE: string = 'retrieve';
  public static readonly PATH_ACTION_SEARCH: string = 'search';
  public static readonly PATH_ACTION_UPDATE_RESOURCE_TYPES: string = 'update-resource-types';
  public static readonly PATH_ACTION_UPDATE_SML_INTEGRATION: string = 'update-sml-integration-data';
  public static readonly PATH_ACTION_GENERATE_DNS_QUERY: string = 'generate-dns-query';
  /* URL variables */
  public static readonly PATH_PARAM_ENC_USER_ID: string = '{user-id}';
  public static readonly PATH_PARAM_ENC_DOMAIN_ID: string = '{domain-id}';
  public static readonly PATH_PARAM_ENC_MEMBER_ID: string = '{member-id}';
  public static readonly PATH_PARAM_ENC_GROUP_ID: string = '{group-id}';
  public static readonly PATH_PARAM_ENC_RESOURCE_ID: string = '{resource-id}';
  public static readonly PATH_PARAM_ENC_SUBRESOURCE_ID: string = '{subresource-id}';
  public static readonly PATH_PARAM_CERT_ALIAS: string = '{cert-alias}';
  public static readonly PATH_PARAM_ENC_CREDENTIAL_ID: string = '{credential-id}';
  public static readonly PATH_PARAM_ENC_MANAGED_USER_ID: string = '{managed-user-id}';

  public static readonly PATH_PARAM_KEYSTORE_PWD: string = '{keystore-pwd}';
  public static readonly PATH_PARAM_KEYSTORE_TYPE: string = '{keystore-type}';

  public static readonly PATH_RESOURCE_TYPE_ALERT: string = 'alert';
  public static readonly PATH_RESOURCE_TYPE_DOMAIN: string = 'domain';
  public static readonly PATH_RESOURCE_TYPE_MEMBER: string = 'member';
  public static readonly PATH_RESOURCE_TYPE_GROUP: string = 'group';
  public static readonly PATH_RESOURCE_TYPE_PROPERTY: string = 'property';
  public static readonly PATH_DNS_TOOLS: string = 'dns-tools';

  public static readonly PATH_RESOURCE_TYPE_RESOURCE_DEF: string = 'res-def';

  public static readonly PATH_RESOURCE_TYPE_RESOURCE: string = 'resource';
  public static readonly PATH_RESOURCE_TYPE_SUBRESOURCE: string = 'subresource';
  public static readonly PATH_RESOURCE_TYPE_DOCUMENT: string = 'document';
  public static readonly PATH_QUERY_FILTER_TYPE: string = 'type'


  //------------------------------
  // public endpoints
  public static readonly REST_PUBLIC = 'public/rest/';
  public static readonly REST_INTERNAL = 'internal/rest/';
  public static readonly REST_EDIT = 'edit/rest/' + SmpConstants.PATH_PARAM_ENC_USER_ID + '/';


  public static readonly REST_EDIT_RESOURCE_SHORT = SmpConstants.REST_EDIT + SmpConstants.PATH_RESOURCE_TYPE_RESOURCE + '/' + SmpConstants.PATH_PARAM_ENC_RESOURCE_ID;

  public static readonly REST_EDIT_DOCUMENT = SmpConstants.REST_EDIT_RESOURCE_SHORT + '/' + SmpConstants.PATH_RESOURCE_TYPE_DOCUMENT;
  public static readonly REST_EDIT_DOCUMENT_VALIDATE = SmpConstants.REST_EDIT_DOCUMENT + '/' + SmpConstants.PATH_ACTION_VALIDATE;
  public static readonly REST_EDIT_DOCUMENT_GENERATE = SmpConstants.REST_EDIT_DOCUMENT + '/' + SmpConstants.PATH_ACTION_GENERATE;
  public static readonly REST_EDIT_DOCUMENT_SUBRESOURCE = SmpConstants.REST_EDIT_RESOURCE_SHORT + '/' + SmpConstants.PATH_RESOURCE_TYPE_SUBRESOURCE + '/' + SmpConstants.PATH_PARAM_ENC_SUBRESOURCE_ID
    + '/' + SmpConstants.PATH_RESOURCE_TYPE_DOCUMENT;

  public static readonly REST_EDIT_DOCUMENT_SUBRESOURCE_VALIDATE = SmpConstants.REST_EDIT_DOCUMENT_SUBRESOURCE + '/' + SmpConstants.PATH_ACTION_VALIDATE;
  public static readonly REST_EDIT_DOCUMENT_SUBRESOURCE_GENERATE = SmpConstants.REST_EDIT_DOCUMENT_SUBRESOURCE + '/' + SmpConstants.PATH_ACTION_GENERATE;

  public static readonly REST_EDIT_SUBRESOURCE = SmpConstants.REST_EDIT_RESOURCE_SHORT + '/' + SmpConstants.PATH_RESOURCE_TYPE_SUBRESOURCE;
  public static readonly REST_EDIT_SUBRESOURCE_DELETE = SmpConstants.REST_EDIT_SUBRESOURCE + '/' + SmpConstants.PATH_PARAM_ENC_SUBRESOURCE_ID
    + '/' + SmpConstants.PATH_ACTION_DELETE;
  public static readonly REST_EDIT_SUBRESOURCE_CREATE = SmpConstants.REST_EDIT_SUBRESOURCE + '/' + SmpConstants.PATH_ACTION_CREATE;

  /* Public services */
  public static readonly REST_PUBLIC_SEARCH_RESOURCE = SmpConstants.REST_PUBLIC + SmpConstants.PATH_ACTION_SEARCH;
  public static readonly REST_PUBLIC_SEARCH_RESOURCE_METADATA = SmpConstants.REST_PUBLIC + SmpConstants.PATH_ACTION_SEARCH + "/metadata";
  public static readonly REST_PUBLIC_DOMAIN = SmpConstants.REST_PUBLIC + SmpConstants.PATH_RESOURCE_TYPE_DOMAIN;
  public static readonly REST_PUBLIC_DNS_TOOLS = SmpConstants.REST_PUBLIC + SmpConstants.PATH_DNS_TOOLS;
  public static readonly REST_PUBLIC_DNS_TOOLS_GEN_QUERY: string = SmpConstants.REST_PUBLIC_DNS_TOOLS + '/' + SmpConstants.PATH_ACTION_GENERATE_DNS_QUERY;

  /* Public edit services */
  public static readonly REST_EDIT_DOMAIN = SmpConstants.REST_EDIT + SmpConstants.PATH_RESOURCE_TYPE_DOMAIN;
  public static readonly REST_EDIT_DOMAIN_MANAGE = SmpConstants.REST_EDIT_DOMAIN + '/' + SmpConstants.PATH_PARAM_ENC_DOMAIN_ID;
  public static readonly REST_EDIT_DOMAIN_MEMBER = SmpConstants.REST_EDIT_DOMAIN_MANAGE + '/' + SmpConstants.PATH_RESOURCE_TYPE_MEMBER;
  public static readonly REST_EDIT_DOMAIN_MEMBER_PUT = SmpConstants.REST_EDIT_DOMAIN_MEMBER + '/' + SmpConstants.PATH_ACTION_PUT;
  public static readonly REST_EDIT_DOMAIN_MEMBER_DELETE = SmpConstants.REST_EDIT_DOMAIN_MEMBER + '/' + SmpConstants.PATH_PARAM_ENC_MEMBER_ID + '/' + SmpConstants.PATH_ACTION_DELETE;

  public static readonly REST_EDIT_DOMAIN_PROPERTIES = SmpConstants.REST_EDIT_DOMAIN_MANAGE + '/' + SmpConstants.PATH_RESOURCE_TYPE_PROPERTY;
  public static readonly REST_EDIT_DOMAIN_PROPERTY_VALIDATE = SmpConstants.REST_EDIT_DOMAIN_PROPERTIES + '/' + SmpConstants.PATH_ACTION_VALIDATE;
  // group endpoints
  public static readonly REST_EDIT_DOMAIN_GROUP = SmpConstants.REST_EDIT_DOMAIN_MANAGE + '/' + SmpConstants.PATH_RESOURCE_TYPE_GROUP;

  public static readonly REST_EDIT_DOMAIN_GROUP_CREATE = SmpConstants.REST_EDIT_DOMAIN_GROUP + '/' + SmpConstants.PATH_ACTION_CREATE;
  public static readonly REST_EDIT_DOMAIN_GROUP_DELETE = SmpConstants.REST_EDIT_DOMAIN_GROUP
    + '/' + SmpConstants.PATH_PARAM_ENC_GROUP_ID + '/' + SmpConstants.PATH_ACTION_DELETE;
  public static readonly REST_EDIT_DOMAIN_GROUP_UPDATE = SmpConstants.REST_EDIT_DOMAIN_GROUP
    + '/' + SmpConstants.PATH_PARAM_ENC_GROUP_ID + '/' + SmpConstants.PATH_ACTION_UPDATE;
  public static readonly REST_EDIT_GROUP_MEMBER = SmpConstants.REST_EDIT_DOMAIN_GROUP + '/' + SmpConstants.PATH_PARAM_ENC_GROUP_ID
    + '/' + SmpConstants.PATH_RESOURCE_TYPE_MEMBER;
  public static readonly REST_EDIT_GROUP_MEMBER_PUT = SmpConstants.REST_EDIT_GROUP_MEMBER + '/' + SmpConstants.PATH_ACTION_PUT;
  public static readonly REST_EDIT_GROUP_MEMBER_DELETE = SmpConstants.REST_EDIT_GROUP_MEMBER + '/' + SmpConstants.PATH_PARAM_ENC_MEMBER_ID
    + '/' + SmpConstants.PATH_ACTION_DELETE;
  public static readonly REST_EDIT_DOMAIN_RESOURCE_DEFS = SmpConstants.REST_EDIT_DOMAIN_MANAGE + '/' + SmpConstants.PATH_RESOURCE_TYPE_RESOURCE_DEF;

  public static readonly REST_EDIT_RESOURCE = SmpConstants.REST_EDIT_DOMAIN_GROUP + '/' + SmpConstants.PATH_PARAM_ENC_GROUP_ID
    + '/' + SmpConstants.PATH_RESOURCE_TYPE_RESOURCE;
  public static readonly REST_EDIT_RESOURCE_CREATE = SmpConstants.REST_EDIT_RESOURCE + '/' + SmpConstants.PATH_ACTION_CREATE
  public static readonly REST_EDIT_RESOURCE_UPDATE = SmpConstants.REST_EDIT_RESOURCE + '/' + SmpConstants.PATH_PARAM_ENC_RESOURCE_ID
    + '/' + SmpConstants.PATH_ACTION_UPDATE;
  public static readonly REST_EDIT_RESOURCE_DELETE = SmpConstants.REST_EDIT_RESOURCE + '/' + SmpConstants.PATH_PARAM_ENC_RESOURCE_ID
    + '/' + SmpConstants.PATH_ACTION_DELETE;

  public static readonly REST_EDIT_RESOURCE_MEMBER = SmpConstants.REST_EDIT_RESOURCE + '/' + SmpConstants.PATH_PARAM_ENC_RESOURCE_ID
    + '/' + SmpConstants.PATH_RESOURCE_TYPE_MEMBER;
  public static readonly REST_EDIT_RESOURCE_MEMBER_PUT = SmpConstants.REST_EDIT_RESOURCE_MEMBER + '/' + SmpConstants.PATH_ACTION_PUT;
  public static readonly REST_EDIT_RESOURCE_MEMBER_DELETE = SmpConstants.REST_EDIT_RESOURCE_MEMBER + '/' + SmpConstants.PATH_PARAM_ENC_MEMBER_ID
    + '/' + SmpConstants.PATH_ACTION_DELETE;

  // legacy
  public static readonly REST_PUBLIC_GROUP_EDIT = SmpConstants.REST_PUBLIC + SmpConstants.PATH_RESOURCE_TYPE_GROUP + '/' + SmpConstants.PATH_PARAM_ENC_USER_ID;
  public static readonly REST_PUBLIC_GROUP_DOMAIN = SmpConstants.REST_PUBLIC_GROUP_EDIT + '/' +
    SmpConstants.PATH_RESOURCE_TYPE_DOMAIN + '/' + SmpConstants.PATH_PARAM_ENC_DOMAIN_ID;

  public static readonly REST_PUBLIC_GROUP_DOMAIN_CREATE = SmpConstants.REST_PUBLIC_GROUP_EDIT + '/' +
    SmpConstants.PATH_RESOURCE_TYPE_DOMAIN + '/' + SmpConstants.PATH_PARAM_ENC_DOMAIN_ID + '/' + SmpConstants.PATH_ACTION_CREATE;

  public static readonly REST_PUBLIC_GROUP_DOMAIN_DELETE = SmpConstants.REST_PUBLIC_GROUP_EDIT + '/' + SmpConstants.PATH_PARAM_ENC_GROUP_ID + '/' +
    SmpConstants.PATH_RESOURCE_TYPE_DOMAIN + '/' + SmpConstants.PATH_PARAM_ENC_DOMAIN_ID + '/' + SmpConstants.PATH_ACTION_DELETE;

  public static readonly REST_PUBLIC_GROUP_DOMAIN_UPDATE = SmpConstants.REST_PUBLIC_GROUP_EDIT + '/' + SmpConstants.PATH_PARAM_ENC_GROUP_ID + '/' +
    SmpConstants.PATH_RESOURCE_TYPE_DOMAIN + '/' + SmpConstants.PATH_PARAM_ENC_DOMAIN_ID + '/' + SmpConstants.PATH_ACTION_UPDATE;


  public static readonly REST_PUBLIC_GROUP_MEMBERS = SmpConstants.REST_PUBLIC_GROUP_EDIT +
    '/' + SmpConstants.PATH_PARAM_ENC_GROUP_ID + '/' + "members";
  public static readonly REST_PUBLIC_GROUP_MEMBERS_ADD = SmpConstants.REST_PUBLIC_GROUP_EDIT
    + '/' + SmpConstants.PATH_PARAM_ENC_GROUP_ID + '/' + "member";
  public static readonly REST_PUBLIC_GROUP_MEMBERS_DELETE = SmpConstants.REST_PUBLIC_GROUP_EDIT
    + '/' + SmpConstants.PATH_PARAM_ENC_GROUP_ID + '/' + "member"
    + '/' + SmpConstants.PATH_PARAM_ENC_MEMBER_ID + '/' + SmpConstants.PATH_ACTION_DELETE;


  public static readonly REST_PUBLIC_APPLICATION_INFO = SmpConstants.REST_PUBLIC + 'application/info';
  public static readonly REST_PUBLIC_APPLICATION_CONFIG = SmpConstants.REST_PUBLIC + 'application/config';
  // user public services
  public static readonly REST_PUBLIC_USER = SmpConstants.REST_PUBLIC + 'user';


  public static readonly REST_PUBLIC_USER_MANAGE = SmpConstants.REST_PUBLIC_USER + '/' + SmpConstants.PATH_PARAM_ENC_USER_ID + '/';
  public static readonly REST_PUBLIC_USER_ALERT = SmpConstants.REST_PUBLIC_USER_MANAGE + 'alert';
  public static readonly REST_PUBLIC_USER_GENERATE_ACCESS_TOKEN = SmpConstants.REST_PUBLIC_USER_MANAGE + 'generate-access-token';
  public static readonly REST_PUBLIC_USER_CHANGE_PASSWORD = SmpConstants.REST_PUBLIC_USER_MANAGE + 'change-password';

  public static readonly REST_PUBLIC_USER_SEARCH = SmpConstants.REST_PUBLIC_USER_MANAGE + SmpConstants.PATH_ACTION_SEARCH;
  // truststore public services
  public static readonly REST_PUBLIC_TRUSTSTORE = SmpConstants.REST_PUBLIC + "truststore/" + '/' + SmpConstants.PATH_PARAM_ENC_USER_ID + '/';
  public static readonly REST_PUBLIC_TRUSTSTORE_CERT_VALIDATE = SmpConstants.REST_PUBLIC_TRUSTSTORE + 'validate-certificate';

  // public authentication services
  public static readonly REST_PUBLIC_SECURITY = SmpConstants.REST_PUBLIC + 'security/';
  public static readonly REST_PUBLIC_SECURITY_AUTHENTICATION = SmpConstants.REST_PUBLIC_SECURITY + 'authentication'
  public static readonly REST_PUBLIC_SECURITY_RESET_CREDENTIALS_REQUEST = SmpConstants.REST_PUBLIC_SECURITY + 'request-reset-credential';
  public static readonly REST_PUBLIC_SECURITY_RESET_CREDENTIALS = SmpConstants.REST_PUBLIC_SECURITY + 'reset-credential';

  public static readonly REST_PUBLIC_SECURITY_USER = SmpConstants.REST_PUBLIC_SECURITY + 'user';

  //------------------------------
  // internal endpoints
  public static readonly REST_INTERNAL_ALERT_MANAGE = SmpConstants.REST_INTERNAL + SmpConstants.PATH_RESOURCE_TYPE_ALERT +
    '/' + SmpConstants.PATH_PARAM_ENC_USER_ID;

  public static readonly REST_INTERNAL_DOMAIN_MANAGE_DEPRECATED = SmpConstants.REST_INTERNAL + SmpConstants.PATH_RESOURCE_TYPE_DOMAIN;

  public static readonly REST_INTERNAL_DOMAIN_MANAGE = SmpConstants.REST_INTERNAL
    + SmpConstants.PATH_PARAM_ENC_USER_ID + '/' + SmpConstants.PATH_RESOURCE_TYPE_DOMAIN

  public static readonly REST_INTERNAL_DOMAIN_PROPERTIES_MANAGE = SmpConstants.REST_INTERNAL_DOMAIN_MANAGE
    + '/'  + SmpConstants.PATH_PARAM_ENC_DOMAIN_ID + '/' + SmpConstants.PATH_RESOURCE_TYPE_PROPERTY

  public static readonly REST_INTERNAL_DOMAIN_MANAGE_DELETE = SmpConstants.REST_INTERNAL_DOMAIN_MANAGE
    + '/' +  SmpConstants.PATH_PARAM_ENC_DOMAIN_ID + '/' + SmpConstants.PATH_ACTION_DELETE;

  public static readonly REST_INTERNAL_DOMAIN_MANAGE_UPDATE = SmpConstants.REST_INTERNAL_DOMAIN_MANAGE
    + '/' + SmpConstants.PATH_PARAM_ENC_DOMAIN_ID + '/' + SmpConstants.PATH_ACTION_UPDATE;

  public static readonly REST_INTERNAL_DOMAIN_MANAGE_CREATE = SmpConstants.REST_INTERNAL_DOMAIN_MANAGE
    + '/' + SmpConstants.PATH_ACTION_CREATE;

  public static readonly REST_INTERNAL_DOMAIN_MANAGE_UPDATE_SML_INTEGRATION = SmpConstants.REST_INTERNAL_DOMAIN_MANAGE
    + '/' + SmpConstants.PATH_PARAM_ENC_DOMAIN_ID + '/' + SmpConstants.PATH_ACTION_UPDATE_SML_INTEGRATION;

  public static readonly REST_INTERNAL_DOMAIN_MANAGE_UPDATE_RESOURCE_TYPES = SmpConstants.REST_INTERNAL_DOMAIN_MANAGE
    + '/' + SmpConstants.PATH_PARAM_ENC_DOMAIN_ID + '/' + SmpConstants.PATH_ACTION_UPDATE_RESOURCE_TYPES;

  public static readonly REST_INTERNAL_EXTENSION_MANAGE = SmpConstants.REST_INTERNAL + 'extension';
  public static readonly REST_INTERNAL_PROPERTY_MANAGE = SmpConstants.REST_INTERNAL + 'property';
  public static readonly REST_INTERNAL_PROPERTY_VALIDATE = SmpConstants.REST_INTERNAL_PROPERTY_MANAGE + '/validate';
  public static readonly REST_INTERNAL_DOMAIN_VALIDATE_DELETE = SmpConstants.REST_INTERNAL_DOMAIN_MANAGE_DEPRECATED + '/validate-delete';
  public static readonly REST_INTERNAL_USER_MANAGE = SmpConstants.REST_INTERNAL + 'user' + '/' + SmpConstants.PATH_PARAM_ENC_USER_ID;

  public static readonly REST_INTERNAL_USER_MANAGE_CREATE = SmpConstants.REST_INTERNAL_USER_MANAGE + '/' + SmpConstants.PATH_ACTION_CREATE;
  public static readonly REST_INTERNAL_USER_MANAGE_UPDATE = SmpConstants.REST_INTERNAL_USER_MANAGE + '/' + SmpConstants.PATH_PARAM_ENC_MANAGED_USER_ID + '/' + SmpConstants.PATH_ACTION_UPDATE;
  public static readonly REST_INTERNAL_USER_MANAGE_DELETE = SmpConstants.REST_INTERNAL_USER_MANAGE + '/' + SmpConstants.PATH_PARAM_ENC_MANAGED_USER_ID + '/' + SmpConstants.PATH_ACTION_DELETE;

  public static readonly INTERNAL_USER_MANAGE_SEARCH = SmpConstants.REST_INTERNAL_USER_MANAGE + '/' + SmpConstants.PATH_ACTION_SEARCH;

  public static readonly REST_INTERNAL_USER_MANAGE_DATA = SmpConstants.REST_INTERNAL_USER_MANAGE
    + '/' + SmpConstants.PATH_PARAM_ENC_MANAGED_USER_ID + '/' + SmpConstants.PATH_ACTION_RETRIEVE;


  public static readonly REST_INTERNAL_USER_GENERATE_ACCESS_TOKEN = SmpConstants.REST_INTERNAL_USER_MANAGE +
    '/' + 'generate-access-token-for' + '/' + SmpConstants.PATH_PARAM_ENC_MANAGED_USER_ID;

  public static readonly REST_INTERNAL_USER_CHANGE_PASSWORD = SmpConstants.REST_INTERNAL_USER_MANAGE +
    '/' + 'change-password-for' + '/' + SmpConstants.PATH_PARAM_ENC_MANAGED_USER_ID;

  public static readonly REST_INTERNAL_USER_VALIDATE_DELETE = `${SmpConstants.REST_INTERNAL_USER_MANAGE}/validate-delete`;
  public static readonly REST_INTERNAL_KEYSTORE_DEPRECATED = SmpConstants.REST_INTERNAL + 'keystore';
  public static readonly REST_INTERNAL_KEYSTORE_MANAGE = SmpConstants.REST_INTERNAL + 'keystore' + '/' + SmpConstants.PATH_PARAM_ENC_USER_ID;

  public static readonly REST_INTERNAL_KEYSTORE_UPLOAD = SmpConstants.REST_INTERNAL_KEYSTORE_MANAGE + '/' + 'upload'
    + '/' + SmpConstants.PATH_PARAM_KEYSTORE_TYPE + '/' + SmpConstants.PATH_PARAM_KEYSTORE_PWD;
  public static readonly REST_INTERNAL_KEYSTORE_DELETE_ENTRY = SmpConstants.REST_INTERNAL_KEYSTORE_MANAGE + '/' + SmpConstants.PATH_ACTION_DELETE
    + '/' + SmpConstants.PATH_PARAM_CERT_ALIAS;

  public static readonly REST_INTERNAL_TRUSTSTORE_MANAGE = SmpConstants.REST_INTERNAL + 'truststore' + '/' + SmpConstants.PATH_PARAM_ENC_USER_ID;
  public static readonly REST_INTERNAL_TRUSTSTORE_UPLOAD_CERT = SmpConstants.REST_INTERNAL_TRUSTSTORE_MANAGE + '/' + 'upload-certificate';
  public static readonly REST_INTERNAL_TRUSTSTORE_DELETE_CERT = SmpConstants.REST_INTERNAL_TRUSTSTORE_MANAGE + '/' + SmpConstants.PATH_ACTION_DELETE + '/' + SmpConstants.PATH_PARAM_CERT_ALIAS;


  public static readonly REST_PUBLIC_USER_NAVIGATION_TREE = SmpConstants.REST_PUBLIC_USER + '/' + SmpConstants.PATH_PARAM_ENC_USER_ID + '/' + 'navigation-tree';
  public static readonly REST_PUBLIC_USER_CREDENTIAL_STATUS = SmpConstants.REST_PUBLIC_USER + '/' + SmpConstants.PATH_PARAM_ENC_USER_ID + '/' + 'username-credential-status';

  public static readonly REST_PUBLIC_USER_ACCESS_TOKEN_CREDENTIALS = SmpConstants.REST_PUBLIC_USER + '/' + SmpConstants.PATH_PARAM_ENC_USER_ID + '/' + 'access-token-credentials';
  public static readonly REST_PUBLIC_USER_CERTIFICATE_CREDENTIALS = SmpConstants.REST_PUBLIC_USER + '/' + SmpConstants.PATH_PARAM_ENC_USER_ID + '/' + 'certificate-credentials';

  public static readonly REST_PUBLIC_USER_CERTIFICATE_CREDENTIAL = SmpConstants.REST_PUBLIC_USER + '/'
    + SmpConstants.PATH_PARAM_ENC_USER_ID + '/'
    + 'certificate-credential' + '/'
    + SmpConstants.PATH_PARAM_ENC_CREDENTIAL_ID
  public static readonly REST_PUBLIC_USER_MANAGE_ACCESS_TOKEN_CREDENTIAL = SmpConstants.REST_PUBLIC_USER + '/'
    + SmpConstants.PATH_PARAM_ENC_USER_ID + '/'
    + 'access-token-credential' + '/'
    + SmpConstants.PATH_PARAM_ENC_CREDENTIAL_ID

  public static readonly REST_PUBLIC_USER_MANAGE_CERTIFICATE_CREDENTIAL = SmpConstants.REST_PUBLIC_USER + '/'
    + SmpConstants.PATH_PARAM_ENC_USER_ID + '/'
    + 'certificate-credential' + '/'
    + SmpConstants.PATH_PARAM_ENC_CREDENTIAL_ID

}
