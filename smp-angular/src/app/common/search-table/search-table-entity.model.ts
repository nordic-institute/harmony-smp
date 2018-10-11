import {SearchTableEntityStatus} from "./search-table-entity-status.model";

export interface SearchTableEntity {
  status: SearchTableEntityStatus;
  deleted?: boolean;
}
