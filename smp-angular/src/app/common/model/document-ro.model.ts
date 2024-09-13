import {DocumentPropertyRo} from "./document-property-ro.model";
import {SearchTableEntity} from "../search-table/search-table-entity.model";
import {EntityStatus} from "../enums/entity-status.enum";
import {
  DocumentVersionRo
} from "./document-version-ro.model";
import {DocumentVersionEventRo} from "./document-version-event-ro.model";
import {DocumentVersionsStatus} from "../enums/document-versions-status.enum";

export interface DocumentRo extends SearchTableEntity  {
  mimeType?: string;
  name?: string;
  documentId?: string;
  currentResourceVersion?:number;
  allVersions?: number[];
  payloadVersion?:number;
  payloadCreatedOn?: Date;
  payload?:string;
  payloadStatus: EntityStatus;
  properties?: DocumentPropertyRo[];
  documentVersionStatus?: DocumentVersionsStatus;
  documentVersionEvents?: DocumentVersionEventRo[];
  documentVersions?: DocumentVersionRo[];
}

