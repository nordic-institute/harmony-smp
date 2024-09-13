import {SearchTableEntity} from "../search-table/search-table-entity.model";

export interface DocumentVersionEventRo extends SearchTableEntity {
  eventType: string;
  eventOn: Date;
  username: string;
  eventSourceType: string;
  details: string;
}

