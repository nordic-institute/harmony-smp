import {SearchTableEntity} from "../search-table/search-table-entity.model";
import {DocumentLevelType} from "../enums/documetn-reference-type.enum";

export interface SearchReferenceDocument extends SearchTableEntity  {
  documentId?: string;
  documentName?: string;

  referenceType?: DocumentLevelType;
  resourceValue?: string;
  resourceScheme?: string;
  subesourceValue?: string;
  subesourceScheme?: string;

  referenceUrl?: string;
}

