import {Injectable} from "@angular/core";
import {Role} from "./role.model";

@Injectable()
export class RoleService {

  public getLabel(role: Role): string {
    switch (role) {
      case Role.SMP_ADMINISTRATOR:
        return 'SMP Administrator';
      case Role.SERVICE_GROUP_ADMINISTRATOR:
        return 'ServiceGroup Administrator';
      case Role.SYSTEM_ADMINISTRATOR:
        return 'System Administrator';
      default:
        return '';
    }
  }

}
