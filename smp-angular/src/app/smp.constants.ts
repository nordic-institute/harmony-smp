export class SmpConstants {
  //------------------------------
  // public endpoints
  public static readonly REST_PUBLIC = 'public/rest/';
  public static readonly REST_INTERNAL = 'internal/rest/';
  public static readonly REST_PUBLIC_SEARCH_SERVICE_GROUP = SmpConstants.REST_PUBLIC + 'search';
  public static readonly REST_PUBLIC_DOMAIN_SEARCH = SmpConstants.REST_PUBLIC + 'domain';
  public static readonly REST_PUBLIC_APPLICATION_INFO = SmpConstants.REST_PUBLIC + 'application/info';
  // user public services
  public static readonly REST_PUBLIC_USER = SmpConstants.REST_PUBLIC + 'user';
  public static readonly REST_PUBLIC_USER_UPDATE = SmpConstants.REST_PUBLIC_USER + '/{user-id}/';
  public static readonly REST_PUBLIC_USER_CERT_VALIDATE = SmpConstants.REST_PUBLIC_USER_UPDATE + 'validate-certificate';
  public static readonly REST_PUBLIC_USER_GENERATE_ACCESS_TOKEN = SmpConstants.REST_PUBLIC_USER_UPDATE + 'generate-access-token';
  public static readonly REST_PUBLIC_USER_CHANGE_PASSWORD = SmpConstants.REST_PUBLIC_USER_UPDATE + 'change-password';
  public static readonly REST_PUBLIC_TRUSTSTORE = SmpConstants.REST_PUBLIC + 'truststore/{user-id}/';
  public static readonly REST_PUBLIC_TRUSTSTORE_VALIDATE_CERT = SmpConstants.REST_PUBLIC_TRUSTSTORE + 'validate-certificate';
  // public authentication services
  public static readonly REST_PUBLIC_SECURITY_AUTHENTICATION = SmpConstants.REST_PUBLIC + 'security/authentication';
  public static readonly REST_PUBLIC_SECURITY_USER = SmpConstants.REST_PUBLIC + 'security/user';

  public static readonly REST_PUBLIC_SERVICE_GROUP = SmpConstants.REST_PUBLIC + 'service-group';
  public static readonly REST_SERVICE_GROUP_EXTENSION = `${SmpConstants.REST_PUBLIC_SERVICE_GROUP}/extension`;
  public static readonly REST_SERVICE_GROUP_EXTENSION_VALIDATE = `${SmpConstants.REST_SERVICE_GROUP_EXTENSION}/validate`;
  public static readonly REST_SERVICE_GROUP_EXTENSION_FORMAT = `${SmpConstants.REST_SERVICE_GROUP_EXTENSION}/format`;

  public static readonly REST_METADATA = SmpConstants.REST_PUBLIC +'service-metadata';
  public static readonly REST_METADATA_VALIDATE = `${SmpConstants.REST_METADATA}/validate`;


  //------------------------------
  // internal endpoints
  public static readonly REST_INTERNAL_DOMAIN_MANAGE = SmpConstants.REST_INTERNAL + 'domain';
  public static readonly REST_INTERNAL_DOMAIN_VALIDATE_DELETE = SmpConstants.REST_INTERNAL_DOMAIN_MANAGE + '/validate-delete';
  public static readonly REST_INTERNAL_USER_MANAGE = SmpConstants.REST_INTERNAL + 'user';
  public static readonly REST_INTERNAL_USER_VALIDATE_DELETE = `${SmpConstants.REST_INTERNAL_USER_MANAGE}/validate-delete`;
  public static readonly REST_INTERNAL_APPLICATION_CONFIG = SmpConstants.REST_INTERNAL + 'application/config';
  public static readonly REST_INTERNAL_KEYSTORE = SmpConstants.REST_INTERNAL + 'keystore';
  public static readonly REST_INTERNAL_TRUSTSTORE = SmpConstants.REST_INTERNAL + 'truststore';






}
