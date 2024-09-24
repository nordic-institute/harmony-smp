import {SearchTableEntity} from "../search-table/search-table-entity.model";
import {DocumentVersionsStatus} from "../enums/document-versions-status.enum";

export interface DocumentVersionEventRo extends SearchTableEntity {
  eventType: string;
  eventOn: Date;
  username: string;
  eventSourceType: string;
  details: string;
  documentVersionStatus?: DocumentVersionsStatus;
}

