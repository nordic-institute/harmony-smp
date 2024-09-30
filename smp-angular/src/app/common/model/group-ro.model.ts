
import {SearchTableEntity} from "../search-table/search-table-entity.model";
import {VisibilityEnum} from "../enums/visibility.enum";

export interface GroupRo extends SearchTableEntity {

  groupId?: string;
  groupName: string;
  groupDescription?: string;
  visibility: VisibilityEnum;
}
