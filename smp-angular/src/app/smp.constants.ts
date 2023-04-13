export class SmpConstants {

  public static EXPANDED_MENU_WIDTH: string = "180px"
  public static COLLAPSED_MENU_WIDTH: string = "50px"
  public static readonly NULL_VALUE: string = "-----------"
  public static readonly DATE_TIME_FORMAT = 'dd/MM/yyyy HH:mm:ss z';
  public static readonly DATE_FORMAT = 'dd/MM/yyyy';

  /* URL resource actions */
  public static readonly PATH_ACTION_DELETE = 'delete';
  public static readonly PATH_ACTION_UPDATE = 'update';
  public static readonly PATH_ACTION_CREATE = 'create';
  public static readonly PATH_ACTION_ADD = 'add';
  public static readonly PATH_ACTION_RETRIEVE = 'retrieve';
  public static readonly PATH_ACTION_SEARCH = 'search';
  public static readonly PATH_ACTION_UPDATE_RESOURCE_TYPES = 'update-resource-types';
  public static readonly PATH_ACTION_UPDATE_SML_INTEGRATION = 'update-sml-integration-data';
  /* URL variables */
  public static readonly PATH_PARAM_ENC_USER_ID = '{user-id}';
  public static readonly PATH_PARAM_ENC_DOMAIN_ID = '{domain-id}';
  public static readonly PATH_PARAM_ENC_MEMBER_ID = '{member-id}';
  public static readonly PATH_PARAM_ENC_GROUP_ID = '{group-id}';
  public static readonly PATH_PARAM_CERT_ALIAS = '{cert-alias}';
  public static readonly PATH_PARAM_ENC_CREDENTIAL_ID = '{credential-id}';
  public static readonly PATH_PARAM_ENC_MANAGED_USER_ID = '{managed-user-id}';
  public static readonly PATH_PARAM_SRV_GROUP_ID = '{service-group-id}';

  public static readonly PATH_PARAM_KEYSTORE_PWD = '{keystore-pwd}';
  public static readonly PATH_PARAM_KEYSTORE_TYPE = '{keystore-type}';

  public static readonly PATH_RESOURCE_TYPE_DOMAIN = 'domain';
  public static readonly PATH_RESOURCE_TYPE_MEMBER = 'member';
  public static readonly PATH_RESOURCE_TYPE_GROUP = 'group';

  //------------------------------
  // public endpoints
  public static readonly REST_PUBLIC = 'public/rest/';
  public static readonly REST_INTERNAL = 'internal/rest/';
  public static readonly REST_PUBLIC_SEARCH_SERVICE_GROUP = SmpConstants.REST_PUBLIC + SmpConstants.PATH_ACTION_SEARCH;
  public static readonly REST_PUBLIC_DOMAIN = SmpConstants.REST_PUBLIC + SmpConstants.PATH_RESOURCE_TYPE_DOMAIN;

  public static readonly REST_PUBLIC_DOMAIN_EDIT = SmpConstants.REST_PUBLIC_DOMAIN + '/' + SmpConstants.PATH_PARAM_ENC_USER_ID;
  public static readonly REST_PUBLIC_DOMAIN_MEMBERS = SmpConstants.REST_PUBLIC_DOMAIN_EDIT +
    '/' + SmpConstants.PATH_PARAM_ENC_DOMAIN_ID + '/' + "members";
  public static readonly REST_PUBLIC_DOMAIN_MEMBERS_ADD = SmpConstants.REST_PUBLIC_DOMAIN_EDIT
    + '/' + SmpConstants.PATH_PARAM_ENC_DOMAIN_ID + '/' + "member";
  public static readonly REST_PUBLIC_DOMAIN_MEMBERS_DELETE = SmpConstants.REST_PUBLIC_DOMAIN_EDIT
    + '/' + SmpConstants.PATH_PARAM_ENC_DOMAIN_ID + '/' + "member"
    + '/' + SmpConstants.PATH_PARAM_ENC_MEMBER_ID + '/' + SmpConstants.PATH_ACTION_DELETE;


  public static readonly REST_PUBLIC_GROUP_EDIT = SmpConstants.REST_PUBLIC + SmpConstants.PATH_RESOURCE_TYPE_GROUP + '/' + SmpConstants.PATH_PARAM_ENC_USER_ID;
  public static readonly REST_PUBLIC_GROUP_DOMAIN = SmpConstants.REST_PUBLIC_GROUP_EDIT + '/' +
    SmpConstants.PATH_RESOURCE_TYPE_DOMAIN + '/' + SmpConstants.PATH_PARAM_ENC_DOMAIN_ID;

  public static readonly REST_PUBLIC_GROUP_DOMAIN_CREATE = SmpConstants.REST_PUBLIC_GROUP_EDIT + '/' +
    SmpConstants.PATH_RESOURCE_TYPE_DOMAIN + '/' + SmpConstants.PATH_PARAM_ENC_DOMAIN_ID + '/' + SmpConstants.PATH_ACTION_CREATE;

  public static readonly REST_PUBLIC_GROUP_DOMAIN_DELETE = SmpConstants.REST_PUBLIC_GROUP_EDIT + '/' + SmpConstants.PATH_PARAM_ENC_GROUP_ID + '/' +
    SmpConstants.PATH_RESOURCE_TYPE_DOMAIN + '/' + SmpConstants.PATH_PARAM_ENC_DOMAIN_ID + '/' + SmpConstants.PATH_ACTION_DELETE;

  public static readonly REST_PUBLIC_GROUP_DOMAIN_UPDATE = SmpConstants.REST_PUBLIC_GROUP_EDIT + '/' + SmpConstants.PATH_PARAM_ENC_GROUP_ID + '/' +
    SmpConstants.PATH_RESOURCE_TYPE_DOMAIN + '/' + SmpConstants.PATH_PARAM_ENC_DOMAIN_ID + '/' + SmpConstants.PATH_ACTION_UPDATE;


  public static readonly REST_PUBLIC_APPLICATION_INFO = SmpConstants.REST_PUBLIC + 'application/info';
  public static readonly REST_PUBLIC_APPLICATION_CONFIG = SmpConstants.REST_PUBLIC + 'application/config';
  // user public services
  public static readonly REST_PUBLIC_USER = SmpConstants.REST_PUBLIC + 'user';

  public static readonly REST_PUBLIC_USER_UPDATE = SmpConstants.REST_PUBLIC_USER + '/' + SmpConstants.PATH_PARAM_ENC_USER_ID + '/';
  public static readonly REST_PUBLIC_USER_GENERATE_ACCESS_TOKEN = SmpConstants.REST_PUBLIC_USER_UPDATE + 'generate-access-token';
  public static readonly REST_PUBLIC_USER_CHANGE_PASSWORD = SmpConstants.REST_PUBLIC_USER_UPDATE + 'change-password';

  public static readonly REST_PUBLIC_USER_SEARCH = SmpConstants.REST_PUBLIC_USER + '/' + SmpConstants.PATH_PARAM_ENC_USER_ID + '/' + SmpConstants.PATH_ACTION_SEARCH;
  // truststore public services
  public static readonly REST_PUBLIC_TRUSTSTORE = SmpConstants.REST_PUBLIC + "truststore/" + '/' + SmpConstants.PATH_PARAM_ENC_USER_ID + '/';
  public static readonly REST_PUBLIC_TRUSTSTORE_CERT_VALIDATE = SmpConstants.REST_PUBLIC_TRUSTSTORE + 'validate-certificate';

  // public authentication services
  public static readonly REST_PUBLIC_SECURITY = SmpConstants.REST_PUBLIC + 'security/';
  public static readonly REST_PUBLIC_SECURITY_AUTHENTICATION = SmpConstants.REST_PUBLIC_SECURITY + 'authentication';
  public static readonly REST_PUBLIC_SECURITY_USER = SmpConstants.REST_PUBLIC_SECURITY + 'user';

  public static readonly REST_PUBLIC_SERVICE_GROUP = SmpConstants.REST_PUBLIC + 'service-group';
  public static readonly REST_PUBLIC_SERVICE_GROUP_ENTITY = SmpConstants.REST_PUBLIC_SERVICE_GROUP + '/' + SmpConstants.PATH_PARAM_SRV_GROUP_ID;
  public static readonly REST_PUBLIC_SERVICE_GROUP_ENTITY_EXTENSION = SmpConstants.REST_PUBLIC_SERVICE_GROUP_ENTITY + '/extension';
  // service group extension tools
  public static readonly REST_SERVICE_GROUP_EXTENSION = `${SmpConstants.REST_PUBLIC_SERVICE_GROUP}/extension`;
  public static readonly REST_SERVICE_GROUP_EXTENSION_VALIDATE = `${SmpConstants.REST_SERVICE_GROUP_EXTENSION}/validate`;

  public static readonly REST_METADATA = SmpConstants.REST_PUBLIC + 'service-metadata';
  public static readonly REST_METADATA_VALIDATE = `${SmpConstants.REST_METADATA}/validate`;


  //------------------------------
  // internal endpoints
  public static readonly REST_INTERNAL_ALERT_MANAGE = SmpConstants.REST_INTERNAL + 'alert';
  public static readonly REST_INTERNAL_DOMAIN_MANAGE_DEPRECATED = SmpConstants.REST_INTERNAL + SmpConstants.PATH_RESOURCE_TYPE_DOMAIN;

  public static readonly REST_INTERNAL_DOMAIN_MANAGE = SmpConstants.REST_INTERNAL + SmpConstants.PATH_RESOURCE_TYPE_DOMAIN +
    '/' + SmpConstants.PATH_PARAM_ENC_USER_ID;

  public static readonly REST_INTERNAL_DOMAIN_MANAGE_DELETE = SmpConstants.REST_INTERNAL + SmpConstants.PATH_RESOURCE_TYPE_DOMAIN +
    '/' + SmpConstants.PATH_PARAM_ENC_USER_ID + '/' + SmpConstants.PATH_PARAM_ENC_DOMAIN_ID + '/' + SmpConstants.PATH_ACTION_DELETE;

  public static readonly REST_INTERNAL_DOMAIN_MANAGE_UPDATE = SmpConstants.REST_INTERNAL + SmpConstants.PATH_RESOURCE_TYPE_DOMAIN +
    '/' + SmpConstants.PATH_PARAM_ENC_USER_ID + '/' + SmpConstants.PATH_PARAM_ENC_DOMAIN_ID + '/' + SmpConstants.PATH_ACTION_UPDATE;

  public static readonly REST_INTERNAL_DOMAIN_MANAGE_CREATE = SmpConstants.REST_INTERNAL + SmpConstants.PATH_RESOURCE_TYPE_DOMAIN +
    '/' + SmpConstants.PATH_PARAM_ENC_USER_ID + '/' + SmpConstants.PATH_ACTION_CREATE;


  public static readonly REST_INTERNAL_DOMAIN_MANAGE_UPDATE_SML_INTEGRATION = SmpConstants.REST_INTERNAL + SmpConstants.PATH_RESOURCE_TYPE_DOMAIN +
    '/' + SmpConstants.PATH_PARAM_ENC_USER_ID + '/' + SmpConstants.PATH_PARAM_ENC_DOMAIN_ID + '/' + SmpConstants.PATH_ACTION_UPDATE_SML_INTEGRATION;


  public static readonly REST_INTERNAL_DOMAIN_MANAGE_UPDATE_RESOURCE_TYPES = SmpConstants.REST_INTERNAL + SmpConstants.PATH_RESOURCE_TYPE_DOMAIN +
    '/' + SmpConstants.PATH_PARAM_ENC_USER_ID + '/' + SmpConstants.PATH_PARAM_ENC_DOMAIN_ID + '/' + SmpConstants.PATH_ACTION_UPDATE_RESOURCE_TYPES;

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
