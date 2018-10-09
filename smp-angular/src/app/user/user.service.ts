import {Injectable} from "@angular/core";
import {Observable, Subject} from "rxjs";
import {Http} from "@angular/http";
import {Role} from "../security/role.model";

@Injectable()
export class UserService {

  constructor(private http: Http) {}

  getUserRoles$() {
    // return this.http.get('rest/user/userroles');
    // TODO create the endpoint
    return Observable.of({json: () => [Role.SMP_ADMINISTRATOR, Role.SERVICE_GROUP_ADMINISTRATOR, Role.SYSTEM_ADMINISTRATOR]});
  }
}
