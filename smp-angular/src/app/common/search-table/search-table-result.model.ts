import {SearchTableEntity} from "./search-table-entity.model";

export interface SearchTableResult {
  serviceEntities: Array<SearchTableEntity>;
  pageSize: number;
  count: number;
  filter: any;
}
