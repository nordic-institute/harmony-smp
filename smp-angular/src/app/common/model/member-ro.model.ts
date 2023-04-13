
import {MembershipRoleEnum} from "../enums/membership-role.enum";
import {SearchTableEntity} from "../search-table/search-table-entity.model";
import {MemberTypeEnum} from "../enums/member-type.enum";

export interface MemberRo extends SearchTableEntity {

  memberId:string;
  username:string;
  memberOf:MemberTypeEnum;
  fullName:string;
  roleType:MembershipRoleEnum;
}
