import {SearchTableEntity} from '../common/search-table/search-table-entity.model';

export interface DomainRo extends SearchTableEntity {
  domainId: string;
  bdmslClientCertHeader: string;
  bdmslClientCertAlias: string;
  bdmslSmpId: string;
  signatureCertAlias: string;
}



