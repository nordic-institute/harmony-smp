import {SearchTableEntity} from "../search-table/search-table-entity.model";

export interface DocumentMetadataRo extends SearchTableEntity {
  name?: string;
  mimeType?: string;
  sharingEnabled?: boolean;
  referenceDocumentId?: string;
  allVersions?: number[];
  publishedVersion?: number;
}

