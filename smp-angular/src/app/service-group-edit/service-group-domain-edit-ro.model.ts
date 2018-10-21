import {SearchTableEntity} from "../common/search-table/search-table-entity.model";

export interface ServiceGroupDomainEditRo extends SearchTableEntity {
  id: number;
  domainId: number;
  domainCode: string;
  smlSubdomain: string;
  smlRegistered: boolean;
  serviceMetadataCount?: number;
}
