export class SmpConstants {

  public static readonly REST_DOMAIN = 'rest/domain';
  public static readonly REST_USER = 'rest/user';
  public static readonly REST_SEARCH = 'rest/search';
  public static readonly REST_EDIT = 'rest/servicegroup';
  public static readonly REST_METADATA = 'rest/servicemetadata';

  public static readonly REST_CERTIFICATE = `${SmpConstants.REST_USER}/certdata`;
  public static readonly REST_SERVICE_GROUP_EXTENSION = `${SmpConstants.REST_EDIT}/extension`;
  public static readonly REST_SERVICE_GROUP_EXTENSION_VALIDATE = `${SmpConstants.REST_SERVICE_GROUP_EXTENSION}/validate`;
  public static readonly REST_SERVICE_GROUP_EXTENSION_FORMAT = `${SmpConstants.REST_SERVICE_GROUP_EXTENSION}/format`;
  public static readonly REST_METADATA_VALIDATE = `${SmpConstants.REST_METADATA}/validate`;

}
