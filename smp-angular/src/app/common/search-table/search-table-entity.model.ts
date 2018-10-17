import {SearchTableEntityStatus} from './search-table-entity-status.model';

export interface SearchTableEntity {
  index: number;
  status: SearchTableEntityStatus;
  deleted?: boolean;
}
