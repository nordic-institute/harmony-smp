import {Injectable} from '@angular/core';
import {Observable, of, Subject} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Role} from '../security/role.model';

@Injectable()
export class UserService {

  constructor(private http: HttpClient) {}

  getUserRoles$() {
    // return this.http.get('rest/user/userroles');
    // TODO create the endpoint
    return of({json: () => [Role.SYSTEM_ADMINISTRATOR, Role.SMP_ADMINISTRATOR, Role.SERVICE_GROUP_ADMINISTRATOR]});
  }
}