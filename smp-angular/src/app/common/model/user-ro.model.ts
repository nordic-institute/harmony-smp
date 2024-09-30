import {SearchTableEntity} from "../search-table/search-table-entity.model";
import {ApplicationRoleEnum} from "../enums/application-role.enum";
import {CertificateRo} from "./certificate-ro.model";

export interface UserRo extends SearchTableEntity {
  userId?: string
  username: string;
  fullName?: string;
  emailAddress?: string;
  smpTheme?: string;
  smpLocale?: string;
  role: ApplicationRoleEnum;
  active: boolean;

  passwordExpireOn?:	Date;
  passwordUpdatedOn?:	Date;

  suspended?: boolean;
  casUserDataUrl?: string;
  sequentialLoginFailureCount?:number;
  lastFailedLoginAttempt?:Date;
  suspendedUtil?:Date;

 // deprecated
  accessTokenId?: string;
  accessTokenExpireOn?:	Date;
  certificate?: CertificateRo;
  sequentialTokenLoginFailureCount?:number;
  lastTokenFailedLoginAttempt?:Date;
  tokenSuspendedUtil?:Date;
}
