
import {MembershipRoleEnum} from "./membership-role.enum";
import {SearchTableEntity} from "../../../common/search-table/search-table-entity.model";
import {MemberTypeEnum} from "./member-dialog/member-type.enum";

export interface MemberRo extends SearchTableEntity {

  memberId:string;
  username:string;
  memberOf:MemberTypeEnum;
  fullName:string;
  roleType:MembershipRoleEnum;
}
