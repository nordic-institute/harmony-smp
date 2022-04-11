import {SearchTableEntity} from '../common/search-table/search-table-entity.model';
import {CertificateRo} from './certificate-ro.model';

export interface UserRo extends SearchTableEntity {
  userId?: string
  username: string;
  emailAddress: string;
  password?: string;
  accessTokenId?: string;
  role: string;
  active: boolean;
  suspended?: boolean;
  certificate?: CertificateRo;
  statusPassword: number;
}
