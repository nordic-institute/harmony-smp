import {SearchTableEntity} from "../search-table/search-table-entity.model";
import {DocumentReferenceType} from "../enums/documetn-reference-type.enum";

export interface SearchReferenceDocument extends SearchTableEntity  {
  documentId?: string;
  documentName?: string;

  referenceType?: DocumentReferenceType;
  resourceValue?: string;
  resourceScheme?: string;
  subesourceValue?: string;
  subesourceScheme?: string;

  referenceUrl?: string;
}

