import {SearchTableEntity} from "../common/search-table/search-table-entity.model";

export interface UserRo extends SearchTableEntity {
  userName: string;
  password?: string;
  role: string;
  suspended?: boolean;
}
