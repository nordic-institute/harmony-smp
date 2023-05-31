import {SearchTableEntity} from '../../common/search-table/search-table-entity.model';
import {CertificateRo} from './certificate-ro.model';
import {ApplicationRoleEnum} from "../../common/enums/application-role.enum";

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
