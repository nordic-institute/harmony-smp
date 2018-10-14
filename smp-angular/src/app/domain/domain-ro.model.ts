import {SearchTableEntity} from '../common/search-table/search-table-entity.model';

export interface DomainRo extends SearchTableEntity {
  domainCode: string;
  smlSubdomain: string;
  smlSmpId: string;
  smlParticipantIdentifierRegExp: string;
  smlClientCertHeader: string;
  smlClientKeyAlias: string;
  signatureKeyAlias: string;
}

