import {SearchTableEntity} from '../common/search-table/search-table-entity.model';
import {CertificateRo} from './certificate-ro.model';

export interface UserRo extends SearchTableEntity {
  username: string;
  email: string;
  password?: string;
  role: string;
  suspended?: boolean;
  certificate?: CertificateRo;
}
