import {SearchTableEntity} from "../search-table/search-table-entity.model";
import {VisibilityEnum} from "../enums/visibility.enum";

export interface ResourceRo extends SearchTableEntity {

  resourceId?: string;
  resourceTypeIdentifier?: string;

  identifierValue: string;

  identifierScheme?: string;

  smlRegistered: boolean;


  visibility: VisibilityEnum;
}
