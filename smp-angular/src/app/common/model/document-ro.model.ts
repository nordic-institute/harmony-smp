import {DocumentPropertyRo} from "./document-property-ro.model";
import {SearchTableEntity} from "../search-table/search-table-entity.model";
import {EntityStatus} from "../enums/entity-status.enum";

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
}

