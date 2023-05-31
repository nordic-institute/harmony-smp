import {SearchTableEntity} from "../common/search-table/search-table-entity.model";

export interface ServiceMetadataSearchRo extends SearchTableEntity {
  documentIdentifier: string;
  documentIdentifierScheme: string;
  smlSubdomain: string;
  subresourceDefUrlSegment?:string;
  domainCode: string;
}
