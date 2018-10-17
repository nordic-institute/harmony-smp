import {SearchTableEntityStatus} from './search-table-entity-status.model';

export interface SearchTableEntity {
  id?: number;
  index?: number;
  status: SearchTableEntityStatus;
  deleted?: boolean;
}
