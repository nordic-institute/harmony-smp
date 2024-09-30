import {SearchTableEntity} from "../search-table/search-table-entity.model";
import {ResourceRo} from "./resource-ro.model";
import {SubresourceRo} from "./subresource-ro.model";

export interface DocumentConfigurationRo extends SearchTableEntity {
  name?: string;
  mimeType?: string;
  sharingEnabled?: boolean;
  allVersions?: number[];
  publishedVersion?: number;
  // optional reference data.
  referenceDocumentId?: string;
  referenceDocumentName?: string;
  referenceDocumentUrl?: string;

}

