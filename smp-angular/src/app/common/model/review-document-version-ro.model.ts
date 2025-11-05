import {SearchTableEntity} from "../search-table/search-table-entity.model";
import {DocumentLevelType} from "../enums/documetn-reference-type.enum";

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
  documentLevel: DocumentLevelType;
  lastUpdatedOn: Date;
}
