
import {MembershipRoleEnum} from "../enums/membership-role.enum";
import {SearchTableEntity} from "../search-table/search-table-entity.model";
import {MemberTypeEnum} from "../enums/member-type.enum";
import {VisibilityEnum} from "../enums/visibility.enum";

export interface GroupRo extends SearchTableEntity {

  groupId?: string;
  groupName: string;
  groupDescription?: string;
  visibility: VisibilityEnum;
}
