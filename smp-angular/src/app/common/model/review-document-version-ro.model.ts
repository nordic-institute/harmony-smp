import {SearchTableEntity} from "../search-table/search-table-entity.model";

export interface ReviewDocumentVersionRo extends SearchTableEntity {

  documentId: string;
  documentVersionId: string;
  resourceId: string;
  subresourceId?: string;
  version: number;
  currentStatus: string;
  resourceIdentifierValue: string;
  resourceIdentifierScheme: string;
  subresourceIdentifierValue?: string;
  subresourceIdentifierScheme?: string;
  target: string;
  lastUpdatedOn: Date;
}
