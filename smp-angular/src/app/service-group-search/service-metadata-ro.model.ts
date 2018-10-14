import {SearchTableEntity} from "../common/search-table/search-table-entity.model";

export interface ServiceMetadataRo extends SearchTableEntity {
  documentIdentifier: string;
  documentIdentifierScheme: string;
  smlSubdomain: string;
  domainCode: string;
}
