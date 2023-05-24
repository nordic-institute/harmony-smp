import {SearchTableEntity} from "../search-table/search-table-entity.model";
import {VisibilityEnum} from "../enums/visibility.enum";

export interface SubresourceRo extends SearchTableEntity {

  subresourceId?: string;
  subresourceTypeIdentifier?: string;

  identifierValue: string;

  identifierScheme?: string;
}
