import {SearchTableEntity} from "../search-table/search-table-entity.model";

export interface SearchReferenceDocument extends SearchTableEntity  {
  referenceDocumentId?: string;
  referenceDocumentName?: string;

  referenceResourceValue?: string;
  referenceResourceScheme?: string;
  referenceSubesourceValue?: string;
  referenceSubesourceScheme?: string;
}

