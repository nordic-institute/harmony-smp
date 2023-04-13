import {SearchTableEntity} from "../../../search-table/search-table-entity.model";

export interface SearchUserRo extends SearchTableEntity {
  userId: string,
  username: string;
  fullName: string;

}

