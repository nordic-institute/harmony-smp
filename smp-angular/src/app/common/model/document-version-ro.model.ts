import {SearchTableEntity} from "../search-table/search-table-entity.model";

export interface DocumentVersionRo extends SearchTableEntity {
  version: number;
  versionStatus:string;
  createdOn: Date;
  lastUpdatedOn: Date;
}

