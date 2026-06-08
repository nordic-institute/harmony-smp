import {SearchTableEntity} from './search-table-entity.model';

export interface SearchTableResult {
  serviceEntities: Array<SearchTableEntity>;
  pageSize: number;
  page?: number;
  count: number;
  filter: any;
}
