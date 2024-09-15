import {SearchTableEntity} from "../search-table/search-table-entity.model";

export interface SubresourceRo extends SearchTableEntity {

  subresourceId?: string;
  subresourceTypeIdentifier?: string;
  identifierValue: string;
  identifierScheme?: string;
}
