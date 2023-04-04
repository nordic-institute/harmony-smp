import {SearchTableEntity} from '../../common/search-table/search-table-entity.model';
import {CertificateRo} from './certificate-ro.model';

export interface UserRo extends SearchTableEntity {
  userId?: string
  username: string;
  emailAddress: string;
  accessTokenId?: string;
  passwordExpireOn?:	Date;
  accessTokenExpireOn?:	Date;
  role: string;
  active: boolean;
  suspended?: boolean;
  certificate?: CertificateRo;
  statusPassword: number;
  casUserDataUrl?: string;
  sequentialLoginFailureCount?:number;
  lastFailedLoginAttempt?:Date;
  suspendedUtil?:Date;
  sequentialTokenLoginFailureCount?:number;
  lastTokenFailedLoginAttempt?:Date;
  tokenSuspendedUtil?:Date;
}
