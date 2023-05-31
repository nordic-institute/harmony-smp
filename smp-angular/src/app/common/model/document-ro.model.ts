import {SearchTableEntity} from '../search-table/search-table-entity.model';
import {VisibilityEnum} from "../enums/visibility.enum";

export interface DocumentRo {
  mimeType?: string;
  name?: string;
  documentId?: string;
  currentResourceVersion?:number;
  allVersions?: number[];
  payloadVersion?:number;
  payload?:string;
}

