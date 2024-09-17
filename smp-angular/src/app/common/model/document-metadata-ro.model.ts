import {SearchTableEntity} from "../search-table/search-table-entity.model";
import {ResourceRo} from "./resource-ro.model";
import {SubresourceRo} from "./subresource-ro.model";

export interface DocumentMetadataRo extends SearchTableEntity {
  name?: string;
  mimeType?: string;
  sharingEnabled?: boolean;
  allVersions?: number[];
  publishedVersion?: number;
  // optional reference data.
  referenceDocumentId?: string;
  referenceDocumentName?: string;

  referenceResourceValue?: string;
  referenceResourceScheme?: string;
  referenceSubesourceValue?: string;
  referenceSubesourceScheme?: string;

}

